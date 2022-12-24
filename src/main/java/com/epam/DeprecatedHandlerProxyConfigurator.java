package com.epam;

import net.sf.cglib.proxy.Enhancer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Evgeny Borisov
 */
public class DeprecatedHandlerProxyConfigurator implements ProxyConfigurator {
    @Override
    public Object replaceWithProxyIfNeeded(Object t, Class implClass) {
        //todo make support for @Deprecate above methods, not class
        List<Method> methodsWithDeprecatedAnnotation;

        if (implClass.isAnnotationPresent(Deprecated.class)) {
            methodsWithDeprecatedAnnotation = List.of(implClass.getMethods()).stream()
                    .map(this::getInterfaceMethod)
                    .collect(Collectors.toList());
        } else {
            methodsWithDeprecatedAnnotation = Arrays.stream(implClass.getMethods())
                    .filter(method -> method.isAnnotationPresent(Deprecated.class))
                    .map(this::getInterfaceMethod)
                    .collect(Collectors.toList());
        }
        if (!methodsWithDeprecatedAnnotation.isEmpty()) {
            if (implClass.getInterfaces().length == 0) {
                return Enhancer.create(implClass, (net.sf.cglib.proxy.InvocationHandler) (proxy, method, args) -> getInvocationHandlerLogic(method, args, t, methodsWithDeprecatedAnnotation));
            }

            return Proxy.newProxyInstance(implClass.getClassLoader(), implClass.getInterfaces(), (proxy, method, args) -> getInvocationHandlerLogic(method, args, t, methodsWithDeprecatedAnnotation));
        } else {
            return t;
        }

    }

    private Method getInterfaceMethod(Method method) {
        for (Class<?> anInterface : method.getDeclaringClass().getInterfaces()) {
            try {
                return anInterface.getMethod(method.getName(), method.getParameterTypes());
            } catch (NoSuchMethodException e) {
            }
        }
        return method;
    }

    private Object getInvocationHandlerLogic(Method method, Object[] args, Object t, List<Method> methodsWithDeprecatedAnnotation) throws IllegalAccessException, InvocationTargetException {
        if (methodsWithDeprecatedAnnotation.contains(method)) {

            System.out.println("********** что ж ты делаешь урод!! ");
        }
        return method.invoke(t, args);
    }
}
