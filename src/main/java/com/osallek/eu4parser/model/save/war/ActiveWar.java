package com.osallek.eu4parser.model.save.war;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.eu4parser.common.Eu4Utils;
import com.osallek.eu4parser.model.save.country.Losses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ActiveWar {

    protected final ClausewitzItem item;

    private final Map<String, WarParticipant> attackers = new HashMap<>();

    private final Map<String, WarParticipant> defenders = new HashMap<>();

    private SortedMap<Date, Map<WarHistoryAction, List<String>>> actionsHistory;

    private SortedMap<Date, List<Battle>> battles;

    public ActiveWar(ClausewitzItem item) {
        this.item = item;
        refreshAttributes();
    }

    public String getName() {
        return this.item.getVarAsString("name");
    }

    public void setName(String name) {
        this.item.setVariable("name", name);
    }

    public SortedMap<Date, Map<WarHistoryAction, List<String>>> getActionsHistory() {
        return actionsHistory;
    }

    public SortedMap<Date, List<Battle>> getBattles() {
        return battles;
    }

    public WarParticipant getAttacker(String attacker) {
        return this.attackers.get(attacker);
    }

    public Map<String, WarParticipant> getAttackers() {
        return attackers;
    }

    public Map<Losses, Integer> getAttackersLosses() {
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

    public WarParticipant getDefender(String defender) {
        return this.defenders.get(defender);
    }

    public Map<String, WarParticipant> getDefenders() {
        return defenders;
    }

    public Map<Losses, Integer> getDefendersLosses() {
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

    public Date getAction() {
        return this.item.getVarAsDate("action");
    }

    public Double getDefenderScore() {
        return this.item.getVarAsDouble("defender_score");
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

    private void refreshAttributes() {
        List<ClausewitzItem> participantsItems = this.item.getChildren("participants");
        ClausewitzList attackersList = this.item.getList("attackers");
        ClausewitzList defendersList = this.item.getList("defenders");

        if (attackersList != null && defendersList != null) {
            participantsItems.forEach(participantsItem -> {
                WarParticipant warParticipant = new WarParticipant(participantsItem);

                if (attackersList.getValues().contains(ClausewitzUtils.removeQuotes(warParticipant.getTag()))) {
                    this.attackers.put(ClausewitzUtils.removeQuotes(warParticipant.getTag()), warParticipant);
                } else if (defendersList.getValues().contains(ClausewitzUtils.removeQuotes(warParticipant.getTag()))) {
                    this.defenders.put(ClausewitzUtils.removeQuotes(warParticipant.getTag()), warParticipant);
                }
            });
        }

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
                                   if (child.getVar(action.name().toLowerCase()) != null) {
                                       actions.merge(action,
                                                     Collections.singletonList(child.getVarAsString(action.name()
                                                                                                          .toLowerCase())),
                                                     (strings, strings2) -> Stream.concat(strings.stream(), strings2.stream())
                                                                                  .collect(Collectors.toList()));
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
                                                                (a, b) -> Stream.concat(a.stream(), b.stream())
                                                                                .collect(Collectors.toList()),
                                                                TreeMap::new));
        }
    }
}
