package com.wanted.breadcrumbs.mapper;

import com.wanted.breadcrumbs.entity.Board;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface BoardMapper {

    void insertBoard(Board board);

    Optional<Board> getBoardById(Long id);

    List<Board> getSubListById(Long id);
}
