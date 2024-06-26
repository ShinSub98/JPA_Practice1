package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.order.OrderRepository;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepositoy;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepositoy orderSimpleQueryRepositoy;

    /**
     * 엔티티를 직접 노출하는 방법.<br>
     * 추천하지 않는다.
     */
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();
        }
        return all;
    }

    /**
     * 엔티티를 조회해서 DTO로 변환(fetch join은 사용X)<br>
     * 단점: Lazy 로딩으로 쿼리가 총 N+N+1번 발생(order, order->member, order->delivery)
     */
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        List<Order> orders = orderRepository.findByString(new OrderSearch());
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o)).collect(Collectors.toList());

        return result;
    }

    /**
     * 엔티티를 fetch join해서 쿼리 1번에 조회한다.<br>
     * 즉, order->member, order->delivery는 이미 조회된 상태이므로 조회하지 않는다.
     */
//    @GetMapping("/api/v3/simple-orders")
//    public List<SimpleOrderDto> ordersV3() {
//        List<Order> orders = orderRepository.findAllWithMemberDelivery();
//        List<SimpleOrderDto> result = orders.stream()
//                .map(o -> new SimpleOrderDto(o)).collect(Collectors.toList());
//        return result;
//    }

    /**
     * JPA에서 DTO로 바로 조회한다.<br>
     * 쿼리를 1번만 호출하며 select절을 통해 원하는 데이터만 선택해서 조회할 수 있다.
     */
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4() {
        return orderSimpleQueryRepositoy.findOrderDto();
    }


    @Data
    static class SimpleOrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            this.orderId = order.getId();
            this.name = order.getMember().getName();
            this.orderDate = order.getOrderDate();
            this.orderStatus = order.getStatus();
            this.address = order.getDelivery().getAddress();
        }
    }
}
