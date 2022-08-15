package com.example.demo.src.neighbor.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class neighborBoardDetailCommentRes {
    private String userId;
    private String profileImg;
    private String address;
    private String comment;
    private int like;
    private String writeTime;



}
