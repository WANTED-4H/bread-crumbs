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

    public Board getBoard(Long boardId) {
        return boardMapper.getBoardById(boardId);
    }

    public List<Board> findBreadcrumbs(Board currentBoard) {
        List<Board> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(currentBoard);
        System.out.println(currentBoard.getDescription());

        while (currentBoard.getParentId() != null && currentBoard.getId() != currentBoard.getParentId()) {
            Board parentBoard = boardMapper.getBoardById(currentBoard.getParentId());
            System.out.println(currentBoard.getParentId());
            if (parentBoard == null) {
                break;
            }
            breadcrumbs.add(parentBoard);
            currentBoard = parentBoard;
        }

        Collections.reverse(breadcrumbs);

        return breadcrumbs;
    }
    public List<Board> getChildBoards(Long parentId) {
        List<Board> childBoards = boardMapper.getChildBoards(parentId);
        if (childBoards == null) {
            return Collections.emptyList();
        }

        return childBoards;
    }


}
