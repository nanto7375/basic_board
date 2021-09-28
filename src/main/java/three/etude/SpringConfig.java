package three.etude;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import three.etude.repository.BoardRepository;
import three.etude.repository.MemberRepository;
import three.etude.service.BoardService;
import three.etude.service.MemberService;

import javax.sql.DataSource;

@Configuration
public class SpringConfig {
    private final DataSource dataSource;

    public SpringConfig(DataSource dataSource) {
        this.dataSource = dataSource; }

    @Bean
    public MemberService memberService() {
        return new MemberService(memberRepository());
    }

    @Bean
    public MemberRepository memberRepository() {
        return new MemberRepository(dataSource); }



    @Bean
    public BoardService boardService() {
        return new BoardService(boardRepository());
    }

    @Bean
    public BoardRepository boardRepository() {
        return new BoardRepository(dataSource); }
}