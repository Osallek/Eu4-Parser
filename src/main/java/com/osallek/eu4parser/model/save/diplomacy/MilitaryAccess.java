package com.osallek.eu4parser.model.save.diplomacy;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.save.Save;
import org.apache.commons.lang3.BooleanUtils;

import java.time.LocalDate;

public class MilitaryAccess extends DatableRelation {

    public MilitaryAccess(ClausewitzItem item, Save save) {
        super(item, save);
    }

    public boolean enforcePeace() {
        return BooleanUtils.toBoolean(this.item.getVarAsBool("enforce_peace"));
    }

    public void setEnforcePeace(boolean enforcePeace) {
        this.item.setVariable("enforce_peace", enforcePeace);
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String name, String first, String second, LocalDate startDate, boolean enforcePeace) {
        ClausewitzItem toItem = DatableRelation.addToItem(parent, name, first, second, startDate);
        toItem.addVariable("enforce_peace", enforcePeace);

        return toItem;
    }
}
