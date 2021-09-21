package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.clausewitzparser.model.ClausewitzPObject;
import fr.osallek.eu4parser.model.Color;
import org.apache.commons.collections4.CollectionUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Area extends Noded {

    private ClausewitzItem item;

    private ClausewitzList list;

    private ClausewitzPObject object;

    private Region region;

    public Area(ClausewitzList list, FileNode fileNode) {
        super(fileNode);
        this.list = list;
        this.item = null;
        this.object = this.list;
    }

    public Area(ClausewitzItem item, FileNode fileNode) {
        super(fileNode);
        this.item = item;
        this.list = null;
        this.object = this.item;
    }

    @Override
    public String getName() {
        return this.object.getName();
    }

    public void setName(String name) {
        this.object.setName(name);
    }

    public List<Integer> getProvinces() {
        if (this.list != null) {
            return this.list.getValuesAsInt();
        } else if (this.item != null) {
            ClausewitzList clausewitzList = this.item.getList("");

            return clausewitzList == null ? new ArrayList<>() : clausewitzList.getValuesAsInt();
        } else {
            return new ArrayList<>();
        }
    }

    public void setProvinces(List<Integer> provinces) {
        if (CollectionUtils.isEmpty(provinces)) {
            if (this.list != null) {
                this.list.clear();
            } else if (this.item != null) {
                ClausewitzList clausewitzList = this.item.getList("");

                if (clausewitzList != null) {
                    clausewitzList.clear();
                }
            }

            return;
        }

        if (this.list != null) {
            this.list.setAll(provinces.stream().filter(Objects::nonNull).toArray(Integer[]::new));
        } else if (this.item != null) {
            ClausewitzList clausewitzList = this.item.getList("");

            if (clausewitzList != null) {
                clausewitzList.setAll(provinces.stream().filter(Objects::nonNull).toArray(Integer[]::new));
            } else {
                this.item.addList("", provinces.stream().filter(Objects::nonNull).toArray(Integer[]::new));
            }
        }
    }

    public void addProvince(int province) {
        if (this.list != null) {
            this.list.add(province);
        } else if (this.item != null) {
            ClausewitzList clausewitzList = this.item.getList("");

            if (clausewitzList != null) {
                clausewitzList.setAll(province);
            } else {
                this.item.addList("", province);
            }
        }
    }

    public void removeProvince(int province) {
        if (this.list != null) {
            this.list.remove(String.valueOf(province));
        }  else if (this.item != null) {
            ClausewitzList clausewitzList = this.item.getList("");

            if (clausewitzList != null) {
                clausewitzList.remove(String.valueOf(province));
            }
        }
    }

    public Color getColor() {
        if (this.item == null) {
            return null;
        }

        ClausewitzList clausewitzList = this.item.getList("color");
        return clausewitzList == null ? null : new Color(clausewitzList);
    }

    public void setColor(Color color) {
        if (color == null) {
            if (this.item != null) {
                this.item.removeList("color");
            }

            return;
        }

        if (this.list != null) {
            this.item = this.list.getParent().addChild(this.list.getName(), true);
            this.list = null;
            this.object = this.item;
        }

        ClausewitzList clausewitzList = this.item.getList("color");

        if (clausewitzList != null) {
            Color actualColor = new Color(clausewitzList);
            actualColor.setRed(color.getRed());
            actualColor.setGreen(color.getGreen());
            actualColor.setBlue(color.getBlue());
        } else {
            Color.addToItem(this.item, "color", color);
        }
    }

    public Region getRegion() {
        return region;
    }

    void setRegion(Region region) {
        this.region = region;
    }

    @Override
    public void write(BufferedWriter writer) throws IOException {
        this.object.write(writer, true, 0, new HashMap<>());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Area)) {
            return false;
        }

        Area area = (Area) o;

        return Objects.equals(getName(), area.getName());
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
