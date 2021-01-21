package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.Game;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SaveNativeAdvancements {

    private final Game game;

    private final ClausewitzItem item;

    private Map<String, SaveNativeAdvancement> nativeAdvancements;

    public SaveNativeAdvancements(ClausewitzItem item, Game game) {
        this.item = item;
        this.game = game;
        refreshAttributes();
    }

    public Map<String, SaveNativeAdvancement> getNativeAdvancements() {
        return nativeAdvancements;
    }

    private void refreshAttributes() {
        this.nativeAdvancements = this.item.getLists()
                                           .stream()
                                           .map(list -> new SaveNativeAdvancement(list, this, this.game))
                                           .collect(Collectors.toMap(SaveNativeAdvancement::getName, Function.identity()));
    }
}
