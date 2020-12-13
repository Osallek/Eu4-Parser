package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.model.game.LeaderPersonality;
import fr.osallek.eu4parser.model.save.Save;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class History {

    private final Save save;

    private final Country country;

    private final ClausewitzItem item;

    private Map<Integer, Monarch> monarchs;

    private Map<Integer, Leader> leaders;

    private Map<Integer, Heir> heirs;

    private Map<Integer, Queen> queens;

    private SortedMap<LocalDate, String> changedTagFrom;

    public History(ClausewitzItem item, Save save, Country country) {
        this.save = save;
        this.item = item;
        this.country = country;
        refreshAttributes();
    }

    public Monarch getMonarch(int id) {
        return this.monarchs.get(id);
    }

    public Map<Integer, Monarch> getMonarchs() {
        return monarchs;
    }

    public Leader getLeader(int id) {
        return this.leaders.get(id);
    }

    public Map<Integer, Leader> getLeaders() {
        return leaders;
    }

    public Heir getHeir(int id) {
        return this.heirs.get(id);
    }

    public Map<Integer, Heir> getHeirs() {
        return heirs;
    }

    public Queen getQueen(int id) {
        return this.queens.get(id);
    }

    public Map<Integer, Queen> getQueens() {
        return queens;
    }

    public SortedMap<LocalDate, String> getChangedTagFrom() {
        return changedTagFrom;
    }

    public void setChangedTagFrom(SortedMap<LocalDate, String> changedTagFrom) {
        this.changedTagFrom = changedTagFrom;
    }

    public void addEvent(LocalDate date, String name, String value) {
        addEvents(date, Collections.singletonMap(name, value));
    }

    public void addEvents(LocalDate date, Map<String, String> variables) {
        ClausewitzItem child = this.item.addChild(ClausewitzUtils.dateToString(date));
        variables.forEach(child::addVariable);
    }

    public void addLeader(LocalDate date, LocalDate birthDate, String name, LeaderType type, int manuever, int fire, int shock, int siege,
                          LeaderPersonality personality, int id) {
        ClausewitzItem child = this.item.addChild(ClausewitzUtils.dateToString(date));
        Leader.addToItem(child, name, type, manuever, fire, shock, siege, personality, date, birthDate, id, this.country);
        refreshAttributes();
    }

    public void removeLeader(Leader leader) {
        this.item.getChildren()
                 .stream()
                 .filter(child -> child.hasChild("leader"))
                .filter(child -> new Leader(child.getChild("leader"), this.country).getId().equals(leader.getId()))
                .findFirst()
                .ifPresent(this.item::removeChild);
    }

    private void refreshAttributes() {
        this.monarchs = this.item.getChildren()
                                 .stream()
                                 .map(child -> {
                                     ClausewitzItem monarchItem = child.getChild("monarch");

                                     if (monarchItem == null) {
                                         monarchItem = child.getChild("monarch_heir");
                                     }

                                     if (monarchItem == null) {
                                         monarchItem = child.getChild("monarch_consort");
                                     }

                                     if (monarchItem == null) {
                                         monarchItem = child.getChild("monarch_foreign_heir");
                                     }

                                     return monarchItem;
                                 })
                                 .filter(Objects::nonNull)
                                 .map(child -> new Monarch(child, this.save, this.country, Eu4Utils.stringToDate(child.getName())))
                                 .collect(Collectors.toMap(monarch -> monarch.getId().getId(), Function.identity(), (monarch, monarch2) -> monarch2));

        this.leaders = this.item.getChildren()
                                .stream()
                                .map(child -> child.getChildren("leader"))
                                .flatMap(Collection::stream)
                                .filter(Objects::nonNull)
                                .map(item1 -> new Leader(item1, this.country))
                                .collect(Collectors.toMap(leader -> leader.getId().getId(), Function.identity(), (leader, leader2) -> leader2));

        this.heirs = this.item.getChildren()
                              .stream()
                              .map(child -> {
                                  ClausewitzItem monarchItem = child.getChild("heir");

                                  if (monarchItem == null) {
                                      monarchItem = child.getChild("foreign_heir");
                                  }

                                  return monarchItem;
                              })
                              .filter(Objects::nonNull)
                              .map(child -> new Heir(child, this.save, this.country))
                              .collect(Collectors.toMap(heir -> heir.getId().getId(), Function.identity(), (heir, heir2) -> heir2));

        this.queens = this.item.getChildren()
                               .stream()
                               .map(child -> child.getChild("queen"))
                               .filter(Objects::nonNull)
                               .map(child -> new Queen(child, this.save, this.country))
                               .collect(Collectors.toMap(queen -> queen.getId().getId(), Function.identity(), (queen, queen2) -> queen2));

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

        this.changedTagFrom = this.item.getChildren()
                                       .stream()
                                       .filter(child -> child.hasVar("changed_tag_from"))
                                       .collect(Collectors.toMap(child -> Eu4Utils.stringToDate(child.getName()),
                                                                 child -> ClausewitzUtils.removeQuotes(child.getVarAsString("changed_tag_from")),
                                                                 (a, b) -> b,
                                                                 TreeMap::new));
    }
}
