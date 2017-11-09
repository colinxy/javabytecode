package javabytecode;


public class RewriteMe1 {
    public String f1 = "f1";
    public String f2 = "f2";
    public String f3 = "f3";
    public String f4 = "f4";
    public String f5 = "f5";

    public String mod1(String f1) {
        String oldf1 = f1;
        this.f1 = f1;
        return oldf1;
    }
}
