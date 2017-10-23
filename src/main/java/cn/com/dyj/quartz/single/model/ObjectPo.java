package cn.com.dyj.quartz.single.model;

import cn.com.dyj.quartz.single.neum.StrategyType;

import lombok.Data;

import org.joda.time.DateTime;

/**
 * duanyuanjun 2017/10/23 16:13
 */
@Data
public class ObjectPo {

  private Long id;
  private String name;
  private String cronExpression;

  private StrategyType strategyType;

  private DateTime TriggerTime;
  private DateTime endTriggerTime;

}
