package com.osallek.eu4parser.model.save.diplomacy;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.save.Save;

import java.util.Date;

public class CasusBelli extends EndDatableRelation {

    public CasusBelli(ClausewitzItem item, Save save) {
        super(item, save);
    }

    public com.osallek.eu4parser.model.game.CasusBelli getType() {
        return this.save.getGame().getCasusBelli(this.item.getVarAsString("type"));
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String first, String second, Date startDate, Date endDate, com.osallek.eu4parser.model.game.CasusBelli type) {
        ClausewitzItem toItem = EndDatableRelation.addToItem(parent, "casus_belli", first, second, startDate, endDate);
        toItem.addVariable("type", ClausewitzUtils.addQuotes(type.getName()));

        return toItem;
    }
}
