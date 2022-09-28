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

    public int insertKeywordAlarm(String keyword, int userIdx){
        String selectKeywordAlarmQuery = "select count(*) from `keywordAlarm` where userIdx = ? and keyword = ?";
        Object[] selectKeywordAlarmParams = new Object[]{userIdx, keyword};
        int result = this.jdbcTemplate.queryForObject(selectKeywordAlarmQuery,int.class,selectKeywordAlarmParams);

        if (result <1 ) {
            String insertKeywordAlarmQuery = "insert into `keywordAlarm` (userIdx, keyword, status) values (?, ?, '생성됨')";
            Object[] insertKeywordAlarmParams = new Object[]{userIdx, keyword};
            return this.jdbcTemplate.update(insertKeywordAlarmQuery, insertKeywordAlarmParams);
        }else {
            return 0;
        }

    }

    public String neighborSet(String neighbor, int userIdx){
        String selectNeighborQuery = "select address1, address2, count(*) as count from `memberAddress` where userIdx = ?";
        userAddressRes res = this.jdbcTemplate.queryForObject(selectNeighborQuery,
                (rs,rowNum)-> new userAddressRes(
                        rs.getString("address1"),
                        rs.getString("address2"),
                        rs.getInt("count")), userIdx);

        String result = "";
        int result2 = 0;
        System.out.println("확인 두번째" + (res.getAddress1() != null ));
        if ((res.getAddress1() == null || res.getAddress1().equals(""))&& res.getCount() <1){
            String neighborSetQuery = "insert into `memberAddress` (address2, userIdx, status) value (?, ?, '생성됨')";
            Object[] selectKeywordAlarmParams = new Object[]{neighbor, userIdx};
             result2 = this.jdbcTemplate.update(neighborSetQuery,int.class,selectKeywordAlarmParams);
            if(result2 >0){
                result = "나의 동네로 설정되었습니다.";
            }else{
                result = "설정에 실패하였습니다.";
            }
            System.out.println("확인");
        }else if (res.getAddress1() != null && res.getAddress2() == null && !res.getAddress1().equals(neighbor) && res.getCount() >0){
            String neighborSetQuery = "update `memberAddress` set address2 = ? where userIdx = ?";
            String address2 = neighbor;
            Object[] neighborSetParams = new Object[]{address2, userIdx};
            result2 = this.jdbcTemplate.update(neighborSetQuery,int.class,neighborSetParams);
            if(result2 >0){
                result = "나의 동네로 설정되었습니다.";
            }else{
                result = "설정에 실패하였습니다.";
            }
            System.out.println("확인2"+!res.getAddress1().equals(neighbor));
        }else if (res.getAddress1() != null && res.getAddress2() != null && res.getCount() >1){
            result = "이미 나의 동네(최대 2개까지)가 설정 되어 있습니다.";
        }else if (res.getAddress1().equals(neighbor)){
            result = "이미 등록된 동네입니다.";
        }

        return result;
    }


}
