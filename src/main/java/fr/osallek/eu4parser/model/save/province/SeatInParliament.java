package fr.osallek.eu4parser.model.save.province;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

public class SeatInParliament {

    protected final ClausewitzItem item;

    public SeatInParliament(ClausewitzItem item) {
        this.item = item;
    }

    public Boolean getBack() {
        return this.item.getVarAsBool("back");
    }

    public void setBack(boolean back) {
        this.item.setVariable("back", back);
    }

    public String getBribe() {
        return this.item.getVarAsString("bribe");
    }

    public void setBribe(boolean bribe) {
        this.item.setVariable("bribe", bribe);
    }
}
