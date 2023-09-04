package com.wanted.breadcrumbs.service;

import com.wanted.breadcrumbs.entity.board.Board;
import com.wanted.breadcrumbs.mapper.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterBoardServiceImpl {
    private final BoardRepository boardRepository;

    public void registerBoard(Board board) {
        boardRepository.insertBoard(board);
    }
}
