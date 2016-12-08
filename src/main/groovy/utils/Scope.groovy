package utils
/**
 * @author nivanov
 * on 06.12.16.
 */
class Scope {
    static Scope global = new Scope(null)
    Scope parent
    Set<Variable> variableSet

    Scope(parent){
        this.parent = parent
    }

    void addVariables(Variable... variables){
        for (Variable variable: variables)
            variableSet.add(variable)
    }

    Optional<Variable> getVariable(name){
        variableSet.stream().filter{ variable ->
            (variable.name == name)
        }.findAny()
    }

    void removeVariable(name){
        variableSet.removeIf{variable -> (variable.name == name) }
    }
}