package scanner;
import compiler.Token;
public class test {
    public static void main(String[] args) {
        System.out.println("Running test");
        Scanner scanner = new Scanner("src/testfile.mp");
        Token t;
        String s = "";
        do {
            t = scanner.nextToken();
            s += t.getContents() + " ";
        }
        while (t.getID() != Token.ID.EOF);
        System.out.println(s);
    }
}