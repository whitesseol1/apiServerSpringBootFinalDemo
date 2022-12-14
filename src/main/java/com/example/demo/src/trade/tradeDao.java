package com.example.demo.src.trade;

import com.example.demo.src.trade.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import javax.transaction.Transactional;
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
                "select distinct `t3`.address1, t4.profileImg, t4.userId, lastChatMessage," +
                        "  (select imgUrl from tradeImg where boardIdx = t2.boardIdx limit 1) as imgUrl, " +
                        "    CASE " +
                        "    WHEN TIMEDIFF(NOW(), lastChatMessageTimestamp) <'01:00:00' THEN concat(MINUTE(TIMEDIFF(NOW(), lastChatMessageTimestamp)), '분전')" +
                        "    WHEN TIMEDIFF(NOW(), lastChatMessageTimestamp) <'24:00:00' THEN concat(HOUR(TIMEDIFF(NOW(), lastChatMessageTimestamp)), '시간전')" +
                        "    ELSE concat(DATEDIFF(NOW(), lastChatMessageTimestamp), '일전') END as lastChatMessageTimestamp2," +
                        "    t1.roomIdx as chatRoomIdx " +
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
                        "                         group by roomIdx) CurrentMessage   where currentMessageNo = chatIdx) lastChatMessage on lastChatMessage.roomIdx = `t1`.roomIdx" +
                        "       where t1.userIdx = ? group by `t1`.roomIdx";

        int getMyChatListParams = userIdx;
        return this.jdbcTemplate.query(getMyChatListQuery,
                (rs, rowNum) -> new myChatRes(
                        rs.getString("address1"),
                        rs.getString("profileImg"),
                        rs.getString("userId"),
                        rs.getString("imgUrl"),
                        rs.getString("lastChatMessage"),
                        rs.getString("lastChatMessageTimestamp2"),
                        rs.getInt("chatRoomIdx")),
                getMyChatListParams);
    }

    public List<myTradeRes> myTradeList(int userIdx) {

        String getMyTradeListQuery = "select t1.userIdx, t1.boardIdx, t1.tradeTitle, t1.tradeStatus, FORMAT(t1.price , 0) as price , `t3`.address1 as address, t1.interest," +
                "       t1.refresh," +
                "         CASE" +
                "      WHEN TIMEDIFF(NOW(), t1.refreshStatus) < '01:00:00' THEN concat(MINUTE(TIMEDIFF(NOW(), t1.refreshStatus)), '분전')" +
                "      WHEN TIMEDIFF(NOW(), t1.refreshStatus) < '24:00:00' THEN concat(HOUR(TIMEDIFF(NOW(), t1.refreshStatus)), '시간전')" +
                "      ELSE concat(DATEDIFF(NOW(), t1.refreshStatus), '일전') END as refreshStatus," +
                "      CASE" +
                "      WHEN TIMEDIFF(NOW(), t1.createdAt) < '01:00:00' THEN concat(MINUTE(TIMEDIFF(NOW(), t1.createdAt)), '분전')" +
                "      WHEN TIMEDIFF(NOW(), t1.createdAt) < '24:00:00' THEN concat(HOUR(TIMEDIFF(NOW(), t1.createdAt)), '시간전')" +
                "      ELSE concat(DATEDIFF(NOW(), t1.createdAt), '일전') END as tradeBoardCreateTime" +
                ", (select count(*) from tradeBoard" +
                "    join chatRoom as chatRoom on tradeBoard.boardIdx = chatRoom.boardIdx" +
                "                   where tradeBoard.boardIdx = t4.boardIdx)" +
                "    as 'chatRoomCount'," +
                "  (select imgUrl from tradeImg where boardIdx = t1.boardIdx limit 1) as imgUrl " +
                "from tradeBoard as `t1`  left join `memberAddress` as `t3`" +
                "   on t1.userIdx = t3.userIdx left join `chatRoom` as `t4` on `t1`.boardIdx = t4.boardIdx" +
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

    public List<myTradeRes> myFinishTradeList(int userIdx) {
        String getMyTradeListQuery = "select t1.userIdx, t1.boardIdx, t1.tradeTitle, t1.tradeStatus, FORMAT(t1.price , 0) as price , `t3`.address1 as address, t1.interest," +
                "       t1.refresh, " +
                "         CASE " +
                "      WHEN TIMEDIFF(NOW(), t1.refreshStatus) < '01:00:00' THEN concat(MINUTE(TIMEDIFF(NOW(), t1.refreshStatus)), '분전')" +
                "      WHEN TIMEDIFF(NOW(), t1.refreshStatus) < '24:00:00' THEN concat(HOUR(TIMEDIFF(NOW(), t1.refreshStatus)), '시간전')" +
                "      WHEN t1.refreshStatus is null THEN null "+
                "      ELSE concat(DATEDIFF(NOW(), t1.refreshStatus), '일전') END as refreshStatus," +
                "      CASE" +
                "      WHEN TIMEDIFF(NOW(), t1.createdAt) < '01:00:00' THEN concat(MINUTE(TIMEDIFF(NOW(), t1.createdAt)), '분전')" +
                "      WHEN TIMEDIFF(NOW(), t1.createdAt) < '24:00:00' THEN concat(HOUR(TIMEDIFF(NOW(), t1.createdAt)), '시간전')" +
                "      ELSE concat(DATEDIFF(NOW(), t1.createdAt), '일전') END as tradeBoardCreateTime" +
                ", (select count(*) from tradeBoard" +
                "    join chatRoom as chatRoom on tradeBoard.boardIdx = chatRoom.boardIdx" +
                "                   where tradeBoard.boardIdx = t4.boardIdx)" +
                "    as 'chatRoomCount', (select imgUrl from tradeImg where boardIdx = t1.boardIdx limit 1) as imgUrl" +
                "  from tradeBoard as `t1` left join `memberAddress` as `t3`" +
                "   on t1.userIdx = t3.userIdx left join `chatRoom` as `t4` on `t1`.boardIdx = t4.boardIdx" +
                "    where t1.userIdx = ? and t1.tradeStatus = '거래완료' group by `t1`.boardIdx";
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

    public List<myTradeRes> myBuyTradeList(int userIdx){
        String getMyTradeListQuery = "select t1.userIdx, t1.boardIdx, t1.tradeTitle, t1.tradeStatus, FORMAT(t1.price , 0) as price , `t3`.address1 as address, t1.interest,"+
               " t1.refresh,"+
                "CASE "+
        "WHEN TIMEDIFF(NOW(), t1.refreshStatus) < '01:00:00' THEN concat(MINUTE(TIMEDIFF(NOW(), t1.refreshStatus)), '분전') "+
        "WHEN TIMEDIFF(NOW(), t1.refreshStatus) < '24:00:00' THEN concat(HOUR(TIMEDIFF(NOW(), t1.refreshStatus)), '시간전') "+
        "ELSE concat(DATEDIFF(NOW(), t1.refreshStatus), '일전') END as refreshStatus,"+
                "CASE "+
        "WHEN TIMEDIFF(NOW(), t1.createdAt) < '01:00:00' THEN concat(MINUTE(TIMEDIFF(NOW(), t1.createdAt)), '분전') "+
        "WHEN TIMEDIFF(NOW(), t1.createdAt) < '24:00:00' THEN concat(HOUR(TIMEDIFF(NOW(), t1.createdAt)), '시간전') "+
        "ELSE concat(DATEDIFF(NOW(), t1.createdAt), '일전') END as tradeBoardCreateTime"+
                ", (select count(*) from tradeBoard "+
        "join chatRoom as chatRoom on tradeBoard.boardIdx = chatRoom.boardIdx "+
        "where tradeBoard.boardIdx = t4.boardIdx) "+
        "as 'chatRoomCount', (select imgUrl from tradeImg where boardIdx = t1.boardIdx limit 1) as imgUrl "+
        "from tradeBoard as `t1`  left join `memberAddress` as `t3` "+
        "on t1.userIdx = t3.userIdx  left join `chatRoom` as `t4` on `t1`.boardIdx = t4.boardIdx "+
        " join buySellRecord bSR on t1.boardIdx = bSR.boardIdx "+
        "where bSR.userIdx = ? and bSR.buySellStatus = 'buy' and bSR.tradeStatus = '거래완료'";

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
        String getMyNeighborTradeListQuery = "select (select imgUrl from tradeImg where boardIdx = t1.boardIdx limit 1) as imgUrl, t1.tradeTitle, t2.address1 as address," +
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
                "           ,t1.interest, t1.boardIdx " +
                "    from" +
                "    tradeBoard as t1 left join memberAddress as t2 on t1.userIdx = t2.userIdx" +
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
                        rs.getInt("interest"),
                        rs.getInt("boardIdx")),
                getMyNeighborTradeListParams);
    }

    public List<myKeywordTradeListRes> myKeywordTradeList(int userIdx){
        String getMyKeywordTradeListQuery = "select (select imgUrl from tradeImg where tradeImg.boardIdx = tradeBoard.boardIdx limit 1) as tradeImg," +
                "       tradeBoard.tradeTitle ,  FORMAT(tradeBoard.price , 0) as price, tradeBoard.boardIdx" +
                "     from" +
                "    tradeBoard " +
                "    where itemCategory =" +
                "    (select t2.categoryName from interestCategory as t1 join interestCategoryDefinition as t2 on t1.interestIdx = t2.categoryIdx where t1.userIdx = ?)" +
                "    or instr(tradeTitle, (select categoryName2 from interestCategory as t1 join interestCategoryDefinition as t2 on t1.interestIdx = t2.categoryIdx where t1.userIdx = ?))";

        Object[] getMyKeywordTradeListParams = new Object[]{userIdx,userIdx};
        return this.jdbcTemplate.query(getMyKeywordTradeListQuery,
                (rs, rowNum) -> new myKeywordTradeListRes(
                        rs.getString("tradeImg"),
                        rs.getString("tradeTitle"),
                        rs.getString("price"),
                        rs.getInt("boardIdx")),
                        getMyKeywordTradeListParams);
    }


    public List<userTradeListRes> userTradeList(int userIdx){
        String getUserTradeListQuery = "select Member.userId," +
                "    (select imgUrl from tradeImg where tradeImg.boardIdx = tradeBoard.boardIdx limit 1) as tradeImg," +
                "       tradeBoard.tradeTitle ," +
                "    FORMAT(tradeBoard.price , 0) as price, tradeBoard.boardIdx as boardIdx" +
                "     from" +
                "    tradeBoard " +
                "               join Member on tradeBoard.userIdx = Member.userIdx" +
                "    where tradeBoard.userIdx = ? and tradeBoard.tradeStatus = '거래대기'";

        int getUserTradeListParams = userIdx;
        return this.jdbcTemplate.query(getUserTradeListQuery,
                (rs, rowNum) -> new userTradeListRes(
                        rs.getString("userId"),
                        rs.getString("tradeImg"),
                        rs.getString("tradeTitle"),
                        rs.getString("price"),
                        rs.getInt("boardIdx")),
                        getUserTradeListParams);

    }

    public tradeDetailRes tradeDetail(int boardIdx){
        String getTradeDetailQuery =
                " select Member.userId as userId, Member.userIdx as userIdx, " +
                "       Member.profileImg as profileImg," +
                "       tradeBoard.tradeTitle as tradeTitle," +
                "       tradeBoard.content as content," +
                "       tradeBoard.itemCategory as itemCategory," +
                "    FORMAT(tradeBoard.price , 0) as price," +
                "     Member.mannerPoint as mannerPoint, memberAddress.address1 as address," +
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
                "           writeTime," +
                "         tradeBoard.boardIdx as boardIdx " +
                "        from" +
                "         tradeBoard " +
                "                    left join Member on tradeBoard.userIdx = Member.userIdx" +
                "                    left join memberAddress on tradeBoard.userIdx = memberAddress.userIdx" +
                "                     where tradeBoard.boardIdx = ?";
        int getTradeDetailParams = boardIdx;
        return this.jdbcTemplate.queryForObject(getTradeDetailQuery,
                (rs,rowNum)-> new tradeDetailRes(
                        rs.getString("userId"),
                        rs.getString("profileImg"),
                        rs.getString("tradeTitle"),
                        rs.getString("content"),
                        rs.getString("itemCategory"),
                        rs.getString("price"),
                        rs.getFloat("mannerPoint"),
                        rs.getString("address"),
                        rs.getString("refreshStatus"),
                        rs.getString("writeTime"),
                        rs.getInt("boardIdx"),
                        rs.getInt("userIdx")),
                getTradeDetailParams);

    }

    public List<String> tradeDetail2(int boardIdx){
        String getImgQuery = "select imgUrl from tradeImg where boardIdx = ?";
        return this.jdbcTemplate.queryForList(getImgQuery,String.class,boardIdx);
    }


    public List<myChatDetailRes> chatDetail(int userIdx, int chatRoomIdx){
        String getMyChatDetailQuery = "select (select imgUrl from tradeImg where tradeImg.boardIdx = tradeBoard.boardIdx limit 1) as tradeImg, tradeBoard.tradeStatus, tradeBoard.price, tradeBoard.tradeTitle," +
                "          chatContent.content," +
                "          Member.profileImg as profileImg," +
                "          Member.userId as writer," +
                "        REPLACE(REPLACE(DATE_FORMAT(chatContent.updatedAt, '%p %l:%i'),'AM','오전'),'PM','오후')" +
                "        as writeTime, chatContent.chatIdx as chatIdx" +
                "   from" +
                "        tradeBoard " +
                "        left join chatRoom on chatRoom.boardIdx = tradeBoard.boardIdx" +
                "        left join chatContent on chatContent.roomIdx = chatRoom.roomIdx" +
                "        left join Member on chatContent.userIdx = Member.userIdx" +
                "        where chatRoom.roomIdx = ? order by writeTime ";

        Object[] getMyChatDetailParams = new Object[]{chatRoomIdx};
        return this.jdbcTemplate.query(getMyChatDetailQuery,
                (rs,rowNum)-> new myChatDetailRes(
                        rs.getString("tradeImg"),
                        rs.getString("tradeStatus"),
                        rs.getString("price"),
                        rs.getString("tradeTitle"),
                        rs.getString("content"),
                        rs.getString("profileImg"),
                        rs.getString("writer"),
                        rs.getString("writeTime"),
                        rs.getInt("chatIdx")),
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


    public int tradeModify(int userIdx, tradeWriteReq req){
        String selectUserQuery = "SELECT userIdx FROM `tradeBoard` WHERE boardIdx = ?";
        int selectUserIdx = this.jdbcTemplate.queryForObject(selectUserQuery,int.class, req.getBoardIdx());
        int result1=0;
        if(selectUserIdx == userIdx) {
            String tradeModifyQuery = "UPDATE `tradeBoard` SET `updatedAt` = current_timestamp," +
                    " tradeTitle = ?, content = ?, price = ?, itemCategory = ?, isOffer = ?, status = '수정됨' WHERE boardIdx = ?";
            Object[] tradeModifyParams = new Object[]{req.getTradeTitle(),req.getContent(),req.getPrice(),req.getItemCategory(),req.getIsOffer(),req.getBoardIdx()};
            result1 = this.jdbcTemplate.update(tradeModifyQuery,tradeModifyParams);

            if(req.getImgUrl() != null){
                String deleteQuery = "DELETE FROM `tradeImg` WHERE boardIdx = ?";
                int deleteResult = this.jdbcTemplate.update(deleteQuery, req.getBoardIdx());

                    for (int i = 0; i < req.getImgUrl().size(); i++) {
                        String insertImgQuery = "INSERT INTO `tradeImg` (boardIdx, imgUrl, status)" +
                                "value (?, ?,'수정됨' )";
                        Object[] insertImgParams = new Object[]{req.getBoardIdx(), req.getImgUrl().get(i)};
                        this.jdbcTemplate.update(insertImgQuery, insertImgParams);

                    }
            }

        }

        return result1;

    }

    public int chatWrite(int userIdx, chatWriteReq req){



        //채팅글 처음 생성시(roomIdx가 없을때)
            String user2SelectQuery = "SELECT userIdx FROM `tradeBoard` WHERE boardIdx = ?";
            int user2SelectParam = req.getBoardIdx();
            int userIdx2 = this.jdbcTemplate.queryForObject(user2SelectQuery,int.class,user2SelectParam);


            String chatRoomInsertQuery = "INSERT INTO `chatRoom` (boardIdx, status) VALUE (?, '생성됨')";
            int chatRoomInsertParam = req.getBoardIdx();
            this.jdbcTemplate.update(chatRoomInsertQuery, chatRoomInsertParam);
            String lastInsertIdxQuery = "select last_insert_id()";
            int chatRoomIdx = this.jdbcTemplate.queryForObject(lastInsertIdxQuery, int.class);

        String chatContentInsertQuery = "INSERT INTO `chatContent` (roomIdx, userIdx,userIdx2, content)" +
                "value (?, ?, ?, ?)";
        Object[] chatContentInsertParams = new Object[]{chatRoomIdx, userIdx, userIdx2, req.getContent()};
        return this.jdbcTemplate.update(chatContentInsertQuery, chatContentInsertParams);

    }

    public chatWriteUserRes chatWrite2(chatWriteReq req){
        //채팅글 생성이 되어있을때
        int userIdx2 = 0;
        String selectQuery = "SELECT userIdx, userIdx2 FROM `chatContent` WHERE roomIdx = ? group by roomIdx";
        int selectParam = req.getRoomIdx();
        return this.jdbcTemplate.queryForObject(selectQuery,
                (rs,rowNum)-> new chatWriteUserRes(
                        rs.getInt("userIdx"),
                        rs.getInt("userIdx2")), selectParam);

    }

    public int chatWrite3(int userIdx, chatWriteReq req, chatWriteUserRes res){

        int userIdx2 = 0;
        if( res.getUserIdx() == userIdx ){
            userIdx2 = res.getUserIdx2();
        }else if (res.getUserIdx2() == userIdx){
            userIdx2 = res.getUserIdx();
        }
        int chatRoomIdx = req.getRoomIdx();


        String chatContentInsertQuery = "INSERT INTO `chatContent` (roomIdx, userIdx,userIdx2, content)" +
                "value (?, ?, ?, ?)";
        Object[] chatContentInsertParams = new Object[]{chatRoomIdx, userIdx, userIdx2, req.getContent()};
        return this.jdbcTemplate.update(chatContentInsertQuery, chatContentInsertParams);

    }
@Transactional
    public String insertInterest(int userIdx, int boardIdx){
       this.jdbcTemplate.update("UPDATE `tradeBoard` SET `interest` =  0 WHERE `interest` is null");

        String selectInterestListQuery = "SELECT interestStatus, COUNT(*) as count FROM `interestList` where userIdx = ? and boardIdx = ?";
        Object[] selectInterestListParam = new Object[]{userIdx, boardIdx};
        interestRes res = this.jdbcTemplate.queryForObject(selectInterestListQuery,
                (rs,rowNum)-> new interestRes(
                        rs.getString("interestStatus"),
                        rs.getInt("count")
                ), selectInterestListParam);

        String resultMessage = "";
        int result1 = 0; int result2 = 0;
       if(res.getCount() <1 ) {
           String interestInsertBoardQuery = "UPDATE `tradeBoard` SET `updatedAt` = current_timestamp, interest = interest+1 WHERE boardIdx = ?";
           int interestInsertBoardParam = boardIdx;
           result1 = this.jdbcTemplate.update(interestInsertBoardQuery, interestInsertBoardParam);

           String insertInterestListQuery = "INSERT INTO `interestList` (boardIdx,interestStatus, userIdx)" +
                   "value (?, '관심있음', ?)";
           Object[] insertInterestListParams = new Object[]{boardIdx, userIdx};
           result2 = this.jdbcTemplate.update(insertInterestListQuery, insertInterestListParams);
           if(result1 >0 && result2 >0){
               resultMessage = "나의 관심목록에 등록되었습니다.";
           }
       }else if(res.getCount() >0 && res.getInterestStatus().equals("관심있음")){
           String interestInsertRollBackBoardQuery = "UPDATE `tradeBoard` SET `updatedAt` = current_timestamp, interest = interest-1 WHERE boardIdx = ?";
           int interestInsertRollBackBoardParam = boardIdx;
           result1 = this.jdbcTemplate.update(interestInsertRollBackBoardQuery, interestInsertRollBackBoardParam);

           String insertInterestListQuery = "UPDATE `interestList` SET `updatedAt` = current_timestamp, interestStatus = '관심있음취소' WHERE boardIdx = ? and userIdx = ?";
           Object[] insertInterestListParams = new Object[]{boardIdx, userIdx};
           result2 = this.jdbcTemplate.update(insertInterestListQuery, insertInterestListParams);
           if(result1 >0 && result2 >0){
               resultMessage = "나의 관심목록에서 취소되었습니다.";
           }

       }else if(res.getCount() >0 && res.getInterestStatus().equals("관심있음취소")){
           String interestInsertBoardQuery = "UPDATE `tradeBoard` SET `updatedAt` = current_timestamp, interest = interest+1 WHERE boardIdx = ?";
           int interestInsertBoardParam = boardIdx;
           result1 = this.jdbcTemplate.update(interestInsertBoardQuery, interestInsertBoardParam);

           String insertInterestListQuery = "UPDATE `interestList` SET `updatedAt` = current_timestamp, interestStatus = '관심있음' WHERE boardIdx = ? and userIdx = ?";
           Object[] insertInterestListParams = new Object[]{boardIdx, userIdx};
           result2 = this.jdbcTemplate.update(insertInterestListQuery, insertInterestListParams);
           if(result1 >0 && result2 >0){
               resultMessage = "나의 관심목록에 등록되었습니다.";
           }
       }

           return resultMessage;

    }

}