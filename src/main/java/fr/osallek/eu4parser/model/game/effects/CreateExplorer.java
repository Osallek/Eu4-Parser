package fr.osallek.eu4parser.model.game.effects;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

//create_explorer, create_conquistador
public class CreateExplorer {

    private final ClausewitzItem item;

    public CreateExplorer(ClausewitzItem item) {
        this.item = item;
    }

    public Integer getTradition() {
        return this.item.getVarAsInt("tradition");
    }
}
