package group.idealworld.dew.core.quartz;


import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootApplication
@SpringBootTest
public class quartzTest {

    private static final Logger log = LoggerFactory.getLogger(quartzTest.class);

    @Autowired
    QuartzTemplate template;

    @Test
    public void test() throws InterruptedException {
        testQuartz();
        testRunOnce();
        startAllJob();
        deleteJob();
    }


    public void testQuartz() throws InterruptedException {
        template.addJob("group.idealworld.dew.core.quartz.TfCommandJob", "myJob", "default", "0/1 * * * * ?", null);
        Thread.sleep(3000);
        template.pauseJob("myJob","default");
        Thread.sleep(3000);
        template.resumeJob("myJob","default");
        Thread.sleep(3000);
    }


    public void testRunOnce() throws InterruptedException {
        template.runOnce("myJob","default");
    }


    public void startAllJob() throws InterruptedException {
        template.startAllJobs();
        Thread.sleep(5000);
    }


    public void deleteJob(){
        log.info("deleteJob");
        template.deleteJob("myJob","default");
    }

}
