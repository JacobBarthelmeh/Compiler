package scanner;

import compiler.Token;
import javax.swing.JOptionPane;

public class FSA {

    /**
     * Test a digit and return the token
     *
     * @param r
     * @return
     */
    public static Token TEST_DIGIT(Reader r) {
        String str = "";
        int state = 0;
        Token.ID id = Token.ID.INTEGER_LIT;
        char c;
        do {
            c = r.nextChar();
            switch (state) {
                case 0:
                    if (c >= '0' && c <= '9') {
                        str += c;
                        state = 1;
                    } else {
                        //other character found
                        r.ungetChar(c);
                        return null;
                    }
                    break;
                case 1:
                    if (c >= '0' && c <= '9') {
                        //stay in state 0
                        str += c;
                        state = 1;
                    } else if (c == '.') {
                        str += c;
                        state = 2;
                    } else if (c == 'E') {
                        str += c;
                        state = 3;
                    } else if (c == 'e') {
                        str += c;
                        state = 4;
                    } else {
                        //other character found
                        r.ungetChar(c);
                        return new Token(str, id);
                    }
                    break;
                case 2:
                    if (c >= '0' && c <= '9') {
                        //transition to state 2
                        str += c;
                        id = Token.ID.FIXED_LIT;
                        state = 5;
                    } else {
                        //other character found
                        r.ungetChar(c);
                        r.ungetChar('.');
                        return new Token(str.subSequence(0, str.length() - 1).toString(), id);
                    }
                    break;
                case 3:
                    switch (c) {
                        case '+':
                            str += c;
                            state = 8;
                            break;
                        case '-':
                            str += c;
                            state = 6;
                            break;
                        default:
                            r.ungetChar(c);
                            r.ungetChar('E');
                            return new Token(str.subSequence(0, str.length() - 1).toString(), id);
                    }
                    break;
                case 4:
                    switch (c) {
                        case '+':
                            str += c;
                            state = 12;
                            break;
                        case '-':
                            str += c;
                            state = 10;
                            break;
                        default:
                            r.ungetChar(c);
                            r.ungetChar('e');
                            return new Token(str.subSequence(0, str.length() - 1).toString(), id);
                    }
                    break;
                case 5:
                    if (c >= '0' && c <= '9') {
                        //transition to state 2
                        str += c;
                        state = 5;
                    } else if (c == 'e') {
                        str += c;
                        state = 4;
                    } else if (c == 'E') {
                        str += c;
                        state = 3;
                    } else {
                        //other character found
                        r.ungetChar(c);
                        return new Token(str, id);
                    }
                    break;
                case 6:
                    if (c >= '0' && c <= '9') {
                        //transition to state 2
                        str += c;
                        id = Token.ID.FLOAT_LIT;
                        state = 7;
                    } else {
                        //other character found
                        r.ungetChar(c);
                        r.ungetChar('-');
                        r.ungetChar('E');
                        return new Token(str.subSequence(0, str.length() - 2).toString(), id);
                    }
                    break;
                case 7:
                    if (c >= '0' && c <= '9') {
                        str += c;
                        state = 1;
                    } else {
                        //other character found
                        r.ungetChar(c);
                        return new Token(str, id);
                    }
                    break;
                case 8:
                    if (c >= '0' && c <= '9') {
                        //transition to state 2
                        str += c;
                        id = Token.ID.FLOAT_LIT;
                        state = 7;
                    } else {
                        //other character found
                        r.ungetChar(c);
                        r.ungetChar('+');
                        r.ungetChar('E');
                        return new Token(str.subSequence(0, str.length() - 2).toString(), id);
                    }
                    break;
                case 10:
                    if (c >= '0' && c <= '9') {
                        str += c;
                        id = Token.ID.FLOAT_LIT;
                        state = 7;
                    } else {
                        //other character found
                        r.ungetChar(c);
                        r.ungetChar('-');
                        r.ungetChar('e');
                        return new Token(str.subSequence(0, str.length() - 2).toString(), id);
                    }
                    break;
                case 12:
                    if (c >= '0' && c <= '9') {
                        str += c;
                        id = Token.ID.FLOAT_LIT;
                        state = 7;
                    } else {
                        //other character found
                        r.ungetChar(c);
                        r.ungetChar('+');
                        r.ungetChar('e');
                        return new Token(str.subSequence(0, str.length() - 2).toString(), id);
                    }
                    break;
                default:
                    //you lose
                    JOptionPane.showMessageDialog(null, "you lose : state = " + state);

            }
        } while (c != '\u001a');
        //End of file fall through
        return new Token(str, id);
    }

