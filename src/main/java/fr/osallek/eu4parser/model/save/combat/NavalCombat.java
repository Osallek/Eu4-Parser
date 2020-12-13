package fr.osallek.eu4parser.model.save.combat;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.Save;

public class NavalCombat extends Combat<NavalCombatant> {

    public NavalCombat(ClausewitzItem item, Save save) {
        super(item, save, NavalCombatant::new);
    }
}
