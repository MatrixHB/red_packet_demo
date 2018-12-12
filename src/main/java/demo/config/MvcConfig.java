package demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.Executor;

@Configuration
public class MvcConfig implements WebMvcConfigurer  {

    //url与视图的对应，不加上这个，输入url是不会跳转的
    public void addViewControllers(ViewControllerRegistry registry) {

        registry.addViewController("/").setViewName("index");
        registry.addViewController("/grap").setViewName("grap");
        registry.addViewController("/grapForVersion").setViewName("grapForVersion");
        registry.addViewController("/grapByRedis").setViewName("grapByRedis");
    }

}
