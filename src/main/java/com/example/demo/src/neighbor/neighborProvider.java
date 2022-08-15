package com.example.demo.src.neighbor;

import com.example.demo.config.BaseException;
import com.example.demo.src.neighbor.model.neighborBoardDetailCommentRes;
import com.example.demo.src.neighbor.model.neighborBoardDetailRes;
import com.example.demo.src.trade.tradeDao;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class neighborProvider {

  private final neighborDao neighborDao;

  private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public neighborProvider(neighborDao neighborDao, JwtService jwtService) {
        this.neighborDao = neighborDao;
        this.jwtService = jwtService;
    }

    public neighborBoardDetailRes neighborBoardDetail(int boardIdx) throws BaseException {

       try{
            neighborBoardDetailRes res = neighborDao.neighborBoardDetail(boardIdx);
            return res;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }


    public List<neighborBoardDetailCommentRes> neighborBoardComment(int boardIdx) throws BaseException {
        try{
            List<neighborBoardDetailCommentRes> res = neighborDao.neighborBoardComment(boardIdx);
            return res;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
