package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.common.ImageReader;
import fr.osallek.eu4parser.model.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;

public class Estate {

    private final ClausewitzItem item;

    private final Game game;

    private List<ModifierDefinition> modifierDefinitions;

    private Path writenTo;

    public Estate(ClausewitzItem item, List<ModifierDefinition> modifierDefinitions, Game game) {
        this.item = item;
        this.game = game;
        this.modifierDefinitions = modifierDefinitions;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public List<ModifierDefinition> getModifierDefinitions() {
        return modifierDefinitions;
    }

    public void setModifierDefinitions(List<ModifierDefinition> modifierDefinitions) {
        this.modifierDefinitions = modifierDefinitions;
    }

    public Integer getIcon() {
        return this.item.getVarAsInt("icon");
    }

    public void setIcon(Integer icon) {
        if (icon == null) {
            this.item.removeVariable("icon");
        } else {
            this.item.setVariable("icon", icon);
        }
    }

    public Condition getTrigger() {
        ClausewitzItem child = this.item.getChild("trigger");
        return child == null ? null : new Condition(child);
    }

    public Modifiers getCountryModifierHappy() {
        return new Modifiers(this.item.getChild("country_modifier_happy"));
    }

    public Modifiers getCountryModifierNeutral() {
        return new Modifiers(this.item.getChild("country_modifier_neutral"));
    }

    public Modifiers getCountryModifierAngry() {
        return new Modifiers(this.item.getChild("country_modifier_angry"));
    }

    public Modifiers getLandOwnershipModifier() {
        return new Modifiers(this.item.getChild("land_ownership_modifier"));
    }

    public Double getBaseInfluence() {
        return this.item.getVarAsDouble("base_influence");
    }

    public void setBaseInfluence(Double baseInfluence) {
        if (baseInfluence == null) {
            this.item.removeVariable("base_influence");
        } else {
            this.item.setVariable("base_influence", baseInfluence);
        }
    }

    public Double getInfluenceFromDevModifier() {
        return this.item.getVarAsDouble("influence_from_dev_modifier");
    }

    public void setInfluenceFromDevModifier(Double influenceFromDevModifier) {
        if (influenceFromDevModifier == null) {
            this.item.removeVariable("influence_from_dev_modifier");
        } else {
            this.item.setVariable("influence_from_dev_modifier", influenceFromDevModifier);
        }
    }

    public List<EstateModifier> getInfluenceModifiers() {
        return this.item.getChildren("influence_modifier")
                        .stream()
                        .map(child -> new EstateModifier(child, "influence"))
                        .toList();
    }

    public List<EstateModifier> getLoyaltyModifiers() {
        return this.item.getChildren("loyalty_modifier")
                        .stream()
                        .map(child -> new EstateModifier(child, "loyalty"))
                        .toList();
    }

    public List<Names> getColonialNames() {
        List<ClausewitzItem> names = item.getChildren("custom_name");
        return names.stream().map(Names::new).toList();
    }

    public Color getColor() {
        if (this.item == null) {
            return null;
        }

        ClausewitzList clausewitzList = this.item.getList("color");
        return clausewitzList == null ? null : new Color(clausewitzList);
    }

    public void setColor(Color color) {
        if (color == null) {
            this.item.removeList("color");
            return;
        }

        ClausewitzList list = this.item.getList("color");

        if (list != null) {
            Color actualColor = new Color(list);
            actualColor.setRed(color.getRed());
            actualColor.setGreen(color.getGreen());
            actualColor.setBlue(color.getBlue());
        } else {
            Color.addToItem(this.item, "color", color);
        }
    }

    public Boolean contributesToCuriaTreasury() {
        return this.item.getVarAsBool("contributes_to_curia_treasury");
    }

    public void setCanHaveSecondaryReligion(Boolean contributesToCuriaTreasury) {
        if (contributesToCuriaTreasury == null) {
            this.item.removeVariable("contributes_to_curia_treasury");
        } else {
            this.item.setVariable("contributes_to_curia_treasury", contributesToCuriaTreasury);
        }
    }

    public Map<String, EstatePrivilege> getPrivileges() {
        ClausewitzList list = this.item.getList("privileges");
        return list == null ? null :
               list.getValues().stream().map(this.game::getEstatePrivilege).collect(Collectors.toMap(EstatePrivilege::getName, Function.identity(),
                                                                                                     (a, b) -> b, LinkedHashMap::new));
    }

    public List<String> getAgendas() {
        ClausewitzList list = this.item.getList("agendas");
        return list == null ? null : list.getValues();
    }

    public void setAgendas(List<String> agendas) {
        if (CollectionUtils.isEmpty(agendas)) {
            this.item.removeList("agendas");
            return;
        }

        ClausewitzList list = this.item.getList("agendas");

        if (list != null) {
            list.setAll(agendas.stream().filter(Objects::nonNull).toArray(String[]::new));
        } else {
            this.item.addList("agendas", agendas.stream().filter(Objects::nonNull).toArray(String[]::new));
        }
    }

    public BufferedImage getImage() throws IOException {
        return ImageReader.convertFileToImage(this.game.getEstatesImage()).getSubimage((getIcon() - 1) * 47, 0, 47, 44);
    }

    public void writeImageTo(Path dest) throws IOException {
        FileUtils.forceMkdirParent(dest.toFile());
        ImageIO.write(getImage(), "png", dest.toFile());
        Eu4Utils.optimizePng(dest, dest);
        this.writenTo = dest;
    }

    public void setWritenTo(Path writenTo) {
        this.writenTo = writenTo;
    }

    public Path getWritenTo() {
        return writenTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Estate estate)) {
            return false;
        }

        return Objects.equals(getName(), estate.getName());
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
