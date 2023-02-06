package fr.osallek.eu4parser.model.game;

import java.util.List;
import java.util.Map;

public interface ConditionI {

    String getName();

    Map<String, List<String>> getConditions();
}
