package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Optional;

public final class WarGoal {

    private final ClausewitzItem item;

    private final Game game;

    public WarGoal(ClausewitzItem item, Game game) {
        this.item = item;
        this.game = game;
    }

    public Optional<String> getName() {
        return this.item.getVarAsString("name");
    }

    public void setName(String name) {
        this.item.setVariable("name", name);
    }

    public Optional<String> getType() {
        return this.item.getVarAsString("type");
    }

    public Optional<CasusBelli> getCasusBelli() {
        return this.item.getVarAsString("casus_belli").map(this.game::getCasusBelli);
    }

    public Optional<String> getTag() {
        return this.item.getVarAsString("tag");
    }

    public Optional<Integer> getProvince() {
        return this.item.getVarAsInt("province");
    }
}
