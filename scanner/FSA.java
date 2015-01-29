package scanner;
import compiler.Token;
public class FSA {
    public static Token TEST_STRING_LIT(Scanner s) {
        String str = "";
        int state = 0;
        char c = s.nextChar();
        
        //  Precondition: first character is the first character in the token
        while (c != '\u001a') {
            switch (state) {
                /*  State 0:
                    Proceed if the character is a '
                    Reject otherwise
                */
                case 0:
                    if (c == '\'') {
                        str += c;
                        state++;
                        c = s.nextChar();
                        break;
                    }
                    return null;
                /*  State 1:
                    Accept if the character is a '
                    Stay in the state otherwise
                */
                case 1:
                    str += c;
                    if (c == '\'') {
                        return new Token(str, Token.ID.STRING_LIT);
                    }
                    c = s.nextChar();
                    break;
            }
        }
        System.out.println("Syntax Error: " +
                "Quotation mark opened but not closed.");
        System.exit(0);
        return null;
    }
    public static Token TEST_COLON(Scanner s) {
        String str = "";
        int state = 0;
        char c = s.nextChar();
        //  Precondition: first character is the first character in the machine
        while (c != '\u001a') {
            switch (state) {
                /*  State 0:
                    Proceed if the character is a :
                    Reject otherwise
                */
                case 0:
                    if (c == ':') {
                        str += c;
                        state++;
                        c = s.peekChar();
                        break;
                    }
                    return null;
                /*  State 1:
                    Proceed if next character is =
                    Accept : otherwise
                */
                case 1:
                    str += c;
                    if (c == '=') {
                        str += c;
                        state++;
                        s.nextChar();
                        break;
                    }
                    return new Token(str, Token.ID.COLON);
                /*  State 2:
                    Accept :=
                */
                case 2:
                    //  Accept the character to move the file pointer
                    //  This prepares the dispatcher
                    s.nextChar();
                    return new Token(str, Token.ID.ASSIGN);
                    
            }
        }
        //  This should never happen
        return null;
    }

}
