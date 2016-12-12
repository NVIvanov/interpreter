package utils;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

/**
 * @author nivanov
 * on 06.12.16.
 */
class Scope {
    static final Scope global = new Scope(null);
    private Scope parent;
    private Set<Variable> variableSet;

    Scope(Scope parent){
        this.parent = parent;
    }

    /**
     * Добавляет массив переменных
     * @param variables
     */
    void addVariables(Variable... variables){
        Collections.addAll(variableSet, variables);
    }

    /**
     * Возвращает Optional от переменной с указанным именем
     * @param name
     * @return найденная переменная
     */
    Optional<Variable> getVariable(String name) {
        return variableSet.stream().filter(variable -> variable.name.equals(name)).findAny();
    }
    /**
     * Удаляет переменную с указанным именем
     * @param name
     */
    void removeVariable(String name){
        variableSet.removeIf(variable -> variable.name.equals(name));
    }
}
