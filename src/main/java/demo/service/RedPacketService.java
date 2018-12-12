package demo.service;

import demo.dao.RedPacketDao;
import demo.pojo.RedPacket;
import demo.pojo.UserRedPacket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RedPacketService {

    @Autowired
    RedPacketDao redPacketDao;

    //事务的隔离级别为读提交
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public boolean grabRedPacket(int redPacketId, int userId){
        //不加锁，并发可能超发
//        RedPacket redPacket = redPacketDao.getRedPacket(redPacketId);
        //悲观锁
//        RedPacket redPacket = redPacketDao.getRedPacketForUpdate(redPacketId);
        //乐观锁，可重入
        long start = System.currentTimeMillis();
        while(true) {
            long end = System.currentTimeMillis();
            if(end - start >1000){             //超时1秒则视为抢红包失败
                return false;
            }
            RedPacket redPacket = redPacketDao.getRedPacket(redPacketId);
            int stock = redPacket.getStock();
            if (stock > 0) {
                int update = redPacketDao.decreaseRedPacketForVersion(redPacketId, redPacket.getVersion());
                if (update == 0) {
                    continue;
                }
                UserRedPacket userRedPacket = new UserRedPacket();
                userRedPacket.setRedPacketId(redPacketId);
                userRedPacket.setUserId(userId);
                userRedPacket.setAmount(redPacket.getUnitAmount());
                userRedPacket.setNote("抢到了第" + String.valueOf(redPacket.getTotal() - stock + 1) + "个红包");
                redPacketDao.addUserRedPacket(userRedPacket);
                return true;
            }
        }
    }
}
