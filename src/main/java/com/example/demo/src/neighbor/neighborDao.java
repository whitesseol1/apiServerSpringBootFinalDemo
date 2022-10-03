package com.example.demo.src.neighbor;

import com.example.demo.src.neighbor.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import javax.transaction.Transactional;
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
                "         THEN (select count(*) from `empathy` as A where A.userIdx = ? and A.boardIdx = ? and A.status = '공감함' ) END as empathyStatus," +
                "        t1.boardIdx as boardIdx " +
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
                        rs.getInt("empathyStatus"),
                        rs.getInt("boardIdx")),
                getBoardParams);
    }

    public List<neighborBoardDetailCommentRes> neighborBoardComment(int boardIdx, int userIdx) {
        String getCommentQuery = " SELECT member.userId, member.profileImg,t2.address1 as address , t1.comment, " +
                "(select count(*) from `commentLike` as C where userIdx = ? and C.commentIdx = t1.commentIdx) as myLikeStatus,"+
                "         CASE" +
                "          WHEN TIMEDIFF(NOW(), t1.updatedAt) < '01:00:00' THEN concat(MINUTE(TIMEDIFF(NOW(),  t1.updatedAt)), '분전')" +
                "          WHEN TIMEDIFF(NOW(),  t1.updatedAt) < '24:00:00' THEN concat(HOUR(TIMEDIFF(NOW(),  t1.updatedAt)), '시간전')" +
                "          ELSE concat(DATEDIFF(NOW(),  t1.updatedAt), '일전') END as writeTime," +
                "          t1.commentIdx as commentIdx, t1.imgUrl as imgUrl " +
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
                        rs.getString("writeTime"),
                        rs.getInt("commentIdx"),
                        rs.getString("imgUrl")),
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
                "        (select count(*) from neighborBoardComment where t1.boardIdx = neighborBoardComment.boardIdx) as commentCount," +
                "       t1.boardIdx as boardIdx " +
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
                        rs.getInt("commentCount"),
                        rs.getInt("boardIdx")),
                getNeighborBoardListParams );
    }

  public int neighborboardWrite(neighborBoardInsertReq req, int userIdx){
        String neighborboardWriteQuery = "INSERT INTO `neighborBoard` (userIdx, category, content, status) "+
                "value (?, ?, ?, '생성됨')";
        Object[] neighborboardWriteParams = new Object[] {userIdx,req.getCategory(),req.getContent()};
        this.jdbcTemplate.update(neighborboardWriteQuery,neighborboardWriteParams);

      return this.jdbcTemplate.queryForObject("select last_insert_id()",int.class);
  }

  public int neighborboardWrite2(int boardIdx, String imgUrl){
      String neighborboardWrite2Query = "INSERT INTO `neighborBoardImg` (boardIdx, img, status)" +
              "value (?, ?,'생성됨' )";
      Object[] neighborboardWrite2Params = new Object[]{boardIdx, imgUrl};
      return this.jdbcTemplate.update(neighborboardWrite2Query, neighborboardWrite2Params);
  }

  @Transactional
  public int neighborboardCommentWrite(neighborBoardCommentInsertReq req, int userIdx){
        String neighborboardCommentWriteQuery = "INSERT INTO `neighborBoardComment` (boardIdx, userIdx, comment, commentDepth, imgUrl, status)" +
                "VALUE (?, ?, ?, 1, ?, '생성됨') ";
        Object[] neighborboardCommentWriteParams = new Object[]{req.getBoardIdx(), userIdx, req.getComment(), req.getImgUrl()};
        this.jdbcTemplate.update(neighborboardCommentWriteQuery, neighborboardCommentWriteParams);

      int commentIdx = this.jdbcTemplate.queryForObject("select last_insert_id()",int.class);

      String updateCommentIdxQuery = "UPDATE `neighborBoardComment` SET commentGroup = ? WHERE commentIdx = ?";
      this.jdbcTemplate.update(updateCommentIdxQuery,commentIdx,commentIdx);

      return commentIdx;
  }

  public int neighborboardCommentWrite2(neighborBoardCommentInsertReq req, int userIdx){
        String neighborboardCommentWrite2Query = "INSERT INTO `neighborBoardComment` (boardIdx, userIdx, comment, commentDepth, commentGroup, imgUrl, status)" +
                "VALUE (?, ?, ?, 2, ?, ?, '생성됨') ";
        Object[] neighborboardCommentWrite2Params = new Object[]{req.getBoardIdx(), userIdx, req.getComment(),req.getCommentGroup(),req.getImgUrl()};
      this.jdbcTemplate.update(neighborboardCommentWrite2Query, neighborboardCommentWrite2Params);
      return this.jdbcTemplate.queryForObject("select last_insert_id()",int.class);
  }

  @Transactional
  public String insertEmpathy(int userIdx, int boardIdx){
     this.jdbcTemplate.update("UPDATE `neighborBoard` SET `wonder` = 0 WHERE `wonder` is null");
     this.jdbcTemplate.update("UPDATE `neighborBoard` SET `empathy` = 0 WHERE `empathy` is null");

    String selectEmpathyQuery = "SELECT status, COUNT(*) as count FROM `empathy` where userIdx = ? and boardIdx = ?";
    Object[] selectEmpathyParam = new Object[]{userIdx, boardIdx};
    empathySelectRes res = this.jdbcTemplate.queryForObject(selectEmpathyQuery,
            (rs,rowNum)-> new empathySelectRes(
                    rs.getString("status"),
                    rs.getInt("count")
            ),selectEmpathyParam );

    String resultMessage = "";
    int result1 = 0; int result2 = 0;

    if(res.getCount() <1 ){
        String selectBoardStatusQuery = "SELECT category FROM `neighborBoard` where boardIdx = ? ";
        int category = this.jdbcTemplate.queryForObject(selectBoardStatusQuery, int.class, boardIdx);
        if(category == 2) {
            String insertEmpathyQuery = "UPDATE `neighborBoard` SET `updatedAt` = current_timestamp, wonder = wonder+1 WHERE boardIdx = ?";
            result1 = this.jdbcTemplate.update(insertEmpathyQuery, boardIdx);

            String insertEmpathyQuery2 = "INSERT INTO `empathy` (userIdx, status, boardIdx) VALUE (?, '궁금함', ?)";
            Object[] insertEmpathyParams2 = new Object[]{userIdx, boardIdx};
            result2 = this.jdbcTemplate.update(insertEmpathyQuery2,insertEmpathyParams2 );
            if(result1 >0 && result2 >0){
                resultMessage = "궁금해요 등록되었습니다.";
            }
        }else{
            String insertEmpathyQuery = "UPDATE `neighborBoard` SET `updatedAt` = current_timestamp, empathy = empathy+1 WHERE boardIdx = ?";
            result1 = this.jdbcTemplate.update(insertEmpathyQuery, boardIdx);

            String insertEmpathyQuery2 = "INSERT INTO `empathy` (userIdx, status, boardIdx) VALUE (?, '공감함', ?)";
            Object[] insertEmpathyParams2 = new Object[]{userIdx, boardIdx};
            result2 = this.jdbcTemplate.update(insertEmpathyQuery2,insertEmpathyParams2 );
            if(result1 >0 && result2 >0){
                resultMessage = "공감해요 등록되었습니다.";
            }
        }
    }else if(res.getCount() >0 && res.getStatus().equals("궁금함")){
        String empathyRollBackNeighborBoardQuery = "UPDATE `neighborBoard` SET `updatedAt` = current_timestamp, wonder = wonder-1 WHERE boardIdx = ?";
        result1 = this.jdbcTemplate.update(empathyRollBackNeighborBoardQuery, boardIdx);

        String insertEmpathyQuery = "UPDATE `empathy` SET `updatedAt` = current_timestamp, status = '궁금함취소' WHERE boardIdx = ? and userIdx = ?";
        Object[] insertEmpathyParams = new Object[]{boardIdx, userIdx};
        result2 = this.jdbcTemplate.update(insertEmpathyQuery, insertEmpathyParams);
        if(result1 >0 && result2 >0){
            resultMessage = "궁금해요 취소되었습니다.";
        }
    }else if(res.getCount() >0 && res.getStatus().equals("공감함")){
        String empathyRollBackNeighborBoardQuery = "UPDATE `neighborBoard` SET `updatedAt` = current_timestamp, empathy = empathy-1 WHERE boardIdx = ?";
        result1 = this.jdbcTemplate.update(empathyRollBackNeighborBoardQuery, boardIdx);

        String insertEmpathyQuery = "UPDATE `empathy` SET `updatedAt` = current_timestamp, status = '공감함취소' WHERE boardIdx = ? and userIdx = ?";
        Object[] insertEmpathyParams = new Object[]{boardIdx, userIdx};
        result2 = this.jdbcTemplate.update(insertEmpathyQuery, insertEmpathyParams);
        if(result1 >0 && result2 >0){
            resultMessage = "공감해요 취소되었습니다.";
        }
    }else if(res.getCount() > 0 && res.getStatus().equals("궁금함취소")){
        String updateEmpathyQuery = "UPDATE `neighborBoard` SET `updatedAt` = current_timestamp, wonder = wonder+1 WHERE boardIdx = ?";
        result1 = this.jdbcTemplate.update(updateEmpathyQuery, boardIdx);

        String updateEmpathyQuery2 = "UPDATE `empathy` SET `updatedAt` = current_timestamp, status = '궁금함' WHERE boardIdx = ? and userIdx = ?";
        Object[] updateEmpathyParams2 = new Object[]{boardIdx, userIdx};
        result2 = this.jdbcTemplate.update(updateEmpathyQuery2, updateEmpathyParams2);
        if(result1 >0 && result2 >0){
            resultMessage = "궁금해요 등록되었습니다.";
        }
    }else if(res.getCount() > 0 && res.getStatus().equals("공감함취소")) {
        String updateEmpathyQuery = "UPDATE `neighborBoard` SET `updatedAt` = current_timestamp, empathy = empathy+1 WHERE boardIdx = ?";
        result1 = this.jdbcTemplate.update(updateEmpathyQuery, boardIdx);

        String updateEmpathyQuery2 = "UPDATE `empathy` SET `updatedAt` = current_timestamp, status = '공감함' WHERE boardIdx = ? and userIdx = ?";
        Object[] updateEmpathyParams2 = new Object[]{boardIdx, userIdx};
        result2 = this.jdbcTemplate.update(updateEmpathyQuery2, updateEmpathyParams2);
        if (result1 > 0 && result2 > 0) {
            resultMessage = "공감해요 등록되었습니다.";
        }
    }
    return resultMessage;
  }

}
