package javabytecode;


public class RewriteMe2 {
    public String f1;
    public String f2;
    public String f3;
    public String f4 = "f4";
    public String f5 = "f5";

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
}
