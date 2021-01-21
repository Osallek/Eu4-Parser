package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;

import java.time.MonthDay;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Region {

    private final String name;

    private String localizedName;

    private final List<Area> areas;

    private final Map<MonthDay, MonthDay> monsoon;

    private SuperRegion superRegion;

    public Region(ClausewitzItem item, Game game) {
        this.name = item.getName();

        ClausewitzList list = item.getList("areas");
        this.areas = list == null ? null : list.getValues().stream().map(game::getArea).collect(Collectors.toList());

        List<ClausewitzList> lists = item.getLists("monsoon");
        this.monsoon = lists.stream()
                            .collect(Collectors.toMap(
                                    l -> MonthDay.of(
                                            Integer.parseInt(l.get(0).substring(l.get(0).indexOf(".") + 1, l.get(0).lastIndexOf("."))),
                                            Integer.parseInt(l.get(0).substring(l.get(0).lastIndexOf(".") + 1))),
                                    l -> MonthDay.of(
                                            Integer.parseInt(l.get(1).substring(l.get(1).indexOf(".") + 1, l.get(0).lastIndexOf("."))),
                                            Integer.parseInt(l.get(1).substring(l.get(1).lastIndexOf(".") + 1)))));
    }

    public String getName() {
        return name;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    public void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public SuperRegion getSuperRegion() {
        return superRegion;
    }

    void setSuperRegion(SuperRegion superRegion) {
        this.superRegion = superRegion;
    }

    public List<Area> getAreas() {
        return areas;
    }

    public Map<MonthDay, MonthDay> getMonsoon() {
        return monsoon;
    }

    public boolean isMonsoon(Date date) {
        MonthDay monthDay = MonthDay.from(date.toInstant());
        return this.monsoon != null && this.monsoon.entrySet()
                                                   .stream()
                                                   .anyMatch(entry -> entry.getKey().equals(monthDay) || entry.getValue().equals(monthDay)
                                                                      || (entry.getKey().isBefore(monthDay) && entry.getValue().isBefore(monthDay)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Region)) {
            return false;
        }

        Region area = (Region) o;

        return Objects.equals(name, area.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
