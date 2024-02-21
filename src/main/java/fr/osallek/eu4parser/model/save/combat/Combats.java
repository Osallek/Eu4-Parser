package fr.osallek.eu4parser.model.save.combat;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.Save;

import java.util.List;

public class Combats {

    private final Save save;

    private final ClausewitzItem item;

    public Combats(ClausewitzItem item, Save save) {
        this.save = save;
        this.item = item;
    }

    public List<SiegeCombat> getSiegeCombats() {
        return this.item.getChildren("siege_combat").stream().map(child -> new SiegeCombat(child, this.save)).toList();
    }

    public List<LandCombat> getLandCombats() {
        return this.item.getChildren("land_combat").stream().map(child -> new LandCombat(child, this.save)).toList();
    }

    public List<NavalCombat> getNavalCombats() {
        return this.item.getChildren("naval_combat").stream().map(child -> new NavalCombat(child, this.save)).toList();
    }
}
