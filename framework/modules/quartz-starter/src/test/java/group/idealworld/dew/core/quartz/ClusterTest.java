/*
 * Copyright 2021. the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package group.idealworld.dew.core.quartz;

import group.idealworld.dew.test.MySqlExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Cluster test.
 *
 * @author nipeixuan
 */
@ExtendWith({SpringExtension.class, MySqlExtension.class})
@ContextConfiguration(initializers = MySqlExtension.Initializer.class)
@SpringBootApplication
@SpringBootTest
@Testcontainers
public class ClusterTest {

    @Autowired
    private QuartzTemplate quartzTemplate;

    /**
     * Test mq.
     *
     * @throws InterruptedException the interrupted exception
     */
    @Test
    public void testMQ() throws InterruptedException, SchedulerException {
        new QuartzTest().testQuartz(quartzTemplate);
    }

}
