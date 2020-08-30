package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.clausewitzparser.model.ClausewitzObject;
import com.osallek.eu4parser.model.Color;
import org.luaj.vm2.ast.Str;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Government {

    private final String name;

    private String localizedName;

    private final String basicReform;

    private final Color color;

    private final List<String> legacyGovernment;

    private final List<String> exclusiveReforms;

    private Map<String, List<String>> reformLevels;

    public Government(ClausewitzItem item) {
        this.name = item.getName();
        this.basicReform = item.getVarAsString("basic_reform");

        ClausewitzList list;
        this.color = (list = item.getList("color")) == null ? null : new Color(list);
        this.legacyGovernment = (list = item.getList("legacy_government")) == null ? null : list.getValues();

        List<ClausewitzList> lists = item.getLists("exclusive_reforms");
        this.exclusiveReforms = lists.stream().map(ClausewitzList::getValues).flatMap(Collection::stream).collect(Collectors.toList());

        ClausewitzItem child = item.getChild("reform_levels");
        if (child != null) {
            this.reformLevels = child.getChildren().stream().collect(Collectors.toMap(ClausewitzObject::getName, clausewitzItem -> {
                ClausewitzList reformsList = clausewitzItem.getList("reforms");
                return reformsList == null ? new ArrayList<>() : reformsList.getValues();
            }, (strings, strings2) -> strings, LinkedHashMap::new));
        }
    }

    public String getName() {
        return name;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public String getBasicReform() {
        return basicReform;
    }

    public Color getColor() {
        return color;
    }

    public List<String> getLegacyGovernment() {
        return legacyGovernment;
    }

    public List<String> getExclusiveReforms() {
        return exclusiveReforms;
    }

    public Map<String, List<String>> getReformLevels() {
        return reformLevels;
    }
}
