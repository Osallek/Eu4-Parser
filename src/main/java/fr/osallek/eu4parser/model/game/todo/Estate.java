package fr.osallek.eu4parser.model.game.todo;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.model.Color;
import fr.osallek.eu4parser.model.game.Condition;
import fr.osallek.eu4parser.model.game.EstateModifier;
import fr.osallek.eu4parser.model.game.EstatePrivilege;
import fr.osallek.eu4parser.model.game.Game;
import fr.osallek.eu4parser.model.game.ModifierDefinition;
import fr.osallek.eu4parser.model.game.Modifiers;
import fr.osallek.eu4parser.model.game.Names;
import org.apache.commons.lang3.BooleanUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Estate {

    private final ClausewitzItem item;

    private final Game game;

    private final Integer icon;

    private final Double baseInfluence;

    private final List<EstateModifier> influenceModifiers;

    private final List<EstateModifier> loyaltyModifiers;

    private final List<Names> names;

    private final boolean contributesToCuriaTreasury;

    private final List<String> agendas;

    private final Double influenceFromDevModifier;

    private List<ModifierDefinition> modifierDefinitions;

    public Estate(ClausewitzItem item, List<ModifierDefinition> modifierDefinitions, Game game) {
        this.item = item;
        this.game = game;
        this.modifierDefinitions = modifierDefinitions;
        this.icon = item.getVarAsInt("icon");

        this.baseInfluence = item.getVarAsDouble("base_influence");
        this.influenceFromDevModifier = item.getVarAsDouble("influence_from_dev_modifier");
        this.contributesToCuriaTreasury = BooleanUtils.toBoolean(item.getVarAsBool("contributes_to_curia_treasury"));

        List<ClausewitzItem> items = item.getChildren("custom_name");
        this.names = items.stream().map(Names::new).collect(Collectors.toList());

        items = item.getChildren("influence_modifier");
        this.influenceModifiers = items.stream()
                                       .map(i -> new EstateModifier(i, "influence"))
                                       .collect(Collectors.toList());

        items = item.getChildren("loyalty_modifier");
        this.loyaltyModifiers = items.stream()
                                     .map(i -> new EstateModifier(i, "loyalty"))
                                     .collect(Collectors.toList());

        ClausewitzList list = item.getList("agendas");
        this.agendas = list == null ? null : list.getValues();
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
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
        ClausewitzItem child = this.item.getChild("trigger");
        return child == null ? null : new Condition(child);
    }

    public Modifiers getCountryModifierHappy() {
        return new Modifiers(this.item.getChild("country_modifier_happy"));
    }

    public Modifiers getCountryModifierNeutral() {
        return new Modifiers(this.item.getChild("country_modifier_neutral"));
    }

    public Modifiers getCountryModifierAngry() {
        return new Modifiers(this.item.getChild("country_modifier_angry"));
    }

    public Modifiers getLandOwnershipModifier() {
        return new Modifiers(this.item.getChild("land_ownership_modifier"));
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
        if (this.item == null) {
            return null;
        }

        ClausewitzList clausewitzList = this.item.getList("color");
        return clausewitzList == null ? null : new Color(clausewitzList, true);
    }

    public void setColor(Color color) {
        if (color == null) {
            this.item.removeList("color");
            return;
        }

        ClausewitzList list = this.item.getList("color");

        if (list != null) {
            Color actualColor = new Color(list, true);
            actualColor.setRed(color.getRed());
            actualColor.setGreen(color.getGreen());
            actualColor.setBlue(color.getBlue());
        } else {
            Color.addToItem(this.item, "color", color);
        }
    }

    public boolean isContributesToCuriaTreasury() {
        return contributesToCuriaTreasury;
    }

    public Map<String, EstatePrivilege> getPrivileges() {
        ClausewitzList list = this.item.getList("privileges");
        return list == null ? null :
               list.getValues().stream().map(this.game::getEstatePrivilege).collect(Collectors.toMap(EstatePrivilege::getName, Function.identity(),
                                                                                                     (a, b) -> b, LinkedHashMap::new));
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
        return Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return getName();
    }
}
