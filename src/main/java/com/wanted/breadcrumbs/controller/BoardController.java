package com.wanted.breadcrumbs.controller;

import com.wanted.breadcrumbs.dto.BoardDetail;
import com.wanted.breadcrumbs.entity.Board;
import com.wanted.breadcrumbs.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/board")
public class BoardController {
    @Autowired
    BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }
    @PostMapping
    public ResponseEntity<Board> createBoard(@RequestBody Board board) {
        Board createdBoard = boardService.createBoard(board);
        return new ResponseEntity<>(createdBoard, HttpStatus.CREATED);
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<BoardDetail> getBoardWithBreadcrumbs(@PathVariable Long boardId) {
        Board board = boardService.getBoard(boardId);
        List<Board> childBoards = boardService.getChildBoards(boardId);
        List<Board> breadcrumbsPath = boardService.findBreadcrumbs(board);

        BoardDetail boardDetail = new BoardDetail(board, childBoards, breadcrumbsPath);
        return new ResponseEntity<>(boardDetail, HttpStatus.OK);
    }

}
