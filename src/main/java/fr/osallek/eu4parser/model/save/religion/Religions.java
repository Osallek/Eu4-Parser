package fr.osallek.eu4parser.model.save.religion;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.Save;
import fr.osallek.eu4parser.model.save.SaveReligion;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Religions {

    private final ClausewitzItem religionsItem;

    private final ClausewitzItem religionInstanceDataItem;

    private final Save save;

    public Religions(ClausewitzItem religionsItem, ClausewitzItem religionInstanceDataItem, Save save) {
        this.religionsItem = religionsItem;
        this.religionInstanceDataItem = religionInstanceDataItem;
        this.save = save;
    }

    public SaveReligion getReligion(String name) {
        return getReligions().get(name);
    }

    public Map<String, SaveReligion> getReligions() {
        Map<String, SaveReligion> religions = this.religionsItem.getChildren()
                                                                .stream()
                                                                .map(item -> new SaveReligion(item, religionInstanceDataItem.getChild(item.getName()),
                                                                                              this.save))
                                                                .collect(Collectors.toMap(SaveReligion::getName, Function.identity(), (e1, e2) -> e1,
                                                                                          LinkedHashMap::new));

        this.religionInstanceDataItem.getChildren().forEach(religion -> {
            if (!religions.containsKey(religion.getName())) {
                religions.put(religion.getName(), new SaveReligion(null, religion, this.save));
            }
        });

        return religions;
    }
}
