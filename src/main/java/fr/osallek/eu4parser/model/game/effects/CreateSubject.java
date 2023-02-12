package fr.osallek.eu4parser.model.game.effects;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

public class CreateSubject {

    private final ClausewitzItem item;

    public CreateSubject(ClausewitzItem item) {
        this.item = item;
    }

    public String getSubject() {
        return this.item.getVarAsString("subject");
    }

    public String getSubjectType() {
        return this.item.getVarAsString("subject_type");
    }

}
