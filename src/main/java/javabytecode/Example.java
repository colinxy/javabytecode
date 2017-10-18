package javabytecode;

import java.lang.reflect.InvocationTargetException;


class Example {
    public static void main(String[] args) {
        try {
            classloader();
            System.out.println("\n*** End of class loader example\n");
        } catch (Exception exc) {
            System.out.println("*** Fail to run class loader example ***\n"
                               + exc.getMessage());
        }

        try {
            Bytecode.main();
        } catch (Exception exc) {
            System.out.println("*** Fail to run bytecode example ***\n"
                               + exc.getMessage());
        }
    }

    public static void classloader()
        throws ClassNotFoundException
        , InstantiationException
        , IllegalAccessException
        , NoSuchMethodException
        , InvocationTargetException {

        System.out.println();
        System.out.println("*** Before starting class loader example");
        System.out.println();

        CustomClassLoader loader1 =
            new CustomClassLoader(Example.class.getClassLoader());
        Class<?> clazz1 = loader1.loadClass("javabytecode.StaticAccessor");
        Object instance1 = clazz1.newInstance();
        clazz1.getMethod("runMe").invoke(instance1);
        // --- Starting runMe. Static value: null
        // --- Finishing runMe. Static value: 4

        CustomClassLoader loader2 =
            new CustomClassLoader(Example.class.getClassLoader());
        Class<?> clazz2 = loader2.loadClass("javabytecode.StaticAccessor");
        Object instance2 = clazz2.newInstance();
        clazz2.getMethod("runMe").invoke(instance2);
        // --- Starting runMe. Static value: null
        // --- Finishing runMe. Static value: 4

        System.out.println("clazz1 == clazz2 is " + (clazz1 == clazz2));
        // clazz1 == clazz2 is false
    }
}
