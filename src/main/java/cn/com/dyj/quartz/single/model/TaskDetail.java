package cn.com.dyj.quartz.single.model;

import lombok.Builder;
import lombok.Data;

import org.quartz.JobDetail;
import org.quartz.Trigger;

/**
 * Created by liufei on 2017/8/15.
 */
@Data
@Builder
public class TaskDetail {

  private Trigger trigger;
  private JobDetail jobDetail;
}
