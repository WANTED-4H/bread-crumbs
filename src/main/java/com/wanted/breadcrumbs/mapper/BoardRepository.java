package com.wanted.breadcrumbs.mapper;

import com.wanted.breadcrumbs.entity.board.Board;

import java.util.List;

public interface BoardRepository {
    void insertBoard(Board board);
    Board getBoardById(Long id);
    List<Board> getChildById(Long id);
}
