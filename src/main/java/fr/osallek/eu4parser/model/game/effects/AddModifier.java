package fr.osallek.eu4parser.model.game.effects;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

//add_province_modifier, add_country_modifier
public class AddModifier {

    private final ClausewitzItem item;

    public AddModifier(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getVarAsString("name");
    }

    public Integer getDuration() {
        return this.item.getVarAsInt("duration");
    }

    public Boolean isHidden() {
        return this.item.getVarAsBool("hidden");
    }

    public String getDesc() {
        return this.item.getVarAsString("desc");
    }
}
