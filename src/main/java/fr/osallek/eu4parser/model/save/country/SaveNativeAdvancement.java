package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.model.game.Game;
import fr.osallek.eu4parser.model.game.NativeAdvancement;

import java.util.List;

public class SaveNativeAdvancement {

    private final ClausewitzList list;

    private final SaveNativeAdvancements saveNativeAdvancements;

    public SaveNativeAdvancement(ClausewitzList list, SaveNativeAdvancements saveNativeAdvancements) {
        this.saveNativeAdvancements = saveNativeAdvancements;
        this.list = list;
    }

    public String getName() {
        return this.list.getName();
    }

    public SaveNativeAdvancements getSaveNativeAdvancements() {
        return saveNativeAdvancements;
    }

    public List<NativeAdvancement> getEmbracedNativeAdvancements(Game game) {
        return game.getNativeAdvancements(this.list.getName()).getNativeAdvancements().stream().filter(this::getEmbracedNativeAdvancement).toList();
    }

    public List<NativeAdvancement> getNotEmbracedNativeAdvancements(Game game) {
        return game.getNativeAdvancements(this.list.getName())
                   .getNativeAdvancements()
                   .stream()
                   .filter(index -> !this.getEmbracedNativeAdvancement(index))
                   .toList();
    }

    public boolean getEmbracedNativeAdvancement(NativeAdvancement nativeAdvancement) {
        return 1 == this.list.getAsInt(nativeAdvancement.getIndex());
    }

    public boolean getEmbracedNativeAdvancement(int nativeAdvancement) {
        return 1 == this.list.getAsInt(nativeAdvancement);
    }

    public long getNbEmbracedNativeAdvancements(Game game) {
        return getEmbracedNativeAdvancements(game).size();
    }

    public void embracedNativeAdvancement(int nativeAdvancement, boolean embraced) {
        this.list.set(nativeAdvancement, embraced ? 1 : 0);
    }
}
