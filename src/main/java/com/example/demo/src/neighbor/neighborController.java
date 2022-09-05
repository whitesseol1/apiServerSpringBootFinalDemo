package com.example.demo.src.neighbor;


import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.neighbor.model.neighborBoardDetailCommentRes;
import com.example.demo.src.neighbor.model.neighborBoardDetailRes;
import com.example.demo.src.neighbor.model.neighborBoardListRes;
import com.example.demo.src.trade.model.myChatRes;
import com.example.demo.src.user.model.GetUserRes;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import static com.example.demo.config.BaseResponseStatus.REQUEST_ERROR;

import java.util.List;


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
    public BaseResponse<neighborBoardDetailRes> neighborBoardDetail(@RequestParam(required = true) int neighborBoardIdx){
        try{
            if(neighborBoardIdx == 0){
                return new BaseResponse<>( REQUEST_ERROR);
            }

            neighborBoardDetailRes neighborBoardDetailRes = neighborProvider.neighborBoardDetail(neighborBoardIdx);
            return new BaseResponse<>(neighborBoardDetailRes);
        } catch(BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/neighborboardcomment")
   public BaseResponse<List<neighborBoardDetailCommentRes>> neighborBoardComment(@RequestParam(required = true) int neighborBoardIdx){
        try{
            if(neighborBoardIdx == 0){
                return new BaseResponse<>( REQUEST_ERROR);
            }

            List<neighborBoardDetailCommentRes> res = neighborProvider.neighborBoardComment(neighborBoardIdx);
            return new BaseResponse<>(res);
        } catch(BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PostMapping("/neighborboardlist")
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


}

