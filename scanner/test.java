package scanner;

import compiler.Token;

public class test {

    public static void main(String[] args) {
        System.out.println("Running test");
        Scanner scanner = new Scanner("testfile.mp");
        Token t;
        String s = "";
        boolean cancel = false;
        do {
            t = scanner.nextToken();
            if (t == null) {
                System.out.println("Null return");
                cancel = true;
            } else {
                System.out.println(t.toString());
                s += t.getContents() + " ";
            }
        } while (t == null || t.getID() != Token.ID.EOF);
        if (cancel) {
            System.out.println("Compile failed due to syntax errors. Aborting...");
        } else {
            System.out.println(s);
        }
        //  Since we know this passes and the FSA wasn't edited we can comment this out
        numberTest();
    }

    public static void numberTest() {
        System.out.println("Running Number Test");
        Scanner scanner = null;
        try {
            scanner = new Scanner("numberTest.mp");
        } catch (Exception e) {
            System.out.println("Error reading src/numberTest.mp");
            return;
        }
        Token t = null;
        try {
            t = scanner.nextToken();
            //assert (t.getID() == Token.ID.FIXED_LIT);
            assert (t.getContents().equals("1.2"));
            assert (scanner.nextToken().getID() == Token.ID.PERIOD);
            //assert (scanner.nextToken().getID() == Token.ID.FIXED_LIT);
            assert (scanner.nextToken().getID() == Token.ID.INTEGER_LIT);
            assert (scanner.nextToken().getID() == Token.ID.IDENTIFIER);
            assert (scanner.nextToken().getID() == Token.ID.PLUS);
            assert (scanner.nextToken().getID() == Token.ID.PERIOD);
            assert (scanner.nextToken().getID() == Token.ID.INTEGER_LIT);
            assert (scanner.nextToken().getID() == Token.ID.PERIOD);
            assert (scanner.nextToken().getID() == Token.ID.MINUS);
            //assert (scanner.nextToken().getID() == Token.ID.FIXED_LIT);
            assert (scanner.nextToken().getID() == Token.ID.IDENTIFIER);
            assert (scanner.nextToken().getID() == Token.ID.FLOAT_LIT);
            t = scanner.nextToken();
            assert (t.getID() == Token.ID.INTEGER_LIT);
            assert (t.getContents().equals("45"));
            assert (scanner.nextToken().getID() == Token.ID.PERIOD);
            assert (scanner.nextToken().getID() == Token.ID.MINUS);
            assert (scanner.nextToken().getID() == Token.ID.INTEGER_LIT);
            t = scanner.nextToken();
            assert (t.getID() == Token.ID.FLOAT_LIT);
            assert (t.getContents().equals("0e-1"));
            assert (scanner.nextToken().getID() == Token.ID.INTEGER_LIT);
            assert (scanner.nextToken().getID() == Token.ID.IDENTIFIER);
            assert (scanner.nextToken().getID() == Token.ID.MINUS);
            assert (scanner.nextToken().getID() == Token.ID.EQUAL);
            //assert (scanner.nextToken().getID() == Token.ID.FIXED_LIT);
            assert (scanner.nextToken().getID() == Token.ID.INTEGER_LIT);
            assert (scanner.nextToken().getID() == Token.ID.MINUS);
            assert (scanner.nextToken().getID() == Token.ID.INTEGER_LIT);
            assert (scanner.nextToken().getID() == Token.ID.EQUAL);
            assert (scanner.nextToken().getID() == Token.ID.INTEGER_LIT);
        } catch (Exception e) {
            System.out.println("number test failed make sure file src/nuberTest.mp"
                    + " has not been changed");
            return;
        }
        System.out.println("Passed");
    }
}
