package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

public final class WarGoal {

    private final ClausewitzItem item;

    public WarGoal(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getVarAsString("name");
    }

    public void setName(String name) {
        this.item.setVariable("name", name);
    }

    public String getType() {
        return this.item.getVarAsString("type");
    }

    public String getCasusBelli() {
        return this.item.getVarAsString("casus_belli");
    }

    public CasusBelli getCasusBelli(Game game) {
        return game.getCasusBelli(this.item.getVarAsString("casus_belli"));
    }

    public String getTag() {
        return this.item.getVarAsString("tag");
    }

    public Integer getProvince() {
        return this.item.getVarAsInt("province");
    }
}