    public static Token TEST_LETTER(Reader r) {
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
        int state = 0;
        char c = r.nextChar();
        String str = "" + c;

        //  Temporarily force this return to test other features
        return new Token(str, Token.ID.IDENTIFIER);

        //  Accept all consecutive alphanumeric characters (with _ included)
        while (c != '\u001a') {
            c = r.peekChar();
            //  Note- numbers are permitted after the first letter is read

            //  Also note: Use of Regex (which means all .matches) is not allowed in this project
            if (("" + c).matches("(a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z|A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z|0|1|2|3|4|5|6|7|8|9|\\_)")) {
                str += c;
                switch (state) {
                    //  State 0: Accept either _ or letter
                    //      If _ is accepted, move to state 1
                    //      If letter is accepted, move to state 2
                    case 0:
                        if (c == '_') {
                            state = 1;
                        } else { // alphabetic character received
                            state = 2;
                        }
                        break;

                    //      If letter is accepted move to state 2
                    //      If anything else is found, reject
                    case 1:
                        // if the character read in is alphanumeric....
                        if (c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z') {
                            state = 2;
                            break;
                        } // if it isn't alphanumeric return null
                        else {
                            return null;
                        }

                    //  State 2:
                    //      If a letter is found, loop on 2
                    //      If _ is found, go to state 3
                    //      Otherwise go to state 4
                    case 2:
                        // Letter found
                        if (c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z') {
                            state = 2;
                        } // '_' found
                        else if (c == '_') {
                            state = 3;
                        } else {
                            state = 4;
                        }
                        break;

                    //  State 3:
                    //      If a letter is found, go to state 2
                    //      Otherwise reject
                    case 3:
                        // Alphanumeric found, go to state 2
                        if (c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z') {
                            state = 2;
                        } // No alphanumeric found, reject
                        else {
                            return null;
                        }
                }
            }
            r.nextChar();
        }
        if (state == 4) {
            //  Switch is a machine-level hashmap
            switch (str) {
                case "and":
                    return new Token(str, Token.ID.AND);
                case "begin":
                    return new Token(str, Token.ID.BEGIN);
                case "Boolean":
                    return new Token(str, Token.ID.BOOLEAN);
                case "div":
                    return new Token(str, Token.ID.DIV);
                case "do":
                    return new Token(str, Token.ID.DO);
                case "downto":
                    return new Token(str, Token.ID.DOWNTO);
                case "else":
                    return new Token(str, Token.ID.ELSE);
                case "end":
                    return new Token(str, Token.ID.END);
                case "flase":
                    return new Token(str, Token.ID.FALSE);
                case "fixed":
                    return new Token(str, Token.ID.FIXED);
                case "float":
                    return new Token(str, Token.ID.FLOAT);
                case "for":
                    return new Token(str, Token.ID.FOR);
                case "function":
                    return new Token(str, Token.ID.FUNCTION);
                case "if":
                    return new Token(str, Token.ID.IF);
                case "integer":
                    return new Token(str, Token.ID.INTEGER);
                case "mod":
                    return new Token(str, Token.ID.MOD);
                case "not":
                    return new Token(str, Token.ID.NOT);
                case "or":
                    return new Token(str, Token.ID.OR);
                case "procedure":
                    return new Token(str, Token.ID.PROCEDURE);
                case "program":
                    return new Token(str, Token.ID.PROGRAM);
                case "read":
                    return new Token(str, Token.ID.READ);
                case "repeat":
                    return new Token(str, Token.ID.REPEAT);
                case "string":
                    return new Token(str, Token.ID.STRING);
                case "then":
                    return new Token(str, Token.ID.THEN);
                case "true":
                    return new Token(str, Token.ID.TRUE);
                case "to":
                    return new Token(str, Token.ID.TO);
                case "type":
                    return new Token(str, Token.ID.TYPE);
                case "until":
                    return new Token(str, Token.ID.UNTIL);
                case "var":
                    return new Token(str, Token.ID.VAR);
                case "while":
                    return new Token(str, Token.ID.WHILE);
                case "write":
                    return new Token(str, Token.ID.WRITE);
                case "writeln":
                    return new Token(str, Token.ID.WRITELN);
                default:
                    return new Token("identifer:" + str, Token.ID.IDENTIFIER);
            }
        }
        r.nextChar();

        System.out.println("ERROR: Reached endline for LETTER FSA");
        return null;
    }

    public static Token TEST_STRING_LIT(Reader r) {
        String str = "";
        int state = 0;
        char c = r.nextChar();  //  1 char accepted

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
                    System.out.println("ERROR: Failed precondition for STRING_LIT FSA");
                    return null;
                /*  State 1:
                 c is ' or ?
                 Accept if the character is a '
                 Stay in the state otherwise
                 */
                case 1:
                    //  Okay to accept every character until the end is reached
                    //   (as opposed to having to peek and undo)
                    c = r.nextChar();     //  2 + n characters accepted
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
        System.out.println("Syntax Error: "
                + "Quotation mark opened but not closer.");
        System.exit(0);
        return null;
    }

