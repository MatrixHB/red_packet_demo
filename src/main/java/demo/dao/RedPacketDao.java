package demo.dao;

import demo.mapper.RedPacketMapper;
import demo.mapper.UserRedPacketMapper;
import demo.pojo.RedPacket;
import demo.pojo.UserRedPacket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class RedPacketDao {

    @Autowired
    DataSource dataSource;
    @Autowired
    RedPacketMapper redPacketMapper;
    @Autowired
    UserRedPacketMapper userRedPacketMapper;

    public RedPacket getRedPacket(int id){
        return redPacketMapper.getRedPacket(id);
    }

    public void decreaseRedPacket(int id){
        redPacketMapper.decreaseRedPacket(id);
    }

    public void addUserRedPacket(UserRedPacket userRedPacket){
        userRedPacketMapper.addUserRedPacket(userRedPacket);
    }

    //悲观锁机制，读取同一个表时，要等到其他事务被提交时才会执行
    public RedPacket getRedPacketForUpdate (int id){
        return redPacketMapper.getRedPacketForUpdate(id);
    }

    //乐观锁机制，用版本号
    public int decreaseRedPacketForVersion (int id, int version){
        return redPacketMapper.decreaseRedPacketForVersion(id, version);
    }

    //批量插入抢红包记录
    public void addUserRedPacketBatch(List<UserRedPacket> list){
        userRedPacketMapper.addUserRedPacketBatch(list);
    }

}
