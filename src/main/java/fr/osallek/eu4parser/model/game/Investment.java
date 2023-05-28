package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.condition.ConditionAnd;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.Optional;

public class Investment {

    private final Game game;

    private final ClausewitzItem item;

    public Investment(ClausewitzItem item, Game game) {
        this.game = game;
        this.item = item;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public Optional<String> getCategory() {
        return this.item.getVarAsString("category");
    }

    public void setCategory(String category) {
        if (StringUtils.isBlank(category)) {
            this.item.removeVariable("category");
        } else {
            this.item.setVariable("category", category);
        }
    }

    public Optional<Double> getCost() {
        return this.item.getVarAsDouble("cost");
    }

    public void setCost(Double cost) {
        if (cost == null) {
            this.item.removeVariable("cost");
        } else {
            this.item.setVariable("cost", cost);
        }
    }

    public Optional<Investment> getUpgradesTo() {
        return this.item.getVarAsString("upgrades_to").map(this.game::getInvestment);
    }

    public Optional<String> getSprite() {
        return this.item.getVarAsString("sprite").map(ClausewitzUtils::removeQuotes);
    }

    public void setSprite(String sprite) {
        if (StringUtils.isBlank(sprite)) {
            this.item.removeVariable("sprite");
        } else {
            this.item.setVariable("sprite", ClausewitzUtils.addQuotes(sprite));
        }
    }

    public Optional<ConditionAnd> getAllow() {
        return this.item.getChild("allow").map(ConditionAnd::new);
    }

    public Optional<Modifiers> getOwnerModifier() {
        return this.item.getChild("owner_modifier").map(Modifiers::new);
    }

    public Optional<Modifiers> getCompanyProvinceAreaModifier() {
        return this.item.getChild("company_province_area_modifier").map(Modifiers::new);
    }

    public Optional<Modifiers> getAreaModifier() {
        return this.item.getChild("area_modifier").map(Modifiers::new);
    }

    public Optional<Modifiers> getOwnerCompanyRegionModifier() { //Apply to the owner in the region (ie: country modifiers)
        return this.item.getChild("owner_company_region_modifier").map(Modifiers::new);
    }

    public Optional<Modifiers> getCompanyRegionModifier() { //Apply to provinces in region (ie: province modifiers)
        return this.item.getChild("company_region_modifier").map(Modifiers::new);
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Investment investment)) {
            return false;
        }

        return Objects.equals(getName(), investment.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
