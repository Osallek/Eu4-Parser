package fr.osallek.eu4parser.model.save.province;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.ParliamentBribe;
import fr.osallek.eu4parser.model.save.Save;

public record SeatInParliament(ClausewitzItem item, Save save) {

    public Boolean getBack() {
        return this.item.getVarAsBool("back");
    }

    public void setBack(boolean back) {
        this.item.setVariable("back", back);
    }

    public String getBribeName() {
        return this.item.getVarAsString("bribe");
    }

    public ParliamentBribe getBribe() {
        return this.save.getGame().getParliamentBribe(ClausewitzUtils.removeQuotes(getBribeName()));
    }

    public void setBribe(ParliamentBribe bribe) {
        this.item.setVariable("bribe", bribe.getName());
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String bribe) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "seat_in_parliament", parent.getVar("previous_controller").getOrder() + 1, true, true);
        toItem.addVariable("bribe", bribe);

        return toItem;
    }
}
