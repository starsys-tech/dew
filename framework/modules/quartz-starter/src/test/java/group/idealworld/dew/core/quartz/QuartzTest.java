package group.idealworld.dew.core.quartz;


import org.junit.jupiter.api.Test;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

//@SpringBootApplication
//@SpringBootTest
public class QuartzTest {

    private static final Logger log = LoggerFactory.getLogger(QuartzTest.class);

//    @Autowired
//    QuartzTemplate template;

//    @Test
    public void test(QuartzTemplate template) throws InterruptedException, SchedulerException {
        testQuartz(template);
        testRunOnce(template);
        startAllJob(template);
        deleteJob(template);
    }


    public void testQuartz(QuartzTemplate template) throws InterruptedException, SchedulerException {
        template.addJob("group.idealworld.dew.core.quartz.TfCommandJob", "myJob", "0/1 * * * * ?", null);
        assert template.checkJob("myJob");
        template.pauseJob("myJob");
        assert "PAUSED".equals(template.getJobState("myJob"));
        template.resumeJob("myJob");
        assert "NORMAL".equals(template.getJobState("myJob"));
    }


    public void testRunOnce(QuartzTemplate template) throws InterruptedException, SchedulerException {
        template.runOnce("myJob");
        assert "NORMAL".equals(template.getJobState("myJob"));
    }


    public void startAllJob(QuartzTemplate template) throws InterruptedException, SchedulerException {
        template.startAllJobs();
        Thread.sleep(1000);
        assert "NORMAL".equals(template.getJobState("myJob"));
    }


    public void deleteJob(QuartzTemplate template) throws SchedulerException {
        template.deleteJob("myJob");
        assert false == template.checkJob("myJob");
    }

}
