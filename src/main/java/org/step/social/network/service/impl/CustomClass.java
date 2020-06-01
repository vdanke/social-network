package org.step.social.network.service.impl;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.step.social.network.service.CustomAnnotation;

import javax.annotation.PostConstruct;

@Component("myCustomClass")
@CustomAnnotation
//@Scope("prototype")
public class CustomClass {

    @PostConstruct
    public void init() {
        System.out.println("Post construct is called");
    }
}
