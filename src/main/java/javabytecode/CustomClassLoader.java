package javabytecode;

import java.io.*;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.bytecode.ClassFile;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.Mnemonic;
// import java.util.HashMap;


/**
 * Our custom implementation of the ClassLoader.
 * For any of classes from "javablogging" package
 * it will use its {@link CustomClassLoader#getClass()}
 * method to load it from the specific .class file. For any
 * other class it will use the super.loadClass() method
 * from ClassLoader, which will eventually pass the
 * request to the parent.
 *
 */
public class CustomClassLoader extends ClassLoader {
    // HashMap<String, Class<?>> classes;

     /**
     * Parent ClassLoader passed to this constructor
     * will be used if this ClassLoader can not resolve a
     * particular class.
     *
     * @param parent Parent ClassLoader
     *              (may be from getClass().getClassLoader())
     */
    public CustomClassLoader(ClassLoader parent) {
        super(parent);
        // classes = new HashMap<>();
    }

     /**
     * Loads a given class from .class file just like
     * the default ClassLoader. This method could be
     * changed to load the class over network from some
     * other server or from the database.
     *
     * @param name Full class name
     */
    private Class<?> getClass(String name)
        throws ClassNotFoundException {
        // We are getting a name that looks like
        // javabytecode.package.ClassToLoad
        // and we have to convert it into the .class file name
        // like javabytecode/package/ClassToLoad.class

        // Class<?> cache = classes.get(name);
        // if (cache != null) {
        //     System.out.println("*** Loading " + name + " from cache");
        //     return cache;
        // }

        String file = name
            .replace('.', File.separatorChar)
            + ".class";
        byte[] b = null;
        try {
            // This loads the byte code data from the file
            System.out.println("*** Loading " + name + " from " + file);
            b = loadClassData(file);
            // defineClass is inherited from the ClassLoader class
            // and converts the byte array into a Class
            Class<?> c = defineClass(name, b, 0, b.length);
            resolveClass(c);

            // classes.put(name, c);
            return c;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

     /**
     * Every request for a class passes through this method.
     * If the requested class is in "javablogging" package,
     * it will load it using the
     * {@link CustomClassLoader#getClass()} method.
     * If not, it will use the super.loadClass() method
     * which in turn will pass the request to the parent.
     *
     * @param name
     *            Full class name
     */
    @Override
    public Class<?> loadClass(String name)
        throws ClassNotFoundException {
        if (name.startsWith("javabytecode.")) {
            System.out.println("loading class '" + name + "' with " + this);
            return getClass(name);
        }
        System.out.println("loading class '" + name + "' with system class loader");
        return super.loadClass(name);
    }

     /**
     * Loads a given file (presumably .class) into a byte array.
     * The file should be accessible as a resource, for example
     * it could be located on the classpath.
     *
     * @param name File name to load
     * @return Byte array read from the file
     * @throws IOException Is thrown when there
     *               was some problem reading the file
     */
    private byte[] loadClassData(String name) throws IOException {
        // Opening the file
        InputStream stream = getClass().getClassLoader()
            .getResourceAsStream(name);
        int size = stream.available();
        byte buff[] = new byte[size];
        DataInputStream in = new DataInputStream(stream);
        // Reading the binary data
        in.readFully(buff);
        in.close();
        return buff;
    }
}
