package javabytecode;

import java.lang.reflect.Field;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtField.Initializer;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.bytecode.ClassFile;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Mnemonic;
import java.nio.file.Paths;
import javassist.bytecode.ConstPool;
import java.util.List;
import javassist.bytecode.FieldInfo;
import java.util.HashMap;
import java.util.Map;


public class Bytecode {
    final static Map<Integer, String> bytecodeTable =
        new HashMap<Integer, String>(){{
            put(ConstPool.CONST_Class, "CONST_Class");
            put(ConstPool.CONST_Double, "CONST_Double");
            put(ConstPool.CONST_Fieldref, "CONST_Fieldref");
            put(ConstPool.CONST_Float, "CONST_Float");
            put(ConstPool.CONST_Integer, "CONST_Integer");
            put(ConstPool.CONST_InterfaceMethodref, "CONST_InterfaceMethodref");
            put(ConstPool.CONST_InvokeDynamic, "CONST_InvokeDynamic");
            put(ConstPool.CONST_Long, "CONST_Long");
            put(ConstPool.CONST_MethodHandle, "CONST_MethodHandle");
            put(ConstPool.CONST_Methodref, "CONST_Methodref");
            put(ConstPool.CONST_MethodType, "CONST_MethodType");
            put(ConstPool.CONST_Methodref, "CONST_Methodref");
            put(ConstPool.CONST_Module, "CONST_Module");
            put(ConstPool.CONST_NameAndType, "CONST_NameAndType");
            put(ConstPool.CONST_Package, "CONST_Package");
            put(ConstPool.CONST_String, "CONST_String");
            put(ConstPool.CONST_Utf8, "CONST_Utf8");
        }};

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
        CtClass cc = pool.get("javabytecode.RewriteMe1");

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

    public static void constantsPool(String... args) throws Exception {
        final String className = "javabytecode.RewriteMe2";

        // System.out.println(Paths.get(".").toAbsolutePath().normalize());

        InputStream fin = Bytecode.class.getClassLoader()
            .getResourceAsStream(
                className.replace('.', File.separatorChar) + ".class");
        ClassFile cf = new ClassFile(new DataInputStream(fin));
        ConstPool constPool = cf.getConstPool();

        // constPool.print();
        final int entries = constPool.getSize();
        // from 1 to (entries-1)
        for (int i = 1; i < entries; i++) {
            int tag = constPool.getTag(i);
            System.out.println(bytecodeTable.get(tag));
        }
    }

    public static void methodBytecode(String... args) throws Exception {
        final String className = "javabytecode.RewriteMe2";

        InputStream fin = Bytecode.class.getClassLoader()
            .getResourceAsStream(
                className.replace('.', File.separatorChar) + ".class");
        ClassFile cf = new ClassFile(new DataInputStream(fin));

        List<FieldInfo> fields = cf.getFields();
        for (FieldInfo finfo : fields) {
            System.out.println(finfo.getName() + ": "
                               + finfo.getDescriptor());
        }

        List<MethodInfo> methods = cf.getMethods();
        for (MethodInfo minfo : methods) {
            System.out.println("==> At method " + minfo.getName());
            CodeAttribute ca = minfo.getCodeAttribute();
            CodeIterator ci = ca.iterator();

            while (ci.hasNext()) {
                int index = ci.next();
                int op = ci.byteAt(index);
                System.out.println(Mnemonic.OPCODE[op]);
            }
        }
    }
}
