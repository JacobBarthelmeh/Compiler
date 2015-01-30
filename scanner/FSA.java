package scanner;

import compiler.Token;
import javax.swing.JOptionPane;

public class FSA {

    /**
     * Test a digit and return the token
     *
     * @param d
     * @return
     */
    public static Token TEST_DIGIT(Dispatcher d) {
        String str = "";
        int state = 0;
        Token.ID id = Token.ID.INTEGER;
        char c = d.nextChar();  //  1 char accepted
        while (c != '\u001a') {
            switch (state) {
                case 0:
                    if (c == '1' || (c >= '0' && c <= '9')) {
                        //stay in state 0
                        str += c;
                        state = 0;
                    } else if (c == '.') {
                        str += c;
                        state = 1;
                    } else if (c == 'E' || c == 'e') {
                        str += c;
                        state = 3;
                    } else {
                        //other character found
                        d.ungetChar(c);
                        if (str.length() > 0) {
                            return new Token(str, id);
                        } else {
                            //no digit
                            return null;
                        }
                    }
                    break;

                case 1:
                    if (c >= '0' && c <= '9') {
                        //transition to state 2
                        str += c;
                        id = Token.ID.FIXED;
                        state = 2;
                    } else {
                        //other character found
                        d.ungetChar('.');
                        d.ungetChar(c);
                        return new Token(str.subSequence(0, str.length() - 2).toString(), id);
                    }
                    break;
                case 2:
                    if (c >= '0' && c <= '9') {
                        //stay in state 2
                        str += c;
                        state = 2;
                    } else if (c == 'E' || c == 'e') {
                        char peek = d.peekChar();
                        if (peek != '+' || peek != '-') {
                            d.ungetChar(c);
                            return new Token(str, id);
                        }
                        str += c;
                        state = 3;
                    } else {
                        //other character found
                        d.ungetChar(c);
                        return new Token(str, id);
                    }
                    break;
                case 3:
                    if (c == '+' || c == '-') {
                        //transition to state 4
                        str += c;
                        if (id == Token.ID.FIXED) {
                            id = Token.ID.FIXED_LIT;
                        } else {
                            id = Token.ID.INTEGER_LIT;
                        }
                        state = 4;
                    }
                    break;
                case 4:
                    if (c >= '0' && c <= '9') {
                        //stay in state 4
                        str += c;
                        state = 4;
                    } else {
                        state = -1;
                    }
                    break;
                default:
                    //you lose
                    JOptionPane.showMessageDialog(null, "you lose");

            }
            c = d.nextChar();
        }

        /* Accept state
         state 0
         all and only digits
        
         state 2
         digits . digits
        
         state 5
         digits (. digits) Ee +- digits
         */
        if (state == 0) {
            if (str.length() > 0) {
                return new Token(str, Token.ID.INTEGER);
            }
        }
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
        return null;
    }

