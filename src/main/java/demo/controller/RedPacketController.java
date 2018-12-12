package demo.controller;

import demo.service.RedPacketService;
import demo.service.RedisRedPacketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/userRedPacket")
public class RedPacketController {

    @Autowired
    RedPacketService redPacketService;

    @Autowired
    RedisRedPacketService redisRedPacketService;

    @PostMapping("/grapRedPacketForVersion")
    public Map<String,Object> grabRedPacket(int redPacketId, int userId){
        Map<String,Object> retMap = new HashMap<>();
        boolean flag = redPacketService.grabRedPacket(redPacketId, userId);
        retMap.put("success", flag);
        String msg = flag ? "抢红包成功" : "抢红包失败";
        retMap.put("message", msg);
        System.out.println( userId + "号用户" + msg);
        return retMap;
    }

    @PostMapping("/grapRedPacketByRedis")
    public void grabRedPacketByRedis(int redPacketId, int userId){
        Long res = redisRedPacketService.grabRedPacketByRedis(redPacketId, userId);
        String msg = res>0 ? "抢红包成功" : "抢红包失败";
        System.out.println( userId + "号用户" + msg);
    }

}
