package org.step.social.network.service;

import java.lang.annotation.*;

@Documented
@Target(value = {ElementType.FIELD, ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface CustomAnnotation {
}
