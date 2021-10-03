package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.common.NumbersUtils;

import java.util.Comparator;

public class GovernmentRank implements Comparable<GovernmentRank> {

    private final ClausewitzItem item;

    public GovernmentRank(ClausewitzItem item) {
        this.item = item;
    }

    public int getLevel() {
        return NumbersUtils.toInt(this.item.getName());
    }

    public void setLevel(int level) {
        this.item.setName(String.valueOf(level));
    }

    public Modifiers getModifiers() {
        return new Modifiers(this.item);
    }

    @Override
    public int compareTo(GovernmentRank o) {
        return Comparator.comparingInt(GovernmentRank::getLevel).compare(this, o);
    }
}
