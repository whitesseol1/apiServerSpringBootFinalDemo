package com.example.demo.src.user2;


import com.example.demo.config.BaseException;
import com.example.demo.src.user2.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class UserProvider2 {

    private final UserDao2 userDao2;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserProvider2(UserDao2 userDao2, JwtService jwtService) {
        this.userDao2 = userDao2;
        this.jwtService = jwtService;
    }

    public int checkEmail(String email) throws BaseException{
        try{
            return userDao2.checkEmail(email);
        } catch (Exception exception){
           throw new BaseException(DATABASE_ERROR);
        }
    }


    public int checkId(String id) throws BaseException{
        try{
            return userDao2.checkId(id);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException{
        User user = userDao2.getPwd(postLoginReq);
        String encryptPwd;
        try {
            encryptPwd=new SHA256().encrypt(postLoginReq.getPassword());
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_DECRYPTION_ERROR);
        }

        if(user.getPassword().equals(encryptPwd)){
            int userIdx = user.getUserIdx();
            String jwt = jwtService.createJwt(userIdx);
            return new PostLoginRes(userIdx,jwt);
        }
        else{
            throw new BaseException(FAILED_TO_LOGIN);
        }

    }

    public myAccountBookRes myAccountBook(int userIdx,String yearMonth) throws BaseException{
        try{
            return userDao2.myAccountBook(userIdx, yearMonth);
        } catch (Exception exception){
            throw new BaseException(RESPONSE_ERROR);
        }

    }


}