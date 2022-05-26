package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;
import fr.osallek.eu4parser.model.game.RulerPersonality;
import fr.osallek.eu4parser.model.save.Save;

import java.util.List;

public record Personalities(ClausewitzItem item, Save save) {

    public List<RulerPersonality> getPersonalities() {
        return this.item.getVariables()
                        .stream()
                        .map(ClausewitzVariable::getName)
                        .map(s -> this.save.getGame().getRulerPersonality(s))
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
