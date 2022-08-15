package com.example.demo.src.user2;



import com.example.demo.config.BaseException;
import com.example.demo.src.user2.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class UserService2 {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao2 userDao2;
    private final UserProvider2 userProvider2;
    private final JwtService jwtService;


    @Autowired
    public UserService2(UserDao2 userDao2, UserProvider2 userProvider2, JwtService jwtService) {
        this.userDao2 = userDao2;
        this.userProvider2 = userProvider2;
        this.jwtService = jwtService;

    }

    //POST
    @Transactional
    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {


        //회원아이디 중복
        if (userProvider2.checkId(postUserReq.getId()) == 1) {
            throw new BaseException(POST_USERS_EXISTS_ID);
        }

        //이메일중복

        if (userProvider2.checkEmail(postUserReq.getEmail()) == 1) {

            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }



        String pwd;
        try {
            //암호화
            pwd = new SHA256().encrypt(postUserReq.getPassword());
            postUserReq.setPassword(pwd);

        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        try {
            if(postUserReq.getUserImg() == null){
                postUserReq.setUserImg("");
            }
            int userIdx = userDao2.createUser(postUserReq);
            int result = userDao2.insertAddress(userIdx, postUserReq.getAddress());
            //jwt 발급.
            String jwt = jwtService.createJwt(userIdx);
            return new PostUserRes(jwt, userIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

}