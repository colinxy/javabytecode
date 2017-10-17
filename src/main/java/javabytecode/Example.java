package javabytecode;

import java.lang.reflect.InvocationTargetException;


class Example {
    public static void main(String[] args)
        throws ClassNotFoundException
        , InstantiationException
        , IllegalAccessException
        , NoSuchMethodException
        , InvocationTargetException {
        System.out.println("Hello world");
        System.out.println();

        CustomClassLoader loader1 =
            new CustomClassLoader(Example.class.getClassLoader());
        Class<?> clazz1 = loader1.loadClass("javabytecode.StaticAccessor");
        Object instance1 = clazz1.newInstance();
        clazz1.getMethod("runMe").invoke(instance1);

        CustomClassLoader loader2 =
            new CustomClassLoader(Example.class.getClassLoader());
        Class<?> clazz2 = loader2.loadClass("javabytecode.StaticAccessor");
        Object instance2 = clazz2.newInstance();
        clazz2.getMethod("runMe").invoke(instance2);
    }
}
