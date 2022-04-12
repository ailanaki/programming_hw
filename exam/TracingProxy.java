package info.kgeorgiy.ja.yakupova;

import java.lang.reflect.*;

public class TracingProxy implements InvocationHandler {
    // :NOTE: * модификаторы доступа
    final Object real;//Исходный объект
    final Object instance;//Прокси-объект, реализующий все интерфейсы и протоколирующий все методы
    final int depth;

    TracingProxy(int depth, Object obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        this.depth = depth;
        this.real = obj;
        this.instance = Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(), this);
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects){
        System.out.println("Calling method: " + method.getName() +" on object" + real);
        if (objects != null) {
            if (objects.length != 0) {
                System.out.println("On args: ");
            }
            for (Object obj : objects) {
                System.out.println(obj);
            }
        } else {
            System.out.println("Method don't have args");
        }

        Object obj = null;
        // :NOTE: * в логирование нет указание глубины
        try {
            if (method.getReturnType().equals(Void.TYPE)) {
                method.invoke(real, objects);
                System.out.println("Void finished without exceptions");
            } else {
                obj = method.invoke(real, objects);
                System.out.println("Method finished without exceptions. Return " + obj);
            }
            if (depth > 0 && obj != null) {
                obj = new TracingProxy(depth - 1, obj);
            }
        // :NOTE: * catch(Throwable)
        } catch (Throwable e) {
            System.out.println("Method finished with exception: " + e.getCause());
            System.err.println(e.getMessage());
        }
        return obj;
    }

    Object getInstance(){
        return instance;
    }
}
