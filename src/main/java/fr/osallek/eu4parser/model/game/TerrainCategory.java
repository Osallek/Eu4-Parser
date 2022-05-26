package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.model.Color;
import org.apache.commons.collections4.CollectionUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class TerrainCategory extends Nodded {

    private final ClausewitzItem item;

    private final List<Integer> computedProvinces;

    public TerrainCategory(ClausewitzItem item, FileNode fileNode) {
        super(fileNode);
        this.item = item;
        this.computedProvinces = new ArrayList<>();
    }

    @Override
    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public Color getColor() {
        ClausewitzList list = this.item.getList("color");
        return list == null ? null : new Color(list);
    }

    public void setColor(Color color) {
        if (color == null) {
            this.item.removeList("color");
            return;
        }

        ClausewitzList list = this.item.getList("color");

        if (list != null) {
            fr.osallek.eu4parser.model.Color actualColor = new Color(list);
            actualColor.setRed(color.getRed());
            actualColor.setGreen(color.getGreen());
            actualColor.setBlue(color.getBlue());
        } else {
            Color.addToItem(this.item, "color", color);
        }
    }

    public String getSoundType() {
        return this.item.getVarAsString("sound_type");
    }

    public void setSoundType(String soundType) {
        this.item.setVariable("sound_type", soundType);
    }

    public Boolean isWater() {
        return this.item.getVarAsBool("is_water");
    }

    public void setIsWater(Boolean isWater) {
        if (isWater == null) {
            this.item.removeVariable("is_water");
        } else {
            this.item.setVariable("is_water", isWater);
        }
    }

    public Boolean isInlandSea() {
        return this.item.getVarAsBool("inland_sea");
    }

    public void setInlandSea(Boolean inlandSea) {
        if (inlandSea == null) {
            this.item.removeVariable("inland_sea");
        } else {
            this.item.setVariable("inland_sea", inlandSea);
        }
    }

    public Integer getDefence() {
        return this.item.getVarAsInt("defence");
    }

    public void setDefence(Integer defence) {
        if (defence == null) {
            this.item.removeVariable("defence");
        } else {
            this.item.setVariable("defence", defence);
        }
    }

    public Integer getAllowedNumOfBuildings() {
        return this.item.getVarAsInt("allowed_num_of_buildings");
    }

    public void setAllowedNumOfBuildings(Integer allowedNumOfBuildings) {
        if (allowedNumOfBuildings == null) {
            this.item.removeVariable("allowed_num_of_buildings");
        } else {
            this.item.setVariable("allowed_num_of_buildings", allowedNumOfBuildings);
        }
    }

    public Integer getSupplyLimit() {
        return this.item.getVarAsInt("supply_limit");
    }

    public void setSupplyLimit(Integer supplyLimit) {
        if (supplyLimit == null) {
            this.item.removeVariable("supply_limit");
        } else {
            this.item.setVariable("supply_limit", supplyLimit);
        }
    }

    public Double getMovementCost() {
        return this.item.getVarAsDouble("movement_cost");
    }

    public void setMovementCost(Double movementCost) {
        if (movementCost == null) {
            this.item.removeVariable("movement_cost");
        } else {
            this.item.setVariable("movement_cost", movementCost);
        }
    }

    public Double getLocalDevelopmentCost() {
        return this.item.getVarAsDouble("local_development_cost");
    }

    public void setLocalDevelopmentCost(Double localDevelopmentCost) {
        if (localDevelopmentCost == null) {
            this.item.removeVariable("local_development_cost");
        } else {
            this.item.setVariable("local_development_cost", localDevelopmentCost);
        }
    }

    public Double getNationDesignerCostMultiplier() {
        return this.item.getVarAsDouble("nation_designer_cost_multiplier");
    }

    public void setNationDesignerCostMultiplier(Double nationDesignerCostMultiplier) {
        if (nationDesignerCostMultiplier == null) {
            this.item.removeVariable("nation_designer_cost_multiplier");
        } else {
            this.item.setVariable("nation_designer_cost_multiplier", nationDesignerCostMultiplier);
        }
    }

    public Double getLocalDefensiveness() {
        return this.item.getVarAsDouble("local_defensiveness");
    }

    public void setLocalDefensiveness(Double localDefensiveness) {
        if (localDefensiveness == null) {
            this.item.removeVariable("local_defensiveness");
        } else {
            this.item.setVariable("local_defensiveness", localDefensiveness);
        }
    }

    public List<Integer> getProvinces() {
        ClausewitzList list = this.item.getList("terrain_override");
        return list == null ? null : list.getValuesAsInt();
    }

    public void setProvinces(List<Integer> provinces) {
        if (CollectionUtils.isEmpty(provinces)) {
            this.item.removeList("terrain_override");
            return;
        }

        ClausewitzList list = this.item.getList("terrain_override");

        if (list != null) {
            list.setAll(provinces.stream().filter(Objects::nonNull).toArray(Integer[]::new));
        } else {
            this.item.addList("terrain_override", provinces.stream().filter(Objects::nonNull).toArray(Integer[]::new));
        }
    }

    public void addProvince(int province) {
        ClausewitzList list = this.item.getList("terrain_override");

        if (list != null) {
            list.add(province);
            list.sortInt();
        } else {
            this.item.addList("terrain_override", province);
        }
    }

    public void removeProvince(int province) {
        ClausewitzList list = this.item.getList("terrain_override");

        if (list != null) {
            list.remove(String.valueOf(province));
        }
    }

    public List<Integer> getComputedProvinces() {
        return computedProvinces;
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

        if (!(o instanceof TerrainCategory terrainCategory)) {
            return false;
        }

        return Objects.equals(getName(), terrainCategory.getName());
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
