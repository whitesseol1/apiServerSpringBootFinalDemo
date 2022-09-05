package com.example.demo.src.neighbor.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class neighborBoardListRes {

    private String category;
    private String content;
    private String userId;
    private String address;
    private String writeTime;
    private int wonderStatus;
    private int commentCount;

}
