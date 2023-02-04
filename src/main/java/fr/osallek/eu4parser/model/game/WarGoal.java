package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

public final class WarGoal {

    private final ClausewitzItem item;

    private final Game game;

    public WarGoal(ClausewitzItem item, Game game) {
        this.item = item;
        this.game = game;
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

    public CasusBelli getCasusBelli() {
        return this.game.getCasusBelli(this.item.getVarAsString("casus_belli"));
    }

    public String getTag() {
        return this.item.getVarAsString("tag");
    }

    public Integer getProvince() {
        return this.item.getVarAsInt("province");
    }
}
