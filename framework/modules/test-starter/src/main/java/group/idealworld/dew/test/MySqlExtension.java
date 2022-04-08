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

package group.idealworld.dew.test;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.File;

public class MySqlExtension implements BeforeAllCallback {

    private static final Logger logger = LoggerFactory.getLogger(MySqlExtension.class);

//    private static JdbcDatabaseContainer mysqlContainer = new MySQLContainer(DockerImageName.parse("8").asCompatibleSubstituteFor("mysql"));

    private static MySQLContainer mysqlContainer = (MySQLContainer) new MySQLContainer("mysql:8.0.11").withDatabaseName("test")
            .withUsername("test").withPassword("test").withEnv("MYSQL_ROOT_HOST", "%");

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        var scriptPath = ClassLoader.getSystemResource("").getPath() + "/sql/init.sql";
        if (new File(scriptPath).exists()) {
            mysqlContainer.withInitScript("init.sql");
        }
        mysqlContainer.withCommand("--max_allowed_packet=10M");
        mysqlContainer.start();
        logger.info("Test mysql port: " + mysqlContainer.getFirstMappedPort()
                + ", username: " + mysqlContainer.getUsername() + ", password: " + mysqlContainer.getPassword());
    }

    public static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=jdbc:mysql://127.0.0.1:" + mysqlContainer.getFirstMappedPort() + "/test?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=true",
                    "spring.datasource.username=" + mysqlContainer.getUsername(),
                    "spring.datasource.password=" + mysqlContainer.getPassword(),
                    "spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver"
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

}
