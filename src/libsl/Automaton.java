package libsl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Automaton {

    private static final String TEMP_STATE = "initstate Created;";

    private String name;
    private String type;
    private List<String> states;
    private List<Fun> funs;

    public Automaton(String name) {
        this.name = name;
        this.states = Collections.singletonList(TEMP_STATE);
        this.funs = new ArrayList<>();
        this.type = this.name;
    }

    public Automaton(String name, List<Fun> funs) {
        this.name = name;
        this.funs = funs;
        this.type = this.name;
        this.states = Collections.singletonList(TEMP_STATE);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getStates() {
        return states;
    }

    public boolean addState(String state) {
        return this.states.add(state);
    }

    public List<Fun> getFuns() {
        return funs;
    }

    public void setFuns(List<Fun> funs) {
        this.funs = funs;
    }

    public boolean addFun(Fun fun) {
        return funs.add(fun);
    }

    @Override
    public String toString() {
        return String.format("automaton %s : %s {%n%s%n %n%s%n}%n",
                getName(),
                getType(),
                getStates()
                        .stream()
                        .map(state -> String.format("\t%s", state))
                        .collect(Collectors.joining("\n")),
                getFuns()
                        .stream()
                        .map(fun -> String.format("\t%s", fun.toString()))
                        .collect(Collectors.joining("\n"))
        );
    }
}
