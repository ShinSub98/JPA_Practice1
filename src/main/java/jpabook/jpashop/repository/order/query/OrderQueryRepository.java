package jpabook.jpashop.repository.order.query;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    // 주문 조회 V4: JPA에서 DTO 직접 조회
    /**
     * 컬렉션은 별도로 조회<br>
     * Query: 루트 1번, 컬렉션 N번<br>
     * 단건 조회에서 많이 사용하는 방식
     */
    public List<OrderQueryDto> findOrderQueryDtos() {
        // 루트 조회(toOne 코드를 모두 한 번에 조회)
        List<OrderQueryDto> result = findOrders();

        // 루프를 돌면서 컬렉션 추가(추가 쿼리 실행)
        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });
        return result;
    }

    /**
     *  1:N 관계(컬렉션)를 제외한 나머지를 한번에 조회<br>
     *  <p>
     *      JPQL 해설
     *  </p>
     *  <p>
     *      1. 먼저 `from Order o`를 통해 Order 엔티티를 조회할 것을 명시한다. 이 때, 별칭 o로 설정한다.<br>
     *      2. `join o.member m`에서 Order 엔티티와 매핑된 member 엔티티를 m이라는 별칭으로 조인한다.<br>
     *      3. `join o.delivery d`에서 Order 엔티티와 매핑된 delivery 엔티티를 d라는 별칭으로 조인한다.<br>
     *      4. `select new ...`에서 뒷부분에 선택된 필드만 선택하여 OrderQueryDto 객체로 만들어 전달한다.
     *  </p>
     */
    private List<OrderQueryDto> findOrders() {
        return em.createQuery("select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderQueryDto.class).getResultList();
    }

    /**
     * 1:N 관계인 orderItems(컬렉션) 조회
     * <p>
     *     JPQL 해설
     * </p>
     * <p>
     *     `where oi.order.id = : orderId`는 인자로 전달된 orderId와 필드의 orderId의 값이 동일한 엔티티만 필터링한다.
     * </p>
     */
    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id = : orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId).getResultList();
    }


    // 주문 조회 V5: JPA에서 DTO 직접 조회 - 컬렉션 조회 최적화
    /**
     * 최적화<br>
     * Query: 루트 1번, 컬렉션 1번<br>
     * 데이터를 한꺼번에 많이 처리할 때 많이 사용하는 방식
     */
    public List<OrderQueryDto> findAllByDto_optimization() {

        // 루트 조회(toOne 코드를 모두 한번에 조회)
        List<OrderQueryDto> result = findOrders();

        // orderItem 컬렉션을 Map 한방에 조회
        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(toOrderIds(result));

        // 루프 돌면서 컬렉션 추가(추가 쿼리 실행 없음)
        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return result;
    }

    private List<Long> toOrderIds(List<OrderQueryDto> result) {
        return result.stream()
                .map(o -> o.getOrderId())
                .collect(Collectors.toList());
    }

    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {
        List<OrderItemQueryDto> orderItems = em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id in : orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds).getResultList();

        // 이 단계에서 groupingBy() 메소드를 통해 Map<Long, List<OrderItemQueryDto>> 형태로 변환한다.
        // 즉, 같은 주문번호를 갖는 주문상품들을 하나의 리스트로 묶고 이를 각 주문번호에 대한 Map 형태로 리턴한다.
        return orderItems.stream()
                .collect(Collectors.groupingBy(OrderItemQueryDto::getOrderId));
    }


    // 주문 조회 V6: JPA에서 DTO로 직접 조회, 플랫 데이터 최적화
    public List<OrderFlatDto> findAllByDto_flat() {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderFlatDto(o.io, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d" +
                        " join o.orderItems oi" +
                        " join oi.item i", OrderFlatDto.class).getResultList();
    }
}
