package utils;

/**
 * @author nivanov
 * on 08.12.16.
 */
class FunctionArgument {
    private String name;
    private Class type;

    FunctionArgument(String name, Class type){
        this.name = name;
        this.type = type;
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

    String getName() {
        return name;
    }

    Class getType() {
        return type;
    }
}
