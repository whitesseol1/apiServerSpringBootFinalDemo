package com.example.demo.src.trade.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class tradeWriteReq {
    private String tradeTitle;
    private String content;
    private int price;
    private String itemCategory;
    private List<String> imgUrl;
    private String isOffer;
    private int boardIdx;

}