    public static Token TEST_LETTER(Dispatcher d) {
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
        char c = d.nextChar();
        String str = "" + c;
        //  Accept all consecutive alphanumeric characters (with _ included)
        while (c != '\u001a') {
            c = d.peekChar();
            //  Note- numbers are permitted after the first letter is read
            if (("" + c).matches("(a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z|A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z|0|1|2|3|4|5|6|7|8|9|\\_)")) {
                str += c;
                switch(state) {
					//  State 0: Accept either _ or letter
					//      If _ is accepted, move to state 1
					//      If letter is accepted, move to state 2
					case 0:
						if (c == '_') {
							state = 1;
						}
						else { // alphabetic character received
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
               			}
               			// if it isn't alphanumeric return null
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
               			}
               			// '_' found
               			else if (c == '_') {
               				state = 3;
               			}
               			else {
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
               			}
               			// No alphanumeric found, reject
               			else {
               				return null;
               			}
               	}
            }
            d.nextChar();
        }
        if (state == 4) {
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
				default: return new Token("identifer:" + str, Token.ID.IDENTIFIER);
			}
        }
        return null;
    }

    public static Token TEST_STRING_LIT(Dispatcher d) {
        String str = "";
        int state = 0;
        char c = d.nextChar();  //  1 char accepted

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
                    c = d.nextChar();     //  2 + n characters accepted
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
                + "Quotation mark opened but not closed.");
        System.exit(0);
        return null;
    }

    public static Token TEST_COLON(Dispatcher d) {
        String str = "";
        int state = 0;
        char c = d.nextChar();
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
                    c = d.peekChar();   //  Don't want to accept always
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
                    d.nextChar();   //  keep the found token (pc met!)
                    return new Token(str, Token.ID.ASSIGN);
                //  Postcondition: c points to the character after this token
            }
        }
        //  This should never happen
        return null;
    }

    public static Token TEST_LTHAN(Dispatcher d) {
        String str = "";
        int state = 0;
        char c = d.nextChar();
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
                    c = d.peekChar();
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
                    d.nextChar();   //  now points after = (pc met!)
                    return new Token(str, Token.ID.LEQUAL);
                /*  State 3:
                 Accept <>
                 */
                case 3:
                    d.nextChar();   //  now points after = (pc met!)
                    return new Token(str, Token.ID.NEQUAL);
            }
        }
        //  This should never happen
        return null;
    }

    public static Token TEST_GTHAN(Dispatcher d) {
        String str = "";
        char c = d.nextChar();
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
                    c = d.peekChar();
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
                    d.nextChar();   //  accept the = so the pc is met
                    return new Token(str, Token.ID.GEQUAL);
            }
        }
        //  This should never happen;
        return null;
    }

    public static Token TEST_COMMA(Dispatcher d) {
        char c = d.nextChar();
        if (c == ',') {
            return new Token(",", Token.ID.COMMA);
        }
        //  This should never happen
        return null;
    }

    public static Token TEST_EQUAL(Dispatcher d) {
        char c = d.nextChar();
        if (c == '=') {
            return new Token("=", Token.ID.EQUAL);
        }
        //  This should never happen
        return null;
    }

    public static Token TEST_FLOAT_DIVIDE(Dispatcher d) {
        char c = d.nextChar();
        if (c == '/') {
            return new Token("/", Token.ID.FLOAT_DIVIDE);
        }
        //  This should never happen
        return null;
    }

    public static Token TEST_LPAREN(Dispatcher d) {
        char c = d.nextChar();
        if (c == '(') {
            return new Token("(", Token.ID.LPAREN);
        }
        //  This should never happen
        return null;
    }

    public static Token TEST_RPAREN(Dispatcher d) {
        char c = d.nextChar();
        if (c == ')') {
            return new Token(")", Token.ID.RPAREN);
        }
        //  This should never happen
        return null;
    }

    public static Token TEST_MINUS(Dispatcher d) {
        char c = d.nextChar();
        if (c == '-') {
            return new Token("-", Token.ID.MINUS);
        }
        //  This should never happen
        return null;
    }

    public static Token TEST_PERIOD(Dispatcher d) {
        char c = d.nextChar();
        if (c == '.') {
            return new Token(".", Token.ID.PERIOD);
        }
        //  This should never happen
        return null;
    }

    public static Token TEST_PLUS(Dispatcher d) {
        char c = d.nextChar();
        if (c == '+') {
            return new Token("+", Token.ID.PLUS);
        }
        //  This should never happen
        return null;
    }

    public static Token TEST_SCOLON(Dispatcher d) {
        char c = d.nextChar();
        if (c == ';') {
            return new Token(";", Token.ID.SCOLON);
        }
        //  This should never happen
        return null;
    }

    public static Token TEST_TIMES(Dispatcher d) {
        char c = d.nextChar();
        if (c == '*') {
            return new Token("*", Token.ID.TIMES);
        }
        //  This should never happen
        return null;
    }
}
