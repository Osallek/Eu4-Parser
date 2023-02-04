package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;
import fr.osallek.eu4parser.model.game.Game;
import fr.osallek.eu4parser.model.game.RulerPersonality;

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

    void addPersonality(RulerPersonality personality) {
        this.item.addVariable(personality.getName(), true);
    }

    void changePersonality(int index, String name) {
        this.item.setVariable(index, name);
    }

    void removePersonality(int index) {
        this.item.removeVariable(index);
    }

    void removePersonality(RulerPersonality personality) {
        this.item.removeVariable(personality.getName());
    }
}
