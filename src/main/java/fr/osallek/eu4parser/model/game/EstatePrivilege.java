package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.common.NumbersUtils;
import fr.osallek.eu4parser.model.game.todo.Estate;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EstatePrivilege {

    private final ClausewitzItem item;

    private Estate estate;

    public EstatePrivilege(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public Estate getEstate() {
        return estate;
    }

    void setEstate(Estate estate) {
        this.estate = estate;
    }

    public String getIcon() {
        return this.item.getVarAsString("icon");
    }

    public void setIcon(String icon) {
        if (StringUtils.isBlank(icon)) {
            this.item.removeVariable("icon");
        } else {
            this.item.setVariable("icon", icon);
        }
    }

    public double getLoyalty() {
        return this.item.getVarAsDouble("loyalty");
    }

    public void setLoyalty(Integer loyalty) {
        if (loyalty == null) {
            this.item.removeVariable("loyalty");
        } else {
            this.item.setVariable("loyalty", loyalty);
        }
    }


    public Double getInfluence() {
        return this.item.getVarAsDouble("influence");
    }

    public void setInfluence(Integer influence) {
        if (influence == null) {
            this.item.removeVariable("influence");
        } else {
            this.item.setVariable("influence", influence);
        }
    }

    public Condition getIsValid() {
        ClausewitzItem child = this.item.getChild("is_valid");
        return child == null ? null : new Condition(child);
    }

    public Condition getCanSelect() {
        ClausewitzItem child = this.item.getChild("can_select");
        return child == null ? null : new Condition(child);
    }

    public Condition getCanRevoke() {
        ClausewitzItem child = this.item.getChild("can_revoke");
        return child == null ? null : new Condition(child);
    }

    public Modifiers getModifiers() {
        Modifiers modifiers = new Modifiers();
        modifiers.addModifier(ModifiersUtils.getModifier("max_absolutism"), NumbersUtils.doubleOrDefault(this.item.getVarAsDouble("max_absolutism")));

        ClausewitzItem child = this.item.getChild("penalties");
        if (child != null) {
            modifiers.addAll(new Modifiers(child.getVariables()));
        }

        child = item.getChild("benefits");
        if (child != null) {
            modifiers.addAll(new Modifiers(child.getVariables()));
        }

        return modifiers;
    }

    public List<EstatePrivilegeModifier> getConditionalModifiers() {
        return this.item.getChildren("conditional_modifier").stream().map(EstatePrivilegeModifier::new).collect(Collectors.toList());
    }

    public Modifiers getModifierByLandOwnership() {
        return new Modifiers(this.item.getChild("modifier_by_land_ownership"));
    }

    public List<String> getMechanics() {
        ClausewitzList list = this.item.getList("mechanics");
        return list == null ? null : list.getValues();
    }

    public void setMechanics(List<String> mechanics) {
        if (CollectionUtils.isEmpty(mechanics)) {
            this.item.removeList("mechanics");
            return;
        }

        ClausewitzList list = this.item.getList("mechanics");

        if (list != null) {
            list.setAll(mechanics.stream().filter(Objects::nonNull).toArray(String[]::new));
        } else {
            this.item.addList("mechanics", mechanics.stream().filter(Objects::nonNull).toArray(String[]::new));
        }
    }

    public Integer getCooldownYears() {
        return this.item.getVarAsInt("cooldown_years");
    }

    public void setCooldownYears(Integer cooldownYears) {
        if (cooldownYears == null) {
            this.item.removeVariable("cooldown_years");
        } else {
            this.item.setVariable("cooldown_years", cooldownYears);
        }
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
