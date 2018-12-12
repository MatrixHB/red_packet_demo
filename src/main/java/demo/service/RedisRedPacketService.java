package demo.service;

import demo.dao.RedPacketDao;
import demo.pojo.UserRedPacket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Jedis;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/*
 * 使用Redis缓存抢红包的服务类
 * 执行批量记录同步到数据库的功能
 */
@Service
public class RedisRedPacketService {

    @Autowired
    RedPacketDao redPacketDao;

    @Autowired
    RedisTemplate redisTemplate;

    //lua脚本
    String script = "local userRedPacketKey = 'user_red_packet_list_'..KEYS[1]\n" +
            "local redPacketKey = 'red_packet_'..KEYS[1]\n" +
            "local stock = tonumber(redis.call('hget',redPacketKey,'stock'))\n" +
            "local total = tonumber(redis.call('hget',redPacketKey,'total'))\n" +
            "local num = '-'..tostring(total - stock +1)\n" +
            "local record = ARGV[1]..num\n" +
            "if stock<=0 then \n" +
            "    return 0\n" +
            "end\n" +
            "stock = stock-1\n" +
            "redis.call('hset',redPacketKey,'stock',tostring(stock))\n" +
            "redis.call('rpush',userRedPacketKey, record)\n" +
            "if stock==0 then \n" +
            "    return 2\n" +
            "end \n" +
            "return 1";

    //执行lua脚本的对象
    private DefaultRedisScript redisScript;

    //Redis抢红包服务，使用lua脚本，通过RedisTemplate操作
    public Long grabRedPacketByRedis(int redPacketId, int userId){
        //只创建一次对象，避免内存占用
        if(redisScript == null){
            redisScript = new DefaultRedisScript();
            //设置脚本执行后的返回类型，只接受Long、Boolean、List和deserialized value四种类型
            redisScript.setResultType(Long.class);
            //设置执行的脚本语句
            redisScript.setScriptText(script);
        }

        List<String> keyList = new ArrayList<>();
        keyList.add(String.valueOf(redPacketId) );

        //在Redis保存抢红包记录时，保存“用户ID - 抢红包时间”
        String args = userId + "-" + System.currentTimeMillis();

        //参数1为脚本执行对象，参数2为KEYS的列表，参数3为ARGV可以为任意对象
        Long result = (Long)redisTemplate.execute(redisScript, keyList, args);
        if(result==2){
            String amount = (String)redisTemplate.opsForHash().get("red_packet_"+redPacketId, "unit_amount");
            saveRedisListToDB(redPacketId, Double.valueOf(amount) );
        }

        return result;

    }

    private static final String PREFIX = "user_red_packet_list_";
    //为避免内存占用过多，每次只取1000条出来同步到数据库
    private static final int TIME_SIZE = 1000;

    //抢红包记录同步到数据库，@Async开启新线程
    @Async
    public void saveRedisListToDB(int redPacketId, double amount){
        System.out.println("开启保存数据的线程~");
        //获取列表操作对象
        BoundListOperations ops = redisTemplate.boundListOps(PREFIX + redPacketId);
        long size = ops.size();
        long times = size%TIME_SIZE==0 ? size/TIME_SIZE : size/TIME_SIZE +1;       //一共要操作多少次
        List<UserRedPacket> userRedPacketList = new ArrayList<>();
        for(int i=0; i<times; i++){
            List<String> grabRecordList = ops.range(i*TIME_SIZE , (i+1)*TIME_SIZE -1 );
            userRedPacketList.clear();
            for(String grabRecord : grabRecordList){
                UserRedPacket userRedPacket = new UserRedPacket();
                int userId = Integer.valueOf(grabRecord.split("-")[0]);
                long time = Long.valueOf(grabRecord.split("-")[1]);
                int num = Integer.valueOf(grabRecord.split("-")[2]);
                userRedPacket.setId(userId);
                userRedPacket.setUserId(userId);
                userRedPacket.setGrabTime( new Timestamp(time) );
                userRedPacket.setRedPacketId(redPacketId);
                userRedPacket.setAmount(amount);
                userRedPacket.setNote("抢到了第" + num + "个红包");
                userRedPacketList.add(userRedPacket);
            }
            //批量写入数据库
            redPacketDao.addUserRedPacketBatch(userRedPacketList);
        }
        System.out.println("记录已同步更新至数据库");
    }
}
