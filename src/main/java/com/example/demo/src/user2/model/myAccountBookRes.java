package com.example.demo.src.user2.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class myAccountBookRes {

    private String userId;
    private String profileImg;
    private int tradeCount;
    private String price;


}
