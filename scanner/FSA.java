package scanner;
import compiler.Token;
public class FSA {
    public static Token TEST_DIGIT(Scanner s) {
        //  TODO
        //  State 0: Accept the first digit and proceed to state 1
        //  State 1: Accept an arbitrary number of digits
        //      If a period is found, move to state 2
        //      If e or E is found, move to state 3
        //      If anything else is found, return an integer (preserve CP!)
        //  State 2: Accept an arbitrary number of digits
        //      If e or E is found, move to state 3
        //      If anything else is found, return a fixed point (preserve CP!)
        //  State 3: Accept e or E and look for + or -
        //      If + or - is found, move to state 4
        //      If anything else is found, return syntax error message
        //  State 4: Accept an arbitrary number of digits
        //      If anything else is found, return a fixed point (preserve CP!)
        
        //  This should never happen
        return null;
    }
    public static Token TEST_LETTER(Scanner s) {
        //  TODO
        //  State 0: Accept either _ or letter
        //      If _ is accepted, move to state 1
        //      If letter is accepted, move to state 2
        //  State 1: Accept a letter
        //      If letter is accepted move to state 2
        //      If anything else is found, reject
        //  State 2:
        //      If a letter is found, loop on 2
        //      If _ is found, go to state 3
        //      Otherwise go to state 4
        //  State 3:
        //      If a letter is found, go to state 2
        //      Otherwise reject
        //  State 4:
        //      Found identifier! However, it still must be
        //      run through the reserved words list which 
        //      can be foudn in the README
        
        //  This should never happen
        return null;
    }
    public static Token TEST_STRING_LIT(Scanner s) {
        String str = "";
        int state = 0;
        char c = s.nextChar();
        
        //  Precondition: first character is the first character in the token
        while (c != '\u001a') {
            switch (state) {
                /*  State 0:
                    c is '
                    Proceed if the character is a '
                    Reject otherwise
                */
                case 0:
                    if (c == '\'') {
                        str += c;
                        state = 1;
                        c = s.nextChar();
                        break;
                    }
                    return null;
                /*  State 1:
                    c is ' or ?
                    Accept if the character is a '
                    Stay in the state otherwise
                */
                case 1:
                    str += c;
                    if (c == '\'') {
                        s.nextChar();
                        //  c is now the character after this token (pc met!)
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
                    c is :
                    Proceed if the character is a :
                    Reject otherwise
                */
                case 0:
                    if (c == ':') {
                        str += c;
                        state = 1;
                        c = s.nextChar();
                        break;
                    }
                    return null;
                /*  State 1:
                    c is either = or ?
                    Proceed if next character is =
                    Accept : otherwise
                */
                case 1:
                    str += c;
                    if (c == '=') {
                        str += c;
                        state = 2;
                        c = s.nextChar(); //    now points after = (pc met!)
                        break;
                    }
                    //  c is the character after this token (pc met!)
                    return new Token(str, Token.ID.COLON);
                /*  State 2:
                    c is the character after this token (pc met!)
                    Accept :=
                */
                case 2:
                    return new Token(str, Token.ID.ASSIGN);
                    //  Postcondition: c points to the character after this token
            }
        }
        //  This should never happen
        return null;
    }
    public static Token TEST_LTHAN(Scanner s) {
        String str = "";
        int state = 0;
        char c = s.nextChar();
        //  Precondition: first character is the first character in the machine
        while (c != '\u001a') {
            switch (state) {
                /*  State 0:
                    c is <
                    Proceed if the character is a <
                    Reject otherwise
                */
                case 0:
                    if (c == '<') {
                        str += c;
                        state = 1;
                        c = s.peekChar();
                        break;
                    }
                    return null;
                /*  State 1:
                    c is = or > or ?
                    Proceed to 2 if character is =
                    Proceed to 3 if character is >
                    Accept < otherwise
                */
                case 1:
                    str += c;
                    if (c == '=') {
                        str += c;
                        state = 2;
                        c = s.nextChar();   //  now points after = (pc met!)
                        break;
                    }
                    if (c == '>') {
                        str += c;
                        state = 3;
                        c = s.nextChar();   //  now points after > (pc met!)
                        break;
                    }
                    //  Postcondition: now c is the character after < (pc met!)
                    return new Token(str, Token.ID.COLON);
                /*  State 2:
                    Accept <=
                */
                case 2:
                    return new Token(str, Token.ID.LEQUAL);
                case 3:
                    return new Token(str, Token.ID.NEQUAL);
            }
        }
        //  This should never happen
        return null;
    }
    public static Token TEST_GTHAN(Scanner s) {
        String str = "";
        char c = s.nextChar();
        int state = 0;
        while (c != '\u001a') {
            switch (state) {
                /*  State 0
                    c is >
                    Proceed to 1 if c is >
                    Reject otherwise
                */
                case 0:
                    if (c == '>') {
                        state = 1;
                        str += c;
                        c = s.nextChar();
                        break;
                    }
                    return null;
                /*  State 1
                    c is = or ?
                    Proceed to 2 if c is =
                    Accept > otherwise
                */
                case 1:
                    if (c == '=') {
                        str += c;
                        c = s.nextChar();   //  now points after = (pc met!)
                        state = 2;
                        break;
                    }
                    return new Token(str, Token.ID.GTHAN);
                /*  State 2
                    c is character after = (pc met!)

                */
                case 2:
                    return new Token(str, Token.ID.GEQUAL);
            }
        }
        //  This should never happen;
        return null;
    }
    public static Token TEST_COMMA(Scanner s) {
        char c = s.nextChar();
        if (c == ',') {
            return new Token(",", Token.ID.COMMA);
        }
        //  This should never happen
        return null;
    }
    public static Token TEST_EQUAL(Scanner s) {
        char c = s.nextChar();
        if (c == '=') {
            return new Token("=", Token.ID.EQUAL);
        }
        //  This should never happen
        return null;
    }
    public static Token TEST_FLOAT_DIVIDE(Scanner s) {
        char c = s.nextChar();
        if (c == '/') {
            return new Token("/", Token.ID.FLOAT_DIVIDE);
        }
        //  This should never happen
        return null;
    }
    public static Token TEST_LPAREN(Scanner s) {
        char c = s.nextChar();
        if (c == '(') {
            return new Token("(", Token.ID.LPAREN);
        }
        //  This should never happen
        return null;
    }
    public static Token TEST_RPAREN(Scanner s) {
        char c = s.nextChar();
        if (c == ')') {
            return new Token(")", Token.ID.RPAREN);
        }
        //  This should never happen
        return null;
    }
    public static Token TEST_MINUS(Scanner s) {
        char c = s.nextChar();
        if (c == '-') {
            return new Token("-", Token.ID.MINUS);
        }
        //  This should never happen
        return null;
    }
    public static Token TEST_PERIOD(Scanner s) {
        char c = s.nextChar();
        if (c == '.') {
            return new Token(".", Token.ID.PERIOD);
        }
        //  This should never happen
        return null;
    }
    public static Token TEST_PLUS(Scanner s) {
        char c = s.nextChar();
        if (c == '+') {
            return new Token("+", Token.ID.PLUS);
        }
        //  This should never happen
        return null;
    }
    public static Token TEST_SCOLON(Scanner s) {
        char c = s.nextChar();
        if (c == ';') {
            return new Token(";", Token.ID.SCOLON);
        }
        //  This should never happen
        return null;
    }
    public static Token TEST_TIMES(Scanner s) {
        char c = s.nextChar();
        if (c == '*') {
            return new Token("*", Token.ID.TIMES);
        }
        //  This should never happen
        return null;
    }
}
