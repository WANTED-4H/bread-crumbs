package com.wanted.breadcrumbs.entity;

import lombok.*;

import java.util.List;
import java.util.Queue;
import java.util.Stack;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class BoardResult {
    private Long id;
    private String title;
    private String description;
    private Long parentId;
    private List<Board> subPageList;
    private String[] breadCrumbs;
}
