package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.order.OrderPracDto;
import lombok.Data;

import java.util.List;

@Data
public class MemberPracDto {

    private List<OrderPracDto> orders;

    public MemberPracDto(List<OrderPracDto> orders) {
        this.orders = orders;
    }
}
