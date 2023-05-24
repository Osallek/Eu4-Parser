package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.war.Combatant;
import org.apache.commons.lang3.BooleanUtils;

import java.time.LocalDate;
import java.util.Optional;

public class Battle {

    private final ClausewitzItem item;

    private final LocalDate date;

    public Battle(LocalDate date, ClausewitzItem item) {
        this.item = item;
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getName() {
        return this.item.getVarAsString("name").map(ClausewitzUtils::removeQuotes).orElse("");
    }

    public void setName(String name) {
        this.item.setVariable("name", name);
    }

    public int getLocation() {
        return this.item.getVarAsInt("location").orElse(0);
    }

    public Optional<Combatant> getAttacker() {
        return this.item.getChild("attacker").map(Combatant::new);
    }

    public Optional<Combatant> getDefender() {
        return this.item.getChild("defender").map(Combatant::new);
    }

    public boolean getResult() {
        return this.item.getVarAsBool("result").map(BooleanUtils::toBoolean).orElse(false);
    }

    public Optional<Double> getWinnerAlliance() {
        return this.item.getVarAsDouble("winner_alliance");
    }

    public Optional<Double> getLoserAlliance() {
        return this.item.getVarAsDouble("loser_alliance");
    }
}
