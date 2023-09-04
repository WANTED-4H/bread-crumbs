package com.wanted.breadcrumbs.entity.board.dto;

import com.wanted.breadcrumbs.entity.board.Board;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardDto {

    private Long boardId;
    private String title;
    private String contents;
    private List<String> subPages;
    private List<String> breadCrumbs;

    public static BoardDto of(Board board, List<String> subPages, List<String> breadCrumbs) {
        return BoardDto.builder()
                .boardId(board.getId())
                .subPages(subPages)
                .title(board.getTitle())
                .breadCrumbs(breadCrumbs)
                .contents(board.getContents())
                .build();
    }
}
