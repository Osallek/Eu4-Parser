package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SaveNativeAdvancements {

    private final ClausewitzItem item;

    public SaveNativeAdvancements(ClausewitzItem item) {
        this.item = item;
    }

    public Map<String, SaveNativeAdvancement> getNativeAdvancements() {
        return this.item.getLists()
                        .stream()
                        .map(list -> new SaveNativeAdvancement(list, this))
                        .collect(Collectors.toMap(SaveNativeAdvancement::getName, Function.identity()));
    }
}
