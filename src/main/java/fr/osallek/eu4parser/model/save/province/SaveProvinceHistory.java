package fr.osallek.eu4parser.model.save.province;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.common.NumbersUtils;
import fr.osallek.eu4parser.model.game.Culture;
import fr.osallek.eu4parser.model.save.SaveReligion;
import fr.osallek.eu4parser.model.save.country.SaveCountry;
import org.apache.commons.collections4.MapUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SaveProvinceHistory extends SaveProvinceHistoryEvent {

    private final ClausewitzItem item;

    private final SaveProvince province;

    private SortedMap<LocalDate, SaveCountry> owners;

    private SortedMap<LocalDate, List<SaveCountry>> claims;

    private SortedMap<LocalDate, SaveCountry> controllers;

    private SortedMap<LocalDate, SaveReligion> religions;

    private SortedMap<LocalDate, Culture> cultures;

    private Map<Integer, SaveAdvisor> advisors;

    public SaveProvinceHistory(ClausewitzItem item, SaveProvince province) {
        super(item, province);
        this.item = item;
        this.province = province;
        refreshAttributes();
    }

    @Override
    public LocalDate getDate() {
        return this.province.getSave().getStartDate().orElse(null);
    }

    public Optional<Boolean> getSeatInParliament() {
        return this.item.getLastVarAsBool("seat_in_parliament");
    }

    public Optional<Double> getExtraCost() {
        return this.item.getLastVarAsDouble("extra_cost");
    }

    public Optional<Integer> getFormerNativeSize() {
        return this.item.getLastVarAsDouble("former_native_size").map(NumbersUtils::doubleToInt);
    }

    public List<SaveProvinceHistoryEvent> getEvents() {
        return this.item.getChildren()
                        .stream()
                        .filter(child -> ClausewitzUtils.DATE_PATTERN.matcher(ClausewitzUtils.removeQuotes(child.getName())).matches())
                        .map(child -> new SaveProvinceHistoryEvent(child, this.province))
                        .collect(Collectors.toList());
    }

    public SortedMap<LocalDate, SaveCountry> getOwners() {
        return owners;
    }

    public SortedMap<LocalDate, List<SaveCountry>> getClaims() {
        return claims;
    }

    public SortedMap<LocalDate, SaveCountry> getControllers() {
        return controllers;
    }

    public SortedMap<LocalDate, SaveReligion> getReligions() {
        return religions;
    }

    public SortedMap<LocalDate, Culture> getCultures() {
        return cultures;
    }

    public Map<Integer, SaveAdvisor> getAdvisors() {
        return advisors;
    }

    public void addEvent(LocalDate date, String key, String value) {
        if (date != null) {
            ClausewitzItem eventItem = this.item.addChild(ClausewitzUtils.dateToString(date));
            eventItem.addVariable(key, value);
            refreshAttributes();
        }
    }

    public void addEvent(LocalDate date, String objectKey, String key, String value) {
        if (date != null) {
            ClausewitzItem eventItem = this.item.addChild(ClausewitzUtils.dateToString(date));
            ClausewitzItem child = eventItem.addChild(objectKey);
            child.addVariable(key, value);
            refreshAttributes();
        }
    }

    public void addEvent(LocalDate date, String key, boolean value) {
        if (date != null) {
            ClausewitzItem eventItem = this.item.addChild(ClausewitzUtils.dateToString(date));
            eventItem.addVariable(key, value);
            refreshAttributes();
        }
    }

    private void refreshAttributes() {
        this.owners = this.item.getChildren()
                               .stream()
                               .filter(child -> child.hasVar("owner"))
                               .collect(Collectors.toMap(child -> Eu4Utils.stringToDate(child.getName()),
                                                         child -> this.province.getSave().getCountry(child.getVarAsString("owner").get()),
                                                         (a, b) -> b,
                                                         TreeMap::new));
        this.claims = this.item.getChildren()
                               .stream()
                               .filter(child -> child.hasVar("add_claim"))
                               .collect(Collectors.groupingBy(child -> Eu4Utils.stringToDate(child.getName()),
                                                              TreeMap::new,
                                                              Collectors.mapping(child -> this.province.getSave().getCountry(child.getVarAsString("add_claim").get()),
                                                                                 Collectors.toList())));
        //No startDate because already in history

        this.controllers = this.item.getChildren()
                                    .stream()
                                    .filter(child -> child.getChild("controller").filter(child2 -> child2.hasVar("tag")).isPresent())
                                    .collect(Collectors.toMap(child -> Eu4Utils.stringToDate(child.getName()),
                                                              child -> this.province.getSave()
                                                                                    .getCountry(child.getChild("controller").get().getVarAsString("tag").get()),
                                                              (a, b) -> a, //Take a because only happens when changing tag
                                                              TreeMap::new));

        if (MapUtils.isNotEmpty(this.owners) && this.province.getOwner() != null && !this.owners.containsValue(this.province.getOwner())) {
            if (MapUtils.isNotEmpty(this.province.getOwner().getHistory().getChangedTagsFrom())) {
                this.owners.put(this.province.getOwner().getHistory().getChangedTagsFrom().lastKey(), this.province.getOwner());
            } else {
                this.owners.put(this.province.getSave().getDate(), this.province.getOwner());
            }
        }


        this.item.getChild("controller")
                 .flatMap(controllerItem -> controllerItem.getVarAsString("tag"))
                 .map(s -> this.province.getSave().getCountry(s))
                 .ifPresent(country -> this.controllers.put(this.province.getSave().getStartDate().get(), country));

        this.religions = this.item.getChildrenNot("advisor")
                                  .stream()
                                  .filter(child -> child.getVarAsString("religion").map(s -> this.province.getSave().getReligions().getReligion(s)).isPresent())
                                  .collect(Collectors.toMap(child -> Eu4Utils.stringToDate(child.getName()),
                                                            child -> this.province.getSave().getReligions().getReligion(child.getVarAsString("religion").get()),
                                                            (a, b) -> b,
                                                            TreeMap::new));
        this.item.getVarAsString("religion")
                 .map(s -> this.province.getSave().getReligions().getReligion(s))
                 .ifPresent(r -> this.religions.put(this.province.getSave().getStartDate().get(), r));

        this.cultures = this.item.getChildrenNot("advisor")
                                 .stream()
                                 .filter(child -> child.getVarAsString("culture").map(s -> this.province.getSave().getGame().getCulture(s)).isPresent())
                                 .collect(Collectors.toMap(child -> Eu4Utils.stringToDate(child.getName()),
                                                           child -> this.province.getSave().getGame().getCulture(child.getVarAsString("culture").get()),
                                                           (a, b) -> b,
                                                           TreeMap::new));

        this.item.getVarAsString("culture")
                 .map(s -> this.province.getSave().getGame().getCulture(s))
                 .ifPresent(culture -> this.cultures.put(this.province.getSave().getStartDate().get(), culture));

        this.advisors = this.item.getChildren()
                                 .stream()
                                 .map(child -> child.getChild("advisor"))
                                 .filter(Optional::isPresent)
                                 .map(child -> new SaveAdvisor(child.get(), this.province.getSave()))
                                 .collect(Collectors.toMap(advisor -> advisor.getId().getId(), Function.identity()));

        this.item.getChild("advisor").ifPresent(advisorItem -> {
            SaveAdvisor advisor = new SaveAdvisor(advisorItem, this.province.getSave());
            this.advisors.put(advisor.getId().getId(), advisor);
        });

        if (!this.advisors.isEmpty()) {
            if (!this.owners.isEmpty()) {
                this.advisors.values()
                             .forEach(advisor -> this.owners.values()
                                                            .stream()
                                                            .filter(country -> country.getAdvisorsIds() != null)
                                                            .filter(country -> country.getAdvisorsIds().contains(advisor.getId()))
                                                            .findFirst()
                                                            .ifPresent(country -> country.addAdvisor(advisor)));
            }

            this.province.getSave().putAllAdvisors(this.advisors);
        }
    }
}
