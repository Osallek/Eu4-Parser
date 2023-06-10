package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.common.ImageReader;
import fr.osallek.eu4parser.model.Color;
import fr.osallek.eu4parser.model.game.condition.ConditionAnd;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Religion {

    private final ReligionGroup religionGroup;

    private final ClausewitzItem item;

    private Path writenTo;

    public Religion(ClausewitzItem item, ReligionGroup religionGroup) {
        this.item = item;
        this.religionGroup = religionGroup;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public Optional<Color> getColor() {
        if (this.item == null) {
            return Optional.empty();
        }

        return this.item.getList("color").map(Color::new);
    }

    public void setColor(Color color) {
        if (color == null) {
            this.item.removeList("color");
            return;
        }

        this.item.getList("color").ifPresentOrElse(list -> {
            Color actualColor = new Color(list, true);
            actualColor.setRed(color.getRed());
            actualColor.setGreen(color.getGreen());
            actualColor.setBlue(color.getBlue());
        }, () -> Color.addToItem(this.item, "color", color));
    }

    public Optional<Integer> getIcon() {
        return this.item.getVarAsInt("icon");
    }

    public void setIcon(Integer icon) {
        if (icon == null) {
            this.item.removeVariable("icon");
        } else {
            this.item.setVariable("icon", icon);
        }
    }

    public boolean isHreReligion() {
        return this.item.getVarAsBool("hre_religion").map(BooleanUtils::toBoolean).orElse(false);
    }

    public void setHreReligion(Boolean hreReligion) {
        if (hreReligion == null) {
            this.item.removeVariable("hre_religion");
        } else {
            this.item.setVariable("hre_religion", hreReligion);
        }
    }

    public boolean isHreHereticReligion() {
        return this.item.getVarAsBool("hre_heretic_religion").map(BooleanUtils::toBoolean).orElse(false);
    }

    public void setHreHereticReligion(Boolean hreHereticReligion) {
        if (hreHereticReligion == null) {
            this.item.removeVariable("hre_heretic_religion");
        } else {
            this.item.setVariable("hre_heretic_religion", hreHereticReligion);
        }
    }

    public boolean isUseAuthority() {
        return this.item.getVarAsBool("use_authority").map(BooleanUtils::toBoolean).orElse(false);
    }

    public void setUseAuthority(Boolean useAuthority) {
        if (useAuthority == null) {
            this.item.removeVariable("use_authority");
        } else {
            this.item.setVariable("use_authority", useAuthority);
        }
    }

    public boolean useReligiousReforms() {
        return this.item.getVarAsBool("use_religious_reforms").map(BooleanUtils::toBoolean).orElse(false);
    }

    public void setUseReligiousReforms(Boolean useReligiousReforms) {
        if (useReligiousReforms == null) {
            this.item.removeVariable("use_religious_reforms");
        } else {
            this.item.setVariable("use_religious_reforms", useReligiousReforms);
        }
    }

    public boolean usesAnglicanPower() {
        return this.item.getVarAsBool("uses_anglican_power").map(BooleanUtils::toBoolean).orElse(false);
    }

    public void setUsesAnglicanPower(Boolean usesAnglicanPower) {
        if (usesAnglicanPower == null) {
            this.item.removeVariable("uses_anglican_power");
        } else {
            this.item.setVariable("uses_anglican_power", usesAnglicanPower);
        }
    }

    public boolean usesHussitePower() {
        return this.item.getVarAsBool("uses_hussite_power").map(BooleanUtils::toBoolean).orElse(false);
    }

    public void setUsesHussitePower(Boolean usesHussitePower) {
        if (usesHussitePower == null) {
            this.item.removeVariable("uses_hussite_power");
        } else {
            this.item.setVariable("uses_hussite_power", usesHussitePower);
        }
    }

    public boolean usesChurchPower() {
        return this.item.getVarAsBool("uses_church_power").map(BooleanUtils::toBoolean).orElse(false);
    }

    public void setUsesChurchPower(Boolean usesChurchPower) {
        if (usesChurchPower == null) {
            this.item.removeVariable("uses_church_power");
        } else {
            this.item.setVariable("uses_church_power", usesChurchPower);
        }
    }

    public boolean useFervor() {
        return this.item.getVarAsBool("use_fervor").map(BooleanUtils::toBoolean).orElse(false);
    }

    public void setUseFervor(Boolean useFervor) {
        if (useFervor == null) {
            this.item.removeVariable("use_fervor");
        } else {
            this.item.setVariable("use_fervor", useFervor);
        }
    }

    public boolean hasPatriarchs() {
        return this.item.getVarAsBool("has_patriarchs").map(BooleanUtils::toBoolean).orElse(false);
    }

    public void setHasPatriarchs(Boolean hasPatriarchs) {
        if (hasPatriarchs == null) {
            this.item.removeVariable("has_patriarchs");
        } else {
            this.item.setVariable("has_patriarchs", hasPatriarchs);
        }
    }

    public boolean misguidedHeretic() {
        return this.item.getVarAsBool("misguided_heretic").map(BooleanUtils::toBoolean).orElse(false);
    }

    public void setMisguidedHeretic(Boolean misguidedHeretic) {
        if (misguidedHeretic == null) {
            this.item.removeVariable("misguided_heretic");
        } else {
            this.item.setVariable("misguided_heretic", misguidedHeretic);
        }
    }

    public boolean useFetishistCult() {
        return this.item.getVarAsBool("fetishist_cult").map(BooleanUtils::toBoolean).orElse(false);
    }

    public void setUseFetishistCult(Boolean useFetishistCult) {
        if (useFetishistCult == null) {
            this.item.removeVariable("fetishist_cult");
        } else {
            this.item.setVariable("fetishist_cult", useFetishistCult);
        }
    }

    public boolean useDoom() {
        return this.item.getVarAsBool("doom").map(BooleanUtils::toBoolean).orElse(false);
    }

    public void setDoom(Boolean doom) {
        if (doom == null) {
            this.item.removeVariable("doom");
        } else {
            this.item.setVariable("doom", doom);
        }
    }

    public boolean usePersonalDeity() {
        return this.item.getVarAsBool("personal_deity").map(BooleanUtils::toBoolean).orElse(false);
    }

    public void setUsePersonalDeity(Boolean usePersonalDeity) {
        if (usePersonalDeity == null) {
            this.item.removeVariable("personal_deity");
        } else {
            this.item.setVariable("personal_deity", usePersonalDeity);
        }
    }

    public boolean usesIsolationism() {
        return this.item.getVarAsBool("uses_isolationism").map(BooleanUtils::toBoolean).orElse(false);
    }

    public void setUsesIsolationism(Boolean usesIsolationism) {
        if (usesIsolationism == null) {
            this.item.removeVariable("uses_isolationism");
        } else {
            this.item.setVariable("uses_isolationism", usesIsolationism);
        }
    }

    public boolean usesKarma() {
        return this.item.getVarAsBool("uses_karma").map(BooleanUtils::toBoolean).orElse(false);
    }

    public void setUsesKarma(Boolean usesKarma) {
        if (usesKarma == null) {
            this.item.removeVariable("uses_karma");
        } else {
            this.item.setVariable("uses_karma", usesKarma);
        }
    }

    public boolean usesPiety() {
        return this.item.getVarAsBool("uses_piety").map(BooleanUtils::toBoolean).orElse(false);
    }

    public void setUsesPiety(Boolean usesPiety) {
        if (usesPiety == null) {
            this.item.removeVariable("uses_piety");
        } else {
            this.item.setVariable("uses_piety", usesPiety);
        }
    }

    public boolean usesHarmony() {
        return this.item.getVarAsBool("uses_harmony").map(BooleanUtils::toBoolean).orElse(false);
    }

    public void setUsesHarmony(Boolean usesHarmony) {
        if (usesHarmony == null) {
            this.item.removeVariable("uses_harmony");
        } else {
            this.item.setVariable("uses_harmony", usesHarmony);
        }
    }

    public boolean canHaveSecondaryReligion() {
        return this.item.getVarAsBool("can_have_secondary_religion").map(BooleanUtils::toBoolean).orElse(false);
    }

    public void setCanHaveSecondaryReligion(Boolean canHaveSecondaryReligion) {
        if (canHaveSecondaryReligion == null) {
            this.item.removeVariable("can_have_secondary_religion");
        } else {
            this.item.setVariable("can_have_secondary_religion", canHaveSecondaryReligion);
        }
    }

    public List<String> getAllowedCenterConversion() {
        return this.item.getList("allowed_center_conversion").map(ClausewitzList::getValues).orElse(new ArrayList<>());
    }

    public void setAllowedCenterConversion(List<String> allowedCenterConversion) {
        if (CollectionUtils.isEmpty(allowedCenterConversion)) {
            this.item.removeList("allowed_center_conversion");
            return;
        }

        this.item.getList("allowed_center_conversion")
                 .ifPresentOrElse(list -> list.setAll(allowedCenterConversion.stream().filter(Objects::nonNull).toArray(String[]::new)),
                                  () -> this.item.addList("allowed_center_conversion",
                                                          allowedCenterConversion.stream().filter(Objects::nonNull).toArray(String[]::new)));
    }

    public List<String> getAspects() {
        return this.item.getList("aspects").map(ClausewitzList::getValues).orElse(new ArrayList<>());
    }

    public void setAspects(List<String> aspects) {
        if (CollectionUtils.isEmpty(aspects)) {
            this.item.removeList("aspects");
            return;
        }

        this.item.getList("aspects")
                 .ifPresentOrElse(list -> list.setAll(aspects.stream().filter(Objects::nonNull).toArray(String[]::new)),
                                  () -> this.item.addList("aspects", aspects.stream().filter(Objects::nonNull).toArray(String[]::new)));
    }

    public List<String> getBlessings() {
        return this.item.getList("blessings").map(ClausewitzList::getValues).orElse(new ArrayList<>());
    }

    public void setBlessings(List<String> blessings) {
        if (CollectionUtils.isEmpty(blessings)) {
            this.item.removeList("blessings");
            return;
        }

        this.item.getList("blessings")
                 .ifPresentOrElse(list -> list.setAll(blessings.stream().filter(Objects::nonNull).toArray(String[]::new)),
                                  () -> this.item.addList("blessings", blessings.stream().filter(Objects::nonNull).toArray(String[]::new)));
    }

    public List<String> getHeretic() {
        return this.item.getList("heretic").map(ClausewitzList::getValues).orElse(new ArrayList<>());
    }

    public void setHeretic(List<String> heretic) {
        if (CollectionUtils.isEmpty(heretic)) {
            this.item.removeList("heretic");
            return;
        }

        this.item.getList("heretic")
                 .ifPresentOrElse(list -> list.setAll(heretic.stream().filter(Objects::nonNull).toArray(String[]::new)),
                                  () -> this.item.addList("heretic", heretic.stream().filter(Objects::nonNull).toArray(String[]::new)));
    }

    public List<String> getHolySites() {
        return this.item.getList("holy_sites").map(ClausewitzList::getValues).orElse(new ArrayList<>());
    }

    public void setHolySites(List<String> holySites) {
        if (CollectionUtils.isEmpty(holySites)) {
            this.item.removeList("holy_sites");
            return;
        }

        this.item.getList("holy_sites")
                 .ifPresentOrElse(list -> list.setAll(holySites.stream().filter(Objects::nonNull).toArray(String[]::new)),
                                  () -> this.item.addList("holy_sites", holySites.stream().filter(Objects::nonNull).toArray(String[]::new)));
    }

    public Optional<LocalDate> getDate() {
        return this.item.getVarAsDate("date");
    }

    public void setDate(LocalDate date) {
        if (date == null) {
            this.item.removeVariable("date");
        } else {
            this.item.setVariable("date", date);
        }
    }

    public Optional<Papacy> getPapacy() {
        return this.item.getChild("papacy").map(Papacy::new);
    }

    public List<Icon> getIcons() {
        return this.item.getChild("orthodox_icons").map(ClausewitzItem::getChildren).stream().flatMap(Collection::stream).map(Icon::new).toList();
    }

    public ReligionGroup getReligionGroup() {
        return religionGroup;
    }

    public Optional<Modifiers> getCountry() {
        return this.item.getChild("country").map(Modifiers::new);
    }

    public Optional<Modifiers> getCountryAsSecondary() {
        return this.item.getChild("country_as_secondary").map(Modifiers::new);
    }

    public Optional<ConditionAnd> getWillGetCenter() {
        return this.item.getChild("will_get_center").map(ConditionAnd::new);
    }

    public Optional<String> getHarmonizedModifier() {
        return this.item.getVarAsString("harmonized_modifier");
    }

    public void setHarmonizedModifier(String harmonizedModifier) {
        if (StringUtils.isBlank(harmonizedModifier)) {
            this.item.removeVariable("harmonized_modifier");
        } else {
            this.item.setVariable("harmonized_modifier", harmonizedModifier);
        }
    }

    public Optional<BufferedImage> getImage() throws IOException {
        return getSubImage(ImageReader.convertFileToImage(this.religionGroup.getGame().getReligionsImage()));
    }

    public Optional<BufferedImage> getSubImage(BufferedImage image) {
        return getIcon().filter(integer -> this.religionGroup.getGame().getReligionsNbFrames().isPresent()).map(i -> {
            double size = (double) image.getWidth() / this.religionGroup.getGame().getReligionsNbFrames().get();
            return image.getSubimage((int) ((i - 1) * size), 0, (int) size, image.getHeight());
        });

    }

    public void writeImageTo(Path dest) throws IOException {
        Optional<BufferedImage> image = getImage();

        if (image.isPresent()) {
            FileUtils.forceMkdirParent(dest.toFile());
            ImageIO.write(image.get(), "png", dest.toFile());
            Eu4Utils.optimizePng(dest, dest);
            this.writenTo = dest;
        }
    }

    public Path getWritenTo() {
        return writenTo;
    }

    public void setWritenTo(Path writenTo) {
        this.writenTo = writenTo;
    }
}