    public static Token TEST_COLON(Reader r) {
        String str = "";
        int state = 0;
        char c = r.nextChar();
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
                    System.out.println("ERROR: Failed precondition for COLON FSA");
                    return null;
                /*  State 1:
                 c is either = or ?
                 Proceed if next character is =
                 Accept : otherwise
                 */
                case 1:
                    c = r.peekChar();   //  Don't want to accept always
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
                    r.nextChar();   //  keep the found token (pc met!)
                    return new Token(str, Token.ID.ASSIGN);
                //  Postcondition: c points to the character after this token
            }
        }
        //  This should never happen
        System.out.println("ERROR: Reached endline for COLON FSA");
        return null;
    }

    public static Token TEST_LTHAN(Reader r) {
        String str = "";
        int state = 0;
        char c = r.nextChar();
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
                    System.out.println("ERROR: Failed precondition for LTHAN FSA");
                    return null;
                /*  State 1:
                 c is = or > or ?
                 Proceed to 2 if character is =
                 Proceed to 3 if character is >
                 Accept < otherwise
                 */
                case 1:
                    //  Don't always accept the next character
                    c = r.peekChar();
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
                    return new Token(str, Token.ID.LTHAN);
                /*  State 2:
                 Accept <=
                 */
                case 2:
                    r.nextChar();   //  now points after = (pc met!)
                    return new Token(str, Token.ID.LEQUAL);
                /*  State 3:
                 Accept <>
                 */
                case 3:
                    r.nextChar();   //  now points after = (pc met!)
                    return new Token(str, Token.ID.NEQUAL);
            }
        }
        //  This should never happen
        System.out.println("ERROR: Reached endline for LTHANFSA");
        return null;
    }

    public static Token TEST_GTHAN(Reader r) {
        String str = "";
        char c = r.nextChar();
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
                    c = r.peekChar();
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
                    r.nextChar();   //  accept the = so the pc is met
                    return new Token(str, Token.ID.GEQUAL);
            }
        }
        //  This should never happen;
        System.out.println("ERROR: Reached endline for GTHAN FSA");
        return null;
    }

    public static Token TEST_COMMA(Reader r) {
        char c = r.nextChar();
        if (c == ',') {
            return new Token(",", Token.ID.COMMA);
        }
        //  This should never happen
        System.out.println("ERROR: Reached endline for COMMA FSA");
        return null;
    }

    public static Token TEST_EQUAL(Reader r) {
        char c = r.nextChar();
        if (c == '=') {
            return new Token("=", Token.ID.EQUAL);
        }
        //  This should never happen
        System.out.println("ERROR: Reached endline for EQUAL FSA");
        return null;
    }

    public static Token TEST_FLOAT_DIVIDE(Reader r) {
        char c = r.nextChar();
        if (c == '/') {
            return new Token("/", Token.ID.FLOAT_DIVIDE);
        }
        //  This should never happen
        System.out.println("ERROR: Reached endline for FLOAT_DIVIDE FSA");
        return null;
    }

    public static Token TEST_LPAREN(Reader r) {
        char c = r.nextChar();
        if (c == '(') {
            return new Token("(", Token.ID.LPAREN);
        }
        //  This should never happen
        System.out.println("ERROR: Reached endline for LPAREN FSA");
        return null;
    }

    public static Token TEST_RPAREN(Reader r) {
        char c = r.nextChar();
        if (c == ')') {
            return new Token(")", Token.ID.RPAREN);
        }
        //  This should never happen
        System.out.println("ERROR: Reached endline for RPAREN FSA");
        return null;
    }

    public static Token TEST_MINUS(Reader r) {
        char c = r.nextChar();
        if (c == '-') {
            return new Token("-", Token.ID.MINUS);
        }
        //  This should never happen
        System.out.println("ERROR: Reached endline for MINUS FSA");
        return null;
    }

    public static Token TEST_PERIOD(Reader r) {
        char c = r.nextChar();
        if (c == '.') {
            return new Token(".", Token.ID.PERIOD);
        }
        //  This should never happen
        System.out.println("ERROR: Reached endline for PERIOD FSA");
        return null;
    }

    public static Token TEST_PLUS(Reader r) {
        char c = r.nextChar();
        if (c == '+') {
            return new Token("+", Token.ID.PLUS);
        }
        //  This should never happen
        System.out.println("ERROR: Reached endline for PLUS FSA");
        return null;
    }

    public static Token TEST_SCOLON(Reader r) {
        char c = r.nextChar();
        if (c == ';') {
            return new Token(";", Token.ID.SCOLON);
        }
        //  This should never happen
        System.out.println("ERROR: Reached endline for SCOLON FSA");
        return null;
    }

    public static Token TEST_TIMES(Reader r) {
        char c = r.nextChar();
        if (c == '*') {
            return new Token("*", Token.ID.TIMES);
        }
        //  This should never happen
        System.out.println("ERROR: Reached endline for TIMES FSA");
        return null;
    }
}
