package com.osallek.eu4parser.model.save.diplomacy;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.save.Save;
import org.apache.commons.lang3.BooleanUtils;

import java.time.LocalDate;

public class KnowledgeSharing extends EndDatableRelation {

    public KnowledgeSharing(ClausewitzItem item, Save save) {
        super(item, save);
    }

    public boolean getSubjectInteractions() {
        return BooleanUtils.toBoolean(this.item.getVarAsBool("subject_interactions"));
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String first, String second, LocalDate startDate, LocalDate endDate, boolean subjectInteractions) {
        ClausewitzItem toItem = EndDatableRelation.addToItem(parent, "knowledge_sharing", first, second, startDate, endDate);
        toItem.addVariable("subject_interactions", subjectInteractions);

        return toItem;
    }
}
