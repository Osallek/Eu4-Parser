package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.model.Localised;
import fr.osallek.eu4parser.model.game.condition.ConditionAnd;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Building extends Nodded implements Localised {

    private final ClausewitzItem item;

    private final Game game;

    private Path writenTo;

    public Building(ClausewitzItem item, Game game, FileNode fileNode) {
        super(fileNode);
        this.item = item;
        this.game = game;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    @Override
    public String getLocalisationKey() {
        return "building_" + getName();
    }

    public String getSpriteName() {
        return "GFX_" + getName();
    }

    public SpriteType getSprite() {
        return this.game.getSpriteType(getSpriteName());
    }

    public File getImage() {
        return this.game.getBuildingFlagImage(this);
    }

    public Optional<Integer> getCost() {
        return this.item.getVarAsInt("cost");
    }

    public void setCost(Integer cost) {
        if (cost == null) {
            this.item.removeVariable("cost");
        } else {
            this.item.setVariable("cost", cost);
        }
    }

    public Optional<Integer> getTime() {
        return this.item.getVarAsInt("time");
    }

    public void setTime(Integer time) {
        if (time == null) {
            this.item.removeVariable("time");
        } else {
            this.item.setVariable("time", time);
        }
    }

    public Optional<String> getMakeObsolete() {
        return this.item.getVarAsString("make_obsolete").filter(StringUtils::isNotBlank);
    }

    public void setMakeObsolete(String makeObsolete) {
        if (StringUtils.isBlank(makeObsolete)) {
            this.item.removeVariable("make_obsolete");
        } else {
            this.item.setVariable("make_obsolete", makeObsolete);
        }
    }

    public boolean makeObsolete() {
        return getMakeObsolete().isPresent();
    }

    public Optional<Building> getMakeObsoleteBuilding() {
        return getMakeObsolete().map(this.game::getBuilding);
    }

    public Optional<Boolean> onePerCountry() {
        return this.item.getVarAsBool("one_per_country");
    }

    public void setOnePerCountry(Boolean oncePerCountry) {
        if (oncePerCountry == null) {
            this.item.removeVariable("one_per_country");
        } else {
            this.item.setVariable("one_per_country", oncePerCountry);
        }
    }

    public Optional<Boolean> allowInGoldProvince() {
        return this.item.getVarAsBool("allow_in_gold_provinces");
    }

    public void setAllowInGoldProvinces(Boolean allowInGoldProvince) {
        if (allowInGoldProvince == null) {
            this.item.removeVariable("allow_in_gold_provinces");
        } else {
            this.item.setVariable("allow_in_gold_provinces", allowInGoldProvince);
        }
    }

    public Optional<Boolean> indestructible() {
        return this.item.getVarAsBool("indestructible");
    }

    public void setIndestructible(Boolean indestructible) {
        if (indestructible == null) {
            this.item.removeVariable("indestructible");
        } else {
            this.item.setVariable("indestructible", indestructible);
        }
    }

    public Optional<Boolean> onMap() {
        return this.item.getVarAsBool("onmap");
    }

    public void setOnMap(Boolean onMap) {
        if (onMap == null) {
            this.item.removeVariable("onmap");
        } else {
            this.item.setVariable("onmap", onMap);
        }
    }

    public Optional<Boolean> influencingFort() {
        return this.item.getVarAsBool("influencing_fort");
    }

    public void setInfluencingFort(Boolean influencingFort) {
        if (influencingFort == null) {
            this.item.removeVariable("influencing_fort");
        } else {
            this.item.setVariable("influencing_fort", influencingFort);
        }
    }

    public List<String> getManufactoryFor() {
        return this.item.getList("manufactory").map(ClausewitzList::getValues).orElse(new ArrayList<>());
    }

    public void setManufactoryFor(List<String> manufactoryFor) {
        if (CollectionUtils.isEmpty(manufactoryFor)) {
            this.item.removeList("manufactory");
            return;
        }

        this.item.getList("manufactory")
                 .ifPresentOrElse(list -> list.setAll(manufactoryFor.stream().filter(Objects::nonNull).toArray(String[]::new)),
                                  () -> this.item.addList("manufactory", manufactoryFor.stream().filter(Objects::nonNull).toArray(String[]::new)));
    }

    public List<String> getBonusManufactory() {
        return this.item.getList("bonus_manufactory").map(ClausewitzList::getValues).orElse(new ArrayList<>());
    }

    public void setBonusManufactory(List<String> bonusManufactory) {
        if (CollectionUtils.isEmpty(bonusManufactory)) {
            this.item.removeList("bonus_manufactory");
            return;
        }

        this.item.getList("bonus_manufactory")
                 .ifPresentOrElse(list -> list.setAll(bonusManufactory.stream().filter(Objects::nonNull).toArray(String[]::new)),
                                  () -> this.item.addList("bonus_manufactory", bonusManufactory.stream().filter(Objects::nonNull).toArray(String[]::new)));
    }

    public Optional<Boolean> governmentSpecific() {
        return this.item.getVarAsBool("government_specific");
    }

    public void setGovernmentSpecific(Boolean governmentSpecific) {
        if (governmentSpecific == null) {
            this.item.removeVariable("government_specific");
        } else {
            this.item.setVariable("government_specific", governmentSpecific);
        }
    }

    public Optional<Boolean> showSeparate() {
        return this.item.getVarAsBool("show_separate");
    }

    public void setShowSeparate(Boolean showSeparate) {
        if (showSeparate == null) {
            this.item.removeVariable("show_separate");
        } else {
            this.item.setVariable("show_separate", showSeparate);
        }
    }

    public Optional<Modifiers> getModifiers() {
        return this.item.getChild("modifier").map(Modifiers::new);
    }

    public boolean onlyInPort() {
        return getTrigger().map(condition -> "yes".equals(condition.getCondition("has_port"))).orElse(false);
    }

    public boolean onlyNative() {
        return getTrigger().map(t -> t.getScopes("owner"))
                           .filter(CollectionUtils::isNotEmpty)
                           .map(conditions -> conditions.stream()
                                                        .anyMatch(c -> MapUtils.isNotEmpty(c.getConditions()) && c.getCondition("government") != null
                                                                       && c.getConditions().get("government").stream().anyMatch("native"::equals)))
                           .orElse(false);
    }

    public Optional<ConditionAnd> getTrigger() {
        return this.item.getChild("build_trigger").map(ConditionAnd::new);
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
    public void write(BufferedWriter writer) throws IOException {
        this.item.write(writer, true, 0, new HashMap<>());
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Building)) {
            return false;
        }

        Building building = (Building) o;

        return Objects.equals(getName(), building.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
