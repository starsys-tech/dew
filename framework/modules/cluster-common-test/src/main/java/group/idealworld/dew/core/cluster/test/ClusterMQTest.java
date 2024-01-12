package group.idealworld.dew.core.cluster.test;

import group.idealworld.dew.core.cluster.Cluster;
import group.idealworld.dew.core.cluster.ClusterMQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Cluster mq test.
 *
 * @author gudaoxuri
 */
public class ClusterMQTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterMQTest.class);

    /**
     * Test.
     *
     * @param mq the mq
     * @throws InterruptedException the interrupted exception
     */
    public void test(ClusterMQ mq) throws InterruptedException {
        testPubSub(mq);
        testReqResp(mq);
        testHA(mq);
    }

    /**
     * Test pub sub.
     *
     * @param mq the mq
     * @throws InterruptedException the interrupted exception
     */
    public void testPubSub(ClusterMQ mq) throws InterruptedException {
        CountDownLatch waiting = new CountDownLatch(40);
        new Thread(() -> mq.subscribe("test_pub_sub", message -> {
            assert message.getBody().contains("msg");
            if (message.getBody().contains("msgA") && mq.supportHeader()) {
                assert message.getHeader().get().containsKey("h");
            }
            LOGGER.info("subscribe instance 1: pub_sub>>" + message);
            waiting.countDown();
        })).start();
        new Thread(() -> mq.subscribe("test_pub_sub", message -> {
            assert message.getBody().contains("msg");
            if (message.getBody().contains("msgA") && mq.supportHeader()) {
                assert message.getHeader().get().containsKey("h");
            }
            LOGGER.info("subscribe instance 2: pub_sub>>" + message);
            waiting.countDown();
        })).start();
        Thread.sleep(2000);
        for (int i = 0; i < 10; i++) {
            mq.publish("test_pub_sub", "msgA" + i, new HashMap<>() {
                {
                    put("h", "001");
                }
            });
            mq.publish("test_pub_sub", "msgB" + i);
        }
        waiting.await();
    }

    /**
     * Test req resp.
     *
     * @param mq the mq
     * @throws InterruptedException the interrupted exception
     */
    public void testReqResp(ClusterMQ mq) throws InterruptedException {
        CountDownLatch waiting = new CountDownLatch(20);
        List<String> conflictFlag = new ArrayList<>();
        new Thread(() -> mq.response("test_rep_resp", message -> {
            if (conflictFlag.contains(message.getBody())) {
                assert false;
            } else {
                if (message.getBody().contains("msgA") && mq.supportHeader()) {
                    assert message.getHeader().get().containsKey("h");
                }
                conflictFlag.add(message.getBody());
                LOGGER.info("response instance 1: req_resp>>" + message);
                waiting.countDown();
            }
        })).start();
        new Thread(() -> mq.response("test_rep_resp", message -> {
            if (conflictFlag.contains(message.getBody())) {
                assert false;
            } else {
                if (message.getBody().contains("msgA") && mq.supportHeader()) {
                    assert message.getHeader().get().containsKey("h");
                }
                conflictFlag.add(message.getBody());
                LOGGER.info("response instance 2: req_resp>>" + message);
                waiting.countDown();
            }
        })).start();
        Thread.sleep(1000);
        for (int i = 0; i < 10; i++) {
            mq.request("test_rep_resp", "msgA" + i, new HashMap<>() {
                {
                    put("h", "001");
                }
            });
            mq.request("test_rep_resp", "msgB" + i);
        }
        waiting.await();
    }

    /**
     * Test ha.
     *
     * @param mq the mq
     * @throws InterruptedException the interrupted exception
     */
    public void testHA(ClusterMQ mq) throws InterruptedException {
        Cluster.ha();
        CountDownLatch waitingOccurError = new CountDownLatch(1);
        Thread mockErrorThread = new Thread(() -> mq.subscribe("test_ha", message -> {
            LOGGER.info("subscribe instance: pub_sub_ha>>" + message);
            waitingOccurError.countDown();
            if (waitingOccurError.getCount() == 0) {
                throw new RuntimeException("Mock Some Error");
            }
        }));
        mockErrorThread.start();
        Thread.sleep(1000);
        mq.publish("test_ha", "ha_msgA");
        waitingOccurError.await();
        var jdKVersion = System.getProperty("java.version");
        if (!jdKVersion.startsWith("19") && !jdKVersion.startsWith("20") && !jdKVersion.startsWith("21")) {
            mockErrorThread.stop();
        }
        // restart subscribe
        CountDownLatch waiting = new CountDownLatch(2);
        new Thread(() -> mq.subscribe("test_ha", message -> {
            LOGGER.info("subscribe new instance: pub_sub_ha>>" + message);
            waiting.countDown();
        })).start();
        Thread.sleep(1000);
        mq.publish("test_ha", "ha_msgB");
        waiting.await();
    }

}
