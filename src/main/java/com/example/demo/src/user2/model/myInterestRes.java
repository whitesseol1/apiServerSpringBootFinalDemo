package com.example.demo.src.user2.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class myInterestRes {

    private int boardIdx;
    private String imgUrl;
    private String tradeTitle;
    private String address1;
    private String status;
    private int price;
    private int chatRoomCount;
    private int interest;
    private String myStatus;
}
