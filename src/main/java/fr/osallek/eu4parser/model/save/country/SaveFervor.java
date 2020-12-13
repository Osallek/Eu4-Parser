package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.Fervor;
import fr.osallek.eu4parser.model.game.Game;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SaveFervor {

    private final Game game;

    private final ClausewitzItem item;

    public SaveFervor(ClausewitzItem item, Game game) {
        this.game = game;
        this.item = item;
    }

    public Double getValue() {
        return this.item.getVarAsDouble("value");
    }

    public void setValue(Double value) {
        if (value < -100d) {
            value = -100d;
        } else if (value > 100d) {
            value = 100d;
        }

        this.item.setVariable("value", value);
    }

    public List<Fervor> getActives() {
        return this.item.getVarsAsStrings("active").stream().map(this.game::getFervor).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public void addActive(Fervor active) {
        List<String> ignoreDecisions = this.item.getVarsAsStrings("active");

        if (!ignoreDecisions.contains(active.getName())) {
            this.item.addVariable("active", active.getName());
        }
    }

    public void removeActive(int index) {
        this.item.removeVariable("active", index);
    }

    public void removeActive(Fervor active) {
        this.item.removeVariable("active", active.getName());
    }
}
