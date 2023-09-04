package com.wanted.breadcrumbs.entity;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Board {
    private Long id;
    private String title;
    private String description;
    private Long parentId;
}