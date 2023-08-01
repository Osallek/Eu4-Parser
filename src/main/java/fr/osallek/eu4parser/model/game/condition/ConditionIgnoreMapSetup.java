package fr.osallek.eu4parser.model.game.condition;

import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ConditionIgnoreMapSetup extends ConditionAnd {

    public ConditionIgnoreMapSetup(ConditionAbstract other) {
        super(other);
        this.filter = this.filter.and(Predicate.not("map_setup"::equalsIgnoreCase));

        if (other.getScopes() != null) {
            this.scopes = other.getScopes().stream().map(ConditionAbstract::ignoreMapSetup).collect(Collectors.toList());
            this.scopes.removeIf(ConditionAbstract::isEmpty);
        }
    }
}
