package com.epam;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Evgeny Borisov
 */
public class ApplicationContext {
    @Setter
    private ObjectFactory factory;
    private Map<Class, Object> cache = new ConcurrentHashMap<>();
    @Getter
    private Config config;

    public ApplicationContext(Config config) {
        this.config = config;
    }

    public <T> T getObject(Class<T> type) {
        if (cache.containsKey(type)) {
            return (T) cache.get(type);
        }

        Class<? extends T> implClass = type;

        if (type.isInterface()) {
            implClass = config.getImplClass(type);
        }
        T t = factory.createObject(implClass);

        if (implClass.isAnnotationPresent(Singleton.class)) {
            cache.put(type, t);
        }

        return t;
    }

    public void initSingletons() {
        Set<Class<?>> classes = config.getScanner().getTypesAnnotatedWith(Singleton.class);
        classes.removeIf(aClass -> aClass.isAnnotationPresent(Lazy.class));
        for (Class<?> aClass : classes) {
            Class<?>[] interfaces = aClass.getInterfaces();
            if (interfaces.length == 0) {
                cache.put(aClass, factory.createObject(aClass));
            } else if (interfaces.length == 1) {
                cache.put(interfaces[0], factory.createObject(aClass));
            }
        }
    }
}
