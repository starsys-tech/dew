package group.idealworld.dew.core.quartz;

import org.quartz.*;
import org.quartz.listeners.SchedulerListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleSchedulerListener extends SchedulerListenerSupport {

    private static final Logger log = LoggerFactory.getLogger(SimpleSchedulerListener.class);

    @Override
    public void jobScheduled(Trigger trigger) {
        log.info("create job" + trigger.getJobKey());
    }

    @Override
    public void jobUnscheduled(TriggerKey triggerKey) {
    }

    @Override
    public void triggerFinalized(Trigger trigger) {
    }

    @Override
    public void triggerPaused(TriggerKey triggerKey) {
    }

    @Override
    public void triggersPaused(String triggerGroup) {
    }

    @Override
    public void triggerResumed(TriggerKey triggerKey) {
    }

    @Override
    public void triggersResumed(String triggerGroup) {
    }

    @Override
    public void jobAdded(JobDetail jobDetail) {
        log.info("job add: " + jobDetail.getJobClass());
    }

    @Override
    public void jobDeleted(JobKey jobKey) {
        log.info("delete job: " + jobKey);
    }

    @Override
    public void jobPaused(JobKey jobKey) {
        log.info("job paused: " + jobKey);
    }

    @Override
    public void jobsPaused(String jobGroup) {
        log.info("jobGroup paused: " + jobGroup);
    }

    @Override
    public void jobResumed(JobKey jobKey) {
        log.info("job resumed: " + jobKey);
    }

    @Override
    public void jobsResumed(String jobGroup) {
        log.info("jobGroup resumed: " + jobGroup);
    }

    @Override
    public void schedulerError(String msg, SchedulerException cause) {
        cause.printStackTrace();
    }

    @Override
    public void schedulerInStandbyMode() {
    }

    @Override
    public void schedulerStarted() {
    }

    @Override
    public void schedulerStarting() {
    }

    @Override
    public void schedulerShutdown() {
    }

    @Override
    public void schedulerShuttingdown() {
    }

    @Override
    public void schedulingDataCleared() {
    }
}
