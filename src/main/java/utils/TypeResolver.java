package utils;

/**
 * @author nivanov
 *         on 12.12.16.
 */
public class TypeResolver {

    public static Object getValue(String typeName, String stringValue){
        try {
            if (typeName.equals("int") || typeName.equals("cint"))
                return Integer.valueOf(stringValue);
            if (typeName.equals("bool") || typeName.equals("cbool")){
                Boolean val = "true".equals(stringValue) ? Boolean.TRUE :
                        ("false".equals(stringValue) ? Boolean.FALSE : null);
                if (val == null)
                    throw new InterpreterException(InterpreterException.Type.ILLEGAL_ARGUMENT_TYPE,
                            "UNKNOWN TYPE");
                return val;
            }
        }catch (Exception e){
            throw new InterpreterException(InterpreterException.Type.ILLEGAL_ARGUMENT_TYPE, "UNKNOWN TYPE");
        }
        return null;
    }

    public static Object getValue(String value){
        if (value.equals("true") || value.equals("false"))
            return Boolean.valueOf(value);
        try {
            return Integer.valueOf(value);
        }catch (Exception e){
            throw new InterpreterException(InterpreterException.Type.ILLEGAL_OPERAND_TYPE, "UNKNOWN OPERAND TYPE");
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static Class resolveType(String value){
        if (value.equals("true") || value.equals("false"))
            return Boolean.class;
        try {
            Integer.valueOf(value);
        }catch (Exception e){
            throw new InterpreterException(InterpreterException.Type.ILLEGAL_OPERAND_TYPE, "UNKNOWN OPERAND TYPE");
        }
        return Integer.class;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static Class resolveArrayType(String type){
        if (type.equals("boolarray") || type.equals("bool2array"))
            return Boolean.class;
        return Integer.class;
    }

    public static boolean isImmutable(String name){
        return "cint".equals(name) || "cbool".equals(name);
    }
}
