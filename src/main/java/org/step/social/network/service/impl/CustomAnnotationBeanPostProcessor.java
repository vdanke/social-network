package org.step.social.network.service.impl;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;
import org.step.social.network.service.CustomAnnotation;

@Component
public class CustomAnnotationBeanPostProcessor implements BeanPostProcessor {

    private final ConfigurableListableBeanFactory factoryPostProcessor;

    @Autowired
    public CustomAnnotationBeanPostProcessor(ConfigurableListableBeanFactory factoryPostProcessor) {
        this.factoryPostProcessor = factoryPostProcessor;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
//        Map<String, Object> beansWithAnnotation = factoryPostProcessor.getBeansWithAnnotation(CustomAnnotation.class);
//
//        CustomClass myCustomClass = (CustomClass) beansWithAnnotation.get("myCustomClass");
//
//        System.out.println(myCustomClass.getClass().getSimpleName());
//
//        String[] beanDefinitionNames = factoryPostProcessor.getBeanDefinitionNames();
//
//        Arrays.stream(beanDefinitionNames).forEach(System.out::println);

        boolean isPresent = bean.getClass().isAnnotationPresent(CustomAnnotation.class);

        if (isPresent) {
            System.out.println("Before initialization " + beanName);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        boolean isPresent = bean.getClass().isAnnotationPresent(CustomAnnotation.class);

        if (isPresent) {
            System.out.println("After initialization " + beanName);
        }
        return bean;
    }
}
