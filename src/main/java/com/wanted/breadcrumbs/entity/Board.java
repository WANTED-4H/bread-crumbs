package com.wanted.breadcrumbs.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Board {

    private Long id;
    private String title;
    private String description;
    private Long parentId;
    private Integer[] subBoardListIds;
}