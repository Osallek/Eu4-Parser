package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.condition.ConditionAnd;
import org.apache.commons.collections4.CollectionUtils;

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

    public Optional<ConditionAnd> getPotentialInviteScholar() {
        return this.item.getChild("potential_invite_scholar").map(ConditionAnd::new);
    }

    public Optional<ConditionAnd> getCanInviteScholar() {
        return this.item.getChild("can_invite_scholar").map(ConditionAnd::new);
    }

    public Optional<Modifiers> getOnInviteScholar() {
        return this.item.getChild("on_invite_scholar").map(Modifiers::new);
    }

    public Optional<String> getInviteScholarModifierDisplay() {
        return this.item.getVarAsString("invite_scholar_modifier_display");
    }

    public Optional<String> getPicture() {
        return this.item.getVarAsString("picture").map(ClausewitzUtils::removeQuotes);
    }

    public Optional<Modifiers> getModifiers() {
        return Optional.of(this.item.getVarsNot("picture", "invite_scholar_modifier_display")).filter(CollectionUtils::isNotEmpty).map(Modifiers::new);
    }

    public Optional<File> getImageFile() {
        return getPicture().map(this.game::getSpriteTypeImageFile);
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
