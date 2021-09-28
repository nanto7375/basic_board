package three.etude.service;

import three.etude.controller.MemberForm;
import three.etude.repository.MemberRepository;
import three.etude.domain.Member;

public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public String join(Member member) {
        validateDuplicateMember(member);
        memberRepository.join(member);
        return member.getM_id();
    }

    private void validateDuplicateMember(Member member) {
        memberRepository.findById(member.getM_id())
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 존재하는 회원입니다.");
                });
    }

    public Member login(MemberForm member) {
        Member loginMember = memberRepository.login(member);
        return loginMember;
    }
}
