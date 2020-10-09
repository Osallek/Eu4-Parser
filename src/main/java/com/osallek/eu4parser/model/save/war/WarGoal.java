package com.osallek.eu4parser.model.save.war;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.game.CasusBelli;
import com.osallek.eu4parser.model.save.Save;
import com.osallek.eu4parser.model.save.country.Country;
import com.osallek.eu4parser.model.save.province.SaveProvince;

public class WarGoal {

    protected final Save save;

    protected final ClausewitzItem item;

    public WarGoal(ClausewitzItem item, Save save) {
        this.save = save;
        this.item = item;
    }

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

    public Country getTag() {
        return this.save.getCountry(this.item.getVarAsString("tag"));
    }

    public SaveProvince getProvince() {
        return this.save.getProvince(this.item.getVarAsInt("province"));
    }
}
