package com.stdp.start.canal.annotation;

import com.stdp.start.canal.config.CanalClientConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({
//        CanalConfig.class,
        CanalClientConfiguration.class})
public @interface EnableCanalClient {

}
