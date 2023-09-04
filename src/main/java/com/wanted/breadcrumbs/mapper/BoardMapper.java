package com.wanted.breadcrumbs.mapper;

import com.wanted.breadcrumbs.entity.Board;

import java.util.List;


public interface BoardMapper {

    void insertBoard(Board board);
    Board getBoardById(Long id);
    // 다른 필요한 메소드들 추가 가능
}
