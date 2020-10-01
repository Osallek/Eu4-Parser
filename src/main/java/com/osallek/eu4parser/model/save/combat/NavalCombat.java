package com.osallek.eu4parser.model.save.combat;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.save.Save;

public class NavalCombat extends Combat<NavalCombatant> {

    public NavalCombat(ClausewitzItem item, Save save) {
        super(item, save, NavalCombatant::new);
    }
}
