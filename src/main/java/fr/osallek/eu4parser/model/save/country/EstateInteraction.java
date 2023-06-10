package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.model.game.EstatePrivilege;
import fr.osallek.eu4parser.model.game.Game;

import java.time.LocalDate;
import java.util.Optional;

public record EstateInteraction(Game game, ClausewitzList list) {

    public Optional<EstatePrivilege> getPrivilege() {
        return this.list.get(0).map(this.game::getEstatePrivilege);
    }

    public Optional<LocalDate> getDate() {
        return this.list.getAsDate(1);
    }

    public void setDate(LocalDate date) {
        this.list.set(1, date);
    }

    public static ClausewitzList addToItem(ClausewitzItem parent, String name, LocalDate date) {
        return parent.addList(null, true, name, ClausewitzUtils.dateToString(date));
    }
}
