package com.example.demo.src.trade.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class userTradeListRes {

    private String userId;
    private String tradeImg;
    private String tradeTitle;
    private String price;
    private int boardIdx;
}
