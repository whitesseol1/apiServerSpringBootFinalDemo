package com.example.demo.src.neighbor;

import com.example.demo.src.neighbor.model.neighborBoardDetailCommentRes;
import com.example.demo.src.neighbor.model.neighborBoardDetailRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class neighborDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public neighborBoardDetailRes neighborBoardDetail(int boardIdx) {
        String getBoardQuery = "SELECT (select categoryName from categoryDefinition where categoryIdx = t1.category ) as category, t1.content, t2.userId," +
                "       t2.addressConfirm, t3.address1 as address, t2.profileImg," +
                "       CASE" +
                "          WHEN TIMEDIFF(NOW(),  t1.refreshStatus) < '01:00:00' THEN concat(MINUTE(TIMEDIFF(NOW(),   t1.refreshStatus)), '분전')" +
                "          WHEN TIMEDIFF(NOW(),   t1.refreshStatus) < '24:00:00' THEN concat(HOUR(TIMEDIFF(NOW(),   t1.refreshStatus)), '시간전')" +
                "          ELSE concat(DATEDIFF(NOW(),   t1.refreshStatus), '일전') END as" +
                "           refreshStatus," +
                "        CASE" +
                "      WHEN TIMEDIFF(NOW(), t1.createdAt) < '01:00:00' THEN concat(MINUTE(TIMEDIFF(NOW(), t1.createdAt)), '분전')" +
                "      WHEN TIMEDIFF(NOW(), t1.createdAt) < '24:00:00' THEN concat(HOUR(TIMEDIFF(NOW(), t1.createdAt)), '시간전')" +
                "      ELSE concat(DATEDIFF(NOW(), t1.createdAt), '일전') END as createdTime, t1.wonder as 'wonder'" +
                "       FROM" +
                "     `neighborBoard` as `t1` left outer join Member as `t2` on t1.userIdx = t2.userIdx" +
                "     left outer join `memberAddress` as `t3` on t2.userIdx = t3.userIdx" +
                "      where boardIdx = ?";
        int getBoardParams = boardIdx;
        return this.jdbcTemplate.queryForObject(getBoardQuery,
                (rs, rowNum) -> new neighborBoardDetailRes(
                        rs.getString("category"),
                        rs.getString("content"),
                        rs.getString("userId"),
                        rs.getString("addressConfirm"),
                        rs.getString("address"),
                        rs.getString("profileImg"),
                        rs.getString("createdTime"),
                        rs.getString("refreshStatus"),
                        rs.getInt("wonder")),
                getBoardParams);
    }

    public List<neighborBoardDetailCommentRes> neighborBoardComment(int boardIdx) {
        String getCommentQuery = " SELECT member.userId, member.profileImg,t2.address1 as address , t1.comment, t1.`like`," +
                "         CASE" +
                "          WHEN TIMEDIFF(NOW(), t1.updatedAt) < '01:00:00' THEN concat(MINUTE(TIMEDIFF(NOW(),  t1.updatedAt)), '분전')" +
                "          WHEN TIMEDIFF(NOW(),  t1.updatedAt) < '24:00:00' THEN concat(HOUR(TIMEDIFF(NOW(),  t1.updatedAt)), '시간전')" +
                "          ELSE concat(DATEDIFF(NOW(),  t1.updatedAt), '일전') END as writeTime" +
                "         FROM neighborBoardComment as t1" +
                "         left join Member as member on t1.userIdx = member.userIdx" +
                "         left join memberAddress as t2 on t1.userIdx = t2.userIdx" +
                "         where boardIdx = ?" +
                "         order by commentIdx, commentGroup, commentDepth ";
        int getBoardParams = boardIdx;
        return this.jdbcTemplate.query(getCommentQuery,
                (rs,rowNum) -> new neighborBoardDetailCommentRes(
                        rs.getString("userId"),
                        rs.getString("profileImg"),
                        rs.getString("address"),
                        rs.getString("comment"),
                        rs.getInt("like"),
                        rs.getString("writeTime")),
                getBoardParams);

    }

}
