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
    private int myLikeStatus;
    private String writeTime;
    private int commentIdx;
    private String imgUrl;



}
