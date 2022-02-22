package group.idealworld.dew.core.quartz;


import org.junit.Assert;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuartzTest {

    private static final Logger log = LoggerFactory.getLogger(QuartzTest.class);


    public void test(QuartzTemplate template) throws InterruptedException, SchedulerException {
        testQuartz(template);
        testRunOnce(template);
        startAllJob(template);
        deleteJob(template);
    }


    public void testQuartz(QuartzTemplate template) throws InterruptedException, SchedulerException {
        template.addJob("group.idealworld.dew.core.quartz.TfCommandJob", "myJob", "0/1 * * * * ?", null);
        Assert.assertTrue(template.checkJob("myJob"));
        template.pauseJob("myJob");
        Assert.assertEquals("PAUSED",template.getJobState("myJob"));
        template.resumeJob("myJob");
        Assert.assertEquals("NORMAL",template.getJobState("myJob"));
    }


    public void testRunOnce(QuartzTemplate template) throws InterruptedException, SchedulerException {
        template.runOnce("myJob");
        Assert.assertEquals("NORMAL",template.getJobState("myJob"));
    }


    public void startAllJob(QuartzTemplate template) throws InterruptedException, SchedulerException {
        template.startAllJobs();
        Thread.sleep(1000);
        Assert.assertEquals("NORMAL",template.getJobState("myJob"));
    }


    public void deleteJob(QuartzTemplate template) throws SchedulerException {
        template.deleteJob("myJob");
        Assert.assertFalse(template.checkJob("myJob"));
    }

}
