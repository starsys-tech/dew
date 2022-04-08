package group.idealworld.dew.core.quartz;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class QuartzTemplate implements QuartzOperation {

    private static final Logger log = LoggerFactory.getLogger(QuartzTemplate.class);

    private Scheduler scheduler;

    public QuartzTemplate(Scheduler scheduler){
        this.scheduler = scheduler;
    }

    @Override
    public void addJob(String clazzName, String jobkey, String cronExp, Map<String, Object> param) {
        try {
//            scheduler.start();
            //构建job信息
            Class<? extends Job> jobClass = (Class<? extends Job>) Class.forName(clazzName);
            JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobkey).build();
            //表达式调度构建器(即任务执行的时间)
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExp);
            //按新的cronExpression表达式构建一个新的trigger
            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(jobkey).withSchedule(scheduleBuilder).build();
            //获得JobDataMap，写入数据
            if (param != null) {
                trigger.getJobDataMap().putAll(param);
            }
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            log.error("创建任务失败", e);
        }
    }

    @Override
    public void pauseJob(String jobKey) {
        try {
            scheduler.pauseJob(JobKey.jobKey(jobKey));
        } catch (SchedulerException e) {
            log.error("暂停任务失败", e);
        }
    }

    @Override
    public void resumeJob(String jobKey) {
        try {
            scheduler.resumeJob(JobKey.jobKey(jobKey));
        } catch (SchedulerException e) {
            log.error("恢复任务失败", e);
        }
    }

    @Override
    public void runOnce(String jobKey) {
        try {
            scheduler.triggerJob(JobKey.jobKey(jobKey));
        } catch (SchedulerException e) {
            log.error("立即运行一次定时任务失败", e);
        }
    }

    @Override
    public void updateJob(String jobKey, String cronExp, Map<String, Object> param) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(jobKey);
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            if (cronExp != null) {
                // 表达式调度构建器
                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExp);
                // 按新的cronExpression表达式重新构建trigger
                trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
            }
            //修改map
            if (param != null) {
                trigger.getJobDataMap().putAll(param);
            }
            // 按新的trigger重新设置job执行
            scheduler.rescheduleJob(triggerKey, trigger);
        } catch (Exception e) {
            log.error("更新任务失败", e);
        }
    }

    @Override
    public void deleteJob(String jobKey) {
        try {
            //暂停、移除、删除
            scheduler.pauseTrigger(TriggerKey.triggerKey(jobKey));
            scheduler.unscheduleJob(TriggerKey.triggerKey(jobKey));
            scheduler.deleteJob(JobKey.jobKey(jobKey));
        } catch (Exception e) {
            log.error("删除任务失败", e);
        }
    }

    @Override
    public void startAllJobs() {
        try {
            scheduler.start();
        } catch (Exception e) {
            log.error("开启所有的任务失败", e);
        }
    }

    @Override
    public void pauseAllJobs() {
        try {
            scheduler.pauseAll();
        } catch (Exception e) {
            log.error("暂停所有任务失败", e);
        }
    }

    @Override
    public void resumeAllJobs() {
        try {
            scheduler.resumeAll();
        } catch (Exception e) {
            log.error("恢复所有任务失败", e);
        }
    }

    @Override
    public void shutdownAllJobs() {
        try {

            if (!scheduler.isShutdown()) {
                scheduler.shutdown(true);
            }
        } catch (Exception e) {
            log.error("关闭所有的任务失败", e);
        }
    }

    @Override
    public boolean checkJob(String jobKey) throws SchedulerException {
        return scheduler.checkExists(new JobKey(jobKey));
    }

    public String getJobState(String jobKey) throws SchedulerException {
        return scheduler.getTriggerState(TriggerKey.triggerKey(jobKey)).toString();
    }


}
