package cn.com.dyj.quartz.single.task;

import cn.com.dyj.quartz.single.model.ObjectPo;
import cn.com.dyj.quartz.single.service.TaskResultService;

import lombok.extern.slf4j.Slf4j;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.annotation.Resource;

/**
 * duanyuanjun 2017/10/17 15:03
 */
@Slf4j
public class SendRedPacketsJob implements Job {

  @Resource
  private TaskResultService taskResultService;

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

    if (jobExecutionContext == null) {
      log.error("quartz task SendRedPacketsJob error:无法获取上下文信息");
      return;
    }

    if (jobExecutionContext.getJobDetail() == null) {
      log.error("quartz task SendRedPacketsJob error:无法获取信息详细信息");
      return;
    }

    if (jobExecutionContext.getJobDetail().getJobDataMap() == null) {
      log.error("quartz task SendRedPacketsJob error:无法获取任务数据信息");
      return;
    }

    final JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
    final Object strategyObj = jobDataMap.get("strategy");

    if (strategyObj == null) {
      log.error("quartz task SendRedPacketsJob error:无法获取策略信息");
      return;
    }

    if (strategyObj instanceof ObjectPo) {

      final ObjectPo strategy = (ObjectPo) strategyObj;
      //====一====
      //防止同一条记录由多个线程同时执行 由于多台机器 时间可能不同 也会导致一条记录同一时间被多个线程执行
      final int recordNum = 0;//strategyService.updateStrategyStatus(strategy.getId(), ExecuteStatus.EXECUTING
      // .getIndex(),ExecuteStatus.NONEXEC.getIndex());

      if (recordNum == 1) {
        try {
          log.info("SendRedPacketsJob 任务id：{},名称：{}已经在当前节点执行！", strategy.getId(), strategy.getName());

          taskResultService.executeTask(strategy);

          log.info("SendRedPacketsJob 任务id：" + strategy.getId() + ",名称：" + strategy.getName() + "已经执行完成！");
        } catch (Exception ex) {

          log.error("SendRedPacketsJob 任务执行出错：");
        } finally {
          //恢复  ====一==== 之前修改的状态
          //strategyService.updateStrategyStatus(strategy.getId(), ExecuteStatus.NONEXEC.getIndex(), ExecuteStatus
          // .EXECUTING.getIndex());
        }
      } else {
        log.info("SendRedPacketsJob 任务id：{},名称：{}已经在其它节点执行！", strategy.getId(), strategy.getName());
      }

    } else {
      log.error("SendRedPacketsJob quartz task SendRedPacketsJob error:未知的任务数据类型");
    }

  }
}
