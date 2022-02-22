package group.idealworld.dew.core.quartz;

import org.quartz.SchedulerException;

import java.util.Map;

public interface QuartzOperation {
    /**
     * 添加任务可以传参数
     *
     * @param clazzName
     * @param jobKey
     * @param cronExp
     * @param param
     */
    void addJob(String clazzName, String jobKey, String cronExp, Map<String, Object> param);

    /**
     * 暂停任务
     *
     * @param jobKey
     */
    void pauseJob(String jobKey);

    /**
     * 恢复任务
     *
     * @param jobKey
     */
    void resumeJob(String jobKey);

    /**
     * 立即运行一次定时任务
     *
     * @param jobKey
     */
    void runOnce(String jobKey);

    /**
     * 更新任务
     *
     * @param jobKey
     * @param cronExp
     * @param param
     */
    void updateJob(String jobKey, String cronExp, Map<String, Object> param);

    /**
     * 删除任务
     *
     * @param jobKey
     */
    void deleteJob(String jobKey);

    /**
     * 启动所有任务
     */
    void startAllJobs();

    /**
     * 暂停所有任务
     */
    void pauseAllJobs();

    /**
     * 恢复所有任务
     */
    void resumeAllJobs();

    /**
     * 关闭所有任务
     */
    void shutdownAllJobs();

    boolean checkJob(String jobKey) throws SchedulerException;

}
