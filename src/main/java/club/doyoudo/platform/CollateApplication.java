package club.doyoudo.platform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("club.doyoudo.platform.mapper")
@SpringBootApplication
@EnableScheduling
public class CollateApplication {

    public static void main(String[] args) {
        SpringApplication.run( CollateApplication.class, args );
    }

}
