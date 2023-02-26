package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.effects.Effects;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

public class ReligiousSchool {

    private final ClausewitzItem item;

    private final Game game;

    private Path writenTo;

    public ReligiousSchool(ClausewitzItem item, Game game) {
        this.item = item;
        this.game = game;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public ConditionAnd getPotentialInviteScholar() {
        return Optional.ofNullable(this.item.getChild("potential_invite_scholar")).map(ConditionAnd::new).orElse(null);
    }

    public ConditionAnd getCanInviteScholar() {
        return Optional.ofNullable(this.item.getChild("can_invite_scholar")).map(ConditionAnd::new).orElse(null);
    }

    public Effects getOnInviteScholar() {
        return Optional.ofNullable(this.item.getChild("on_invite_scholar")).map(i -> new Effects(i, this.game)).orElse(null);
    }

    public String getInviteScholarModifierDisplay() {
        return this.item.getVarAsString("invite_scholar_modifier_display");
    }

    public String getPicture() {
        return ClausewitzUtils.removeQuotes(this.item.getVarAsString("picture"));
    }

    public Modifiers getModifiers() {
        return new Modifiers(this.item.getVarsNot("picture", "invite_scholar_modifier_display"));
    }

    public File getImageFile() {
        return this.game.getSpriteTypeImageFile(getPicture());
    }

    public Path getWritenTo() {
        return writenTo;
    }

    public void setWritenTo(Path writenTo) {
        this.writenTo = writenTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ReligiousSchool religionGroup)) {
            return false;
        }

        return Objects.equals(getName(), religionGroup.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
