package com.osallek.eu4parser.model.save.diplomacy;

import com.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Date;

public class KnowledgeSharing extends EndDatableRelation {

    public KnowledgeSharing(ClausewitzItem item) {
        super(item);
    }

    public boolean getSubjectInteractions() {
        return this.item.getVarAsBool("subject_interactions");
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String first, String second, Date startDate, Date endDate, boolean subjectInteractions) {
        ClausewitzItem toItem = EndDatableRelation.addToItem(parent, "knowledge_sharing", first, second, startDate, endDate);
        toItem.addVariable("subject_interactions", subjectInteractions);

        return toItem;
    }
}
