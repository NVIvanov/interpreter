package utils;

/**
 * @author nivanov
 * on 08.12.16.
 */
public class FunctionArgument {
    private String name;
    private Class type;
    private Object defaultValue;

    public FunctionArgument(String name, Class type, Object defaultValue){
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof FunctionArgument))
            return false;
        FunctionArgument arg = (FunctionArgument) obj;
        return name.equals(arg.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public String getName() {
        return name;
    }

    public Class getType() {
        return type;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }
}
