package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

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

    public String getCategory() {
        return this.item.getVarAsString("category");
    }

    public void setCategory(String category) {
        if (StringUtils.isBlank(category)) {
            this.item.removeVariable("category");
        } else {
            this.item.setVariable("category", category);
        }
    }

    public Double getCost() {
        return this.item.getVarAsDouble("cost");
    }

    public void setCost(Double cost) {
        if (cost == null) {
            this.item.removeVariable("cost");
        } else {
            this.item.setVariable("cost", cost);
        }
    }

    public Investment getUpgradesTo() {
        return this.item.getVarAsString("upgrades_to") == null ? null : this.game.getInvestment(this.item.getVarAsString("upgrades_to"));
    }

    public String getSprite() {
        return ClausewitzUtils.removeQuotes(this.item.getVarAsString("sprite"));
    }

    public void setSprite(String sprite) {
        if (StringUtils.isBlank(sprite)) {
            this.item.removeVariable("sprite");
        } else {
            this.item.setVariable("sprite", ClausewitzUtils.addQuotes(sprite));
        }
    }

    public Condition getAllow() {
        ClausewitzItem child = this.item.getChild("allow");
        return child == null ? null : new Condition(child);
    }

    public Modifiers getOwnerModifier() {
        return new Modifiers(this.item.getChild("owner_modifier"));
    }

    public Modifiers getCompanyProvinceAreaModifier() {
        return new Modifiers(this.item.getChild("company_province_area_modifier"));
    }

    public Modifiers getAreaModifier() {
        return new Modifiers(this.item.getChild("area_modifier"));
    }

    public Modifiers getOwnerCompanyRegionModifier() { //Apply to the owner in the region (ie: country modifiers)
        return new Modifiers(this.item.getChild("owner_company_region_modifier"));
    }

    public Modifiers getCompanyRegionModifier() { //Apply to provinces in region (ie: province modifiers)
        return new Modifiers(this.item.getChild("company_region_modifier"));
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

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Investment investment = (Investment) o;

        return Objects.equals(getName(), investment.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
