package fr.osallek.eu4parser.model.game.condition;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.common.ConditionsUtils;
import fr.osallek.eu4parser.model.game.Country;
import fr.osallek.eu4parser.model.game.Province;
import fr.osallek.eu4parser.model.save.country.Leader;
import fr.osallek.eu4parser.model.save.country.LeaderType;
import fr.osallek.eu4parser.model.save.country.SaveCountry;
import fr.osallek.eu4parser.model.save.province.SaveProvince;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

public class ConditionNot extends ConditionAbstract {

    @SafeVarargs
    public ConditionNot(Pair<String, String>... conditions) {
        super(s -> true, conditions);
    }

    public ConditionNot(ClausewitzItem item, String... ignore) {
        super(s -> true, item, ignore);
    }

    public ConditionNot(ConditionAbstract other) {
        super(other.filter);
        this.name = other.name;
        this.conditions = other.conditions;
        this.scopes = other.getScopes();
    }

    @Override
    public boolean apply(SaveCountry root, SaveCountry from) {
        if (this.conditions != null) {
            for (Map.Entry<String, List<String>> e : this.conditions.entrySet()) {
                if (this.filter.test(e.getKey())) {
                    for (String s : e.getValue()) {
                        if (ConditionsUtils.applyConditionToCountry(root, root, from, e.getKey(), s)) {
                            return false;
                        }
                    }
                }
            }
        }

        if (getScopes() != null) {
            for (ConditionAbstract scope : getScopes()) {
                if (ConditionsUtils.applyScopeToCountry(root, from, scope)) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean apply(Country root, Country from) {
        if (this.conditions != null) {
            for (Map.Entry<String, List<String>> e : this.conditions.entrySet()) {
                if (this.filter.test(e.getKey())) {
                    for (String s : e.getValue()) {
                        if (ConditionsUtils.applyConditionToCountry(root, root, from, e.getKey(), s)) {
                            return false;
                        }
                    }
                }
            }
        }

        if (getScopes() != null) {
            for (ConditionAbstract scope : getScopes()) {
                if (ConditionsUtils.applyScopeToCountry(root, from, scope)) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean apply(SaveProvince province) {
        if (this.conditions != null) {
            for (Map.Entry<String, List<String>> e : this.conditions.entrySet()) {
                if (this.filter.test(e.getKey())) {
                    for (String s : e.getValue()) {
                        if (ConditionsUtils.applyConditionToProvince(province, e.getKey(), s)) {
                            return false;
                        }
                    }
                }
            }
        }

        if (getScopes() != null) {
            for (ConditionAbstract scope : getScopes()) {
                if (ConditionsUtils.applyScopeToProvince(province, scope)) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean apply(Province province) {
        if (this.conditions != null) {
            for (Map.Entry<String, List<String>> e : this.conditions.entrySet()) {
                if (this.filter.test(e.getKey())) {
                    for (String s : e.getValue()) {
                        if (ConditionsUtils.applyConditionToProvince(province, e.getKey(), s)) {
                            return false;
                        }
                    }
                }
            }
        }

        if (getScopes() != null) {
            for (ConditionAbstract scope : getScopes()) {
                if (ConditionsUtils.applyScopeToProvince(province, scope)) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean apply(SaveCountry country, SaveProvince from) {
        if (this.conditions != null) {
            for (Map.Entry<String, List<String>> e : this.conditions.entrySet()) {
                if (this.filter.test(e.getKey())) {
                    for (String s : e.getValue()) {
                        if (ConditionsUtils.applyConditionToCountry(country, country, country, e.getKey(), s)) {
                            return false;
                        }
                    }
                }
            }
        }

        if (getScopes() != null && getScopes().stream().anyMatch(scope -> {
            if ("FROM".equals(scope.name)) {
                return ConditionsUtils.applyScopeToProvince(from, scope);
            } else {
                return ConditionsUtils.applyScopeToCountry(country, country, scope);
            }
        })) {
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
}
