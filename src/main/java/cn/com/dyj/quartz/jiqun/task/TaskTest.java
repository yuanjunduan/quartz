package cn.com.dyj.quartz.jiqun.task;

import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@EnableScheduling
public class TaskTest {

  @Scheduled(cron = "* 0/1 * * * ?")
  public void abc() {


    log.info("JobName: { abc }");
  }
}
