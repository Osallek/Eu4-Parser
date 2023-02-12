package fr.osallek.eu4parser.model.game.effects;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.ConditionAnd;

//add_years_of_owned_provinces_production_income, add_years_of_owned_provinces_manpower, add_years_of_owned_provinces_sailors
public class AddYearsOf {

    private final ClausewitzItem item;

    public AddYearsOf(ClausewitzItem item) {
        this.item = item;
    }

    public Integer getYears() {
        return this.item.getVarAsInt("years");
    }

    public String getCustomTooltip() {
        return this.item.getVarAsString("custom_tooltip");
    }

    public ConditionAnd getTrigger() {
        return this.item.hasChild("trigger") ? new ConditionAnd(this.item.getChild("trigger")) : null;
    }
}
