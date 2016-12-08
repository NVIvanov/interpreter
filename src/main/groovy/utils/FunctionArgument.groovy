package utils

/**
 * @author nivanov
 * on 08.12.16.
 */
class FunctionArgument {
    String name
    Class type

    FunctionArgument(name, type){
        this.name = name
        this.type = type
    }

    @Override
    boolean equals(Object obj) {
        if (obj == null)
            return false
        FunctionArgument arg = obj as FunctionArgument
        return name == arg.name
    }

    @Override
    int hashCode() {
        return name.hashCode()
    }
}
