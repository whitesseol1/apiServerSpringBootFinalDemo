package com.example.demo.src.trade;

import com.example.demo.config.BaseException;
import com.example.demo.src.trade.model.chatWriteReq;
import com.example.demo.src.trade.model.chatWriteUserRes;
import com.example.demo.src.trade.model.tradeWriteReq;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class tradeService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final tradeDao tradeDao;
    private final tradeProvider tradeProvider;
    private final JwtService jwtService;

    @Autowired
    public tradeService(tradeDao tradeDao, tradeProvider tradeProvider,  JwtService jwtService) {
        this.tradeProvider = tradeProvider;
        this.tradeDao = tradeDao;
        this.jwtService = jwtService;

    }

    public int tradeWrite(int userIdx, tradeWriteReq req)  throws BaseException{
       // try{
            if(req.getTradeTitle() == null || req.getTradeTitle() == ""){
                throw new BaseException(REQUEST_ERROR);
            }else if(req.getContent() == null || req.getContent() == ""){
                throw new BaseException(REQUEST_ERROR);
            }else if(req.getPrice() == 0){
                throw new BaseException(REQUEST_ERROR);
            }else if (req.getItemCategory() == null || req.getItemCategory() == ""){
                throw new BaseException(REQUEST_ERROR);
            }else if (req.getImgUrl() == null){
                throw new BaseException(REQUEST_ERROR);
            } //필수값 체크 (거래게시글제목,내용,카테고리,사진)
            int boardIdx = tradeDao.tradeWrite(userIdx,req);
            if(req.getImgUrl() != null){
              for(int i = 0; i < req.getImgUrl().size(); i++){
                int isSuccess = tradeDao.tradeWrite2(boardIdx, req.getImgUrl().get(i));
              }
            }
            return boardIdx;
       // } catch (Exception exception){
       //     throw new BaseException(RESPONSE_ERROR);
       // }
    }

    public int tradeModify(int userIdx, tradeWriteReq req) throws BaseException{
        try{
            if(req.getTradeTitle() == null || req.getTradeTitle() == ""){
                throw new BaseException(REQUEST_ERROR);
            }else if(req.getContent() == null || req.getContent() == ""){
                throw new BaseException(REQUEST_ERROR);
            }else if(req.getPrice() == 0){
                throw new BaseException(REQUEST_ERROR);
            }else if (req.getItemCategory() == null || req.getItemCategory() == ""){
                throw new BaseException(REQUEST_ERROR);
            }else if (req.getImgUrl() == null){
                throw new BaseException(REQUEST_ERROR);
            }else if (req.getBoardIdx() == 0){
                throw new BaseException(REQUEST_ERROR);
            }//필수값 체크 (거래게시글제목,내용,카테고리,사진)
            int result = tradeDao.tradeModify(userIdx,req);

            return result;
        } catch (Exception exception){
            throw new BaseException(RESPONSE_ERROR);
        }
    }


    public int chatWrite(int userIdx, chatWriteReq req) throws BaseException{
        try{
            if(req.getRoomIdx() == 0) { //roomIdx(채팅룸)처음 생성시
                int result = tradeDao.chatWrite(userIdx, req);
                return result;
            } else { //roomIdx(채팅룸) 생성되어있을때
                chatWriteUserRes res = tradeDao.chatWrite2(req);
                int result = tradeDao.chatWrite3(userIdx, req, res);
                return result;
            }
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public String insertInterest(int userIdx, int boardIdx) throws BaseException{
        try{
             String result = tradeDao.insertInterest(userIdx, boardIdx);
             return result;
        }catch (Exception exception){
           throw new BaseException(DATABASE_ERROR);
        }
    }


}
