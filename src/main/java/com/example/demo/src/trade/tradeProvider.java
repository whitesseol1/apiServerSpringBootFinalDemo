package com.example.demo.src.trade;

import com.example.demo.config.BaseException;
import com.example.demo.src.user2.UserDao2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.src.trade.model.*;
import com.example.demo.utils.JwtService;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.demo.config.BaseResponseStatus.RESPONSE_ERROR;

@Service
public class tradeProvider {

    private final tradeDao tradeDao;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public tradeProvider(tradeDao tradeDao, JwtService jwtService) {
        this.tradeDao = tradeDao;
        this.jwtService = jwtService;
    }

    public List<myChatRes> myChatList(int userIdx) throws BaseException{
        try{
            return tradeDao.myChatList(userIdx);
        } catch (Exception exception){
            throw new BaseException(RESPONSE_ERROR);
        }
    }

    public List<myTradeRes> myTradeList(int userIdx) throws BaseException{
        try{
            return tradeDao.myTradeList(userIdx);
        } catch (Exception exception){
            throw new BaseException(RESPONSE_ERROR);
        }

    }

    public List<myNeighborTradeListRes> myNeighborTradeList(int userIdx) throws BaseException{
        try{
            return tradeDao.myNeighborTradeList(userIdx);
        } catch (Exception exception){
            throw new BaseException(RESPONSE_ERROR);
        }
    }

    public List<myKeywordTradeListRes> myKeywordTradeList(int userIdx) throws BaseException{
        try{
            return tradeDao.myKeywordTradeList(userIdx);
        } catch (Exception exception){
            throw new BaseException(RESPONSE_ERROR);
        }
    }

    public List<userTradeListRes> userTradeList(int userIdx) throws BaseException{
        try{
            return tradeDao.userTradeList(userIdx);
        } catch (Exception exception){
            throw new BaseException(RESPONSE_ERROR);
        }
    }

    public tradeDetailRes tradeDetail(int boardIdx) throws BaseException{
        try{
            return tradeDao.tradeDetail(boardIdx);
        } catch (Exception exception){
            throw new BaseException(RESPONSE_ERROR);
        }
    }

}
