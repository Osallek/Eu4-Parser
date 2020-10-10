package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import com.osallek.eu4parser.common.NumbersUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class EstatePrivilege {

    private final String name;

    private String localizedName;

    private final String icon;

    private final double maxAbsolutism;

    private final double loyalty;

    private final double influence;

    private final Condition isValid;

    private final Condition canSelect;

    private final Condition canRevoke;

    private final Map<String, String> modifiers;

    private final List<EstatePrivilegeModifier> conditionalModifiers;

    private final Map<String, String> modifierByLandOwnership;

    private final List<String> mechanics;

    private final int cooldownYears;

    public EstatePrivilege(ClausewitzItem item) {
        this.name = item.getName();
        this.icon = item.getVarAsString("icon");
        this.maxAbsolutism = NumbersUtils.doubleOrDefault(item.getVarAsDouble("max_absolutism"));
        this.loyalty = NumbersUtils.doubleOrDefault(item.getVarAsDouble("loyalty"));
        this.influence = NumbersUtils.doubleOrDefault(item.getVarAsDouble("influence"));
        this.cooldownYears = NumbersUtils.intOrDefault(item.getVarAsInt("cooldown_years"));

        ClausewitzItem child = item.getChild("is_valid");
        this.isValid = child == null ? null : new Condition(child);

        child = item.getChild("can_select");
        this.canSelect = child == null ? null : new Condition(child);

        child = item.getChild("can_revoke");
        this.canRevoke = child == null ? null : new Condition(child);

        this.modifiers = new LinkedHashMap<>();
        child = item.getChild("penalties");
        if (child != null) {
            child.getVariables().forEach(var -> this.modifiers.put(var.getName(), var.getValue()));
        }

        child = item.getChild("benefits");
        if (child != null) {
            child.getVariables().forEach(var -> this.modifiers.put(var.getName(), var.getValue()));
        }

        child = item.getChild("modifier_by_land_ownership");
        this.modifierByLandOwnership = child == null ? null : child.getVariables().stream().collect(Collectors.toMap(ClausewitzVariable::getName,
                                                                                                                     ClausewitzVariable::getValue,
                                                                                                                     (a, b) -> b,
                                                                                                                     LinkedHashMap::new));

        ClausewitzList list = item.getList("mechanics");
        this.mechanics = list == null ? null : list.getValues();

        this.conditionalModifiers = item.getChildren("conditional_modifier").stream().map(EstatePrivilegeModifier::new).collect(Collectors.toList());
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

    public String getIcon() {
        return icon;
    }

    public double getMaxAbsolutism() {
        return maxAbsolutism;
    }

    public double getLoyalty() {
        return loyalty;
    }

    public double getInfluence() {
        return influence;
    }

    public Condition getIsValid() {
        return isValid;
    }

    public Condition getCanSelect() {
        return canSelect;
    }

    public Condition getCanRevoke() {
        return canRevoke;
    }

    public Map<String, String> getModifiers() {
        return modifiers;
    }

    public List<EstatePrivilegeModifier> getConditionalModifiers() {
        return conditionalModifiers;
    }

    public Map<String, String> getModifierByLandOwnership() {
        return modifierByLandOwnership;
    }

    public List<String> getMechanics() {
        return mechanics;
    }

    public int getCooldownYears() {
        return cooldownYears;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof EstatePrivilege)) {
            return false;
        }

        EstatePrivilege that = (EstatePrivilege) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
