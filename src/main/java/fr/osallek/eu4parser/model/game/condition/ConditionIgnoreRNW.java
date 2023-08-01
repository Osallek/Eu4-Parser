package fr.osallek.eu4parser.model.game.condition;

import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ConditionIgnoreRNW extends ConditionAnd {

    public ConditionIgnoreRNW(ConditionAbstract other) {
        super(other);
        this.filter = this.filter.and(Predicate.not("is_random_new_world"::equalsIgnoreCase));

        if (other.getScopes() != null) {
            this.scopes = other.getScopes().stream().map(ConditionAbstract::ignoreRNW).collect(Collectors.toList());
            this.scopes.removeIf(ConditionAbstract::isEmpty);
        }
    }
}
