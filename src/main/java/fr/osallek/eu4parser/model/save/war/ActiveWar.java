package fr.osallek.eu4parser.model.save.war;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.model.game.Battle;
import fr.osallek.eu4parser.model.game.WarGoal;
import fr.osallek.eu4parser.model.game.WarHistoryEvent;
import fr.osallek.eu4parser.model.save.Save;
import fr.osallek.eu4parser.model.save.country.Losses;
import fr.osallek.eu4parser.model.save.country.SaveCountry;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ActiveWar implements Comparable<ActiveWar> {

    protected final ClausewitzItem item;

    public ActiveWar(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return ClausewitzUtils.removeQuotes(this.item.getVarAsString("name"));
    }

    public void setName(String name) {
        this.item.setVariable("name", ClausewitzUtils.addQuotes(name));
    }

    public LocalDate getStartDate() {
        SortedMap<LocalDate, Map<WarHistoryAction, List<String>>> actions = getActionsHistory();
        return actions.isEmpty() ? null : actions.firstKey();
    }

    public SortedMap<LocalDate, Map<WarHistoryAction, List<String>>> getActionsHistory() {
        ClausewitzItem historyItem = this.item.getChild("history");
        SortedMap<LocalDate, Map<WarHistoryAction, List<String>>> actionsHistory = new TreeMap<>();

        if (historyItem != null) {
            historyItem.getChildrenNot("war_goal")
                       .stream()
                       .collect(Collectors.groupingBy(child -> Eu4Utils.stringToDate(child.getName())))
                       .forEach((key, value) -> {
                           Map<WarHistoryAction, List<String>> actions = new EnumMap<>(WarHistoryAction.class);

                           value.forEach(child -> {
                               for (WarHistoryAction action : WarHistoryAction.values()) {
                                   if (child.hasVar(action.name().toLowerCase())) {
                                       actions.merge(action,
                                                     Collections.singletonList(child.getVarAsString(action.name().toLowerCase())),
                                                     (strings, strings2) -> Stream.concat(strings.stream(), strings2.stream()).toList());
                                   }
                               }
                           });
                           if (!actions.isEmpty()) {
                               actionsHistory.put(key, actions);
                           }
                       });
        }

        return actionsHistory;
    }

    public SortedMap<LocalDate, List<Battle>> getBattles() {
        ClausewitzItem historyItem = this.item.getChild("history");

        if (historyItem != null) {
            return historyItem.getChildrenNot("war_goal")
                              .stream()
                              .filter(child -> ClausewitzUtils.DATE_PATTERN.matcher(ClausewitzUtils.removeQuotes(child.getName())).matches())
                              .filter(child -> child.hasChild("battle"))
                              .map(child -> new Battle(Eu4Utils.stringToDate(child.getName()), child.getChild("battle")))
                              .collect(Collectors.groupingBy(Battle::getDate, TreeMap::new, Collectors.toList()));
        }

        return null;
    }

    public Stream<WarParticipant> getParticipantsStream() {
        return this.item.getChildren("participants").stream().map(WarParticipant::new);
    }

    public boolean participate(SaveCountry country) {
        return getParticipantsStream().anyMatch(warParticipant -> warParticipant.getTag().equals(country.getTag()));
    }

    public Stream<String> getAttackersStream() {
        ClausewitzList list = this.item.getList("attackers");

        if (list != null) {
            return list.getStream();
        }

        return Stream.empty();
    }

    public WarParticipant getAttacker(SaveCountry attacker) {
        return getAttackers().get(attacker.getTag());
    }

    public Map<String, WarParticipant> getAttackers() {
        Map<String, WarParticipant> participants = getParticipantsStream().collect(Collectors.toMap(WarParticipant::getTag, Function.identity()));

        return getAttackersStream().map(tag -> Pair.of(tag, participants.get(tag)))
                                   .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
    }

    public Map<Losses, Integer> getAttackersLosses() {
        return getAttackers().values()
                             .stream()
                             .map(WarParticipant::getLosses)
                             .flatMap(m -> m.entrySet().stream())
                             .collect(Collectors.toMap(Map.Entry::getKey,
                                                       Map.Entry::getValue,
                                                       Integer::sum,
                                                       () -> new EnumMap<>(Losses.class)));
    }

    public Stream<String> getDefendersStream() {
        return this.item.getList("defenders").getValues().stream();
    }

    public WarParticipant getDefender(SaveCountry defender) {
        return getDefenders().get(defender.getTag());
    }

    public Map<String, WarParticipant> getDefenders() {
        Map<String, WarParticipant> participants = getParticipantsStream().collect(Collectors.toMap(WarParticipant::getTag, Function.identity()));

        return getDefendersStream().map(tag -> Pair.of(tag, participants.get(tag)))
                                   .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
    }

    public Map<Losses, Integer> getDefendersLosses() {
        return getDefenders().values()
                             .stream()
                             .map(WarParticipant::getLosses)
                             .flatMap(m -> m.entrySet().stream())
                             .collect(Collectors.toMap(Map.Entry::getKey,
                                                       Map.Entry::getValue,
                                                       Integer::sum,
                                                       () -> new EnumMap<>(Losses.class)));
    }

    public WarGoal getWarGoal() {
        for (WarGoalType warGoalType : WarGoalType.values()) {
            if (this.item.hasChild(warGoalType.name().toLowerCase())) {
                return new WarGoal(this.item.getChild(warGoalType.name().toLowerCase()));
            }
        }

        return null;
    }

    public Stream<String> getPersistentAttackersStream() {
        ClausewitzList list = this.item.getList("persistent_attackers");

        if (list != null) {
            return list.getStream();
        }

        return Stream.empty();
    }

    public Map<String, WarParticipant> getPersistentAttackers() {
        Map<String, WarParticipant> participants = getParticipantsStream().collect(Collectors.toMap(WarParticipant::getTag, Function.identity()));

        return getPersistentAttackersStream().map(tag -> Pair.of(tag, participants.get(tag)))
                                             .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
    }

    public Stream<String> getPersistentDefendersStream() {
        ClausewitzList list = this.item.getList("persistent_defenders");

        if (list != null) {
            return list.getStream();
        }

        return Stream.empty();
    }

    public Map<String, WarParticipant> getPersistentDefenders() {
        Map<String, WarParticipant> participants = getParticipantsStream().collect(Collectors.toMap(WarParticipant::getTag, Function.identity()));

        return getPersistentDefendersStream().map(tag -> Pair.of(tag, participants.get(tag)))
                                             .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
    }

    public String getOriginalAttacker() {
        return this.item.getVarAsString("original_attacker");
    }

    public void setOriginalAttacker(String tag) {
        this.item.setVariable("original_attacker", ClausewitzUtils.addQuotes(tag));
    }

    public String getOriginalDefender() {
        return this.item.getVarAsString("original_defender");
    }

    public void setOriginalDefender(String tag) {
        this.item.setVariable("original_defender", ClausewitzUtils.addQuotes(tag));
    }

    public LocalDate getAction() {
        return this.item.getVarAsDate("action");
    }

    public Double getDefenderScore() {
        return this.item.getVarAsDouble("defender_score");
    }

    public Double getAttackerScore() {
        return this.item.getVarAsDouble("attacker_score");
    }

    public Double getScore() {
        if (getDefenderScore() != null) {
            return -getDefenderScore();
        } else if (getAttackerScore() != null) {
            return getAttackerScore();
        }

        return null;
    }

    public Double getScore(SaveCountry country) {
        if (getAttacker(country) != null) {
            return -getDefenderScore();
        } else if (getDefender(country) != null) {
            return getDefenderScore();
        } else {
            return null;
        }
    }

    public Integer getWarDirectionQuarter() {
        return this.item.getVarAsInt("war_direction_quarter");
    }

    public Integer getWarDirectionYear() {
        return this.item.getVarAsInt("war_direction_year");
    }

    public Integer getLastWarScoreQuarter() {
        return this.item.getVarAsInt("last_warscore_quarter");
    }

    public Integer getLastWarScoreYear() {
        return this.item.getVarAsInt("last_warscore_year");
    }

    public Integer getStalledYears() {
        return this.item.getVarAsInt("stalled_years");
    }

    public boolean isCoalition() {
        return this.item.getVarAsBool("is_coalition");
    }

    public List<WarHistoryEvent> getEvents() {
        if (!this.item.hasChild("history")) {
            return new ArrayList<>();
        }

        return this.item.getChild("history")
                        .getChildren()
                        .stream()
                        .filter(child -> ClausewitzUtils.DATE_PATTERN.matcher(ClausewitzUtils.removeQuotes(child.getName())).matches())
                        .map(WarHistoryEvent::new)
                        .sorted(Comparator.comparing(WarHistoryEvent::getDate))
                        .collect(Collectors.toList());
    }

    public boolean isFinished() {
        return MapUtils.isEmpty(getAttackers()) || MapUtils.isEmpty(getDefenders());
    }

    public Map<String, WarParticipant> getSide(SaveCountry country) {
        Map<String, WarParticipant> map = getAttackers();
        if (map.containsKey(country.getTag())) {
            return map;
        } else if ((map = getDefenders()).containsKey(country.getTag())) {
            return map;
        } else {
            return new HashMap<>();
        }
    }

    public Map<String, WarParticipant> getOtherSide(SaveCountry country) {
        Map<String, WarParticipant> attackers = getAttackers();
        Map<String, WarParticipant> defenders = getDefenders();
        if (attackers.containsKey(country.getTag())) {
            return defenders;
        } else if (defenders.containsKey(country.getTag())) {
            return attackers;
        } else {
            return new HashMap<>();
        }
    }

    @Override
    public int compareTo(ActiveWar o) {
        return Comparator.comparing(ActiveWar::getStartDate).compare(this, o);
    }
}
