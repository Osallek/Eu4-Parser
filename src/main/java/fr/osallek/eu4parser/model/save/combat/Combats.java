package fr.osallek.eu4parser.model.save.combat;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.Save;

import java.util.List;

public class Combats {

    private final Save save;

    private final ClausewitzItem item;

    private List<SiegeCombat> siegeCombats;

    private List<LandCombat> landCombats;

    private List<NavalCombat> navalCombats;

    public Combats(ClausewitzItem item, Save save) {
        this.save = save;
        this.item = item;
        refreshAttributes();
    }

    public List<SiegeCombat> getSiegeCombats() {
        return siegeCombats;
    }

    public List<LandCombat> getLandCombats() {
        return landCombats;
    }

    public List<NavalCombat> getNavalCombats() {
        return navalCombats;
    }

    private void refreshAttributes() {
        List<ClausewitzItem> siegeCombatsItems = this.item.getChildren("siege_combat");
        this.siegeCombats = siegeCombatsItems.stream()
                                             .map(child -> new fr.osallek.eu4parser.model.save.combat.SiegeCombat(child, this.save))
                                             .toList();

        List<ClausewitzItem> landCombatsItems = this.item.getChildren("land_combat");
        this.landCombats = landCombatsItems.stream()
                                           .map(child -> new fr.osallek.eu4parser.model.save.combat.LandCombat(child, this.save))
                                           .toList();

        List<ClausewitzItem> navalCombatsItems = this.item.getChildren("naval_combat");
        this.navalCombats = navalCombatsItems.stream()
                                             .map(child -> new fr.osallek.eu4parser.model.save.combat.NavalCombat(child, this.save))
                                             .toList();
    }
}
