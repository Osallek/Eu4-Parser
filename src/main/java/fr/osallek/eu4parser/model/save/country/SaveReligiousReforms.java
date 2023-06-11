package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.model.game.Game;
import fr.osallek.eu4parser.model.game.ReligiousReform;
import fr.osallek.eu4parser.model.game.ReligiousReforms;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SaveReligiousReforms {

    private final Game game;

    private final ClausewitzItem item;

    public SaveReligiousReforms(ClausewitzItem item, Game game) {
        this.game = game;
        this.item = item;
    }

    public Optional<ReligiousReforms> getReligiousReforms() {
        return this.item.getList(0).map(ClausewitzList::getName).map(this.game::getReligiousReforms);
    }

    public List<ReligiousReform> getAdoptedReforms() {
        List<Integer> valuesAsInt = this.item.getList(0).get().getValuesAsInt();
        List<ReligiousReform> reforms = new ArrayList<>();

        getReligiousReforms().ifPresent(religiousReforms -> {
            for (int i = 0; i < valuesAsInt.size(); i++) {
                if (valuesAsInt.get(i) == 1) {
                    reforms.add(religiousReforms.getReforms().get(i));
                }
            }
        });

        return reforms;
    }

    public void addAdoptedReform(ReligiousReform religiousReform) {
        this.item.getList(0).ifPresent(clausewitzList -> clausewitzList.set(religiousReform.getIndex(), 1));
    }

    public void removeAdoptedReform(int index) {
        this.item.getList(0).ifPresent(clausewitzList -> clausewitzList.set(index, 0));
    }

    public void removeAdoptedReform(ReligiousReform religiousReform) {
        removeAdoptedReform(religiousReform.getIndex());
    }
}
