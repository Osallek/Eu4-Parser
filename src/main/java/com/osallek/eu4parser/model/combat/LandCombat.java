package com.osallek.eu4parser.model.combat;

import com.osallek.clausewitzparser.model.ClausewitzItem;

public class LandCombat extends Combat<LandCombatant> {

    public LandCombat(ClausewitzItem item) {
        super(item, LandCombatant::new);
    }
}
