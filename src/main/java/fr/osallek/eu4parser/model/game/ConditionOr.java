package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.common.ConditionsUtils;
import fr.osallek.eu4parser.model.save.country.Leader;
import fr.osallek.eu4parser.model.save.country.LeaderType;
import fr.osallek.eu4parser.model.save.country.SaveCountry;
import fr.osallek.eu4parser.model.save.province.SaveProvince;

public class ConditionOr extends ConditionAbstract {

    public ConditionOr(ConditionAbstract other) {
        super(other.filter);
        this.name = other.name;
        this.conditions = other.conditions;
        this.scopes = other.getScopes();
    }

    public ConditionOr(ClausewitzItem item, String... ignore) {
        super(s -> true, item, ignore);
    }

    public boolean apply(SaveCountry root, SaveCountry from) {
        if (this.conditions != null &&
            this.conditions.entrySet()
                           .stream()
                           .filter(e -> this.filter.test(e.getKey()))
                           .anyMatch(entry -> entry.getValue()
                                                   .stream()
                                                   .anyMatch(s -> ConditionsUtils.applyConditionToCountry(root, root, from, entry.getKey(), s)))) {
            return true;
        }

        if (getScopes() != null && getScopes().stream().anyMatch(scope -> ConditionsUtils.applyScopeToCountry(root, from, scope))) {
            return true;
        }

        return false;
    }

    public boolean apply(Country root, Country from) {
        if (this.conditions != null &&
            this.conditions.entrySet()
                           .stream()
                           .anyMatch(entry -> entry.getValue()
                                                   .stream()
                                                   .anyMatch(s -> ConditionsUtils.applyConditionToCountry(root, root, from, entry.getKey(), s)))) {
            return true;
        }

        if (getScopes() != null && getScopes().stream().anyMatch(scope -> ConditionsUtils.applyScopeToCountry(root, from, scope))) {
            return true;
        }

        return false;
    }

    public boolean apply(SaveProvince province) {
        if (this.conditions != null &&
            this.conditions.entrySet()
                           .stream()
                           .anyMatch(entry -> entry.getValue()
                                                   .stream()
                                                   .anyMatch(s -> ConditionsUtils.applyConditionToProvince(province, entry.getKey(), s)))) {
            return true;
        }

        if (getScopes() != null && getScopes().stream().anyMatch(scope -> ConditionsUtils.applyScopeToProvince(province, scope))) {
            return true;
        }

        return false;
    }

    public boolean apply(Province province) {
        if (this.conditions != null &&
            this.conditions.entrySet()
                           .stream()
                           .anyMatch(entry -> entry.getValue()
                                                   .stream()
                                                   .anyMatch(s -> ConditionsUtils.applyConditionToProvince(province, entry.getKey(), s)))) {
            return true;
        }

        if (getScopes() != null && getScopes().stream().anyMatch(scope -> ConditionsUtils.applyScopeToProvince(province, scope))) {
            return true;
        }

        return false;
    }

    public boolean apply(SaveProvince province, SaveProvince from) {
        if (this.conditions != null &&
            this.conditions.entrySet()
                           .stream()
                           .anyMatch(entry -> entry.getValue()
                                                   .stream()
                                                   .anyMatch(s -> ConditionsUtils.applyConditionToProvince(province, entry.getKey(), s)))) {
            return true;
        }

        if (getScopes() != null && getScopes().stream().anyMatch(scope -> ConditionsUtils.applyScopeToProvince(province, scope))) {
            return true;
        }

        return false;
    }

    public boolean apply(SaveCountry country, SaveProvince from) {
        if (this.conditions != null
            && this.conditions.entrySet()
                              .stream()
                              .anyMatch(entry -> entry.getValue()
                                                      .stream()
                                                      .anyMatch(s -> ConditionsUtils.applyConditionToCountry(country, country, country, entry.getKey(), s)))) {
            return true;
        }

        if (getScopes() != null && getScopes().stream().anyMatch(scope -> {
            if ("FROM".equals(scope.name)) {
                return ConditionsUtils.applyScopeToProvince(from, scope);
            } else {
                return ConditionsUtils.applyScopeToCountry(country, country, scope);
            }
        })) {
            return true;
        }

        return false;
    }

    public boolean apply(Leader leader) {
        if (this.conditions != null && getCondition("is_admiral") != null) {
            if ("yes".equalsIgnoreCase(getCondition("is_admiral")) && !LeaderType.ADMIRAL.equals(leader.getType())
                || "no".equalsIgnoreCase(getCondition("is_admiral")) && LeaderType.ADMIRAL.equals(leader.getType())) {
                return true;
            }
        }

        return leader.getCountry() == null || apply(leader.getCountry(), leader.getCountry());
    }
}
