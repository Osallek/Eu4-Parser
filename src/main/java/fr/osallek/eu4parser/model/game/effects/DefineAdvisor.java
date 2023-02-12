package fr.osallek.eu4parser.model.game.effects;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;

public class DefineAdvisor {

    private final ClausewitzItem item;

    public DefineAdvisor(ClausewitzItem item) {
        this.item = item;
    }

    public String getType() {
        return this.item.getVarAsString("type");
    }

    public String getName() {
        return ClausewitzUtils.removeQuotes(this.item.getVarAsString("name"));
    }

    public Integer getSkill() {
        return this.item.getVarAsInt("skill");
    }

    public Integer getCostMultiplier() {
        return this.item.getVarAsInt("cost_multiplier");
    }

    public String getCulture() {
        return this.item.getVarAsString("culture");
    }

    public String getReligion() {
        return this.item.getVarAsString("religion");
    }

    public Boolean getFemale() {
        return this.item.getVarAsBool("female");
    }

    public Boolean getDiscount() {
        return this.item.getVarAsBool("discount");
    }

    public Integer getLocation() {
        return this.item.getVarAsInt("location");
    }
}
