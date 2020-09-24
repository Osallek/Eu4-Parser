package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzObject;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import com.osallek.eu4parser.common.ConditionsUtils;
import com.osallek.eu4parser.model.save.country.Country;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Condition {

    private final List<Condition> not;

    private final List<Condition> or;

    private final Map<String, String> conditions;

    private final Map<String, List<Condition>> scopes;

    public Condition(ClausewitzItem item) {
        this.not = item.getChildren("NOT").stream().map(Condition::new).collect(Collectors.toList());
        this.or = item.getChildren("OR").stream().map(Condition::new).collect(Collectors.toList());
        this.conditions = item.getChildren("AND")
                              .stream()
                              .map(ClausewitzItem::getVariables)
                              .flatMap(Collection::stream)
                              .collect(Collectors.toMap(ClausewitzObject::getName, ClausewitzVariable::getValue));
        item.getVariables().forEach(variable -> this.conditions.put(variable.getName(), variable.getValue()));
        this.scopes = item.getChildrenNot("NOT", "OR", "AND").stream().collect(Collectors.groupingBy(ClausewitzObject::getName,
                                                                                                     Collectors.mapping(Condition::new, Collectors.toList())));
    }

    public List<Condition> getNot() {
        return not;
    }

    public List<Condition> getOr() {
        return or;
    }

    public Map<String, String> getConditions() {
        return conditions;
    }

    public Map<String, List<Condition>> getScopes() {
        return scopes;
    }

    public boolean applyToCountry(Country country) {
        if (this.conditions.entrySet().stream().anyMatch(entry -> !applyConditionToCountry(country, entry.getKey(), entry.getValue()))) {
            return false;
        }

        return true;
    }

    private boolean applyConditionToCountry(Country country, String condition, String value) {
        return ConditionsUtils.applyConditionToCountry(country, condition, value);
    }
}
