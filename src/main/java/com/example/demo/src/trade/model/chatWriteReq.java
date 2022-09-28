package com.example.demo.src.trade.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class chatWriteReq {
    private int userIdx;
    private int roomIdx;
    private int boardIdx;
    private String content;

}
