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

    /**
     * Добавляет массив переменных
     * @param variables
     */
    void addVariables(Variable... variables){
        for (Variable variable: variables)
            variableSet.add(variable)
    }

    /**
     * Возвращает Optional от переменной с указанным именем
     * @param name
     * @return найденная переменная
     */
    Optional<Variable> getVariable(name){
        variableSet.stream().filter{ variable ->
            (variable.name == name)
        }.findAny()
    }

    /**
     * Удаляет переменную с указанным именем
     * @param name
     */
    void removeVariable(name){
        variableSet.removeIf{variable -> (variable.name == name) }
    }
}
