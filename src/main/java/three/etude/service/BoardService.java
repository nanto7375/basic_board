package three.etude.service;

import org.springframework.web.bind.annotation.RequestParam;
import three.etude.controller.BoardForm;
import three.etude.domain.Board;
import three.etude.domain.Good;
import three.etude.domain.Reply;
import three.etude.repository.BoardRepository;

import java.util.List;

public class BoardService {

    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public List<Board> getList(String search, String keyword) {
        return boardRepository.getList(search, keyword);
    }
    public List<Reply> getReplys(int idx) {
        return boardRepository.getReplys(idx);
    }

    public Board getDetail(int idx) {
        return boardRepository.getDetail(idx);
    }

    public int register(BoardForm form) {
        int idx = boardRepository.register(form);
        return idx;
    }

    public int update(BoardForm form) {
        return boardRepository.update(form);
    }

    public void doDelete(long idx) {
        boardRepository.doDelete(idx);
    }

    public int regReply(Reply reply) {
        return boardRepository.regReply(reply);
    }

    public int delReply(int r_idx, int b_idx) {
        return boardRepository.delReply(r_idx, b_idx);
    }

    public int likeOrUnlike(String x, int idx, String id) {
        return boardRepository.likeOrUnlike(x, idx, id);
    }

    public Good getGood(int idx, String id) {
        return boardRepository.getGood(idx, id);
    }
}
