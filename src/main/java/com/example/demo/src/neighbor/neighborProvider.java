package com.example.demo.src.neighbor;

import com.example.demo.config.BaseException;
import com.example.demo.src.neighbor.model.neighborBoardDetailCommentRes;
import com.example.demo.src.neighbor.model.neighborBoardDetailRes;
import com.example.demo.src.neighbor.model.neighborBoardDetailRes2;
import com.example.demo.src.neighbor.model.neighborBoardListRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

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

    public neighborBoardDetailRes2 neighborBoardDetail(int boardIdx, int userIdx) throws BaseException {

       try{
            neighborBoardDetailRes res = neighborDao.neighborBoardDetail(boardIdx,userIdx);
            List<String> list = neighborDao.neighborBoardDetail2(boardIdx);
            //List<String> list = new ArrayList<String>();
            //list.add("1");

            neighborBoardDetailRes2 res2 = new neighborBoardDetailRes2(res,list);
            return res2;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }


    public List<neighborBoardDetailCommentRes> neighborBoardComment(int boardIdx, int userIdx) throws BaseException {
        try{
            List<neighborBoardDetailCommentRes> res = neighborDao.neighborBoardComment(boardIdx,userIdx);
            return res;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<neighborBoardListRes> neighborBoardList(int userIdx) throws BaseException {
        try{
            List<neighborBoardListRes> res = neighborDao. neighborBoardList(userIdx);
            return res;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

}
