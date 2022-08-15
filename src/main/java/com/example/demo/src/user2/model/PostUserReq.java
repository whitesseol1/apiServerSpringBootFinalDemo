package com.example.demo.src.user2.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostUserReq {
    private String userName;
    private String id;
    private String email;
    private String phone;
    private String address;
    private String userImg;
    private String password;
}