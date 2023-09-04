package com.wanted.breadcrumbs.entity;

import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Board {

    private Long id;
    private String title;
    private String description;
    private Long parentId;

}