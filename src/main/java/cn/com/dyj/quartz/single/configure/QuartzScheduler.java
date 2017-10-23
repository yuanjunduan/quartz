package cn.com.dyj.quartz.single.configure;

import cn.com.dyj.quartz.single.model.ObjectPo;
import cn.com.dyj.quartz.single.model.TaskDetail;
import cn.com.dyj.quartz.single.neum.StrategyType;
import cn.com.dyj.quartz.single.task.SendRedPacketsJob;
import cn.com.dyj.quartz.single.task.SendSmsJob;

import lombok.extern.slf4j.Slf4j;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

/**
 * Created by liufei on 2017/8/7.
 */

@Slf4j
@Configuration
@Component
@EnableScheduling
public class QuartzScheduler {

  @Autowired
  private SchedulerFactoryBean schedulerFactoryBean;

  @Scheduled(cron = "* 0/1 * * * ?")
  public void updateSimpleNum() {
    //fixme  具体操作的业务逻辑 TODO
    log.info(" 数据每天需要进行更新  更新完 下面定时任务使用");
  }

  @PostConstruct
  public void initTask() {

    log.info("初始化任务信息开始！");
    final List<ObjectPo> strategies = new ArrayList<>();// 去数据库查询要执行的数据
    if (!strategies.isEmpty()) {


      try {
        final Scheduler scheduler = schedulerFactoryBean.getScheduler();
        final int strategyNum = strategies.size();
        int errorNum = 0;
        log.info("查询到" + strategyNum + "个任务");

        for (final ObjectPo strategy : strategies) {//fixme 这里可以把执行完的跳过
          if (!addQuartzJob(scheduler, strategy)) {
            errorNum++;
          }
        }
        log.info("共查询到{}个策略任务，初始化成功{}个，失败{}个。", strategyNum, strategyNum - errorNum, errorNum);
        scheduler.start();
      } catch (SchedulerException ex) {
        log.error("quartz init task error:");
      }
      log.info("初始化任务信息结束！");
    }
  }

  /**
   * 更新策略信息时候，将策略信息更新到quartz
   */
  public void updateTaskStrategyInfo(final ObjectPo strategy) {
    try {

      final Scheduler scheduler = schedulerFactoryBean.getScheduler();
      if (strategy != null) {
        updateQuartzJob(scheduler, strategy);
      }
    } catch (Exception ex) {
      log.error("quartz update task error:" + ex.toString());
    }
  }

  /**
   * 删除策略任务信息
   */
  public void delTaskStrategyInfo(final ObjectPo strategy) {

    try {

      final Scheduler scheduler = schedulerFactoryBean.getScheduler();
      if (strategy != null) {
        delQuartzJob(scheduler, strategy);
      }
    } catch (Exception ex) {
      log.error("quartz delete task error:" + ex.toString());
    }
  }

  /**
   * 添加任务到定时器
   */
  public boolean addQuartzJob(final Scheduler scheduler, final ObjectPo strategy) {
    if (scheduler == null) {
      log.error("定时任务调度器不能为null");
      return false;
    }

    if (strategy == null) {
      log.error("定时任务策略不能为空");
      return false;
    }
    try {

      final Class jobClass = genJobClass(strategy.getStrategyType());
      //如果策略类型找不到对应的处理类，刚不添加任务策略
      if (jobClass != null) {
        //构建触发器
        final CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(strategy.getCronExpression())
            .withMisfireHandlingInstructionDoNothing();//这里可以设置任务错失 需不需要执行

        final CronTrigger trigger = TriggerBuilder.newTrigger()
            .withDescription(strategy.getName())
            .withIdentity(String.valueOf(strategy.getId()))
            .withSchedule(scheduleBuilder)
            .startAt(strategy.getTriggerTime().toDate())//开始时间
            .endAt(strategy.getEndTriggerTime().toDate())//结束时间
            .build();

        //将策略信息放到jobDataMap中，传到job中使用
        final JobDataMap jobDataMap = new JobDataMap();
        //目前这里只有短信、红包
        jobDataMap.putIfAbsent("strategy", strategy);

        final JobDetail jobDetail = JobBuilder.newJob(jobClass)
            .withIdentity(String.valueOf(strategy.getId()))
            .withDescription(strategy.getName())
            .setJobData(jobDataMap)
            .build();

        scheduler.scheduleJob(jobDetail, trigger);

        QuartzTaskManager.getInstance()
            .setTask(strategy.getId(), TaskDetail.builder().trigger(trigger).jobDetail(jobDetail).build());

        log.info("添加任务" + strategy.getName() + "完成！");
        return true;
      } else {

        log.error("策略" + strategy.getName() + "找不到对应的处理类");
        return false;
      }
    } catch (Exception ex) {
      log.error("定时任务初始化异常：策略id为:" + strategy.getId() + "的任务初始化异常！");
      return false;
    }
  }

