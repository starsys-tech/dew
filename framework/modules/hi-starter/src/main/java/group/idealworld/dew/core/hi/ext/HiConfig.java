package group.idealworld.dew.core.hi.ext;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * The type Hi config.
 *
 * @author gudaoxuri
 */
@Component
@ConfigurationProperties(prefix = "hive.hi")
public class HiConfig {

    private String basicPackage = "cn.com";

    /**
     * Gets basic package.
     *
     * @return the basic package
     */
    public String getBasicPackage() {
        return basicPackage;
    }

    /**
     * Sets basic package.
     *
     * @param basicPackage the basic package
     */
    public void setBasicPackage(String basicPackage) {
        this.basicPackage = basicPackage;
    }
}
