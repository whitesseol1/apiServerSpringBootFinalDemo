package com.example.demo.src.user2.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostLoginRes {

    private int userIdx;
    private String jwt;
}