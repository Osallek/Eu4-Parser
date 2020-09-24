package com.osallek.eu4parser.model.save.diplomacy;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.save.Save;

import java.util.Date;

public class Dependency extends DatableRelation {

    public Dependency(ClausewitzItem item, Save save) {
        super(item, save);
    }

    public String getSubjectType() {
        return this.item.getVarAsString("subject_type");
    }

    public void setSubjectType(String subjectType) {
        this.item.setVariable("subject_type", ClausewitzUtils.addQuotes(subjectType));
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String first, String second, Date startDate, String type) {
        ClausewitzItem toItem = DatableRelation.addToItem(parent, "dependency", first, second, startDate);
        toItem.addVariable("subject_type", ClausewitzUtils.addQuotes(type));

        return toItem;
    }
}
