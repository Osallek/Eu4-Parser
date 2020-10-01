package com.osallek.eu4parser.model.save.combat;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.save.Save;

import java.util.List;
import java.util.stream.Collectors;

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
                                             .map(child -> new SiegeCombat(child, this.save))
                                             .collect(Collectors.toList());

        List<ClausewitzItem> landCombatsItems = this.item.getChildren("land_combat");
        this.landCombats = landCombatsItems.stream()
                                           .map(child -> new LandCombat(child, this.save))
                                           .collect(Collectors.toList());

        List<ClausewitzItem> navalCombatsItems = this.item.getChildren("naval_combat");
        this.navalCombats = navalCombatsItems.stream()
                                             .map(child -> new NavalCombat(child, this.save))
                                             .collect(Collectors.toList());
    }
}
