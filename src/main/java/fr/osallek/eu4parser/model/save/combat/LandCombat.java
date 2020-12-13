package fr.osallek.eu4parser.model.save.combat;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.Save;

public class LandCombat extends Combat<LandCombatant> {

    public LandCombat(ClausewitzItem item, Save save) {
        super(item, save, LandCombatant::new);
    }
}
