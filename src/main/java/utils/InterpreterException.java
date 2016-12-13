package utils;

import java.io.PrintStream;

/**
 * @author nivanov
 * on 08.12.16.
 */
public class InterpreterException extends RuntimeException {
    private Type type;
    private String message;
    private LanguageFunction function;

    public InterpreterException(Type type, String message){
        super(type.toString() + " - " + message);
        this.type = type;
        this.message = message;
        function = Stack.getInstance().currentFunction();
    }

    public String getErrorMessage() {
        return this.message;
    }

    public Type getType() {
        return type;
    }

    @Override
    public void printStackTrace(PrintStream s) {
        s.println("ERROR in function \"" + function.getName() + "\":" + function.getCurrentLine());
        super.printStackTrace(s);
    }

    public enum Type{
        UNEXPECTED_TOKEN, IDENTIFIER_NOT_FOUND, ILLEGAL_ARGUMENT_TYPE, ILLEGAL_OPERAND_TYPE, IDENTIFIER_ALREADY_EXISTS
    }
}
