package com.osallek.eu4parser.model.save.combat;

import com.osallek.clausewitzparser.model.ClausewitzItem;

public class NavalCombat extends Combat<NavalCombatant> {

    public NavalCombat(ClausewitzItem item) {
        super(item, NavalCombatant::new);
    }
}
