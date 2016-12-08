package utils

/**
 * @author nivanov
 * on 08.12.16.
 */
class Error {
    String message
    Type type

    Error(type, message){
        this.type = type
        this.message = message
    }

    enum Type{
        UNEXPECTED_TOKEN, IDENTIFIER_NOT_FOUND, ILLEGAL_ARGUMENT_TYPE, ILLEGAL_OPERAND_TYPE, IDENTIFIER_ALREADY_EXISTS
    }
}
