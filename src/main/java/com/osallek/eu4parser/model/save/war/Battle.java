package com.osallek.eu4parser.model.save.war;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import org.apache.commons.lang3.BooleanUtils;

public class Battle {

    private final ClausewitzItem item;

    private Combatant attacker;

    private Combatant defender;

    public Battle(ClausewitzItem item) {
        this.item = item;
        refreshAttributes();
    }

    public String getName() {
        return this.item.getVarAsString("name");
    }

    public void setName(String name) {
        this.item.setVariable("name", name);
    }

    public Integer getLocation() {
        return this.item.getVarAsInt("location");
    }

    public Combatant getAttacker() {
        return attacker;
    }

    public Combatant getDefender() {
        return defender;
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

    private void refreshAttributes() {
        ClausewitzItem attackerItem = this.item.getChild("attacker");

        if (attackerItem != null) {
            this.attacker = new Combatant(attackerItem);
        }

        ClausewitzItem defenderItem = this.item.getChild("defender");

        if (defenderItem != null) {
            this.defender = new Combatant(defenderItem);
        }
    }
}
