package com.dingding.open.achelous.core;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class Factory implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public enum Type {
        SPRING, NON_SPRING
    };

    public static <M> M getEntity(String name) {
        if (name.contains(".")) {
            return getEntity(Type.NON_SPRING, name);
        } else {
            return getEntity(Type.SPRING, name);
        }
    }

    @SuppressWarnings("unchecked")
    public static <M> M getEntity(Type type, String name) {
        switch (type) {
            case NON_SPRING:
                try {
                    return (M) Class.forName(name).newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case SPRING:
                return (M) applicationContext.getBean(name);
            default:
                break;
        }
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("static-access")
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

    }
}
