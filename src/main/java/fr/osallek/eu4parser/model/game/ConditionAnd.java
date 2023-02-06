package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;
import fr.osallek.eu4parser.common.ConditionsUtils;
import fr.osallek.eu4parser.model.save.country.Leader;
import fr.osallek.eu4parser.model.save.country.LeaderType;
import fr.osallek.eu4parser.model.save.country.SaveCountry;
import fr.osallek.eu4parser.model.save.province.SaveProvince;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class ConditionAnd extends ConditionAbstract {

    protected List<? extends ConditionAbstract> scopes;

    protected BooleanSupplier anyMatch;

    protected UnaryOperator<Boolean> converter;

    @SafeVarargs
    public ConditionAnd(Pair<String, String>... conditions) {
        super(s -> true);
        this.anyMatch = () -> false;
        this.converter = Boolean::booleanValue;
        this.conditions = Arrays.stream(conditions).collect(Collectors.groupingBy(Pair::getKey, Collectors.mapping(Pair::getValue, Collectors.toList())));
    }

    public ConditionAnd(ClausewitzItem item, String... ignore) {
        super(s -> true);
        this.anyMatch = () -> false;
        this.converter = Boolean::booleanValue;
        List<String> list = Arrays.asList(ignore);

        this.name = item.getName();
        this.conditions = item.getVariables()
                              .stream()
                              .filter(variable -> CollectionUtils.isEmpty(list) || !list.contains(variable.getName()))
                              .collect(Collectors.groupingBy(variable -> variable.getName().toLowerCase(),
                                                             Collectors.mapping(ClausewitzVariable::getValue, Collectors.toList())));
        this.scopes = item.getChildren()
                          .stream()
                          .filter(child -> CollectionUtils.isEmpty(list) || !list.contains(child.getName()))
                          .map(ConditionAnd::new)
                          .toList();
    }

    public ConditionAnd(ConditionAbstract other) {
        super(other.filter);
        this.anyMatch = () -> false;
        this.converter = Boolean::booleanValue;
        this.name = other.name;
        this.conditions = other.conditions;
        this.scopes = other.getScopes();
    }

    @Override
    public List<? extends ConditionAbstract> getScopes() {
        return this.scopes;
    }

    public boolean apply(SaveCountry root, SaveCountry from) {
        if (this.conditions != null &&
            this.conditions.entrySet()
                           .stream()
                           .filter(e -> this.filter.test(e.getKey()))
                           .anyMatch(entry -> entry.getValue()
                                                   .stream()
                                                   .anyMatch(s -> !ConditionsUtils.applyConditionToCountry(root, root, from, entry.getKey(), s)))) {
            return this.anyMatch.getAsBoolean();
        }

        if (getScopes() != null && getScopes().stream().anyMatch(scope -> !ConditionsUtils.applyScopeToCountry(root, from, scope))) {
            return this.anyMatch.getAsBoolean();
        }

        return !this.anyMatch.getAsBoolean();
    }

    public boolean apply(Country root, Country from) {
        if (this.conditions != null &&
            this.conditions.entrySet()
                           .stream()
                           .filter(e -> this.filter.test(e.getKey()))
                           .anyMatch(entry -> entry.getValue()
                                                   .stream()
                                                   .anyMatch(s -> !ConditionsUtils.applyConditionToCountry(root, root, from, entry.getKey(), s)))) {
            return this.anyMatch.getAsBoolean();
        }

        if (getScopes() != null && getScopes().stream().anyMatch(scope -> !ConditionsUtils.applyScopeToCountry(root, from, scope))) {
            return this.anyMatch.getAsBoolean();
        }

        return !this.anyMatch.getAsBoolean();
    }

    public boolean apply(SaveProvince province) {
        if (this.conditions != null &&
            this.conditions.entrySet()
                           .stream()
                           .filter(e -> this.filter.test(e.getKey()))
                           .anyMatch(entry -> entry.getValue()
                                                   .stream()
                                                   .anyMatch(s -> !ConditionsUtils.applyConditionToProvince(province, entry.getKey(), s)))) {
            return this.anyMatch.getAsBoolean();
        }

        if (getScopes() != null && getScopes().stream().anyMatch(scope -> !ConditionsUtils.applyScopeToProvince(province, scope))) {
            return this.anyMatch.getAsBoolean();
        }

        return !this.anyMatch.getAsBoolean();
    }

    public boolean apply(Province province) {
        if (this.conditions != null &&
            this.conditions.entrySet()
                           .stream()
                           .filter(e -> this.filter.test(e.getKey()))
                           .anyMatch(entry -> entry.getValue()
                                                   .stream()
                                                   .anyMatch(s -> !ConditionsUtils.applyConditionToProvince(province, entry.getKey(), s)))) {
            return this.anyMatch.getAsBoolean();
        }

        if (getScopes() != null && getScopes().stream().anyMatch(scope -> !ConditionsUtils.applyScopeToProvince(province, scope))) {
            return this.anyMatch.getAsBoolean();
        }

        return !this.anyMatch.getAsBoolean();
    }

    public boolean apply(SaveProvince province, SaveProvince from) {
        if (this.conditions != null &&
            this.conditions.entrySet()
                           .stream()
                           .filter(e -> this.filter.test(e.getKey()))
                           .anyMatch(entry -> entry.getValue()
                                                   .stream()
                                                   .anyMatch(s -> !ConditionsUtils.applyConditionToProvince(province, entry.getKey(), s)))) {
            return this.anyMatch.getAsBoolean();
        }

        if (getScopes() != null && getScopes().stream().anyMatch(scope -> !ConditionsUtils.applyScopeToProvince(province, scope))) {
            return this.anyMatch.getAsBoolean();
        }

        return !this.anyMatch.getAsBoolean();
    }

    public boolean apply(SaveCountry country, SaveProvince from) {
        if (this.conditions != null
            && this.conditions.entrySet()
                              .stream()
                              .filter(e -> this.filter.test(e.getKey()))
                              .anyMatch(entry -> entry.getValue()
                                                      .stream()
                                                      .anyMatch(s -> !ConditionsUtils.applyConditionToCountry(country, country, country, entry.getKey(), s)))) {
            return this.anyMatch.getAsBoolean();
        }

        if (getScopes() != null && getScopes().stream().anyMatch(scope -> {
            if ("FROM".equals(scope.name)) {
                return !ConditionsUtils.applyScopeToProvince(from, scope);
            } else {
                return !ConditionsUtils.applyScopeToCountry(country, country, scope);
            }
        })) {
            return this.anyMatch.getAsBoolean();
        }

        return !this.anyMatch.getAsBoolean();
    }

    public boolean apply(Leader leader) {
        if (this.conditions != null && getCondition("is_admiral") != null) {
            if ("yes".equalsIgnoreCase(getCondition("is_admiral")) && !LeaderType.ADMIRAL.equals(leader.getType())
                || "no".equalsIgnoreCase(getCondition("is_admiral")) && LeaderType.ADMIRAL.equals(leader.getType())) {
                return this.anyMatch.getAsBoolean();
            }
        }

        return leader.getCountry() == null || apply(leader.getCountry(), leader.getCountry());
    }
}
