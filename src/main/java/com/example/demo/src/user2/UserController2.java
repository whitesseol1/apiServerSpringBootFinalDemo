package com.example.demo.src.user2;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user2.model.PostUserReq;
import com.example.demo.src.user2.model.PostUserRes;
import com.example.demo.src.user2.model.kakaoLoginRes;
import com.example.demo.src.user2.model.myAccountBookRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.*;


@RestController
@RequestMapping("/app/users")
public class UserController2 {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserProvider2 userProvider2;
    @Autowired
    private final UserService2 userService2;
    @Autowired
    private final JwtService jwtService;




    public UserController2(UserProvider2 userProvider2, UserService2 userService2, JwtService jwtService){
        this.userProvider2 = userProvider2;
        this.userService2 = userService2;
        this.jwtService = jwtService;
    }



    /**
     * 회원가입 API
     * [POST] /users
     * @return BaseResponse<PostUserRes>
     */
    // Body
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {
        // TODO: email 관련한 짧은 validation 예시입니다. 그 외 더 부가적으로 추가해주세요!
        logger.warn("req테스트 " +postUserReq);
        if(postUserReq.getEmail() == null || postUserReq.getEmail() == ""){
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        if(postUserReq.getPhone() == null || postUserReq.getPhone() == "" ){
            return new BaseResponse<>(POST_USERS_EMPTY_PHONE);
        }
        if(postUserReq.getId() == null || postUserReq.getId() == "" ){
            return new BaseResponse<>(USERS_EMPTY_USER_ID);
        }
        if(postUserReq.getAddress() == null || postUserReq.getAddress() == ""){
            return new BaseResponse<>(POST_USERS_EMPTY_ADDRESS);
        }
        //이메일 정규표현
        if(!isRegexEmail(postUserReq.getEmail())){
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        //아이디 정규표현
        if(!isRegexId(postUserReq.getId())){
            return new BaseResponse<>(POST_USERS_INVALID_ID);
        }
        //비번 정규표현 8자리이상 특수문자,숫자포함
        if(!isRegexPassword(postUserReq.getPassword())){
            return new BaseResponse<>(POST_USERS_INVALID_PASSWORD);
        }
        //핸드폰번호 정규표현
        if(!isRegexPhone(postUserReq.getPhone())){
            return new BaseResponse<>(POST_USERS_INVALID_PHONE);
        }

        //주소 정규표현
        if(!isRegexAddress(postUserReq.getAddress())){
            return new BaseResponse<>(POST_USERS_INVALID_ADDRESS);
        }

        try{
            PostUserRes postUserRes = userService2.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }






    /**
     * 로그인 API
     * [POST] /users/logIn
     * @return BaseResponse<PostLoginRes>
     */
    @ResponseBody
    @GetMapping("/oauth/kakao")
    public BaseResponse<kakaoLoginRes> kakaoCallBack(@RequestParam String code){
        try{
            String access_Token = userService2.getkakaoAccessToken(code);
            kakaoLoginRes kakaoLoginRes = userService2.kakaoLogin(access_Token);
            return new BaseResponse<>(kakaoLoginRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PostMapping("/myaccountbook")
    public BaseResponse<myAccountBookRes>  myAccountBook(@RequestBody String yearMonth){
            int userIdxByJwt = 0;
            String yearMonth1 = yearMonth;
        try {
            //jwt에서 idx 추출.
             userIdxByJwt = jwtService.getUserIdx();

            if(userIdxByJwt <1){
                return new BaseResponse<>(INVALID_USER_JWT);
            }



            myAccountBookRes res = userProvider2.myAccountBook(userIdxByJwt, yearMonth1);

            return new BaseResponse<>(res);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }







   /* *//**
     * 유저정보변경 API
     * [PATCH] /users/:userIdx
     * @return BaseResponse<String>
     *//*
    @ResponseBody
    @PatchMapping("/{userIdx}")
    public BaseResponse<String> modifyUserName(@PathVariable("userIdx") int userIdx, @RequestBody User user){
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            //같다면 유저네임 변경
            PatchUserReq patchUserReq = new PatchUserReq(userIdx,user.getUserName());
            userService.modifyUserName(patchUserReq);

            String result = "";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
*/

}
