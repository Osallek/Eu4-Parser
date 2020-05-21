package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

import java.util.List;
import java.util.stream.Collectors;

public class Personalities {

    private final ClausewitzItem item;

    public Personalities(ClausewitzItem item) {
        this.item = item;
    }

    public List<String> getPersonalities() {
        return this.item.getVariables()
                        .stream()
                        .map(ClausewitzVariable::getName)
                        .collect(Collectors.toList());
    }

    public void addPersonality(String name) {
        this.item.addVariable(name, true);
    }

    public void changePersonality(int index, String name) {
        this.item.setVariable(index, name);
    }

    public void removePersonality(int index) {
        this.item.removeVariable(index);
    }

    public void removePersonality(String personality) {
        this.item.removeVariable(personality);
    }
}
