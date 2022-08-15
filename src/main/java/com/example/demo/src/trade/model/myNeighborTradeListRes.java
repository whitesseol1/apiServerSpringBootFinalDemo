package com.example.demo.src.trade.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class myNeighborTradeListRes {

private String imgUrl;
private String tradeTitle;
private String address;
private String refreshStatus;
private String writeTime;
private String price;
private int chatRoomCount;
private int interest;

}