  /**
   * 更新定时任务信息
   */
  public void updateQuartzJob(final Scheduler scheduler, final ObjectPo strategy) throws Exception {

    if (scheduler == null) {
      log.error("定时任务调度器不能为null");
      return;
    }

    if (strategy == null) {
      log.error("定时任务策略不能为空");
      return;
    }

    final TaskDetail taskDetail = QuartzTaskManager.getInstance().getTask(strategy.getId());

    if (taskDetail != null) {
      log.info("更新任务" + strategy.getName() + "开始！");

      //构建触发器
      final CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(strategy.getCronExpression());
      CronTrigger trigger = (CronTrigger) scheduler.getTrigger(taskDetail.getTrigger().getKey());
      trigger = trigger.getTriggerBuilder().withDescription(strategy.getName())
          .withIdentity(strategy.getName())
          .withSchedule(scheduleBuilder)
          .startAt(strategy.getTriggerTime().toDate())
          .endAt(strategy.getEndTriggerTime().toDate())
          .build();

      //将策略信息放到jobDataMap中，传到job中使用
      final JobDataMap jobDataMap = new JobDataMap();
      //目前这里只有短信、红包
      jobDataMap.putIfAbsent("strategy", strategy);

      final JobDetail jobDetail = scheduler.getJobDetail(taskDetail.getJobDetail().getKey())
          .getJobBuilder()
          .withIdentity(String.valueOf(strategy.getId()))
          .withDescription(strategy.getName())
          .setJobData(jobDataMap)
          .build();

      scheduler.scheduleJob(jobDetail, trigger);
      log.info("更新任务" + strategy.getName() + "完成！");
    } else {

      this.addQuartzJob(scheduler, strategy);
    }

  }

  /**
   * 删除定时任务信息
   */
  public void delQuartzJob(final Scheduler scheduler, final ObjectPo strategy) throws Exception {

    if (scheduler == null) {
      log.error("定时任务调度器不能为null");
      return;
    }

    if (strategy == null) {
      log.error("定时任务策略不能为空");
      return;
    }

    final TaskDetail taskDetail = QuartzTaskManager.getInstance().getTask(strategy.getId());

    if (taskDetail != null) {
      log.info("更新任务" + strategy.getName() + "开始！");

      scheduler.deleteJob(taskDetail.getJobDetail().getKey());
      log.info("删除任务" + strategy.getName() + "完成！");
    } else {

      log.error("quartz task error:任务信息为空，无法删除任务信息");
    }
  }

  /**
   * 匹配job类型
   */
  private Class genJobClass(final StrategyType type) {

    Class targetClass = null;
    switch (type) {
      case SMS:
        targetClass = SendSmsJob.class;
        break;
      case EMAIL:
        break;
      case TELEMARKETING:
        break;
      case ACTIVITY:
        break;
      case PUSH:
        break;
      case MACHINE_LEARNING:
        break;
      case RED_PACKETS_MARKETING:
        targetClass = SendRedPacketsJob.class;
        break;
      default:
    }
    return targetClass;
  }
}