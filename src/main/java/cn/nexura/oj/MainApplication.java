package cn.nexura.oj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 主类（项目启动入口）
 */
// 增加题目的通过数、提交数统计，计算通过率
// TODO: 2024/1/23 限制代码沙箱中最多允许同时启动的 Docker 容器数，防止系统过载（甚至还可以用池化技术复用 Docker容器）
// TODO: 2024/1/23 更多类型的代码沙箱实现，比如使用 AI 进行判题？使用第三方服务（judge0 api）进行判题？
// TODO: 2024/1/23 限制单个用户的同时最大提交数，合理分配资源。
// TODO: 2024/1/23 限制单个用户的提交频率，可以通过 Redisson 或者 Sentinel 网关层限流实现。
// TODO: 2024/1/23 实现 ACM 模式（通过代码进行输入输出）的代码沙箱
// TODO: 2024/1/23 用同样的思路或者 Linux 的 cgroup 语法实现一种其他编程语言的代码沙箱
// TODO: 2024/1/23 实现 Special Judge 特判程序的逻辑
// TODO: 2024/1/23 给判题过程中的每个测试用例增加一个独立的内存、时间占用的统计
@SpringBootApplication
@MapperScan("cn.nexura.oj.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

}
