/**
   Runtime Constant Pool
   https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-5.html#jvms-5.1

   The Java Virtual Machine maintains a per-type constant pool
   (§2.5.5), a run-time data structure that serves many of the
   purposes of the symbol table of a conventional programming language
   implementation.

   The constant_pool table (§4.4) in the binary representation of a
   class or interface is used to construct the run-time constant pool
   upon class or interface creation (§5.3). All references in the
   run-time constant pool are initially symbolic. The symbolic
   references in the run-time constant pool are derived from
   structures in the binary representation of the class or interface
   as follows:

   A symbolic reference to a class or interface is derived from a
   CONSTANT_Class_info structure (§4.4.1) in the binary representation
   of a class or interface. Such a reference gives the name of the
   class or interface in the form returned by the Class.getName
   method, that is:

   For a nonarray class or an interface, the name is the binary name
   (§4.2.1) of the class or interface.

   For an array class of n dimensions, the name begins with n
   occurrences of the ASCII "[" character followed by a representation
   of the element type:

   If the element type is a primitive type, it is represented by the
   corresponding field descriptor (§4.3.2).

   Otherwise, if the element type is a reference type, it is
   represented by the ASCII "L" character followed by the binary name
   (§4.2.1) of the element type followed by the ASCII ";" character.

   Whenever this chapter refers to the name of a class or interface,
   it should be understood to be in the form returned by the
   Class.getName method.

   A symbolic reference to a field of a class or an interface is
   derived from a CONSTANT_Fieldref_info structure (§4.4.2) in the
   binary representation of a class or interface. Such a reference
   gives the name and descriptor of the field, as well as a symbolic
   reference to the class or interface in which the field is to be
   found.

   A symbolic reference to a method of a class is derived from a
   CONSTANT_Methodref_info structure (§4.4.2) in the binary
   representation of a class or interface. Such a reference gives the
   name and descriptor of the method, as well as a symbolic reference
   to the class in which the method is to be found.

   A symbolic reference to a method of an interface is derived from a
   CONSTANT_InterfaceMethodref_info structure (§4.4.2) in the binary
   representation of a class or interface. Such a reference gives the
   name and descriptor of the interface method, as well as a symbolic
   reference to the interface in which the method is to be found.

   A symbolic reference to a method handle is derived from a
   CONSTANT_MethodHandle_info structure (§4.4.8) in the binary
   representation of a class or interface. Such a reference gives a
   symbolic reference to a field of a class or interface, or a method
   of a class, or a method of an interface, depending on the kind of
   the method handle.

   A symbolic reference to a method type is derived from a
   CONSTANT_MethodType_info structure (§4.4.9) in the binary
   representation of a class or interface. Such a reference gives a
   method descriptor (§4.3.3).

   A symbolic reference to a call site specifier is derived from a
   CONSTANT_InvokeDynamic_info structure (§4.4.10) in the binary
   representation of a class or interface. Such a reference gives:

   a symbolic reference to a method handle, which will serve as a
   bootstrap method for an invokedynamic instruction (§invokedynamic);

   a sequence of symbolic references (to classes, method types, and
   method handles), string literals, and run-time constant values
   which will serve as static arguments to a bootstrap method;

   a method name and method descriptor.

   In addition, certain run-time values which are not symbolic
   references are derived from items found in the constant_pool table:

   A string literal is a reference to an instance of class String, and
   is derived from a CONSTANT_String_info structure (§4.4.3) in the
   binary representation of a class or interface. The
   CONSTANT_String_info structure gives the sequence of Unicode code
   points constituting the string literal.

   The Java programming language requires that identical string
   literals (that is, literals that contain the same sequence of code
   points) must refer to the same instance of class String (JLS
   §3.10.5). In addition, if the method String.intern is called on any
   string, the result is a reference to the same class instance that
   would be returned if that string appeared as a literal. Thus, the
   following expression must have the value true:

   ("a" + "b" + "c").intern() == "abc"

   To derive a string literal, the Java Virtual Machine examines the
   sequence of code points given by the CONSTANT_String_info
   structure.

   If the method String.intern has previously been called on an
   instance of class String containing a sequence of Unicode code
   points identical to that given by the CONSTANT_String_info
   structure, then the result of string literal derivation is a
   reference to that same instance of class String.

   Otherwise, a new instance of class String is created containing the
   sequence of Unicode code points given by the CONSTANT_String_info
   structure; a reference to that class instance is the result of
   string literal derivation. Finally, the intern method of the new
   String instance is invoked.

   Run-time constant values are derived from CONSTANT_Integer_info,
   CONSTANT_Float_info, CONSTANT_Long_info, or CONSTANT_Double_info
   structures (§4.4.4, §4.4.5) in the binary representation of a class
   or interface.

   Note that CONSTANT_Float_info structures represent values in IEEE
   754 single format and CONSTANT_Double_info structures represent
   values in IEEE 754 double format (§4.4.4, §4.4.5). The run-time
   constant values derived from these structures must thus be values
   that can be represented using IEEE 754 single and double formats,
   respectively.

   The remaining structures in the constant_pool table of the binary
   representation of a class or interface - the
   CONSTANT_NameAndType_info and CONSTANT_Utf8_info structures
   (§4.4.6, §4.4.7) - are only used indirectly when deriving symbolic
   references to classes, interfaces, methods, fields, method types,
   and method handles, and when deriving string literals and call site
   specifiers.
*/

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

    public static void constantsPool() throws Exception {
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

    public static void constantPoolCopy() throws Exception {
        final String className = "javabytecode.RewriteMe2";

        InputStream fin = Bytecode.class.getClassLoader()
            .getResourceAsStream(
                className.replace('.', File.separatorChar) + ".class");
        ClassFile cf = new ClassFile(new DataInputStream(fin));
        ConstPool constPool = cf.getConstPool();
        constPool.print();

        ConstPool constPoolCopy = new ConstPool("javabytecode.RewriteMe2");

        // create a new const pool, add each field to it from the old pool,
        // altering the desired field on the fly
        // then change the class's const pool to use the new one

        for (int i = 1; i < constPool.getSize(); i++) {
            constPool.copy(i, constPoolCopy, null);
        }
        constPoolCopy.print();

        // modifying the constPool is really hard
        // also need to change the relevant reference in code
    }

    public static void methodBytecode() throws Exception {
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

    public static void methodRef() throws Exception {
        final String className = "javabytecode.RewriteMe2";

        InputStream fin = Bytecode.class.getClassLoader()
            .getResourceAsStream(
                className.replace('.', File.separatorChar) + ".class");
        ClassFile cf = new ClassFile(new DataInputStream(fin));
        ConstPool constPool = cf.getConstPool();
        // constPool.print();

        List<MethodInfo> methods = cf.getMethods();

        // method:
        // InterfaceMethodref
        // MethodHandle
        // Methodref
        // MethodType

        for (MethodInfo minfo : methods) {
            System.out.println("==> At method " + minfo.getName());
            CodeAttribute ca = minfo.getCodeAttribute();
            CodeIterator ci = ca.iterator();

            while (ci.hasNext()) {
                int index = ci.next();
                int op = ci.byteAt(index);

                int constPoolIndex;
                switch (op) {
                case 0xba:      // invokedynamic
                    // duck typing?
                    break;
                case 0xb9:      // invokeinterface
                    break;
                case 0xb7:      // invokespecial
                    constPoolIndex = ci.u16bitAt(index+1);
                    System.out.println("invokespecial " + constPoolIndex
                                       + ": " + getFieldOrMethod(constPool, constPoolIndex));
                    break;
                case 0xb8:      // invokestatic
                    break;
                case 0xb6:      // invokevirtual
                    constPoolIndex = ci.u16bitAt(index+1);
                    System.out.println("invokevirtual " + constPoolIndex
                                       + ": " + getFieldOrMethod(constPool, constPoolIndex));
                    break;
                }
            }
        }
    }

    private static String getFieldOrMethod(ConstPool constPool, int constPoolIndex) {
        final int tag = constPool.getTag(constPoolIndex);

        String className;
        String name;
        String type;
        String repr;
        switch (tag) {
        case ConstPool.CONST_Methodref:
            className = constPool.getMethodrefClassName(constPoolIndex);
            name = constPool.getMethodrefName(constPoolIndex);
            type = constPool.getMethodrefType(constPoolIndex);
            repr = className + ":" + name + ":" + type;
            break;
        case ConstPool.CONST_Fieldref:
            className = constPool.getFieldrefClassName(constPoolIndex);
            name = constPool.getFieldrefName(constPoolIndex);
            type = constPool.getFieldrefType(constPoolIndex);
            repr = className + ":" + name + ":" + type;
            break;
        default:
            repr = "UNHANDLED TAG: " + bytecodeTable.get(tag);
        }

        return repr;
    }

    public static void fieldRef() throws Exception {
        final String className = "javabytecode.RewriteMe2";

        InputStream fin = Bytecode.class.getClassLoader()
            .getResourceAsStream(
                className.replace('.', File.separatorChar) + ".class");
        ClassFile cf = new ClassFile(new DataInputStream(fin));
        ConstPool constPool = cf.getConstPool();

        List<FieldInfo> fields = cf.getFields();
        List<MethodInfo> methods = cf.getMethods();

        for (MethodInfo minfo : methods) {
            System.out.println("==> At method " + minfo.getName());
            CodeAttribute ca = minfo.getCodeAttribute();
            CodeIterator ci = ca.iterator();

            while (ci.hasNext()) {
                int index = ci.next();
                int op = ci.byteAt(index);

                int constPoolIndex;
                switch (op) {
                case 0xb4:      // getfield
                    constPoolIndex = ci.u16bitAt(index+1);
                    System.out.println("getfield " + constPoolIndex
                                       + ": " + getFieldOrMethod(constPool, constPoolIndex));
                    break;
                }
            }
        }
    }
}
