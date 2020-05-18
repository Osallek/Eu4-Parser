package com.osallek.eu4parser.model.combat;

import com.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.List;
import java.util.stream.Collectors;

public class Combats {

    private final ClausewitzItem item;

    private List<SiegeCombat> siegeCombats;

    private List<LandCombat> landCombats;

    private List<NavalCombat> navalCombats;

    public Combats(ClausewitzItem item) {
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
                                             .map(SiegeCombat::new)
                                             .collect(Collectors.toList());

        List<ClausewitzItem> landCombatsItems = this.item.getChildren("land_combat");
        this.landCombats = landCombatsItems.stream()
                                           .map(LandCombat::new)
                                           .collect(Collectors.toList());

        List<ClausewitzItem> navalCombatsItems = this.item.getChildren("naval_combat");
        this.navalCombats = navalCombatsItems.stream()
                                             .map(NavalCombat::new)
                                             .collect(Collectors.toList());
    }
}
