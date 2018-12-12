package demo;

import demo.dao.RedPacketDao;
import demo.pojo.RedPacket;
import demo.pojo.UserRedPacket;
import demo.service.RedPacketService;
import demo.service.RedisRedPacketService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedPacketDemoApplicationTests {

    @Autowired
    RedPacketService redPacketService;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    RedPacketDao redPacketDao;

    @Autowired
    RedisRedPacketService redisRedPacketService;

    //测试service
    @Test
    public void test01() {
        if(redPacketService.grabRedPacket(1, 20)){
            System.out.println("success!");
        }
    }

    //测试redis读写
    @Test
    public void test02() {
//        RedPacket redPacket = redPacketDao.getRedPacket(1);
//        redisTemplate.opsForValue().set("redPacket01", redPacket);
        Object amount = redisTemplate.opsForHash().get("red_packet_1", "stock");
        System.out.println(amount);
    }

    //测试mybatis批量插入功能
    @Test
    public void test03() {
        List<UserRedPacket> list = new ArrayList<>();
        for(int i=1; i<=3; i++){
            UserRedPacket userRedPacket = new UserRedPacket();
            userRedPacket.setId(i);
            userRedPacket.setRedPacketId(1);
            userRedPacket.setUserId(i);
            userRedPacket.setAmount(20);
            userRedPacket.setGrabTime( new Timestamp(System.currentTimeMillis()) );
            userRedPacket.setNote("抢到了第" + i + "个红包");
            list.add(userRedPacket);
        }
        redPacketDao.addUserRedPacketBatch(list);
    }

    @Test
    public void test04(){
        redisRedPacketService.grabRedPacketByRedis(1, 20);
    }

}
