import cn.com.dyj.quartz.QuartzApplication;
import cn.com.dyj.quartz.jiqun.model.ModelPo;
import cn.com.dyj.quartz.jiqun.task.TaskInfo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobDataMap;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;

/**
 * duanyuanjun 2017/10/23 15:37
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = QuartzApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@WebAppConfiguration
public class QuartzTest extends Assert {

  protected MockMvc mockMvc;
  @Resource
  protected WebApplicationContext wac;
  //@Resource
  //protected FilterChainProxy filterChainProxy;
  protected ObjectMapper objectMapper = new ObjectMapper();

  @Before()
  public void initMock() {

    objectMapper.registerModule(new JodaModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();//addFilters(filterChainProxy)
  }


  @Test
  public void addTask() throws Exception {

    final ModelPo modelPo = ModelPo.builder().id(1L).name("自定义业务类型").build();
    final JobDataMap jobDataMap = new JobDataMap();
    jobDataMap.put("modelPo", modelPo);


    final TaskInfo taskInfo = TaskInfo.builder()
        .cronExpression("0 0/1 * * * ? ")//每分钟执行一次
        .jobDescription("测试添加定时任务")
        //下面二个必填
        .jobName("cn.com.dyj.quartz.jiqun.task.ScheduledTest")
        .jobGroup("groupName")
        .jobDataMap(jobDataMap)
        .build();


    final String contentAsString = mockMvc.perform(
        MockMvcRequestBuilders
            .post("/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            //.headers(getHttpHeader())
            .content(objectMapper.writeValueAsString(taskInfo)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();
  }
}
