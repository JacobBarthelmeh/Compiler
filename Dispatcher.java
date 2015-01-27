/* calls various fsa that scan for various tokens (first thing scanner calls)
   skips white space, peeks at first non white space character
   1) if letter call identifier fsa
   2) left, right () or digit
   .... if not the start of any token than throw an error (line number)

   function to skip over white space and place file pointer to the first
   possible character */
public class Dispatcher {
    /*
        Note- I use String for characters because they are easy
        to match Regex. Java's String has native Regex functionality,
        making state machines easy to imitate
    */
    //  Prototype for peekChar()
    //  This should only read a char from the file without moving the file pointer
    public String peekChar() {
        //  Pseudocode:
        //  reader.read(filepointer);
    }
    //  Prototype for nextChar
    //  This should read a char from the file and move the file pointer
    public String nextChar() {
        String next = peekChar();
        //  Pseudocode:
        //  filepointer++;
        return next;
    }
    //  Get the next token.
    //  Should the Token class contain the String to be returned?
    public Token nextToken() {
        String next = peekChar();
        while (next.charAt(0) != "\u001a") {
            //  Match alphabet characters
            if (next.match("\\w") || next.charAt(0) == '\'') {
                return l_handler.getToken(next);
            }
            //  Match digit characters
            else if (next.match("\\d")) {
                return d_handler.getToken(next);
            }
            //  Symbol handler:
            //  Switch on the character
            else switch (next.charAt(0)) {
                case ':':
                    next = nextChar();
                    //  Symbol := is special
                    if (next.charAt(0) == '=') {
                        nextChar();
                        return MP_ASSIGN;
                    }
                    //  Otherwise accept :
                    return MP_COLON;
                case '>':
                    next = nextChar();
                    //  Symbol >= is special
                    if (next.charAt(0) == '=') {
                        nextChar();
                        return MP_GTHAN;
                    }
                    //  Otherwise accept :
                    return MP_GTHAN;
                case '<':
                    next = nextChar();
                    //  Symbol <= is special
                    if (next.charAt(0) == '=') {
                        nextChar();
                        return MP_LTHAN;
                    }
                    //  Symbol <> is also special
                    else if (next.charAt(0) == '>') {
                        nextChar();
                        return MP_NEQUAL;
                    }
                    //  Otherwise accept :
                    return MP_LTHAN;
                case ',':
                    //  Proceed to the next character to advance the file pointer
                    nextChar();
                    return MP_COMMA;
                case '=':
                    //  Proceed to the next character to advance the file pointer
                    nextChar();
                    return MP_EQUAL;
                case '-':
                    //  Proceed to the next character to advance the file pointer
                    nextChar();
                    return MP_MINUS;
                case '(':
                    //  Proceed to the next character to advance the file pointer
                    nextChar(); 
                    return MP_LPAREN;
                case '.':
                    //  Proceed to the next character to advance the file pointer
                    nextChar(); 
                    return MP_PERIOD;
                case '+':
                    //  Proceed to the next character to advance the file pointer
                    nextChar(); 
                    return MP_PLUS;
                case ')':
                    //  Proceed to the next character to advance the file pointer
                    nextChar(); 
                    return MP_RPAREN;
                case ';':
                    //  Proceed to the next character to advance the file pointer
                    nextChar(); 
                    return MP_SCOLON;
                case '*':
                    //  Proceed to the next character to advance the file pointer
                    nextChar(); 
                    return MP_TIMES;
                case '\r':case '\n':
                    //  Problem arises for the \r\n and \n\r cases
                    next = peekChar();
                    //  Only increment 
                    if (next.charAt(0) == '\r' || nextc.charAt(0) == '\n') {
                        next = nextChar();
                    }
                    next = nextChar();
                    linenumber++;
                    break;
                case '\r':case ' ':
                    next = nextChar();
                    break;
                default:
                    return new Token("Unknown symbol " + next + " at line " + linenumber, Token.ID.ERROR);
            }
        }
        return Token.MP_ERROR;
    }

/*
    Variables and Construction
*/

    //  Handlers for when symbols are found
    private LetterHandler l_handler;
    private DigitHandler d_handler;

    //  File handling
    private BufferedReader reader;
    private int linenumber;


    public Dispatcher() {
        linenumber = 0;
        //  prepare the handlers
        l_handler = new LetterHandler(this);
        d_handler = new DigitHandler(this);
    }
}