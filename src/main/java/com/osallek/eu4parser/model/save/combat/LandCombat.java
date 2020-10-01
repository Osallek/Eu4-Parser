package com.osallek.eu4parser.model.save.combat;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.save.Save;

public class LandCombat extends Combat<LandCombatant> {

    public LandCombat(ClausewitzItem item, Save save) {
        super(item, save, LandCombatant::new);
    }
}
