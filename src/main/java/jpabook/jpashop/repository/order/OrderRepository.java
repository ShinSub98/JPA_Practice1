package jpabook.jpashop.repository.order;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    // 전체 주문 리턴
    public List<Order> findAll() {
        return em.createQuery("select o from Order o", Order.class).getResultList();
    }


    /** JQPL로 검색 로직 구현 **/
    public List<Order> findByString(OrderSearch orderSearch) {
        //language = JPAQL
        String jpql = "select o From Order o join o.member m"; // 모든 주문을 회원 엔티티와 조인해서 셀렉하는 기본 쿼리문 작성
        boolean isFirstCondition = true; // 처음 조건이 붙는 경우라면 where이 붙어야 하고, 그 이후에는 and이 붙어야 하므로 필요한 플래그

        // 주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }

        // 회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name"; // like 연산은 문자열 부분 포함도 검색 허용한다.
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class).setMaxResults(1000); // 최대 1000건 검색

        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status" , orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }

        return query.getResultList();
    }


    /** JPA Criteria로 검색 로직 구현 **/
    public List<Order> findByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Order, Member> m = o.join("member", JoinType.INNER); // 주문-회원 테이블 조인

        List<Predicate> criteria = new ArrayList<>();

        // 주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }

        // 회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name = cb.like(m.<String>get("name"), "%" + orderSearch.getMemberName() + "%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); // 최대 1000건 검색

        return query.getResultList();
    }

//    public List<Order> findAllWithMemberDelivery() {
//        return em.createQuery(
//                "select o from Order o" +
//                        " join fetch o.member m" +
//                        " join fetch o.delivery d", Order.class
//        ).getResultList();

    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class
        ).setFirstResult(offset).setMaxResults(limit).getResultList();
    }

    /**
     * 1대N join이 있을 때는 중복 데이터 때문에 DB상 row가 증가하며<br>
     * 같은 엔티티에 대한 조회 수도 증가하게 된다.<br>
     * 이 때 distinct를 사용하면 JPA에서 중복 엔티티를 조회할 때 중복을 걸러준다.＜br>
     * 하지만 페이징이 불가능하다.
     */
    public List<Order> findAllWithItem() {
        return em.createQuery(
                "select distinct o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d" +
                        " join fetch o.orderItems oi" +
                        " join fetch oi.item i", Order.class
        ).getResultList();
    }
}
