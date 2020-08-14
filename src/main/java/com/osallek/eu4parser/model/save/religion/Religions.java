package com.osallek.eu4parser.model.save.religion;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.save.Save;
import com.osallek.eu4parser.model.save.SaveReligion;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Religions {

    private final ClausewitzItem religionsItem;

    private final ClausewitzItem religionInstanceDataItem;

    private final Save save;

    private Map<String, SaveReligion> religions;

    public Religions(ClausewitzItem religionsItem, ClausewitzItem religionInstanceDataItem, Save save) {
        this.religionsItem = religionsItem;
        this.religionInstanceDataItem = religionInstanceDataItem;
        this.save = save;

        refreshAttributes();
    }

    public SaveReligion getReligion(String name) {
        return this.religions.get(name);
    }

    public Map<String, SaveReligion> getReligions() {
        return religions;
    }

    private void refreshAttributes() {
        this.religions = this.religionsItem.getChildren()
                                           .stream()
                                           .map(item -> new SaveReligion(item, religionInstanceDataItem.getChild(item.getName()), this.save))
                                           .collect(Collectors.toMap(SaveReligion::getName, Function.identity(), (e1, e2) -> e1, LinkedHashMap::new));

        this.religionInstanceDataItem.getChildren().forEach(religion -> {
            if (!this.religions.containsKey(religion.getName())) {
                this.religions.put(religion.getName(), new SaveReligion(null, religion, this.save));
            }
        });
    }
}
