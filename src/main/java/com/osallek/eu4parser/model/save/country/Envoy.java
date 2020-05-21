package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

public class Envoy {

    private final ClausewitzItem item;

    public Envoy(ClausewitzItem item) {
        this.item = item;
    }

    public Integer getType() {
        return this.item.getVarAsInt("type");
    }

    public Integer getAction() {
        return this.item.getVarAsInt("action");
    }

    public String getName() {
        return this.item.getVarAsString("name");
    }

    public void setName(String name) {
        ClausewitzVariable nameVar = this.item.getVar("name");
        name = ClausewitzUtils.addQuotes(name);

        if (nameVar != null) {
            nameVar.setValue(name);
        } else {
            this.item.addVariable("name", name);
        }
    }

    public Integer getId() {
        return this.item.getVarAsInt("id");
    }
}
