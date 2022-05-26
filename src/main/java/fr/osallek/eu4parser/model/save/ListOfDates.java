package fr.osallek.eu4parser.model.save;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record ListOfDates(ClausewitzItem item) {

    public List<String> getNames() {
        return this.item.getVariables().stream().map(ClausewitzVariable::getName).toList();
    }

    public Map<String, LocalDate> getAll() {
        return this.item.getVariables().stream().collect(Collectors.toMap(ClausewitzVariable::getName, ClausewitzVariable::getAsDate));
    }

    public boolean contains(String name) {
        return this.item.getVar(name) != null;
    }

    public LocalDate get(String name) {
        return this.item.getVarAsDate(name);
    }

    public void set(String name, LocalDate date) {
        this.item.setVariable(name, date);
    }

    public void remove(String name) {
        this.item.removeVariable(name);
    }
}
