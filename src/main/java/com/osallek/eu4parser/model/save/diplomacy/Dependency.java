package com.osallek.eu4parser.model.save.diplomacy;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.game.SubjectType;
import com.osallek.eu4parser.model.save.Save;

import java.time.LocalDate;

public class Dependency extends DatableRelation {

    public Dependency(ClausewitzItem item, Save save) {
        super(item, save);
    }

    public SubjectType getSubjectType() {
        return this.save.getGame().getSubjectType(ClausewitzUtils.removeQuotes(this.item.getVarAsString("subject_type")));
    }

    public void setSubjectType(SubjectType subjectType) {
        this.item.setVariable("subject_type", ClausewitzUtils.addQuotes(subjectType.getName()));
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String first, String second, LocalDate startDate, SubjectType type) {
        ClausewitzItem toItem = DatableRelation.addToItem(parent, "dependency", first, second, startDate);
        toItem.addVariable("subject_type", ClausewitzUtils.addQuotes(type.getName()));

        return toItem;
    }
}
