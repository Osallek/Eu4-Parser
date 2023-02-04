package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.common.Eu4Utils;

import java.time.LocalDate;
import java.util.List;

public final class WarHistoryEvent {

    private final ClausewitzItem item;

    public WarHistoryEvent(ClausewitzItem item) {
        this.item = item;
    }

    public LocalDate getDate() {
        return Eu4Utils.stringToDate(this.item.getName());
    }

    public List<String> getAddAttacker() {
        return this.item.getVarsAsStrings("add_attacker").stream().map(ClausewitzUtils::removeQuotes).toList();
    }

    public List<String> getAddDefender() {
        return this.item.getVarsAsStrings("add_defender").stream().map(ClausewitzUtils::removeQuotes).toList();
    }

    public List<String> getRemAttacker() {
        return this.item.getVarsAsStrings("rem_attacker").stream().map(ClausewitzUtils::removeQuotes).toList();
    }

    public List<String> getRemDefender() {
        return this.item.getVarsAsStrings("rem_defender").stream().map(ClausewitzUtils::removeQuotes).toList();
    }

    public List<Battle> getBattles() {
        return this.item.getChildren("battle").stream().map(child -> new Battle(getDate(), child)).toList();
    }

}
