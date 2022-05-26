package fr.osallek.eu4parser.model.save;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;

import java.util.List;

public record ListOfDoubles(ClausewitzItem item) {

    public List<String> getNames() {
        return this.item.getVariables().stream().map(ClausewitzVariable::getName).toList();
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
