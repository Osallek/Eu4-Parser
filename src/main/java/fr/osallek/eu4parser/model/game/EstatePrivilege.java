package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.model.game.condition.ConditionAnd;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class EstatePrivilege {

    private final ClausewitzItem item;

    private final Game game;

    private Estate estate;

    public EstatePrivilege(ClausewitzItem item, Game game) {
        this.item = item;
        this.game = game;
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

    public Optional<String> getIcon() {
        return this.item.getVarAsString("icon");
    }

    public void setIcon(String icon) {
        if (StringUtils.isBlank(icon)) {
            this.item.removeVariable("icon");
        } else {
            this.item.setVariable("icon", icon);
        }
    }

    public Optional<File> getImage() {
        return getIcon().map(this.game::getSpriteTypeImageFile);
    }

    public double getLoyalty() {
        return this.item.getVarAsDouble("loyalty").orElse(0d);
    }

    public void setLoyalty(Integer loyalty) {
        if (loyalty == null) {
            this.item.removeVariable("loyalty");
        } else {
            this.item.setVariable("loyalty", loyalty);
        }
    }


    public Optional<Double> getInfluence() {
        return this.item.getVarAsDouble("influence");
    }

    public void setInfluence(Integer influence) {
        if (influence == null) {
            this.item.removeVariable("influence");
        } else {
            this.item.setVariable("influence", influence);
        }
    }

    public Optional<ConditionAnd> getIsValid() {
        return this.item.getChild("is_valid").map(ConditionAnd::new);
    }

    public Optional<ConditionAnd> getCanSelect() {
        return this.item.getChild("can_select").map(ConditionAnd::new);
    }

    public Optional<ConditionAnd> getCanRevoke() {
        return this.item.getChild("can_revoke").map(ConditionAnd::new);
    }

    public Modifiers getModifiers() {
        Modifiers modifiers = new Modifiers();
        this.item.getVarAsDouble("max_absolutism").ifPresent(d -> modifiers.addModifier(ModifiersUtils.getModifier("max_absolutism"), d));
        this.item.getChild("penalties").ifPresent(child -> modifiers.addAll(new Modifiers(child.getVariables())));
        this.item.getChild("benefits").ifPresent(child -> modifiers.addAll(new Modifiers(child.getVariables())));

        return modifiers;
    }

    public List<EstatePrivilegeModifier> getConditionalModifiers() {
        return this.item.getChildren("conditional_modifier").stream().map(EstatePrivilegeModifier::new).toList();
    }

    public Optional<Modifiers> getModifierByLandOwnership() {
        return this.item.getChild("modifier_by_land_ownership").map(Modifiers::new);
    }

    public List<String> getMechanics() {
        return this.item.getList("mechanics").map(ClausewitzList::getValues).orElse(new ArrayList<>());
    }

    public void setMechanics(List<String> mechanics) {
        if (CollectionUtils.isEmpty(mechanics)) {
            this.item.removeList("mechanics");
            return;
        }

        this.item.getList("mechanics")
                 .ifPresentOrElse(list -> list.setAll(mechanics.stream().filter(Objects::nonNull).toArray(String[]::new)),
                                  () -> this.item.addList("mechanics", mechanics.stream().filter(Objects::nonNull).toArray(String[]::new)));
    }

    public Optional<Integer> getCooldownYears() {
        return this.item.getVarAsInt("cooldown_years");
    }

    public void setCooldownYears(Integer cooldownYears) {
        if (cooldownYears == null) {
            this.item.removeVariable("cooldown_years");
        } else {
            this.item.setVariable("cooldown_years", cooldownYears);
        }
    }

    public Game getGame() {
        return game;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof EstatePrivilege estatePrivilege)) {
            return false;
        }

        return Objects.equals(getName(), estatePrivilege.getName());
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
