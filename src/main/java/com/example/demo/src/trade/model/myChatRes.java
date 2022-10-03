package com.example.demo.src.trade.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class myChatRes {
    private String address;
    private String profileImg;
    private String userId;
    private String imgUrl;
    private String lastChatMessage;
    private String lastChatTime;
    private int chatRoomIdx;

}
