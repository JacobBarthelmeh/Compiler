package scanner;


import compiler.Token;

public class test {
    public static void main(String[] args) {
        System.out.println("Running test");
        Dispatcher disp = new Dispatcher("testfile.mp");
        Token t;
        do {
            t = disp.nextToken();
            System.out.println("Debug: Found token " + t.getContents() + ".");
        }
        while (t.getID() != Token.ID.EOF);
    }
}