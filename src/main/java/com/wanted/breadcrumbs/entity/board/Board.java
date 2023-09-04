package com.wanted.breadcrumbs.entity.board;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class Board {

    private Long id;
    private String title;
    private String contents;
    private Long parentId;
}
