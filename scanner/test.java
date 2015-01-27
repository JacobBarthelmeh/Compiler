package scanner;


import compiler.Token;

public class test {
    public static void main(String[] args) {
        System.out.println("Running test");
        Dispatcher disp = new Dispatcher("src/testfile.mp");
        Token t;
        String s = "";
        do {
            t = disp.nextToken();
            s += t.getContents() + " ";
        }
        while (t.getID() != Token.ID.EOF);
        System.out.println(s);
    }
}