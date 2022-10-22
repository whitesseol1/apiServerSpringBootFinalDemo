package com.example.demo.src.user2;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user2.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * 로그인 API_1
     * [POST] /users/logIn
     * @return BaseResponse<PostLoginRes>
     */
    @ResponseBody
    @PostMapping("/logIn")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq){
        try{
            // TODO: 로그인 값들에 대한 형식적인 validatin 처리해주셔야합니다!
            // TODO: 유저의 status ex) 비활성화된 유저, 탈퇴한 유저 등을 관리해주고 있다면 해당 부분에 대한 validation 처리도 해주셔야합니다.
            PostLoginRes postLoginRes = userProvider2.logIn(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }




    /**
     * 로그인 API_2_카카오로그인
     * 회원가입 및 로그인 (기존 회원이 처음 카카오 로그인시 기존 회원 정보를 불러올 수 없어서
     * 카카오 인증 후 우리 서버에서는 새로운 userIdx를 발급하고 카카오정보를 저장 후 jwt발급 한다.)
     * - 다음 로그인에는 기존 카카오 정보가 저장되 있으면 userIdx를 조회해 반환한다.
     * 우리서버에 카카오 정보가 있으면 userIdx 조회하고, 없으면 저장 후 userIdx 발급, 반환하므로
     * 카카오로 회원가입 & 카카오로 로그인  두가지 기능이 다 포함된다.
     * (카카오로 로그인시 기존 회원 가입 정보가 있는 회원은 기존 정보를 조회할 수 없어 혼선이 있을 수 있어 '회원가입 및 로그인'
     * 으로 지칭하여 새로운 계정으로 접속됨을 알려준다.)
     * [POST] /oauth/kakao
     * @return BaseResponse<PostLoginRes>
     */
    @ResponseBody
    @GetMapping("/oauth/kakao")
    public BaseResponse<PostLoginRes> kakaoCallBack(@RequestParam String code){
        try{
            String access_Token = userService2.getkakaoAccessToken(code);
            kakaoLoginRes kakaoLoginRes = userService2.kakaoLogin(access_Token);
            PostLoginRes res = userService2.kakaoLogin2(kakaoLoginRes);
            return new BaseResponse<>(res);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/myaccountbook")
    public BaseResponse<myAccountBookRes>  myAccountBook(@RequestParam String yearMonth){
            int userIdxByJwt = 0;
            System.out.println("yearMonth "+yearMonth);
        try {
            //jwt에서 idx 추출.
             userIdxByJwt = jwtService.getUserIdx();

            if(userIdxByJwt <1){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            myAccountBookRes res = userProvider2.myAccountBook(userIdxByJwt, yearMonth);

            return new BaseResponse<>(res);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    @ResponseBody
    @GetMapping("/keyword")
    public BaseResponse<Integer> keywordAlarm(@RequestParam String keyword){
        int userIdxByJwt = 0;

        try{
            //jwt에서 idx 추출.
            userIdxByJwt = jwtService.getUserIdx();
            if(userIdxByJwt <1){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            int result = userService2.insertKeywordAlarm(keyword,userIdxByJwt);
            return new BaseResponse<>(result);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/neighborset")
    public BaseResponse<String> neighborSet(@RequestParam String neighbor){
        int userIdxByJwt = 0;
        try{
            //jwt에서 idx 추출.
            userIdxByJwt = jwtService.getUserIdx();
            if(userIdxByJwt <1) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            String result = userService2.neighborSet(neighbor, userIdxByJwt);
            return new BaseResponse<>(result);
        }catch(BaseException exception){
                return new BaseResponse<>((exception.getStatus()));
            }
    }

    @ResponseBody
    @GetMapping("/interestlist")
    public BaseResponse<List<myInterestRes>> interestList(){

        int userIdxByJwt = 0;
        try {
            //jwt에서 idx 추출.
            userIdxByJwt = jwtService.getUserIdx();
            if (userIdxByJwt < 1) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<myInterestRes> result = userService2.interestList(userIdxByJwt);
            return new BaseResponse<>(result);
        }catch(BaseException exception){
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
