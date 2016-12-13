package utils;

/**
 * @author nivanov
 * on 08.12.16.
 */
public class Variable{
    protected Object value;
    private String name;
    private Class type;
    private Boolean immutable;

    public Variable(String name,Class type,Object value,Boolean immutable) {
        this.name = name;
        this.type = type;
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

    public Class getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
