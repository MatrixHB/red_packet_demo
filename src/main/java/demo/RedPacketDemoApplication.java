package demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(value = "Mapper")
@SpringBootApplication
public class RedPacketDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedPacketDemoApplication.class, args);
    }
}

/*
 * 1、没有加锁，整个抢红包过程持续4.32分钟，超发2个红包
 * 2、悲观锁，整个抢红包过程持续14.5分钟，最大读并发数8，最慢update为3.187秒，最慢insert为14.043秒，最慢select为162.498秒
 * 3、乐观锁，如果不可重入，则仅有1066次写入成功，其他视为抢不到红包，失败率过高
 *    乐观锁可重入有两种方式，限定时间内可重入，或者限定次数可重入
 *    乐观锁1秒内可重入：5593次请求，成功4307次，执行事务重入23495次，仍有1286次请求在1秒内无法写入成功，整个过程持续9.25分钟
 * 4、采用Redis缓存 + lua原子性语言，整个抢红包过程仅持续21秒，批量写入数据库5次花了4.446秒，既保证不超发，又大大提高了效率
 */