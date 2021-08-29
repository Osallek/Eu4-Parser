package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.HolyOrder;
import fr.osallek.eu4parser.model.game.StateEdict;
import fr.osallek.eu4parser.model.save.Save;

import java.time.LocalDate;

public class CountryState {

    private final Save save;

    private final ClausewitzItem item;

    private Edict activeEdict;

    public CountryState(ClausewitzItem item, Save save) {
        this.save = save;
        this.item = item;
        refreshAttributes();
    }

    public Double getProsperity() {
        return this.item.getVarAsDouble("prosperity");
    }

    public void setProsperity(double prosperity) {
        this.item.setVariable("prosperity", prosperity);
    }

    public Boolean hasStatePatriarch() {
        return this.item.getVarAsBool("has_state_patriach");
    }

    public void setHasStatePatriarch(boolean hasStatePatriach) {
        this.item.setVariable("has_state_patriach", hasStatePatriach);
    }

    public Boolean hasStatePasha() {
        return this.item.getVarAsBool("has_state_pasha");
    }

    public void setHasStatePasha(boolean hasStatePatriach) {
        this.item.setVariable("has_state_pasha", hasStatePatriach);
    }

    public SaveCountry getCountry() {
        return this.save.getCountry(this.item.getVarAsString("country"));
    }

    public Edict getActiveEdict() {
        return activeEdict;
    }

    public void setActiveEdict(StateEdict which, LocalDate date) {
        if (this.activeEdict != null) {
            this.activeEdict.setWhich(which);
            this.activeEdict.setDate(date);
        } else {
            Edict.addToItem(this.item, which, date);
            refreshAttributes();
        }
    }

    public void removeActiveEdict() {
        this.item.removeChild("active_edict");
        this.activeEdict = null;
    }

    public HolyOrder getHolyOrder() {
        return this.save.getGame().getHolyOrder(ClausewitzUtils.removeQuotes(this.item.getVarAsString("holy_order")));
    }

    public void setHolyOrder(HolyOrder holyOrder) {
        this.item.setVariable("holy_order", ClausewitzUtils.addQuotes(holyOrder.getName()));
    }

    public SaveCountry getHolyOrderFounder() {
        return this.save.getCountry(this.item.getVarAsString("holy_order_founder"));
    }

    private void refreshAttributes() {
        ClausewitzItem activeEditItem = this.item.getChild("active_edict");

        if (activeEditItem != null) {
            this.activeEdict = new Edict(activeEditItem, this.save.getGame());
        }
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, SaveCountry country) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "country_state", parent.getOrder() + 1);
        toItem.addVariable("country", ClausewitzUtils.addQuotes(country.getTag()));
        toItem.addVariable("prosperity", 0d);

        parent.addChild(toItem);

        return toItem;
    }
}
