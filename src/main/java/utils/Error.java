package utils;

/**
 * @author nivanov
 * on 08.12.16.
 */
class Error {
    private String message;
    private Type type;

    Error(Type type, String message){
        this.type = type;
        this.message = message;
    }

    enum Type{
        UNEXPECTED_TOKEN, IDENTIFIER_NOT_FOUND, ILLEGAL_ARGUMENT_TYPE, ILLEGAL_OPERAND_TYPE, IDENTIFIER_ALREADY_EXISTS
    }
}
