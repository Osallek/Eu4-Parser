package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.List;

public class ActiveAgendaScope {

    private final ActiveAgenda activeAgenda;

    private final ClausewitzItem item;

    public ActiveAgendaScope(ClausewitzItem item, ActiveAgenda activeAgenda) {
        this.activeAgenda = activeAgenda;
        this.item = item;
    }

    public Boolean scopeIsValid() {
        return this.item.getVarAsBool("scope_is_valid");
    }

    public SaveCountry getCountry() {
        return this.activeAgenda.country().getSave().getCountry(this.item.getVarAsString("country"));
    }

    public void setCountry(SaveCountry country) {
        this.item.setVariable("country", ClausewitzUtils.addQuotes(country.getTag()));
    }

    public List<SavedEventTarget> getSavedEventTargets() {
        return this.item.getChildren("saved_event_target").stream().map(child -> new SavedEventTarget(child, this)).toList();
    }

    public ActiveAgenda getActiveAgenda() {
        return activeAgenda;
    }
}
