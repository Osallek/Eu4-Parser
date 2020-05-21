package com.osallek.eu4parser.model.save.religion;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.save.Religion;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Religions {

    private final ClausewitzItem religionsItem;

    private final ClausewitzItem religionInstanceDataItem;

    private Map<String, Religion> religions;

    public Religions(ClausewitzItem religionsItem, ClausewitzItem religionInstanceDataItem) {
        this.religionsItem = religionsItem;
        this.religionInstanceDataItem = religionInstanceDataItem;

        refreshAttributes();
    }

    public Religion getReligion(String name) {
        return this.religions.get(name);
    }

    public List<MuslimRelation> getMuslimRelations() {
        Religion religion = this.religions.get("muslim");

        if (religion == null) {
            return null;
        }

        return religion.getRelations();
    }

    public Papacy getPapacy() {
        Religion religion = this.religions.get("catholic");

        if (religion == null) {
            return null;
        }

        return religion.getPapacy();
    }

    public Map<String, Religion> getReligions() {
        return religions;
    }

    private void refreshAttributes() {
        this.religions = this.religionsItem.getChildren()
                                           .stream()
                                           .map(item -> new Religion(item, religionInstanceDataItem.getChild(item.getName())))
                                           .collect(Collectors.toMap(Religion::getName, Function.identity(), (e1, e2) -> e1, LinkedHashMap::new));
        this.religionInstanceDataItem.getChildren().forEach(religion -> {
            if (!this.religions.containsKey(religion.getName())) {
                this.religions.put(religion.getName(), new Religion(null, religion));
            }
        });
    }
}
