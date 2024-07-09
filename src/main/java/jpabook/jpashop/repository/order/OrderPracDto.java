package jpabook.jpashop.repository.order;

import jpabook.jpashop.repository.order.query.OrderItemQueryDto;
import lombok.Data;

import java.util.List;

@Data
public class OrderPracDto {

    private Long id;
    private Long userId;
    private String userName;

    public OrderPracDto(Long id) {
        this.id = id;
    }

    public OrderPracDto(Long id, Long userId) {
        this.id = id;
        this.userId = userId;
    }

    public OrderPracDto(Long id, Long userId, String userName) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
    }
}
