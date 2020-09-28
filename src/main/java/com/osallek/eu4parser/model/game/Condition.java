package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzObject;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import com.osallek.eu4parser.common.ConditionsUtils;
import com.osallek.eu4parser.model.save.country.Country;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Condition {

    private final String name;

    private final Map<String, List<String>> conditions;

    private final List<Condition> scopes;

    public Condition(ClausewitzItem item) {
        this.name = item.getName();
        this.conditions = item.getVariables()
                              .stream()
                              .collect(Collectors.groupingBy(ClausewitzObject::getName, Collectors.mapping(ClausewitzVariable::getValue, Collectors.toList())));
        this.scopes = item.getChildren().stream().map(Condition::new).collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public Map<String, List<String>> getConditions() {
        return conditions;
    }

    public List<Condition> getScopes() {
        return scopes;
    }

    public boolean apply(Country root, Country from) {
        if (this.conditions.entrySet()
                           .stream()
                           .anyMatch(entry -> entry.getValue()
                                                   .stream()
                                                   .anyMatch(s -> !ConditionsUtils.applyConditionToCountry(root, root, from, entry.getKey(), s)))) {
            return false;
        }

        if (this.scopes.stream().anyMatch(scope -> !ConditionsUtils.applyScopeToCountry(root, from, scope))) {
            return false;
        }

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Condition)) {
            return false;
        }

        Condition condition = (Condition) o;
        return Objects.equals(name, condition.name) &&
               Objects.equals(conditions, condition.conditions) &&
               Objects.equals(scopes, condition.scopes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, conditions, scopes);
    }

    @Override
    public String toString() {
        return name;
    }
}
