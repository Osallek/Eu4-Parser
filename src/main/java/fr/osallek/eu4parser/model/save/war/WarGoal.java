package fr.osallek.eu4parser.model.save.war;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.CasusBelli;
import fr.osallek.eu4parser.model.save.Save;
import fr.osallek.eu4parser.model.save.country.SaveCountry;
import fr.osallek.eu4parser.model.save.province.SaveProvince;

public record WarGoal(ClausewitzItem item, Save save) {

    public String getName() {
        return this.item.getVarAsString("name");
    }

    public void setName(String name) {
        this.item.setVariable("name", name);
    }

    public String getType() {
        return this.item.getVarAsString("type");
    }

    public CasusBelli getCasusBelli() {
        return this.save.getGame().getCasusBelli(this.item.getVarAsString("casus_belli"));
    }

    public SaveCountry getTag() {
        return this.save.getCountry(this.item.getVarAsString("tag"));
    }

    public SaveProvince getProvince() {
        return this.save.getProvince(this.item.getVarAsInt("province"));
    }
}
