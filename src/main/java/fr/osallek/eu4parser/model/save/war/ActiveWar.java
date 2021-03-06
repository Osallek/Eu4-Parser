package fr.osallek.eu4parser.model.save.war;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.model.save.Save;
import fr.osallek.eu4parser.model.save.country.Country;
import fr.osallek.eu4parser.model.save.country.Losses;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ActiveWar implements Comparable<ActiveWar> {

    protected final Save save;

    protected final ClausewitzItem item;

    private Map<Country, WarParticipant> attackers;

    private Map<Country, WarParticipant> defenders;

    private WarGoal warGoal;

    private SortedMap<LocalDate, Map<WarHistoryAction, List<String>>> actionsHistory;

    private SortedMap<LocalDate, List<Battle>> battles;

    public ActiveWar(ClausewitzItem item, Save save) {
        this.save = save;
        this.item = item;
        refreshAttributes();
    }

    public String getName() {
        return this.item.getVarAsString("name");
    }

    public void setName(String name) {
        this.item.setVariable("name", name);
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

    public WarParticipant getAttacker(Country attacker) {
        if (this.attackers != null) {
            return this.attackers.get(attacker);
        }

        return null;
    }

    public Map<Country, WarParticipant> getAttackers() {
        return this.attackers == null ? new HashMap<>() : this.attackers;
    }

    public Map<Losses, Integer> getAttackersLosses() {
        if (this.attackers != null) {
            return this.attackers.values()
                                 .stream()
                                 .map(WarParticipant::getLosses)
                                 .filter(Objects::nonNull)
                                 .flatMap(m -> m.entrySet().stream())
                                 .collect(Collectors.toMap(Map.Entry::getKey,
                                                           Map.Entry::getValue,
                                                           Integer::sum,
                                                           () -> new EnumMap<>(Losses.class)));
        }

        return new EnumMap<>(Losses.class);
    }

    public WarParticipant getDefender(Country defender) {
        if (this.defenders != null) {
            return this.defenders.get(defender);
        }

        return null;
    }

    public Map<Country, WarParticipant> getDefenders() {
        return this.defenders == null ? new HashMap<>() : this.defenders;
    }

    public Map<Losses, Integer> getDefendersLosses() {
        if (this.defenders != null) {
            return this.defenders.values()
                                 .stream()
                                 .map(WarParticipant::getLosses)
                                 .filter(Objects::nonNull)
                                 .flatMap(m -> m.entrySet().stream())
                                 .collect(Collectors.toMap(Map.Entry::getKey,
                                                           Map.Entry::getValue,
                                                           Integer::sum,
                                                           () -> new EnumMap<>(Losses.class)));
        }

        return new EnumMap<>(Losses.class);
    }

    public WarGoal getWarGoal() {
        return warGoal;
    }

    public List<String> getPersistentAttackers() {
        ClausewitzList list = this.item.getList("persistent_attackers");

        if (list != null) {
            return list.getValues();
        }

        return new ArrayList<>();
    }

    public List<String> getPersistentDefenders() {
        ClausewitzList list = this.item.getList("persistent_defenders");

        if (list != null) {
            return list.getValues();
        }

        return new ArrayList<>();
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

    public Double getScore(Country country) {
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

    public boolean isFinished() {
        return false;
    }

    public Map<Country, WarParticipant> getSide(Country country) {
        if (getAttacker(country) != null) {
            return this.attackers;
        } else if (getDefender(country) != null) {
            return this.defenders;
        } else {
            return new HashMap<>();
        }
    }

    public Map<Country, WarParticipant> getOtherSide(Country country) {
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
        ClausewitzItem historyItem = this.item.getChild("history");

        if (historyItem != null) {
            this.actionsHistory = new TreeMap<>();
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
                                                     (strings, strings2) -> Stream.concat(strings.stream(), strings2.stream()).collect(Collectors.toList()));
                                   }
                               }
                           });
                           if (!actions.isEmpty()) {
                               this.actionsHistory.put(key, actions);
                           }
                       });

            this.battles = historyItem.getChildrenNot("war_goal")
                                      .stream()
                                      .filter(child -> child.getChild("battle") != null)
                                      .collect(Collectors.toMap(child -> Eu4Utils.stringToDate(child.getName()),
                                                                child -> Collections.singletonList(new Battle(child.getChild("battle"))),
                                                                (a, b) -> Stream.concat(a.stream(), b.stream()).collect(Collectors.toList()),
                                                                TreeMap::new));
        }

        ClausewitzItem warGoalChild = this.item.getChild("superiority");

        if (warGoalChild == null) {
            warGoalChild = this.item.getChild("take_province");
        }

        if (warGoalChild == null) {
            warGoalChild = this.item.getChild("blockade_ports");
        }

        this.warGoal = new WarGoal(warGoalChild, this.save);

        List<ClausewitzItem> participantsItems = this.item.getChildren("participants");
        ClausewitzList attackersList = this.item.getList("attackers");
        ClausewitzList defendersList = this.item.getList("defenders");

        if (attackersList != null && defendersList != null) {
            this.attackers = new HashMap<>();
            this.defenders = new HashMap<>();
            participantsItems.forEach(participantsItem -> {
                WarParticipant warParticipant = new WarParticipant(participantsItem);
                Country country = this.save.getCountry(ClausewitzUtils.removeQuotes(warParticipant.getTag()));
                country.addWar(this);

                if (attackersList.getValues().contains(ClausewitzUtils.removeQuotes(warParticipant.getTag()))) {
                    this.attackers.put(country, warParticipant);
                } else if (defendersList.getValues().contains(ClausewitzUtils.removeQuotes(warParticipant.getTag()))) {
                    this.defenders.put(country, warParticipant);
                }
            });
        }
    }
}
