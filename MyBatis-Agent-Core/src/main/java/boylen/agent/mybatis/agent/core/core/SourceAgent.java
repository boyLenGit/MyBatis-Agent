package boylen.agent.mybatis.agent.core.core;

import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Inherited
public @interface SourceAgent {
    String database() default "";
}
