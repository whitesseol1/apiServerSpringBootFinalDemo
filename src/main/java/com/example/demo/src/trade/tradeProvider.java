package com.example.demo.src.trade;

import com.example.demo.config.BaseException;
import com.example.demo.src.trade.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<myTradeRes> myFinishTradeList(int userIdx) throws BaseException{
        try{
            return tradeDao.myFinishTradeList(userIdx);
        } catch (Exception exception){
            throw new BaseException(RESPONSE_ERROR);
        }
    }

    public List<myTradeRes> myBuyTradeList(int userIdx) throws BaseException{
        //try{
            return tradeDao.myBuyTradeList(userIdx);
        //} catch (Exception exception){
       //     throw new BaseException(RESPONSE_ERROR);
        //}
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

    public tradeDetailRes2 tradeDetail(int boardIdx) throws BaseException{
        try{

            tradeDetailRes res = tradeDao.tradeDetail(boardIdx);
            List<String> imgUrl = tradeDao.tradeDetail2(boardIdx);
            return new tradeDetailRes2(res,imgUrl);
        } catch (Exception exception){
            throw new BaseException(RESPONSE_ERROR);
        }
    }

    public List<myChatDetailRes> chatDetail(int userIdx,int chatRoomIdx) throws BaseException{
       try{
            return tradeDao.chatDetail(userIdx,chatRoomIdx);
        } catch (Exception exception){
            throw new BaseException(RESPONSE_ERROR);
        }

    }

}
