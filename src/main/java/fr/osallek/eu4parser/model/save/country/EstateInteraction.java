package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.model.game.EstatePrivilege;
import fr.osallek.eu4parser.model.game.Game;

import java.time.LocalDate;

public class EstateInteraction {

    private final Game game;

    private final ClausewitzList list;

    public EstateInteraction(Game game, ClausewitzList list) {
        this.game = game;
        this.list = list;
    }

    public EstatePrivilege getPrivilege() {
        return this.game.getEstatePrivilege(this.list.get(0));
    }

    public LocalDate getDate() {
        return this.list.getAsDate(1);
    }

    public void setDate(LocalDate date) {
        this.list.set(1, date);
    }

    public static ClausewitzList addToItem(ClausewitzItem parent, String name, LocalDate date) {
        return parent.addList(null, true, name, ClausewitzUtils.dateToString(date));
    }
}
