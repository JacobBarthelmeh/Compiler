package scanner;
import compiler.Token;
public class test {
    public static void main(String[] args) {
        System.out.println("Running test");
        Scanner scanner = new Scanner("src/testfile.mp");
        Token t;
        String s = "";
        boolean cancel = false;
        do {
            t = scanner.nextToken();
            if (t == null ) {
                System.out.println("Null return");
                cancel = true;
            }
            else {
                System.out.println(t.getContents() + " " + t.getID());
                s += t.getContents() + " ";
            }
        }
        while (t == null || t.getID() != Token.ID.EOF);
        if (cancel) {
            System.out.println("Compile failed due to syntax errors. Aborting...");
        }
        else {
            System.out.println(s);
        }
    }
}