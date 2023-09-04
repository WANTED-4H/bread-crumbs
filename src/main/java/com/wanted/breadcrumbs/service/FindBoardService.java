package com.wanted.breadcrumbs.service;

import com.wanted.breadcrumbs.entity.board.Board;
import com.wanted.breadcrumbs.entity.board.dto.BoardDto;

import java.util.List;

public interface FindBoardService {
    Board findBoardByIdFromDB(Long id);

    List<String> findSubPageById(Long id);

    List<Board> findBreadCrumbsById(Long id);

    BoardDto findBoardById(Long id);
}
