package group.idealworld.dew.core.hi.ext;



import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

/**
 * Spring Bean探测器.
 *
 * @author gudaoxuri
 */
@Configuration
public class SpringBeanDetector {

    protected static ApplicationContext applicationContext;

    /**
     * 获取一个Bean实例.
     *
     * @param clazz Bean对应的类
     * @return Bean实例
     */
    public static Optional<Object> getBean(Class<?> clazz) {
        try {
            return Optional.of(applicationContext.getBean(clazz));
        } catch (BeansException beansException) {
            return Optional.empty();
        }
    }

}
