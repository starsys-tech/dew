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


    @Bean
    public QuartzTemplate quartzTemplate(Scheduler scheduler){
        return new QuartzTemplate(scheduler);
    }

    @Bean
    public SimpleSchedulerListener simpleSchedulerListener(){
        return new SimpleSchedulerListener();
    }


}