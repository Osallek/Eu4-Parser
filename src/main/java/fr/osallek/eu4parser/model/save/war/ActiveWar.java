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
import org.apache.commons.lang3.BooleanUtils;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ActiveWar implements Comparable<ActiveWar> {

    protected final Save save;

    protected final ClausewitzItem item;

    private Map<SaveCountry, WarParticipant> attackers;

    private Map<SaveCountry, WarParticipant> persistentAttackers;

    private Map<SaveCountry, WarParticipant> defenders;

    private Map<SaveCountry, WarParticipant> persistentDefenders;

    private WarGoal warGoal;

    private SortedMap<LocalDate, Map<WarHistoryAction, List<String>>> actionsHistory;

    private SortedMap<LocalDate, List<Battle>> battles;

    public ActiveWar(ClausewitzItem item, Save save) {
        this.save = save;
        this.item = item;
        refreshAttributes();
    }

    public String getName() {
        return this.item.getVarAsString("name").map(ClausewitzUtils::removeQuotes).orElse("");
    }

    public void setName(String name) {
        this.item.setVariable("name", ClausewitzUtils.addQuotes(name));
    }

    public LocalDate getStartDate() {
        return this.actionsHistory.firstKey();
    }

    public SortedMap<LocalDate, Map<WarHistoryAction, List<String>>> getActionsHistory() {
        return actionsHistory;
    }

    public SortedMap<LocalDate, List<Battle>> getBattles() {
        return battles;
    }

    public WarParticipant getAttacker(SaveCountry attacker) {
        if (this.attackers != null) {
            return this.attackers.get(attacker);
        }

        return null;
    }

    public Map<SaveCountry, WarParticipant> getAttackers() {
        return this.attackers == null ? new HashMap<>() : this.attackers;
    }

    public Map<Losses, Integer> getAttackersLosses() {
        if (this.attackers != null) {
            return this.attackers.values()
                                 .stream()
                                 .map(WarParticipant::getLosses)
                                 .filter(MapUtils::isNotEmpty)
                                 .flatMap(m -> m.entrySet().stream())
                                 .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Integer::sum, () -> new EnumMap<>(Losses.class)));
        }

        return new EnumMap<>(Losses.class);
    }

    public WarParticipant getDefender(SaveCountry defender) {
        if (this.defenders != null) {
            return this.defenders.get(defender);
        }

        return null;
    }

    public Map<SaveCountry, WarParticipant> getDefenders() {
        return this.defenders == null ? new HashMap<>() : this.defenders;
    }

    public Map<Losses, Integer> getDefendersLosses() {
        if (this.defenders != null) {
            return this.defenders.values()
                                 .stream()
                                 .map(WarParticipant::getLosses)
                                 .filter(MapUtils::isNotEmpty)
                                 .flatMap(m -> m.entrySet().stream())
                                 .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Integer::sum, () -> new EnumMap<>(Losses.class)));
        }

        return new EnumMap<>(Losses.class);
    }

    public WarGoal getWarGoal() {
        return warGoal;
    }

    public Map<SaveCountry, WarParticipant> getPersistentAttackers() {
        return this.persistentAttackers == null ? new HashMap<>() : this.persistentAttackers;
    }

    public Map<SaveCountry, WarParticipant> getPersistentDefenders() {
        return this.persistentDefenders == null ? new HashMap<>() : this.persistentDefenders;
    }

    public Optional<String> getOriginalAttacker() {
        return this.item.getVarAsString("original_attacker");
    }

    public void setOriginalAttacker(String tag) {
        this.item.setVariable("original_attacker", ClausewitzUtils.addQuotes(tag));
    }

    public Optional<String> getOriginalDefender() {
        return this.item.getVarAsString("original_defender");
    }

    public void setOriginalDefender(String tag) {
        this.item.setVariable("original_defender", ClausewitzUtils.addQuotes(tag));
    }

    public Optional<LocalDate> getAction() {
        return this.item.getVarAsDate("action");
    }

    public Optional<Double> getDefenderScore() {
        return this.item.getVarAsDouble("defender_score");
    }

    public Optional<Double> getAttackerScore() {
        return this.item.getVarAsDouble("attacker_score");
    }

    public Optional<Double> getScore() {
        return getDefenderScore().map(aDouble -> -aDouble).or(this::getAttackerScore);
    }

    public Optional<Double> getScore(SaveCountry country) {
        if (getAttacker(country) != null) {
            return getDefenderScore().map(aDouble -> -aDouble);
        } else if (getDefender(country) != null) {
            return getDefenderScore();
        } else {
            return Optional.empty();
        }
    }

    public Optional<Integer> getWarDirectionQuarter() {
        return this.item.getVarAsInt("war_direction_quarter");
    }

    public Optional<Integer> getWarDirectionYear() {
        return this.item.getVarAsInt("war_direction_year");
    }

    public Optional<Integer> getLastWarScoreQuarter() {
        return this.item.getVarAsInt("last_warscore_quarter");
    }

    public Optional<Integer> getLastWarScoreYear() {
        return this.item.getVarAsInt("last_warscore_year");
    }

    public Optional<Integer> getStalledYears() {
        return this.item.getVarAsInt("stalled_years");
    }

    public boolean isCoalition() {
        return this.item.getVarAsBool("is_coalition").map(BooleanUtils::toBoolean).orElse(false);
    }

    public List<WarHistoryEvent> getEvents() {
        return this.item.getChild("history")
                        .map(ClausewitzItem::getChildren)
                        .stream()
                        .flatMap(Collection::stream)
                        .filter(child -> ClausewitzUtils.DATE_PATTERN.matcher(ClausewitzUtils.removeQuotes(child.getName())).matches())
                        .map(WarHistoryEvent::new)
                        .sorted(Comparator.comparing(WarHistoryEvent::getDate))
                        .toList();
    }

    public boolean isFinished() {
        return MapUtils.isEmpty(this.attackers) || MapUtils.isEmpty(this.defenders);
    }

    public Map<SaveCountry, WarParticipant> getSide(SaveCountry country) {
        if (getAttacker(country) != null) {
            return this.attackers;
        } else if (getDefender(country) != null) {
            return this.defenders;
        } else {
            return new HashMap<>();
        }
    }

    public Map<SaveCountry, WarParticipant> getOtherSide(SaveCountry country) {
        if (getAttacker(country) != null) {
            return this.defenders;
        } else if (getDefender(country) != null) {
            return this.attackers;
        } else {
            return new HashMap<>();
        }
    }

    @Override
    public int compareTo(ActiveWar o) {
        return Comparator.comparing(ActiveWar::getStartDate).compare(this, o);
    }

    private void refreshAttributes() {
        this.item.getChild("history").ifPresent(historyItem -> {
            this.actionsHistory = new TreeMap<>();
            historyItem.getChildrenNot("war_goal")
                       .stream()
                       .collect(Collectors.groupingBy(child -> Eu4Utils.stringToDate(child.getName())))
                       .forEach((key, value) -> {
                           Map<WarHistoryAction, List<String>> actions = new EnumMap<>(WarHistoryAction.class);

                           value.forEach(child -> {
                               for (WarHistoryAction action : WarHistoryAction.values()) {
                                   if (child.hasVar(action.name().toLowerCase())) {
                                       actions.merge(action, List.of(child.getVarAsString(action.name().toLowerCase()).get()),
                                                     (strings, strings2) -> Stream.concat(strings.stream(), strings2.stream()).toList());
                                   }
                               }
                           });
                           if (!actions.isEmpty()) {
                               this.actionsHistory.put(key, actions);
                           }
                       });

            this.battles = historyItem.getChildrenNot("war_goal")
                                      .stream()
                                      .filter(child -> ClausewitzUtils.DATE_PATTERN.matcher(ClausewitzUtils.removeQuotes(child.getName())).matches())
                                      .filter(child -> child.hasChild("battle"))
                                      .map(child -> new Battle(Eu4Utils.stringToDate(child.getName()), child.getChild("battle").get()))
                                      .collect(Collectors.groupingBy(Battle::getDate, TreeMap::new, Collectors.toList()));
        });

        for (WarGoalType warGoalType : WarGoalType.values()) {
            Optional<ClausewitzItem> child = this.item.getChild(warGoalType.name().toLowerCase());
            if (child.isPresent()) {
                this.warGoal = new WarGoal(child.get(), this.save.getGame());
                break;
            }
        }

        List<ClausewitzItem> participantsItems = this.item.getChildren("participants");
        Optional<ClausewitzList> attackersList = this.item.getList("attackers");
        Optional<ClausewitzList> defendersList = this.item.getList("defenders");

        if (attackersList.isPresent() && defendersList.isPresent()) {
            this.attackers = new LinkedHashMap<>();
            this.defenders = new LinkedHashMap<>();
            participantsItems.forEach(participantsItem -> {
                WarParticipant warParticipant = new WarParticipant(participantsItem);
                warParticipant.getTag().map(this.save::getCountry).ifPresent(country -> {
                    country.addWar(this);

                    if (attackersList.get().getValues().stream().map(ClausewitzUtils::removeQuotes).anyMatch(s -> s.equals(country.getTag()))) {
                        this.attackers.put(country, warParticipant);
                    } else if (defendersList.get().getValues().stream().map(ClausewitzUtils::removeQuotes).anyMatch(s -> s.equals(country.getTag()))) {
                        this.defenders.put(country, warParticipant);
                    }
                });
            });
        }

        Optional<ClausewitzList> persistentAttackersList = this.item.getList("persistent_attackers");
        Optional<ClausewitzList> persistentDefendersList = this.item.getList("persistent_defenders");

        if (persistentAttackersList.isPresent() && persistentDefendersList.isPresent()) {
            this.persistentAttackers = new LinkedHashMap<>();
            this.persistentDefenders = new LinkedHashMap<>();
            participantsItems.forEach(participantsItem -> {
                WarParticipant warParticipant = new WarParticipant(participantsItem);
                warParticipant.getTag().map(this.save::getCountry).ifPresent(country -> {
                    country.addWar(this);

                    if (persistentAttackersList.get().getValues().stream().map(ClausewitzUtils::removeQuotes).anyMatch(s -> s.equals(country.getTag()))) {
                        this.persistentAttackers.put(country, warParticipant);
                    } else if (persistentDefendersList.get()
                                                      .getValues()
                                                      .stream()
                                                      .map(ClausewitzUtils::removeQuotes)
                                                      .anyMatch(s -> s.equals(country.getTag()))) {
                        this.persistentDefenders.put(country, warParticipant);
                    }
                });
            });
        }
    }
}
