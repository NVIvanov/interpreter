package utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author nivanov
 * on 06.12.16.
 */
public class Scope {
    public static final Scope global = new Scope(null);
    private Scope parent;
    private Set<Variable> variableSet = new HashSet<>();

    Scope(Scope parent){
        this.parent = parent;
    }

    /**
     * Добавляет массив переменных
     * @param variables
     */
    public void addVariables(Variable... variables){
        Collections.addAll(variableSet, variables);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    /**
     * Возвращает Optional от переменной с указанным именем
     * @param name
     * @return найденная переменная
     */
    public Optional<Variable> getVariable(String name) {
        Optional<Variable> variableOpt = variableSet.stream().filter(variable -> variable.getName().equals(name)).findAny();
        if (!variableOpt.isPresent() && parent != null)
            return parent.getVariable(name);
        return variableOpt;
    }
    /**
     * Удаляет переменную с указанным именем
     * @param name
     */
    public void removeVariable(String name){
        variableSet.removeIf(variable -> variable.getName().equals(name));
    }

    @Override
    public String toString() {
        String parent = this.parent != null? this.parent.toString() : "";
        StringBuilder builder = new StringBuilder();
        variableSet.forEach(variable -> builder.append(variable.getName()).append("-").append(variable.getType()).append("-")
                .append(variable.getValue()).append("-").append(variable.getImmutable()).append("\r\n"));
        return builder.insert(0, parent).toString();
    }
}
