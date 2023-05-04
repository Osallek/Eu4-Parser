package fr.osallek.eu4parser.model.game;

import java.util.stream.Collectors;

public class ConditionDlcOnly extends ConditionAnd {

    public ConditionDlcOnly(ConditionAbstract other) {
        super(other);
        this.filter = this.filter.and("has_dlc"::equalsIgnoreCase);

        if (other.getScopes() != null) {
            this.scopes = other.getScopes().stream().map(ConditionAbstract::dlcOnly).collect(Collectors.toList());
            this.scopes.removeIf(ConditionAbstract::isEmpty);
        }
    }
}
