package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import org.apache.commons.lang3.tuple.Pair;

public class ConditionNot extends ConditionAnd {

    @SafeVarargs
    public ConditionNot(Pair<String, String>... conditions) {
        super(conditions);
        this.anyMatch = () -> true;
    }

    public ConditionNot(ClausewitzItem item, String... ignore) {
        super(item, ignore);
        this.anyMatch = () -> true;
    }

    public ConditionNot(ConditionAbstract other) {
        super(other);

        if (other instanceof ConditionAnd and) {
            this.anyMatch = () -> !and.anyMatch.getAsBoolean();
        } else {
            this.anyMatch = () -> true;
        }
    }
}