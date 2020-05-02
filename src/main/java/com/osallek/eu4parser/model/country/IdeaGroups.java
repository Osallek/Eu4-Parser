package com.osallek.eu4parser.model.country;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzObject;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class IdeaGroups {

    private final ClausewitzItem item;

    public IdeaGroups(ClausewitzItem item) {
        this.item = item;
    }

    public Map<String, Integer> getIdeaGroups() {
        return this.item.getVariables()
                        .stream()
                        .collect(Collectors.toMap(ClausewitzObject::getName, ClausewitzVariable::getAsInt, (a, b) -> a, LinkedHashMap::new));
    }

    public void addIdeaGroup(String name, int level) {
        ClausewitzVariable var = this.item.getVar(name);

        if (level < 0) {
            level = 0;
        } else if (level > 7) {
            level = 7;
        }

        if (var != null) {
            var.setValue(level);
        } else {
            this.item.addVariable(name, level);
        }
    }

    public void removeIdeaGroup(int index) {
        this.item.removeVariable(index);
    }

    public void removeIdeaGroup(String ideaGroup) {
        this.item.removeVariable(ideaGroup);
    }
}
