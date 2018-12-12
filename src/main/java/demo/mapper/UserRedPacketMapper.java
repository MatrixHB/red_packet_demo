package demo.mapper;

import demo.pojo.UserRedPacket;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserRedPacketMapper {

    @Insert("insert into user_red_packet (id, user_id, red_packet_id, amount, grab_time, note)" +
            "values (seq_id.nextval, #{userId}, #{redPacketId}, #{amount}, sysdate, #{note})")
    public void addUserRedPacket(UserRedPacket userRedPacket);

    @Insert("<script>" +
            "insert into user_red_packet (id, user_id, red_packet_id, amount, grab_time, note)" +
                "<foreach collection='list' item='urp' index='index' separator='union all'>" +
                "(select #{urp.id}, #{urp.userId}, #{urp.redPacketId}, #{urp.amount}, #{urp.grabTime}, #{urp.note} from dual)" +
                "</foreach>" +
            "</script>")
    public void addUserRedPacketBatch(@Param("list") List<UserRedPacket> list);

}
