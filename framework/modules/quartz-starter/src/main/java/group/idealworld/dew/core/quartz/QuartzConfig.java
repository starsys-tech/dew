package group.idealworld.dew.core.quartz;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.io.IOException;

@Configuration
public class QuartzConfig {

//    @Bean
//    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
//        //获取配置属性
//        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
//        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
//        //在quartz.properties中的属性被读取并注入后再初始化对象
//        propertiesFactoryBean.afterPropertiesSet();
//        SchedulerFactoryBean factory = new SchedulerFactoryBean();
//        factory.setQuartzProperties(propertiesFactoryBean.getObject());
////        factory.setJobFactory(jobFactory);//支持在JOB实例中注入其他的业务对象
//        factory.setApplicationContextSchedulerContextKey("applicationContextKey");
//        factory.setWaitForJobsToCompleteOnShutdown(true);
//        factory.setOverwriteExistingJobs(false);
//        factory.setStartupDelay(10);
//
//        return factory;
//    }

    @Bean
    public QuartzTemplate quartzTemplate(Scheduler scheduler){
        return new QuartzTemplate(scheduler);
    }

    @Bean
    public SimpleSchedulerListener simpleSchedulerListener(){
        return new SimpleSchedulerListener();
    }

//    /**
//     * 通过SchedulerFactoryBean获取Scheduler的实例
//     * @return
//     * @throws IOException
//     * @throws SchedulerException
//     */
//    @Bean(name = "scheduler")
//    public Scheduler scheduler(SimpleSchedulerListener simpleSchedulerListener) throws IOException, SchedulerException {
//        Scheduler scheduler = schedulerFactoryBean().getScheduler();
//        scheduler.getListenerManager().addSchedulerListener(simpleSchedulerListener);
//        scheduler.start();
//        return scheduler;
//    }
}