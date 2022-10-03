package com.example.demo.src.trade.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class tradeDetailRes {

    private String userId;
    private String profileImg;
    private String tradeImg;
    private String tradeTitle;
    private String content;
    private String itemCategory;
    private String price;
    private float mannerPoint;
    private String address;
    private String refreshStatus;
    private String writeTime;
    private int boardIdx;


}
