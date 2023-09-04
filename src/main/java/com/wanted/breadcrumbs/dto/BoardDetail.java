package com.wanted.breadcrumbs.dto;

import com.wanted.breadcrumbs.entity.Board;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BoardDetail {
    private String boardTitle;
    private String boardContent;
    private List<String> childBoardTitles;
    private List<String> breadcrumbTitles;

    public BoardDetail(Board board, List<Board> childBoards, List<Board> breadcrumbs) {
        this.boardTitle = board.getTitle();
        this.boardContent = board.getDescription();

        this.childBoardTitles = new ArrayList<>();
        for (Board childBoard : childBoards) {
            this.childBoardTitles.add(childBoard.getTitle());
        }

        this.breadcrumbTitles = new ArrayList<>();
        for (Board breadcrumb : breadcrumbs) {
            this.breadcrumbTitles.add(breadcrumb.getTitle());
        }
    }
}
