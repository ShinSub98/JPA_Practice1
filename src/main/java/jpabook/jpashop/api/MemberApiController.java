package jpabook.jpashop.api;

import jakarta.validation.Valid;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController // 데이터를 json, xml로 바로 응답하기 위한 어노테이션
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    /**
     * 엔티티에 프레젠테이션 레이어 로직이 추가되어야 하며<br>
     * 엔티티의 모든 값을 노출하게 된다.<br>
     * 이처럼 엔티티를 수정하게 되면 API 스펙이 변경되기 때문에 이런 개발은 지양할 것.<br>
     * 뿐만 아니라 출력 포맷을 엔티티의 배열로 고정할 수 밖에 없게 된다.
     */
    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        return memberService.findMembers();
    }

    /**
     * Result, MemberDto 클래스를 활용해 출력 양식 성형 로직도 프레젠테이션 레벨에서 해결할 수 있고<br>
     * 이 외에 다른 데이터를 추가하기도 용이하여 이후 유지보수에 좋다.
     */
    @GetMapping("/api/v2/members")
    public Result membersV2() {
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName())).collect(Collectors.toList());

        return new Result(collect.size(), collect);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count;
        private T data; // data라는 key의 배열 데이터를 가진다.
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {

        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request) {

        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }


    /**
     * 아래와 같이 요청 및 응답 DTO를 직접 정의하면 API 스펙도 일정하게 유지할 수 있으며<br>
     * 동시에 Entity에서 수정이 일어나도 컴파일 시 에러가 발생하기 때문에 해당 에러만 수정하면 API에 문제가 없다.
     */
    @Data
    static class CreateMemberRequest {
        private String name;
    }
    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    static class UpdateMemberResponse {
        private Long id;
        private String name;

        public UpdateMemberResponse(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
