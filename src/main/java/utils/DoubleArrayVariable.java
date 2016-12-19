package utils;

import java.util.List;

/**
 * @author nivanov
 *         on 12.12.16.
 */
public class DoubleArrayVariable extends ArrayVariable {
    public DoubleArrayVariable(String name, Class type, List<List> value, Boolean immutable) {
        super(name, type, value, immutable);
    }

    @Override
    public List<List> getValue() {
        return (List<List>) value;
    }

    public Object getValue(int index1, int index2){
        if (index1 < 0 || index1 >= getValue().size())
            throw new InterpreterException(InterpreterException.Type.ILLEGAL_ARGUMENT_TYPE);
        List list = getValue().get(index1);
        if (index2 < 0 || index2 >= list.size())
            throw new InterpreterException(InterpreterException.Type.ILLEGAL_ARGUMENT_TYPE);
        return getValue().get(index1).get(index2);
    }

    public void setValue(int index1, int index2, Object value){
        Object tryGet = getValue(index1, index2);
        getValue().get(index1).set(index2, value);
    }
}
