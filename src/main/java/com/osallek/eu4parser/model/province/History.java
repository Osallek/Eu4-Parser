package com.osallek.eu4parser.model.province;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.common.Eu4Utils;

import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class History {

    private final ClausewitzItem item;

    private final Province province;

    private SortedMap<Date, String> owners;

    private SortedMap<Date, String> controllers;

    private SortedMap<Date, String> religions;

    private SortedMap<Date, String> cultures;

    private Map<Long, Advisor> advisors;

    public History(ClausewitzItem item, Province province) {
        this.item = item;
        this.province = province;
        refreshAttributes();
    }

    public SortedMap<Date, String> getOwners() {
        return owners;
    }

    public SortedMap<Date, String> getControllers() {
        return controllers;
    }

    public SortedMap<Date, String> getReligions() {
        return religions;
    }

    public SortedMap<Date, String> getCultures() {
        return cultures;
    }

    public Map<Long, Advisor> getAdvisors() {
        return advisors;
    }

    private void refreshAttributes() {
        this.owners = this.item.getChildren()
                               .stream()
                               .filter(child -> child.getVar("owner") != null)
                               .collect(Collectors.toMap(child -> Eu4Utils.stringToDate(child.getName()),
                                                         child -> child.getVarAsString("owner"),
                                                         (a, b) -> b,
                                                         TreeMap::new));
        //No startDate because already in history

        this.controllers = this.item.getChildren()
                                    .stream()
                                    .filter(child -> child.getChild("controller") != null)
                                    .filter(child -> child.getChild("controller").getVar("tag") != null)
                                    .collect(Collectors.toMap(var -> Eu4Utils.stringToDate(var.getName()),
                                                              child -> child.getChild("controller")
                                                                            .getVarAsString("tag"),
                                                              (a, b) -> b,
                                                              TreeMap::new));
        ClausewitzItem controllerItem = this.item.getChild("controller");

        if (controllerItem != null) {
            this.controllers.put(this.province.getSave().getStartDate(), controllerItem.getVarAsString("tag"));
        }

        this.religions = this.item.getChildrenNot("advisor")
                                  .stream()
                                  .filter(child -> child.getVar("religion") != null)
                                  .collect(Collectors.toMap(child -> Eu4Utils.stringToDate(child.getName()),
                                                            child -> child.getVarAsString("religion"),
                                                            (a, b) -> b,
                                                            TreeMap::new));
        this.religions.put(this.province.getSave().getStartDate(), this.item.getVarAsString("religion"));

        this.cultures = this.item.getChildrenNot("advisor")
                                 .stream()
                                 .filter(child -> child.getVar("culture") != null)
                                 .collect(Collectors.toMap(child -> Eu4Utils.stringToDate(child.getName()),
                                                           child -> child.getVarAsString("culture"),
                                                           (a, b) -> b,
                                                           TreeMap::new));
        this.cultures.put(this.province.getSave().getStartDate(), this.item.getVarAsString("culture"));

        this.advisors = this.item.getChildren()
                                 .stream()
                                 .map(child -> child.getChild("advisor"))
                                 .filter(Objects::nonNull)
                                 .map(Advisor::new)
                                 .collect(Collectors.toMap(advisor -> advisor.getId().getId(), Function.identity()));

        ClausewitzItem advisorItem = this.item.getChild("advisor");

        if (advisorItem != null) {
            Advisor advisor = new Advisor(advisorItem);
            this.advisors.put(advisor.getId().getId(), advisor);
        }

        if (!this.advisors.isEmpty() && !this.owners.isEmpty()) {
            this.advisors.values()
                         .forEach(advisor -> this.owners.values()
                                                        .stream()
                                                        .map(owner -> this.province.getSave().getCountry(ClausewitzUtils.removeQuotes(owner)))
                                                        .filter(country -> country.getAdvisorsIds() != null)
                                                        .filter(country -> country.getAdvisorsIds()
                                                                                  .contains(advisor.getId()))
                                                        .findFirst()
                                                        .ifPresent(country -> country.getAdvisors()
                                                                                     .put(advisor.getId().getId(),
                                                                                          advisor)));
        }
    }
}
