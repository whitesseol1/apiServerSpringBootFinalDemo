package com.example.demo.src.neighbor;

import com.example.demo.src.neighbor.model.neighborBoardDetailCommentRes;
import com.example.demo.src.neighbor.model.neighborBoardDetailRes;
import com.example.demo.src.neighbor.model.neighborBoardInsertReq;
import com.example.demo.src.neighbor.model.neighborBoardListRes;
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

    public neighborBoardDetailRes neighborBoardDetail(int boardIdx, int userIdx) {
        String getBoardQuery = "SELECT (select categoryName from `neighborLifeCategory` where categoryNo = t1.category ) as category, t1.content, t2.userId," +
                "       t2.addressConfirm, t3.address1 as address, t2.profileImg," +
                "       CASE" +
                "          WHEN TIMEDIFF(NOW(),  t1.refreshStatus) < '01:00:00' THEN concat(MINUTE(TIMEDIFF(NOW(),   t1.refreshStatus)), '분전')" +
                "          WHEN TIMEDIFF(NOW(),   t1.refreshStatus) < '24:00:00' THEN concat(HOUR(TIMEDIFF(NOW(),   t1.refreshStatus)), '시간전')" +
                "          ELSE concat(DATEDIFF(NOW(),   t1.refreshStatus), '일전') END as" +
                "           refreshStatus," +
                "        CASE" +
                "      WHEN TIMEDIFF(NOW(), t1.createdAt) < '01:00:00' THEN concat(MINUTE(TIMEDIFF(NOW(), t1.createdAt)), '분전')" +
                "      WHEN TIMEDIFF(NOW(), t1.createdAt) < '24:00:00' THEN concat(HOUR(TIMEDIFF(NOW(), t1.createdAt)), '시간전')" +
                "      ELSE concat(DATEDIFF(NOW(), t1.createdAt), '일전') END as createdTime," +
                "        CASE" +
                "         WHEN (select categoryName from `neighborLifeCategory` where categoryNo = t1.category ) = '동네질문' " +
                "         THEN (select count(*) from `empathy` as A where A.userIdx = ? and A.boardIdx = ? and A.status = '궁금함' ) END as wonderStatus," +
                "        CASE "+
                "         WHEN (select categoryName from `neighborLifeCategory` where categoryNo = t1.category  ) <> '동네질문' " +
                "         THEN (select count(*) from `empathy` as A where A.userIdx = ? and A.boardIdx = ? and A.status = '공감함' ) END as empathyStatus" +
                "       FROM" +
                "     `neighborBoard` as `t1` left outer join Member as `t2` on t1.userIdx = t2.userIdx" +
                "     left outer join `memberAddress` as `t3` on t2.userIdx = t3.userIdx" +
                "      where boardIdx = ?";
        Object[] getBoardParams = new Object []{userIdx,boardIdx,userIdx,boardIdx,boardIdx};
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
                        rs.getInt("wonderStatus"),
                        rs.getInt("empathyStatus")),
                getBoardParams);
    }

    public List<neighborBoardDetailCommentRes> neighborBoardComment(int boardIdx, int userIdx) {
        String getCommentQuery = " SELECT member.userId, member.profileImg,t2.address1 as address , t1.comment, " +
                "(select count(*) from `commentLike` as C where userIdx = ? and C.commentIdx = t1.commentIdx) as myLikeStatus,"+
                "         CASE" +
                "          WHEN TIMEDIFF(NOW(), t1.updatedAt) < '01:00:00' THEN concat(MINUTE(TIMEDIFF(NOW(),  t1.updatedAt)), '분전')" +
                "          WHEN TIMEDIFF(NOW(),  t1.updatedAt) < '24:00:00' THEN concat(HOUR(TIMEDIFF(NOW(),  t1.updatedAt)), '시간전')" +
                "          ELSE concat(DATEDIFF(NOW(),  t1.updatedAt), '일전') END as writeTime" +
                "         FROM neighborBoardComment as t1" +
                "         left join Member as member on t1.userIdx = member.userIdx" +
                "         left join memberAddress as t2 on t1.userIdx = t2.userIdx" +
                "         where boardIdx = ?" +
                "         order by commentIdx, commentGroup, commentDepth ";
        Object[] getBoardParams = new Object []{userIdx,boardIdx};
        return this.jdbcTemplate.query(getCommentQuery,
                (rs,rowNum) -> new neighborBoardDetailCommentRes(
                        rs.getString("userId"),
                        rs.getString("profileImg"),
                        rs.getString("address"),
                        rs.getString("comment"),
                        rs.getInt("myLikeStatus"),
                        rs.getString("writeTime")),
                getBoardParams);

    }

    public List<neighborBoardListRes> neighborBoardList(int userIdx){
        String getNeighborBoardListQuery = "select (select categoryName from `neighborLifeCategory` where categoryNo = t1.category ) as category," +
                "       t1.content, Member.userId, memberAddress.address1 as address," +
                "      CASE" +
                "          WHEN TIMEDIFF(NOW(),  t1.updatedAt) < '01:00:00' THEN concat(MINUTE(TIMEDIFF(NOW(),   t1.updatedAt)), '분전')" +
                "          WHEN TIMEDIFF(NOW(),   t1.updatedAt) < '24:00:00' THEN concat(HOUR(TIMEDIFF(NOW(),   t1.updatedAt)), '시간전')" +
                "          ELSE concat(DATEDIFF(NOW(),   t1.updatedAt), '일전') END as" +
                "           writeTime," +
                "        CASE" +
                "         WHEN (select categoryName from `neighborLifeCategory` where categoryNo = t1.category ) = '동네질문' " +
                "         THEN (select count(*) from `empathy` as A where A.userIdx = ? and A.boardIdx = t1.boardIdx and A.status = '궁금함' ) END as wonderStatus," +
                "        CASE "+
                "         WHEN (select categoryName from `neighborLifeCategory` where categoryNo = t1.category  ) <> '동네질문' " +
                "         THEN (select count(*) from `empathy` as A where A.userIdx = ? and A.boardIdx = t1.boardIdx and A.status = '공감함' ) END as empathyStatus," +
                "        (select count(*) from neighborBoardComment where t1.boardIdx = neighborBoardComment.boardIdx) as commentCount" +
                "     from neighborBoard as t1" +
                "     join Member on t1.userIdx = Member.userIdx" +
                "     join memberAddress on t1.userIdx = memberAddress.userIdx" +
                "     where INSTR( memberAddress.address1, SUBSTRING_INDEX((select address1 from memberAddress where userIdx = ?), '동', 1)) >0" +
                "       or  INSTR( memberAddress.address1, SUBSTRING_INDEX((select address1 from memberAddress where userIdx = ?), '동', 2)) >0" +
                "       or  INSTR( memberAddress.address1, SUBSTRING_INDEX((select address1 from memberAddress where userIdx = ?), '구', 1)) >0";

        Object[] getNeighborBoardListParams = new Object []{userIdx,userIdx,userIdx,userIdx,userIdx};
        return this.jdbcTemplate.query(getNeighborBoardListQuery,
                (rs,rowNum) -> new neighborBoardListRes(
                        rs.getString("category"),
                        rs.getString("content"),
                        rs.getString("userId"),
                        rs.getString("address"),
                        rs.getString("writeTime"),
                        rs.getInt("wonderStatus"),
                        rs.getInt("empathyStatus"),
                        rs.getInt("commentCount")),
                getNeighborBoardListParams );
    }

  public int neighborboardWrite(neighborBoardInsertReq req, int userIdx){
        String neighborboardWriteQuery = "INSERT INTO `neighborBoard` (userIdx, category, content, status)"+
                "value (?,?,?,'생성됨')";
        Object neighborboardWriteParams = new Object[]{userIdx,req.getCategory(),req.getContent()};
        this.jdbcTemplate.update(neighborboardWriteQuery,neighborboardWriteParams);

      return this.jdbcTemplate.queryForObject("select last_insert_id()",int.class);
  }

  public int neighborboardWrite2(int boardIdx, String imgUrl){
      String neighborboardWrite2Query = "INSERT INTO `neighborBoardImg` (boardIdx, img, status)" +
              "value (?, ?,'생성됨' )";
      Object[] neighborboardWrite2Params = new Object[]{boardIdx, imgUrl};
      return this.jdbcTemplate.update(neighborboardWrite2Query, neighborboardWrite2Params);
  }

}
