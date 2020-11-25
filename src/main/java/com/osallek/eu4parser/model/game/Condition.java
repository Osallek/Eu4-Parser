package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import com.osallek.eu4parser.common.ConditionsUtils;
import com.osallek.eu4parser.model.save.country.Country;
import com.osallek.eu4parser.model.save.country.Leader;
import com.osallek.eu4parser.model.save.country.LeaderType;
import com.osallek.eu4parser.model.save.province.SaveProvince;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Condition {

    private String name;

    private Map<String, List<String>> conditions;

    private List<Condition> scopes;

    @SafeVarargs
    public Condition(Pair<String, String>... conditions) {
        this.conditions = Arrays.stream(conditions).collect(Collectors.groupingBy(Pair::getKey, Collectors.mapping(Pair::getValue, Collectors.toList())));
    }

    @SafeVarargs
    public Condition(String scope, Pair<String, String>... conditions) {
        this.scopes = new ArrayList<>();
        Condition condition = new Condition(conditions);
        condition.name = scope;
        this.scopes.add(condition);
    }

    public Condition(ClausewitzItem item) {
        this.name = item.getName();
        this.conditions = item.getVariables()
                              .stream()
                              .collect(Collectors.groupingBy(var -> var.getName().toLowerCase(),
                                                             Collectors.mapping(ClausewitzVariable::getValue, Collectors.toList())));
        this.scopes = item.getChildren().stream().map(Condition::new).collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public Map<String, List<String>> getConditions() {
        return conditions;
    }

    public String getCondition(String condition) {
        return (this.conditions != null && this.conditions.containsKey(condition.toLowerCase())) ? this.conditions.get(condition.toLowerCase()).get(0) : null;
    }

    public List<Condition> getScopes() {
        return scopes;
    }

    public boolean apply(Country root, Country from) {
        if (this.conditions != null && this.conditions.entrySet()
                                                      .stream()
                                                      .anyMatch(entry -> entry.getValue()
                                                                              .stream()
                                                                              .anyMatch(s -> !ConditionsUtils.applyConditionToCountry(root, root, from,
                                                                                                                                      entry.getKey(), s)))) {
            return false;
        }

        if (this.scopes != null && this.scopes.stream().anyMatch(scope -> !ConditionsUtils.applyScopeToCountry(root, from, scope))) {
            return false;
        }

        return true;
    }

    public boolean apply(SaveProvince province) {
        if (this.conditions != null && this.conditions.entrySet()
                           .stream()
                           .anyMatch(entry -> entry.getValue()
                                                   .stream()
                                                   .anyMatch(s -> !ConditionsUtils.applyConditionToProvince(province, entry.getKey(), s)))) {
            return false;
        }

        if (this.scopes != null && this.scopes.stream().anyMatch(scope -> !ConditionsUtils.applyScopeToProvince(province, scope))) {
            return false;
        }

        return true;
    }

    public boolean apply(Leader leader) {
        if (this.conditions != null && getCondition("is_admiral") != null) {
            if ("yes".equalsIgnoreCase(getCondition("is_admiral")) && !LeaderType.ADMIRAL.equals(leader.getType())
                || "no".equalsIgnoreCase(getCondition("is_admiral")) && LeaderType.ADMIRAL.equals(leader.getType())) {
                return false;
            }
        }

        return leader.getCountry() == null || apply(leader.getCountry(), leader.getCountry());
    }

    public void removeCondition(String condition, String value) {
        if (this.conditions != null && this.conditions.containsKey(condition)) {
            this.conditions.get(condition).remove(value);
        }

        if (this.scopes != null) {
            this.scopes.forEach(scope -> scope.removeCondition(condition, value));
        }
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
