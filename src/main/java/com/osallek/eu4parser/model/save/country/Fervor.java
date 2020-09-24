package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.List;

public class Fervor {

    private final ClausewitzItem item;

    public Fervor(ClausewitzItem item) {
        this.item = item;
    }

    public Double getValue() {
        return this.item.getVarAsDouble("value");
    }

    public void setValue(Double value) {
        if (value < -100d) {
            value = -100d;
        } else if (value > 100d) {
            value = 100d;
        }

        this.item.setVariable("value", value);
    }

    public List<String> getActives() {
        return this.item.getVarsAsStrings("active");
    }

    public void addActive(String active) {
        List<String> ignoreDecisions = this.item.getVarsAsStrings("active");

        if (!ignoreDecisions.contains(active)) {
            this.item.addVariable("active", active);
        }
    }

    public void removeActive(int index) {
        this.item.removeVariable("active", index);
    }

    public void removeActive(String active) {
        this.item.removeVariable("active", active);
    }
}
