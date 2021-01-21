package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.common.NumbersUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public class GovernmentRank implements Comparable<GovernmentRank> {

    private final int level;

    private final Modifiers modifiers;

    public GovernmentRank(ClausewitzItem item) {
        this.level = NumbersUtils.toInt(item.getName());
        this.modifiers = new Modifiers(item);
    }

    public int getLevel() {
        return level;
    }

    public Modifiers getModifiers() {
        return modifiers;
    }

    @Override
    public int compareTo(@NotNull GovernmentRank o) {
        return Comparator.comparingInt(GovernmentRank::getLevel).compare(this, o);
    }
}
