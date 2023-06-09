package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.HolyOrder;
import fr.osallek.eu4parser.model.game.StateEdict;
import fr.osallek.eu4parser.model.save.Save;

import java.time.LocalDate;
import java.util.Optional;

public class CountryState {

    private final Save save;

    private final ClausewitzItem item;

    private Edict activeEdict;

    public CountryState(ClausewitzItem item, Save save) {
        this.save = save;
        this.item = item;
        refreshAttributes();
    }

    public Optional<Double> getProsperity() {
        return this.item.getVarAsDouble("prosperity");
    }

    public void setProsperity(double prosperity) {
        this.item.setVariable("prosperity", prosperity);
    }

    public Optional<Boolean> hasStatePatriarch() {
        return this.item.getVarAsBool("has_state_patriach");
    }

    public void setHasStatePatriarch(boolean hasStatePatriach) {
        this.item.setVariable("has_state_patriach", hasStatePatriach);
    }

    public Optional<Boolean> hasStatePasha() {
        return this.item.getVarAsBool("has_state_pasha");
    }

    public void setHasStatePasha(boolean hasStatePatriach) {
        this.item.setVariable("has_state_pasha", hasStatePatriach);
    }

    public Optional<SaveCountry> getCountry() {
        return this.item.getVarAsString("country").map(this.save::getCountry);
    }

    public Optional<Edict> getActiveEdict() {
        return Optional.ofNullable(this.activeEdict);
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

    public Optional<HolyOrder> getHolyOrder() {
        return this.item.getVarAsString("holy_order").map(s -> this.save.getGame().getHolyOrder(s));
    }

    public void setHolyOrder(HolyOrder holyOrder) {
        this.item.setVariable("holy_order", ClausewitzUtils.addQuotes(holyOrder.getName()));
    }

    public Optional<SaveCountry> getHolyOrderFounder() {
        return this.item.getVarAsString("holy_order_founder").map(this.save::getCountry);
    }

    private void refreshAttributes() {
        this.activeEdict = this.item.getChild("active_edict").map(i -> new Edict(i, this.save.getGame())).orElse(null);
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, SaveCountry country) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "country_state", parent.getOrder() + 1);
        toItem.addVariable("country", ClausewitzUtils.addQuotes(country.getTag()));
        toItem.addVariable("prosperity", 0d);

        parent.addChild(toItem);

        return toItem;
    }
}
