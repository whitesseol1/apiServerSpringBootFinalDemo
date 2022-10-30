package com.example.demo.src.neighbor;


import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.neighbor.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.REQUEST_ERROR;


@RestController
@RequestMapping("/app/neighbor")
public class neighborController {

    @Autowired
    private final neighborProvider neighborProvider;

    @Autowired
    private final neighborService neighborService;

    @Autowired
    private final JwtService jwtService;

    public neighborController(neighborProvider neighborProvider, neighborService neighborService, JwtService jwtService){
        this.neighborProvider = neighborProvider;
        this.neighborService = neighborService;
        this.jwtService = jwtService;
    }

    @ResponseBody
    @GetMapping("/neighborboard")
    public BaseResponse<neighborBoardDetailRes2> neighborBoardDetail(@RequestParam(required = true) int neighborBoardIdx){
        int userIdxByJwt = 0;
        try {
            //jwt에서 idx 추출.
            userIdxByJwt = jwtService.getUserIdx();
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

        try{
            if(neighborBoardIdx == 0){
                return new BaseResponse<>( REQUEST_ERROR);
            }

            neighborBoardDetailRes2 neighborBoardDetailRes = neighborProvider.neighborBoardDetail(neighborBoardIdx,userIdxByJwt);
            return new BaseResponse<>(neighborBoardDetailRes);
        } catch(BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/neighborboardcomment")
   public BaseResponse<List<neighborBoardDetailCommentRes>> neighborBoardComment(@RequestParam(required = true) int neighborBoardIdx){
        int userIdxByJwt = 0;
        try {
            //jwt에서 idx 추출.
            userIdxByJwt = jwtService.getUserIdx();
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

        try{
            if(neighborBoardIdx == 0){
                return new BaseResponse<>( REQUEST_ERROR);
            }

            List<neighborBoardDetailCommentRes> res = neighborProvider.neighborBoardComment(neighborBoardIdx,userIdxByJwt);
            return new BaseResponse<>(res);
        } catch(BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/neighborboardlist")
    public BaseResponse<List<neighborBoardListRes>> neighborBoardList(){
        int userIdxByJwt = 0;

        try {
            //jwt에서 idx 추출.
            userIdxByJwt = jwtService.getUserIdx();

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

        try {
            List<neighborBoardListRes> neighborBoardList = neighborProvider.neighborBoardList(userIdxByJwt);
            return new BaseResponse<>(neighborBoardList);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PostMapping("/neighborboardwrite")
    public BaseResponse<Integer> neighborboardWrite(@RequestBody neighborBoardInsertReq req){
        try{
            //jwt에서 idx 추출.
           int userIdxByJwt = jwtService.getUserIdx();
           int boardIdx = neighborService.neighborboardWrite(req,userIdxByJwt);
            return new BaseResponse<>(boardIdx);
        }catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PostMapping("/neighborboardcommentwrite")
    public BaseResponse<Integer> neighborboardCommentWrite(@RequestBody neighborBoardCommentInsertReq req){
        try{
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            int commentIdx = neighborService.neighborboardCommentWrite(req,userIdxByJwt);
            return new BaseResponse<>(commentIdx);
        }catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PostMapping("/neighborboardcommentwrite2")
    public BaseResponse<Integer> neighborboardCommentWrite2(@RequestBody neighborBoardCommentInsertReq req){
        try{
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            int commentIdx = neighborService.neighborboardCommentWrite2(req,userIdxByJwt);
            return new BaseResponse<>(commentIdx);
        }catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

     @ResponseBody
     @PostMapping("/empathy")
   public BaseResponse<String> insertEmpathy(@RequestBody empathyReq req){
       try{

           int userIdxByJwt = jwtService.getUserIdx();
           String result = neighborService.insertEmpathy(userIdxByJwt, req.getBoardIdx());
           return new BaseResponse<>(result);
       } catch(BaseException exception) {
           return new BaseResponse<>((exception.getStatus()));
       }

     }


}

