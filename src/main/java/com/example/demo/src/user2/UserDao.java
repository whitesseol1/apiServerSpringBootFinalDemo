package com.example.demo.src.user2;


import com.example.demo.src.user2.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int checkEmail(String email){
        String checkEmailQuery = "select exists(select email from Member where email = ?)";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);

    }

    public int checkEmail(String id){
        String checkIdQuery = "select exists(select id from Member where id = ?)";
        String checkIdParams = id;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkIdParams);

    }

    public int createUser(PostUserReq postUserReq){
        String createUserQuery = "insert into Member (userName, userId, passWord, email) VALUES (?,?,?,?)";
        Object[] createUserParams = new Object[]{postUserReq.getUserName(), postUserReq.getId(), postUserReq.getPassword(), postUserReq.getEmail()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }


    public User getPwd(PostLoginReq postLoginReq){
        String getPwdQuery = "select userIdx, passWord,email,userName,userId from Member where userId = ?";
        String getPwdParams = postLoginReq.getId();

        return this.jdbcTemplate.queryForObject(getPwdQuery,
                (rs,rowNum)-> new User(
                        rs.getInt("userIdx"),
                        rs.getString("userId"),
                        rs.getString("userName"),
                        rs.getString("passWord"),
                        rs.getString("email")
                ),
                getPwdParams
        );

    }




}
