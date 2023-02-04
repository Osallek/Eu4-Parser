package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.common.Eu4Utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class War extends Nodded {

    private final ClausewitzItem item;

    private final Game game;

    public War(FileNode fileNode, ClausewitzItem item, Game game) {
        super(fileNode);
        this.item = item;
        this.game = game;
    }

    @Override
    public String getName() {
        return ClausewitzUtils.removeQuotes(this.item.getVarAsString("name"));
    }

    @Override
    public void write(BufferedWriter writer) throws IOException {
        this.item.write(writer, true, 0, new HashMap<>());
    }

    public void setName(String name) {
        this.item.setVariable("name", ClausewitzUtils.addQuotes(name));
    }

    public WarGoal getWarGoal() {
        return new WarGoal(this.item.getChild("war_goal"), this.game);
    }

    public LocalDate getStart() {
        return getEvents().first().getDate();
    }

    public LocalDate getEnd() {
        return getEvents().last().getDate();
    }

    public SortedSet<WarHistoryEvent> getEvents() {
        return getEventsAt(null);
    }

    public SortedSet<WarHistoryEvent> getEventsAt(LocalDate date) {
        return this.item.getChildren()
                        .stream()
                        .filter(child -> ClausewitzUtils.DATE_PATTERN.matcher(ClausewitzUtils.removeQuotes(child.getName())).matches())
                        .filter(child -> date == null || date.isAfter(Eu4Utils.stringToDate(child.getName()))
                                         || date.equals(Eu4Utils.stringToDate(child.getName())))
                        .map(WarHistoryEvent::new)
                        .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(WarHistoryEvent::getDate))));
    }

    public List<String> getAttackersAt(LocalDate date) {
        List<String> attackers = new ArrayList<>();

        for (WarHistoryEvent event : getEventsAt(date)) {
            attackers.addAll(event.getAddAttacker());
            attackers.removeAll(event.getRemAttacker());
        }

        return attackers;
    }

    public List<String> getDefendersAt(LocalDate date) {
        List<String> defenders = new ArrayList<>();

        for (WarHistoryEvent event : getEventsAt(date)) {
            defenders.addAll(event.getAddDefender());
            defenders.removeAll(event.getRemDefender());
        }

        return defenders;
    }

    public boolean inOtherSideAt(LocalDate date, Country country, Country other) {
        return (getAttackersAt(date).contains(country.getTag()) && getDefendersAt(date).contains(other.getTag())) ||
               (getDefendersAt(date).contains(country.getTag()) && getAttackersAt(date).contains(other.getTag()));
    }

    public Game getGame() {
        return game;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof War war)) {
            return false;
        }

        return Objects.equals(getName(), war.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return getName();
    }
}
