package com.wanted.breadcrumbs.service;

import com.wanted.breadcrumbs.entity.Board;
import com.wanted.breadcrumbs.mapper.BoardMapper;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class BoardService {
    private final BoardMapper boardMapper;

    @Autowired
    public BoardService(SqlSessionTemplate sqlSessionTemplate) {
        this.boardMapper = sqlSessionTemplate.getMapper(BoardMapper.class);
    }

    public Board createBoard(Board board) {
        boardMapper.insertBoard(board);
        return board;
    }

}
