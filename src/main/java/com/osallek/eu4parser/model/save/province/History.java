package com.osallek.eu4parser.model.save.province;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.common.Eu4Utils;
import com.osallek.eu4parser.model.game.Culture;
import com.osallek.eu4parser.model.save.SaveReligion;
import com.osallek.eu4parser.model.save.country.Country;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class History {

    private final ClausewitzItem item;

    private final SaveProvince province;

    private SortedMap<LocalDate, Country> owners;

    private SortedMap<LocalDate, List<Country>> claims;

    private SortedMap<LocalDate, Country> controllers;

    private SortedMap<LocalDate, SaveReligion> religions;

    private SortedMap<LocalDate, Culture> cultures;

    private Map<Integer, SaveAdvisor> advisors;

    public History(ClausewitzItem item, SaveProvince province) {
        this.item = item;
        this.province = province;
        refreshAttributes();
    }

    public SortedMap<LocalDate, Country> getOwners() {
        return owners;
    }

    public SortedMap<LocalDate, List<Country>> getClaims() {
        return claims;
    }

    public SortedMap<LocalDate, Country> getControllers() {
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
                                                         child -> this.province.getSave().getCountry(child.getVarAsString("owner")),
                                                         (a, b) -> b,
                                                         TreeMap::new));
        this.claims = this.item.getChildren()
                               .stream()
                               .filter(child -> child.hasVar("add_claim"))
                               .collect(Collectors.groupingBy(child -> Eu4Utils.stringToDate(child.getName()),
                                                              TreeMap::new,
                                                              Collectors.mapping(child -> this.province.getSave().getCountry(child.getVarAsString("add_claim")),
                                                                                 Collectors.toList())));
        //No startDate because already in history

        this.controllers = this.item.getChildren()
                                    .stream()
                                    .filter(child -> child.hasChild("controller"))
                                    .filter(child -> child.getChild("controller").hasVar("tag"))
                                    .collect(Collectors.toMap(var -> Eu4Utils.stringToDate(var.getName()),
                                                              child -> this.province.getSave().getCountry(child.getChild("controller").getVarAsString("tag")),
                                                              (a, b) -> b,
                                                              TreeMap::new));


        ClausewitzItem controllerItem = this.item.getChild("controller");

        if (controllerItem != null) {
            this.controllers.put(this.province.getSave().getStartDate(), this.province.getSave().getCountry(controllerItem.getVarAsString("tag")));
        }

        this.religions = this.item.getChildrenNot("advisor")
                                  .stream()
                                  .filter(child -> child.hasVar("religion"))
                                  .collect(Collectors.toMap(child -> Eu4Utils.stringToDate(child.getName()),
                                                            child -> this.province.getSave().getReligions().getReligion(child.getVarAsString("religion")),
                                                            (a, b) -> b,
                                                            TreeMap::new));
        this.religions.put(this.province.getSave().getStartDate(), this.province.getSave().getReligions().getReligion(this.item.getVarAsString("religion")));

        this.cultures = this.item.getChildrenNot("advisor")
                                 .stream()
                                 .filter(child -> child.hasVar("culture"))
                                 .collect(Collectors.toMap(child -> Eu4Utils.stringToDate(child.getName()),
                                                           child -> this.province.getSave().getGame().getCulture(child.getVarAsString("culture")),
                                                           (a, b) -> b,
                                                           TreeMap::new));
        this.cultures.put(this.province.getSave().getStartDate(), this.province.getSave().getGame().getCulture(this.item.getVarAsString("culture")));

        this.advisors = this.item.getChildren()
                                 .stream()
                                 .map(child -> child.getChild("advisor"))
                                 .filter(Objects::nonNull)
                                 .map(child -> new SaveAdvisor(child, this.province.getSave()))
                                 .collect(Collectors.toMap(advisor -> advisor.getId().getId(), Function.identity()));

        ClausewitzItem advisorItem = this.item.getChild("advisor");

        if (advisorItem != null) {
            SaveAdvisor advisor = new SaveAdvisor(advisorItem, this.province.getSave());
            this.advisors.put(advisor.getId().getId(), advisor);
        }

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
