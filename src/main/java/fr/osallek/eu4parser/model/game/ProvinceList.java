package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzList;
import org.apache.commons.collections4.CollectionUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ProvinceList extends Nodded {

    private final ClausewitzList list;

    private final boolean fake;

    private final String localizationKey;

    public ProvinceList(ClausewitzList list, FileNode fileNode) {
        super(fileNode);
        this.list = list;
        this.fake = false;
        this.localizationKey = list.getName();
    }

    public ProvinceList(ClausewitzList list, String localizationKey, FileNode fileNode) {
        super(fileNode);
        this.list = list;
        this.fake = false;
        this.localizationKey = localizationKey;
    }

    public ProvinceList(String name, List<Integer> provinces) {
        this.list = new ClausewitzList(null, name, 0);
        this.list.setAll(provinces.toArray(Integer[]::new));
        this.fake = true;
        this.localizationKey = name;
    }

    public ProvinceList(String name, String localizationKey, List<Integer> provinces) {
        this.list = new ClausewitzList(null, name, 0);
        this.list.setAll(provinces.toArray(Integer[]::new));
        this.fake = true;
        this.localizationKey = localizationKey;
    }

    @Override
    public String getName() {
        return this.list.getName();
    }

    public void setName(String name) {
        this.list.setName(name);
    }

    public String getLocalizationKey() {
        return localizationKey;
    }

    public List<Integer> getProvinces() {
        return this.list.getValuesAsInt();
    }

    public void setProvinces(List<Integer> provinces) {
        if (CollectionUtils.isEmpty(provinces)) {
            this.list.clear();
            return;
        }

        this.list.setAll(provinces.stream().filter(Objects::nonNull).toArray(Integer[]::new));
        this.list.sortInt();
    }

    public void addProvince(int province) {
        this.list.add(province);
        this.list.sortInt();
    }

    public void removeProvince(int province) {
        this.list.remove(String.valueOf(province));
    }

    public boolean isFake() {
        return fake;
    }

    @Override
    public void write(BufferedWriter writer) throws IOException {
        this.list.write(writer, true, 0, new HashMap<>());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ProvinceList provinceList)) {
            return false;
        }

        return Objects.equals(getName(), provinceList.getName());
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
