package com.example.demo.src.trade.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class myChatDetailRes {

    private String tradeImg;
    private String tradeStatus;
    private String price;
    private String tradeTitle;
    private String content;
    private String profileImg;
    private String writer;
    private String writeTime;
    private int chatIdx;

}
