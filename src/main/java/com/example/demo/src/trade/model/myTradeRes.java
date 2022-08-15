package com.example.demo.src.trade.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class myTradeRes {

    private int userIdx;
    private int boardIdx;
    private String tradeTitle;
    private String imgUrl;
    private String tradeStatus;
    private String price;
    private String address;
    private int interest;
    private String refresh;
    private String refreshStatus;
    private String tradeBoardCreateTime;
    private int chatRoomCount;


}
