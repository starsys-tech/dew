package group.idealworld.dew.core.hi.ext;

import group.idealworld.dew.core.hi.Hi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;


/**
 * The type Hi auto configuration.
 *
 * @author gudaoxuri
 */
@Configuration
@ComponentScan(basePackageClasses = {Hi.class})
public class HiAutoConfiguration {

    @Autowired
    private HiConfig hiConfig;
    @Autowired
    private ApplicationContext injectApplicationContext;

    @PostConstruct
    private void init() {
        SpringBeanDetector.applicationContext = injectApplicationContext;
        Hi.init(hiConfig.getBasicPackage());
    }

}
