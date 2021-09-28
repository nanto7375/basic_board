package three.etude.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import three.etude.controller.BoardForm;
import three.etude.domain.Board;
import three.etude.domain.Good;
import three.etude.domain.Reply;

import javax.sql.DataSource;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardRepository {

    private final JdbcTemplate jdbcTemplate;

    public BoardRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<Board> getList(String search, String keyword) {
        String where = "";
        if (!search.equals("") && !search.equals("all")) where = "where b_" + search + " like '%" + keyword + "%'";
        else if (!search.equals("") && search.equals("all")) where = "where b_content like '%" + keyword + "%' or b_title like '%" + keyword + "%'";
        return jdbcTemplate.query("select * from board " + where, boardRowMapper());
    }

    private RowMapper<Board> boardRowMapper() {
        return (rs, rowNum) -> {
            Board board = new Board();

            board.setB_idx(rs.getInt("b_idx"));
            board.setB_like(rs.getInt("b_like"));
            board.setB_read(rs.getInt("b_read"));
            board.setB_replycount(rs.getInt("b_replycount"));
            board.setM_id(rs.getString("m_id"));
            board.setB_title(rs.getString("b_title"));
            board.setB_content(rs.getString("b_content"));
            board.setB_img(rs.getString("b_img"));
            board.setB_date(rs.getString("b_date"));

            return board;
        };
    }

    public Board getDetail(int idx) {
        jdbcTemplate.update("update board set b_read = b_read + 1 where b_idx = ?", idx);
        Board result = jdbcTemplate.queryForObject("select * from board where b_idx = ?", boardRowMapper(), idx);

        return result;
    }

    public int register(BoardForm form) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("board").usingGeneratedKeyColumns("b_idx");

        Calendar today = Calendar.getInstance();
        String now = today.get(Calendar.YEAR) + "-" + (today.get(Calendar.MONTH) + 1) + "-" + today.get(Calendar.DATE)
                + " " + today.get(Calendar.HOUR_OF_DAY) + ":" + today.get(Calendar.MINUTE) + ":" + today.get(Calendar.SECOND);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("m_id", form.getM_id());
        parameters.put("b_content", form.getB_content());
        parameters.put("b_title", form.getB_title());
        parameters.put("b_date", now);
        parameters.put("b_like", 0);
        parameters.put("b_read", 0);
        parameters.put("b_replycount", 0);

        Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters));
        int idx = key.intValue();

        return idx;
    }

    public int update(BoardForm form) {

        Calendar today = Calendar.getInstance();
        String now = today.get(Calendar.YEAR) + "-" + (today.get(Calendar.MONTH) + 1) + "-" + today.get(Calendar.DATE)
                + " " + today.get(Calendar.HOUR_OF_DAY) + ":" + today.get(Calendar.MINUTE) + ":" + today.get(Calendar.SECOND);

        int result = jdbcTemplate.update(
                "update board set b_title = ?, b_content = ?, b_date = ? where b_idx = ? and m_id = ?", form.getB_title(), form.getB_content(), now, form.getB_idx(), form.getM_id()
        );

        return result;
    }

    public void doDelete(long idx) {
        jdbcTemplate.update("delete from board where b_idx = ?", idx);
    }

    public List<Reply> getReplys(int idx) {
        return jdbcTemplate.query("select * from reply where b_idx = ?",
                (rs, rowNum) -> {
                    Reply reply = new Reply();
                    reply.setR_idx(rs.getInt("r_idx"));
                    reply.setB_idx(rs.getInt("b_idx"));
                    reply.setM_id(rs.getString("m_id"));
                    reply.setR_content(rs.getString("r_content"));
                    reply.setR_date(rs.getString("r_date"));

                    return reply;
                }, idx);
    }


    public int regReply(Reply reply) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("reply").usingGeneratedKeyColumns("r_idx");

        Calendar today = Calendar.getInstance();
        String now = today.get(Calendar.YEAR) + "-" + (today.get(Calendar.MONTH) + 1) + "-" + today.get(Calendar.DATE)
                + " " + today.get(Calendar.HOUR_OF_DAY) + ":" + today.get(Calendar.MINUTE) + ":" + today.get(Calendar.SECOND);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("b_idx", reply.getB_idx());
        parameters.put("m_id", reply.getM_id());
        parameters.put("r_content", reply.getR_content());
        parameters.put("r_date", now);

        Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters));
        int idx = key.intValue();

        jdbcTemplate.update(
                "update board set b_replycount = b_replycount + 1 where b_idx = ?", reply.getB_idx()
        );

        return idx;
    }

    public int delReply(int r_idx, int b_idx) {
        jdbcTemplate.update(
                "update board set b_replycount = b_replycount - 1 where b_idx = ?", b_idx
        );

        return jdbcTemplate.update("delete from reply where r_idx = ?", r_idx);
    }

    public int likeOrUnlike(String x, int idx, String id) {
        int result = 0;
        if (x.equals("a")) {
            SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
            jdbcInsert.withTableName("good").usingGeneratedKeyColumns("g_idx");

            Calendar today = Calendar.getInstance();
            String now = today.get(Calendar.YEAR) + "-" + (today.get(Calendar.MONTH) + 1) + "-" + today.get(Calendar.DATE)
                    + " " + today.get(Calendar.HOUR_OF_DAY) + ":" + today.get(Calendar.MINUTE) + ":" + today.get(Calendar.SECOND);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("b_idx", idx);
            parameters.put("m_id", id);
            parameters.put("g_date", now);

            jdbcInsert.execute(new MapSqlParameterSource(parameters));
            result = 1;

            jdbcTemplate.update(
                    "update board set b_like = b_like + 1 where b_idx = ?", idx
            );

        } else {
            result = jdbcTemplate.update("delete from good where b_idx = ? and m_id = ? ", idx, id);

            jdbcTemplate.update(
                    "update board set b_like = b_like - 1 where b_idx = ?", idx
            );
        }
        return result;
    }

    public Good getGood(int idx, String id) {
        try {
            Good result = jdbcTemplate.queryForObject("select * from good where b_idx = ? and m_id = ?",
                (rs, rowNum) -> {
                    Good good = new Good();
                    good.setG_idx(rs.getInt("g_idx"));
                    good.setB_idx(rs.getInt("b_idx"));
                    good.setM_id(rs.getString("m_id"));
                    good.setG_date(rs.getString("g_date"));

                    return good;
                }, idx, id);

            return result;

        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
