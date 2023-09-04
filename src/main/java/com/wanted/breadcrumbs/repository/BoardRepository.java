package com.wanted.breadcrumbs.repository;

import com.wanted.breadcrumbs.entity.Board;
import com.wanted.breadcrumbs.mapper.BoardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BoardRepository {
    private final BoardMapper boardMapper;

    public void insertBoard(Board board) {
        boardMapper.insertBoard(board);
    }

    public Optional<Board> getBoardById(Long boardId) {
        return boardMapper.getBoardById(boardId);
    }

    public List<Board> getSubListById(Long id) {
        return boardMapper.getSubListById(id);
    }
}
