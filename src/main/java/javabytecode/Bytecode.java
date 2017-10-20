package javabytecode;

import java.lang.reflect.Field;
import java.util.Arrays;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtField.Initializer;
import javassist.CtMethod;
import javassist.Modifier;


public class Bytecode {
    @SuppressWarnings("unchecked")
    private static <T> Class<T> generify(Class<?> cls) {
        return (Class<T>)cls;
    }

    public static void main(String... args) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.makeClass("Point");

        CtField x = new CtField(CtClass.intType, "x", cc);
        CtField y = new CtField(CtClass.intType, "y", cc);
        x.setModifiers(Modifier.PUBLIC);
        y.setModifiers(Modifier.PUBLIC);
        cc.addField(x, CtField.Initializer.constant(0));
        cc.addField(y, CtField.Initializer.constant(0));
        CtMethod prettyprint = CtMethod.make(
            "public String toString(){return \"(\" +x+ \", \" +y+ \")\";}",
            cc);
        cc.addMethod(prettyprint);

        Class<?> Point = cc.toClass();
        Object ins = Point.newInstance();
        Field fx = Point.getField("x");
        fx.set(ins, 123);
        Field fy = Point.getField("y");
        fy.set(ins, 456);
        System.out.println(Point.getName());
        System.out.println(ins); // pretty printed

        System.out.println(Arrays.toString(cc.getFields()));
    }

    public static void fields(String... args) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.get("RewriteMe1");

        CtField[] fs = cc.getFields();
        for (CtField f : fs) {
            System.out.println(f.getName() + " " + f.getSignature());
        }

        // includes inherited methods
        // CtMethod[] ms = cc.getMethods();
        // only methods declared in this class
        CtMethod[] ms = cc.getDeclaredMethods();
        for (CtMethod m : ms) {
            System.out.println(m.getName() + " " + m.getLongName());
        }
    }
}
