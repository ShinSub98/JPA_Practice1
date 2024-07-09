package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;

    @Test
    public void 회원가입() {
        //1
        Member member = new Member();
        member.setName("Kim");

        //2
        Long savedId = memberService.join(member);

        //3
        Assert.assertEquals(member, memberRepository.findById(savedId));
    }

    @Test(expected = IllegalStateException.class)
    public void 중복_회원_예외() throws Exception {
        //1
        Member member1 = new Member();
        member1.setName("Kim");

        Member member2 = new Member();
        member2.setName("Kim");

        //2
        memberService.join(member1);
        memberService.join(member2);

        Assert.fail("예외 발생해야 함");
    }

    @Test
    public void 엔티티_조회_테스트() throws Exception {
        /*given*/
        Member member = memberRepository.findById(1L);

        /*when*/
        List<Order> orders = member.getOrders();

        /*then*/
        Assert.assertEquals(2, orders.size());
        System.out.println("주문의 ID값 = " + orders.get(0).getId());
    }
}