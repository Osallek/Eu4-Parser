package fr.osallek.eu4parser.model.save.province;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.common.NumbersUtils;
import fr.osallek.eu4parser.model.game.Culture;
import fr.osallek.eu4parser.model.save.SaveReligion;
import fr.osallek.eu4parser.model.save.country.SaveCountry;
import fr.osallek.eu4parser.model.save.country.SaveCountryHistoryEvent;
import org.apache.commons.collections4.MapUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SaveProvinceHistory extends SaveProvinceHistoryEvent {

    private final ClausewitzItem item;

    private final SaveProvince province;

    public SaveProvinceHistory(ClausewitzItem item, SaveProvince province) {
        super(item, province);
        this.item = item;
        this.province = province;
    }

    @Override
    public LocalDate getDate() {
        return this.province.getSave().getStartDate();
    }

    public Boolean getSeatInParliament() {
        return this.item.getLastVarAsBool("seat_in_parliament");
    }

    public Double getExtraCost() {
        return this.item.getLastVarAsDouble("extra_cost");
    }

    public Integer getFormerNativeSize() {
        return NumbersUtils.doubleToInt(this.item.getLastVarAsDouble("former_native_size"));
    }

    public List<SaveProvinceHistoryEvent> getEvents() {
        return this.item.getChildren()
                        .stream()
                        .filter(child -> ClausewitzUtils.DATE_PATTERN.matcher(ClausewitzUtils.removeQuotes(child.getName())).matches())
                        .map(child -> new SaveProvinceHistoryEvent(child, this.province))
                        .collect(Collectors.toList());
    }

    public Stream<SaveProvinceHistoryEvent> getEventsAfter(LocalDate date) {
        return this.item.getChildren()
                        .reversed()
                        .stream()
                        .filter(child -> ClausewitzUtils.DATE_PATTERN.matcher(child.getName()).matches())
                        .map(child -> new SaveProvinceHistoryEvent(child, this.province))
                        .filter(event -> event.getDate().isAfter(date));
    }

    public SortedMap<LocalDate, SaveCountry> getOwners() {
        SortedMap<LocalDate, SaveCountry> owners = this.item.getChildren()
                                                            .stream()
                                                            .filter(child -> child.hasVar("owner"))
                                                            .collect(Collectors.toMap(child -> Eu4Utils.stringToDate(child.getName()),
                                                                                      child -> this.province.getSave()
                                                                                                            .getCountry(child.getVarAsString("owner")),
                                                                                      (a, b) -> b,
                                                                                      TreeMap::new));
        if (MapUtils.isNotEmpty(owners) && this.province.getOwner() != null && !owners.containsValue(this.province.getOwner())) {
            if (MapUtils.isNotEmpty(this.province.getOwner().getHistory().getChangedTagsFrom())) {
                owners.put(this.province.getOwner().getHistory().getChangedTagsFrom().lastKey(), this.province.getOwner());
            } else {
                owners.put(this.province.getSave().getDate(), this.province.getOwner());
            }
        }

        return owners;
    }

    public SortedMap<LocalDate, List<SaveCountry>> getClaims() {
        return this.item.getChildren()
                        .stream()
                        .filter(child -> child.hasVar("add_claim"))
                        .collect(Collectors.groupingBy(child -> Eu4Utils.stringToDate(child.getName()),
                                                       TreeMap::new,
                                                       Collectors.mapping(child -> this.province.getSave().getCountry(child.getVarAsString("add_claim")),
                                                                          Collectors.toList())));
    }

    public SortedMap<LocalDate, SaveCountry> getControllers() {
        return this.item.getChildren()
                        .stream()
                        .filter(child -> child.hasChild("controller"))
                        .filter(child -> child.getChild("controller").hasVar("tag"))
                        .collect(Collectors.toMap(child -> Eu4Utils.stringToDate(child.getName()),
                                                  child -> this.province.getSave().getCountry(child.getChild("controller").getVarAsString("tag")),
                                                  (a, b) -> a, //Take a because only happens when changing tag
                                                  TreeMap::new));
    }

    public SortedMap<LocalDate, SaveReligion> getReligions() {
        SortedMap<LocalDate, SaveReligion> religions = this.item.getChildrenNot("advisor")
                                                                .stream()
                                                                .filter(child -> child.hasVar("religion"))
                                                                .collect(Collectors.toMap(child -> Eu4Utils.stringToDate(child.getName()),
                                                                                          child -> this.province.getSave()
                                                                                                                .getReligions()
                                                                                                                .getReligion(child.getVarAsString("religion")),
                                                                                          (a, b) -> b,
                                                                                          TreeMap::new));

        religions.put(this.province.getSave().getStartDate(), this.province.getSave().getReligions().getReligion(this.item.getVarAsString("religion")));

        return religions;
    }

    public SortedMap<LocalDate, Culture> getCultures() {
        SortedMap<LocalDate, Culture> cultures = this.item.getChildrenNot("advisor")
                                                          .stream()
                                                          .filter(child -> child.hasVar("culture"))
                                                          .filter(child -> this.province.getSave().getGame().getCulture(child.getVarAsString("culture"))
                                                                           != null)
                                                          .collect(Collectors.toMap(child -> Eu4Utils.stringToDate(child.getName()),
                                                                                    child -> this.province.getSave()
                                                                                                          .getGame()
                                                                                                          .getCulture(child.getVarAsString("culture")),
                                                                                    (a, b) -> b,
                                                                                    TreeMap::new));

        if (this.province.getSave().getGame().getCulture(this.item.getVarAsString("culture")) != null) {
            cultures.put(this.province.getSave().getStartDate(), this.province.getSave().getGame().getCulture(this.item.getVarAsString("culture")));
        }

        return cultures;
    }

    public Map<Integer, SaveAdvisor> getAdvisors() {
        Map<Integer, SaveAdvisor> advisors = this.item.getChildren()
                                                      .stream()
                                                      .map(child -> child.getChild("advisor"))
                                                      .filter(Objects::nonNull)
                                                      .map(child -> new SaveAdvisor(child, this.province.getSave()))
                                                      .collect(Collectors.toMap(advisor -> advisor.getId().getId(), Function.identity()));

        ClausewitzItem advisorItem = this.item.getChild("advisor");

        if (advisorItem != null) {
            SaveAdvisor advisor = new SaveAdvisor(advisorItem, this.province.getSave());
            advisors.put(advisor.getId().getId(), advisor);
        }

        return advisors;
    }

    public static void addEvent(ClausewitzItem item, LocalDate date, String key, String value) {
        if (date != null) {
            ClausewitzItem eventItem = item.addChild(ClausewitzUtils.dateToString(date));
            eventItem.addVariable(key, value);
        }
    }

    public static void addEvent(ClausewitzItem item, LocalDate date, String objectKey, String key, String value) {
        if (date != null) {
            ClausewitzItem eventItem = item.addChild(ClausewitzUtils.dateToString(date));
            ClausewitzItem child = eventItem.addChild(objectKey);
            child.addVariable(key, value);
        }
    }

    public static void addEvent(ClausewitzItem item, LocalDate date, String key, boolean value) {
        if (date != null) {
            ClausewitzItem eventItem = item.addChild(ClausewitzUtils.dateToString(date));
            eventItem.addVariable(key, value);
        }
    }
}
