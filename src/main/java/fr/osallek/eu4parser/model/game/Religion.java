package fr.osallek.eu4parser.model.game;

import com.googlecode.pngtastic.core.PngImage;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.common.Eu4MapUtils;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.common.ImageReader;
import fr.osallek.eu4parser.model.Color;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

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
            Color actualColor = new Color(list, true);
            actualColor.setRed(color.getRed());
            actualColor.setGreen(color.getGreen());
            actualColor.setBlue(color.getBlue());
        } else {
            Color.addToItem(this.item, "color", color);
        }
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

    public Boolean isHreReligion() {
        return this.item.getVarAsBool("hre_religion");
    }

    public void setHreReligion(Boolean hreReligion) {
        if (hreReligion == null) {
            this.item.removeVariable("hre_religion");
        } else {
            this.item.setVariable("hre_religion", hreReligion);
        }
    }

    public Boolean isHreHereticReligion() {
        return this.item.getVarAsBool("hre_heretic_religion");
    }

    public void setHreHereticReligion(Boolean hreHereticReligion) {
        if (hreHereticReligion == null) {
            this.item.removeVariable("hre_heretic_religion");
        } else {
            this.item.setVariable("hre_heretic_religion", hreHereticReligion);
        }
    }

    public Boolean isUseAuthority() {
        return this.item.getVarAsBool("use_authority");
    }

    public void setUseAuthority(Boolean useAuthority) {
        if (useAuthority == null) {
            this.item.removeVariable("use_authority");
        } else {
            this.item.setVariable("use_authority", useAuthority);
        }
    }

    public Boolean useReligiousReforms() {
        return this.item.getVarAsBool("use_religious_reforms");
    }

    public void setUseReligiousReforms(Boolean useReligiousReforms) {
        if (useReligiousReforms == null) {
            this.item.removeVariable("use_religious_reforms");
        } else {
            this.item.setVariable("use_religious_reforms", useReligiousReforms);
        }
    }

    public Boolean usesAnglicanPower() {
        return this.item.getVarAsBool("uses_anglican_power");
    }

    public void setUsesAnglicanPower(Boolean usesAnglicanPower) {
        if (usesAnglicanPower == null) {
            this.item.removeVariable("uses_anglican_power");
        } else {
            this.item.setVariable("uses_anglican_power", usesAnglicanPower);
        }
    }

    public Boolean usesHussitePower() {
        return this.item.getVarAsBool("uses_hussite_power");
    }

    public void setUsesHussitePower(Boolean usesHussitePower) {
        if (usesHussitePower == null) {
            this.item.removeVariable("uses_hussite_power");
        } else {
            this.item.setVariable("uses_hussite_power", usesHussitePower);
        }
    }

    public Boolean usesChurchPower() {
        return this.item.getVarAsBool("uses_church_power");
    }

    public void setUsesChurchPower(Boolean usesChurchPower) {
        if (usesChurchPower == null) {
            this.item.removeVariable("uses_church_power");
        } else {
            this.item.setVariable("uses_church_power", usesChurchPower);
        }
    }

    public Boolean useFervor() {
        return this.item.getVarAsBool("use_fervor");
    }

    public void setUseFervor(Boolean useFervor) {
        if (useFervor == null) {
            this.item.removeVariable("use_fervor");
        } else {
            this.item.setVariable("use_fervor", useFervor);
        }
    }

    public Boolean hasPatriarchs() {
        return this.item.getVarAsBool("has_patriarchs");
    }

    public void setHasPatriarchs(Boolean hasPatriarchs) {
        if (hasPatriarchs == null) {
            this.item.removeVariable("has_patriarchs");
        } else {
            this.item.setVariable("has_patriarchs", hasPatriarchs);
        }
    }

    public Boolean misguidedHeretic() {
        return this.item.getVarAsBool("misguided_heretic");
    }

    public void setMisguidedHeretic(Boolean misguidedHeretic) {
        if (misguidedHeretic == null) {
            this.item.removeVariable("misguided_heretic");
        } else {
            this.item.setVariable("misguided_heretic", misguidedHeretic);
        }
    }

    public Boolean useFetishistCult() {
        return this.item.getVarAsBool("fetishist_cult");
    }

    public void setUseFetishistCult(Boolean useFetishistCult) {
        if (useFetishistCult == null) {
            this.item.removeVariable("fetishist_cult");
        } else {
            this.item.setVariable("fetishist_cult", useFetishistCult);
        }
    }

    public Boolean useDoom() {
        return this.item.getVarAsBool("doom");
    }

    public void setDoom(Boolean doom) {
        if (doom == null) {
            this.item.removeVariable("doom");
        } else {
            this.item.setVariable("doom", doom);
        }
    }

    public Boolean usePersonalDeity() {
        return this.item.getVarAsBool("personal_deity");
    }

    public void setUsePersonalDeity(Boolean usePersonalDeity) {
        if (usePersonalDeity == null) {
            this.item.removeVariable("personal_deity");
        } else {
            this.item.setVariable("personal_deity", usePersonalDeity);
        }
    }

    public Boolean usesIsolationism() {
        return this.item.getVarAsBool("uses_isolationism");
    }

    public void setUsesIsolationism(Boolean usesIsolationism) {
        if (usesIsolationism == null) {
            this.item.removeVariable("uses_isolationism");
        } else {
            this.item.setVariable("uses_isolationism", usesIsolationism);
        }
    }

    public Boolean usesKarma() {
        return this.item.getVarAsBool("uses_karma");
    }

    public void setUsesKarma(Boolean usesKarma) {
        if (usesKarma == null) {
            this.item.removeVariable("uses_karma");
        } else {
            this.item.setVariable("uses_karma", usesKarma);
        }
    }

    public Boolean usesPiety() {
        return this.item.getVarAsBool("uses_piety");
    }

    public void setUsesPiety(Boolean usesPiety) {
        if (usesPiety == null) {
            this.item.removeVariable("uses_piety");
        } else {
            this.item.setVariable("uses_piety", usesPiety);
        }
    }

    public Boolean usesHarmony() {
        return this.item.getVarAsBool("uses_harmony");
    }

    public void setUsesHarmony(Boolean usesHarmony) {
        if (usesHarmony == null) {
            this.item.removeVariable("uses_harmony");
        } else {
            this.item.setVariable("uses_harmony", usesHarmony);
        }
    }

    public Boolean canHaveSecondaryReligion() {
        return this.item.getVarAsBool("can_have_secondary_religion");
    }

    public void setCanHaveSecondaryReligion(Boolean canHaveSecondaryReligion) {
        if (canHaveSecondaryReligion == null) {
            this.item.removeVariable("can_have_secondary_religion");
        } else {
            this.item.setVariable("can_have_secondary_religion", canHaveSecondaryReligion);
        }
    }

    public List<String> getAllowedCenterConversion() {
        ClausewitzList list = this.item.getList("allowed_center_conversion");
        return list == null ? null : list.getValues();
    }

    public void setAllowedCenterConversion(List<String> allowedCenterConversion) {
        if (CollectionUtils.isEmpty(allowedCenterConversion)) {
            this.item.removeList("allowed_center_conversion");
            return;
        }

        ClausewitzList list = this.item.getList("allowed_center_conversion");

        if (list != null) {
            list.setAll(allowedCenterConversion.stream().filter(Objects::nonNull).toArray(String[]::new));
        } else {
            this.item.addList("allowed_center_conversion", allowedCenterConversion.stream().filter(Objects::nonNull).toArray(String[]::new));
        }
    }

    public List<String> getAspects() {
        ClausewitzList list = this.item.getList("aspects");
        return list == null ? null : list.getValues();
    }

    public void setAspects(List<String> aspects) {
        if (CollectionUtils.isEmpty(aspects)) {
            this.item.removeList("aspects");
            return;
        }

        ClausewitzList list = this.item.getList("aspects");

        if (list != null) {
            list.setAll(aspects.stream().filter(Objects::nonNull).toArray(String[]::new));
        } else {
            this.item.addList("aspects", aspects.stream().filter(Objects::nonNull).toArray(String[]::new));
        }
    }

    public List<String> getBlessings() {
        ClausewitzList list = this.item.getList("blessings");
        return list == null ? null : list.getValues();
    }

    public void setBlessings(List<String> blessings) {
        if (CollectionUtils.isEmpty(blessings)) {
            this.item.removeList("blessings");
            return;
        }

        ClausewitzList list = this.item.getList("blessings");

        if (list != null) {
            list.setAll(blessings.stream().filter(Objects::nonNull).toArray(String[]::new));
        } else {
            this.item.addList("blessings", blessings.stream().filter(Objects::nonNull).toArray(String[]::new));
        }
    }

    public List<String> getHeretic() {
        ClausewitzList list = this.item.getList("heretic");
        return list == null ? null : list.getValues();
    }

    public void setHeretic(List<String> heretic) {
        if (CollectionUtils.isEmpty(heretic)) {
            this.item.removeList("heretic");
            return;
        }

        ClausewitzList list = this.item.getList("heretic");

        if (list != null) {
            list.setAll(heretic.stream().filter(Objects::nonNull).toArray(String[]::new));
        } else {
            this.item.addList("heretic", heretic.stream().filter(Objects::nonNull).toArray(String[]::new));
        }
    }

    public List<String> getHolySites() {
        ClausewitzList list = this.item.getList("holy_sites");
        return list == null ? null : list.getValues();
    }

    public void setHolySites(List<String> holySites) {
        if (CollectionUtils.isEmpty(holySites)) {
            this.item.removeList("holy_sites");
            return;
        }

        ClausewitzList list = this.item.getList("holy_sites");

        if (list != null) {
            list.setAll(holySites.stream().filter(Objects::nonNull).toArray(String[]::new));
        } else {
            this.item.addList("holy_sites", holySites.stream().filter(Objects::nonNull).toArray(String[]::new));
        }
    }

    public LocalDate getDate() {
        return this.item.getVarAsDate("date");
    }

    public void setDate(LocalDate date) {
        if (date == null) {
            this.item.removeVariable("date");
        } else {
            this.item.setVariable("date", date);
        }
    }

    public Papacy getPapacy() {
        ClausewitzItem child = this.item.getChild("papacy");
        return child == null ? null : new Papacy(child);
    }

    public List<Icon> getIcons() {
        ClausewitzItem child = this.item.getChild("orthodox_icons");
        return child == null ? null : child.getChildren().stream().map(Icon::new).toList();
    }

    public ReligionGroup getReligionGroup() {
        return religionGroup;
    }

    public Modifiers getCountry() {
        return new Modifiers(this.item.getChild("country"));
    }

    public Modifiers getCountryAsSecondary() {
        return new Modifiers(item.getChild("country_as_secondary"));
    }

    public Condition getWillGetCenter() {
        ClausewitzItem child = this.item.getChild("will_get_center");
        return child == null ? null : new Condition(child);
    }

    public String getHarmonizedModifier() {
        return this.item.getVarAsString("harmonized_modifier");
    }

    public void setHarmonizedModifier(String harmonizedModifier) {
        if (StringUtils.isBlank(harmonizedModifier)) {
            this.item.removeVariable("harmonized_modifier");
        } else {
            this.item.setVariable("harmonized_modifier", harmonizedModifier);
        }
    }

    public BufferedImage getImage() throws IOException {
        return ImageReader.convertFileToImage(this.religionGroup.getGame().getReligionsImage()).getSubimage((getIcon() - 1) * 64, 0, 64, 64);
    }

    public void writeImageTo(Path dest) throws IOException {
        FileUtils.forceMkdirParent(dest.toFile());
        ImageIO.write(getImage(), "png", dest.toFile());
        Eu4Utils.optimizePng(dest, dest);
        this.writenTo = dest;
    }

    public Path getWritenTo() {
        return writenTo;
    }

    public void setWritenTo(Path writenTo) {
        this.writenTo = writenTo;
    }
}
