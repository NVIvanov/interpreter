package utils;

import java.util.*;

/**
 * @author nivanov
 * on 06.12.16.
 */
public class Stack {
    static private Stack instance;
    private final List<LanguageFunction> functionDefinitions = new ArrayList<>();
    private final Deque<LanguageFunction> current = new ArrayDeque<>();

    private Stack(){
        functionDefinitions.add(LanguageFunction.main);
        functionDefinitions.add(LanguageFunction.printInt);
        functionDefinitions.add(LanguageFunction.square);
        functionDefinitions.add(LanguageFunction.forw);
        functionDefinitions.add(LanguageFunction.back);
        functionDefinitions.add(LanguageFunction.right);
        functionDefinitions.add(LanguageFunction.left);
        functionDefinitions.add(LanguageFunction.getb);
        functionDefinitions.add(LanguageFunction.getr);
        functionDefinitions.add(LanguageFunction.getl);
        functionDefinitions.add(LanguageFunction.getf);
        functionDefinitions.add(LanguageFunction.pushB);
        functionDefinitions.add(LanguageFunction.pushF);
        functionDefinitions.add(LanguageFunction.pushL);
        functionDefinitions.add(LanguageFunction.pushR);
        functionDefinitions.add(LanguageFunction.undo);
        current.addLast(LanguageFunction.main);
    }

    /**
     * Проверяет существование функции с указанным именем
     * @param name имя функции
     * @return true, если функция существует.
     */
    public boolean functionWithSuchNameExists(String name){
        return functionDefinitions.stream().anyMatch(function -> function.getName().equals(name));
    }

    /**
     * Проверяет существование функции с указанными именем и набором аргументов
     * @param name имя функции
     * @param types список аргументов
     * @return найденную функцию
     */
    public Optional<LanguageFunction> functionWithSuchArgumentsExists(String name, List<Class> types, List<Class> returnTypes){
        return functionDefinitions.stream().filter(function -> function.getName().equals(name))
                .filter(function -> {
            if (function.getArguments().size() != types.size())
                return false;
            for (int i = 0; i < function.getArguments().size(); i++)
                if (function.getArguments().get(i).getType() != types.get(i) && types.get(i) != null) // can be null class when argument omitted
                    return false;
            if (function.getReturnValues().size() != returnTypes.size())
                return false;
            for (int i = 0; i < function.getReturnValues().size(); i++)
                if (function.getReturnValues().get(i).getType() != returnTypes.get(i) && returnTypes.get(i) != null) // can be null class when argument omitted
                    return false;
            return true;
        }).findAny();
    }

    public LanguageFunction currentFunction(){
        return current.getLast();
    }

    public void removeLast(){
        current.removeLast();
    }

    public void putFunction(LanguageFunction function){
        current.addLast(function);
        function.createScope();
    }

    public static Stack getInstance(){
        if (instance == null)
            instance = new Stack();
        return instance;
    }

    public void createDefinition(LanguageFunction function){
        functionDefinitions.add(function);
    }
}
