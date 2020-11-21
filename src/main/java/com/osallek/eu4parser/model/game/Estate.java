package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.eu4parser.model.Color;
import org.apache.commons.lang3.BooleanUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Estate {

    private final String name;

    private String localizedName;

    private final int icon;

    private final Condition trigger;

    private final Modifiers countryModifierHappy;

    private final Modifiers countryModifierNeutral;

    private final Modifiers countryModifierAngry;

    private final Modifiers landOwnershipModifier;

    private final double baseInfluence;

    private final List<EstateModifier> influenceModifiers;

    private final List<EstateModifier> loyaltyModifiers;

    private final List<Names> names;

    private final Color color;

    private final boolean contributesToCuriaTreasury;

    private final Map<String, EstatePrivilege> privileges;

    private final List<String> agendas;

    private final double influenceFromDevModifier;

    private List<ModifierDefinition> modifierDefinitions;

    public Estate(ClausewitzItem item, List<ModifierDefinition> modifierDefinitions, Game game) {
        this.name = item.getName();
        this.modifierDefinitions = modifierDefinitions;
        this.icon = item.getVarAsInt("icon");

        ClausewitzItem child = item.getChild("trigger");
        this.trigger = child == null ? null : new Condition(child);
        this.baseInfluence = item.getVarAsDouble("base_influence");
        this.influenceFromDevModifier = item.getVarAsDouble("influence_from_dev_modifier");
        this.contributesToCuriaTreasury = BooleanUtils.toBoolean(item.getVarAsBool("contributes_to_curia_treasury"));

        List<ClausewitzItem> items = item.getChildren("custom_name");
        this.names = items.stream().map(Names::new).collect(Collectors.toList());

        this.countryModifierHappy = new Modifiers(item.getChild("country_modifier_happy"));

        this.countryModifierNeutral = new Modifiers(item.getChild("country_modifier_neutral"));

        this.countryModifierAngry = new Modifiers(item.getChild("country_modifier_angry"));

        this.landOwnershipModifier = new Modifiers(item.getChild("land_ownership_modifier"));

        items = item.getChildren("influence_modifier");
        this.influenceModifiers = items.stream()
                                       .map(i -> new EstateModifier(i, "influence"))
                                       .collect(Collectors.toList());

        items = item.getChildren("loyalty_modifier");
        this.loyaltyModifiers = items.stream()
                                     .map(i -> new EstateModifier(i, "loyalty"))
                                     .collect(Collectors.toList());

        ClausewitzList list = item.getList("color");
        this.color = child == null ? null : new Color(list);

        list = item.getList("privileges");
        this.privileges = list == null ? null : list.getValues().stream().map(game::getEstatePrivilege).collect(Collectors.toMap(EstatePrivilege::getName,
                                                                                                                                 Function.identity(),
                                                                                                                                 (a, b) -> b,
                                                                                                                                 LinkedHashMap::new));
        if (this.privileges != null) {
            this.privileges.values().forEach(privilege -> privilege.setEstate(this));
        }

        list = item.getList("agendas");
        this.agendas = list == null ? null : list.getValues();
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

    public List<ModifierDefinition> getModifierDefinitions() {
        return modifierDefinitions;
    }

    public void setModifierDefinitions(List<ModifierDefinition> modifierDefinitions) {
        this.modifierDefinitions = modifierDefinitions;
    }

    public int getIcon() {
        return icon;
    }

    public Condition getTrigger() {
        return trigger;
    }

    public Modifiers getCountryModifierHappy() {
        return countryModifierHappy;
    }

    public Modifiers getCountryModifierNeutral() {
        return countryModifierNeutral;
    }

    public Modifiers getCountryModifierAngry() {
        return countryModifierAngry;
    }

    public Modifiers getLandOwnershipModifier() {
        return landOwnershipModifier;
    }

    public double getBaseInfluence() {
        return baseInfluence;
    }

    public List<EstateModifier> getInfluenceModifiers() {
        return influenceModifiers;
    }

    public List<EstateModifier> getLoyaltyModifiers() {
        return loyaltyModifiers;
    }

    public List<Names> getNames() {
        return names;
    }

    public Color getColor() {
        return color;
    }

    public boolean isContributesToCuriaTreasury() {
        return contributesToCuriaTreasury;
    }

    public Map<String, EstatePrivilege> getPrivileges() {
        return privileges;
    }

    public List<String> getAgendas() {
        return agendas;
    }

    public double getInfluenceFromDevModifier() {
        return influenceFromDevModifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Estate)) {
            return false;
        }

        Estate that = (Estate) o;
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
