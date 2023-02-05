package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;

import java.util.List;
import java.util.Objects;

public record Personalities(ClausewitzItem item, Game game) {

    public List<RulerPersonality> getPersonalities() {
        return this.item.getVariables()
                        .stream()
                        .map(ClausewitzVariable::getName)
                        .map(this.game::getRulerPersonality)
                        .filter(Objects::nonNull)
                        .toList();
    }

    public List<String> getPersonalitiesName() {
        return this.item.getVariables()
                        .stream()
                        .map(ClausewitzVariable::getName)
                        .filter(Objects::nonNull)
                        .toList();
    }

    public void addPersonality(RulerPersonality personality) {
        this.item.addVariable(personality.getName(), true);
    }

    public void changePersonality(int index, String name) {
        this.item.setVariable(index, name);
    }

    public void removePersonality(int index) {
        this.item.removeVariable(index);
    }

    public void removePersonality(RulerPersonality personality) {
        this.item.removeVariable(personality.getName());
    }
}
