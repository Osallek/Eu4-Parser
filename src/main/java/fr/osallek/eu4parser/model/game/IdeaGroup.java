package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.model.Power;
import fr.osallek.eu4parser.model.game.condition.ConditionAnd;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class IdeaGroup {

    private final ClausewitzItem item;

    private final Game game;

    private Path writenTo;

    public IdeaGroup(ClausewitzItem item, Game game) {
        this.item = item;
        this.game = game;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public Power getCategory() {
        ClausewitzVariable variable = item.getVar("category");
        return variable == null ? null : Power.byName(variable.getValue());
    }

    public void setCategory(Power power) {
        this.item.setVariable("category", power.name());
    }

    public boolean isFree() {
        return BooleanUtils.toBoolean(this.item.getVarAsBool("free"));
    }

    public void setFree(Boolean free) {
        if (free == null) {
            this.item.removeVariable("free");
        } else {
            this.item.setVariable("free", free);
        }
    }

    public ConditionAnd getTrigger() {
        ClausewitzItem child = this.item.getChild("trigger");
        return child == null ? null : new ConditionAnd(child);
    }

    public Modifiers getStart() {
        return new Modifiers(this.item.getChild("start"));
    }

    public Modifiers getBonus() {
        return new Modifiers(this.item.getChild("bonus"));
    }

    public Map<String, Modifiers> getIdeas() {
        return this.item.getChildrenNot("start", "bonus", "trigger", "ai_will_do")
                        .stream()
                        .collect(Collectors.toMap(ClausewitzItem::getName, Modifiers::new, (a, b) -> b, LinkedHashMap::new));
    }

    public Double getModifier(int level, Modifier modifier) {
        List<Double> modifiers = new ArrayList<>();
        Map<String, Modifiers> ideas = getIdeas();
        level = Math.min(level, ideas.size());

        if (level >= 0 && getStart().hasModifier(modifier)) {
            modifiers.add(getStart().getModifier(modifier));
        }

        Iterator<Modifiers> modifiersIterator = ideas.values().iterator();
        for (int i = level; i > 0; i--) {
            Modifiers m = modifiersIterator.next();

            if (m.hasModifier(modifier)) {
                modifiers.add(m.getModifier(modifier));
            }
        }

        if (level >= ideas.size() && getBonus().hasModifier(modifier)) {
            modifiers.add(getBonus().getModifier(modifier));
        }

        return ModifiersUtils.sumModifiers(modifier, modifiers);
    }

    public int getLevel(String idea) {
        List<String> ideas = new ArrayList<>(getIdeas().keySet());

        for (int i = 0; i < ideas.size(); i++) {
            if (ideas.get(i).equals(idea)) {
                return i + 1;
            }
        }

        return 0;
    }

    public File getImage() {
        return this.game.getSpriteTypeImageFile("GFX_idea_" + getName());
    }

    public void writeImageTo(Path dest) throws IOException {
        FileUtils.forceMkdirParent(dest.toFile());
        ImageIO.write(ImageIO.read(getImage()), "png", dest.toFile());
        Eu4Utils.optimizePng(dest, dest);
        this.writenTo = dest;
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

        if (!(o instanceof IdeaGroup ideaGroup)) {
            return false;
        }

        return Objects.equals(getName(), ideaGroup.getName());
    }

    public Game getGame() {
        return game;
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
