package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.country.Leader;
import fr.osallek.eu4parser.model.save.country.SaveCountry;
import fr.osallek.eu4parser.model.save.province.SaveProvince;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class ConditionAbstract {

    protected String name;

    protected Map<String, List<String>> conditions;

    protected Predicate<String> filter;

    protected List<ConditionAbstract> scopes;

    protected Map<String, List<ConditionAbstract>> clauses;

    protected ConditionAbstract(Predicate<String> filter) {
        this.filter = filter;
    }

    protected ConditionAbstract(Predicate<String> filter, ClausewitzItem item, String... ignore) {
        this(filter);
        List<String> list = Arrays.asList(ignore);

        this.name = item.getName();
        this.conditions = item.getVariables()
                              .stream()
                              .filter(variable -> CollectionUtils.isEmpty(list) || !list.contains(variable.getName()))
                              .collect(Collectors.groupingBy(variable -> variable.getName().toLowerCase(),
                                                             Collectors.mapping(v -> ClausewitzUtils.removeQuotes(v.getValue()), Collectors.toList())));
        this.scopes = new ArrayList<>();
        this.clauses = new HashMap<>();

        item.getChildren()
            .stream()
            .filter(child -> CollectionUtils.isEmpty(list) || !list.contains(child.getName()))
            .forEach(child -> {
                switch (child.getName().toLowerCase()) {
                    case "not" -> this.scopes.add(new ConditionNot(child));
                    case "or" -> this.scopes.add(new ConditionOr(child));
                    case "and" -> this.scopes.add(new ConditionAnd(child));
                    default -> {
                        this.clauses.putIfAbsent(child.getName(), new ArrayList<>());
                        this.clauses.get(child.getName()).add(new ConditionAnd(child));
                    }
                }
            });
    }

    @SafeVarargs
    protected ConditionAbstract(Predicate<String> filter, Pair<String, String>... conditions) {
        this(filter);
        this.conditions = Arrays.stream(conditions)
                                .collect(Collectors.groupingBy(Pair::getKey,
                                                               Collectors.mapping(p -> ClausewitzUtils.removeQuotes(p.getValue()), Collectors.toList())));
    }

    public ConditionTagOnly tagOnly() {
        return new ConditionTagOnly(this);
    }

    public ConditionOr or() {
        return new ConditionOr(this);
    }

    public ConditionAnd and() {
        return new ConditionAnd(this);
    }

    public ConditionNot not() {
        return new ConditionNot(this);
    }

    public ConditionIgnoreDlc ignoreDlc() {
        return new ConditionIgnoreDlc(this);
    }

    public ConditionIgnoreMapSetup ignoreMapSetup() {
        return new ConditionIgnoreMapSetup(this);
    }

    public ConditionIgnoreRNW ignoreRNW() {
        return new ConditionIgnoreRNW(this);
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

    public List<ConditionAbstract> getScopes() {
        return scopes;
    }

    public List<ConditionAbstract> getScopes(String name) {
        return getScopes() == null ? null : getScopes().stream().filter(condition -> name.equals(condition.getName())).toList();
    }

    public Map<String, List<ConditionAbstract>> getClauses() {
        return clauses;
    }

    public boolean isEmpty() {
        return (MapUtils.isEmpty(this.conditions) || this.conditions.entrySet().stream().noneMatch(e -> this.filter.test(e.getKey()))) &&
               CollectionUtils.isEmpty(getScopes());
    }

    public abstract boolean apply(SaveCountry root, SaveCountry from);

    public abstract boolean apply(Country root, Country from);

    public abstract boolean apply(SaveProvince province);

    public abstract boolean apply(Province province);

    public abstract boolean apply(SaveCountry country, SaveProvince from);

    public abstract boolean apply(Leader leader);

    public void removeCondition(String condition, String value) {
        if (this.conditions != null && this.conditions.containsKey(condition)) {
            this.conditions.get(condition).remove(value);
        }

        if (getScopes() != null) {
            getScopes().forEach(scope -> scope.removeCondition(condition, value));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ConditionAnd condition)) {
            return false;
        }

        return Objects.equals(name, condition.name) &&
               Objects.equals(conditions, condition.conditions) &&
               Objects.equals(getScopes(), condition.getScopes());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, conditions, getScopes());
    }

    @Override
    public String toString() {
        return getName();
    }
}
