package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.model.game.LeaderPersonality;
import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SaveCountryHistory extends SaveCountryHistoryEvent {

    public SaveCountryHistory(ClausewitzItem item, SaveCountry country) {
        super(item, country);
    }

    @Override
    public LocalDate getDate() {
        return this.country.getSave().getStartDate();
    }

    public boolean hasEvents() {
        return this.item.getChildren()
                        .stream()
                        .anyMatch(child -> ClausewitzUtils.DATE_PATTERN.matcher(ClausewitzUtils.removeQuotes(child.getName())).matches());
    }

    public boolean hasEventAfter(LocalDate date) {
        return hasEvents() && this.item.getChildren()
                                       .stream()
                                       .filter(child -> ClausewitzUtils.DATE_PATTERN.matcher(ClausewitzUtils.removeQuotes(child.getName())).matches())
                                       .map(child -> Eu4Utils.stringToDate(child.getName()))
                                       .filter(Objects::nonNull)
                                       .anyMatch(date::isBefore);
    }

    public Stream<SaveCountryHistoryEvent> getEventsAfter(LocalDate date) {
        return this.item.getChildren()
                        .reversed()
                        .stream()
                        .filter(child -> ClausewitzUtils.DATE_PATTERN.matcher(child.getName()).matches())
                        .map(child -> new SaveCountryHistoryEvent(child, this.country))
                        .filter(event -> event.getDate().isAfter(date));
    }

    public List<SaveCountryHistoryEvent> getEvents() {
        return this.item.getChildren()
                        .stream()
                        .filter(child -> ClausewitzUtils.DATE_PATTERN.matcher(ClausewitzUtils.removeQuotes(child.getName())).matches())
                        .map(child -> new SaveCountryHistoryEvent(child, this.country))
                        .collect(Collectors.toList());
    }

    public Monarch getMonarch(int id) {
        return getMonarchs().get(id);
    }

    public Map<Integer, Monarch> getMonarchs() {
        return this.item.getChildren()
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

                            return monarchItem == null ? null : Pair.of(monarchItem, Eu4Utils.stringToDate(child.getName()));
                        })
                        .filter(Objects::nonNull)
                        .map(child -> new Monarch(child.getKey(), this.country, child.getValue()))
                        .collect(Collectors.toMap(monarch -> monarch.getId().getId(), Function.identity(), (monarch, monarch2) -> monarch2));
    }

    public Leader getLeader(int id) {
        return getLeaders().get(id);
    }

    public Map<Integer, Leader> getLeaders() {
        Map<Integer, Leader> leaders = this.item.getChildren()
                                                .stream()
                                                .map(child -> child.getChildren("leader"))
                                                .flatMap(Collection::stream)
                                                .filter(Objects::nonNull)
                                                .map(item1 -> new Leader(item1, this.country))
                                                .collect(Collectors.toMap(leader -> leader.getId().getId(), Function.identity(), (leader, leader2) -> leader2));

        leaders.putAll(getMonarchs().values()
                                    .stream()
                                    .filter(monarch -> monarch.getLeader() != null)
                                    .collect(Collectors.toMap(monarch -> monarch.getLeader().getId().getId(), Monarch::getLeader)));

        leaders.putAll(getHeirs().values()
                                 .stream()
                                 .filter(heir -> heir.getLeader() != null)
                                 .collect(Collectors.toMap(heir -> heir.getLeader().getId().getId(), Heir::getLeader)));

        return leaders;
    }

    public Heir getHeir(int id) {
        return getHeirs().get(id);
    }

    public Map<Integer, Heir> getHeirs() {
        return this.item.getChildren()
                        .stream()
                        .map(child -> {
                            ClausewitzItem monarchItem = child.getChild("heir");

                            if (monarchItem == null) {
                                monarchItem = child.getChild("foreign_heir");
                            }

                            return monarchItem == null ? null : Pair.of(monarchItem, Eu4Utils.stringToDate(child.getName()));
                        })
                        .filter(Objects::nonNull)
                        .map(child -> new Heir(child.getKey(), this.country, child.getValue()))
                        .collect(Collectors.toMap(heir -> heir.getId().getId(), Function.identity(), (heir, heir2) -> heir2));
    }

    public Queen getQueen(int id) {
        return getQueens().get(id);
    }

    public Map<Integer, Queen> getQueens() {
        return this.item.getChildren()
                        .stream()
                        .map(child -> {
                            ClausewitzItem monarchItem = child.getChild("queen");

                            return monarchItem == null ? null : Pair.of(monarchItem, Eu4Utils.stringToDate(child.getName()));
                        })
                        .filter(Objects::nonNull)
                        .map(child -> new Queen(child.getKey(), this.country, child.getValue()))
                        .collect(Collectors.toMap(queen -> queen.getId().getId(), Function.identity(), (queen, queen2) -> queen2));
    }

    public SortedMap<LocalDate, List<String>> getChangedTagsFrom() {
        return this.item.getChildren()
                        .stream()
                        .filter(child -> child.hasVar("changed_tag_from"))
                        .collect(Collectors.groupingBy(child -> Eu4Utils.stringToDate(child.getName()),
                                                       TreeMap::new,
                                                       Collectors.mapping(
                                                               child -> ClausewitzUtils.removeQuotes(child.getVarAsString("changed_tag_from")),
                                                               Collectors.toList())));
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
    }

    public void removeLeader(Leader leader) {
        this.item.getChildren()
                 .stream()
                 .filter(child -> child.hasChild("leader"))
                 .filter(child -> new Leader(child.getChild("leader"), this.country).getId().equals(leader.getId()))
                 .findFirst()
                 .ifPresent(this.item::removeChild);
    }
}
