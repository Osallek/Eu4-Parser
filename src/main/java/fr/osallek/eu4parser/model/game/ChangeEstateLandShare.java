package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Optional;

public class ChangeEstateLandShare {

    private final ClausewitzItem item;

    private final Game game;

    public ChangeEstateLandShare(ClausewitzItem item, Game game) {
        this.item = item;
        this.game = game;
    }

    public Optional<Estate> getEstate() {
        return getEstateName().map(this.game::getEstate);
    } //Null => all

    public Optional<String> getEstateName() {
        return this.item.getVarAsString("estate");
    }

    public void setEstate(String estate) {
        this.item.setVariable("estate", estate);
    }

    public void setEstate(Estate estate) {
        setEstate(estate.getName());
    }

    public Optional<Double> getShare() {
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
