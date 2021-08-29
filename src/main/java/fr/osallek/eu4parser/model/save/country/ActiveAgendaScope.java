package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.List;
import java.util.stream.Collectors;

public class ActiveAgendaScope {

    private final ActiveAgenda activeAgenda;

    private final ClausewitzItem item;

    private List<SavedEventTarget> savedEventTargets;

    public ActiveAgendaScope(ClausewitzItem item, ActiveAgenda activeAgenda) {
        this.activeAgenda = activeAgenda;
        this.item = item;
        refreshAttributes();
    }

    public Boolean scopeIsValid() {
        return this.item.getVarAsBool("scope_is_valid");
    }

    public SaveCountry getCountry() {
        return this.activeAgenda.getCountry().getSave().getCountry(this.item.getVarAsString("country"));
    }

    public void setCountry(SaveCountry country) {
        this.item.setVariable("country", ClausewitzUtils.addQuotes(country.getTag()));
    }

    public List<SavedEventTarget> getSavedEventTargets() {
        return savedEventTargets;
    }

    public ActiveAgenda getActiveAgenda() {
        return activeAgenda;
    }

    private void refreshAttributes() {
        List<ClausewitzItem> children = this.item.getChildren("saved_event_target");
        this.savedEventTargets = children.stream().map(child -> new SavedEventTarget(child, this)).collect(Collectors.toList());
    }
}
