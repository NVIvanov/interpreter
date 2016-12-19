package utils;

import java.util.List;

/**
 * @author nivanov
 *         on 12.12.16.
 */
public class ArrayVariable extends Variable {
    public ArrayVariable(String name, Class type, List value, Boolean immutable) {
        super(name, type, value, immutable);
    }

    public Object getValue(int index) {
        if (index < 0 || index >= getValue().size())
            throw new InterpreterException(InterpreterException.Type.ARRAY_OUT_OF_BOUND);
        return getValue().get(index);
    }

    @Override
    public List getValue() {
        return (List) value;
    }

    public void setValue(Integer index ,Object value) {
        Object tryGet = getValue(index);
        getValue().set(index, value);
    }
}
