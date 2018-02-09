package javabytecode;


public class RewriteMe2 extends Base implements Iface {
    public String f1;
    public String f2;
    public String f3;
    public String f4 = "the field f4";
    public String f5 = "the field f5";

    // public RewriteMe2() {
    //     f1 = "f1";
    //     f2 = "f2";
    //     f3 = "f3";
    // }

    public String test() {
        int a = 1;
        String foo = "bar";
        a++;
        return foo;
    }

    public int arith() {
        int a = 1;
        int b = 2;
        int c = a + b;
        int d = a - b;
        System.out.println("c " + c);
        System.out.println("d " + d);
        return c * d;
    }

    public String f5Ref() {
        return f5;
    }

    public String notF5Ref() {
        String f5 = "this is not field f5";
        return f5;
    }

    public void invokeAll() {
        test();
        f5Ref();
        notF5Ref();
        // f5 reference
        System.out.println(f5);
        System.out.println(this.f5);
        System.out.println(((Iface)this).f5);

        Inner inner = new Inner();
        System.out.println(inner.f5);
    }

    public static void staticInvokeAll() {
        Base base = new RewriteMe2();
        System.out.println(base.f5);

        Inherited inherited = new Inherited();
        System.out.println(inherited.f5);
        inherited.invokeAll();

        StaticInner inner = new StaticInner();
        System.out.println(inner.f5);

        Iface iface = new RewriteMe2();
        System.out.println(iface.f5);
    }

    public class Inner {
        public String f5 = "inner f5";
    }

    public static class StaticInner {
        public String f5 = "static inner f5";
    }
}


class Base {
    public String f5 = "base f5";
}


class Inherited extends RewriteMe2 {
    public String f5 = "inherited h5";
}

interface Iface {
    // All variables declared inside interface are implicitly
    // public static final variables(constants)
    public String f5 = "interface f5";
}
