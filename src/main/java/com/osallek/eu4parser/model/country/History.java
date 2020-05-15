package com.osallek.eu4parser.model.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.counters.Counter;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class History {

    private final ClausewitzItem item;

    private final Country country;

    private Map<Long, Monarch> monarchs;

    private Map<Long, Leader> leaders;

    private Map<Long, Heir> heirs;

    private Map<Long, Queen> queens;

    public History(ClausewitzItem item, Country country) {
        this.item = item;
        this.country = country;
        refreshAttributes();
    }

    public Monarch getMonarch(long id) {
        return this.monarchs.get(id);
    }

    public Map<Long, Monarch> getMonarchs() {
        return monarchs;
    }

    public Leader getLeader(long id) {
        return this.leaders.get(id);
    }

    public Map<Long, Leader> getLeaders() {
        return leaders;
    }

    public Heir getHeir(long id) {
        return this.heirs.get(id);
    }

    public Map<Long, Heir> getHeirs() {
        return heirs;
    }

    public Queen getQueen(long id) {
        return this.queens.get(id);
    }

    public Map<Long, Queen> getQueens() {
        return queens;
    }

    public void addEvent(Date date, String name, String value) {
        addEvents(date, Collections.singletonMap(name, value));
    }

    public void addEvents(Date date, Map<String, String> variables) {
        ClausewitzItem child = this.item.addChild(ClausewitzUtils.dateToString(date));
        variables.forEach(child::addVariable);
    }

    public void addLeader(Date date, String name, LeaderType type, int manuever, int fire, int shock, int siege, String personality, long id) {
        ClausewitzItem child = this.item.addChild(ClausewitzUtils.dateToString(date));
        Leader.addToItem(child, name, type, manuever, fire, shock, siege, personality, date, id);
        refreshAttributes();
    }

    private void refreshAttributes() {
        this.monarchs = this.item.getChildren()
                                 .stream()
                                 .map(child -> {
                                     ClausewitzItem monarchItem = child.getChild("monarch");

                                     if (monarchItem == null) {
                                         monarchItem = child.getChild("monarch_heir");
                                     }

                                     return monarchItem;
                                 })
                                 .filter(Objects::nonNull)
                                 .map(Monarch::new)
                                 .collect(Collectors.toMap(monarch -> monarch.getId().getId(), Function.identity()));

        this.leaders = this.item.getChildren()
                                .stream()
                                .map(child -> child.getChildren("leader"))
                                .flatMap(Collection::stream)
                                .filter(Objects::nonNull)
                                .map(Leader::new)
                                .collect(Collectors.toMap(leader -> leader.getId().getId(), Function.identity()));

        this.heirs = this.item.getChildren()
                              .stream()
                              .map(child -> child.getChild("heir"))
                              .filter(Objects::nonNull)
                              .map(Heir::new)
                              .collect(Collectors.toMap(heir -> heir.getId().getId(), Function.identity()));

        this.queens = this.item.getChildren()
                               .stream()
                               .map(child -> child.getChild("queen"))
                               .filter(Objects::nonNull)
                               .map(Queen::new)
                               .collect(Collectors.toMap(queen -> queen.getId().getId(), Function.identity()));

        this.leaders.putAll(this.monarchs.values()
                                         .stream()
                                         .filter(monarch -> monarch.getLeader() != null)
                                         .collect(Collectors.toMap(monarch -> monarch.getLeader().getId().getId(),
                                                                   Monarch::getLeader)));

        this.leaders.putAll(this.heirs.values()
                                      .stream()
                                      .filter(heir -> heir.getLeader() != null)
                                      .collect(Collectors.toMap(heir -> heir.getLeader().getId().getId(),
                                                                Heir::getLeader)));
    }
}
