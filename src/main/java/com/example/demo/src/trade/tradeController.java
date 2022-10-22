package com.example.demo.src.trade;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.trade.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @RequestMapping("/myfinishtradelist")
    public BaseResponse<List<myTradeRes>> myFinishTradeList(){
        try{
            //jwt에서 idx 추출.
           int userIdxByJwt = jwtService.getUserIdx();
           List<myTradeRes> myTradeRes = tradeProvider.myFinishTradeList(userIdxByJwt);
           return new BaseResponse<>(myTradeRes);

        }catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @RequestMapping("/mybuytradelist")
    public BaseResponse<List<myTradeRes>> myBuyTradeList(){
        try{
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            List<myTradeRes> myTradeRes = tradeProvider.myBuyTradeList(userIdxByJwt);
            return new BaseResponse<>(myTradeRes);

        }catch (BaseException exception){
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
    public BaseResponse<List<userTradeListRes>> userTradeList(@RequestParam int userIdx){
        try {
            List<userTradeListRes> tradeList = tradeProvider.userTradeList(userIdx);
            return new BaseResponse<>(tradeList);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/tradedetail")
    public BaseResponse<tradeDetailRes2> tradeDetail(@RequestParam int boardIdx){
        try {
            tradeDetailRes2 tradeDetail = tradeProvider.tradeDetail(boardIdx);
            return new BaseResponse<>(tradeDetail);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/mychatdetail")
    public BaseResponse<List<myChatDetailRes>> chatDetail(@RequestParam int chatRoomIdx){
        int userIdxByJwt = 0;
        //int chatRoomIdx = req.getChatRoomIdx();
        try {
            //jwt에서 idx 추출.
            userIdxByJwt = jwtService.getUserIdx();

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

        try {
            List<myChatDetailRes> chatDetail = tradeProvider.chatDetail(userIdxByJwt, chatRoomIdx);
            return new BaseResponse<>(chatDetail);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PostMapping("/tradewrite")
  public BaseResponse<Integer> tradeWrite(@RequestBody tradeWriteReq req) {
        int userIdxByJwt = 0;
        try {
            //jwt에서 idx 추출.
            userIdxByJwt = jwtService.getUserIdx();
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

        try {
            int boardIdx = tradeService.tradeWrite(userIdxByJwt, req);
            return new BaseResponse<>(boardIdx);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PostMapping("/trademodify")
  public BaseResponse<Integer> tradeModify(@RequestBody tradeWriteReq req) {
        int userIdxByJwt = 0;
        try {
            //jwt에서 idx 추출.
            userIdxByJwt = jwtService.getUserIdx();
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

        try {
            int result = tradeService.tradeModify(userIdxByJwt, req);
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    @ResponseBody
    @PostMapping("/chatwrite")
  public BaseResponse<Integer> chatWrite(@RequestBody chatWriteReq req){
        int userIdxByJwt = 0;
        try {
            //jwt에서 idx 추출.
            userIdxByJwt = jwtService.getUserIdx();
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

        try {
            int result = tradeService.chatWrite(userIdxByJwt, req);
            //채팅을 처음 시작할때는 boardIdx, content를 받고 이미 시작돼 있을 경우는 roomIdx와 content를 받는다.
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/interest")
  public BaseResponse<String>  insertInterest(@RequestParam int boardIdx){
        int userIdxByJwt = 0;
        try {
            //jwt에서 idx 추출.
            userIdxByJwt = jwtService.getUserIdx();
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

        try{
            String result = tradeService.insertInterest(userIdxByJwt, boardIdx);
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}
