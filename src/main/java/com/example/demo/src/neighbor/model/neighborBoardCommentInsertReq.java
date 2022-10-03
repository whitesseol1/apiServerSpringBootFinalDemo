package com.example.demo.src.neighbor.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class neighborBoardCommentInsertReq {
    private int boardIdx;
    private String comment;
    private String imgUrl;
    private int commentGroup;
}
