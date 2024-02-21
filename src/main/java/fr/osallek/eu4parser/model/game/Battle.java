package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.war.Combatant;
import org.apache.commons.lang3.BooleanUtils;

import java.time.LocalDate;

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
        return ClausewitzUtils.removeQuotes(this.item.getVarAsString("name"));
    }

    public void setName(String name) {
        this.item.setVariable("name", name);
    }

    public Integer getLocation() {
        return this.item.getVarAsInt("location");
    }

    public Combatant getAttacker() {
        ClausewitzItem child = this.item.getChild("attacker");

        return child == null ? null : new Combatant(child);
    }

    public Combatant getDefender() {
        ClausewitzItem child = this.item.getChild("defender");

        return child == null ? null : new Combatant(child);
    }

    public boolean getResult() {
        return BooleanUtils.toBoolean(this.item.getVarAsBool("result"));
    }

    public Double getWinnerAlliance() {
        return this.item.getVarAsDouble("winner_alliance");
    }

    public Double getLoserAlliance() {
        return this.item.getVarAsDouble("loser_alliance");
    }
}
