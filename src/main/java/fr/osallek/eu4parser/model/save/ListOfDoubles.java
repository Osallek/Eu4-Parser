package fr.osallek.eu4parser.model.save;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;

import java.util.List;
import java.util.stream.Collectors;

public class ListOfDoubles {

    private final ClausewitzItem item;

    public ListOfDoubles(ClausewitzItem item) {
        this.item = item;
    }

    public List<String> getNames() {
        return this.item.getVariables().stream().map(ClausewitzVariable::getName).collect(Collectors.toList());
    }

    public boolean contains(String name) {
        return this.item.getVar(name) != null;
    }

    public Double get(String name) {
        return this.item.getVarAsDouble(name);
    }

    public void set(String name, Double aDouble) {
        this.item.setVariable(name, aDouble);
    }
}
