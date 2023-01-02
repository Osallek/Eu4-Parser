package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import org.apache.commons.lang3.BooleanUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CustomizableLocalization extends Nodded {

    private final ClausewitzItem item;

    private final Game game;

    public CustomizableLocalization(ClausewitzItem item, Game game, FileNode fileNode) {
        super(fileNode);
        this.item = item;
        this.game = game;
    }

    public String getName() {
        return this.item.getVarAsString("name");
    }

    public void setName(String name) {
        this.item.setVariable("name", name);
    }

    public boolean random() {
        return BooleanUtils.toBooleanDefaultIfNull(this.item.getVarAsBool("random"), true);
    }

    public void setRandom(boolean random) {
        this.item.setVariable("random", random);
    }

    public List<CustomizableLocalizationText> getTexts() {
        return this.item.getChildren("text").stream().map(CustomizableLocalizationText::new).toList();
    }

    public Game getGame() {
        return game;
    }

    @Override
    public void write(BufferedWriter writer) throws IOException {
        this.item.write(writer, true, 0, new HashMap<>());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof CustomizableLocalization customizableLocalization)) {
            return false;
        }

        return Objects.equals(getName(), customizableLocalization.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return getName();
    }
}
