package group.idealworld.dew.core.hi.process;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Hi 引用注解，用于弱实现，多用于静态方法处理.
 *
 * @author gudoxuri
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HiRef {

    /**
     * 引用接口.
     *
     * @return the string
     */
    Class<?> value();

}
