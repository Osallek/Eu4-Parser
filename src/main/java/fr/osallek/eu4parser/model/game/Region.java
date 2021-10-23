package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.MonthDay;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Region extends Nodded {

    private final ClausewitzItem item;

    private final Game game;

    private SuperRegion superRegion;

    public Region(ClausewitzItem item, Game game) {
        this.item = item;
        this.game = game;
    }

    @Override
    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public SuperRegion getSuperRegion() {
        return superRegion;
    }

    void setSuperRegion(SuperRegion superRegion) {
        this.superRegion = superRegion;
    }

    public List<Area> getAreas() {
        ClausewitzList list = this.item.getList("areas");
        return list == null ? null : list.getValues().stream().map(this.game::getArea).collect(Collectors.toList());
    }

    public void setAreas(List<String> areas) {
        if (CollectionUtils.isEmpty(areas)) {
            this.item.removeList("areas");
            return;
        }

        ClausewitzList list = this.item.getList("areas");

        if (list != null) {
            list.setAll(areas.stream().filter(Objects::nonNull).toArray(String[]::new));
        } else {
            this.item.addList("areas", areas.stream().filter(Objects::nonNull).toArray(String[]::new));
        }
    }

    public void addArea(String area) {
        ClausewitzList list = this.item.getList("areas");

        if (list != null) {
            list.add(area);
            list.sort();
        } else {
            this.item.addList("areas", area);
        }
    }

    public void removeArea(String area) {
        ClausewitzList list = this.item.getList("areas");

        if (list != null) {
            list.remove(area);
        }
    }

    public Map<MonthDay, MonthDay> getMonsoon() {
        List<ClausewitzList> lists = this.item.getLists("monsoon");

        return lists.stream()
                    .collect(Collectors.toMap(
                            l -> MonthDay.of(
                                    Integer.parseInt(l.get(0).substring(l.get(0).indexOf(".") + 1, l.get(0).lastIndexOf("."))),
                                    Integer.parseInt(l.get(0).substring(l.get(0).lastIndexOf(".") + 1))),
                            l -> MonthDay.of(
                                    Integer.parseInt(l.get(1).substring(l.get(1).indexOf(".") + 1, l.get(0).lastIndexOf("."))),
                                    Integer.parseInt(l.get(1).substring(l.get(1).lastIndexOf(".") + 1)))));
    }

    public void setMonsoon(Map<MonthDay, MonthDay> monsoon) {
        if (MapUtils.isEmpty(monsoon)) {
            this.item.removeListIf(list -> "monsoon".equals(list.getName()));
            return;
        }

        Map<MonthDay, MonthDay> existing = getMonsoon();

        NumberFormat formatter = new DecimalFormat("00");

        monsoon.forEach((startDay, endDay) -> {
            if (!existing.containsKey(startDay) || existing.get(startDay) != endDay) {
                this.item.addList("monsoon", "00." + formatter.format(startDay.getMonthValue()) + "." + formatter.format(startDay.getDayOfMonth()),
                                  "00." + formatter.format(endDay.getMonthValue()) + "." + formatter.format(endDay.getDayOfMonth()));
            }
        });
    }

    public boolean isMonsoon(Date date) {
        MonthDay monthDay = MonthDay.from(date.toInstant());
        Map<MonthDay, MonthDay> monsoon = getMonsoon();

        return monsoon != null && monsoon.entrySet()
                                         .stream()
                                         .anyMatch(entry -> entry.getKey().equals(monthDay) || entry.getValue().equals(monthDay)
                                                            || (entry.getKey().isBefore(monthDay) && entry.getValue().isBefore(monthDay)));
    }

    @Override
    public void write(BufferedWriter writer) throws IOException {
        this.item.write(writer, true, 0, new HashMap<>());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Region)) {
            return false;
        }

        Region region = (Region) o;

        return Objects.equals(getName(), region.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return getName();
    }
}
