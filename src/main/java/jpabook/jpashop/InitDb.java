package jpabook.jpashop;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    /**
     * "@PostConstruct"가 있으면 빈이 모두 등록된 후 자동으로 해당 메소드가 실행된다.<br>
     * 따라서 이 메소드를 통해 자동으로 매번 DB에 필요한 데이터를 초기화시킬 수 있다.
     */
    @PostConstruct
    public void init() {
//        initService.dbInit1();
        initService.dbInit2();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final EntityManager em;


        public void dbInit2() {
            Member member = createMember("userB", "서울", "2", "2222");
            em.persist(member);

            Book book1 = createBook("SPRING1 BOOK", 20000, 200);
            em.persist(book1);

            Book book2 = createBook("SPRING2 BOOK", 40000, 400);
            em.persist(book2);

            Delivery delivery = createDelivery(member);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 20000, 3);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 40000, 4);

            Order order = Order.createOrder(member, createDelivery(member), orderItem1, orderItem2);
            em.persist(order);

            Order order2 = Order.createOrder(member, createDelivery(member), orderItem1, orderItem2);
            em.persist(order2);
            member.getOrders().add(order);
            member.getOrders().add(order2);
        }

        private Member createMember(String name, String city, String street, String zipcode) {
            Member member = new Member();
            member.setName(name);
            member.setAddress(new Address(city, street, zipcode));
            return member;
        }

        private Book createBook(String name, int price, int stockQuantity) {
            Book book = new Book();
            book.setName(name);
            book.setPrice(price);
            book.setStockQuantity(stockQuantity);
            return book;
        }

        private Delivery createDelivery(Member member) {
            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());
            return delivery;
        }
    }
}
