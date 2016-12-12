package utils;

/**
 * @author nivanov
 * on 08.12.16.
 */
public class Variable extends FunctionArgument{
    private Object value;
    private Boolean immutable;

    public Variable(String name,Class type,Object value,Boolean immutable) {
        super(name, type);
        this.value = value;
        this.immutable = immutable;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Boolean getImmutable() {
        return immutable;
    }
}
