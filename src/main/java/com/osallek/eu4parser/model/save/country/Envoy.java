package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;

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
        this.item.setVariable("name", ClausewitzUtils.addQuotes(name));
    }

    public Integer getId() {
        return this.item.getVarAsInt("id");
    }
}
