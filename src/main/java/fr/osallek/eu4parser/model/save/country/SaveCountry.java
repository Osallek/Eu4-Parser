package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.clausewitzparser.model.ClausewitzObject;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.common.NumbersUtils;
import fr.osallek.eu4parser.model.Color;
import fr.osallek.eu4parser.model.Power;
import fr.osallek.eu4parser.model.UnitType;
import fr.osallek.eu4parser.model.game.AgeAbility;
import fr.osallek.eu4parser.model.game.CenterOfTrade;
import fr.osallek.eu4parser.model.game.ColonialRegion;
import fr.osallek.eu4parser.model.game.Country;
import fr.osallek.eu4parser.model.game.CrownLandBonus;
import fr.osallek.eu4parser.model.game.Culture;
import fr.osallek.eu4parser.model.game.Fervor;
import fr.osallek.eu4parser.model.game.FetishistCult;
import fr.osallek.eu4parser.model.game.GameModifier;
import fr.osallek.eu4parser.model.game.GovernmentName;
import fr.osallek.eu4parser.model.game.GovernmentRank;
import fr.osallek.eu4parser.model.game.GovernmentReform;
import fr.osallek.eu4parser.model.game.GreatProjectTier;
import fr.osallek.eu4parser.model.game.ImperialReform;
import fr.osallek.eu4parser.model.game.Institution;
import fr.osallek.eu4parser.model.game.Investment;
import fr.osallek.eu4parser.model.game.Isolationism;
import fr.osallek.eu4parser.model.game.LeaderPersonality;
import fr.osallek.eu4parser.model.game.Mission;
import fr.osallek.eu4parser.model.game.Modifier;
import fr.osallek.eu4parser.model.game.Modifiers;
import fr.osallek.eu4parser.model.game.ModifiersUtils;
import fr.osallek.eu4parser.model.game.NativeAdvancement;
import fr.osallek.eu4parser.model.game.NavalDoctrine;
import fr.osallek.eu4parser.model.game.PersonalDeity;
import fr.osallek.eu4parser.model.game.Policy;
import fr.osallek.eu4parser.model.game.ProfessionalismModifier;
import fr.osallek.eu4parser.model.game.ProvinceList;
import fr.osallek.eu4parser.model.game.Religion;
import fr.osallek.eu4parser.model.game.ReligionGroup;
import fr.osallek.eu4parser.model.game.ReligiousReform;
import fr.osallek.eu4parser.model.game.RulerPersonality;
import fr.osallek.eu4parser.model.game.StaticModifier;
import fr.osallek.eu4parser.model.game.StaticModifiers;
import fr.osallek.eu4parser.model.game.SubjectType;
import fr.osallek.eu4parser.model.game.TechGroup;
import fr.osallek.eu4parser.model.game.TradeGood;
import fr.osallek.eu4parser.model.game.TradePolicy;
import fr.osallek.eu4parser.model.save.Id;
import fr.osallek.eu4parser.model.save.ListOfDates;
import fr.osallek.eu4parser.model.save.ListOfDoubles;
import fr.osallek.eu4parser.model.save.Save;
import fr.osallek.eu4parser.model.save.SaveGreatProject;
import fr.osallek.eu4parser.model.save.SaveReligion;
import fr.osallek.eu4parser.model.save.TradeLeague;
import fr.osallek.eu4parser.model.save.counters.Counter;
import fr.osallek.eu4parser.model.save.diplomacy.DatableRelation;
import fr.osallek.eu4parser.model.save.diplomacy.Dependency;
import fr.osallek.eu4parser.model.save.province.SaveAdvisor;
import fr.osallek.eu4parser.model.save.province.SaveProvince;
import fr.osallek.eu4parser.model.save.religion.SavePapacy;
import fr.osallek.eu4parser.model.save.trade.TradeNodeCountry;
import fr.osallek.eu4parser.model.save.war.ActiveWar;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SaveCountry {

    private final ClausewitzItem item;

    private Save save;

    private String localizedName;

    private SubjectType subjectType; //Keep event after clean

    private LocalDate subjectStartDate; //Keep event after clean

    private Map<Integer, SaveAdvisor> advisors = new HashMap<>(); //In memory because heavy to compute

    private Path writenTo;

    public SaveCountry(ClausewitzItem item, Save save) {
        this.item = item;
        this.save = save;
    }

    public SaveCountry(String tag) {
        this.item = new ClausewitzItem(null, tag, 0);
    }

    public Save getSave() {
        return save;
    }

    public String getTag() {
        return ClausewitzUtils.removeQuotes(this.item.getName());
    }

    public String getLocalizedName() {
        return localizedName;
    }

    public void setLocalizedName(String localizedName) {
        String name = this.item.getVarAsString("name");

        if (name != null) {
            this.localizedName = ClausewitzUtils.removeQuotes(name);
        } else {
            name = this.item.getVarAsString("custom_name");

            if (name != null) {
                this.localizedName = ClausewitzUtils.removeQuotes(name);
            } else {
                this.localizedName = ClausewitzUtils.removeQuotes(localizedName);
            }
        }
    }

    public String getCustomName() {
        return this.item.getVarAsString("custom_name");
    }

    public String getName() {
        return this.item.getVarAsString("name");
    }

    public String getFlagPath(String extension) {
        return Path.of(Eu4Utils.GFX_FOLDER_PATH, "flags", getTag() + "." + extension).toString();
    }

    public File getFlagFile() {
        return this.save.getGame().getAbsoluteFile(getFlagPath("tga"));
    }

    public boolean useCustomFlagImage() {
        return isCustom() || isColony() || isClientState() || isTradeCity();
    }

    public BufferedImage getCustomFlagImage() throws IOException {
        if (isCustom()) {
            //Todo
            return null;
        } else if (isColony() && getColonialParent() != null) {
            Optional<ColonialRegion> region = this.save.getGame()
                                                       .getColonialRegions()
                                                       .stream()
                                                       .filter(r -> CollectionUtils.isNotEmpty(r.getProvinces()))
                                                       .filter(r -> r.getProvinces().contains(getCapitalId()))
                                                       .findFirst();

            if (region.isEmpty() || region.get().getColor() == null) {
                return null;
            }

            BufferedImage image = ImageIO.read(getColonialParent().getFlagFile());
            Graphics2D g = image.createGraphics();
            g.setColor(region.get().getColor().toColor());
            g.fillRect(image.getWidth() / 2, 0, image.getWidth() / 2, image.getHeight());
            g.dispose();

            return image;
        } else if (isClientState()) {
            //Todo
            return null;
        } else if (isTradeCity()) {
            //Todo
            return null;
        } else {
            return null;
        }
    }

    public void writeImageTo(Path dest) throws IOException {
        FileUtils.forceMkdirParent(dest.toFile());
        ImageIO.write(getCustomFlagImage(), "png", dest.toFile());
        Eu4Utils.optimizePng(dest, dest);
        this.writenTo = dest;
    }

    public void writeImageTo(Path dest, BufferedImage image) throws IOException {
        FileUtils.forceMkdirParent(dest.toFile());
        ImageIO.write(image, "png", dest.toFile());
        Eu4Utils.optimizePng(dest, dest);
        this.writenTo = dest;
    }

    public Path getWritenTo() {
        return writenTo;
    }

    public void setWritenTo(Path writenTo) {
        this.writenTo = writenTo;
    }

    public void setName(String name) {
        if (this.item.hasVar("custom_name")) {
            this.item.setVariable("custom_name", name);
        } else {
            this.item.setVariable("name", name);
        }
    }

    public boolean isPlayable() {
        return !"---".equals(getTag()) && !"REB".equals(getTag()) && !"NAT".equals(getTag())
               && !"PIR".equals(getTag()) && !getTag().matches("O\\d{2}");
    }

    public boolean isAlive() {
        return getCapital() != null && getDevelopment() != null && getDevelopment() > 0 && CollectionUtils.isNotEmpty(getContinents());
    }

    public boolean isCustom() {
        return Country.CUSTOM_COUNTRY_PATTERN.matcher(getTag()).matches();
    }

    public boolean isColony() {
        return Country.COLONY_PATTERN.matcher(getTag()).matches();
    }

    public boolean isTradeCity() {
        return Country.TRADING_CITY_PATTERN.matcher(getTag()).matches();
    }

    public boolean isClientState() {
        return Country.CLIENT_STATE_PATTERN.matcher(getTag()).matches();
    }

    public boolean isCossackRevolt() {
        return Country.COSSACK_REVOLT_PATTERN.matcher(getTag()).matches();
    }

    public boolean isObserver() {
        return Country.OBSERVER_PATTERN.matcher(getTag()).matches();
    }

    public boolean isUnknown() {
        return Country.UNKNOWN_PATTERN.matcher(getTag()).matches();
    }

    public boolean isNameEditable() {
        return isCustom() || isColony() || isTradeCity() || isClientState();
    }

    public SaveHegemon getHegemon() {
        return this.save.getHegemons().stream().filter(h -> h.getCountry().equals(this)).findFirst().orElse(null);
    }

    public boolean isHuman() {
        return BooleanUtils.toBoolean(this.item.getVarAsBool("human"));
    }

    public Boolean hasSwitchedNation() {
        return this.item.getVarAsBool("has_switched_nation");
    }

    public boolean wasPlayer() {
        return BooleanUtils.toBoolean(this.item.getVarAsBool("was_player"));
    }

    public void setWasPlayer(boolean wasPlayer) {
        this.item.setVariable("was_player", wasPlayer);
    }

    public List<String> getPlayers() {
        return this.save.getPlayers().entrySet().stream().filter(e -> e.getValue().equals(this)).map(Map.Entry::getKey).toList();
    }

    public Integer getGreatPowerRank() {
        return this.save.getGreatPowersRank(this);
    }

    public PlayerAiPrefsCommand getPlayerAiPrefsCommand() {
        ClausewitzItem playerAiPrefsCommandItem = this.item.getChild("player_ai_prefs_command");

        return playerAiPrefsCommandItem != null ? new PlayerAiPrefsCommand(playerAiPrefsCommandItem) : null;
    }

    public void setPlayerAiPrefsCommand(boolean startWars, boolean keepAlliances, boolean keepTreaties,
                                        boolean quickPeace, boolean moveTraders, boolean takeDecisions,
                                        boolean embraceInstitutions, boolean developProvinces, boolean disbandUnits,
                                        boolean changeFleetMissions, boolean sendMissionaries, boolean convertCultures,
                                        boolean promoteCultures, boolean braindead, int timeout) {
        PlayerAiPrefsCommand.addToItem(this.item, startWars, keepAlliances, keepTreaties, quickPeace, moveTraders,
                                       takeDecisions, embraceInstitutions, developProvinces, disbandUnits,
                                       changeFleetMissions, sendMissionaries, convertCultures, promoteCultures,
                                       braindead, timeout);
    }

    public Boolean hasSetGovernmentName() {
        return this.item.getVarAsBool("has_set_government_name");
    }

    public Integer getGovernmentLevel() {
        return this.item.getVarAsInt("government_rank");
    }

    public GovernmentRank getGovernmentRank() {
        return this.save.getGame().getGovernmentRank(getGovernmentLevel());
    }

    public void setGovernmentRank(int governmentRank) {
        if (governmentRank <= 0) {
            governmentRank = 1;
        }

        this.item.setVariable("government_rank", governmentRank);
    }

    public void setGovernmentRank(String governmentRank) {
        Integer rank = null;
        GovernmentName governmentName = getGovernmentName();

        if (governmentName != null) {
            rank = governmentName.getRanks()
                                 .entrySet()
                                 .stream()
                                 .filter(entry -> entry.getValue().equals(governmentRank))
                                 .findFirst()
                                 .map(Map.Entry::getKey)
                                 .orElse(null);
        }

        if (rank != null) {
            setGovernmentRank(rank);
        }
    }

    public GovernmentName getGovernmentName() {
        String governmentNameVar = this.item.getVarAsString("government_name");

        return StringUtils.isNotBlank(governmentNameVar) ? this.save.getGame().getGovernmentName(ClausewitzUtils.removeQuotes(governmentNameVar)) : null;
    }

    //Fixme compute the value
    public void setGovernmentName(GovernmentName governmentName) {
        this.item.setVariable("government_name", ClausewitzUtils.addQuotes(governmentName.getName()));
    }

    public Integer getSubjectFocus() {
        return this.item.getVarAsInt("subject_focus");
    }

    public Double getTradeMission() {
        return this.item.getVarAsDouble("trade_mission");
    }

    public Double getBlockadeMission() {
        return this.item.getVarAsDouble("blockade_mission");
    }

    public List<ProvinceList> getContinents() {
        ClausewitzList list = this.item.getList("continent");
        List<ProvinceList> continents = new ArrayList<>();

        if (list != null) {
            for (int i = 0; i < this.save.getGame().getContinents().size(); i++) {
                if (1 == list.getAsInt(i)) {
                    continents.add(this.save.getGame().getContinent(i));
                }
            }
        }

        return continents;
    }

    public Power getNationalFocus() {
        String nationalFocus = this.item.getVarAsString("national_focus");
        return nationalFocus == null ? null : Power.byName(nationalFocus);
    }

    public void setNationalFocus(Power power, LocalDate date) {
        this.item.setVariable("national_focus", power.name());

        if (getHistory() != null) {
            getHistory().addEvent(date, "national_focus", power.name());
        }
    }

    public List<Institution> getEmbracedInstitutions() {
        ClausewitzList list = this.item.getList("institutions");

        if (list != null) {
            return this.save.getGame().getInstitutions().stream().filter(this::getEmbracedInstitution).toList();
        }

        return new ArrayList<>();
    }

    public List<Institution> getNotEmbracedInstitutions() {
        ClausewitzList list = this.item.getList("institutions");

        if (list != null) {
            return this.save.getGame().getInstitutions().stream().filter(index -> !this.getEmbracedInstitution(index)).toList();
        }

        return new ArrayList<>();
    }

    public boolean getEmbracedInstitution(int institution) {
        ClausewitzList list = this.item.getList("institutions");

        if (list != null) {
            return 1 == list.getAsInt(institution);
        }

        return false;
    }

    public boolean getEmbracedInstitution(Institution institution) {
        ClausewitzList list = this.item.getList("institutions");

        if (list != null) {
            return 1 == list.getAsInt(institution.getIndex());
        }

        return false;
    }

    public long getNbEmbracedInstitutions() {
        return getEmbracedInstitutions().size();
    }

    public void embracedInstitution(int institution, boolean embraced) {
        ClausewitzList list = this.item.getList("institutions");

        if (list != null) {
            list.set(institution, embraced ? 1 : 0);
        }
    }

    public Integer getNumOfAgeObjectives() {
        return this.item.getVarAsInt("num_of_age_objectives");
    }

    public List<AgeAbility> getActiveAgeAbility() {
        List<ClausewitzVariable> variables = this.item.getVariables("active_age_ability");

        return variables.stream()
                        .map(variable -> this.save.getGame().getAgeAbility(ClausewitzUtils.removeQuotes(variable.getValue())))
                        .toList();
    }

    public void addAgeAbility(String ageAbility) {
        List<String> abilities = this.item.getVarsAsStrings("active_age_ability");

        if (!abilities.contains(ageAbility.toLowerCase())) {
            this.item.addVariable("active_age_ability", ClausewitzUtils.addQuotes(ageAbility.toLowerCase()));
        }
    }

    public void removeAgeAbility(int index) {
        this.item.removeVariable("active_age_ability", index);
    }

    public void removeAgeAbility(String ageAbility) {
        this.item.removeVariable("active_age_ability", ageAbility);
    }

    public LocalDate getLastSoldProvince() {
        return this.item.getVarAsDate("last_sold_province");
    }

    public void setLastSoldProvince(LocalDate lastSoldProvince) {
        this.item.setVariable("last_sold_province", lastSoldProvince);
    }

    public LocalDate getGoldenEraDate() {
        return this.item.getVarAsDate("golden_era_date");
    }

    public void setGoldenEraDate(LocalDate goldenEraDate) {
        this.item.setVariable("golden_era_date", goldenEraDate);
    }

    public void removeGoldenEraDate() {
        this.item.removeVariable("golden_era_date");
    }

    public LocalDate getLastFocusMove() {
        return this.item.getVarAsDate("last_focus_move");
    }

    public void setLastFocusMove(LocalDate lastFocusMove) {
        this.item.setVariable("last_focus_move", lastFocusMove);
    }

    public LocalDate getLastSentAllianceOffer() {
        return this.item.getVarAsDate("last_sent_alliance_offer");
    }

    public void setLastSentAllianceOffer(LocalDate lastSentAllianceOffer) {
        this.item.setVariable("last_sent_alliance_offer", lastSentAllianceOffer);
    }

    public LocalDate getLastConversionSecondary() {
        return this.item.getVarAsDate("last_conversion_secondary");
    }

    public void setLastConversionSecondary(LocalDate lastConversionSecondary) {
        this.item.setVariable("last_conversion_secondary", lastConversionSecondary);
    }

    public ListOfDates getCooldowns() {
        ClausewitzItem cooldownsItem = this.item.getChild("cooldowns");

        return cooldownsItem != null ? new ListOfDates(cooldownsItem) : null;
    }

    public SaveCountryHistory getHistory() {
        ClausewitzItem historyItem = this.item.getChild("history");
        return historyItem != null ? new SaveCountryHistory(historyItem, this) : null;
    }

    public ListOfDates getFlags() {
        ClausewitzItem flagsItem = this.item.getChild("flags");

        return flagsItem != null ? new ListOfDates(flagsItem) : null;
    }

    public ListOfDates getHiddenFlags() {
        ClausewitzItem hiddenFlagsItem = this.item.getChild("hidden_flags");

        return hiddenFlagsItem != null ? new ListOfDates(hiddenFlagsItem) : null;
    }

    public ListOfDoubles getVariables() {
        ClausewitzItem variablesItem = this.item.getChild("variables");

        return variablesItem != null ? new ListOfDoubles(variablesItem) : null;
    }

    public Colors getColors() {
        ClausewitzItem colorsItem = this.item.getChild("colors");

        return colorsItem != null ? new Colors(colorsItem) : null;
    }

    public Color getColor() {
        return this.equals(this.save.getRevolution().getRevolutionTarget()) ? getColors().getRevolutionaryColors() : getColors().getMapColor();
    }

    public List<String> getIgnoreDecision() {
        return this.item.getVarsAsStrings("ignore_decision");
    }

    public void addIgnoreDecision(String ignoreDecision) {
        List<String> ignoreDecisions = this.item.getVarsAsStrings("ignore_decision");

        if (!ignoreDecisions.contains(ClausewitzUtils.addQuotes(ignoreDecision))) {
            this.item.addVariable("ignore_decision", ClausewitzUtils.addQuotes(ignoreDecision));
        }
    }

    public void removeIgnoreDecision(int index) {
        this.item.removeVariable("ignore_decision", index);
    }

    public Integer getCapitalId() {
        return this.item.getVarAsInt("capital");
    }

    public SaveProvince getCapital() {
        return this.save.getProvince(getCapitalId());
    }

    public void setCapital(SaveProvince saveProvince) {
        if (saveProvince != null && saveProvince.getOwner().equals(this)) {
            this.item.setVariable("capital", saveProvince.getId());
        }
    }

    public SaveProvince getOriginalCapital() {
        return this.save.getProvince(this.item.getVarAsInt("original_capital"));
    }

    public void setOriginalCapital(Integer provinceId) {
        this.item.setVariable("original_capital", provinceId);
    }

    public SaveProvince getTradePort() {
        return this.save.getProvince(this.item.getVarAsInt("trade_port"));
    }

    public void setTradePort(Integer provinceId) {
        if (this.save.getProvince(provinceId).getOwner().equals(this)) {
            this.item.setVariable("trade_port", provinceId);
        }
    }

    public Double getCustomNationPoints() {
        return this.item.getVarAsDouble("custom_nation_points");
    }

    public Double getBaseTax() {
        return this.item.getVarAsDouble("base_tax");
    }

    public Double getDevelopment() {
        return this.item.getVarAsDouble("development");
    }

    public Double getRawDevelopment() {
        return this.item.getVarAsDouble("raw_development");
    }

    public Double getCappedDevelopment() {
        return this.item.getVarAsDouble("capped_development");
    }

    public Double getRealmDevelopment() {
        return this.item.getVarAsDouble("realm_development");
    }

    public Double getUsedGoverningCapacity() {
        return this.item.getVarAsDouble("used_governing_capacity");
    }

    public Isolationism getIsolationism() {
        return this.save.getGame().getIsolationism(this.item.getVarAsInt("isolationism"));
    }

    public void setIsolationism(Isolationism isolationism) {
        this.item.setVariable("isolationism", isolationism.getIsolationValue());
    }

    public Integer getIsolationismLevel() {
        return getIsolationism() == null ? null : getIsolationism().getIsolationValue();
    }

    public void setIsolationismLevel(int level) {
        if (level < 0) {
            level = 0;
        } else if (level > 4) {
            level = 4;
        }

        this.item.setVariable("isolationism", level);
    }

    public Integer getNumExpandedAdministration() {
        return this.item.getVarAsInt("num_expanded_administration");
    }

    public void setNumExpandedAdministration(Integer numExpandedAdministration) {
        this.item.setVariable("num_expanded_administration", numExpandedAdministration);
    }

    public Integer getKarma() {
        return (int) NumbersUtils.doubleOrDefault(this.item.getVarAsDouble("karma"));
    }

    public void setKarma(int karma) {
        this.item.setVariable("karma", (double) karma);
    }

    public Boolean hasReformedReligion() {
        return this.item.getVarAsBool("has_reformed_religion");
    }

    public void setHasReformedReligion(boolean hasReformedReligion) {
        this.item.setVariable("has_reformed_religion", hasReformedReligion);
    }

    public Double getHarmony() {
        return this.item.getVarAsDouble("harmony");
    }

    public void setHarmony(Double harmony) {
        this.item.setVariable("harmony", harmony);
    }

    public SaveReligion getHarmonizingWithReligion() {
        return this.save.getReligions().getReligion(this.item.getVarAsString("harmonizing_with_religion"));
    }

    public void setHarmonizingWithReligion(SaveReligion harmonizingWithReligion) {
        if (harmonizingWithReligion == null) {
            this.item.removeVariable("harmonizing_with_religion");
        } else {
            this.item.setVariable("harmonizing_with_religion", harmonizingWithReligion.getName());

            if (getHarmonyProgress() == null) {
                setHarmonyProgress(0d);
            }
        }

    }

    public Double getHarmonyProgress() {
        return this.item.getVarAsDouble("harmonization_progress");
    }

    public void setHarmonyProgress(Double harmonizationProgress) {
        if (getHarmonizingWithReligion() != null) {
            this.item.setVariable("harmonization_progress", harmonizationProgress);
        }
    }

    public List<ReligionGroup> getHarmonizedReligionGroups() {
        ClausewitzList list = this.item.getList("harmonized_religion_groups");

        if (list == null) {
            return new ArrayList<>();
        }

        List<ReligionGroup> religionGroups = new ArrayList<>(this.save.getGame().getReligionGroups());
        return list.getValuesAsInt().stream().map(integer -> integer - 1).map(religionGroups::get).toList();
    }

    public void addHarmonizedReligionGroup(ReligionGroup religionGroup) {
        ClausewitzList list = this.item.getList("harmonized_religion_groups");
        Integer index = null;
        List<ReligionGroup> religionGroups = new ArrayList<>(this.save.getGame().getReligionGroups());

        for (int i = 0; i < religionGroups.size(); i++) {
            if (religionGroups.get(i).equals(religionGroup)) {
                index = i + 1;
                break;
            }
        }

        if (index != null) {
            if (list == null) {
                this.item.addList("harmonized_religion_groups", index);
            } else {
                list.add(index);
            }
        }
    }

    public void removeHarmonizedReligionGroup(ReligionGroup religionGroup) {
        ClausewitzList list = this.item.getList("harmonized_religion_groups");

        if (list != null) {
            Integer index = null;
            List<ReligionGroup> religionGroups = new ArrayList<>(this.save.getGame().getReligionGroups());

            for (int i = 0; i < religionGroups.size(); i++) {
                if (religionGroups.get(i).equals(religionGroup)) {
                    index = i + 1;
                    break;
                }
            }

            if (index != null) {
                list.remove(index.toString());
            }
        }
    }

    public List<SaveReligion> getHarmonizedReligions() {
        ClausewitzList list = this.item.getList("harmonized_religions");

        if (list == null) {
            return new ArrayList<>();
        }

        List<SaveReligion> religions = new ArrayList<>(this.save.getReligions().getReligions().values());
        return list.getValuesAsInt().stream().map(integer -> integer - 1).map(religions::get).toList();
    }

    public void addHarmonizedReligion(Religion religion) {
        ClausewitzList list = this.item.getList("harmonized_religions");
        Integer index = null;
        List<Religion> religions = new ArrayList<>(this.save.getGame().getReligions());

        for (int i = 0; i < religions.size(); i++) {
            if (religions.get(i).equals(religion)) {
                index = i + 1;
                break;
            }
        }

        if (index != null) {
            if (list == null) {
                this.item.addList("harmonized_religions", index);
            } else {
                list.add(index);
            }
        }
    }

    public void removeHarmonizedReligion(Religion religion) {
        ClausewitzList list = this.item.getList("harmonized_religions");

        if (list != null) {
            Integer index = null;
            List<Religion> religions = new ArrayList<>(this.save.getGame().getReligions());

            for (int i = 0; i < religions.size(); i++) {
                if (religions.get(i).equals(religion)) {
                    index = i + 1;
                    break;
                }
            }

            if (index != null) {
                list.remove(index.toString());
            }
        }
    }

    public List<String> getActiveIncidents() {
        ClausewitzList list = this.item.getList("active_incidents");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues();
    }

    public List<String> getPotentialIncidents() {
        ClausewitzList list = this.item.getList("potential_incidents");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues();
    }

    public List<String> getPastIncidents() {
        ClausewitzList list = this.item.getList("past_incidents");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues();
    }

    public Map<String, Double> getIncidentVariables() {
        ClausewitzItem child = this.item.getChild("incident_variables");

        if (child == null) {
            return new HashMap<>();
        }

        return child.getVariables().stream().collect(Collectors.toMap(ClausewitzObject::getName, ClausewitzVariable::getAsDouble));
    }

    public boolean hasCircumnavigatedWorld() {
        return BooleanUtils.toBoolean(this.item.getVarAsBool("has_circumnavigated_world"));
    }

    public void setHasCircumnavigatedWorld(boolean hasCircumnavigatedWorld) {
        this.item.setVariable("has_circumnavigated_world", hasCircumnavigatedWorld);
    }

    public boolean initializedRivals() {
        return BooleanUtils.toBoolean(this.item.getVarAsBool("initialized_rivals"));
    }

    public boolean recalculateStrategy() {
        return BooleanUtils.toBoolean(this.item.getVarAsBool("recalculate_strategy"));
    }

    public boolean dirtyColony() {
        return BooleanUtils.toBoolean(this.item.getVarAsBool("dirty_colony"));
    }

    public Culture getPrimaryCulture() {
        return this.save.getGame().getCulture(this.item.getVarAsString("primary_culture"));
    }

    public void setPrimaryCulture(Culture primaryCulture) {
        this.item.setVariable("primary_culture", primaryCulture.getName());
    }

    public Culture getDominantCulture() {
        return this.save.getGame().getCulture(this.item.getVarAsString("dominant_culture"));
    }

    public List<String> getAcceptedCulturesNames() {
        return this.item.getVarsAsStrings("accepted_culture");
    }

    public List<Culture> getAcceptedCultures() {
        return getAcceptedCulturesNames().stream().map(s -> this.save.getGame().getCulture(s)).toList();
    }

    public void setAcceptedCulture(List<Culture> cultures) {
        getAcceptedCultures().forEach(acceptedCulture -> cultures.stream()
                                                                 .filter(culture -> culture.equals(acceptedCulture))
                                                                 .findFirst()
                                                                 .ifPresentOrElse(cultures::remove, () -> removeAcceptedCulture(acceptedCulture)));

        cultures.forEach(this::addAcceptedCulture);
    }

    public void addAcceptedCulture(Culture culture) {
        List<String> acceptedCultures = this.item.getVarsAsStrings("accepted_culture");

        if (!acceptedCultures.contains(culture.getName())) {
            this.item.addVariable("accepted_culture", culture.getName(), this.item.getVar("dominant_culture").getOrder() + 1);
            getHistory().addEvent(this.save.getDate(), "add_accepted_culture", culture.getName());
        }
    }

    public void removeAcceptedCulture(Culture culture) {
        if (this.item.removeVariable("accepted_culture", culture.getName())) {
            getHistory().addEvent(this.save.getDate(), "remove_accepted_culture", culture.getName());
        }
    }

    public String getReligionName() {
        return this.item.getVarAsString("religion");
    }

    public SaveReligion getReligion() {
        return this.save.getReligions().getReligion(getReligionName());
    }

    public void setReligion(SaveReligion religion) {
        this.item.setVariable("religion", religion.getName());
    }

    public String getReligiousSchool() {
        return this.item.getVarAsString("religious_school");
    }

    public void setReligiousSchool(String religiousSchool) {
        this.item.setVariable("religious_school", ClausewitzUtils.addQuotes(religiousSchool));
    }

    public Set<SaveReligion> getAvailableSecondaryReligions() {
        if (getReligion() == null || !BooleanUtils.toBoolean(getReligion().getGameReligion().canHaveSecondaryReligion())) {
            return new HashSet<>();
        }

        Set<SaveReligion> religions = new HashSet<>();
        religions.add(getSecondaryReligion());
        religions.addAll(getOwnedProvinces().stream().map(SaveProvince::getReligion).toList());
        //Todo adjacent provinces

        religions.removeIf(Objects::isNull);
        return religions;
    }

    public SaveReligion getSecondaryReligion() {
        return this.save.getReligions().getReligion(this.item.getVarAsString("secondary_religion"));
    }

    public void setSecondaryReligion(SaveReligion religion) {
        if (religion == null) {
            this.item.removeVariable("secondary_religion");
        } else {
            this.item.setVariable("secondary_religion", religion.getName());
        }
    }

    public SaveReligion getDominantReligion() {
        return this.save.getReligions().getReligion(this.item.getVarAsString("dominant_religion"));
    }

    public SaveFervor getFervor() {
        ClausewitzItem fervorItem = this.item.getChild("fervor");

        return fervorItem != null ? new SaveFervor(fervorItem, this.save.getGame()) : null;
    }

    public TechGroup getTechnologyGroup() {
        return this.save.getGame().getTechGroup(ClausewitzUtils.removeQuotes(this.item.getVarAsString("technology_group")));
    }

    public void setTechnologyGroup(String technologyGroup) {
        this.item.setVariable("technology_group", technologyGroup);
    }

    public Double getLibertyDesire() {
        return this.item.getVarAsDouble("liberty_desire");
    }

    public void setLibertyDesire(Double libertyDesire) {
        this.item.setVariable("liberty_desire", libertyDesire);
    }

    public String getUnitType() {
        return this.item.getVarAsString("unit_type");
    }

    public void setUnitType(String unitType) {
        this.item.setVariable("unit_type", unitType);
    }

    public CountryTechnology getTech() {
        ClausewitzItem techItem = this.item.getChild("technology");

        return techItem != null ? new CountryTechnology(this.save, techItem) : null;
    }

    public List<SaveEstate> getEstates() {
        return this.item.getChildren("estate").stream().map(i -> new SaveEstate(i, this)).toList();
    }

    public SaveEstate getEstate(String name) {
        return getEstates().stream().filter(estate -> name.equalsIgnoreCase(ClausewitzUtils.removeQuotes(estate.getType()))).findFirst().orElse(null);
    }

    public CrownLandBonus getCrownLandBonus() {
        double crownLand = 100 - getEstates().stream().mapToDouble(SaveEstate::getTerritory).sum();

        return this.save.getGame().getCrownLandBonuses().stream().filter(crownLandBonus -> crownLandBonus.isInRange(crownLand)).findFirst().orElse(null);
    }

    public ActiveAgenda getActiveAgenda() {
        ClausewitzItem activeAgendaItem = this.item.getChild("active_agenda");

        return activeAgendaItem != null ? new ActiveAgenda(activeAgendaItem, this) : null;
    }

    public List<EstateInteraction> getInteractionsLastUsed() {
        ClausewitzItem interactionsLastUsedItem = this.item.getChild("interactions_last_used");

        return interactionsLastUsedItem != null ? interactionsLastUsedItem.getLists().stream()
                                                                          .map(list -> new EstateInteraction(this.save.getGame(), list))
                                                                          .toList() : null;
    }

    public List<SaveFaction> getFactions() {
        return this.item.getChildren("faction")
                        .stream()
                        .map(child -> new SaveFaction(child, this.save.getGame()))
                        .toList();
    }

    public SaveFaction getFaction(String name) {
        return getFactions().stream()
                            .filter(faction -> name.equalsIgnoreCase(ClausewitzUtils.removeQuotes(faction.getType().getName())))
                            .findFirst()
                            .orElse(null);
    }

    public Integer getTopFaction() {
        return this.item.getVarAsInt("top_faction");
    }

    public void setTopFaction(int topFaction) {
        this.item.setVariable("top_faction", topFaction);
    }

    public Map<String, Rival> getRivals() {
        return this.item.getChildren("rival")
                        .stream()
                        .map(child -> new Rival(child, this.save))
                        .collect(Collectors.toMap(rival -> ClausewitzUtils.removeQuotes(rival.getRivalTag()), Function.identity()));
    }

    public void addRival(SaveCountry country, LocalDate date) {
        if (!getRivals().containsKey(ClausewitzUtils.addQuotes(country.getTag()))) {
            int order = this.item.getVar("highest_possible_fort").getOrder();
            Rival.addToItem(this.item, country, date, order);
        }
    }

    public void removeRival(int index) {
        this.item.removeVariable("rival", index);
    }

    public void removeRival(SaveCountry rival) {
        this.item.removeChildIf(child -> "rival".equalsIgnoreCase(child.getName())
                                         && ClausewitzUtils.addQuotes(rival.getTag()).equalsIgnoreCase(child.getVarAsString("country")));
    }

    public Double getStatistsVsMonarchists() {
        return this.item.getVarAsDouble("statists_vs_monarchists");
    }

    public boolean isStatistsInPower() {
        if (NumbersUtils.doubleOrDefault(getStatistsVsMonarchists()) > 0) {
            return false;
        }

        SaveGovernment government = getGovernment();
        return government != null
               && government.getReforms().stream().anyMatch(reform -> reform.getStatesGeneralMechanic() != null
                                                                      && (reform.getStatesGeneralMechanic().getValue() == null
                                                                          || reform.getStatesGeneralMechanic().getValue().apply(this, this)));
    }

    public boolean isMonarchistsInPower() {
        return NumbersUtils.doubleOrDefault(getStatistsVsMonarchists()) > 0
               && getGovernment() != null
               && getGovernment().getReforms().stream().anyMatch(reform -> reform.getStatesGeneralMechanic() != null
                                                                           && (reform.getStatesGeneralMechanic().getValue() == null
                                                                               || reform.getStatesGeneralMechanic().getValue().apply(this, this)));
    }

    public void setStatistsVsMonarchists(Double statistsVsMonarchists) {
        statistsVsMonarchists = BigDecimal.valueOf(statistsVsMonarchists).divide(BigDecimal.valueOf(1000d), 3, RoundingMode.HALF_EVEN).doubleValue();
        statistsVsMonarchists = Math.min(Math.max(statistsVsMonarchists, -1), 1);

        this.item.setVariable("statists_vs_monarchists", statistsVsMonarchists);
    }

    public Double getMilitarisedSociety() {
        return this.item.getVarAsDouble("militarised_society");
    }

    public void setMilitarisedSociety(Double militarisedSociety) {
        militarisedSociety = Math.min(Math.max(militarisedSociety, 0), 100);

        this.item.setVariable("militarised_society", militarisedSociety);
    }

    public Double getTribalAllegiance() {
        return this.item.getVarAsDouble("tribal_allegiance");
    }

    public void setTribalAllegiance(Double tribalAllegiance) {
        tribalAllegiance = Math.min(Math.max(tribalAllegiance, 0), 100);

        this.item.setVariable("tribal_allegiance", tribalAllegiance);
    }

    public Integer getHighestPossibleFort() {
        return this.item.getVarAsInt("highest_possible_fort");
    }

    public String getHighestPossibleFortBuilding() {
        return this.item.getVarAsString("highest_possible_fort_building");
    }

    public int getTotalFortLevel() {
        return getOwnedProvinces().stream().filter(province -> province.getOwner().equals(province.getController())).mapToInt(SaveProvince::getFortLevel).sum();
    }

    public Double getTransferHomeBonus() {
        return this.item.getVarAsDouble("transfer_home_bonus");
    }

    public Boolean isExcommunicated() {
        return this.item.getVarAsBool("excommunicated");
    }

    public void setExcommunicated(boolean excommunicated) {
        this.item.setVariable("excommunicated", excommunicated);
    }

    public Integer getNumOfWarReparations() {
        return this.item.getVarAsInt("num_of_war_reparations");
    }

    public SaveCountry getCoalitionTarget() {
        return this.save.getCountry(ClausewitzUtils.removeQuotes(this.item.getVarAsString("coalition_target")));
    }

    public void setCoalitionTarget(SaveCountry coalitionTarget) {
        this.item.setVariable("coalition_target", ClausewitzUtils.addQuotes(coalitionTarget.getTag()));
        setCoalitionDate(this.save.getDate());
    }

    public LocalDate getCoalitionDate() {
        return this.item.getVarAsDate("coalition_date");
    }

    public void setCoalitionDate(LocalDate coalitionDate) {
        if (getCoalitionTarget() != null) {
            this.item.setVariable("coalition_date", coalitionDate);
        }
    }

    public void addWarReparations(SaveCountry country) {
        Integer nbWarReparations = getNumOfWarReparations();

        if (nbWarReparations == null) {
            nbWarReparations = 0;
        }

        this.item.setVariable("num_of_war_reparations", nbWarReparations + 1);
    }

    public void removeWarReparations(SaveCountry country) {
        Integer nbWarReparations = getNumOfWarReparations();

        if (nbWarReparations == null) {
            nbWarReparations = 1;
        }

        this.item.setVariable("num_of_war_reparations", nbWarReparations - 1);
    }

    public String getOverlordTag() {
        return this.item.getVarAsString("overlord");
    }

    public SaveCountry getOverlord() {
        String overlordTag = getOverlordTag();

        return overlordTag == null ? null : this.save.getCountry(ClausewitzUtils.removeQuotes(overlordTag));
    }

    public SubjectType getSubjectType() {
        computeSubject();
        return this.subjectType;
    }

    public void setSubjectType(SubjectType subjectType) {
        this.subjectType = subjectType;
    }

    public LocalDate getSubjectStartDate() {
        computeSubject();
        return subjectStartDate;
    }

    public void setSubjectStartDate(LocalDate subjectStartDate) {
        this.subjectStartDate = subjectStartDate;
    }

    private void computeSubject() {
        if (getOverlordTag() != null && this.subjectType == null) {
            this.save.getDiplomacy().getDependenciesStream().filter(dependency -> this.equals(dependency.getSecond())).findFirst().ifPresent(dependency -> {
                this.subjectType = dependency.getSubjectType();
                this.subjectStartDate = dependency.getStartDate();
            });
        }
    }

    public void setOverlord(String countryTag) {
        if (countryTag == null) {
            this.item.removeVariable("overlord");
        } else {
            this.item.setVariable("overlord", ClausewitzUtils.addQuotes(countryTag));
        }
    }

    public void setOverlord(SaveCountry country) {
        if (country == null) {
            this.item.removeVariable("overlord");
        } else {
            setOverlord(country.getTag());
        }
    }

    public void setOverlord(SaveCountry overlord, SubjectType subjectType, LocalDate startDate) { //Todo add/remove from wars
        if (!Objects.equals(this.getOverlord(), overlord) || !Objects.equals(this.subjectType, subjectType)
            || !Objects.equals(this.subjectStartDate, startDate)) {
            if (Objects.equals(this.getOverlord(), overlord)) {
                this.getSave()
                    .getDiplomacy()
                    .getDependencies()
                    .stream()
                    .filter(dependency -> dependency.getSubjectType().equals(this.subjectType) && dependency.getSecond().equals(this))
                    .findFirst()
                    .ifPresent(dependency -> {
                        dependency.setStartDate(startDate);
                        dependency.setSubjectType(subjectType);
                    });
                this.subjectType = subjectType;
                this.subjectStartDate = startDate;
            } else if (this.getOverlord() != null) {
                this.getSave()
                    .getDiplomacy()
                    .getDependencies()
                    .stream()
                    .filter(dependency -> dependency.getSubjectType().equals(this.subjectType) && dependency.getSecond().equals(this))
                    .findFirst()
                    .ifPresent(dependency -> {
                        dependency.setFirst(overlord);
                        dependency.setStartDate(startDate);
                        dependency.setSubjectType(subjectType);
                    });
                overlord.addSubject(this);
                this.getOverlord().removeSubject(this);

                this.setOverlord(overlord);
                this.subjectType = subjectType;
                this.subjectStartDate = startDate;
            } else {
                this.save.getDiplomacy().addDependency(overlord, this, startDate, subjectType);
            }
        }
    }

    public void removeOverlord() {
        this.item.removeVariable("overlord");
        this.subjectType = null;
        this.subjectStartDate = null;
    }

    public List<SaveCountry> getEnemies() {
        return this.item.getVarsAsStrings("enemy")
                        .stream()
                        .map(s -> this.save.getCountry(ClausewitzUtils.removeQuotes(s)))
                        .toList();
    }

    public Integer getGoldType() {
        return this.item.getVarAsInt("goldtype");
    }

    public Double getCallForPeace() {
        return this.item.getVarAsDouble("call_for_peace");
    }

    public Boolean hasUnconditionalSurrender() {
        return this.item.getVarAsBool("has_unconditional_surrender");
    }

    public List<SaveCountry> gaveAccess() {
        return this.item.getVarsAsStrings("gave_access")
                        .stream()
                        .map(s -> this.save.getCountry(ClausewitzUtils.removeQuotes(s)))
                        .toList();
    }

    public List<SaveCountry> getOurSpyNetwork() {
        return this.item.getVarsAsStrings("our_spy_network")
                        .stream()
                        .map(s -> this.save.getCountry(ClausewitzUtils.removeQuotes(s)))
                        .toList();
    }

    public List<SaveCountry> getTheirSpyNetwork() {
        return this.item.getVarsAsStrings("their_spy_network")
                        .stream()
                        .map(s -> this.save.getCountry(ClausewitzUtils.removeQuotes(s)))
                        .toList();
    }

    public Boolean isLucky() {
        return this.item.getVarAsBool("luck");
    }

    public void setLucky(boolean lucky) {
        this.item.setVariable("luck", lucky);
    }

    public SaveCountry getFederationLeader() {
        return this.save.getCountry(ClausewitzUtils.removeQuotes(this.item.getVarAsString("federation_leader")));
    }

    public List<SaveCountry> getFederationFriends() {
        return this.item.getVarsAsStrings("federation_friends")
                        .stream()
                        .map(s -> this.save.getCountry(ClausewitzUtils.removeQuotes(s)))
                        .toList();
    }

    public List<SaveCountry> getCoalition() {
        return this.item.getVarsAsStrings("coalition_against_us")
                        .stream()
                        .map(s -> this.save.getCountry(ClausewitzUtils.removeQuotes(s)))
                        .toList();
    }

    public List<SaveCountry> getPreferredCoalition() {
        return this.item.getVarsAsStrings("preferred_coalition_against_us")
                        .stream()
                        .map(s -> this.save.getCountry(ClausewitzUtils.removeQuotes(s)))
                        .toList();
    }

    public List<VictoryCard> getVictoryCards() {
        return this.item.getChildren("victory_card").stream().map(VictoryCard::new).toList();
    }

    public void addVictoryCard(String area) {
        if (getVictoryCards().stream().noneMatch(victoryCard -> victoryCard.getArea().equalsIgnoreCase(area))) {
            int level = getVictoryCards().stream().mapToInt(VictoryCard::getLevel).max().orElse(0);
            VictoryCard.addToItem(this.item, area, level + 1, 0d, false);
        }
    }

    public void removeVictoryCard(int index) {
        this.item.removeVariable("victory_card", index);
    }

    public void removeVictoryCard(String area) {
        Integer index = null;
        List<VictoryCard> victoryCardList = getVictoryCards();

        for (int i = 0; i < victoryCardList.size(); i++) {
            if (victoryCardList.get(i).getArea().equalsIgnoreCase(area)) {
                index = i;
                break;
            }
        }

        if (index != null) {
            this.item.removeVariable("victory_card", index);
        }
    }

    public boolean getConvert() {
        return BooleanUtils.toBoolean(this.item.getVarAsBool("convert"));
    }

    public void setConvert(boolean convert) {
        this.item.setVariable("convert", convert);
    }

    public SaveCountry getForceConvert() {
        String forceConverted = ClausewitzUtils.removeQuotes(this.item.getVarAsString("force_converted"));

        if (Eu4Utils.DEFAULT_TAG.equals(forceConverted)) {
            forceConverted = null;
        }

        return this.save.getCountry(forceConverted);
    }

    public void setForceConvert(SaveCountry country) {
        this.item.setVariable("force_converted", ClausewitzUtils.addQuotes(country.getTag()));
        setConvert(true);
    }

    public SaveCountry getColonialParent() {
        String colonialParent = ClausewitzUtils.removeQuotes(this.item.getVarAsString("colonial_parent"));

        if (Eu4Utils.DEFAULT_TAG.equals(colonialParent)) {
            colonialParent = null;
        }

        return this.save.getCountry(colonialParent);
    }

    public void setColonialParent(SaveCountry country) {
        this.item.setVariable("colonial_parent", ClausewitzUtils.addQuotes(country.getTag()));
    }

    public boolean newMonarch() {
        return BooleanUtils.toBoolean(this.item.getVarAsBool("new_monarch"));
    }

    public boolean isAtWar() {
        return BooleanUtils.toBoolean(this.item.getVarAsBool("is_at_war"));
    }

    public LocalDate lastElection() {
        return this.item.getVarAsDate("last_election");
    }

    public void setLastElection(LocalDate lastElection) {
        this.item.setVariable("last_election", lastElection);
    }

    public Double getDelayedTreasure() {
        return this.item.getVarAsDouble("delayed_treasure");
    }

    public List<ActivePolicy> getActivePolicies() {
        return this.item.getChildren("active_policy").stream().map(child -> new ActivePolicy(child, this.save.getGame())).toList();
    }

    public ActivePolicy getActivePolicy(String policy) {
        return getActivePolicies().stream()
                                  .filter(activePolicy -> policy.equals(activePolicy.getPolicy().getName()))
                                  .findFirst()
                                  .orElse(null);
    }

    public void addActivePolicy(Policy policy, LocalDate date) {
        if (getActivePolicies().stream().noneMatch(activePolicy -> activePolicy.getPolicy().equals(policy))) {
            ActivePolicy.addToItem(this.item, policy, date, this.item.getVar("last_election").getOrder() + 1);
        }
    }

    public void removeActivePolicy(int index) {
        this.item.removeVariable("active_policy", index);
    }

    public void removeActivePolicy(Policy policy) {
        Integer index = null;
        List<ActivePolicy> activePolicies = getActivePolicies();

        for (int i = 0; i < activePolicies.size(); i++) {
            if (activePolicies.get(i).getPolicy().equals(policy)) {
                index = i;
                break;
            }
        }

        if (index != null) {
            this.item.removeChild("active_policy", index);
        }
    }

    public Double getCurrentPowerProjection() {
        return this.item.getVarAsDouble("current_power_projection");
    }

    private void setCurrentPowerProjection(double currentPowerProjection) {
        this.item.setVariable("current_power_projection", currentPowerProjection);
    }

    public Double getGreatPowerScore() {
        return this.item.getVarAsDouble("great_power_score");
    }

    public List<PowerProjection> getPowerProjections() {
        return this.item.getChildren("power_projection").stream().map(PowerProjection::new).toList();
    }

    public void removePowerProjections(int index) {
        this.item.removeVariable("power_projection", index);
        computePowerProjection();
    }

    public void removePowerProjections(String modifier) {
        Integer index = null;

        for (int i = 0; i < getActivePolicies().size(); i++) {
            if (getPowerProjections().get(i).getModifier().equalsIgnoreCase(modifier)) {
                index = i;
                break;
            }
        }

        if (index != null) {
            this.item.removeVariable("power_projection", index);
            computePowerProjection();
        }
    }

    private void computePowerProjection() {
        Double powerProjection = null;

        for (PowerProjection projection : getPowerProjections()) {
            if (projection.getCurrent() != null && !projection.getCurrent().equals(0d)) {
                double value = BigDecimal.valueOf(projection.getCurrent())
                                         .setScale(0, RoundingMode.HALF_UP)
                                         .doubleValue();
                if (powerProjection == null) {
                    powerProjection = value;
                } else {
                    powerProjection += value;
                }
            }
        }

        if (powerProjection != null) {
            if (powerProjection <= 0d) {
                powerProjection = null;
            } else if (powerProjection >= 100d) {
                powerProjection = 100d;
            }
        }

        if (!Objects.equals(powerProjection, getCurrentPowerProjection())) {
            setCurrentPowerProjection(powerProjection);
        }
    }

    public Integer getNumOfTradeEmbargos() {
        return this.item.getVarAsInt("num_of_trade_embargos");
    }

    public Integer getNumOfNonRivalTradeEmbargos() {
        return this.item.getVarAsInt("num_of_non_rival_trade_embargos");
    }

    public Double getNavyStrength() {
        return this.item.getVarAsDouble("navy_strength");
    }

    public SaveCountry getPreferredEmperor() {
        return this.save.getCountry(this.item.getVarAsString("preferred_emperor"));
    }

    public Parliament getParliament() {
        ClausewitzItem parliamentItem = this.item.getChild("parliament");
        return parliamentItem != null && parliamentItem.getNbChildren() > 0 ? new Parliament(parliamentItem, this.save.getGame()) : null;
    }

    public Double getTariff() {
        return this.item.getVarAsDouble("tariff");
    }

    public void setTariff(double tariff) {
        this.item.setVariable("tariff", tariff);
    }

    public Integer getTotalWarWorth() {
        return this.item.getVarAsInt("total_war_worth");
    }

    public Integer getNumOwnedHomeCores() {
        return NumbersUtils.intOrDefault(this.item.getVarAsInt("num_owned_home_cores"));
    }

    public Double getNonOverseasDevelopment() {
        return NumbersUtils.doubleOrDefault(this.item.getVarAsDouble("non_overseas_development"));
    }

    public Integer getNumShipsPrivateering() {
        return NumbersUtils.intOrDefault(this.item.getVarAsInt("num_ships_privateering"));
    }

    public Integer getNumOfControlledCities() {
        return NumbersUtils.intOrDefault(this.item.getVarAsInt("num_of_controlled_cities"));
    }

    public Integer getNumOfPorts() {
        return NumbersUtils.intOrDefault(this.item.getVarAsInt("num_of_ports"));
    }

    public Integer getNumOfCorePorts() {
        return NumbersUtils.intOrDefault(this.item.getVarAsInt("num_of_core_ports"));
    }

    public Integer getNumOfTotalPorts() {
        return NumbersUtils.intOrDefault(this.item.getVarAsInt("num_of_total_ports"));
    }

    public Integer getNumOfCardinals() {
        return NumbersUtils.intOrDefault(this.item.getVarAsInt("num_of_cardinals"));
    }

    public Integer getNumOfMercenaries() {
        return NumbersUtils.intOrDefault(this.item.getVarAsInt("num_of_mercenaries"));
    }

    public Integer getNumOfRegulars() {
        return NumbersUtils.intOrDefault(this.item.getVarAsInt("num_of_regulars"));
    }

    public Integer getNumOfCities() {
        return NumbersUtils.intOrDefault(this.item.getVarAsInt("num_of_cities"));
    }

    public Integer getNumOfProvincesInStates() {
        return NumbersUtils.intOrDefault(this.item.getVarAsInt("num_of_provinces_in_states"));
    }

    public Integer getNumOfProvincesInTerritories() {
        return NumbersUtils.intOrDefault(this.item.getVarAsInt("num_of_provinces_in_territories"));
    }

    public Integer getNumOfForts() {
        return NumbersUtils.intOrDefault(this.item.getVarAsInt("forts"));
    }

    public Integer getNumOfAllies() {
        return NumbersUtils.intOrDefault(this.item.getVarAsInt("num_of_allies"));
    }

    public Integer getNumOfRoyalMarriages() {
        return NumbersUtils.intOrDefault(this.item.getVarAsInt("num_of_royal_marriages"));
    }

    public void addRoyalMarriage(SaveCountry country) {
        Integer nbRoyalMarriages = getNumOfRoyalMarriages();

        if (nbRoyalMarriages == null) {
            nbRoyalMarriages = 0;
        }

        this.item.setVariable("num_of_royal_marriages", nbRoyalMarriages + 1);
    }

    public void removeRoyalMarriage(SaveCountry country) {
        Integer nbRoyalMarriages = getNumOfRoyalMarriages();

        if (nbRoyalMarriages == null) {
            nbRoyalMarriages = 1;
        }

        this.item.setVariable("num_of_royal_marriages", nbRoyalMarriages - 1);
    }

    public Integer getNumOfSubjects() {
        return NumbersUtils.intOrDefault(this.item.getVarAsInt("num_of_subjects"));
    }

    public Integer getNumOfHeathenProvs() {
        return NumbersUtils.intOrDefault(this.item.getVarAsInt("num_of_heathen_provs"));
    }

    public Double getInlandSeaRatio() {
        return this.item.getVarAsDouble("inland_sea_ratio");
    }

    public Boolean hasFriendlyReformationCenter() {
        return this.item.getVarAsBool("has_friendly_reformation_center=yes");
    }

    public Double getAverageUnrest() {
        return this.item.getVarAsDouble("average_unrest");
    }

    public Double getAverageEffectiveUnrest() {
        return this.item.getVarAsDouble("average_effective_unrest");
    }

    public Double getAverageAutonomy() {
        return this.item.getVarAsDouble("average_autonomy");
    }

    public Double getAverageAutonomyAboveMin() {
        return this.item.getVarAsDouble("average_autonomy_above_min");
    }

    public Double getAverageHomeAutonomy() {
        return this.item.getVarAsDouble("average_home_autonomy");
    }

    public List<String> getFriends() {
        ClausewitzList list = this.item.getList("friend_tags");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues();
    }

    public Map<Integer, Integer> getNumOfBuildingsIndexed() {
        ClausewitzItem child = this.item.getChild("num_of_buildings_indexed");

        if (child == null) {
            return new HashMap<>();
        }

        return child.getVariables()
                    .stream()
                    .collect(Collectors.toMap(var -> Integer.parseInt(var.getName()), ClausewitzVariable::getAsInt));
    }

    public Map<Integer, Integer> getNumOfBuildingsUnderConstructionIndexed() {
        ClausewitzItem child = this.item.getChild("num_of_buildings_under_construction_indexed");

        if (child == null) {
            return new HashMap<>();
        }

        return child.getVariables()
                    .stream()
                    .collect(Collectors.toMap(var -> Integer.parseInt(var.getName()), ClausewitzVariable::getAsInt));
    }

    public Map<TradeGood, Double> getProducedGoodsValue() {
        ClausewitzList list = this.item.getList("produced_goods_value");
        Map<TradeGood, Double> map = new LinkedHashMap<>();

        if (list == null) {
            return map;
        }

        for (int i = 1; i < list.size(); i++) {
            map.put(this.save.getGame().getTradeGood(i - 1), list.getAsDouble(i));
        }

        return map;
    }

    public Map<TradeGood, Integer> getNumOfGoodsProduced() {
        ClausewitzList list = this.item.getList("num_of_goods_produced");
        Map<TradeGood, Integer> map = new LinkedHashMap<>();

        if (list == null) {
            return map;
        }

        for (int i = 1; i < list.size(); i++) {
            map.put(this.save.getGame().getTradeGood(i - 1), list.getAsInt(i));
        }

        return map;
    }

    public Map<TradeGood, Double> getTraded() {
        ClausewitzList list = this.item.getList("traded");
        Map<TradeGood, Double> map = new LinkedHashMap<>();

        if (list == null) {
            return map;
        }

        for (int i = 1; i < list.size(); i++) {
            map.put(this.save.getGame().getTradeGood(i - 1), list.getAsDouble(i));
        }

        return map;
    }

    public Map<Integer, Integer> getNumOfReligionsIndexed() {
        ClausewitzItem child = this.item.getChild("num_of_religions_indexed");

        if (child == null) {
            return new HashMap<>();
        }

        return child.getVariables()
                    .stream()
                    .collect(Collectors.toMap(var -> Integer.parseInt(var.getName()), ClausewitzVariable::getAsInt));
    }

    public Map<Integer, Double> getNumOfReligionsDev() {
        ClausewitzItem child = this.item.getChild("num_of_religions_dev");

        if (child == null) {
            return new HashMap<>();
        }

        return child.getVariables()
                    .stream()
                    .collect(Collectors.toMap(var -> Integer.parseInt(var.getName()), ClausewitzVariable::getAsDouble));
    }

    public List<Integer> getNumOfLeaders() {
        ClausewitzList list = this.item.getList("num_of_leaders");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValuesAsInt();
    }

    public List<Integer> getNumOfLeadersWithTraits() {
        ClausewitzList list = this.item.getList("num_of_leaders_with_traits");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValuesAsInt();
    }

    public List<Integer> getNumOfFreeLeaders() {
        ClausewitzList list = this.item.getList("num_of_free_leaders");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValuesAsInt();
    }

    public Map<Integer, Integer> getNumOfSubjectCountIndexed() {
        ClausewitzItem child = this.item.getChild("num_of_subject_count_indexed");

        if (child == null) {
            return new HashMap<>();
        }

        return child.getVariables()
                    .stream()
                    .collect(Collectors.toMap(var -> Integer.parseInt(var.getName()), ClausewitzVariable::getAsInt));
    }

    public Map<Integer, Double> getBorderPct() {
        ClausewitzItem child = this.item.getChild("border_pct");

        if (child == null) {
            return new HashMap<>();
        }

        return child.getVariables()
                    .stream()
                    .collect(Collectors.toMap(var -> Integer.parseInt(var.getName()), ClausewitzVariable::getAsDouble));
    }

    public Map<Integer, Double> getBorderSit() {
        ClausewitzItem child = this.item.getChild("border_sit");

        if (child == null) {
            return new HashMap<>();
        }

        return child.getVariables()
                    .stream()
                    .collect(Collectors.toMap(var -> Integer.parseInt(var.getName()), ClausewitzVariable::getAsDouble));
    }

    public List<SaveProvince> getBorderProvinces() {
        ClausewitzList list = this.item.getList("border_provinces");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValuesAsInt().stream().map(this.save::getProvince).toList();
    }

    public List<SaveCountry> getNeighbours() {
        ClausewitzList list = this.item.getList("neighbours");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues().stream().map(this.save::getCountry).toList();
    }

    public List<SaveCountry> getHomeNeighbours() {
        ClausewitzList list = this.item.getList("home_neighbours");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues().stream().map(this.save::getCountry).toList();
    }

    public List<SaveCountry> getCoreNeighbours() {
        ClausewitzList list = this.item.getList("core_neighbours");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues().stream().map(this.save::getCountry).toList();
    }

    public List<SaveCountry> getCurrentAtWarWith() {
        ClausewitzList list = this.item.getList("current_at_war_with");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues().stream().map(this.save::getCountry).toList();
    }

    public List<SaveCountry> getCurrentWarAllies() {
        ClausewitzList list = this.item.getList("current_war_allies");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues().stream().map(this.save::getCountry).toList();
    }

    public List<SaveCountry> getCallToArmsFriends() {
        ClausewitzList list = this.item.getList("call_to_arms_friends");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues().stream().map(this.save::getCountry).toList();
    }

    public List<SaveCountry> getAllies() {
        ClausewitzList list = this.item.getList("allies");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues().stream().map(this.save::getCountry).toList();
    }

    public void addAlly(SaveCountry ally) {
        ClausewitzList list = this.item.getList("allies");

        if (list == null) {
            this.item.addList("allies", ally.getTag());
        } else {
            list.add(ally.getTag());
        }

        Integer nbAllies = getNumOfAllies();

        if (nbAllies == null) {
            nbAllies = 0;
        }

        this.item.setVariable("num_of_allies", nbAllies + 1);
    }

    public void removeAlly(SaveCountry ally) {
        ClausewitzList list = this.item.getList("allies");

        if (list != null) {
            list.remove(ally.getTag());

            Integer nbAllies = getNumOfAllies();

            if (nbAllies == null) {
                nbAllies = 1;
            }

            this.item.setVariable("num_of_allies", nbAllies - 1);
        }
    }

    public List<SaveCountry> getSubjects() {
        ClausewitzList list = this.item.getList("subjects");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues().stream().map(this.save::getCountry).toList();
    }

    public void addSubject(SaveCountry subject) {
        ClausewitzList list = this.item.getList("subjects");

        if (list == null) {
            this.item.addList("subjects", subject.getTag());
        } else {
            list.add(subject.getTag());
        }

        Integer nbSubjects = getNumOfSubjects();

        if (nbSubjects == null) {
            nbSubjects = 0;
        }

        this.item.setVariable("num_of_subjects", nbSubjects + 1);
    }

    public void removeSubject(SaveCountry subject) {
        ClausewitzList list = this.item.getList("subjects");

        if (list != null) {
            list.remove(subject.getTag());

            Integer nbSubjects = getNumOfSubjects();

            if (nbSubjects == null) {
                nbSubjects = 1;
            }

            this.item.setVariable("num_of_subjects", nbSubjects - 1);
        }
    }

    public int getNumOfSubjectsOfType(String type) {
        return (int) getSubjects().stream().filter(subject -> type.equalsIgnoreCase(subject.getSubjectType().getName())).count();
    }

    public int getNumOfLargeColonies() {
        return (int) getSubjects().stream()
                                  .filter(subject -> this.equals(subject.getColonialParent())
                                                     && subject.getOwnedProvinces().stream().filter(Predicate.not(SaveProvince::isColony)).count()
                                                        >= this.save.getGame().getLargeColonialNationLimit())
                                  .count();
    }

    public int getNumOfStrongCompanies() {
        return (int) getTradeCompanies().stream().filter(SaveTradeCompany::strongCompany).count();
    }

    public List<SaveCountry> getIndependenceSupportedBy() {
        ClausewitzList list = this.item.getList("support_independence");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues().stream().map(this.save::getCountry).toList();
    }

    public void addIndependenceSupportedBy(SaveCountry supporter) {
        ClausewitzList list = this.item.getList("support_independence");

        if (list == null) {
            this.item.addList("support_independence", supporter.getTag());
        } else {
            list.add(supporter.getTag());
        }
    }

    public void removeIndependenceSupportedBy(SaveCountry supporter) {
        ClausewitzList list = this.item.getList("support_independence");

        if (list != null) {
            list.remove(supporter.getTag());
        }
    }

    public List<SaveCountry> getGuarantees() {
        ClausewitzList list = this.item.getList("guarantees");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues().stream().map(this.save::getCountry).toList();
    }

    public void addGuarantee(SaveCountry country) {
        ClausewitzList list = this.item.getList("guarantees");
        String guarantee = country.getTag();

        if (list == null) {
            this.item.addList("guarantees", guarantee);
        } else {
            list.add(guarantee);
        }
    }

    public void removeGuarantee(SaveCountry guarantee) {
        ClausewitzList list = this.item.getList("guarantees");

        if (list != null) {
            list.remove(guarantee.getTag());
        }
    }

    public List<SaveCountry> getWarnings() {
        ClausewitzList list = this.item.getList("warnings");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues().stream().map(this.save::getCountry).toList();
    }

    public void addWarning(SaveCountry country) {
        ClausewitzList list = this.item.getList("warnings");
        String warning = ClausewitzUtils.removeQuotes(country.getTag());

        if (list == null) {
            this.item.addList("warnings", warning);
        } else {
            list.add(warning);
        }
    }

    public void removeWarning(SaveCountry warning) {
        ClausewitzList list = this.item.getList("warnings");

        if (list != null) {
            list.remove(ClausewitzUtils.removeQuotes(warning.getTag()));
        }
    }

    public List<SaveCountry> getCondottieriClient() {
        ClausewitzList list = this.item.getList("condottieri_client");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues().stream().map(this.save::getCountry).toList();
    }

    public List<SaveCountry> getTradeEmbargoedBy() {
        ClausewitzList list = this.item.getList("trade_embargoed_by");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues().stream().map(this.save::getCountry).toList();
    }

    public List<SaveCountry> getTradeEmbargoes() {
        ClausewitzList list = this.item.getList("trade_embargoes");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues().stream().map(this.save::getCountry).toList();
    }

    public List<SaveCountry> getTransferTradePowerFrom() {
        ClausewitzList list = this.item.getList("transfer_trade_power_from");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues().stream().map(this.save::getCountry).toList();
    }

    public void addTransferTradePowerFrom(SaveCountry country) {
        ClausewitzList list = this.item.getList("transfer_trade_power_from");

        if (list == null) {
            this.item.addList("transfer_trade_power_from", country.getTag());
        } else {
            list.add(country.getTag());
        }
    }

    public void removeTransferTradePowerFrom(SaveCountry country) {
        ClausewitzList list = this.item.getList("transfer_trade_power_from");

        if (list != null) {
            list.remove(country.getTag());
        }
    }

    public List<SaveCountry> getTransferTradePowerTo() {
        ClausewitzList list = this.item.getList("ransfer_trade_power_to");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues().stream().map(this.save::getCountry).toList();
    }

    public void addTransferTradePowerTo(SaveCountry country) {
        ClausewitzList list = this.item.getList("ransfer_trade_power_to");

        if (list == null) {
            this.item.addList("ransfer_trade_power_to", country.getTag());
        } else {
            list.add(country.getTag());
        }
    }

    public void removeTransferTradePowerTo(SaveCountry country) {
        ClausewitzList list = this.item.getList("ransfer_trade_power_to");

        if (list != null) {
            list.remove(country.getTag());
        }
    }

    public Double getCardScore() {
        return this.item.getVarAsDouble("card_score");
    }

    public Map<Power, Double> getScoreRating() {
        ClausewitzList list = this.item.getList("score_rating");
        Map<Power, Double> scoreRatings = new EnumMap<>(Power.class);

        if (list == null) {
            return scoreRatings;
        }

        for (Power power : Power.values()) {
            scoreRatings.put(power, list.getAsDouble(power.ordinal()));
        }

        return scoreRatings;
    }

    public Map<Power, Integer> getScoreRank() {
        ClausewitzList list = this.item.getList("score_rank");
        Map<Power, Integer> scoreRanks = new EnumMap<>(Power.class);

        if (list == null) {
            return scoreRanks;
        }

        for (Power power : Power.values()) {
            scoreRanks.put(power, list.getAsInt(power.ordinal()));
        }

        return scoreRanks;
    }

    public List<Double> getAgeScore() {
        ClausewitzList list = this.item.getList("age_score");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValuesAsDouble();
    }

    public void setAgeScore(int age, double score) {
        ClausewitzList list = this.item.getList("age_score");

        if (list == null) {
            Double[] scores = new Double[age + 1];

            for (int i = 0; i < age; i++) {
                scores[i] = 0d;
            }

            scores[age] = score;

            this.item.addList("age_score", scores);
        } else {
            if (list.getValues().size() > age) {
                list.set(age, score);
            }
        }
    }

    public List<Double> getVictoryCardAgeScore() {
        ClausewitzList list = this.item.getList("vc_age_score");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValuesAsDouble();
    }

    public void setVictoryCardAgeScore(int age, double score) {
        ClausewitzList list = this.item.getList("vc_age_score");

        if (list == null) {
            Double[] scores = new Double[age + 1];

            for (int i = 0; i < age; i++) {
                scores[i] = 0d;
            }

            scores[age] = score;

            this.item.addList("vc_age_score", scores);
        } else {
            if (list.getValues().size() > age) {
                list.set(age, score);
            }
        }
    }

    public Integer getScorePlace() {
        return this.item.getVarAsInt("score_place");
    }

    public Double getPrestige() {
        return this.item.getVarAsDouble("prestige");
    }

    public void setPrestige(Double prestige) {
        if (prestige < -100d) {
            prestige = -100d;
        } else if (prestige > 100d) {
            prestige = 100d;
        }

        this.item.setVariable("prestige", prestige);
    }

    public Integer getStability() {
        Double stability = this.item.getVarAsDouble("stability");

        if (stability == null) {
            return null;
        } else {
            return stability.intValue();
        }
    }

    public void setStability(Integer stability) {
        if (stability < -3) {
            stability = -3;
        } else if (stability > 3) {
            stability = 3;
        }

        this.item.setVariable("stability", (double) stability);
    }

    public Double getTreasury() {
        return this.item.getVarAsDouble("treasury");
    }

    public void setTreasury(Double treasury) {
        if (treasury > 1000000d) {
            treasury = 1000000d;
        }

        this.item.setVariable("treasury", treasury);
    }

    public Double getEstimatedMonthlyIncome() {
        return this.item.getVarAsDouble("estimated_monthly_income");
    }

    public Double getInflation() {
        return this.item.getVarAsDouble("inflation");
    }

    public void setInflation(Double inflation) {
        if (inflation < 0d) {
            inflation = 0d;
        } else if (inflation.intValue() >= Integer.MAX_VALUE) {
            inflation = (double) Integer.MAX_VALUE;
        }

        this.item.setVariable("inflation", inflation);
    }

    public List<Double> getInflationHistory() {
        ClausewitzList list = this.item.getList("inflation_history");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValuesAsDouble();
    }

    public List<Integer> getOpinionCache() {
        ClausewitzList list = this.item.getList("opinion_cache");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValuesAsInt();
    }

    /**
     * Units under construction: infantry, cavalry, artillery, heavy, light, gallay, transport
     */
    public List<Integer> getUnderConstruction() {
        ClausewitzList list = this.item.getList("under_construction");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValuesAsInt();
    }

    public List<Integer> getUnderConstructionQueued() {
        ClausewitzList list = this.item.getList("under_construction_queued");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValuesAsInt();
    }

    public List<Integer> getTotalCount() {
        ClausewitzList list = this.item.getList("total_count");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValuesAsInt();
    }

    public List<SaveProvince> getOwnedProvinces() {
        ClausewitzList list = this.item.getList("owned_provinces");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValuesAsInt().stream().map(this.save::getProvince).toList();
    }

    public void addOwnedProvince(SaveProvince province) {
        ClausewitzList list = this.item.getList("owned_provinces");

        if (list == null) {
            list = this.item.addList("owned_provinces", this.item.getList("total_count").getOrder() + 1, true, true, true);
        }

        if (!list.contains(province.getId())) {
            list.add(province.getId());

            if (!getContinents().contains(province.getContinent())) {
                List<ProvinceList> continents = this.save.getGame().getContinents();
                for (int i = 0; i < continents.size(); i++) {
                    ProvinceList continent = continents.get(i);
                    if (continent.equals(province.getContinent())) {
                        this.item.getList("continent").set(i, 1);
                    }
                }
            }

            if (list.size() == 1) {
                setCapital(province);
                setTradePort(province.getId());
            }
        }
    }

    public void removeOwnedProvince(SaveProvince province) {
        ClausewitzList list = this.item.getList("owned_provinces");

        if (list != null) {
            Integer index = null;
            for (int i = 0; i < list.size(); i++) {
                if (Objects.equals(list.getAsInt(i), province.getId())) {
                    index = i;
                    break;
                }
            }

            if (index != null) {
                list.remove(index);
            }
        }
    }

    public List<SaveProvince> getControlledProvinces() {
        ClausewitzList list = this.item.getList("controlled_provinces");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValuesAsInt().stream().map(this.save::getProvince).toList();
    }

    public void addControlledProvince(SaveProvince province) {
        ClausewitzList list = this.item.getList("controlled_provinces");

        if (list == null) {
            list = this.item.addList("controlled_provinces", this.item.getList("total_count").getOrder() + 1, true, true, true);
        }

        if (!list.contains(province.getId())) {
            list.add(province.getId());
        }
    }

    public void removeControlledProvince(SaveProvince province) {
        ClausewitzList list = this.item.getList("controlled_provinces");

        if (list != null) {
            Integer index = null;
            for (int i = 0; i < list.size(); i++) {
                if (Objects.equals(list.getAsInt(i), province.getId())) {
                    index = i;
                    break;
                }
            }

            if (index != null) {
                list.remove(index);
            }
        }
    }

    public List<SaveProvince> getCoreProvinces() {
        ClausewitzList list = this.item.getList("core_provinces");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValuesAsInt().stream().map(this.save::getProvince).toList();
    }

    public void addCoreProvince(SaveProvince province) {
        ClausewitzList list = this.item.getList("core_provinces");

        if (list == null) {
            list = this.item.addList("core_provinces", this.item.getList("total_count").getOrder() + 1, true, true, true);
        }

        if (!list.contains(province.getId())) {
            list.add(province.getId());
        }
    }

    public void removeCoreProvince(SaveProvince province) {
        ClausewitzList list = this.item.getList("core_provinces");

        if (list != null) {
            Integer index = null;
            for (int i = 0; i < list.size(); i++) {
                if (Objects.equals(list.getAsInt(i), province.getId())) {
                    index = i;
                    break;
                }
            }

            if (index != null) {
                list.remove(index);
            }
        }
    }

    public List<SaveProvince> getClaimProvinces() {
        ClausewitzList list = this.item.getList("claim_provinces");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValuesAsInt().stream().map(this.save::getProvince).toList();
    }

    public void addClaimProvince(SaveProvince province) {
        ClausewitzList list = this.item.getList("claim_provinces");

        if (list == null) {
            list = this.item.addList("claim_provinces", this.item.getList("total_count").getOrder() + 1, true, true, true);
        }

        if (!list.contains(province.getId())) {
            list.add(province.getId());
        }
    }

    public void removeClaimProvince(SaveProvince province) {
        ClausewitzList list = this.item.getList("claim_provinces");

        if (list != null) {
            Integer index = null;
            for (int i = 0; i < list.size(); i++) {
                if (Objects.equals(list.getAsInt(i), province.getId())) {
                    index = i;
                    break;
                }
            }

            if (index != null) {
                list.remove(index);
            }
        }
    }

    public List<Integer> getIdeaMayCache() {
        ClausewitzList list = this.item.getList("idea_may_cache");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValuesAsInt();
    }

    public Boolean updateOpinionCache() {
        return this.item.getVarAsBool("update_opinion_cache");
    }

    public Boolean needsRefresh() {
        return this.item.getVarAsBool("needs_refresh");
    }

    public Boolean casusBellisRefresh() {
        return this.item.getVarAsBool("casus_bellis_refresh");
    }

    public Boolean needsRebelUnitRefresh() {
        return this.item.getVarAsBool("needs_rebel_unit_refresh");
    }

    public LocalDate lastBetrayedAlly() {
        return this.item.getVarAsDate("last_betrayed_ally");
    }

    public void setLastBetrayedAlly(LocalDate lastBetrayedAlly) {
        this.item.setVariable("last_betrayed_ally", lastBetrayedAlly);
    }

    public LocalDate lastBankrupt() {
        return this.item.getVarAsDate("last_bankrupt");
    }

    public void setLastBankrupt(LocalDate lastBankrupt) {
        this.item.setVariable("last_bankrupt", lastBankrupt);
    }

    public Double getWarExhaustion() {
        return this.item.getVarAsDouble("war_exhaustion");
    }

    public void setWarExhaustion(double warExhaustion) {
        this.item.setVariable("war_exhaustion", warExhaustion);
    }

    public Double getMonthlyWarExhaustion() {
        return this.item.getVarAsDouble("monthly_war_exhaustion");
    }

    public Boolean canTakeWartaxes() {
        return this.item.getVarAsBool("can_take_wartaxes");
    }

    public Boolean warTaxes() {
        return this.item.getVarAsBool("wartax");
    }

    public Double getLandMaintenance() {
        return this.item.getVarAsDouble("land_maintenance");
    }

    public void setLandMaintenance(Double landMaintenance) {
        if (landMaintenance < 0d) {
            landMaintenance = 0d;
        } else if (landMaintenance > 1d) {
            landMaintenance = 1d;
        }

        this.item.setVariable("land_maintenance", landMaintenance);
    }

    public Double getNavalMaintenance() {
        return this.item.getVarAsDouble("naval_maintenance");
    }

    public void setNavalMaintenance(Double navalMaintenance) {
        if (navalMaintenance < 0d) {
            navalMaintenance = 0d;
        } else if (navalMaintenance > 1d) {
            navalMaintenance = 1d;
        }

        this.item.setVariable("naval_maintenance", navalMaintenance);
    }

    public Double getColonialMaintenance() {
        return this.item.getVarAsDouble("colonial_maintenance");
    }

    public void setColonialMaintenance(Double colonialMaintenance) {
        if (colonialMaintenance < 0d) {
            colonialMaintenance = 0d;
        } else if (colonialMaintenance > 1d) {
            colonialMaintenance = 1d;
        }

        this.item.setVariable("colonial_maintenance", colonialMaintenance);
    }

    public Double getMissionaryMaintenance() {
        return this.item.getVarAsDouble("missionary_maintenance");
    }

    public void setMissionaryMaintenance(Double missionaryMaintenance) {
        if (missionaryMaintenance < 0d) {
            missionaryMaintenance = 0d;
        } else if (missionaryMaintenance > 1d) {
            missionaryMaintenance = 1d;
        }

        this.item.setVariable("missionary_maintenance", missionaryMaintenance);
    }

    public Double getArmyTradition() {
        return this.item.getVarAsDouble("army_tradition");
    }

    public void setArmyTradition(Double armyTradition) {
        if (armyTradition < 0d) {
            armyTradition = 0d;
        } else if (armyTradition > 100d) {
            armyTradition = 100d;
        }

        this.item.setVariable("army_tradition", armyTradition);
    }

    public Double getNavyTradition() {
        return this.item.getVarAsDouble("navy_tradition");
    }

    public void setNavyTradition(Double navyTradition) {
        if (navyTradition < 0d) {
            navyTradition = 0d;
        } else if (navyTradition > 100d) {
            navyTradition = 100d;
        }

        this.item.setVariable("navy_tradition", navyTradition);
    }

    public LocalDate getLastWarEnded() {
        return this.item.getVarAsDate("last_war_ended");
    }

    public Integer getNumUncontestedCores() {
        return this.item.getVarAsInt("num_uncontested_cores");
    }

    public Ledger getLedger() {
        ClausewitzItem ledgerItem = this.item.getChild("ledger");
        return ledgerItem != null ? new Ledger(ledgerItem) : null;
    }

    public Integer getCancelledLoans() {
        return this.item.getVarAsInt("cancelled_loans");
    }

    public Integer getLoanSize() {
        return this.item.getVarAsInt("loan_size");
    }

    /**
     * Estimation of interests for a loan (ie: 100 = get 300 ducas, need to give back 400)
     */
    public Double getEstimatedLoan() {
        return this.item.getVarAsDouble("estimated_loan");
    }

    public List<Loan> getLoans() {
        return this.item.getChildren("loan").stream().map(Loan::new).toList();
    }

    public void addLoan(double interest, int amount, LocalDate expiryDate) {
        Loan.addToItem(this.item, this.save.getIds().get(4713).incrementId(2), interest, true, amount, expiryDate);
    }

    public void removeLoan(int id) {
        Integer index = null;
        List<Loan> list = getLoans();

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().getId().equals(id)) {
                index = i;
                break;
            }
        }

        if (index != null) {
            this.item.removeChild("loan", index);
        }
    }

    public Double getReligiousUnity() {
        return this.item.getVarAsDouble("religious_unity");
    }

    public void setRepublicanTradition(Double republicanTradition) {
        if (republicanTradition < 0d) {
            republicanTradition = 0d;
        } else if (republicanTradition > 100d) {
            republicanTradition = 100d;
        }

        this.item.setVariable("republican_tradition", republicanTradition);
    }

    public Double getRepublicanTradition() {
        return this.item.getVarAsDouble("republican_tradition");
    }

    public void setDevotion(Double devotion) {
        if (devotion < 0d) {
            devotion = 0d;
        } else if (devotion > 100d) {
            devotion = 100d;
        }

        this.item.setVariable("devotion", devotion);
    }

    public Double getDevotion() {
        return this.item.getVarAsDouble("devotion");
    }

    public Double getMeritocracy() {
        return this.item.getVarAsDouble("meritocracy");
    }

    public void setMeritocracy(Double meritocracy) {
        if (meritocracy < 0d) {
            meritocracy = 0d;
        } else if (meritocracy > 100d) {
            meritocracy = 100d;
        }

        this.item.setVariable("meritocracy", meritocracy);
    }

    public Double getPiety() {
        return NumbersUtils.doubleOrDefault(this.item.getVarAsDouble("piety")) * 100;
    }

    public void setPiety(double piety) {
        piety = piety / 100;
        if (piety < 0d) {
            piety = 0d;
        } else if (piety > 1d) {
            piety = 1d;
        }

        this.item.setVariable("piety", piety);
    }

    public Double getPatriarchAuthority() {
        return NumbersUtils.doubleOrDefault(this.item.getVarAsDouble("patriarch_authority")) * 100;
    }

    public void setPatriarchAuthority(double patriarchAuthority) {
        patriarchAuthority = patriarchAuthority / 100;

        if (patriarchAuthority < 0d) {
            patriarchAuthority = 0d;
        } else if (patriarchAuthority > 1d) {
            patriarchAuthority = 1d;
        }

        this.item.setVariable("patriarch_authority", patriarchAuthority);
    }

    public Integer getCurrentIcon() {
        return this.item.getVarAsInt("current_icon");
    }

    public void setPatriarchAuthority(int icon) {
        if (getReligion() != null && getReligion().getGameReligion().getIcons() != null && icon >= 0 && icon < getReligion().getGameReligion()
                                                                                                                            .getIcons()
                                                                                                                            .size()) {
            this.item.setVariable("current_icon", icon);
        }
    }

    public List<String> getBlessings() {
        return this.item.getVarsAsStrings("blessing");
    }

    public void addBlessing(String ignoreDecision) {
        List<String> ignoreDecisions = this.item.getVarsAsStrings("blessing");

        if (!ignoreDecisions.contains(ClausewitzUtils.addQuotes(ignoreDecision))) {
            this.item.addVariable("blessing", ClausewitzUtils.addQuotes(ignoreDecision));
        }
    }

    public void removeBlessing(int index) {
        this.item.removeVariable("blessing", index);
    }

    public void removeBlessing(String blessing) {
        this.item.removeVariable("blessing", blessing);
    }

    public Double getPapalInfluence() {
        return this.item.getVarAsDouble("papal_influence");
    }

    public void setPapalInfluence(Double papalInfluence) {
        if (papalInfluence < 0d) {
            papalInfluence = 0d;
        } else if (papalInfluence > 100d) {
            papalInfluence = 100d;
        }

        this.item.setVariable("papal_influence", papalInfluence);
    }

    public Double getCorruption() {
        return this.item.getVarAsDouble("corruption");
    }

    public void setCorruption(Double corruption) {
        if (corruption < 0d) {
            corruption = 0d;
        } else if (corruption > 100d) {
            corruption = 100d;
        }

        this.item.setVariable("corruption", corruption);
    }

    public Double getRecoveryMotivation() {
        return this.item.getVarAsDouble("recovery_motivation");
    }

    public void setRecoveryMotivation(Double recoveryMotivation) {
        if (recoveryMotivation < 0d) {
            recoveryMotivation = 0d;
        } else if (recoveryMotivation > 100d) {
            recoveryMotivation = 100d;
        }

        this.item.setVariable("recovery_motivation", recoveryMotivation);
    }

    public Double getRootOutCorruptionSlider() {
        return this.item.getVarAsDouble("root_out_corruption_slider");
    }

    public Double getDoom() {
        return this.item.getVarAsDouble("doom");
    }

    public void setDoom(Double doom) {
        if (doom < 0d) {
            doom = 0d;
        } else if (doom > 100d) {
            doom = 100d;
        }

        this.item.setVariable("doom", doom);
    }

    public List<PersonalDeity> getUnlockedPersonalDeities() {
        return this.save.getGame()
                        .getPersonalDeities()
                        .stream()
                        .filter(PersonalDeity -> PersonalDeity.getAllow() == null || PersonalDeity.getAllow().apply(this, this))
                        .toList();
    }

    public PersonalDeity getPersonalDeity() {
        ClausewitzVariable variable = this.item.getVar("personal_deity");

        return variable == null ? null : this.save.getGame().getPersonalDeity(ClausewitzUtils.removeQuotes(variable.getValue()));
    }

    public void setPersonalDeity(PersonalDeity personalDeity) {
        if (personalDeity == null) {
            this.item.removeVariable("personal_deity");
        } else {
            this.item.setVariable("personal_deity", ClausewitzUtils.addQuotes(personalDeity.getName()));
        }
    }

    public List<FetishistCult> getUnlockedFetishistCults() {
        return this.save.getGame()
                        .getFetishistCults()
                        .stream()
                        .filter(fetishistCult -> fetishistCult.getAllow() == null || fetishistCult.getAllow().apply(this, this))
                        .toList();
    }

    public FetishistCult getFetishistCult() {
        ClausewitzVariable variable = this.item.getVar("fetishist_cult");

        return variable == null ? null : this.save.getGame().getFetishistCult(ClausewitzUtils.removeQuotes(variable.getValue()));
    }

    public void setFetishistCult(FetishistCult fetishistCult) {
        if (fetishistCult == null) {
            this.item.removeVariable("fetishist_cult");
        } else {
            this.item.setVariable("fetishist_cult", ClausewitzUtils.addQuotes(fetishistCult.getName()));
        }
    }

    public Double getAuthority() {
        return this.item.getVarAsDouble("authority");
    }

    public void setAuthority(Double authority) {
        if (authority < 0d) {
            authority = 0d;
        } else if (authority > 100d) {
            authority = 100d;
        }

        this.item.setVariable("authority", authority);
    }

    public Double getLegitimacy() {
        return this.item.getVarAsDouble("legitimacy");
    }

    public void setLegitimacy(Double legitimacy) {
        if (legitimacy < 0d) {
            legitimacy = 0d;
        } else if (legitimacy > 100d) {
            legitimacy = 100d;
        }

        this.item.setVariable("legitimacy", legitimacy);
    }

    public Double getLegitimacyEquivalent() {
        return Eu4Utils.coalesce(getLegitimacy(), getHordeUnity(), getRepublicanTradition(), getMeritocracy(), getDevotion());
    }

    public Double getHordeUnity() {
        return this.item.getVarAsDouble("horde_unity");
    }

    public void setHordeUnity(Double hordeUnity) {
        if (hordeUnity < 0d) {
            hordeUnity = 0d;
        } else if (hordeUnity > 100d) {
            hordeUnity = 100d;
        }

        this.item.setVariable("horde_unity", hordeUnity);
    }

    public Double getLegitimacyOrHordeUnity() {
        return Eu4Utils.coalesce(getLegitimacy(), getHordeUnity());
    }

    public Integer getMercantilism() {
        Double aDouble = this.item.getVarAsDouble("mercantilism");

        if (aDouble == null) {
            return null;
        } else {
            return aDouble.intValue();
        }
    }

    public void setMercantilism(Integer mercantilism) {
        if (mercantilism < 0) {
            mercantilism = 0;
        } else if (mercantilism > 100) {
            mercantilism = 100;
        }

        this.item.setVariable("mercantilism", (double) mercantilism);
    }

    public Integer getSplendor() {
        Double splendor = this.item.getVarAsDouble("splendor");

        if (splendor == null) {
            return null;
        } else {
            return splendor.intValue();
        }
    }

    public void setSplendor(Integer splendor) {
        if (splendor < 0) {
            splendor = 0;
        }

        this.item.setVariable("splendor", (double) splendor);
    }

    public Integer getAbsolutism() {
        return Optional.ofNullable(this.item.getVarAsDouble("absolutism")).map(Double::intValue).orElse(null);
    }

    public void setAbsolutism(Integer absolutism) {
        if (absolutism < 0) {
            absolutism = 0;
        } else if (absolutism > 100) {
            absolutism = 100;
        }

        this.item.setVariable("absolutism", absolutism);
    }

    public Double getArmyProfessionalism() {
        return this.item.getVarAsDouble("army_professionalism");
    }

    public void setArmyProfessionalism(Double armyProfessionalism) {
        if (armyProfessionalism < 0d) {
            armyProfessionalism = 0d;
        } else if (armyProfessionalism > 100d) {
            armyProfessionalism = 100d;
        }

        this.item.setVariable("army_professionalism", armyProfessionalism);
    }

    public Double getMaxHistoryArmyProfessionalism() {
        return this.item.getVarAsDouble("max_historic_army_professionalism");
    }

    public void setMaxHistoryArmyProfessionalism(Double armyProfessionalism) {
        if (armyProfessionalism < 0d) {
            armyProfessionalism = 0d;
        } else if (armyProfessionalism > 100d) {
            armyProfessionalism = 100d;
        }

        this.item.setVariable("max_historic_army_professionalism", armyProfessionalism);
    }

    public String getActiveDisaster() {
        return this.item.getVarAsString("active_disaster");
    }

    public void setActiveDisaster(String activeDisaster) {
        this.item.setVariable("active_disaster", activeDisaster);
    }

    public Church getChurch() {
        ClausewitzItem churchItem = this.item.getChild("church");
        return churchItem != null ? new Church(churchItem, this.save.getGame()) : null;
    }

    public IdeaGroups getIdeaGroups() {
        ClausewitzItem activeIdeaGroupsItem = this.item.getChild("active_idea_groups");
        return activeIdeaGroupsItem != null ? new IdeaGroups(activeIdeaGroupsItem, this.save) : null;
    }

    public SaveReligiousReforms getReligiousReforms() {
        ClausewitzItem activeReligiousReformsItem = this.item.getChild("active_religious_reform");
        return activeReligiousReformsItem != null ? new SaveReligiousReforms(activeReligiousReformsItem, this.save.getGame()) : null;
    }

    public SaveNativeAdvancements getNativeAdvancements() {
        ClausewitzItem activeNativeAdvancementItem = this.item.getChild("active_native_advancement");
        return activeNativeAdvancementItem != null ? new SaveNativeAdvancements(activeNativeAdvancementItem) : null;
    }

    public SaveGovernment getGovernment() {
        ClausewitzItem governmentItem = this.item.getChild("government");
        return governmentItem != null ? new SaveGovernment(governmentItem, this.save.getGame(), this) : null;
    }

    public List<Envoy> getColonists() {
        ClausewitzItem colonistsItem = this.item.getChild("colonists");
        return colonistsItem != null ? colonistsItem.getChildren("envoy").stream().map(Envoy::new).toList() : new ArrayList<>();
    }

    public List<Envoy> getMerchants() {
        ClausewitzItem merchantsItem = this.item.getChild("merchants");
        return merchantsItem != null ? merchantsItem.getChildren("envoy").stream().map(Envoy::new).toList() : new ArrayList<>();
    }

    public List<Envoy> getMissionaries() {
        ClausewitzItem missionariesItem = this.item.getChild("missionaries");
        return missionariesItem != null ? missionariesItem.getChildren("envoy").stream().map(Envoy::new).toList() : new ArrayList<>();
    }

    public List<Envoy> getDiplomats() {
        ClausewitzItem diplomatsItem = this.item.getChild("diplomats");
        return diplomatsItem != null ? diplomatsItem.getChildren("envoy").stream().map(Envoy::new).toList() : new ArrayList<>();
    }

    public List<SaveModifier> getModifiers() {
        return this.item.getChildren("modifier").stream().map(child -> new SaveModifier(child, this.save.getGame())).toList();
    }

    public void addModifier(String modifier, LocalDate date) {
        addModifier(modifier, date, null);
    }

    public void addModifier(String modifier, LocalDate date, Boolean hidden) {
        SaveModifier.addToItem(this.item, modifier, date, hidden);
    }

    public void removeModifier(int index) {
        this.item.removeChild("modifier", index);
    }

    public void removeModifier(GameModifier modifier) {
        removeModifier(modifier.getName());
    }

    public void removeModifier(String modifier) {
        Integer index = null;
        modifier = ClausewitzUtils.addQuotes(modifier);

        List<SaveModifier> modifiers = getModifiers();
        for (int i = 0; i < modifiers.size(); i++) {
            if (modifiers.get(i).getModifierName().equalsIgnoreCase(modifier)) {
                index = i;
                break;
            }
        }

        if (index != null) {
            this.item.removeChild("modifier", index);
        }
    }

    public Double getManpower() {
        return this.item.getVarAsDouble("manpower");
    }

    public void setManpower(Double manpower) {
        this.item.setVariable("manpower", manpower);
    }

    public Double getMaxManpower() {
        return this.item.getVarAsDouble("max_manpower");
    }

    public Double getSailors() {
        return this.item.getVarAsDouble("sailors");
    }

    public void setSailors(Double sailors) {
        this.item.setVariable("sailors", sailors);
    }

    public Double getMaxSailors() {
        return this.item.getVarAsDouble("max_sailors");
    }

    public SubUnit getSubUnit() {
        ClausewitzItem subUnitItem = this.item.getChild("sub_unit");
        return subUnitItem != null ? new SubUnit(subUnitItem) : null;
    }

    public Integer getNumOfCapturedShipsWithBoardingDoctrine() {
        return this.item.getVarAsInt("num_of_captured_ships_with_boarding_doctrine");
    }

    public void setNumOfCapturedShipsWithBoardingDoctrine(Integer numOfCapturedShipsWithBoardingDoctrine) {
        this.item.setVariable("num_of_captured_ships_with_boarding_doctrine", numOfCapturedShipsWithBoardingDoctrine);
    }

    public Double getOverextensionPercentage() {
        return this.item.getVarAsDouble("overextension_percentage");
    }

    public Map<Integer, MercenaryCompany> getMercenaryCompanies() {
        return this.item.getChildren("mercenary_company")
                        .stream()
                        .map(armyItem -> new MercenaryCompany(armyItem, this))
                        .collect(Collectors.toMap(army -> army.getId().getId(), Function.identity()));
    }

    public Army getArmy(Id id) {
        return getArmies().get(id);
    }

    public Map<Id, Army> getArmies() {
        return this.item.getChildren("army")
                        .stream()
                        .map(armyItem -> new Army(armyItem, this))
                        .collect(Collectors.toMap(AbstractArmy::getId, Function.identity()));
    }

    public int getArmySize() {
        return getArmies().values().stream().mapToInt(army -> army.getRegiments().size()).sum();
    }

    public int getNavySize() {
        return getNavies().values().stream().mapToInt(army -> army.getRegiments().size()).sum();
    }

    public long getNbInfantry() {
        return getArmies().values()
                          .stream()
                          .mapToLong(army -> army.getRegiments().stream().filter(regiment -> UnitType.INFANTRY.equals(regiment.getUnitType())).count())
                          .sum();
    }

    public long getNbCavalry() {
        return getArmies().values()
                          .stream()
                          .mapToLong(army -> army.getRegiments().stream().filter(regiment -> UnitType.CAVALRY.equals(regiment.getUnitType())).count())
                          .sum();
    }

    public long getNbArtillery() {
        return getArmies().values()
                          .stream()
                          .mapToLong(army -> army.getRegiments().stream().filter(regiment -> UnitType.ARTILLERY.equals(regiment.getUnitType())).count())
                          .sum();
    }

    public long getNbHeavyShips() {
        return getNavies().values()
                          .stream()
                          .mapToLong(army -> army.getShips().stream().filter(regiment -> UnitType.HEAVY_SHIP.equals(regiment.getUnitType())).count())
                          .sum();
    }

    public long getNbLightShips() {
        return getNavies().values()
                          .stream()
                          .mapToLong(army -> army.getShips().stream().filter(regiment -> UnitType.LIGHT_SHIP.equals(regiment.getUnitType())).count())
                          .sum();
    }

    public long getNbGalleys() {
        return getNavies().values()
                          .stream()
                          .mapToLong(army -> army.getShips().stream().filter(regiment -> UnitType.GALLEY.equals(regiment.getUnitType())).count())
                          .sum();
    }

    public long getNbTransports() {
        return getNavies().values()
                          .stream()
                          .mapToLong(army -> army.getShips().stream().filter(regiment -> UnitType.TRANSPORT.equals(regiment.getUnitType())).count())
                          .sum();
    }

    public long getNbRegimentOf(String type) {
        return getArmies().values()
                          .stream()
                          .mapToLong(army -> army.getRegiments().stream().filter(regiment -> type.equals(regiment.getTypeName())).count())
                          .sum();
    }

    public long getNbRegimentOfCategory(int category) {
        return getArmies().values()
                          .stream()
                          .mapToLong(army -> army.getRegiments()
                                                 .stream()
                                                 .filter(regiment -> regiment.getCategory() != null && category == regiment.getCategory())
                                                 .count())
                          .sum();
    }

    public void addArmy(String name, int location, String graphicalCulture, String regimentName, String regimentType,
                        double regimentMorale, double regimentDrill) {
        //Todo location -> 		unit={
        //    //			id=6520
        //    //			type=54
        //    //		}
        Army.addToItem(this.item, this.save.getAndIncrementUnitIdCounter(), name, location, graphicalCulture,
                       this.save.getAndIncrementUnitIdCounter(), regimentName, location, regimentType, regimentMorale,
                       regimentDrill);
    }

    public Navy getNavy(Id id) {
        return getNavies().get(id);
    }

    public Map<Id, Navy> getNavies() {
        return this.item.getChildren("navy")
                        .stream()
                        .map(navyItem -> new Navy(navyItem, this))
                        .collect(Collectors.toMap(AbstractArmy::getId, Function.identity()));
    }

    public void addNavy(String name, int location, String graphicalCulture, String shipName, String shipType, double shipMorale) {
        //Todo location -> 		unit={
        //    //			id=6520
        //    //			type=54
        //    //		}
        Navy.addToItem(this.item, this.save.getAndIncrementUnitIdCounter(), name, location, graphicalCulture,
                       this.save.getAndIncrementUnitIdCounter(), shipName, location, shipType, shipMorale);
    }

    public void removeAeFor(String tag) {
        removeOpinionFor(tag, "\"aggressive_expansion\"");
    }

    public void removeOpinionFor(String tag, String modifier) {
        tag = ClausewitzUtils.removeQuotes(tag).toUpperCase();
        ClausewitzItem child = this.item.getChild("active_relations");

        if (child != null) {
            child = child.getChild(tag);

            if (child != null && child.getNbChildren() > 0) {
                child.removeChildIf(item -> "opinion".equals(item.getName()) && item.hasVar("modifier", modifier));
            }
        }
    }

    public Map<String, ActiveRelation> getActiveRelations() {
        ClausewitzItem activeRelationsItem = this.item.getChild("active_relations");
        return activeRelationsItem != null ? activeRelationsItem.getChildren()
                                                                .stream()
                                                                .map(child -> new ActiveRelation(child, this.save))
                                                                .collect(Collectors.toMap(ActiveRelation::getCountryTag, Function.identity())) : null;
    }

    public ActiveRelation getActiveRelation(SaveCountry country) {
        return getActiveRelations().get(country.getTag());
    }

    public Map<Integer, Leader> getLeaders() {
        Map<Integer, Leader> leaders = new HashMap<>();
        SaveCountryHistory history = getHistory();
        if (history.getLeaders() != null) {
            List<ClausewitzItem> leadersItems = this.item.getChildren("leader");
            if (!leadersItems.isEmpty()) {
                leadersItems.forEach(leaderItem -> {
                    Id leaderId = new Id(leaderItem);
                    Leader leader = history.getLeader(leaderId.getId());
                    if (leader != null) {
                        leaders.put(leaderId.getId(), leader);
                    }
                });
            }
        }

        return leaders;
    }

    public List<Leader> getLeadersOfType(LeaderType leaderType) {
        return getLeaders().values().stream().filter(leader -> leaderType.equals(leader.getType())).toList();
    }

    public void addLeader(LocalDate date, LocalDate birthDate, String name, LeaderType type, int manuever, int fire, int shock, int siege,
                          LeaderPersonality personality) {
        int leaderId = this.save.getIdCounters().getAndIncrement(Counter.LEADER);
        Id.addToItem(this.item, "leader", leaderId, 49, this.item.getChild("active_relations").getOrder() + 1);
        getHistory().addLeader(date, birthDate, name, type, manuever, fire, shock, siege, personality, leaderId);
    }

    public void removeLeader(int id) {
        Leader leader = getLeaders().get(id);

        if (leader != null) {
            getArmies().values()
                       .stream()
                       .filter(army -> army.getLeader().getId().equals(id))
                       .findFirst()
                       .ifPresent(AbstractArmy::removeLeader);

            List<Id> leadersIds = this.item.getChildren("leader").stream().map(Id::new).toList();

            for (int i = 0; i < leadersIds.size(); i++) {
                if (leadersIds.get(i).equals(leader.getId())) {
                    this.item.removeChild("leader", i);
                    break;
                }
            }
        }
    }

    public Integer getDecisionSeed() {
        return this.item.getVarAsInt("decision_seed");
    }

    public Monarch getMonarch() {
        SaveCountryHistory history = getHistory();

        if (history != null && history.getMonarchs() != null) {
            ClausewitzItem monarchItem = this.item.getChild("monarch");

            if (monarchItem != null) {
                Id monarchId = new Id(monarchItem);
                return history.getMonarch(monarchId.getId());
            }
        }

        return null;
    }

    public Heir getHeir() {
        SaveCountryHistory history = getHistory();

        if (history != null && history.getMonarchs() != null) {
            ClausewitzItem monarchItem = this.item.getChild("heir");

            if (monarchItem != null) {
                Id monarchId = new Id(monarchItem);
                return history.getHeir(monarchId.getId());
            }
        }

        return null;
    }

    public Queen getQueen() {
        SaveCountryHistory history = getHistory();

        if (history != null && history.getMonarchs() != null) {
            ClausewitzItem monarchItem = this.item.getChild("queen");

            if (monarchItem != null) {
                Id monarchId = new Id(monarchItem);
                return history.getQueen(monarchId.getId());
            }
        }

        return null;
    }

    public Monarch getConsort() {
        Queen queen = getQueen();
        return queen == null ? null : BooleanUtils.toBoolean(queen.getConsort()) ? queen : getMonarch();
    }

    public String getOriginalDynasty() {
        return this.item.getVarAsString("original_dynasty");
    }

    public void setOriginalDynasty(String originalDynasty) {
        this.item.setVariable("original_dynasty", originalDynasty);
    }

    public Integer getNumOfConsorts() {
        return this.item.getVarAsInt("num_of_consorts");
    }

    public void setNumOfConsorts(int numOfConsorts) {
        if (numOfConsorts < 0) {
            numOfConsorts = 0;
        }

        this.item.setVariable("num_of_consorts", numOfConsorts);
    }

    public int getNumOfRelations() {
        return (int) (getNumOfAllies() + getNumOfRoyalMarriages() + getNumOfSubjects()
                      + this.save.getDiplomacy().getGuarantees().stream().filter(guarantee -> this.equals(guarantee.getFirst())).count()
                      + this.save.getDiplomacy().getMilitaryAccesses().stream().filter(guarantee -> this.equals(guarantee.getSecond())).count());
    }

    public boolean isGreatPower() {
        return BooleanUtils.toBoolean(this.item.getVarAsBool("is_great_power"));
    }

    public LocalDate getInauguration() {
        return this.item.getVarAsDate("inauguration");
    }

    public LocalDate getLastMigration() {
        return this.item.getVarAsDate("last_migration");
    }

    public void setLastMigration(LocalDate lastMigration) {
        this.item.setVariable("last_migration", lastMigration);
    }

    public List<Id> getPreviousMonarchs() {
        return this.item.getChildren("previous_monarch").stream().map(Id::new).toList();
    }

    public Map<Integer, SaveAdvisor> getAdvisors() {
        return this.advisors;
    }

    public void addAdvisor(SaveAdvisor advisor) {
        this.advisors.put(advisor.getId().getId(), advisor);
    }

    public SaveAdvisor getAdvisor(int id) {
        return this.advisors.get(id);
    }

    public List<SaveAdvisor> getActiveAdvisors() {
        return this.save.getActiveAdvisors(this).stream().map(Id::getId).map(this::getAdvisor).filter(Objects::nonNull).toList();
    }

    public boolean getAssignedEstates() {
        return BooleanUtils.toBoolean(this.item.getVarAsBool("assigned_estates"));
    }

    public List<TradeGood> getTradedBonus() {
        ClausewitzList list = this.item.getList("traded_bonus");

        if (list != null) {
            return list.getValuesAsInt().stream().map(integer -> this.save.getGame().getTradeGood(integer - 1)).toList();
        }

        return new ArrayList<>();
    }

    public Map<Power, Integer> getPowers() {
        ClausewitzList list = this.item.getList("powers");
        Map<Power, Integer> powers = new EnumMap<>(Power.class);

        if (list == null) {
            return powers;
        }

        for (Power power : Power.values()) {
            powers.put(power, list.getAsInt(power.ordinal()));
        }

        return powers;
    }

    public void setPower(Power power, Integer value) {
        ClausewitzList list = this.item.getList("powers");

        if (list != null) {
            list.set(power.ordinal(), value);
        }
    }

    //1 = Force embargo
    //2 = Loyalists
    //3 = Pay army
    //4 = Scutage
    //5 = Officers
    //6 = Divert trade
    public List<Boolean> getSubjectInteractions() {
        ClausewitzList list = this.item.getList("subject_interactions");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValuesAsBool();
    }

    public List<SaveCountry> getHistoricalFriends() {
        ClausewitzList list = this.item.getList("historical_friends");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues().stream().map(ClausewitzUtils::removeQuotes).map(s -> this.save.getCountry(s)).toList();
    }

    public List<SaveCountry> getHistoricalRivals() {
        ClausewitzList list = this.item.getList("historical_rivals");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues().stream().map(ClausewitzUtils::removeQuotes).map(s -> this.save.getCountry(s)).toList();
    }

    public List<Integer> getInterestingCountries() {
        ClausewitzList list = this.item.getList("interesting_countries");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValuesAsInt();
    }

    public void addInterestingCountries(int countryId) {
        ClausewitzList list = this.item.getList("interesting_countries");

        if (!list.contains(countryId)) {
            list.add(countryId);
        }
    }

    public void removeInterestingCountry(int countryId) {
        ClausewitzList list = this.item.getList("interesting_countries");

        list.remove(Integer.toString(countryId));
    }

    public Double getBlockadedPercent() {
        return this.item.getVarAsDouble("blockaded_percent");
    }

    public List<String> getPreviousCountryTags() {
        return this.item.getVarsAsStrings("previous_country_tags");
    }

    public SortedMap<LocalDate, String> getChangedTags() {
        if (CollectionUtils.isEmpty(getPreviousCountryTags())) {
            return new TreeMap<>();
        } else {
            return getHistory().getEvents()
                               .stream()
                               .filter(h -> StringUtils.isNotBlank(h.getChangedTagFrom()))
                               .collect(Collectors.toMap(SaveCountryHistoryEvent::getDate, SaveCountryHistoryEvent::getChangedTagFrom, (a, b) -> a,
                                                         TreeMap::new));
        }
    }

    public String getTagAt(LocalDate date) {
        SortedMap<LocalDate, String> map = getChangedTags();

        if (MapUtils.isEmpty(map)) {
            return getTag();
        } else {
            for (Map.Entry<LocalDate, String> entry : map.entrySet()) {
                if (entry.getKey().isAfter(date)) {
                    return entry.getValue();
                }
            }
        }

        return getTag(); //Should not happen
    }

    public LocalDate getDiedAt() {
        if (!isAlive() && getHistory().hasEvents() && getCapital() != null && CollectionUtils.isNotEmpty(getCapital().getHistory().getEvents())) {
            return getCapital().getHistory()
                               .getOwners()
                               .entrySet()
                               .stream()
                               .filter(entry -> entry.getValue().getTagAt(entry.getKey()).equals(getTagAt(entry.getKey())))
                               .max(Map.Entry.comparingByKey())
                               .map(Map.Entry::getKey)
                               .map(date -> {
                                   for (LocalDate localDate : getCapital().getHistory().getOwners().keySet()) {
                                       if (localDate.isAfter(date)) {
                                           return localDate;
                                       }
                                   }

                                   return null;
                               }).orElse(null);
        }

        return null;
    }

    public List<CustomNationalIdea> getCustomNationalIdeas() {
        ClausewitzItem customNationalIdeasItem = this.item.getChild("custom_national_ideas");
        return customNationalIdeasItem != null ? customNationalIdeasItem.getChildren().stream().map(CustomNationalIdea::new).toList() : null;
    }

    public Integer getCustomNationalIdeasLevel() {
        return this.item.getVarAsInt("custom_national_ideas_level");
    }

    public Integer getNativePolicy() {
        return this.item.getVarAsInt("native_policy");
    }

    public void setNativePolicy(int nativePolicy) {
        this.item.setVariable("native_policy", nativePolicy);
    }

    public LocalDate getAntiNationRuiningEndDate() {
        return this.item.getVarAsDate("anti_nation_ruining_end_date");
    }

    public void setAntiNationRuiningEndDate(LocalDate antiNationRuiningEndDate) {
        this.item.setVariable("anti_nation_ruining_end_date", antiNationRuiningEndDate);
    }

    public Double getSpyPropensity() {
        return this.item.getVarAsDouble("spy_propensity");
    }

    public PowerSpentIndexed getAdmPowerSpent() {
        ClausewitzItem admPowerSpentItem = this.item.getChild("adm_spent_indexed");
        return admPowerSpentItem != null ? new PowerSpentIndexed(admPowerSpentItem) : null;
    }

    public PowerSpentIndexed getDipPowerSpent() {
        ClausewitzItem dipPowerSpentItem = this.item.getChild("dip_spent_indexed");
        return dipPowerSpentItem != null ? new PowerSpentIndexed(dipPowerSpentItem) : null;
    }

    public PowerSpentIndexed getMilPowerSpent() {
        ClausewitzItem milPowerSpentItem = this.item.getChild("mil_spent_indexed");
        return milPowerSpentItem != null ? new PowerSpentIndexed(milPowerSpentItem) : null;
    }

    public List<Integer> getMothballedForts() {
        ClausewitzList list = this.item.getList("mothballed_forts");

        if (list != null) {
            return list.getValuesAsInt();
        }

        return new ArrayList<>();
    }

    public Map<Losses, Integer> getLosses() {
        Map<Losses, Integer> lossesMap = new EnumMap<>(Losses.class);
        ClausewitzItem lossesItem = this.item.getChild("losses");

        if (lossesItem != null) {
            ClausewitzList list = lossesItem.getList("members");

            if (list == null) {
                return lossesMap;
            }

            for (Losses losses : Losses.values()) {
                lossesMap.put(losses, list.getAsInt(losses.ordinal()));
            }
        }

        return lossesMap;
    }

    public Double getInnovativeness() {
        return this.item.getVarAsDouble("innovativeness");
    }

    public void setInnovativeness(Double innovativeness) {
        this.item.setVariable("innovativeness", innovativeness);
    }

    public List<Mission> getCompletedMissions() {
        ClausewitzList list = this.item.getList("completed_missions");
        List<Mission> reforms = new ArrayList<>();

        if (list != null) {
            return list.getValues().stream().map(ClausewitzUtils::removeQuotes).map(s -> this.save.getGame().getMission(s)).filter(Objects::nonNull).toList();
        }

        return reforms;
    }

    public void addCompletedMission(Mission mission) {
        ClausewitzList list = this.item.getList("completed_missions");

        if (list != null) {
            if (!getCompletedMissions().contains(mission)) {
                list.add(ClausewitzUtils.addQuotes(mission.getName()));
            }
        } else {
            this.item.addList("completed_missions", mission.getName());
        }
    }

    public void removeCompletedMission(int index) {
        ClausewitzList list = this.item.getList("completed_missions");

        if (list != null) {
            list.remove(index);
        }
    }

    public void removeCompletedMission(Mission mission) {
        ClausewitzList list = this.item.getList("completed_missions");

        if (list != null) {
            list.remove(ClausewitzUtils.addQuotes(mission.getName()));
        }
    }

    public HistoryStatsCache getHistoryStatsCache() {
        ClausewitzItem historicStatsCacheItem = this.item.getChild("historic_stats_cache");
        return historicStatsCacheItem != null ? new HistoryStatsCache(historicStatsCacheItem) : null;
    }

    public NavalDoctrine getNavalDoctrine() {
        return this.save.getGame().getNavalDoctrine(ClausewitzUtils.removeQuotes(this.item.getVarAsString("naval_doctrine")));
    }

    public void setNavalDoctrine(NavalDoctrine navalDoctrine) {
        this.item.setVariable("naval_doctrine", ClausewitzUtils.addQuotes(navalDoctrine.getName()));
    }

    public Missions getCountryMissions() {
        ClausewitzItem countryMissionsItem = this.item.getChild("country_missions");

        return countryMissionsItem != null ? new Missions(countryMissionsItem, this.save.getGame()) : null;
    }

    public Double getGovernmentReformProgress() {
        return this.item.getVarAsDouble("government_reform_progress");
    }

    public void setGovernmentReformProgress(Double governmentReformProgress) {
        this.item.setVariable("government_reform_progress", governmentReformProgress);
    }

    public Map<SaveArea, CountryState> getStates() {
        return this.save.getAreasStream()
                        .map(area -> Pair.of(area, area.getCountriesStates().get(this)))
                        .filter(p -> p.getValue() != null)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public SortedMap<Integer, Integer> getIncomeStatistics() {
        return this.save.getIncomeStatistics(this);
    }

    public SortedMap<Integer, Integer> getNationSizeStatistics() {
        return this.save.getNationSizeStatistics(this);
    }

    public SortedMap<Integer, Integer> getScoreStatistics() {
        return this.save.getScoreStatistics(this);
    }

    public SortedMap<Integer, Integer> getInflationStatistics() {
        return this.save.getInflationStatistics(this);
    }

    public List<SaveTradeCompany> getTradeCompanies() {
        return this.save.getTradeCompagnies(this);
    }

    public TradeLeague getTradeLeague() {
        return this.save.getTradeLeague(this);
    }

    public SortedSet<ActiveWar> getActiveWars() {
        return this.save.getActiveWars().stream().filter(war -> war.participate(this)).collect(Collectors.toCollection(TreeSet::new));
    }

    public SortedSet<ActiveWar> getFinishedWars() {
        return this.save.getPreviousWars().stream().filter(war -> war.participate(this)).collect(Collectors.toCollection(TreeSet::new));
    }

    public boolean isFreeCity() {
        return !this.save.getHre().dismantled() && isAlive() && getGovernment() != null && CollectionUtils.isNotEmpty(getGovernment().getReforms())
               && getGovernment().getReforms()
                                 .stream()
                                 .anyMatch(reform -> reform.isFreeCity().getKey()
                                                     && (reform.isFreeCity().getValue() == null || reform.isFreeCity().getValue().apply(this, this)));
    }

    public boolean isElector() {
        return !this.save.getHre().dismantled() && isAlive() && this.save.getHre().getElectors().contains(this);
    }

    public boolean isEmperor() {
        return !this.save.getHre().dismantled() && isAlive() && this.equals(this.save.getHre().getEmperor());
    }

    public double getGoverningCapacity() {
        return (NumbersUtils.doubleOrDefault(getModifier(ModifiersUtils.getModifier("governing_capacity"))) +
                NumbersUtils.doubleOrDefault(getModifier(ModifiersUtils.getModifier("tech_governing_capacity"))))
               * (1 + NumbersUtils.doubleOrDefault(getModifier(ModifiersUtils.getModifier("governing_capacity_modifier"), false)));
    }

    public double getGoverningCapacityUsedPercent() {
        return getUsedGoverningCapacity() / getGoverningCapacity();
    }

    public double getYearlyCorruption() {
        return NumbersUtils.doubleOrDefault(getModifier(ModifiersUtils.getModifier("YEARLY_CORRUPTION")));
    }

    public double getLandMorale() {
        return NumbersUtils.doubleOrDefault(
                getModifier(ModifiersUtils.getModifier("tech_land_morale")) * (1 + getModifier(ModifiersUtils.getModifier("land_morale"))));
    }

    public double getDiscipline() {
        return NumbersUtils.doubleOrDefault(getModifier(ModifiersUtils.getModifier("discipline")));
    }

    public double getNavalMorale() {
        return NumbersUtils.doubleOrDefault(
                getModifier(ModifiersUtils.getModifier("tech_naval_morale")) * (1 + getModifier(ModifiersUtils.getModifier("naval_morale"))));
    }

    public double getLandForceLimit() {
        return (NumbersUtils.doubleOrDefault(getModifier(ModifiersUtils.getModifier("land_forcelimit"), false))
                + getOwnedProvinces().stream().mapToDouble(SaveProvince::getLandForceLimit).sum())
               * (1 + NumbersUtils.doubleOrDefault(getModifier(ModifiersUtils.getModifier("land_forcelimit_modifier"), false)));
    }

    public double getNavalForceLimit() {
        if (getOwnedProvinces().stream().noneMatch(SaveProvince::isPort)) {
            return 0;
        }

        return (NumbersUtils.doubleOrDefault(getModifier(ModifiersUtils.getModifier("naval_forcelimit"), false))
                + NumbersUtils.doubleOrDefault(getOwnedProvinces().stream().mapToDouble(SaveProvince::getNavalForceLimit).sum()))
               * (1 + NumbersUtils.doubleOrDefault(getModifier(ModifiersUtils.getModifier("naval_forcelimit_modifier"), false)));
    }

    public double getProductionEfficiency() {
        return NumbersUtils.doubleOrDefault(getModifier(ModifiersUtils.getModifier("production_efficiency"))) +
               NumbersUtils.doubleOrDefault(getModifier(ModifiersUtils.getModifier("tech_production_efficiency")));
    }

    public double getTradeEfficiency() {
        return NumbersUtils.doubleOrDefault(getModifier(ModifiersUtils.getModifier("trade_efficiency"))) +
               NumbersUtils.doubleOrDefault(getModifier(ModifiersUtils.getModifier("tech_trade_efficiency")));
    }

    public double getTolerance(Religion religion) {
        if (religion == null || getReligion() == null) {
            return 0;
        } else if (religion.getName().equals(getReligionName())) {
            return getToleranceOwn();
        } else if (getReligion().getReligionGroup().equals(religion.getReligionGroup())) {
            return getToleranceHeretic();
        } else {
            return getToleranceHeathen();
        }
    }

    public double getAllowedIdeaGroups() {
        return NumbersUtils.doubleOrDefault(getModifier(ModifiersUtils.getModifier("tech_allowed_idea_groups")));
    }

    public double getToleranceOwn() {
        return NumbersUtils.doubleOrDefault(getModifier(ModifiersUtils.getModifier("tolerance_own")));
    }

    public double getDiplomaticReputation() {
        return NumbersUtils.doubleOrDefault(getModifier(ModifiersUtils.getModifier("DIPLOMATIC_REPUTATION")));
    }

    public double getToleranceHeretic() {
        return NumbersUtils.doubleOrDefault(getModifier(ModifiersUtils.getModifier("tolerance_heretic")));
    }

    public double getToleranceHeathen() {
        return NumbersUtils.doubleOrDefault(getModifier(ModifiersUtils.getModifier("tolerance_heathen")));
    }

    public double getTaxModifier() {
        return NumbersUtils.doubleOrDefault(getModifier(ModifiersUtils.getModifier("global_tax_modifier")));
    }

    public double getTariffs() {
        return NumbersUtils.doubleOrDefault(getModifier(ModifiersUtils.getModifier("global_tariffs")));
    }

    public double getVassalIncome() {
        return NumbersUtils.doubleOrDefault(getModifier(ModifiersUtils.getModifier("vassal_income")));
    }

    public double getNbDiplomaticRelations() {
        return NumbersUtils.doubleOrDefault(getModifier(ModifiersUtils.getModifier("DIPLOMATIC_UPKEEP")));
    }

    public double getNbFreeDiplomaticRelations() {
        Set<SaveCountry> countedCountries = new HashSet<>();
        Set<SaveCountry> tmp;

        double nb = NumbersUtils.doubleOrDefault(getModifier(ModifiersUtils.getModifier("DIPLOMATIC_UPKEEP")));
        nb -= getAllies().size();
        countedCountries.addAll(getAllies());

        tmp = getSubjects().stream().filter(country -> country.getSubjectType().isTakesDiploSlot()).collect(Collectors.toSet());
        nb -= tmp.size();
        countedCountries.addAll(tmp);

        tmp = this.save.getDiplomacy()
                       .getGuarantees()
                       .stream()
                       .filter(rel -> rel.getFirst().equals(this))
                       .map(DatableRelation::getSecond)
                       .filter(Predicate.not(countedCountries::contains))
                       .collect(Collectors.toSet());

        nb -= tmp.size();
        countedCountries.addAll(tmp);

        tmp = this.save.getDiplomacy()
                       .getRoyalMarriage()
                       .stream()
                       .filter(rel -> rel.getFirst().equals(this))
                       .map(DatableRelation::getSecond)
                       .filter(Predicate.not(countedCountries::contains))
                       .collect(Collectors.toSet());

        nb -= tmp.size();
        countedCountries.addAll(tmp);

        tmp = this.save.getDiplomacy()
                       .getMilitaryAccesses()
                       .stream()
                       .filter(rel -> rel.getFirst().equals(this))
                       .map(DatableRelation::getSecond)
                       .filter(Predicate.not(countedCountries::contains))
                       .collect(Collectors.toSet());

        nb -= tmp.size();
        countedCountries.addAll(tmp);

        tmp = this.save.getDiplomacy()
                       .getSupportIndependence()
                       .stream()
                       .filter(rel -> rel.getFirst().equals(this))
                       .map(DatableRelation::getSecond)
                       .filter(Predicate.not(countedCountries::contains))
                       .collect(Collectors.toSet());

        nb -= tmp.size();
        countedCountries.addAll(tmp);

        return nb;
    }

    public Double getModifier(Modifier modifier) {
        return getModifier(modifier, true);
    }

    public Double getModifier(Modifier modifier, boolean includeProvinces) {
        List<Double> list = new ArrayList<>();
        list.add(StaticModifiers.applyToModifiersCountry(this, modifier));

        List<SaveModifier> modifiers = getModifiers();

        if (CollectionUtils.isNotEmpty(modifiers)) {
            list.addAll(modifiers.stream()
                                 .filter(m -> m.getModifier() != null)
                                 .filter(m -> !StaticModifier.class.equals(m.getModifier().getClass()))
                                 .map(m -> m.getModifiers(this, modifier))
                                 .toList());
        }

        IdeaGroups ideaGroups = getIdeaGroups();
        if (ideaGroups != null) {
            ideaGroups.getIdeaGroups().forEach((key, value) -> list.add(key.getModifier(value, modifier)));
        }

        //Todo neighbours

        CountryTechnology tech = getTech();
        List<SaveCountry> ourSpyNetwork = getOurSpyNetwork();
        if ("ADM_TECH_COST_MODIFIER".equalsIgnoreCase(modifier.getName())) {
            if (ideaGroups != null) {
                list.add(ideaGroups.getIdeaGroups()
                                   .entrySet()
                                   .stream()
                                   .filter(entry -> Power.ADM.equals(entry.getKey().getCategory()))
                                   .mapToInt(Map.Entry::getValue)
                                   .sum()
                         * this.save.getGame().getIdeaToTech());
            }
            if (CollectionUtils.isNotEmpty(ourSpyNetwork)) {
                ourSpyNetwork.stream().max(Comparator.comparing(o -> o.getTech().getAdm())).ifPresent(country -> {
                    if (country.getTech().getAdm() > tech.getAdm()) {
                        double mult = Math.max(this.save.getGame().getSpyNetworkTechEffectMax(),
                                               (country.getTech().getAdm() - tech.getAdm()) * this.save.getGame().getSpyNetworkTechEffect());
                        list.add(mult * NumbersUtils.intOrDefault(getActiveRelation(country).getSpyNetwork()));
                    }
                });
            }
        } else if ("DIP_TECH_COST_MODIFIER".equalsIgnoreCase(modifier.getName())) {
            if (ideaGroups != null) {
                list.add(ideaGroups.getIdeaGroups()
                                   .entrySet()
                                   .stream()
                                   .filter(entry -> Power.DIP.equals(entry.getKey().getCategory()))
                                   .mapToInt(Map.Entry::getValue)
                                   .sum()
                         * this.save.getGame().getIdeaToTech());
            }

            if (CollectionUtils.isNotEmpty(ourSpyNetwork)) {
                ourSpyNetwork.stream().max(Comparator.comparing(o -> o.getTech().getDip())).ifPresent(country -> {
                    if (country.getTech().getDip() > tech.getDip()) {
                        double mult = Math.max(this.save.getGame().getSpyNetworkTechEffectMax(),
                                               (country.getTech().getDip() - tech.getDip()) * this.save.getGame().getSpyNetworkTechEffect());
                        list.add(mult * NumbersUtils.intOrDefault(getActiveRelation(country).getSpyNetwork()));
                    }
                });
            }
        } else if ("MIL_TECH_COST_MODIFIER".equalsIgnoreCase(modifier.getName())) {
            if (ideaGroups != null) {
                list.add(ideaGroups.getIdeaGroups()
                                   .entrySet()
                                   .stream()
                                   .filter(entry -> Power.MIL.equals(entry.getKey().getCategory()))
                                   .mapToInt(Map.Entry::getValue)
                                   .sum()
                         * this.save.getGame().getIdeaToTech());
            }

            if (CollectionUtils.isNotEmpty(ourSpyNetwork)) {
                ourSpyNetwork.stream().max(Comparator.comparing(o -> o.getTech().getMil())).ifPresent(country -> {
                    if (country.getTech().getMil() > tech.getMil()) {
                        double mult = Math.max(this.save.getGame().getSpyNetworkTechEffectMax(),
                                               (country.getTech().getMil() - tech.getMil()) * this.save.getGame().getSpyNetworkTechEffect());
                        list.add(mult * NumbersUtils.intOrDefault(getActiveRelation(country).getSpyNetwork()));
                    }
                });
            }
        } else if ("yearly_army_professionalism".equalsIgnoreCase(modifier.getName())) {
            list.add((getArmies().values().stream().filter(Army::isDrilling).mapToInt(army -> army.getRegiments().size()).sum() / getLandForceLimit()) * 0.01);
        } else if ("navy_tradition".equalsIgnoreCase(modifier.getName())) {
            list.add((2 * getNavies().values()
                                     .stream()
                                     .filter(navy -> navy.isPrivateering() || navy.isProtecting())
                                     .mapToLong(navy -> navy.getShips().stream().filter(ship -> UnitType.LIGHT_SHIP.equals(ship.getUnitType())).count())
                                     .sum() / getNavalForceLimit())
                     * (1 + getModifier(ModifiersUtils.getModifier("naval_tradition_from_trade"))));
        }

        Church church = getChurch();
        if (church != null && CollectionUtils.isNotEmpty(church.getAspects())) {
            list.addAll(church.getAspects()
                              .stream()
                              .filter(churchAspect -> churchAspect.getModifiers().hasModifier(modifier))
                              .map(churchAspect -> churchAspect.getModifiers().getModifier(modifier))
                              .toList());
        }

        list.add(tech.getModifier(Power.ADM, tech.getAdm(), modifier));
        list.add(tech.getModifier(Power.DIP, tech.getDip(), modifier));
        list.add(tech.getModifier(Power.MIL, tech.getMil(), modifier));

        Monarch mon;
        if ((mon = getMonarch()) != null && mon.getPersonalities() != null
            && CollectionUtils.isNotEmpty(mon.getPersonalities().getPersonalities())) {
            try {
                list.addAll(mon.getPersonalities()
                               .getPersonalities()
                               .stream()
                               .map(RulerPersonality::getModifiers)
                               .filter(Objects::nonNull)
                               .filter(m -> m.hasModifier(modifier))
                               .map(m -> m.getModifier(modifier))
                               .toList());
            } catch (Exception e) {
                LoggerFactory.getLogger(SaveCountry.class).error(e.getMessage(), e);
            }
        }

        list.addAll(this.save.getGame()
                             .getProfessionalismModifiers()
                             .stream()
                             .filter(m -> m.getArmyProfessionalism() <= NumbersUtils.doubleOrDefault(getArmyProfessionalism()))
                             .map(ProfessionalismModifier::getModifiers)
                             .filter(Objects::nonNull)
                             .filter(m -> m.hasModifier(modifier))
                             .map(m -> m.getModifier(modifier))
                             .toList());

        list.addAll(getTradedBonus().stream()
                                    .map(TradeGood::getModifiers)
                                    .filter(Objects::nonNull)
                                    .filter(m -> m.hasModifier(modifier))
                                    .map(m -> m.getModifier(modifier))
                                    .toList());

        list.addAll(getActivePolicies().stream()
                                       .map(ActivePolicy::getPolicy)
                                       .map(Policy::getModifiers)
                                       .filter(Objects::nonNull)
                                       .filter(m -> m.hasModifier(modifier))
                                       .map(m -> m.getModifier(modifier))
                                       .toList());

        list.addAll(getActiveAgeAbility().stream()
                                         .map(AgeAbility::getModifiers)
                                         .filter(Objects::nonNull)
                                         .filter(m -> m.hasModifier(modifier))
                                         .map(m -> m.getModifier(modifier))
                                         .toList());

        list.addAll(getEmbracedInstitutions().stream()
                                             .map(Institution::getBonuses)
                                             .filter(Objects::nonNull)
                                             .filter(m -> m.hasModifier(modifier))
                                             .map(m -> m.getModifier(modifier))
                                             .toList());

        SaveProvince capital = getCapital();
        if (capital != null && capital.inHre() && !this.save.getHre().dismantled()) {
            list.addAll(this.save.getHre()
                                 .getPassedReforms()
                                 .stream()
                                 .map(ImperialReform::getAllModifiers)
                                 .filter(Objects::nonNull)
                                 .filter(m -> m.hasModifier(modifier))
                                 .map(m -> m.getModifier(modifier))
                                 .filter(Objects::nonNull)
                                 .toList());

            if (this.equals(this.save.getHre().getEmperor())) {
                list.addAll(this.save.getHre()
                                     .getPassedReforms()
                                     .stream()
                                     .map(ImperialReform::getEmperorModifiers)
                                     .filter(Objects::nonNull)
                                     .filter(m -> m.hasModifier(modifier))
                                     .map(m -> m.getModifier(modifier))
                                     .filter(Objects::nonNull)
                                     .toList());
                list.addAll(this.save.getHre()
                                     .getPassedReforms()
                                     .stream()
                                     .map(ImperialReform::getEmperorPerPrinceModifiers)
                                     .filter(Objects::nonNull)
                                     .filter(m -> m.hasModifier(modifier))
                                     .map(m -> m.getModifiers(modifier))
                                     .filter(Objects::nonNull)
                                     .map(m -> ModifiersUtils.scaleWithPrinces(this.save, m))
                                     .map(m -> m.getModifier(modifier))
                                     .toList());
            } else {
                list.addAll(this.save.getHre()
                                     .getPassedReforms()
                                     .stream()
                                     .map(ImperialReform::getMemberModifiers)
                                     .filter(Objects::nonNull)
                                     .filter(m -> m.hasModifier(modifier))
                                     .map(m -> m.getModifier(modifier))
                                     .toList());
            }

            if (this.save.getHre().getElectors().contains(this)) {
                list.addAll(this.save.getHre()
                                     .getPassedReforms()
                                     .stream()
                                     .map(ImperialReform::getElectorPerPrinceModifiers)
                                     .filter(Objects::nonNull)
                                     .filter(m -> m.hasModifier(modifier))
                                     .map(m -> m.getModifiers(modifier))
                                     .filter(Objects::nonNull)
                                     .map(modif -> ModifiersUtils.scaleWithPrinces(this.save, modif))
                                     .map(m -> m.getModifier(modifier))
                                     .toList());
            }
        }

        if (!this.save.getCelestialEmpire().dismantled() && this.equals(this.save.getCelestialEmpire().getEmperor())) {
            list.addAll(this.save.getCelestialEmpire()
                                 .getPassedReforms()
                                 .stream()
                                 .map(ImperialReform::getEmperorModifiers)
                                 .filter(Objects::nonNull)
                                 .filter(m -> m.hasModifier(modifier))
                                 .map(m -> m.getModifier(modifier))
                                 .toList());
        }

        SaveFervor fervor = getFervor();
        if (fervor != null) {
            list.addAll(fervor.getActives()
                              .stream()
                              .map(Fervor::getModifiers)
                              .filter(Objects::nonNull)
                              .filter(m -> m.hasModifier(modifier))
                              .map(m -> m.getModifier(modifier))
                              .toList());

            if (modifier.getName().equalsIgnoreCase("MONTHLY_FERVOR_INCREASE")) {
                list.add(ModifiersUtils.scaleWithActivesFervor(this, new Modifiers(new HashSet<>(),
                                                                                   Map.of(ModifiersUtils.getModifier("MONTHLY_FERVOR_INCREASE"),
                                                                                          -5d)))
                                       .getModifier(modifier));
            }
        }

        SaveNativeAdvancements nativeAdvancements = getNativeAdvancements();
        if (nativeAdvancements != null) {
            list.addAll(nativeAdvancements.getNativeAdvancements()
                                          .values()
                                          .stream()
                                          .map(a -> a.getEmbracedNativeAdvancements(this.save.getGame()))
                                          .flatMap(Collection::stream)
                                          .filter(Objects::nonNull)
                                          .map(NativeAdvancement::getModifiers)
                                          .filter(Objects::nonNull)
                                          .filter(m -> m.hasModifier(modifier))
                                          .map(m -> m.getModifier(modifier))
                                          .toList());
        }

        NavalDoctrine navalDoctrine = getNavalDoctrine();
        if (navalDoctrine != null && navalDoctrine.getModifiers().hasModifier(modifier)) {
            list.add(navalDoctrine.getModifiers().getModifier(modifier));
        }

        PersonalDeity personalDeity = getPersonalDeity();
        if (personalDeity != null && personalDeity.getModifiers().hasModifier(modifier)) {
            list.add(personalDeity.getModifiers().getModifier(modifier));
        }

        SaveReligiousReforms religiousReforms = getReligiousReforms();
        if (religiousReforms != null) {
            list.addAll(religiousReforms.getAdoptedReforms()
                                        .stream()
                                        .map(ReligiousReform::getModifiers)
                                        .filter(Objects::nonNull)
                                        .filter(m -> m.hasModifier(modifier))
                                        .map(m -> m.getModifier(modifier))
                                        .toList());
        }

        list.addAll(this.save.getTradeNodesStream()
                             .map(tradeNode -> tradeNode.getCountry(this))
                             .filter(Objects::nonNull)
                             .filter(TradeNodeCountry::hasTrader)
                             .map(TradeNodeCountry::getTradePolicy)
                             .filter(Objects::nonNull)
                             .map(TradePolicy::getCountriesWithMerchantModifier)
                             .filter(Objects::nonNull)
                             .filter(m -> m.hasModifier(modifier))
                             .map(m -> m.getModifier(modifier))
                             .toList());

        Map<Integer, SaveAdvisor> advisors = getAdvisors();
        if (MapUtils.isNotEmpty(advisors)) {
            list.addAll(advisors.values()
                                .stream()
                                .map(SaveAdvisor::getModifiers)
                                .filter(Objects::nonNull)
                                .filter(m -> m.hasModifier(modifier))
                                .map(m -> m.getModifier(modifier))
                                .toList());
        }

        list.addAll(getOwnedProvinces().stream()
                                       .map(SaveProvince::getSaveArea)
                                       .filter(Objects::nonNull)
                                       .distinct()
                                       .map(saveArea -> saveArea.getInvestment(this))
                                       .filter(Objects::nonNull)
                                       .map(SaveInvestment::getInvestments)
                                       .flatMap(Collection::stream)
                                       .map(Investment::getOwnerModifier)
                                       .filter(Objects::nonNull)
                                       .filter(m -> m.hasModifier(modifier))
                                       .map(m -> m.getModifier(modifier))
                                       .toList());

        list.addAll(getOwnedProvinces().stream()
                                       .map(SaveProvince::getCenterOfTrade)
                                       .filter(Objects::nonNull)
                                       .map(CenterOfTrade::getGlobalModifiers)
                                       .filter(Objects::nonNull)
                                       .filter(m -> m.hasModifier(modifier))
                                       .map(m -> m.getModifier(modifier))
                                       .toList());

        if (includeProvinces) {
            list.addAll(getOwnedProvinces().stream()
                                           .map(province -> province.getModifier(modifier))
                                           .toList());
        }

        list.addAll(getOwnedProvinces().stream()
                                       .map(SaveProvince::getGreatProjects)
                                       .filter(CollectionUtils::isNotEmpty)
                                       .flatMap(Collection::stream)
                                       .map(SaveGreatProject::getTier)
                                       .filter(Objects::nonNull)
                                       .map(GreatProjectTier::getCountryModifiers)
                                       .filter(Objects::nonNull)
                                       .filter(m -> m.hasModifier(modifier))
                                       .map(m -> m.getModifier(modifier))
                                       .toList());

        List<SaveFaction> factions = getFactions();
        if (factions != null) {
            factions.stream()
                    .max(Comparator.comparing(SaveFaction::getInfluence))
                    .ifPresent(faction -> {
                        if (faction.getType().getModifiers().hasModifier(modifier)) {
                            list.add(faction.getType().getModifiers().getModifier(modifier));
                        }
                    });
        }

        SaveGovernment government = getGovernment();
        if (government != null) {
            list.addAll(government.getReforms()
                                  .stream()
                                  .map(GovernmentReform::getModifiers)
                                  .filter(Objects::nonNull)
                                  .filter(m -> m.hasModifier(modifier))
                                  .map(m -> m.getModifier(modifier))
                                  .toList());
        }

        if (isStatistsInPower()) {
            list.addAll(government.getReforms()
                                  .stream()
                                  .filter(reform -> reform.getStatesGeneralMechanic() != null
                                                    && (reform.getStatesGeneralMechanic().getValue() == null
                                                        || reform.getStatesGeneralMechanic().getValue().apply(this, this)))
                                  .map(GovernmentReform::getStatesGeneralMechanic)
                                  .map(Pair::getKey)
                                  .filter(Objects::nonNull)
                                  .map(List::getFirst)
                                  .filter(m -> m.hasModifier(modifier))
                                  .map(m -> m.getModifier(modifier))
                                  .toList());
        } else if (isMonarchistsInPower()) {
            list.addAll(government.getReforms()
                                  .stream()
                                  .filter(reform -> reform.getStatesGeneralMechanic() != null
                                                    && (reform.getStatesGeneralMechanic().getValue() == null
                                                        || reform.getStatesGeneralMechanic().getValue().apply(this, this)))
                                  .map(GovernmentReform::getStatesGeneralMechanic)
                                  .map(Pair::getKey)
                                  .filter(Objects::nonNull)
                                  .map(m -> m.get(1))
                                  .filter(m -> m.hasModifier(modifier))
                                  .map(m -> m.getModifier(modifier))
                                  .toList());
        }

        GovernmentRank governmentRank = getGovernmentRank();
        if (governmentRank != null && governmentRank.getModifiers().hasModifier(modifier)) {
            list.add(governmentRank.getModifiers().getModifier(modifier));
        }

        SaveHegemon hegemon = getHegemon();
        if (hegemon != null && hegemon.getModifiers().hasModifier(modifier)) {
            list.add(hegemon.getModifiers().getModifier(modifier));
        }

        SaveReligion religion = getReligion();
        if (religion != null && religion.getGameReligion().getCountry() != null && religion.getGameReligion().getCountry().hasModifier(modifier)) {
            list.add(religion.getGameReligion().getCountry().getModifier(modifier));
        }

        SaveReligion secondaryReligion = getSecondaryReligion();
        if (secondaryReligion != null && secondaryReligion.getGameReligion().getCountryAsSecondary() != null
            && secondaryReligion.getGameReligion().getCountryAsSecondary().hasModifier(modifier)) {
            list.add(secondaryReligion.getGameReligion().getCountryAsSecondary().getModifier(modifier));
        }

        SavePapacy papacy;
        if (religion != null && (papacy = religion.getPapacy()) != null && BooleanUtils.toBoolean(papacy.getPapacyActive())) {
            if (papacy.getGoldenBull() != null && papacy.getGoldenBull().getModifiers().hasModifier(modifier)) {
                list.add(papacy.getGoldenBull().getModifiers().getModifier(modifier));
            }

            if (BooleanUtils.toBoolean(papacy.getCouncilActive()) && !BooleanUtils.toBoolean(papacy.getCouncilFinished())) {
                if (papacy.getHarsh().contains(this)) {
                    if (papacy.getGamePapacy().getHarshModifiers().hasModifier(modifier)) {
                        list.add(papacy.getGamePapacy().getHarshModifiers().getModifier(modifier));
                    }
                } else if (papacy.getNeutral().contains(this)) {
                    if (papacy.getGamePapacy().getNeutralModifiers().hasModifier(modifier)) {
                        list.add(papacy.getGamePapacy().getNeutralModifiers().getModifier(modifier));
                    }
                } else if (papacy.getConcilatory().contains(this)) {
                    if (papacy.getGamePapacy().getConcilatoryModifiers().hasModifier(modifier)) {
                        list.add(papacy.getGamePapacy().getConcilatoryModifiers().getModifier(modifier));
                    }
                }
            }

            if (MapUtils.isNotEmpty(papacy.getConcessions()) && papacy.getConcessionsModifiers(modifier) != null) {
                list.add(papacy.getConcessionsModifiers(modifier));
            }
        }

        List<SaveCountry> subjects = getSubjects();
        if (CollectionUtils.isNotEmpty(subjects)) {
            subjects.stream()
                    .map(subject -> this.save.getDiplomacy()
                                             .getDependencies()
                                             .stream()
                                             .filter(dependency -> this.equals(dependency.getFirst()) && subject.equals(dependency.getSecond()))
                                             .findFirst())
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(Dependency::getSubjectTypeUpgrades)
                    .filter(Objects::nonNull)
                    .flatMap(Collection::stream)
                    .forEach(upgrade -> list.add(upgrade.getModifiersOverlord().getModifier(modifier)));

            if (modifier.getName().equalsIgnoreCase("LAND_FORCELIMIT")) {
                list.add(subjects.stream()
                                 .filter(subject -> subject.getSubjectType().getForcelimitToOverlord() != 0)
                                 .filter(subject -> !subject.isColony() || subject.getOwnedProvinces().stream().filter(SaveProvince::isCity).count() >= 10)
                                 .mapToDouble(subject -> ((int) subject.getLandForceLimit()) * subject.getSubjectType().getForcelimitToOverlord())
                                 .sum());
            }
        }

        List<SaveEstate> estates = getEstates();
        if (CollectionUtils.isNotEmpty(estates)) {
            list.addAll(estates.stream().map(estate -> estate.getModifiers(modifier)).toList());
        }

        SaveCountry overlord = getOverlord();
        if (overlord != null) {
            this.save.getDiplomacy()
                     .getDependencies()
                     .stream()
                     .filter(dependency -> this.equals(dependency.getSecond()) && overlord.equals(dependency.getSecond()))
                     .map(Dependency::getSubjectTypeUpgrades)
                     .filter(Objects::nonNull)
                     .flatMap(Collection::stream)
                     .forEach(upgrade -> list.add(upgrade.getModifiersSubject().getModifier(modifier)));
        }

        CrownLandBonus crownLandBonus = getCrownLandBonus();
        if (crownLandBonus != null && crownLandBonus.getModifiers().hasModifier(modifier)) {
            list.add(crownLandBonus.getModifiers().getModifier(modifier));
        }

        return ModifiersUtils.sumModifiers(modifier, list);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof SaveCountry country)) {
            return false;
        }

        return Objects.equals(getTag(), country.getTag());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTag());
    }

    @Override
    public String toString() {
        return this.localizedName == null ? getTag() : this.localizedName + (getPlayers() == null ? "" : " (" + String.join("; ", getPlayers()) + ")");
    }
}
