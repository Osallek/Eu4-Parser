package fr.osallek.eu4parser.model.save.religion;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Optional;

public record MuslimRelation(ClausewitzItem item) {

    public Optional<MuslimRelationSchool> getFirst() {
        return this.item.getVarAsString("first").map(String::toUpperCase).map(ClausewitzUtils::removeQuotes).map(MuslimRelationSchool::valueOf);
    }

    public Optional<MuslimRelationSchool> getSecond() {
        return this.item.getVarAsString("second").map(String::toUpperCase).map(ClausewitzUtils::removeQuotes).map(MuslimRelationSchool::valueOf);
    }

    public Optional<MuslimRelationValue> getRelation() {
        return this.item.getVarAsString("value").map(String::toUpperCase).map(MuslimRelationValue::valueOf);
    }

    public void setRelation(MuslimRelationValue relation) {
        this.item.setVariable("value", relation.name().toLowerCase());
    }
}
