/* Generated By:JavaCC: Do not edit this line. TokenMgrError.java Version 6.0 */
 /* JavaCCOptions: */
 /* ================================================================
 * JSQLParser : java based sql parser 
 * ================================================================
 *
 * Project Info:  http://jsqlparser.sourceforge.net
 * Project Lead:  Leonardo Francalanci (leoonardoo@yahoo.it);
 *
 * (C) Copyright 2004, by Leonardo Francalanci
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 */
package net.sf.jsqlparser.parser;

/**
 * Token Manager Error.
 */
public class TokenMgrError extends Error {

    /**
     * The version identifier for this Serializable class. Increment only if the
     * <i>serialized</i> form of the class changes.
     */
    private static final long serialVersionUID = 1L;

    /*
   * Ordinals for various reasons why an Error of this type can be thrown.
     */
    /**
     * Lexical error occurred.
     */
    static final int LEXICAL_ERROR = 0;

    /**
     * An attempt was made to create a second instance of a static token
     * manager.
     */
    static final int STATIC_LEXER_ERROR = 1;

    /**
     * Tried to change to an invalid lexical state.
     */
    static final int INVALID_LEXICAL_STATE = 2;

    /**
     * Detected (and bailed out of) an infinite loop in the token manager.
     */
    static final int LOOP_DETECTED = 3;

    /**
     * Indicates the reason why the exception is thrown. It will have one of the
     * above 4 values.
     */
    int errorCode;

    /**
     * Replaces unprintable characters by their escaped (or unicode escaped)
     * equivalents in the given string
     *
     * @param aValue A string where unprintable characters are escaped.
     * @return String with escaped unprintable characters.
     */
    protected static final String addEscapes(String aValue) {
        StringBuilder retval = new StringBuilder();
        char ch;
        for (int i = 0; i < aValue.length(); i++) {
            switch (aValue.charAt(i)) {
                case 0:
                    continue;
                case '\b':
                    retval.append("\\b");
                    continue;
                case '\t':
                    retval.append("\\t");
                    continue;
                case '\n':
                    retval.append("\\n");
                    continue;
                case '\f':
                    retval.append("\\f");
                    continue;
                case '\r':
                    retval.append("\\r");
                    continue;
                case '\"':
                    retval.append("\\\"");
                    continue;
                case '\'':
                    retval.append("\\\'");
                    continue;
                case '\\':
                    retval.append("\\\\");
                    continue;
                default:
                    if ((ch = aValue.charAt(i)) < 0x20 || ch > 0x7e) {
                        String s = "0000" + Integer.toString(ch, 16);
                        retval.append("\\u").append(s.substring(s.length() - 4, s.length()));
                    } else {
                        retval.append(ch);
                    }
            }
        }
        return retval.toString();
    }

    /**
     * Returns a detailed message for the Error when it is thrown by the token
     * manager to indicate a lexical error. Parameters : EOFSeen : indicates if
     * EOF caused the lexical error curLexState : lexical state in which this
     * error occurred errorLine : line number when the error occurred
     * errorColumn : column number when the error occurred errorAfter : prefix
     * that was seen before this error occurred curchar : the offending
     * character Note: You can customize the lexical error message by modifying
     * this method.
     */
    protected static String LexicalError(boolean EOFSeen, int lexState, int errorLine, int errorColumn, String errorAfter, char curChar) {
        return ("Lexical error at line "
                + errorLine + ", column "
                + errorColumn + ".  Encountered: "
                + (EOFSeen ? "<EOF> " : ("\"" + addEscapes(String.valueOf(curChar)) + "\"") + " (" + (int) curChar + "), ")
                + "after : \"" + addEscapes(errorAfter) + "\"");
    }

    /**
     * You can also modify the body of this method to customize your error
     * messages. For example, cases like LOOP_DETECTED and INVALID_LEXICAL_STATE
     * are not of end-users concern, so you can return something like :
     *
     * "Internal Error : Please file a bug report .... "
     *
     * from this method for such cases in the release version of your parser.
     */
    public String getMessage() {
        return super.getMessage();
    }

    /*
   * Constructors of various flavors follow.
     */
    /**
     * No arg constructor.
     */
    public TokenMgrError() {
    }

    /**
     * Constructor with message and reason.
     */
    public TokenMgrError(String message, int reason) {
        super(message);
        errorCode = reason;
    }

    /**
     * Full Constructor.
     */
    public TokenMgrError(boolean EOFSeen, int lexState, int errorLine, int errorColumn, String errorAfter, char curChar, int reason) {
        this(LexicalError(EOFSeen, lexState, errorLine, errorColumn, errorAfter, curChar), reason);
    }
}
/* JavaCC - OriginalChecksum=fce95be8be002c1d316e0b1420f0aae1 (do not edit this line) */
