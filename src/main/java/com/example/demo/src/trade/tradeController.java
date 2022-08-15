package com.example.demo.src.trade;

import com.example.demo.src.user2.model.PostLoginReq;
import com.example.demo.src.user2.model.PostLoginRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.src.user2.UserProvider2;
import com.example.demo.src.user2.UserService2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.trade.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/app/trade")

public class tradeController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final tradeProvider tradeProvider;
    @Autowired
    private final tradeService tradeService;
    @Autowired
    private final JwtService jwtService;

    public tradeController(tradeProvider tradeProvider, tradeService tradeService, JwtService jwtService){
        this.tradeProvider = tradeProvider;
        this.tradeService = tradeService;
        this.jwtService = jwtService;
    }

    @ResponseBody
    @RequestMapping("/mychatlist")
    public BaseResponse<List<myChatRes>> chatList() {

        int userIdxByJwt = 0;

        try {
            //jwt에서 idx 추출.
            userIdxByJwt = jwtService.getUserIdx();

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

        try {
            List<myChatRes> chatList = tradeProvider.myChatList(userIdxByJwt);
            return new BaseResponse<>(chatList);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }


    }

    @ResponseBody
    @RequestMapping("/mytradelist")
    public BaseResponse<List<myTradeRes>> myTradeList() {


        int userIdxByJwt = 0;

        try {
            //jwt에서 idx 추출.
            userIdxByJwt = jwtService.getUserIdx();

        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

        try{
            List<myTradeRes> myTradeRes= tradeProvider.myTradeList(userIdxByJwt);
            return new BaseResponse<>(myTradeRes);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    @ResponseBody
    @RequestMapping("/myneighborboardtrade")
  public BaseResponse<List<myNeighborTradeListRes>> myNeighborTradeList(){

        int userIdxByJwt = 0;

        try {
            //jwt에서 idx 추출.
            userIdxByJwt = jwtService.getUserIdx();

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

        try {
            List<myNeighborTradeListRes> tradeList = tradeProvider.myNeighborTradeList(userIdxByJwt);
            return new BaseResponse<>(tradeList);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    @ResponseBody
    @RequestMapping("/mykeywordtradelist")
  public BaseResponse<List<myKeywordTradeListRes>> myKeywordTradeList(){
        int userIdxByJwt = 0;

        try {
            //jwt에서 idx 추출.
            userIdxByJwt = jwtService.getUserIdx();

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

        try {
            List<myKeywordTradeListRes> tradeList = tradeProvider.myKeywordTradeList(userIdxByJwt);
            return new BaseResponse<>(tradeList);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    @ResponseBody
    @GetMapping ("/usertradelist")
    public BaseResponse<List<userTradeListRes>> userTradeList(@RequestParam(required = false) int userIdx){
        try {
            List<userTradeListRes> tradeList = tradeProvider.userTradeList(userIdx);
            return new BaseResponse<>(tradeList);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/tradedetail")
    public BaseResponse<tradeDetailRes> tradeDetail(@RequestParam(required = false) int boardIdx){
        try {
            tradeDetailRes tradeDetail = tradeProvider.tradeDetail(boardIdx);
            return new BaseResponse<>(tradeDetail);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


}
