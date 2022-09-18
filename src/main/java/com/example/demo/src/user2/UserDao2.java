package com.example.demo.src.user2;


import com.example.demo.src.user2.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class UserDao2 {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int checkEmail(String email){

        String checkEmailQuery = "select exists(select email from Member where email = ?)";

            return this.jdbcTemplate.queryForObject(checkEmailQuery,
                    int.class,
                    email);


    }

    public int checkId(String id){
        String checkIdQuery = "select exists(select userId from Member where userId = ?)";
        String checkIdParams = id;
        return this.jdbcTemplate.queryForObject(checkIdQuery,
                int.class,
                checkIdParams);

    }

    public int checkKakaoEmail(String kakaoEmail){
        String checkKakaoEmailQuery = "select exists(select userId from Member where kakaoEmail = ?)";
        String checkKakaoEmailParams = kakaoEmail;
        return this.jdbcTemplate.queryForObject(checkKakaoEmailQuery,
                int.class,
                checkKakaoEmailParams);
    }

    public int selectKakaoUser(String kakaoEmail){
        String selectKakaoUserQuery = "select userIdx from Member where kakaoEmail = ?";
        String selectKakaoUserParams = kakaoEmail;
        return this.jdbcTemplate.queryForObject(selectKakaoUserQuery,
                int.class,
                selectKakaoUserParams);
    }

    public int createUser(PostUserReq postUserReq){
        String createUserQuery = "insert into Member (userName, userId, passWord, email, phone, profileImg, status) VALUES (?,?,?,?,?,?, 1)";
        Object[] createUserParams = new Object[]{postUserReq.getUserName(), postUserReq.getId(), postUserReq.getPassword(), postUserReq.getEmail(), postUserReq.getPhone(), postUserReq.getUserImg()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);
        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }

    public int insertKakaoUser(kakaoLoginRes res){
        String insertKakaoUserQuery = "insert into Member (email, kakaoNickName, kakaoEmail, kakaoProfileImg, status) VALUES (?,?,?,?,1)";
        Object[] insertKakaoUserParams =  new Object[]{res.getEmail(), res.getNickName(), res.getEmail(), res.getProfileImage()};
        this.jdbcTemplate.update(insertKakaoUserQuery,insertKakaoUserParams);
        String lastInsertUserQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertUserQuery,int.class);
    }

    public int insertAddress(int userIdx, String address){
        String createAddressQuery = "insert into memberAddress (userIdx, address1, status) VALUES (?,?, 1)";
        Object[] createAddressParams = new Object[]{userIdx, address};
        int result = this.jdbcTemplate.update(createAddressQuery, createAddressParams);
        return result;
    }


    public User getPwd(PostLoginReq postLoginReq){
        String getPwdQuery = "select userIdx, passWord,email,userName,userId,status from Member where userId = ?";
        String getPwdParams = postLoginReq.getId();

        return this.jdbcTemplate.queryForObject(getPwdQuery,
                (rs,rowNum)-> new User(
                        rs.getInt("userIdx"),
                        rs.getString("userId"),
                        rs.getString("userName"),
                        rs.getString("passWord"),
                        rs.getString("email"),
                        rs.getString("status")
                ),
                getPwdParams
        );

    }


    public myAccountBookRes myAccountBook(int userIdx, String yearMonth){
        String getMyAccountBookQuery = "select Member.userId, Member.profileImg, count(*) as tradeCount, FORMAT(sum(tradeBoard.price), 0) as price" +
                "  from Member" +
                "        join tradeBoard on Member.userIdx = tradeBoard.userIdx" +
                "        where tradeBoard.userIdx = ? and tradeBoard.tradeStatus = '거래완료' and" +
                "              DATE(tradeBoard.tradeFinishTime) >= DATE(?) and DATE(tradeBoard.tradeFinishTime) < Date_add( DATE(?) ,interval 1 month)";
        Object[] getMyAccountBookParams = new Object[]{userIdx, yearMonth, yearMonth};
        return this.jdbcTemplate.queryForObject(getMyAccountBookQuery,
                (rs,rowNum)-> new myAccountBookRes(
                        rs.getString("userId"),
                        rs.getString("profileImg"),
                        rs.getInt("tradeCount"),
                        rs.getString("price")),
                getMyAccountBookParams);


    }


}
