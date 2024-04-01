package fr.osallek.eu4parser.model.save.diplomacy;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.Save;

import java.time.LocalDate;

public class CasusBelli extends EndDatableRelation {

    public CasusBelli(ClausewitzItem item, Save save) {
        super(item, save);
    }

    public String getTypeName() {
        return ClausewitzUtils.removeQuotes(this.item.getVarAsString("type"));
    }

    public fr.osallek.eu4parser.model.game.CasusBelli getType() {
        return this.save.getGame().getCasusBelli(this.item.getVarAsString("type"));
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String first, String second, LocalDate startDate, LocalDate endDate, fr.osallek.eu4parser.model.game.CasusBelli type) {
        ClausewitzItem toItem = EndDatableRelation.addToItem(parent, "casus_belli", first, second, startDate, endDate);
        toItem.addVariable("type", ClausewitzUtils.addQuotes(type.getName()));

        return toItem;
    }
}
