package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

public class ChangeEstateLandShare {

    private final ClausewitzItem item;

    private final Game game;

    public ChangeEstateLandShare(ClausewitzItem item, Game game) {
        this.item = item;
        this.game = game;
    }

    public Estate getEstate() {
        return this.game.getEstate(getEstateName());
    } //Null => all

    public String getEstateName() {
        return this.item.getVarAsString("estate");
    }

    public void setEstate(String estate) {
        this.item.setVariable("estate", estate);
    }

    public void setEstate(Estate estate) {
        setEstate(estate.getName());
    }

    public Double getShare() {
        return this.item.getVarAsDouble("share");
    }

    public void setShare(Double share) {
        this.item.setVariable("share", share);
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String estate, Double share) {
        ClausewitzItem toItem = parent.addChild("change_estate_land_share");
        toItem.addVariable("estate", estate);
        toItem.addVariable("share", share);

        return toItem;
    }
}
