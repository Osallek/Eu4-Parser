package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import com.osallek.eu4parser.model.save.Id;

public class Regiment extends Ship {

    public Regiment(ClausewitzItem item) {
        super(item);
    }

    public Double getDrill() {
        return this.item.getLastVarAsDouble("drill");
    }

    public void setDrill(Double drill) {
        ClausewitzVariable var = this.item.getVar("drill");

        if (drill < 0d) {
            drill = 0d;
        } else if (drill > 100d) {
            drill = 100d;
        }

        if (var != null) {
            var.setValue(drill);
        } else {
            this.item.addVariable("drill", drill);
        }
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, long id, String name, int home, String type, double morale, double drill) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "regiment", parent.getOrder() + 1);
        Id.addToItem(toItem, id, 54);
        toItem.addVariable("name", name);
        toItem.addVariable("home", home);
        toItem.addVariable("type", ClausewitzUtils.addQuotes(type));
        toItem.addVariable("morale", morale);
        toItem.addVariable("drill", drill);

        parent.addChild(toItem);

        return toItem;
    }
}
