package fr.osallek.eu4parser.model.game.condition;

import java.util.stream.Collectors;

public class ConditionTagOnly extends ConditionAnd {

    public ConditionTagOnly(ConditionAbstract other) {
        super(other);
        this.filter = this.filter.and("tag"::equalsIgnoreCase);

        if (other.getScopes() != null) {
            this.scopes = other.getScopes().stream().map(ConditionAbstract::tagOnly).collect(Collectors.toList());
            this.scopes.removeIf(ConditionAbstract::isEmpty);
        }
    }
}
