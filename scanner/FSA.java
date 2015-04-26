package scanner;

import util.Reader;
import compiler.Token;
import javax.swing.JOptionPane;
import util.Terminal;

public class FSA {

    /**
     * Test a digit and return the token.
     * @param reader The file reader
     * @param row The row of the occurrence
     * @param col The col of the occurrence
     * @return The token containing the number
     */
    public static Token TEST_DIGIT(Reader reader, int row, int col) {
        String str = "";
        int state = 0;
        Terminal id = Terminal.INTEGER_LIT;
        char c;
        do {
            c = reader.nextChar();
            switch (state) {
                case 0:
                    if (c >= '0' && c <= '9') {
                        str += c;
                        state = 1;
                    } else {
                        //other character found
                        reader.ungetChar(c);
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
                        reader.ungetChar(c);
                        return new Token(str, id, row, col);
                    }
                    break;
                case 2:
                    if (c >= '0' && c <= '9') {
                        //transition to state 5
                        str += c;
                        id = Terminal.FLOAT_LIT;
                        state = 5;
                    } else {
                        //other character found
                        reader.ungetChar(c);
                        reader.ungetChar('.');
                        return new Token(str.subSequence(0, str.length() - 1).toString(), id, row, col);
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
                            reader.ungetChar(c);
                            reader.ungetChar('E');
                            return new Token(str.subSequence(0, str.length() - 1).toString(), id, row, col);
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
                            reader.ungetChar(c);
                            reader.ungetChar('e');
                            return new Token(str.subSequence(0, str.length() - 1).toString(), id, row, col);
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
                        reader.ungetChar(c);
                        return new Token(str, id, row, col);
                    }
                    break;
                case 6:
                    if (c >= '0' && c <= '9') {
                        //transition to state 2
                        str += c;
                        id = Terminal.FLOAT_LIT;
                        state = 7;
                    } else {
                        //other character found
                        reader.ungetChar(c);
                        reader.ungetChar('-');
                        reader.ungetChar('E');
                        return new Token(str.subSequence(0, str.length() - 2).toString(), id, row, col);
                    }
                    break;
                case 7:
                    if (c >= '0' && c <= '9') {
                        str += c;
                        state = 1;
                    } else {
                        //other character found
                        reader.ungetChar(c);
                        return new Token(str, id, row, col);
                    }
                    break;
                case 8:
                    if (c >= '0' && c <= '9') {
                        //transition to state 2
                        str += c;
                        id = Terminal.FLOAT_LIT;
                        state = 7;
                    } else {
                        //other character found
                        reader.ungetChar(c);
                        reader.ungetChar('+');
                        reader.ungetChar('E');
                        return new Token(str.subSequence(0, str.length() - 2).toString(), id, row, col);
                    }
                    break;
                case 10:
                    if (c >= '0' && c <= '9') {
                        str += c;
                        id = Terminal.FLOAT_LIT;
                        state = 7;
                    } else {
                        //other character found
                        reader.ungetChar(c);
                        reader.ungetChar('-');
                        reader.ungetChar('e');
                        return new Token(str.subSequence(0, str.length() - 2).toString(), id, row, col);
                    }
                    break;
                case 12:
                    if (c >= '0' && c <= '9') {
                        str += c;
                        id = Terminal.FLOAT_LIT;
                        state = 7;
                    } else {
                        //other character found
                        reader.ungetChar(c);
                        reader.ungetChar('+');
                        reader.ungetChar('e');
                        return new Token(str.subSequence(0, str.length() - 2).toString(), id, row, col);
                    }
                    break;
                default:
                    //you lose
                    JOptionPane.showMessageDialog(null, "you lose : state = " + state);

            }
        } while (c != '\u001a');
        //End of file fall through
        return new Token(str, id, row, col);
    }
    /**
     * Tests to see if this is a keyword or identifier
     * @param reader The file reader
     * @param row The row of the occurrence
     * @param col The col of the occurrence
     * @return The token containing the keyword or identifier
     */
    public static Token TEST_LETTER(Reader reader, int row, int col) {
        int state = 0;
        char c = reader.nextChar();
        String str = "";
        
        while (c!= '\u001a') {
            switch (state) {
                case 0:
                    if (c == '_') {
                        state = 1;
                        str += c;
                        break;
                    }
                    if (c <= 'z' && c >= 'a' || c <= 'Z' && c >= 'A') {
                        state = 2;
                        str += c;
                        break;
                    }
                    System.out.println("ERROR: Failed precondition for TEST_LETTER FSA");
                    return null;
                    //  This state can only be reached by underscore and it requires
                    //  that there be an alphanumeric directly after all underscores
                case 1:
                    c = reader.peekChar();
                    if (c <= 'z' && c >= 'a' || c <= 'Z' && c >= 'A' || c <= '9' && c >= '0') {
                        state = 2;
                        str += c;
                        reader.nextChar();
                        break;
                    }
                    return new Token(str, Terminal.IDENTIFIER, row, col);
                case 2:
                    c = reader.peekChar();
                    if (c == '_') {
                        state = 1;
                        str += c;
                        reader.nextChar();
                        break;
                    }
                    if (c <= 'z' && c >= 'a' || c <= 'Z' && c >= 'A' || c <= '9' && c >= '0') {
                        state = 2;
                        str += c;
                        reader.nextChar();
                        break;
                    }
                    //  No underscore or character- c is peeking at the first char
                    //  that is not in this token, so postcondition is met.
                    //  Force the exit condition; doesn't get added to string
                    //  so it doesn't really matter
                    c = '\u001a';
                    break;
            }
        }
        //  This language is case-insensitive. Includes keywords and identifiers.
        str = str.toLowerCase();
        switch (str) {
            //  1 bytecode :3
            case "and": return new Token(str, Terminal.AND, row, col);
            case "begin": return new Token(str, Terminal.BEGIN, row, col);
            case "boolean": return new Token(str, Terminal.BOOLEAN, row, col);
            case "div": return new Token(str, Terminal.DIV, row, col);
            case "do": return new Token(str, Terminal.DO, row, col);
            case "downto": return new Token(str, Terminal.DOWNTO, row, col);
            case "else": return new Token(str, Terminal.ELSE, row, col);
            case "end": return new Token(str, Terminal.END, row, col);
            case "false": return new Token(str, Terminal.FALSE, row, col);
            case "fixed": return new Token(str, Terminal.FIXED, row, col);
            case "float": return new Token(str, Terminal.FLOAT, row, col);
            case "for": return new Token(str, Terminal.FOR, row, col);
            case "function": return new Token(str, Terminal.FUNCTION, row, col);
            case "if": return new Token(str, Terminal.IF, row, col);
            case "integer": return new Token(str, Terminal.INTEGER, row, col);
            case "mod": return new Token(str, Terminal.MOD, row, col);
            case "not": return new Token(str, Terminal.NOT, row, col);
            case "or": return new Token(str, Terminal.OR, row, col);
            case "procedure": return new Token(str, Terminal.PROCEDURE, row, col);
            case "program": return new Token(str, Terminal.PROGRAM, row, col);
            case "read": return new Token(str, Terminal.READ, row, col);
            case "repeat": return new Token(str, Terminal.REPEAT, row, col);
            case "string": return new Token(str, Terminal.STRING, row, col);
            case "then": return new Token(str, Terminal.THEN, row, col);
            case "true": return new Token(str, Terminal.TRUE, row, col);
            case "to": return new Token(str, Terminal.TO, row, col);
            case "until": return new Token(str, Terminal.UNTIL, row, col);
            case "var": return new Token(str, Terminal.VAR, row, col);
            case "while": return new Token(str, Terminal.WHILE, row, col);
            case "write": return new Token(str, Terminal.WRITE, row, col);
            case "writeln": return new Token(str, Terminal.WRITELN, row, col);
            default: return new Token(str, Terminal.IDENTIFIER, row, col);
        }
    }
    /**
     * Tests to see if this is a string literal
     * @param reader The file to read from
     * @param row The row of the occurrence
     * @param col The col of the occurrence
     * @return The token with the string
     */
    public static Token TEST_STRING_LIT(Reader reader, int row, int col) {
        String str = "";
        int state = 0;
        char c = reader.nextChar();  //  1 char accepted

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
                    c = reader.nextChar();
                    str += c;
                    if (c == '\'') {
                        state = 2;
                        break;
                    }
                    if (c == '\n' || c == '\r') {
                        state = 3;
                        break;
                    }
                    break;
                case 2:
                    //  Already accepted 2 + n characters so the next is
                    //  the first one after the token has ended (pc met!)
                    return new Token(str, Terminal.STRING_LIT, row, col);
                case 3:
                    return new Token("String not closed",
                            Terminal.EOF, row, col);
            }
        }
        return null;
    }
    /**
     * Tests to see if this is an := or :
     * @param reader The file reader
     * @param row The row of the occurrence
     * @param col The col of the occurrence
     * @return The token containing the result
     */
    public static Token TEST_COLON(Reader reader, int row, int col) {
        String str = "";
        int state = 0;
        char c = reader.nextChar();
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
                    c = reader.peekChar();   //  Don't want to accept always
                    if (c == '=') {
                        str += c;       //  save the := symbol
                        state = 2;      //  Move to accept assign
                        break;
                    }
                    //  One character accepted; pc has been met
                    return new Token(str, Terminal.COLON, row, col);
                /*  State 2:
                 Accept :=
                 */
                case 2:
                    reader.nextChar();   //  keep the found token (pc met!)
                    return new Token(str, Terminal.ASSIGN, row, col);
                //  Postcondition: c points to the character after this token
            }
        }
        //  This should never happen
        System.out.println("ERROR: Reached endline for COLON FSA");
        return null;
    }

    /**
     * Tests to see if this is < or <= or <>
     * @param reader The file reader
     * @param row The row of the occurrence
     * @param col The col of the occurrence
     * @return The token containing the result
     */
    public static Token TEST_LTHAN(Reader reader, int row, int col) {
        String str = "";
        int state = 0;
        char c = reader.nextChar();
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
                    c = reader.peekChar();
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
                    return new Token(str, Terminal.LTHAN, row, col);
                /*  State 2:
                 Accept <=
                 */
                case 2:
                    reader.nextChar();   //  now points after = (pc met!)
                    return new Token(str, Terminal.LEQUAL, row, col);
                /*  State 3:
                 Accept <>
                 */
                case 3:
                    reader.nextChar();   //  now points after = (pc met!)
                    return new Token(str, Terminal.NEQUAL, row, col);
            }
        }
        //  This should never happen
        System.out.println("ERROR: Reached endline for LTHANFSA");
        return null;
    }
    /**
     * Tests to see if this is < or <=
     * @param reader The file reader
     * @param row The row of the occurrence
     * @param col The col of the occurrence
     * @return The result
     */
    public static Token TEST_GTHAN(Reader reader, int row, int col) {
        String str = "";
        char c = reader.nextChar();
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
                    c = reader.peekChar();
                    if (c == '=') {
                        str += c;
                        state = 2;
                        break;
                    }
                    //  Only accepted one character (pc met!)
                    return new Token(str, Terminal.GTHAN, row, col);
                /*  State 2
                 c is character after = (pc met!)

                 */
                case 2:
                    reader.nextChar();   //  accept the = so the pc is met
                    return new Token(str, Terminal.GEQUAL, row, col);
            }
        }
        //  This should never happen;
        System.out.println("ERROR: Reached endline for GTHAN FSA");
        return null;
    }
}
