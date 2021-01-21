package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;
import fr.osallek.eu4parser.model.game.RulerPersonality;
import fr.osallek.eu4parser.model.save.Save;

import java.util.List;
import java.util.stream.Collectors;

public class Personalities {

    private final Save save;

    final ClausewitzItem item;

    public Personalities(ClausewitzItem item, Save save) {
        this.save = save;
        this.item = item;
    }

    public List<RulerPersonality> getPersonalities() {
        return this.item.getVariables()
                        .stream()
                        .map(ClausewitzVariable::getName)
                        .map(s -> this.save.getGame().getRulerPersonality(s))
                        .collect(Collectors.toList());
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
