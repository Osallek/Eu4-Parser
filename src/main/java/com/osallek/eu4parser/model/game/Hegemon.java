package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Hegemon {

    private final String name;

    private String localizedName;

    private final Condition allow;

    private final Map<String, String> base;

    private final Map<String, String> scale;

    private final Map<String, String> max;

    public Hegemon(ClausewitzItem item) {
        this.name = item.getName();

        ClausewitzItem child = item.getChild("base");
        this.base = child == null ? new LinkedHashMap<>() : child.getVariables()
                                                                 .stream()
                                                                 .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                                                           ClausewitzVariable::getValue,
                                                                                           (a, b) -> b,
                                                                                           LinkedHashMap::new));

        child = item.getChild("scale");
        this.scale = child == null ? new LinkedHashMap<>() : child.getVariables()
                                                                  .stream()
                                                                  .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                                                            ClausewitzVariable::getValue,
                                                                                            (a, b) -> b,
                                                                                            LinkedHashMap::new));

        child = item.getChild("max");
        this.max = child == null ? new LinkedHashMap<>() : child.getVariables()
                                                                .stream()
                                                                .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                                                          ClausewitzVariable::getValue,
                                                                                          (a, b) -> b,
                                                                                          LinkedHashMap::new));

        child = item.getChild("allow");
        this.allow = child == null ? null : new Condition(child);
    }

    public String getName() {
        return name;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public Condition getAllow() {
        return allow;
    }

    public Map<String, String> getBase() {
        return base;
    }

    public Map<String, String> getScale() {
        return scale;
    }

    public Map<String, String> getMax() {
        return max;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Hegemon that = (Hegemon) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
