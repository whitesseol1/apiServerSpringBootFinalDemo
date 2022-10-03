package com.example.demo.src.neighbor;

import com.example.demo.config.BaseException;
import com.example.demo.src.neighbor.model.neighborBoardInsertReq;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.example.demo.config.BaseResponseStatus.REQUEST_ERROR;
import static com.example.demo.config.BaseResponseStatus.RESPONSE_ERROR;

@Service
public class neighborService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final neighborDao neighborDao;
    private final neighborProvider neighborProvider;
    private final JwtService jwtService;

    @Autowired
    public neighborService(neighborDao neighborDao, neighborProvider neighborProvider,  JwtService jwtService) {
        this.neighborProvider = neighborProvider;
        this.neighborDao = neighborDao;
        this.jwtService = jwtService;

    }

    @Transactional
    public int neighborboardWrite(neighborBoardInsertReq req, int userIdx) throws BaseException{
        try{
            if(req.getContent() == null || req.getContent() == ""){
                throw new BaseException(REQUEST_ERROR);
            }//필수값체크
            int boardIdx = neighborDao.neighborboardWrite(req,userIdx);
            for(int i = 0; i<req.getBoardImg().size(); i++){
                int isSuccess = neighborDao.neighborboardWrite2(boardIdx, req.getBoardImg().get(i));
            }
            return boardIdx;
        }catch (Exception exception){
            throw new BaseException(RESPONSE_ERROR);
        }
    }
}
