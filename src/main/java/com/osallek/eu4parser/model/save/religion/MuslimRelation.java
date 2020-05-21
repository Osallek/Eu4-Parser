package com.osallek.eu4parser.model.save.religion;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

public class MuslimRelation {

    private final ClausewitzItem item;

    public MuslimRelation(ClausewitzItem item) {
        this.item = item;
    }

    public MuslimRelationSchool getFirst() {
        String value = this.item.getVarAsString("first");

        if (value == null) {
            return null;
        }

        return MuslimRelationSchool.valueOf(ClausewitzUtils.removeQuotes(value).toUpperCase());
    }

    public MuslimRelationSchool getSecond() {
        String value = this.item.getVarAsString("second");

        if (value == null) {
            return null;
        }

        return MuslimRelationSchool.valueOf(ClausewitzUtils.removeQuotes(value).toUpperCase());
    }

    public MuslimRelationValue getRelation() {
        String value = this.item.getVarAsString("value");

        if (value == null) {
            return null;
        }

        return MuslimRelationValue.valueOf(value.toUpperCase());
    }

    public void setRelation(MuslimRelationValue relation) {
        ClausewitzVariable var = this.item.getVar("value");

        if (var != null) {
            var.setValue(relation.name().toLowerCase());
        } else {
            this.item.addVariable("value", relation.name().toLowerCase());
        }
    }
}
