package three.etude.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import three.etude.service.MemberService;
import three.etude.domain.Member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/main")
    public String main() {
        return "main";
    }

    @GetMapping("/join")
    public String createForm() {
        return "members/createMemberForm";
    }

    @PostMapping("/join")
    public String create(MemberForm form) {
        Member member = new Member();
        member.setM_id(form.getM_id());
        member.setM_pwd(form.getM_pwd());

        memberService.join(member);

        return "main";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "members/loginForm";
    }

    @PostMapping("/login")
    public String login(HttpServletRequest request, MemberForm form) {

        Member loginMember = memberService.login(form);
        HttpSession session = request.getSession();

        if (loginMember != null) {
            session.setAttribute("loginMember", loginMember);
            return "redirect:/main";

        } else {
            return "redirect:/login";
        }

    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.invalidate();

        return "redirect:/main";
    }
}
