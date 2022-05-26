package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.model.game.Game;
import fr.osallek.eu4parser.model.game.NativeAdvancement;
import fr.osallek.eu4parser.model.game.NativeAdvancements;

import java.util.List;

public class SaveNativeAdvancement {

    private final ClausewitzList list;

    private final SaveNativeAdvancements saveNativeAdvancements;

    private final NativeAdvancements nativeAdvancements;

    public SaveNativeAdvancement(ClausewitzList list, SaveNativeAdvancements saveNativeAdvancements, Game game) {
        this.saveNativeAdvancements = saveNativeAdvancements;
        this.list = list;
        this.nativeAdvancements = game.getNativeAdvancements(this.list.getName());
    }

    public String getName() {
        return this.list.getName();
    }

    public SaveNativeAdvancements getSaveNativeAdvancements() {
        return saveNativeAdvancements;
    }

    public List<NativeAdvancement> getEmbracedNativeAdvancements() {
        return this.nativeAdvancements.getNativeAdvancements().stream().filter(this::getEmbracedNativeAdvancement).toList();
    }

    public List<NativeAdvancement> getNotEmbracedNativeAdvancements() {
        return this.nativeAdvancements.getNativeAdvancements().stream().filter(index -> !this.getEmbracedNativeAdvancement(index)).toList();
    }

    public boolean getEmbracedNativeAdvancement(NativeAdvancement nativeAdvancement) {
        return 1 == this.list.getAsInt(nativeAdvancement.getIndex());
    }

    public boolean getEmbracedNativeAdvancement(int nativeAdvancement) {
        return 1 == this.list.getAsInt(nativeAdvancement);
    }

    public long getNbEmbracedNativeAdvancements() {
        return getEmbracedNativeAdvancements().size();
    }

    public void embracedNativeAdvancement(int nativeAdvancement, boolean embraced) {
        this.list.set(nativeAdvancement, embraced ? 1 : 0);
    }
}
