package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Icon {

    private final String name;

    private final Condition allow;

    private final Map<String, String> modifiers;

    public Icon(ClausewitzItem item) {
        this.name = item.getName();
        this.allow = new Condition(item.getChild("allow"));
        this.modifiers = item.getVariables()
                             .stream()
                             .collect(Collectors.toMap(ClausewitzVariable::getName, ClausewitzVariable::getValue, (a, b) -> b, LinkedHashMap::new));
    }

    public String getName() {
        return name;
    }

    public Condition getAllow() {
        return allow;
    }

    public Map<String, String> getModifiers() {
        return modifiers;
    }
}
