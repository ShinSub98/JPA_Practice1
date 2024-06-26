package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @NotEmpty
    private String name;
    private String password;

    @Embedded
    private Address address;

    @JsonIgnore // 데이터를 json 형태로 출력할 때 해당 필드는 출력하지 않는 어노테이션
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();
}
