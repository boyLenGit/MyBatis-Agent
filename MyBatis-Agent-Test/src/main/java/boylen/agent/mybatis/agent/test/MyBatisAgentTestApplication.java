package boylen.agent.mybatis.agent.test;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("boylen.agent.mybatis.agent.test.dao")
public class MyBatisAgentTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyBatisAgentTestApplication.class, args);
    }

}
