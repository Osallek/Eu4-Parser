package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.clausewitzparser.model.ClausewitzObject;
import fr.osallek.eu4parser.model.Color;
import fr.osallek.eu4parser.model.game.todo.GovernmentReform;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Government {

    private final ClausewitzItem item;

    private final Game game;

    public Government(ClausewitzItem item, Game game) {
        this.item = item;
        this.game = game;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public String getBasicReform() {
        return this.item.getVarAsString("basic_reform");
    }

    public void setBasicReform(String basicReform) {
        if (StringUtils.isBlank(basicReform)) {
            this.item.removeVariable("basic_reform");
        } else {
            this.item.setVariable("basic_reform", basicReform);
        }
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
            Color actualColor = new Color(list);
            actualColor.setRed(color.getRed());
            actualColor.setGreen(color.getGreen());
            actualColor.setBlue(color.getBlue());
        } else {
            Color.addToItem(this.item, "color", color);
        }
    }

    public List<String> getLegacyGovernment() {
        ClausewitzList list = this.item.getList("legacy_government");
        return list == null ? null : list.getValues();
    }

    public void setLegacyGovernment(List<String> legacyGovernment) {
        if (CollectionUtils.isEmpty(legacyGovernment)) {
            this.item.removeList("legacy_government");
            return;
        }

        ClausewitzList list = this.item.getList("legacy_government");

        if (list != null) {
            list.setAll(legacyGovernment.stream().filter(Objects::nonNull).toArray(String[]::new));
        } else {
            this.item.addList("legacy_government", legacyGovernment.stream().filter(Objects::nonNull).toArray(String[]::new));
        }
    }

    public List<String> getExclusiveReforms() {
        ClausewitzList list = this.item.getList("exclusive_reforms");
        return list == null ? null : list.getValues();
    }

    public void setExclusiveReforms(List<String> exclusiveReforms) {
        if (CollectionUtils.isEmpty(exclusiveReforms)) {
            this.item.removeList("exclusive_reforms");
            return;
        }

        ClausewitzList list = this.item.getList("exclusive_reforms");

        if (list != null) {
            list.setAll(exclusiveReforms.stream().filter(Objects::nonNull).toArray(String[]::new));
        } else {
            this.item.addList("exclusive_reforms", exclusiveReforms.stream().filter(Objects::nonNull).toArray(String[]::new));
        }
    }

    public Map<String, List<GovernmentReform>> getReformLevels() {
        ClausewitzItem child = this.item.getChild("reform_levels");
        if (child != null) {
            return child.getChildren()
                        .stream()
                        .collect(Collectors.toMap(ClausewitzObject::getName, clausewitzItem -> {
                            ClausewitzList reformsList = clausewitzItem.getList("reforms");

                            return reformsList == null ? new ArrayList<>() : reformsList.getValues()
                                                                                        .stream()
                                                                                        .map(this.game::getGovernmentReform)
                                                                                        .toList();
                        }, (reforms, reforms1) -> reforms, LinkedHashMap::new));
        }

        return null;
    }
}
