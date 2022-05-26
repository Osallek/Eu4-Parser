package fr.osallek.eu4parser.model.save.religion;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;

public record MuslimRelation(ClausewitzItem item) {

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
        this.item.setVariable("value", relation.name().toLowerCase());
    }
}
