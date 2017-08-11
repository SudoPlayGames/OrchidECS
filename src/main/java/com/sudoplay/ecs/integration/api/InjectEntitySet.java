package com.sudoplay.ecs.integration.api;

import com.sudoplay.ecs.integration.spi.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectEntitySet {

  Class<? extends Component>[] all() default {};

  Class<? extends Component>[] one() default {};

  Class<? extends Component>[] exclude() default {};

}
