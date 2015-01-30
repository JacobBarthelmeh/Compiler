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
        
        char c = s.nextChar();
        String str = "" + c + collectDigits(s);
        c = s.peekChar();
        if (c == '.') {
            s.nextChar();
            String decimal = collectDigits(s);
            if (decimal.length() == 0) {
                System.out.println("Syntax Error at line " +
                        s.linenumber + " col " + s.col + ": " +
                        "Number format error " + str + " missing value after decimal.");
                return null;
            }
            else {
                str += c + decimal;
            }
            c = s.peekChar();
        }
        if (c == 'e' || c == 'E') {
            str += c;
            s.nextChar();
            c = s.peekChar();
            if (c == '+' || c == '-') {
                s.nextChar();
                str += c + collectDigits(s);
                return new Token(str, Token.ID.FLOAT_LIT);
            }
            else {
                System.out.println("Syntax Error at line " +
                        s.linenumber + " col " + s.col + ": " +
                        "Number format error " + str + " needs +/- in exponent.");
                return null;
            }
        }
        else {
            return new Token(str, Token.ID.FIXED_LIT);
        }
    }
    private static String collectDigits(Scanner s) {
        String str = "";
        while (true) {
            char c = s.peekChar();
            if (("" + c).matches("\\d")) {
                s.nextChar();
                str += c;
            }
            else {
                break;
            }
        }
        return str;
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
        
        char c = s.nextChar();
        String str = "" + c;
        //  Accept all consecutive alphanumeric characters (with _ included)
        while (true) {
            c = s.peekChar();
            //  Note- numbers are permitted after the first letter is read
            if (("" + c).matches("(a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z|A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z|0|1|2|3|4|5|6|7|8|9|\\_)")) {
                str += c;
                s.nextChar();
            }
            else {
                break;
            }
        }
        //  FSA logic- Regular Expression = DFA
        if (!str.matches(Token.ID.IDENTIFIER.regex())) {
            System.out.println("Syntax Error at line " +
                    s.linenumber + " col " + s.col + ": " +
                    "Identifier format error " + str + " illegally formated.");
            return null;
        }
        //  Switch is a machine-level hashmap
        switch (str) {
            case "and": return new Token(str, Token.ID.AND);
            case "begin": return new Token(str, Token.ID.BEGIN);
            case "Boolean": return new Token(str, Token.ID.BOOLEAN);
            case "div": return new Token(str, Token.ID.DIV);
            case "do": return new Token(str, Token.ID.DO);
            case "downto": return new Token(str, Token.ID.DOWNTO);
            case "else": return new Token(str, Token.ID.ELSE);
            case "end": return new Token(str, Token.ID.END);
            case "flase": return new Token(str, Token.ID.FALSE);
            case "fixed": return new Token(str, Token.ID.FIXED);
            case "float": return new Token(str, Token.ID.FLOAT);
            case "for": return new Token(str, Token.ID.FOR);
            case "function": return new Token(str, Token.ID.FUNCTION);
            case "if": return new Token(str, Token.ID.IF);
            case "integer": return new Token(str, Token.ID.INTEGER);
            case "mod": return new Token(str, Token.ID.MOD);
            case "not": return new Token(str, Token.ID.NOT);
            case "or": return new Token(str, Token.ID.OR);
            case "procedure": return new Token(str, Token.ID.PROCEDURE);
            case "program": return new Token(str, Token.ID.PROGRAM);
            case "read": return new Token(str, Token.ID.READ);
            case "repeat": return new Token(str, Token.ID.REPEAT);
            case "string": return new Token(str, Token.ID.STRING);
            case "then": return new Token(str, Token.ID.THEN);
            case "true": return new Token(str, Token.ID.TRUE);
            case "to": return new Token(str, Token.ID.TO);
            case "type": return new Token(str, Token.ID.TYPE);
            case "until": return new Token(str, Token.ID.UNTIL);
            case "var": return new Token(str, Token.ID.VAR);
            case "while": return new Token(str, Token.ID.WHILE);
            case "write": return new Token(str, Token.ID.WRITE);
            case "writeln": return new Token(str, Token.ID.WRITELN);
            default: return new Token(str, Token.ID.IDENTIFIER);
        }
    }
    public static Token TEST_STRING_LIT(Scanner s) {
        String str = "";
        int state = 0;
        char c = s.nextChar();  //  1 char accepted
        
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
                        break;
                    }
                    return null;
                /*  State 1:
                    c is ' or ?
                    Accept if the character is a '
                    Stay in the state otherwise
                */
                case 1:
                    //  Okay to accept every character until the end is reached
                    //   (as opposed to having to peek and undo)
                    c = s.nextChar();     //  2 + n characters accepted
                    str += c;
                    if (c == '\'') {
                        state = 2;
                        break;
                    }
                    break;
                case 2:
                    //  Already accepted 2 + n characters so the next is
                    //  the first one after the token has ended (pc met!)
                    return new Token(str, Token.ID.STRING_LIT);
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
                        break;
                    }
                    return null;
                /*  State 1:
                    c is either = or ?
                    Proceed if next character is =
                    Accept : otherwise
                */
                case 1:
                    c = s.peekChar();   //  Don't want to accept always
                    if (c == '=') {
                        str += c;       //  save the := symbol
                        state = 2;      //  Move to accept assign
                        break;
                    }
                    //  One character accepted; pc has been met
                    return new Token(str, Token.ID.COLON);
                /*  State 2:
                    Accept :=
                */
                case 2:
                    s.nextChar();   //  keep the found token (pc met!)
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
                    //  Don't always accept the next character
                    c = s.peekChar();
                    if (c == '=') {
                        str += c;
                        state = 2;
                        break;
                    }
                    if (c == '>') {
                        str += c;
                        state = 3;
                        break;
                    }
                    //  Postcondition: now c is the character after < (pc met!)
                    return new Token(str, Token.ID.COLON);
                /*  State 2:
                    Accept <=
                */
                case 2:
                    s.nextChar();   //  now points after = (pc met!)
                    return new Token(str, Token.ID.LEQUAL);
                /*  State 3:
                    Accept <>
                */
                case 3:
                    s.nextChar();   //  now points after = (pc met!)
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
                        break;
                    }
                    return null;
                /*  State 1
                    c is = or ?
                    Proceed to 2 if c is =
                    Accept > otherwise
                */
                case 1:
                    c = s.peekChar();
                    if (c == '=') {
                        str += c;
                        state = 2;
                        break;
                    }
                    //  Only accepted one character (pc met!)
                    return new Token(str, Token.ID.GTHAN);
                /*  State 2
                    c is character after = (pc met!)

                */
                case 2:
                    s.nextChar();   //  accept the = so the pc is met
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
