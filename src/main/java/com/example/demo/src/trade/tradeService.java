package com.example.demo.src.trade;

import com.example.demo.config.BaseException;
import com.example.demo.src.trade.model.tradeWriteReq;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.RESPONSE_ERROR;
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
            }
            int boardIdx = tradeDao.tradeWrite(userIdx,req);
            for(int i = 0; i < req.getImgUrl().size(); i++){
                int isSuccess = tradeDao.tradeWrite2(boardIdx, req.getImgUrl().get(i));
            }
            return boardIdx;
        } catch (Exception exception){
            throw new BaseException(RESPONSE_ERROR);
        }
    }


}
