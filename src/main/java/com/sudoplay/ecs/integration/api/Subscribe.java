package com.sudoplay.ecs.integration.api;

import com.sudoplay.ecs.integration.spi.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Subscribe {

  Class<? extends Component>[] all() default {};

  Class<? extends Component>[] one() default {};

  Class<? extends Component>[] exclude() default {};

  int priority() default 0;

}
