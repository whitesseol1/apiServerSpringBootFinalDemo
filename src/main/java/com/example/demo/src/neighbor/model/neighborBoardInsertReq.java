package com.example.demo.src.neighbor.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class neighborBoardInsertReq {
    private int boardIdx;
    private int userIdx;
    private int category;
    private String content;
    private List<String> boardImg;

}
