package cn.com.dyj.quartz.single.configure;

import org.quartz.spi.JobFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.Properties;

/**
 *
 */
@Configuration
public class QuartzConfiguration {

  @Bean
  public JobFactory jobFactory(final ApplicationContext applicationContext) {
    final AutoWiringSpringBeanJobFactory jobFactory = new AutoWiringSpringBeanJobFactory();
    jobFactory.setApplicationContext(applicationContext);
    return jobFactory;
  }

  /**
   * 设置属性
   *
   * @return 返回属性配置类
   */
  private Properties quartzProperties() {

    final Properties prop = new Properties();
    //Configure Main Scheduler Properties
    prop.put("org.quartz.scheduler.instanceName", "DefaultQuartzScheduler");
    prop.put("org.quartz.scheduler.wrapJobExecutionInUserTransaction", "false");
    prop.put("org.quartz.scheduler.instanceId", "AUTO");

    //Configure rmi
    prop.put("org.quartz.scheduler.rmi.export", "false");
    prop.put("org.quartz.scheduler.rmi.proxy", "false");

    //Configure ThreadPool
    prop.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
    prop.put("org.quartz.threadPool.threadCount", "50");
    prop.put("org.quartz.threadPool.threadPriority", "5");
    prop.put("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread", "true");


    return prop;
  }

  /**
   * attention: Details：定义quartz调度工厂.
   */
  @Bean
  public SchedulerFactoryBean schedulerFactoryBean(final JobFactory jobFactory) {
    final SchedulerFactoryBean bean = new SchedulerFactoryBean();
    bean.setJobFactory(jobFactory);
    bean.setQuartzProperties(quartzProperties());
    bean.setAutoStartup(true);
    return bean;
  }


}
