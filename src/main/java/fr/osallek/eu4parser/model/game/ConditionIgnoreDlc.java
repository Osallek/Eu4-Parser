package fr.osallek.eu4parser.model.game;

import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ConditionIgnoreDlc extends ConditionAnd {

    public ConditionIgnoreDlc(ConditionAbstract other) {
        super(other);
        this.filter = this.filter.and(Predicate.not("has_dlc"::equalsIgnoreCase));

        if (other.getScopes() != null) {
            this.scopes = other.getScopes().stream().map(ConditionAbstract::ignoreDlc).collect(Collectors.toList());
            this.scopes.removeIf(ConditionAbstract::isEmpty);
        }
    }
}
