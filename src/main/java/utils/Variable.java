package utils;

/**
 * @author nivanov
 * on 08.12.16.
 */
class Variable extends FunctionArgument{
    String name;
    Class type;
    Object value;
    Boolean immutable;

    Variable(String name,Class type,Object value,Boolean immutable) {
        super(name, type);
        this.value = value;
        this.immutable = immutable;
    }
}
