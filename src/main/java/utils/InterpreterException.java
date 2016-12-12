package utils;

/**
 * @author nivanov
 * on 08.12.16.
 */
public class InterpreterException extends RuntimeException {
    public InterpreterException(Type type, String message){
        super(type.toString() + " " + message);
    }

    public enum Type{
        UNEXPECTED_TOKEN, IDENTIFIER_NOT_FOUND, ILLEGAL_ARGUMENT_TYPE, ILLEGAL_OPERAND_TYPE, IDENTIFIER_ALREADY_EXISTS
    }
}
