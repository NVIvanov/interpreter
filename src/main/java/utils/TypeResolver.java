package utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
                    throw new InterpreterException(InterpreterException.Type.ILLEGAL_ARGUMENT_TYPE
                    );
                return val;
            }
        }catch (Exception e){
            throw new InterpreterException(InterpreterException.Type.ILLEGAL_ARGUMENT_TYPE);
        }
        return null;
    }

    public static Object getValue(String value){
        if (value.equals("true") || value.equals("false"))
            return Boolean.valueOf(value);
        try {
            return Integer.valueOf(value);
        }catch (Exception e){
            throw new InterpreterException(InterpreterException.Type.ILLEGAL_OPERAND_TYPE);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static Class resolveType(String value){
        if (value.equals("true") || value.equals("false"))
            return Boolean.class;
        try {
            Integer.valueOf(value);
        }catch (Exception e){
            throw new InterpreterException(InterpreterException.Type.ILLEGAL_OPERAND_TYPE);
        }
        return Integer.class;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static Class resolveArrayType(String type){
        if (type.equals("boolarray") || type.equals("bool2array"))
            return Boolean.class;
        return Integer.class;
    }

    public static Class resolveArrayTypeByValue(String value){
        String[] tmp = value.split("\\[")[1].split("]");
        if (tmp.length == 0)
            return Integer.class;
        String values = tmp[0];
        values = values.split(",")[0];
        try {
            Integer.valueOf(values);
            return Integer.class;
        }catch (Exception e){
            return Boolean.class;
        }
    }

    public static List parseArrayInit(String value){
        String values = value.split("\\[")[1].split("]")[0];
        return Arrays.asList(values.split(",")).stream().map(String::trim).collect(Collectors.toList());
    }

    public static boolean isImmutable(String name){
        return "cint".equals(name) || "cbool".equals(name);
    }
}
