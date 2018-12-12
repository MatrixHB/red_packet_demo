package demo.mapper;

import demo.pojo.RedPacket;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface RedPacketMapper {

    @Select("select * from red_packet where id = #{id}")
    public RedPacket getRedPacket (int id);

    @Update("update red_packet set stock = stock-1 where id = #{id}")
    public void decreaseRedPacket(int id);

    @Select("select * from red_packet where id = #{id} for update")
    public RedPacket getRedPacketForUpdate (int id);

    //注意传入多参数的方式，要用@Param指定参数名
    @Update("update red_packet set stock = stock-1, version = version +1 " +
            "where id = #{id} and version = #{version}")
    public int decreaseRedPacketForVersion(@Param("id") int id, @Param("version") int version);

}
