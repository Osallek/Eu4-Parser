package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.common.NumbersUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EstatePrivilege implements Comparable<EstatePrivilege> {

    private Estate estate;

    private final String name;

    private String localizedName;

    private final String icon;

    private final double loyalty;

    private final double influence;

    private final Condition isValid;

    private final Condition canSelect;

    private final Condition canRevoke;

    private final Modifiers modifiers;

    private final List<EstatePrivilegeModifier> conditionalModifiers;

    private final Modifiers modifierByLandOwnership;

    private final List<String> mechanics;

    private final int cooldownYears;

    public EstatePrivilege(ClausewitzItem item) {
        this.name = item.getName();
        this.icon = item.getVarAsString("icon");
        this.loyalty = NumbersUtils.doubleOrDefault(item.getVarAsDouble("loyalty")) * 100; //Percent
        this.influence = NumbersUtils.doubleOrDefault(item.getVarAsDouble("influence")) * 100; //Percent
        this.cooldownYears = NumbersUtils.intOrDefault(item.getVarAsInt("cooldown_years"));

        ClausewitzItem child = item.getChild("is_valid");
        this.isValid = child == null ? null : new Condition(child);

        child = item.getChild("can_select");
        this.canSelect = child == null ? null : new Condition(child);

        child = item.getChild("can_revoke");
        this.canRevoke = child == null ? null : new Condition(child);

        this.modifiers = new Modifiers();
        this.modifiers.addModifier(ModifiersUtils.getModifier("max_absolutism"), NumbersUtils.doubleOrDefault(item.getVarAsDouble("max_absolutism")));

        child = item.getChild("penalties");
        if (child != null) {
            this.modifiers.addAll(new Modifiers(child.getVariables()));
        }

        child = item.getChild("benefits");
        if (child != null) {
            this.modifiers.addAll(new Modifiers(child.getVariables()));
        }

        this.modifierByLandOwnership = new Modifiers(item.getChild("modifier_by_land_ownership"));

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
        this.localizedName = this.estate == null ? localizedName :
                             localizedName.replace("[Root.Get" + StringUtils.capitalize(this.estate.getLocalizedName()) + "Name]",
                                                   this.estate.getLocalizedName()).replace("$ESTATE_NAME$", this.estate.getLocalizedName());
    }

    public Estate getEstate() {
        return estate;
    }

    void setEstate(Estate estate) {
        this.estate = estate;
    }

    public String getIcon() {
        return icon;
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

    public Modifiers getModifiers() {
        return modifiers;
    }

    public List<EstatePrivilegeModifier> getConditionalModifiers() {
        return conditionalModifiers;
    }

    public Modifiers getModifierByLandOwnership() {
        return modifierByLandOwnership;
    }

    public List<String> getMechanics() {
        return mechanics;
    }

    public int getCooldownYears() {
        return cooldownYears;
    }

    @Override
    public int compareTo(EstatePrivilege o) {
        return Comparator.comparing(EstatePrivilege::getLocalizedName, Eu4Utils.COLLATOR).compare(this, o);
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
