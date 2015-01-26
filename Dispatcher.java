/* calls various fsa that scan for various tokens (first thing scanner calls)
   skips white space, peeks at first non white space character
   1) if letter call identifier fsa
   2) left, right () or digit
   .... if not the start of any token than throw an error (line number)
 */


/* function to skip over white space and place file pointer to the first
   possible character */
