package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.Game;
import fr.osallek.eu4parser.model.game.ReligiousReform;
import fr.osallek.eu4parser.model.game.ReligiousReforms;

import java.util.List;

public class SaveReligiousReforms {

    private final Game game;

    private final ClausewitzItem item;

    public SaveReligiousReforms(ClausewitzItem item, Game game) {
        this.game = game;
        this.item = item;
    }

    public ReligiousReforms getReligiousReforms() {
        return this.game.getReligiousReforms(this.item.getList(0).getName());
    }

    public List<ReligiousReform> getAdoptedReforms() {
        return this.item.getList(0)
                        .getValuesAsInt()
                        .stream()
                        .filter(i -> i == 1)
                        .map(i -> getReligiousReforms().getReforms().get(i))
                        .toList();
    }

    public void addAdoptedReform(ReligiousReform religiousReform) {
        this.item.getList(0).set(religiousReform.getIndex(), 1);
    }

    public void removeAdoptedReform(int index) {
        this.item.getList(0).set(index, 0);
    }

    public void removeAdoptedReform(ReligiousReform religiousReform) {
        removeAdoptedReform(religiousReform.getIndex());
    }
}
