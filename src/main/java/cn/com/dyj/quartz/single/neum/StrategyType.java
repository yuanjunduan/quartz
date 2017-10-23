package cn.com.dyj.quartz.single.neum;

/**
 * Created by liufei on 2017/8/14.
 */
public enum StrategyType {

  SMS("短信", "sms"),
  EMAIL("邮箱", "email"),
  TELEMARKETING("外呼", "telemarketing"),
  ACTIVITY("活动营销", "activity"),
  PUSH("APP 推送", "push"),
  MACHINE_LEARNING("机器学习", "machine_learning"),
  RED_PACKETS_MARKETING("红包营销", "red_packets_marketing");

  private String name;
  private String type;

  // 构造方法
  StrategyType(final String name, final String type) {
    this.name = name;
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public static String getName(final String type) {
    for (final StrategyType status : StrategyType.values()) {
      if (status.getType().equals(type)) {
        return status.name;
      }
    }
    return null;
  }

  public String getType() {
    return type;
  }

  public static String getType(final String name) {
    for (final StrategyType status : StrategyType.values()) {
      if (status.getName().equals(name)) {
        return status.getType();
      }
    }
    return null;
  }

  public static StrategyType getStrategyType(final String name) {
    for (final StrategyType status : StrategyType.values()) {
      if (status.getType().equalsIgnoreCase(name)) {
        return status;
      }
    }
    return null;
  }
}
