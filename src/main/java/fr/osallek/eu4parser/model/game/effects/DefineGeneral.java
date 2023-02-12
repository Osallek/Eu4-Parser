package fr.osallek.eu4parser.model.game.effects;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

//define_admiral, define_explorer, define_conquistador, define_general
public class DefineGeneral extends DefineRulerToGeneral {

    public DefineGeneral(ClausewitzItem item) {
        super(item);
    }

    public String getTrait() {
        return this.item.getVarAsString("trait");
    }

    public Boolean getFemale() {
        return this.item.getVarAsBool("female");
    }
}
