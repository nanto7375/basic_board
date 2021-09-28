package three.etude.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import three.etude.controller.MemberForm;
import three.etude.domain.Member;

import javax.sql.DataSource;
import java.util.*;

public class MemberRepository {

    private final JdbcTemplate jdbcTemplate;

    public MemberRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Member join(Member member) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("member");

        Calendar today = Calendar.getInstance();
        String now = today.get(Calendar.YEAR) + "-" + (today.get(Calendar.MONTH) + 1) + "-" + today.get(Calendar.DATE)
                + " " + today.get(Calendar.HOUR) + ":" + today.get(Calendar.MINUTE) + ":" + today.get(Calendar.SECOND);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("m_id", member.getM_id());
        parameters.put("m_pwd", member.getM_pwd());
        parameters.put("m_date", now);

        jdbcInsert.execute(new MapSqlParameterSource(parameters));

        return member;
    }

    public Optional<Member> findById(String id) {
        List<Member> result = jdbcTemplate.query("select * from member where m_id = ?", memberRowMapper(), id);
        return result.stream().findAny();
    }

    private RowMapper<Member> memberRowMapper() {
        return (rs, rowNum) -> {
            Member member = new Member();
            member.setM_id(rs.getString("m_id"));
            member.setM_pwd(rs.getString("m_pwd"));
            member.setM_date(rs.getString("m_date"));
            return member;
        };
    }

    public Member login(MemberForm member) {
        try {
            Member result = jdbcTemplate.queryForObject(
                    "select * from member where m_id = ? and m_pwd = ?",
                    memberRowMapper(), member.getM_id(), member.getM_pwd());
            return result;

        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
