package javabytecode;


public class RewriteMe2 {
    public String f1;
    public String f2;
    public String f3;
    public String f4 = "the field f4";
    public String f5 = "the field f5";

    public RewriteMe2() {
        // f1 = "f1";
        // f2 = "f2";
        // f3 = "f3";
    }

    public String test() {
        int a = 1;
        String foo = "bar";
        a++;
        return foo;
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
    }
}
