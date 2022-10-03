package com.example.demo.src.neighbor.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class neighborBoardDetailRes {

    private String category;
    private String content;
    private String userId;
    private String addressConfirm;
    private String address;
    private String profileImg;
    private String createdTime;
    private String refreshStatus;
    private int empathyStatus;
    private int wonderStatus;
    private int boardIdx;
}
