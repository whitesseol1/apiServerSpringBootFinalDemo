package com.example.demo.src.trade;

import com.example.demo.src.trade.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class tradeDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<myChatRes> myChatList(int userIdx) {
        String getMyChatListQuery =
                "select distinct `t3`.address1, t4.profileImg, t4.userId, t5.imgUrl, lastChatMessage," +
                        "    CASE" +
                        "    WHEN TIMEDIFF(NOW(), lastChatMessageTimestamp) <'01:00:00' THEN concat(MINUTE(TIMEDIFF(NOW(), lastChatMessageTimestamp)), '분전')" +
                        "    WHEN TIMEDIFF(NOW(), lastChatMessageTimestamp) <'24:00:00' THEN concat(HOUR(TIMEDIFF(NOW(), lastChatMessageTimestamp)), '시간전')" +
                        "    ELSE concat(DATEDIFF(NOW(), lastChatMessageTimestamp), '일전') END as lastChatMessageTimestamp2" +
                        "      from  chatContent" +
                        "       as `t1` join `chatRoom` as `t2` on t1.roomIdx = t2.roomIdx join `tradeImg` as `t5`" +
                        "         on t2.boardIdx = t5.boardIdx join `memberAddress` as `t3` on t1.userIdx2 = t3.userIdx join `Member` as `t4`" +
                        "         on t1.userIdx2 = t4.userIdx" +
                        "         join" +
                        "         (select `chatContent`.roomIdx," +
                        "                    userIdx as lastUserIdx," +
                        "                    content as lastChatMessage," +
                        "                    createdAt as lastChatMessageTimestamp" +
                        "                   from chatContent" +
                        "                   inner join (select roomIdx, max(chatIdx) as currentMessageNo" +
                        "                        from chatContent" +
                        "                         group by roomIdx) CurrentMessage" + "                  where currentMessageNo = chatIdx) lastChatMessage on lastChatMessage.roomIdx = `t1`.roomIdx" +
                        "       where t1.userIdx = ? group by `t1`.roomIdx";

        int getMyChatListParams = userIdx;
        return this.jdbcTemplate.query(getMyChatListQuery,
                (rs, rowNum) -> new myChatRes(
                        rs.getString("address1"),
                        rs.getString("profileImg"),
                        rs.getString("userId"),
                        rs.getString("imgUrl"),
                        rs.getString("lastChatMessage"),
                        rs.getString("lastChatMessageTimestamp2")),
                getMyChatListParams);
    }

    public List<myTradeRes> myTradeList(int userIdx) {

        String getMyTradeListQuery = "select t1.userIdx, t1.boardIdx, t1.tradeTitle, t2.imgUrl, t1.tradeStatus, FORMAT(t1.price , 0) as price , `t3`.address1 as address, t1.interest," +
                "       t1.refresh," +
                "         CASE" +
                "      WHEN TIMEDIFF(NOW(), t1.refreshStatus) < '01:00:00' THEN concat(MINUTE(TIMEDIFF(NOW(), t1.refreshStatus)), '분전')" +
                "      WHEN TIMEDIFF(NOW(), t1.refreshStatus) < '24:00:00' THEN concat(HOUR(TIMEDIFF(NOW(), t1.refreshStatus)), '시간전')" +
                "      ELSE concat(DATEDIFF(NOW(), t1.refreshStatus), '일전') END as refreshStatus," +
                "      CASE" +
                "      WHEN TIMEDIFF(NOW(), t4.createdAt) < '01:00:00' THEN concat(MINUTE(TIMEDIFF(NOW(), t4.createdAt)), '분전')" +
                "      WHEN TIMEDIFF(NOW(), t4.createdAt) < '24:00:00' THEN concat(HOUR(TIMEDIFF(NOW(), t4.createdAt)), '시간전')" +
                "      ELSE concat(DATEDIFF(NOW(), t4.createdAt), '일전') END as tradeBoardCreateTime" +
                ", (select count(*) from tradeBoard" +
                "    join chatRoom as chatRoom on tradeBoard.boardIdx = chatRoom.boardIdx" +
                "                   where tradeBoard.boardIdx = t4.boardIdx)" +
                "    as 'chatRoomCount'" +
                "from tradeBoard as `t1` left outer join tradeImg as `t2` on t1.boardIdx = `t2`.boardIdx left outer join `memberAddress` as `t3`" +
                "   on t1.userIdx = t3.userIdx left outer join `chatRoom` as `t4` on `t1`.boardIdx = t4.boardIdx" +
                "    where t1.userIdx = ? and t1.tradeStatus = '거래대기' group by `t1`.boardIdx";
        int getMyTradeListParams = userIdx;
        return this.jdbcTemplate.query(getMyTradeListQuery,
                (rs, rowNum) -> new myTradeRes(
                        rs.getInt("userIdx"),
                        rs.getInt("boardIdx"),
                        rs.getString("tradeTitle"),
                        rs.getString("imgUrl"),
                        rs.getString("tradeStatus"),
                        rs.getString("price"),
                        rs.getString("address"),
                        rs.getInt("interest"),
                        rs.getString("refresh"),
                        rs.getString("refreshStatus"),
                        rs.getString("tradeBoardCreateTime"),
                        rs.getInt("chatRoomCount")),
                getMyTradeListParams);


    }

    public List<myNeighborTradeListRes> myNeighborTradeList(int userIdx){
        String getMyNeighborTradeListQuery = "select t3.imgUrl, t1.tradeTitle, t2.address1 as address," +
                "           CASE" +
                "          WHEN TIMEDIFF(NOW(),  t1.refreshStatus) < '01:00:00' THEN concat(MINUTE(TIMEDIFF(NOW(),   t1.refreshStatus)), '분전')" +
                "          WHEN TIMEDIFF(NOW(),   t1.refreshStatus) < '24:00:00' THEN concat(HOUR(TIMEDIFF(NOW(),   t1.refreshStatus)), '시간전')" +
                "          ELSE concat(DATEDIFF(NOW(),   t1.refreshStatus), '일전') END as" +
                "           refreshStatus," +
                "         CASE" +
                "          WHEN TIMEDIFF(NOW(),  t1.createdAt) < '01:00:00' THEN concat(MINUTE(TIMEDIFF(NOW(),   t1.createdAt)), '분전')" +
                "          WHEN TIMEDIFF(NOW(),   t1.createdAt) < '24:00:00' THEN concat(HOUR(TIMEDIFF(NOW(),   t1.createdAt)), '시간전')" +
                "          ELSE concat(DATEDIFF(NOW(),   t1.createdAt), '일전') END as" +
                "           writeTime ," +
                "           FORMAT(t1.price , 0) as price," +
                "           (select count(*) from tradeBoard left join chatRoom on  tradeBoard.boardIdx = chatRoom.boardIdx" +
                "                            where chatRoom.roomIdx = t1.boardIdx) as chatRoomCount" +
                "           ,t1.interest" +
                "    from" +
                "    tradeBoard as t1 left join memberAddress as t2 on t1.userIdx = t2.userIdx" +
                "                     left join tradeImg as t3 on t1.boardIdx = t3.boardIdx" +
                " where INSTR( t2.address1, SUBSTRING_INDEX((select address1 from memberAddress where userIdx = ?), '동', 1)) >0"+
                " or  INSTR( t2.address1, SUBSTRING_INDEX((select address1 from memberAddress where userIdx = ?), '동', 2)) >0"+
                " or  INSTR( t2.address1, SUBSTRING_INDEX((select address1 from memberAddress where userIdx = ?), '구', 1)) >0";
        Object[] getMyNeighborTradeListParams = new Object[]{userIdx, userIdx, userIdx};
        return this.jdbcTemplate.query(getMyNeighborTradeListQuery,
                (rs, rowNum) -> new myNeighborTradeListRes(
                        rs.getString("imgUrl"),
                        rs.getString("tradeTitle"),
                        rs.getString("address"),
                        rs.getString("refreshStatus"),
                        rs.getString("writeTime"),
                        rs.getString("price"),
                        rs.getInt("chatRoomCount"),
                        rs.getInt("interest")),
                getMyNeighborTradeListParams);
    }

    public List<myKeywordTradeListRes> myKeywordTradeList(int userIdx){
        String getMyKeywordTradeListQuery = "select (select imgUrl from tradeImg where tradeImg.boardIdx = tradeBoard.boardIdx group by boardIdx) as tradeImg," +
                "       tradeBoard.tradeTitle ,  FORMAT(tradeBoard.price , 0) as price" +
                "     from" +
                "    tradeBoard join tradeImg on tradeBoard.boardIdx = tradeImg.boardIdx" +
                "    where itemCategory =" +
                "    (select t2.categoryName from interestCategory as t1 join interestCategoryDefinition as t2 on t1.interestIdx = t2.categoryIdx where t1.userIdx = ?)" +
                "    or instr(tradeTitle, (select categoryName2 from interestCategory as t1 join interestCategoryDefinition as t2 on t1.interestIdx = t2.categoryIdx where t1.userIdx = ?))";

        Object[] getMyKeywordTradeListParams = new Object[]{userIdx,userIdx};
        return this.jdbcTemplate.query(getMyKeywordTradeListQuery,
                (rs, rowNum) -> new myKeywordTradeListRes(
                        rs.getString("tradeImg"),
                        rs.getString("tradeTitle"),
                        rs.getString("price")),
                        getMyKeywordTradeListParams);
    }


    public List<userTradeListRes> userTradeList(int userIdx){
        String getUserTradeListQuery = "select Member.userId," +
                "    (select imgUrl from tradeImg where tradeImg.boardIdx = tradeBoard.boardIdx group by boardIdx) as tradeImg," +
                "       tradeBoard.tradeTitle ," +
                "    FORMAT(tradeBoard.price , 0) as price" +
                "     from" +
                "    tradeBoard join tradeImg on tradeBoard.boardIdx = tradeImg.boardIdx" +
                "               join Member on tradeBoard.userIdx = Member.userIdx" +
                "    where tradeBoard.userIdx = ? and tradeBoard.tradeStatus = '거래대기'";

        int getUserTradeListParams = userIdx;
        return this.jdbcTemplate.query(getUserTradeListQuery,
                (rs, rowNum) -> new userTradeListRes(
                        rs.getString("userId"),
                        rs.getString("tradeImg"),
                        rs.getString("tradeTitle"),
                        rs.getString("price")),
                        getUserTradeListParams);

    }

    public tradeDetailRes tradeDetail(int boardIdx){
        String getTradeDetailQuery =
                " select Member.userId," +
                "       Member.profileImg," +
                "    (select imgUrl from tradeImg where tradeImg.boardIdx = tradeBoard.boardIdx group by boardIdx) as tradeImg," +
                "       tradeBoard.tradeTitle ," +
                "       tradeBoard.content," +
                "       tradeBoard.itemCategory," +
                "    FORMAT(tradeBoard.price , 0) as price," +
                "     Member.mannerPoint, memberAddress.address1 as address," +
                "       CASE" +
                "          WHEN TIMEDIFF(NOW(),  tradeBoard.refreshStatus) < '01:00:00' THEN concat(MINUTE(TIMEDIFF(NOW(),   tradeBoard.refreshStatus)), '분전')" +
                "          WHEN TIMEDIFF(NOW(),   tradeBoard.refreshStatus) < '24:00:00' THEN concat(HOUR(TIMEDIFF(NOW(),   tradeBoard.refreshStatus)), '시간전')" +
                "          ELSE concat(DATEDIFF(NOW(),   tradeBoard.refreshStatus), '일전') END as" +
                "           refreshStatus" +
                "        ," +
                "         CASE" +
                "          WHEN TIMEDIFF(NOW(),  tradeBoard.updatedAt) < '01:00:00' THEN concat(MINUTE(TIMEDIFF(NOW(),   tradeBoard.updatedAt)), '분전')" +
                "          WHEN TIMEDIFF(NOW(),   tradeBoard.updatedAt) < '24:00:00' THEN concat(HOUR(TIMEDIFF(NOW(),   tradeBoard.updatedAt)), '시간전')" +
                "          ELSE concat(DATEDIFF(NOW(),   tradeBoard.updatedAt), '일전') END as" +
                "           writeTime" +
                "        from" +
                "         tradeBoard join tradeImg on tradeBoard.boardIdx = tradeImg.boardIdx" +
                "                    join Member on tradeBoard.userIdx = Member.userIdx" +
                "                    join memberAddress on tradeBoard.userIdx = memberAddress.userIdx" +
                "                     where tradeBoard.boardIdx = ?";
        int getTradeDetailParams = boardIdx;
        return this.jdbcTemplate.queryForObject(getTradeDetailQuery,
                (rs,rowNum)-> new tradeDetailRes(
                        rs.getString("userId"),
                        rs.getString("profileImg"),
                        rs.getString("tradeImg"),
                        rs.getString("tradeTitle"),
                        rs.getString("content"),
                        rs.getString("itemCategory"),
                        rs.getString("price"),
                        rs.getFloat("mannerPoint"),
                        rs.getString("address"),
                        rs.getString("refreshStatus"),
                        rs.getString("writeTime")),
                getTradeDetailParams);

    }

    public List<myChatDetailRes> chatDetail(int userIdx, int chatRoomIdx){
        String getMyChatDetailQuery = "select tradeImg.imgUrl as tradeImg, tradeBoard.tradeStatus, tradeBoard.price, tradeBoard.tradeTitle," +
                "          chatContent.content," +
                "          Member.profileImg as profileImg," +
                "          Member.userId as writer," +
                "        REPLACE(REPLACE(DATE_FORMAT(chatContent.updatedAt, '%p %l:%i'),'AM','오전'),'PM','오후')" +
                "        as writeTime" +
                "   from" +
                "        tradeBoard join tradeImg on tradeBoard.boardIdx = tradeImg.boardIdx" +
                "        join chatRoom on chatRoom.boardIdx = tradeBoard.boardIdx" +
                "        join chatContent on chatContent.roomIdx = chatRoom.roomIdx" +
                "        join Member on chatContent.userIdx = Member.userIdx" +
                "        where chatRoom.roomIdx = ? and chatContent.userIdx = ? or chatContent.userIdx2 = ?";

        Object[] getMyChatDetailParams = new Object[]{chatRoomIdx,userIdx,userIdx};
        return this.jdbcTemplate.query(getMyChatDetailQuery,
                (rs,rowNum)-> new myChatDetailRes(
                        rs.getString("tradeImg"),
                        rs.getString("tradeStatus"),
                        rs.getString("price"),
                        rs.getString("tradeTitle"),
                        rs.getString("content"),
                        rs.getString("profileImg"),
                        rs.getString("writer"),
                        rs.getString("writeTime")),
                getMyChatDetailParams);
    }

    public int tradeWrite(int userIdx, tradeWriteReq req){
        String tradeWriteQuery = "INSERT INTO `tradeBoard` (tradeStatus, tradeTitle, content, price, itemCategory, isOffer, userIdx, status)" +
                "value ('거래대기', ?, ?, ?,?, ?, ?, '생성됨' )";
        Object[] tradeWriteParams = new Object[]{req.getTradeTitle(), req.getContent(), req.getPrice(), req.getItemCategory(), req.getIsOffer(), userIdx};
        this.jdbcTemplate.update(tradeWriteQuery, tradeWriteParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }

    public int tradeWrite2(int boardIdx, String imgUrl ){
        String tradeWrite2Query = "INSERT INTO `tradeImg` (boardIdx, imgUrl, status)" +
                "value (?, ?,'생성됨' )";
        Object[] tradeWrite2Params = new Object[]{boardIdx, imgUrl};
        return this.jdbcTemplate.update(tradeWrite2Query, tradeWrite2Params);
    }


}