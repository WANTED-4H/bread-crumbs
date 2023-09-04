package com.wanted.breadcrumbs.controller;

import com.wanted.breadcrumbs.entity.board.Board;
import com.wanted.breadcrumbs.entity.board.dto.BoardDto;
import com.wanted.breadcrumbs.service.FindBoardService;
import com.wanted.breadcrumbs.service.RegisterBoardServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {
    private final FindBoardService findBoardService;
    private final RegisterBoardServiceImpl registerBoardService;

    @GetMapping("/{id}")
    public ResponseEntity<BoardDto> boardGetById(@PathVariable Long id) {
        return ResponseEntity.ok(findBoardService.findBoardById(id));
    }
//
//    @PostMapping("")
//    public ResponseEntity<?> boardEnroll(@RequestBody Board board) {
//        registerBoardService.registerBoard(board);
//        return ResponseEntity.created(null).body(board);
//    }
}
