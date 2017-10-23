package cn.com.dyj.quartz.single.configure;

import cn.com.dyj.quartz.single.model.TaskDetail;

import java.util.concurrent.ConcurrentHashMap;


public class QuartzTaskManager {

  //用于存储所有的任务信息
  private final ConcurrentHashMap<Long, TaskDetail> taskMap = new ConcurrentHashMap<>();

  private QuartzTaskManager() {
  }

  public static final QuartzTaskManager getInstance() {

    return SingletonHolder.INSTANCE;
  }

  /**
   * 通过任务策略id获取某一个定时任务信息
   */
  public TaskDetail getTask(final Long strategyId) {

    return taskMap.get(strategyId);
  }

  /**
   * 增加一个任务信息到缓存中
   */
  public void setTask(final Long strategyId, final TaskDetail tasks) {

    taskMap.put(strategyId, tasks);
  }

  private static final class SingletonHolder {
    private static final QuartzTaskManager INSTANCE = new QuartzTaskManager();
  }
}
