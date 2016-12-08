package utils

/**
 * @author nivanov
 * on 08.12.16.
 */
class Variable extends FunctionArgument{
    String name
    Class type
    Object value
    Boolean const

    Variable(name, type, value, const) {
        super(name, type)
        this.value = value
        this.const = const
    }
}
