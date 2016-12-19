package utils;

import java.io.PrintStream;

/**
 * @author nivanov
 * on 08.12.16.
 */
public class InterpreterException extends RuntimeException {
    private Type type;
    private LanguageFunction function;

    public InterpreterException(Type type){
        super(type.toString());
        this.type = type;
        function = Stack.getInstance().currentFunction();
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
        ARRAY_OUT_OF_BOUND, UNEXPECTED_TOKEN, IDENTIFIER_NOT_FOUND, ILLEGAL_ARGUMENT_TYPE, ILLEGAL_OPERAND_TYPE, IDENTIFIER_ALREADY_EXISTS
    }
}
