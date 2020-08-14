package com.osallek.eu4parser.model.save.diplomacy;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Date;

public class Dependency extends DatableRelation {

    public Dependency(ClausewitzItem item) {
        super(item);
    }

    public SubjectType getSubjectType() {
        String subjectType = this.item.getVarAsString("subject_type");

        if (subjectType != null) {
            return SubjectType.valueOf(ClausewitzUtils.removeQuotes(subjectType).toUpperCase());
        }

        return null;
    }

    public void setSubjectType(SubjectType subjectType) {
        this.item.setVariable("subject_type", ClausewitzUtils.addQuotes(subjectType.name()));
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String first, String second, Date startDate, SubjectType type) {
        ClausewitzItem toItem = DatableRelation.addToItem(parent, "dependency", first, second, startDate);
        toItem.addVariable("subject_type", ClausewitzUtils.addQuotes(type.name().toLowerCase()));

        return toItem;
    }
}
