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
import fr.osallek.eu4parser.model.game.Faction;
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

    private boolean isPlayable;

    private GovernmentName governmentName;

    private String localizedName;

    private List<String> players;

    private SaveHegemon hegemon;

    private Integer greatPowerRank;

    private SaveFervor fervor;

    private PlayerAiPrefsCommand playerAiPrefsCommand;

    private ListOfDates cooldowns;

    private SaveCountryHistory history;

    private ListOfDates flags;

    private ListOfDates hiddenFlags;

    private ListOfDoubles variables;

    private Colors colors;

    private CountryTechnology tech;

    private List<SaveEstate> estates;

    private ActiveAgenda activeAgenda;

    private List<EstateInteraction> interactionsLastUsed;

    private List<SaveFaction> factions;

    private Map<String, Rival> rivals;

    private List<VictoryCard> victoryCards;

    private List<ActivePolicy> activePolicies;

    private List<PowerProjection> powerProjections;

    private Parliament parliament;

    private Ledger ledger;

    private List<Loan> loans;

    private Church church;

    private IdeaGroups ideaGroups;

    private SaveReligiousReforms religiousReforms;

    private SaveNativeAdvancements nativeAdvancements;

    private SaveGovernment government;

    private List<Envoy> colonists;

    private List<Envoy> merchants;

    private List<Envoy> missionaries;

    private List<Envoy> diplomats;

    private List<SaveModifier> modifiers;

    private SubUnit subUnit;

    private Map<Id, Army> armies;

    private Map<Integer, MercenaryCompany> mercenaryCompanies;

    private Map<Id, Navy> navies;

    private Map<String, ActiveRelation> activeRelations;

    private Map<Integer, Leader> leaders;

    private List<Id> previousMonarchs;

    private List<Id> advisorsIds;

    private Map<Integer, SaveAdvisor> advisors;

    private Map<Integer, SaveAdvisor> activeAdvisors;

    private Monarch monarch;

    private Heir heir;

    private Queen queen;

    private PowerSpentIndexed admPowerSpent;

    private PowerSpentIndexed dipPowerSpent;

    private PowerSpentIndexed milPowerSpent;

    private HistoryStatsCache historyStatsCache;

    private List<CustomNationalIdea> customNationalIdeas;

    private Missions countryMissions;

    private final Map<SaveArea, CountryState> states = new HashMap<>();

    private SortedMap<Integer, Integer> incomeStatistics;

    private SortedMap<Integer, Integer> nationSizeStatistics;

    private SortedMap<Integer, Integer> scoreStatistics;

    private SortedMap<Integer, Integer> inflationStatistics;

    private List<SaveTradeCompany> tradeCompanies;

    private final SortedSet<ActiveWar> wars = new TreeSet<>(Comparator.comparing(ActiveWar::getStartDate));

    private SubjectType subjectType;

    private LocalDate subjectStartDate;

    private TradeLeague tradeLeague;

    private Path writenTo;

    public SaveCountry(ClausewitzItem item, Save save) {
        this.item = item;
        this.save = save;
        this.isPlayable = !"---".equals(getTag()) && !"REB".equals(getTag()) && !"NAT".equals(getTag()) && !"PIR".equals(getTag())
                          && !getTag().matches("O[0-9]{2}");
        refreshAttributes();
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
        this.item.getVarAsString("name").ifPresentOrElse(name -> this.localizedName = ClausewitzUtils.removeQuotes(name), () -> {
            this.item.getVarAsString("custom_name")
                     .ifPresentOrElse(name -> this.localizedName = ClausewitzUtils.removeQuotes(name),
                                      () -> this.localizedName = ClausewitzUtils.removeQuotes(localizedName));
        });
    }

    public Optional<String> getCustomName() {
        return this.item.getVarAsString("custom_name");
    }

    public Optional<String> getName() {
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
                                                       .filter(colonialRegion -> getCapitalId().isPresent())
                                                       .filter(r -> r.getProvinces().contains(getCapitalId().get()))
                                                       .findFirst();

            if (region.isEmpty() || region.get().getColor().isEmpty()) {
                return null;
            }

            BufferedImage image = ImageIO.read(getColonialParent().getFlagFile());
            Graphics2D g = image.createGraphics();
            g.setColor(region.get().getColor().get().toColor());
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
        return isPlayable;
    }

    public boolean isAlive() {
        return getCapital() != null && getDevelopment().filter(aDouble -> aDouble > 0).isPresent() && CollectionUtils.isNotEmpty(getContinents());
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
        return hegemon;
    }

    public void setHegemon(SaveHegemon hegemon) {
        this.hegemon = hegemon;
    }

    public boolean isHuman() {
        return this.item.getVarAsBool("human").map(BooleanUtils::toBoolean).orElse(false);
    }

    public Optional<Boolean> hasSwitchedNation() {
        return this.item.getVarAsBool("has_switched_nation");
    }

    public boolean wasPlayer() {
        return this.item.getVarAsBool("was_player").map(BooleanUtils::toBoolean).orElse(false);
    }

    public void setWasPlayer(boolean wasPlayer) {
        this.item.setVariable("was_player", wasPlayer);
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }

    public void addPlayer(String player) {
        if (this.players == null) {
            this.players = new ArrayList<>();
        }

        this.players.add(player);
    }

    public Integer getGreatPowerRank() {
        return greatPowerRank;
    }

    public void setGreatPowerRank(Integer greatPowerRank) {
        this.greatPowerRank = greatPowerRank;
    }

    public PlayerAiPrefsCommand getPlayerAiPrefsCommand() {
        return playerAiPrefsCommand;
    }

    public void setPlayerAiPrefsCommand(boolean startWars, boolean keepAlliances, boolean keepTreaties, boolean quickPeace, boolean moveTraders,
                                        boolean takeDecisions, boolean embraceInstitutions, boolean developProvinces, boolean disbandUnits,
                                        boolean changeFleetMissions, boolean sendMissionaries, boolean convertCultures, boolean promoteCultures,
                                        boolean braindead, int timeout) {
        PlayerAiPrefsCommand.addToItem(this.item, startWars, keepAlliances, keepTreaties, quickPeace, moveTraders, takeDecisions, embraceInstitutions,
                                       developProvinces, disbandUnits, changeFleetMissions, sendMissionaries, convertCultures, promoteCultures, braindead,
                                       timeout);
        refreshAttributes();
    }

    public Optional<Boolean> hasSetGovernmentName() {
        return this.item.getVarAsBool("has_set_government_name");
    }

    public Optional<Integer> getGovernmentLevel() {
        return this.item.getVarAsInt("government_rank");
    }

    public Optional<GovernmentRank> getGovernmentRank() {
        return getGovernmentLevel().map(integer -> this.save.getGame().getGovernmentRank(integer));
    }

    public void setGovernmentRank(int governmentRank) {
        if (governmentRank <= 0) {
            governmentRank = 1;
        }

        this.item.setVariable("government_rank", governmentRank);
    }

    public void setGovernmentRank(String governmentRank) {
        Integer rank = null;

        if (this.governmentName != null) {
            rank = this.governmentName.getRanks()
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
        return this.governmentName;
    }

    //Fixme compute the value
    public void setGovernmentName(GovernmentName governmentName) {
        this.governmentName = governmentName;
    }

    public Optional<Integer> getSubjectFocus() {
        return this.item.getVarAsInt("subject_focus");
    }

    public Optional<Double> getTradeMission() {
        return this.item.getVarAsDouble("trade_mission");
    }

    public Optional<Double> getBlockadeMission() {
        return this.item.getVarAsDouble("blockade_mission");
    }

    public List<ProvinceList> getContinents() {
        List<ProvinceList> continents = new ArrayList<>();

        this.item.getList("continent").ifPresent(list -> {
            for (int i = 0; i < this.save.getGame().getContinents().size(); i++) {
                if (list.getAsInt(i).filter(integer -> integer == 1).isPresent()) {
                    continents.add(this.save.getGame().getContinent(i));
                }
            }
        });

        return continents;
    }

    public Optional<Power> getNationalFocus() {
        return this.item.getVarAsString("national_focus").map(Power::byName);
    }

    public void setNationalFocus(Power power, LocalDate date) {
        this.item.setVariable("national_focus", power.name());

        if (this.history != null) {
            this.history.addEvent(date, "national_focus", power.name());
        }
    }

    public List<Institution> getEmbracedInstitutions() {
        return this.item.getList("institutions")
                        .map(l -> this.save.getGame().getInstitutions().stream().filter(this::getEmbracedInstitution).toList())
                        .orElse(new ArrayList<>());
    }

    public List<Institution> getNotEmbracedInstitutions() {
        return this.item.getList("institutions")
                        .map(l -> this.save.getGame().getInstitutions().stream().filter(index -> !getEmbracedInstitution(index)).toList())
                        .orElse(new ArrayList<>());
    }

    public boolean getEmbracedInstitution(int institution) {
        return this.item.getList("institutions").map(list -> list.getAsInt(institution).filter(integer -> 1 == integer).isPresent()).orElse(false);
    }

    public boolean getEmbracedInstitution(Institution institution) {
        return getEmbracedInstitution(institution.getIndex());
    }

    public long getNbEmbracedInstitutions() {
        return getEmbracedInstitutions().size();
    }

    public void embracedInstitution(int institution, boolean embraced) {
        this.item.getList("institutions").ifPresent(list -> list.set(institution, embraced ? 1 : 0));
    }

    public Optional<Integer> getNumOfAgeObjectives() {
        return this.item.getVarAsInt("num_of_age_objectives");
    }

    public List<AgeAbility> getActiveAgeAbility() {
        List<ClausewitzVariable> variables = this.item.getVariables("active_age_ability");

        return variables.stream().map(variable -> this.save.getGame().getAgeAbility(ClausewitzUtils.removeQuotes(variable.getValue()))).toList();
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

    public Optional<LocalDate> getLastSoldProvince() {
        return this.item.getVarAsDate("last_sold_province");
    }

    public void setLastSoldProvince(LocalDate lastSoldProvince) {
        this.item.setVariable("last_sold_province", lastSoldProvince);
    }

    public Optional<LocalDate> getGoldenEraDate() {
        return this.item.getVarAsDate("golden_era_date");
    }

    public void setGoldenEraDate(LocalDate goldenEraDate) {
        this.item.setVariable("golden_era_date", goldenEraDate);
    }

    public void removeGoldenEraDate() {
        this.item.removeVariable("golden_era_date");
    }

    public Optional<LocalDate> getLastFocusMove() {
        return this.item.getVarAsDate("last_focus_move");
    }

    public void setLastFocusMove(LocalDate lastFocusMove) {
        this.item.setVariable("last_focus_move", lastFocusMove);
    }

    public Optional<LocalDate> getLastSentAllianceOffer() {
        return this.item.getVarAsDate("last_sent_alliance_offer");
    }

    public void setLastSentAllianceOffer(LocalDate lastSentAllianceOffer) {
        this.item.setVariable("last_sent_alliance_offer", lastSentAllianceOffer);
    }

    public Optional<LocalDate> getLastConversionSecondary() {
        return this.item.getVarAsDate("last_conversion_secondary");
    }

    public void setLastConversionSecondary(LocalDate lastConversionSecondary) {
        this.item.setVariable("last_conversion_secondary", lastConversionSecondary);
    }

    public ListOfDates getCooldowns() {
        return cooldowns;
    }

    public SaveCountryHistory getHistory() {
        return history;
    }

    public ListOfDates getFlags() {
        return flags;
    }

    public ListOfDates getHiddenFlags() {
        return hiddenFlags;
    }

    public ListOfDoubles getVariables() {
        return variables;
    }

    public Colors getColors() {
        return colors;
    }

    public Optional<Color> getColor() {
        return this.save.getRevolution().getRevolutionTarget().filter(country -> country.equals(this)).isPresent() ? this.colors.getRevolutionaryColors() :
               this.colors.getMapColor();
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

    public Optional<Integer> getCapitalId() {
        return this.item.getVarAsInt("capital");
    }

    public Optional<SaveProvince> getCapital() {
        return getCapitalId().map(this.save::getProvince);
    }

    public void setCapital(SaveProvince saveProvince) {
        if (saveProvince != null && saveProvince.getOwner().filter(country -> country.equals(this)).isPresent()) {
            this.item.setVariable("capital", saveProvince.getId());
        }
    }

    public Optional<SaveProvince> getOriginalCapital() {
        return this.item.getVarAsInt("original_capital").map(this.save::getProvince);
    }

    public void setOriginalCapital(Integer provinceId) {
        this.item.setVariable("original_capital", provinceId);
    }

    public Optional<SaveProvince> getTradePort() {
        return this.item.getVarAsInt("trade_port").map(this.save::getProvince);
    }

    public void setTradePort(Integer provinceId) {
        if (this.save.getProvince(provinceId).getOwner().filter(c -> c.equals(this)).isPresent()) {
            this.item.setVariable("trade_port", provinceId);
        }
    }

    public Optional<Double> getCustomNationPoints() {
        return this.item.getVarAsDouble("custom_nation_points");
    }

    public Optional<Double> getBaseTax() {
        return this.item.getVarAsDouble("base_tax");
    }

    public Optional<Double> getDevelopment() {
        return this.item.getVarAsDouble("development");
    }

    public Optional<Double> getRawDevelopment() {
        return this.item.getVarAsDouble("raw_development");
    }

    public Optional<Double> getCappedDevelopment() {
        return this.item.getVarAsDouble("capped_development");
    }

    public Optional<Double> getRealmDevelopment() {
        return this.item.getVarAsDouble("realm_development");
    }

    public Optional<Double> getUsedGoverningCapacity() {
        return this.item.getVarAsDouble("used_governing_capacity");
    }

    public Optional<Isolationism> getIsolationism() {
        return this.item.getVarAsInt("isolationism").map(i -> this.save.getGame().getIsolationism(i));
    }

    public void setIsolationism(Isolationism isolationism) {
        this.item.setVariable("isolationism", isolationism.getIsolationValue());
    }

    public Optional<Integer> getIsolationismLevel() {
        return getIsolationism().map(Isolationism::getIsolationValue);
    }

    public void setIsolationismLevel(int level) {
        if (level < 0) {
            level = 0;
        } else if (level > 4) {
            level = 4;
        }

        this.item.setVariable("isolationism", level);
    }

    public Optional<Integer> getNumExpandedAdministration() {
        return this.item.getVarAsInt("num_expanded_administration");
    }

    public void setNumExpandedAdministration(Integer numExpandedAdministration) {
        this.item.setVariable("num_expanded_administration", numExpandedAdministration);
    }

    public int getKarma() {
        return this.item.getVarAsDouble("karma").orElse(0d).intValue();
    }

    public void setKarma(int karma) {
        this.item.setVariable("karma", (double) karma);
    }

    public Optional<Boolean> hasReformedReligion() {
        return this.item.getVarAsBool("has_reformed_religion");
    }

    public void setHasReformedReligion(boolean hasReformedReligion) {
        this.item.setVariable("has_reformed_religion", hasReformedReligion);
    }

    public Optional<Double> getHarmony() {
        return this.item.getVarAsDouble("harmony");
    }

    public void setHarmony(Double harmony) {
        this.item.setVariable("harmony", harmony);
    }

    public Optional<SaveReligion> getHarmonizingWithReligion() {
        return this.item.getVarAsString("harmonizing_with_religion").map(s -> this.save.getReligions().getReligion(s));
    }

    public void setHarmonizingWithReligion(SaveReligion harmonizingWithReligion) {
        if (harmonizingWithReligion == null) {
            this.item.removeVariable("harmonizing_with_religion");
        } else {
            this.item.setVariable("harmonizing_with_religion", harmonizingWithReligion.getName());

            if (getHarmonyProgress().isEmpty()) {
                setHarmonyProgress(0d);
            }
        }

    }

    public Optional<Double> getHarmonyProgress() {
        return this.item.getVarAsDouble("harmonization_progress");
    }

    public void setHarmonyProgress(Double harmonizationProgress) {
        if (getHarmonizingWithReligion().isPresent()) {
            this.item.setVariable("harmonization_progress", harmonizationProgress);
        }
    }

    public List<ReligionGroup> getHarmonizedReligionGroups() {
        List<ReligionGroup> religionGroups = new ArrayList<>(this.save.getGame().getReligionGroups());
        return this.item.getList("harmonized_religion_groups")
                        .map(ClausewitzList::getValuesAsInt)
                        .stream()
                        .flatMap(Collection::stream)
                        .map(integer -> integer - 1)
                        .map(religionGroups::get)
                        .toList();
    }

    public void addHarmonizedReligionGroup(ReligionGroup religionGroup) {
        Integer index = null;
        List<ReligionGroup> religionGroups = new ArrayList<>(this.save.getGame().getReligionGroups());

        for (int i = 0; i < religionGroups.size(); i++) {
            if (religionGroups.get(i).equals(religionGroup)) {
                index = i + 1;
                break;
            }
        }

        if (index != null) {
            Integer finalIndex = index;
            this.item.getList("harmonized_religion_groups")
                     .ifPresentOrElse(list -> list.add(finalIndex), () -> this.item.addList("harmonized_religion_groups", finalIndex));
        }
    }

    public void removeHarmonizedReligionGroup(ReligionGroup religionGroup) {
        this.item.getList("harmonized_religion_groups").ifPresent(list -> {
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
        });
    }

    public List<SaveReligion> getHarmonizedReligions() {
        List<SaveReligion> religions = new ArrayList<>(this.save.getReligions().getReligions().values());
        return this.item.getList("harmonized_religions")
                        .map(ClausewitzList::getValuesAsInt)
                        .stream()
                        .flatMap(Collection::stream)
                        .map(integer -> integer - 1)
                        .map(religions::get)
                        .toList();
    }

    public void addHarmonizedReligion(Religion religion) {
        Integer index = null;
        List<Religion> religions = this.save.getGame().getReligions();

        for (int i = 0; i < religions.size(); i++) {
            if (religions.get(i).equals(religion)) {
                index = i + 1;
                break;
            }
        }

        if (index != null) {
            Integer finalIndex = index;
            this.item.getList("harmonized_religions")
                     .ifPresentOrElse(list -> list.add(finalIndex), () -> this.item.addList("harmonized_religions", finalIndex));
        }
    }

    public void removeHarmonizedReligion(Religion religion) {
        this.item.getList("harmonized_religions").ifPresent(list -> {
            Integer index = null;
            List<Religion> religions = this.save.getGame().getReligions();

            for (int i = 0; i < religions.size(); i++) {
                if (religions.get(i).equals(religion)) {
                    index = i + 1;
                    break;
                }
            }

            if (index != null) {
                list.remove(index.toString());
            }
        });
    }

    public List<String> getActiveIncidents() {
        return this.item.getList("active_incidents").map(ClausewitzList::getValues).orElse(new ArrayList<>());
    }

    public List<String> getPotentialIncidents() {
        return this.item.getList("potential_incidents").map(ClausewitzList::getValues).orElse(new ArrayList<>());
    }

    public List<String> getPastIncidents() {
        return this.item.getList("past_incidents").map(ClausewitzList::getValues).orElse(new ArrayList<>());
    }

    public Map<String, Double> getIncidentVariables() {
        return this.item.getChild("active_incidents")
                        .map(ClausewitzItem::getVariables)
                        .stream()
                        .flatMap(Collection::stream)
                        .collect(Collectors.toMap(ClausewitzObject::getName, ClausewitzVariable::getAsDouble));
    }

    public boolean hasCircumnavigatedWorld() {
        return this.item.getVarAsBool("has_circumnavigated_world").map(BooleanUtils::toBoolean).orElse(false);
    }

    public void setHasCircumnavigatedWorld(boolean hasCircumnavigatedWorld) {
        this.item.setVariable("has_circumnavigated_world", hasCircumnavigatedWorld);
    }

    public boolean initializedRivals() {
        return this.item.getVarAsBool("initialized_rivals").map(BooleanUtils::toBoolean).orElse(false);
    }

    public boolean recalculateStrategy() {
        return this.item.getVarAsBool("recalculate_strategy").map(BooleanUtils::toBoolean).orElse(false);
    }

    public boolean dirtyColony() {
        return this.item.getVarAsBool("dirty_colony").map(BooleanUtils::toBoolean).orElse(false);
    }

    public Optional<Culture> getPrimaryCulture() {
        return this.item.getVarAsString("primary_culture").map(s -> this.save.getGame().getCulture(s));
    }

    public void setPrimaryCulture(Culture primaryCulture) {
        this.item.setVariable("primary_culture", primaryCulture.getName());
    }

    public Optional<Culture> getDominantCulture() {
        return this.item.getVarAsString("dominant_culture").map(s -> this.save.getGame().getCulture(s));
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
            this.item.addVariable("accepted_culture", culture.getName(), this.item.getVar("dominant_culture").map(ClausewitzObject::getOrder).orElse(0) + 1);
            this.history.addEvent(this.save.getDate(), "add_accepted_culture", culture.getName());
        }
    }

    public void removeAcceptedCulture(Culture culture) {
        if (this.item.removeVariable("accepted_culture", culture.getName())) {
            this.history.addEvent(this.save.getDate(), "remove_accepted_culture", culture.getName());
        }
    }

    public Optional<String> getReligionName() {
        return this.item.getVarAsString("religion");
    }

    public Optional<SaveReligion> getReligion() {
        return getReligionName().map(s -> this.save.getReligions().getReligion(s));
    }

    public void setReligion(SaveReligion religion) {
        this.item.setVariable("religion", religion.getName());
    }

    public Optional<String> getReligiousSchool() {
        return this.item.getVarAsString("religious_school");
    }

    public void setReligiousSchool(String religiousSchool) {
        this.item.setVariable("religious_school", ClausewitzUtils.addQuotes(religiousSchool));
    }

    public Set<SaveReligion> getAvailableSecondaryReligions() {
        if (getReligion().isEmpty() || !BooleanUtils.toBoolean(getReligion().get().getGameReligion().canHaveSecondaryReligion())) {
            return new HashSet<>();
        }

        Set<SaveReligion> religions = new HashSet<>();
        getSecondaryReligion().ifPresent(religions::add);
        religions.addAll(getOwnedProvinces().stream().map(SaveProvince::getReligion).filter(Optional::isPresent).map(Optional::get).toList());
        //Todo adjacent provinces

        religions.removeIf(Objects::isNull);
        return religions;
    }

    public Optional<SaveReligion> getSecondaryReligion() {
        return this.item.getVarAsString("secondary_religion").map(s -> this.save.getReligions().getReligion(s));
    }

    public void setSecondaryReligion(SaveReligion religion) {
        if (religion == null) {
            this.item.removeVariable("secondary_religion");
        } else {
            this.item.setVariable("secondary_religion", religion.getName());
        }
    }

    public Optional<SaveReligion> getDominantReligion() {
        return this.item.getVarAsString("dominant_religion").map(s -> this.save.getReligions().getReligion(s));
    }

    public SaveFervor getFervor() {
        return fervor;
    }

    public Optional<TechGroup> getTechnologyGroup() {
        return this.item.getVarAsString("technology_group").map(s -> this.save.getGame().getTechGroup(s));
    }

    public void setTechnologyGroup(String technologyGroup) {
        this.item.setVariable("technology_group", technologyGroup);
    }

    public Optional<Double> getLibertyDesire() {
        return this.item.getVarAsDouble("liberty_desire");
    }

    public void setLibertyDesire(Double libertyDesire) {
        this.item.setVariable("liberty_desire", libertyDesire);
    }

    public Optional<String> getUnitType() {
        return this.item.getVarAsString("unit_type");
    }

    public void setUnitType(String unitType) {
        this.item.setVariable("unit_type", unitType);
    }

    public CountryTechnology getTech() {
        return tech;
    }

    public List<SaveEstate> getEstates() {
        return estates;
    }

    public Optional<SaveEstate> getEstate(String name) {
        name = ClausewitzUtils.removeQuotes(name);
        String finalName = name;

        return this.estates.stream()
                           .filter(estate -> estate.getType().map(ClausewitzUtils::removeQuotes).filter(finalName::equalsIgnoreCase).isPresent())
                           .findFirst();
    }

    public CrownLandBonus getCrownLandBonus() {
        double crownLand = 100 - this.getEstates().stream().mapToDouble(SaveEstate::getTerritory).sum();

        return this.save.getGame().getCrownLandBonuses().stream().filter(crownLandBonus -> crownLandBonus.isInRange(crownLand)).findFirst().orElse(null);
    }

    public ActiveAgenda getActiveAgenda() {
        return activeAgenda;
    }

    public List<EstateInteraction> getInteractionsLastUsed() {
        return interactionsLastUsed;
    }

    public List<SaveFaction> getFactions() {
        return factions;
    }

    public Optional<SaveFaction> getFaction(String name) {
        name = ClausewitzUtils.removeQuotes(name);
        String finalName = name;

        return this.factions.stream()
                            .filter(faction -> faction.getType()
                                                      .map(Faction::getName)
                                                      .map(ClausewitzUtils::removeQuotes)
                                                      .filter(finalName::equalsIgnoreCase)
                                                      .isPresent())
                            .findFirst();
    }

    public Optional<Integer> getTopFaction() {
        return this.item.getVarAsInt("top_faction");
    }

    public void setTopFaction(int topFaction) {
        this.item.setVariable("top_faction", topFaction);
    }

    public Map<String, Rival> getRivals() {
        return rivals;
    }

    public void addRival(SaveCountry country, LocalDate date) {
        if (!this.rivals.containsKey(ClausewitzUtils.addQuotes(country.getTag()))) {
            int order = this.item.getVar("highest_possible_fort").map(ClausewitzObject::getOrder).orElse(1);
            Rival.addToItem(this.item, country, date, order);
            refreshAttributes();
        }
    }

    public void removeRival(int index) {
        this.item.removeVariable("rival", index);
        refreshAttributes();
    }

    public void removeRival(SaveCountry rival) {
        this.item.removeChildIf(child -> "rival".equalsIgnoreCase(child.getName()) && ClausewitzUtils.addQuotes(rival.getTag())
                                                                                                     .equalsIgnoreCase(child.getVarAsString("country")));
        refreshAttributes();
    }

    public Optional<Double> getStatistsVsMonarchists() {
        return this.item.getVarAsDouble("statists_vs_monarchists");
    }

    public boolean isStatistsInPower() {
        return getStatistsVsMonarchists().orElse(0d) <= 0 && getGovernment() != null &&
               getGovernment().getReforms()
                              .stream()
                              .anyMatch(reform -> reform.getStatesGeneralMechanic() != null && (reform.getStatesGeneralMechanic().getValue() == null ||
                                                                                                reform.getStatesGeneralMechanic()
                                                                                                      .getValue()
                                                                                                      .apply(this, this)));
    }

    public boolean isMonarchistsInPower() {
        return getStatistsVsMonarchists().orElse(0d) > 0 && getGovernment() != null &&
               getGovernment().getReforms()
                              .stream()
                              .anyMatch(reform -> reform.getStatesGeneralMechanic() != null && (reform.getStatesGeneralMechanic().getValue() == null ||
                                                                                                reform.getStatesGeneralMechanic()
                                                                                                      .getValue()
                                                                                                      .apply(this, this)));
    }

    public void setStatistsVsMonarchists(Double statistsVsMonarchists) {
        statistsVsMonarchists = BigDecimal.valueOf(statistsVsMonarchists).divide(BigDecimal.valueOf(1000d), 3, RoundingMode.HALF_EVEN).doubleValue();
        statistsVsMonarchists = Math.min(Math.max(statistsVsMonarchists, -1), 1);

        this.item.setVariable("statists_vs_monarchists", statistsVsMonarchists);
    }

    public Optional<Double> getMilitarisedSociety() {
        return this.item.getVarAsDouble("militarised_society");
    }

    public void setMilitarisedSociety(Double militarisedSociety) {
        this.item.setVariable("militarised_society", Math.min(Math.max(militarisedSociety, 0), 100));
    }

    public Optional<Double> getTribalAllegiance() {
        return this.item.getVarAsDouble("tribal_allegiance");
    }

    public void setTribalAllegiance(Double tribalAllegiance) {
        this.item.setVariable("tribal_allegiance", Math.min(Math.max(tribalAllegiance, 0), 100));
    }

    public Optional<Integer> getHighestPossibleFort() {
        return this.item.getVarAsInt("highest_possible_fort");
    }

    public Optional<String> getHighestPossibleFortBuilding() {
        return this.item.getVarAsString("highest_possible_fort_building");
    }

    public int getTotalFortLevel() {
        return getOwnedProvinces().stream().filter(province -> province.getOwner().equals(province.getController())).mapToInt(SaveProvince::getFortLevel).sum();
    }

    public Optional<Double> getTransferHomeBonus() {
        return this.item.getVarAsDouble("transfer_home_bonus");
    }

    public Optional<Boolean> isExcommunicated() {
        return this.item.getVarAsBool("excommunicated");
    }

    public void setExcommunicated(boolean excommunicated) {
        this.item.setVariable("excommunicated", excommunicated);
    }

    public Optional<Integer> getNumOfWarReparations() {
        return this.item.getVarAsInt("num_of_war_reparations");
    }

    public Optional<SaveCountry> getCoalitionTarget() {
        return this.item.getVarAsString("coalition_target").map(this.save::getCountry);
    }

    public void setCoalitionTarget(SaveCountry coalitionTarget) {
        this.item.setVariable("coalition_target", ClausewitzUtils.addQuotes(coalitionTarget.getTag()));
        setCoalitionDate(this.save.getDate());
    }

    public Optional<LocalDate> getCoalitionDate() {
        return this.item.getVarAsDate("coalition_date");
    }

    public void setCoalitionDate(LocalDate coalitionDate) {
        if (getCoalitionTarget().isPresent()) {
            this.item.setVariable("coalition_date", coalitionDate);
        }
    }

    public void addWarReparations(SaveCountry country) {
        this.item.setVariable("num_of_war_reparations", getNumOfWarReparations().map(i -> i + 1).orElse(0));
    }

    public void removeWarReparations(SaveCountry country) {
        this.item.setVariable("num_of_war_reparations", getNumOfWarReparations().map(i -> i - 1).orElse(0));
    }

    public Optional<SaveCountry> getOverlord() {
        return this.item.getVarAsString("overlord").map(this.save::getCountry);
    }

    public Optional<SaveCountry> getRootOverlord() {
        return getOverlord().flatMap(SaveCountry::getRootOverlord);
    }

    public SubjectType getSubjectType() {
        return this.subjectType;
    }

    public void setSubjectType(SubjectType subjectType) {
        this.subjectType = subjectType;
    }

    public LocalDate getSubjectStartDate() {
        return subjectStartDate;
    }

    public void setSubjectStartDate(LocalDate subjectStartDate) {
        this.subjectStartDate = subjectStartDate;
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
            this.item.setVariable("overlord", ClausewitzUtils.addQuotes(country.getTag()));
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
        return this.item.getVarsAsStrings("enemy").stream().map(s -> this.save.getCountry(ClausewitzUtils.removeQuotes(s))).toList();
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
        return this.item.getVarsAsStrings("gave_access").stream().map(s -> this.save.getCountry(ClausewitzUtils.removeQuotes(s))).toList();
    }

    public List<SaveCountry> getOurSpyNetwork() {
        return this.item.getVarsAsStrings("our_spy_network").stream().map(s -> this.save.getCountry(ClausewitzUtils.removeQuotes(s))).toList();
    }

    public List<SaveCountry> getTheirSpyNetwork() {
        return this.item.getVarsAsStrings("their_spy_network").stream().map(s -> this.save.getCountry(ClausewitzUtils.removeQuotes(s))).toList();
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
        return this.item.getVarsAsStrings("federation_friends").stream().map(s -> this.save.getCountry(ClausewitzUtils.removeQuotes(s))).toList();
    }

    public List<SaveCountry> getCoalition() {
        return this.item.getVarsAsStrings("coalition_against_us").stream().map(s -> this.save.getCountry(ClausewitzUtils.removeQuotes(s))).toList();
    }

    public List<SaveCountry> getPreferredCoalition() {
        return this.item.getVarsAsStrings("preferred_coalition_against_us").stream().map(s -> this.save.getCountry(ClausewitzUtils.removeQuotes(s))).toList();
    }

    public List<VictoryCard> getVictoryCards() {
        return victoryCards;
    }

    public void addVictoryCard(String area) {
        if (getVictoryCards().stream().noneMatch(victoryCard -> victoryCard.getArea().equalsIgnoreCase(area))) {
            int level = getVictoryCards().stream().mapToInt(VictoryCard::getLevel).max().orElse(0);
            VictoryCard.addToItem(this.item, area, level + 1, 0d, false);
            refreshAttributes();
        }
    }

    public void removeVictoryCard(int index) {
        this.item.removeVariable("victory_card", index);
        refreshAttributes();
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
            refreshAttributes();
        }
    }

    public boolean getConvert() {
        return this.item.getVarAsBool("convert").map(BooleanUtils::toBoolean).orElse(false);
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

    public Optional<SaveCountry> getColonialParent() {
        return this.item.getVarAsString("colonial_parent")
                        .map(ClausewitzUtils::removeQuotes)
                        .filter(Predicate.not(Eu4Utils.DEFAULT_TAG::equals))
                        .map(this.save::getCountry);
    }

    public void setColonialParent(SaveCountry country) {
        this.item.setVariable("colonial_parent", ClausewitzUtils.addQuotes(country.getTag()));
    }

    public boolean newMonarch() {
        return this.item.getVarAsBool("new_monarch").map(BooleanUtils::toBoolean).orElse(false);
    }

    public boolean isAtWar() {
        return this.item.getVarAsBool("is_at_war").map(BooleanUtils::toBoolean).orElse(false);
    }

    public Optional<LocalDate> lastElection() {
        return this.item.getVarAsDate("last_election");
    }

    public void setLastElection(LocalDate lastElection) {
        this.item.setVariable("last_election", lastElection);
    }

    public Optional<Double> getDelayedTreasure() {
        return this.item.getVarAsDouble("delayed_treasure");
    }

    public List<ActivePolicy> getActivePolicies() {
        return activePolicies;
    }

    public ActivePolicy getActivePolicy(String policy) {
        return this.activePolicies.stream().filter(activePolicy -> policy.equals(activePolicy.getPolicy().getName())).findFirst().orElse(null);
    }

    public void addActivePolicy(Policy policy, LocalDate date) {
        if (getActivePolicies().stream().noneMatch(activePolicy -> activePolicy.getPolicy().equals(policy))) {
            ActivePolicy.addToItem(this.item, policy, date, this.item.getVar("last_election").getOrder() + 1);
            refreshAttributes();
        }
    }

    public void removeActivePolicy(int index) {
        this.item.removeVariable("active_policy", index);
        refreshAttributes();
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
            refreshAttributes();
        }
    }

    public Optional<Double> getCurrentPowerProjection() {
        return this.item.getVarAsDouble("current_power_projection");
    }

    private void setCurrentPowerProjection(double currentPowerProjection) {
        this.item.setVariable("current_power_projection", currentPowerProjection);
    }

    public Optional<Double> getGreatPowerScore() {
        return this.item.getVarAsDouble("great_power_score");
    }

    public List<PowerProjection> getPowerProjections() {
        return powerProjections;
    }

    public void removePowerProjections(int index) {
        this.item.removeVariable("power_projection", index);
        refreshAttributes();
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
            refreshAttributes();
        }
    }

    public Optional<Integer> getNumOfTradeEmbargos() {
        return this.item.getVarAsInt("num_of_trade_embargos");
    }

    public Optional<Integer> getNumOfNonRivalTradeEmbargos() {
        return this.item.getVarAsInt("num_of_non_rival_trade_embargos");
    }

    public Optional<Double> getNavyStrength() {
        return this.item.getVarAsDouble("navy_strength");
    }

    public SaveCountry getPreferredEmperor() {
        return this.save.getCountry(this.item.getVarAsString("preferred_emperor"));
    }

    public Parliament getParliament() {
        return parliament;
    }

    public Optional<Double> getTariff() {
        return this.item.getVarAsDouble("tariff");
    }

    public void setTariff(double tariff) {
        this.item.setVariable("tariff", tariff);
    }

    public Optional<Integer> getTotalWarWorth() {
        return this.item.getVarAsInt("total_war_worth");
    }

    public int getNumOwnedHomeCores() {
        return this.item.getVarAsInt("num_owned_home_cores").orElse(0);
    }

    public double getNonOverseasDevelopment() {
        return this.item.getVarAsDouble("non_overseas_development").orElse(0d);
    }

    public int getNumShipsPrivateering() {
        return this.item.getVarAsInt("num_ships_privateering").orElse(0);
    }

    public int getNumOfControlledCities() {
        return this.item.getVarAsInt("num_of_controlled_cities").orElse(0);
    }

    public int getNumOfPorts() {
        return this.item.getVarAsInt("num_of_ports").orElse(0);
    }

    public int getNumOfCorePorts() {
        return this.item.getVarAsInt("num_of_core_ports").orElse(0);
    }

    public int getNumOfTotalPorts() {
        return this.item.getVarAsInt("num_of_total_ports").orElse(0);
    }

    public int getNumOfCardinals() {
        return this.item.getVarAsInt("num_of_cardinals").orElse(0);
    }

    public int getNumOfMercenaries() {
        return this.item.getVarAsInt("num_of_mercenaries").orElse(0);
    }

    public int getNumOfRegulars() {
        return this.item.getVarAsInt("num_of_regulars").orElse(0);
    }

    public int getNumOfCities() {
        return this.item.getVarAsInt("num_of_cities").orElse(0);
    }

    public int getNumOfProvincesInStates() {
        return this.item.getVarAsInt("num_of_provinces_in_states").orElse(0);
    }

    public int getNumOfProvincesInTerritories() {
        return this.item.getVarAsInt("num_of_provinces_in_territories").orElse(0);
    }

    public int getNumOfForts() {
        return this.item.getVarAsInt("forts").orElse(0);
    }

    public int getNumOfAllies() {
        return this.item.getVarAsInt("num_of_allies").orElse(0);
    }

    public int getNumOfRoyalMarriages() {
        return this.item.getVarAsInt("num_of_royal_marriages").orElse(0);
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

    public int getNumOfSubjects() {
        return this.item.getVarAsInt("num_of_subjects").orElse(0);
    }

    public int getNumOfHeathenProvs() {
        return this.item.getVarAsInt("num_of_heathen_provs").orElse(0);
    }

    public Optional<Double> getInlandSeaRatio() {
        return this.item.getVarAsDouble("inland_sea_ratio");
    }

    public Optional<Boolean> hasFriendlyReformationCenter() {
        return this.item.getVarAsBool("has_friendly_reformation_center=yes");
    }

    public Optional<Double> getAverageUnrest() {
        return this.item.getVarAsDouble("average_unrest");
    }

    public Optional<Double> getAverageEffectiveUnrest() {
        return this.item.getVarAsDouble("average_effective_unrest");
    }

    public Optional<Double> getAverageAutonomy() {
        return this.item.getVarAsDouble("average_autonomy");
    }

    public Optional<Double> getAverageAutonomyAboveMin() {
        return this.item.getVarAsDouble("average_autonomy_above_min");
    }

    public Optional<Double> getAverageHomeAutonomy() {
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

        return child.getVariables().stream().collect(Collectors.toMap(var -> Integer.parseInt(var.getName()), ClausewitzVariable::getAsInt));
    }

    public Map<Integer, Integer> getNumOfBuildingsUnderConstructionIndexed() {
        ClausewitzItem child = this.item.getChild("num_of_buildings_under_construction_indexed");

        if (child == null) {
            return new HashMap<>();
        }

        return child.getVariables().stream().collect(Collectors.toMap(var -> Integer.parseInt(var.getName()), ClausewitzVariable::getAsInt));
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

        return child.getVariables().stream().collect(Collectors.toMap(var -> Integer.parseInt(var.getName()), ClausewitzVariable::getAsInt));
    }

    public Map<Integer, Double> getNumOfReligionsDev() {
        ClausewitzItem child = this.item.getChild("num_of_religions_dev");

        if (child == null) {
            return new HashMap<>();
        }

        return child.getVariables().stream().collect(Collectors.toMap(var -> Integer.parseInt(var.getName()), ClausewitzVariable::getAsDouble));
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

        return child.getVariables().stream().collect(Collectors.toMap(var -> Integer.parseInt(var.getName()), ClausewitzVariable::getAsInt));
    }

    public Map<Integer, Double> getBorderPct() {
        ClausewitzItem child = this.item.getChild("border_pct");

        if (child == null) {
            return new HashMap<>();
        }

        return child.getVariables().stream().collect(Collectors.toMap(var -> Integer.parseInt(var.getName()), ClausewitzVariable::getAsDouble));
    }

    public Map<Integer, Double> getBorderSit() {
        ClausewitzItem child = this.item.getChild("border_sit");

        if (child == null) {
            return new HashMap<>();
        }

        return child.getVariables().stream().collect(Collectors.toMap(var -> Integer.parseInt(var.getName()), ClausewitzVariable::getAsDouble));
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

    public Optional<Double> getTreasury() {
        return this.item.getVarAsDouble("treasury");
    }

    public void setTreasury(Double treasury) {
        if (treasury > 1000000d) {
            treasury = 1000000d;
        }

        this.item.setVariable("treasury", treasury);
    }

    public Optional<Double> getEstimatedMonthlyIncome() {
        return this.item.getVarAsDouble("estimated_monthly_income");
    }

    public Optional<Double> getInflation() {
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

    public Optional<Boolean> updateOpinionCache() {
        return this.item.getVarAsBool("update_opinion_cache");
    }

    public Optional<Boolean> needsRefresh() {
        return this.item.getVarAsBool("needs_refresh");
    }

    public Optional<Boolean> casusBellisRefresh() {
        return this.item.getVarAsBool("casus_bellis_refresh");
    }

    public Optional<Boolean> needsRebelUnitRefresh() {
        return this.item.getVarAsBool("needs_rebel_unit_refresh");
    }

    public Optional<LocalDate> lastBetrayedAlly() {
        return this.item.getVarAsDate("last_betrayed_ally");
    }

    public void setLastBetrayedAlly(LocalDate lastBetrayedAlly) {
        this.item.setVariable("last_betrayed_ally", lastBetrayedAlly);
    }

    public Optional<LocalDate> lastBankrupt() {
        return this.item.getVarAsDate("last_bankrupt");
    }

    public void setLastBankrupt(LocalDate lastBankrupt) {
        this.item.setVariable("last_bankrupt", lastBankrupt);
    }

    public Optional<Double> getWarExhaustion() {
        return this.item.getVarAsDouble("war_exhaustion");
    }

    public void setWarExhaustion(double warExhaustion) {
        this.item.setVariable("war_exhaustion", warExhaustion);
    }

    public Optional<Double> getMonthlyWarExhaustion() {
        return this.item.getVarAsDouble("monthly_war_exhaustion");
    }

    public Optional<Boolean> canTakeWartaxes() {
        return this.item.getVarAsBool("can_take_wartaxes");
    }

    public Optional<Boolean> warTaxes() {
        return this.item.getVarAsBool("wartax");
    }

    public Optional<Double> getLandMaintenance() {
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

    public Optional<Double> getNavalMaintenance() {
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

    public Optional<Double> getColonialMaintenance() {
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

    public Optional<Double> getMissionaryMaintenance() {
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

    public Optional<Double> getArmyTradition() {
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

    public Optional<Double> getNavyTradition() {
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

    public Optional<LocalDate> getLastWarEnded() {
        return this.item.getVarAsDate("last_war_ended");
    }

    public Optional<Integer> getNumUncontestedCores() {
        return this.item.getVarAsInt("num_uncontested_cores");
    }

    public Ledger getLedger() {
        return ledger;
    }

    public Optional<Integer> getCancelledLoans() {
        return this.item.getVarAsInt("cancelled_loans");
    }

    public Optional<Integer> getLoanSize() {
        return this.item.getVarAsInt("loan_size");
    }

    /**
     * Estimation of interests for a loan (ie: 100 = get 300 ducas, need to give back 400)
     */
    public Optional<Double> getEstimatedLoan() {
        return this.item.getVarAsDouble("estimated_loan");
    }

    public List<Loan> getLoans() {
        return loans;
    }

    public void addLoan(double interest, int amount, LocalDate expiryDate) {
        Loan.addToItem(this.item, this.save.getIds().get(4713).incrementId(2), interest, true, amount, expiryDate);
        refreshAttributes();
    }

    public void removeLoan(int id) {
        Integer index = null;
        List<Loan> list = getLoans();

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().get().getId().equals(id)) {
                index = i;
                break;
            }
        }

        if (index != null) {
            this.item.removeChild("loan", index);
            refreshAttributes();
        }
    }

    public Optional<Double> getReligiousUnity() {
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

    public Optional<Double> getRepublicanTradition() {
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

    public Optional<Double> getDevotion() {
        return this.item.getVarAsDouble("devotion");
    }

    public Optional<Double> getMeritocracy() {
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

    public Optional<Double> getPiety() {
        return this.item.getVarAsDouble("piety").map(aDouble -> aDouble * 100);
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

    public Optional<Double> getPatriarchAuthority() {
        return this.item.getVarAsDouble("patriarch_authority").map(aDouble -> aDouble * 100);
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

    public Optional<Integer> getCurrentIcon() {
        return this.item.getVarAsInt("current_icon");
    }

    public void setPatriarchAuthority(int icon) {
        if (getReligion().isPresent() && getReligion().get().getGameReligion().getIcons() != null && icon >= 0 &&
            icon < getReligion().get().getGameReligion().getIcons().size()) {
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

    public Optional<Double> getPapalInfluence() {
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

    public Optional<Double> getCorruption() {
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

    public Optional<Double> getRecoveryMotivation() {
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

    public Optional<Double> getRootOutCorruptionSlider() {
        return this.item.getVarAsDouble("root_out_corruption_slider");
    }

    public Optional<Double> getDoom() {
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
                        .filter(PersonalDeity -> PersonalDeity.getAllow().isEmpty() || PersonalDeity.getAllow().get().apply(this, this))
                        .toList();
    }

    public Optional<PersonalDeity> getPersonalDeity() {
        return this.item.getVar("personal_deity").map(ClausewitzVariable::getValue).map(s -> this.save.getGame().getPersonalDeity(s));
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
                        .filter(fetishistCult -> fetishistCult.getAllow().isEmpty() || fetishistCult.getAllow().get().apply(this, this))
                        .toList();
    }

    public Optional<FetishistCult> getFetishistCult() {
        return this.item.getVar("fetishist_cult").map(ClausewitzVariable::getValue).map(s -> this.save.getGame().getFetishistCult(s));
    }

    public void setFetishistCult(FetishistCult fetishistCult) {
        if (fetishistCult == null) {
            this.item.removeVariable("fetishist_cult");
        } else {
            this.item.setVariable("fetishist_cult", ClausewitzUtils.addQuotes(fetishistCult.getName()));
        }
    }

    public Optional<Double> getAuthority() {
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

    public Optional<Double> getLegitimacy() {
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

    public Optional<Double> getLegitimacyEquivalent() {
        return Eu4Utils.coalesce(getLegitimacy(), getHordeUnity(), getRepublicanTradition(), getMeritocracy(), getDevotion());
    }

    public Optional<Double> getHordeUnity() {
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

    public Optional<Double> getLegitimacyOrHordeUnity() {
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

    public Optional<Integer> getSplendor() {
        return this.item.getVarAsDouble("splendor").map(Double::intValue);
    }

    public void setSplendor(Integer splendor) {
        if (splendor < 0) {
            splendor = 0;
        }

        this.item.setVariable("splendor", (double) splendor);
    }

    public Optional<Integer> getAbsolutism() {
        return this.item.getVarAsDouble("absolutism").map(Double::intValue);
    }

    public void setAbsolutism(Integer absolutism) {
        if (absolutism < 0) {
            absolutism = 0;
        } else if (absolutism > 100) {
            absolutism = 100;
        }

        this.item.setVariable("absolutism", absolutism);
    }

    public Optional<Double> getArmyProfessionalism() {
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

    public Optional<Double> getMaxHistoryArmyProfessionalism() {
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

    public Optional<String> getActiveDisaster() {
        return this.item.getVarAsString("active_disaster");
    }

    public void setActiveDisaster(String activeDisaster) {
        this.item.setVariable("active_disaster", activeDisaster);
    }

    public Church getChurch() {
        return church;
    }

    public IdeaGroups getIdeaGroups() {
        return ideaGroups;
    }

    public SaveReligiousReforms getReligiousReforms() {
        return religiousReforms;
    }

    public SaveNativeAdvancements getNativeAdvancements() {
        return nativeAdvancements;
    }

    public SaveGovernment getGovernment() {
        return government;
    }

    public List<Envoy> getColonists() {
        return this.colonists == null ? new ArrayList<>() : this.colonists;
    }

    public List<Envoy> getMerchants() {
        return this.merchants == null ? new ArrayList<>() : this.merchants;
    }

    public List<Envoy> getMissionaries() {
        return this.missionaries == null ? new ArrayList<>() : this.missionaries;
    }

    public List<Envoy> getDiplomats() {
        return this.diplomats == null ? new ArrayList<>() : this.diplomats;
    }

    public List<SaveModifier> getModifiers() {
        return this.modifiers == null ? new ArrayList<>() : this.modifiers;
    }

    public void addModifier(String modifier, LocalDate date) {
        addModifier(modifier, date, null);
    }

    public void addModifier(String modifier, LocalDate date, Boolean hidden) {
        SaveModifier.addToItem(this.item, modifier, date, hidden);
        refreshAttributes();
    }

    public void removeModifier(int index) {
        this.item.removeChild("modifier", index);
        refreshAttributes();
    }

    public void removeModifier(GameModifier modifier) {
        removeModifier(modifier.getName());
    }

    public void removeModifier(String modifier) {
        Integer index = null;
        modifier = ClausewitzUtils.addQuotes(modifier);

        for (int i = 0; i < this.modifiers.size(); i++) {
            if (this.modifiers.get(i).getModifierName().equalsIgnoreCase(modifier)) {
                index = i;
                break;
            }
        }

        if (index != null) {
            this.item.removeChild("modifier", index);
            refreshAttributes();
        }
    }

    public Optional<Double> getManpower() {
        return this.item.getVarAsDouble("manpower");
    }

    public void setManpower(Double manpower) {
        this.item.setVariable("manpower", manpower);
    }

    public Optional<Double> getMaxManpower() {
        return this.item.getVarAsDouble("max_manpower");
    }

    public Optional<Double> getSailors() {
        return this.item.getVarAsDouble("sailors");
    }

    public void setSailors(Double sailors) {
        this.item.setVariable("sailors", sailors);
    }

    public Optional<Double> getMaxSailors() {
        return this.item.getVarAsDouble("max_sailors");
    }

    public SubUnit getSubUnit() {
        return subUnit;
    }

    public Optional<Integer> getNumOfCapturedShipsWithBoardingDoctrine() {
        return this.item.getVarAsInt("num_of_captured_ships_with_boarding_doctrine");
    }

    public void setNumOfCapturedShipsWithBoardingDoctrine(Integer numOfCapturedShipsWithBoardingDoctrine) {
        this.item.setVariable("num_of_captured_ships_with_boarding_doctrine", numOfCapturedShipsWithBoardingDoctrine);
    }

    public Optional<Double> getOverextensionPercentage() {
        return this.item.getVarAsDouble("overextension_percentage");
    }

    public Map<Integer, MercenaryCompany> getMercenaryCompanies() {
        return mercenaryCompanies;
    }

    public Army getArmy(Id id) {
        return this.armies.get(id);
    }

    public Map<Id, Army> getArmies() {
        return armies;
    }

    public int getArmySize() {
        return this.armies.values().stream().mapToInt(army -> army.getRegiments().size()).sum();
    }

    public int getNavySize() {
        return this.navies.values().stream().mapToInt(army -> army.getRegiments().size()).sum();
    }

    public long getNbInfantry() {
        return this.armies.values()
                          .stream()
                          .mapToLong(army -> army.getRegiments().stream().filter(regiment -> UnitType.INFANTRY.equals(regiment.getUnitType())).count())
                          .sum();
    }

    public long getNbCavalry() {
        return this.armies.values()
                          .stream()
                          .mapToLong(army -> army.getRegiments().stream().filter(regiment -> UnitType.CAVALRY.equals(regiment.getUnitType())).count())
                          .sum();
    }

    public long getNbArtillery() {
        return this.armies.values()
                          .stream()
                          .mapToLong(army -> army.getRegiments().stream().filter(regiment -> UnitType.ARTILLERY.equals(regiment.getUnitType())).count())
                          .sum();
    }

    public long getNbHeavyShips() {
        return this.navies.values()
                          .stream()
                          .mapToLong(army -> army.getShips().stream().filter(regiment -> UnitType.HEAVY_SHIP.equals(regiment.getUnitType())).count())
                          .sum();
    }

    public long getNbLightShips() {
        return this.navies.values()
                          .stream()
                          .mapToLong(army -> army.getShips().stream().filter(regiment -> UnitType.LIGHT_SHIP.equals(regiment.getUnitType())).count())
                          .sum();
    }

    public long getNbGalleys() {
        return this.navies.values()
                          .stream()
                          .mapToLong(army -> army.getShips().stream().filter(regiment -> UnitType.GALLEY.equals(regiment.getUnitType())).count())
                          .sum();
    }

    public long getNbTransports() {
        return this.navies.values()
                          .stream()
                          .mapToLong(army -> army.getShips().stream().filter(regiment -> UnitType.TRANSPORT.equals(regiment.getUnitType())).count())
                          .sum();
    }

    public long getNbRegimentOf(String type) {
        return this.armies.values()
                          .stream()
                          .mapToLong(army -> army.getRegiments().stream().filter(regiment -> type.equals(regiment.getTypeName())).count())
                          .sum();
    }

    public long getNbRegimentOfCategory(int category) {
        return this.armies.values()
                          .stream()
                          .mapToLong(army -> army.getRegiments()
                                                 .stream()
                                                 .filter(regiment -> regiment.getCategory() != null && category == regiment.getCategory())
                                                 .count())
                          .sum();
    }

    public void addArmy(String name, int location, String graphicalCulture, String regimentName, String regimentType, double regimentMorale,
                        double regimentDrill) {
        //Todo location -> 		unit={
        //    //			id=6520
        //    //			type=54
        //    //		}
        Army.addToItem(this.item, this.save.getAndIncrementUnitIdCounter(), name, location, graphicalCulture, this.save.getAndIncrementUnitIdCounter(),
                       regimentName, location, regimentType, regimentMorale, regimentDrill);
        refreshAttributes();
    }

    public Navy getNavy(Id id) {
        return this.navies.get(id);
    }

    public Map<Id, Navy> getNavies() {
        return navies;
    }

    public void addNavy(String name, int location, String graphicalCulture, String shipName, String shipType, double shipMorale) {
        //Todo location -> 		unit={
        //    //			id=6520
        //    //			type=54
        //    //		}
        Navy.addToItem(this.item, this.save.getAndIncrementUnitIdCounter(), name, location, graphicalCulture, this.save.getAndIncrementUnitIdCounter(),
                       shipName, location, shipType, shipMorale);
        refreshAttributes();
    }

    public void removeAeFor(String tag) {
        tag = ClausewitzUtils.removeQuotes(tag).toUpperCase();
        ClausewitzItem child = this.item.getChild("active_relations");

        if (child != null) {
            child = child.getChild(tag);

            if (child != null && child.getNbChildren() > 0) {
                child.removeChildIf(item -> "opinion".equals(item.getName()) && item.hasVar("modifier", "\"aggressive_expansion\""));
            }
        }
    }

    public Map<String, ActiveRelation> getActiveRelations() {
        return activeRelations;
    }

    public ActiveRelation getActiveRelation(SaveCountry country) {
        return this.activeRelations.get(country.getTag());
    }

    public Map<Integer, Leader> getLeaders() {
        return leaders;
    }

    public List<Leader> getLeadersOfType(LeaderType leaderType) {
        return this.leaders.values().stream().filter(leader -> leaderType.equals(leader.getType())).toList();
    }

    public void addLeader(LocalDate date, LocalDate birthDate, String name, LeaderType type, int manuever, int fire, int shock, int siege,
                          LeaderPersonality personality) {
        int leaderId = this.save.getIdCounters().getAndIncrement(Counter.LEADER);
        Id.addToItem(this.item, "leader", leaderId, 49, this.item.getChild("active_relations").getOrder() + 1);
        this.history.addLeader(date, birthDate, name, type, manuever, fire, shock, siege, personality, leaderId);
        refreshAttributes();
    }

    public void removeLeader(int id) {
        Leader leader = this.leaders.get(id);

        if (leader != null) {
            this.armies.values().stream().filter(army -> army.getLeader().getId().equals(id)).findFirst().ifPresent(AbstractArmy::removeLeader);

            List<Id> leadersIds = this.item.getChildren("leader").stream().map(Id::new).toList();

            for (int i = 0; i < leadersIds.size(); i++) {
                if (leadersIds.get(i).equals(leader.getId())) {
                    this.item.removeChild("leader", i);
                    break;
                }
            }
        }
    }

    public Optional<Integer> getDecisionSeed() {
        return this.item.getVarAsInt("decision_seed");
    }

    public Monarch getMonarch() {
        return monarch;
    }

    public Heir getHeir() {
        return heir;
    }

    public Queen getQueen() {
        return queen;
    }

    public Monarch getConsort() {
        return Optional.ofNullable(this.queen).map(q -> q.getConsort().filter(BooleanUtils::toBoolean).orElse(false) ? this.queen : this.monarch).orElse(null);
    }

    public Optional<String> getOriginalDynasty() {
        return this.item.getVarAsString("original_dynasty");
    }

    public void setOriginalDynasty(String originalDynasty) {
        this.item.setVariable("original_dynasty", originalDynasty);
    }

    public Optional<Integer> getNumOfConsorts() {
        return this.item.getVarAsInt("num_of_consorts");
    }

    public void setNumOfConsorts(int numOfConsorts) {
        if (numOfConsorts < 0) {
            numOfConsorts = 0;
        }

        this.item.setVariable("num_of_consorts", numOfConsorts);
    }

    public int getNumOfRelations() {
        return (int) (getNumOfAllies() + getNumOfRoyalMarriages() + getNumOfSubjects() + this.save.getDiplomacy()
                                                                                                  .getGuarantees()
                                                                                                  .stream()
                                                                                                  .filter(guarantee -> this.equals(guarantee.getFirst()))
                                                                                                  .count() + this.save.getDiplomacy()
                                                                                                                      .getMilitaryAccesses()
                                                                                                                      .stream()
                                                                                                                      .filter(guarantee -> this.equals(
                                                                                                                              guarantee.getSecond()))
                                                                                                                      .count());
    }

    public boolean isGreatPower() {
        return this.item.getVarAsBool("is_great_power").map(BooleanUtils::toBoolean).orElse(false);
    }

    public Optional<LocalDate> getInauguration() {
        return this.item.getVarAsDate("inauguration");
    }

    public Optional<LocalDate> getLastMigration() {
        return this.item.getVarAsDate("last_migration");
    }

    public void setLastMigration(LocalDate lastMigration) {
        this.item.setVariable("last_migration", lastMigration);
    }

    public List<Id> getPreviousMonarchs() {
        return previousMonarchs;
    }

    public List<Id> getAdvisorsIds() {
        return advisorsIds;
    }

    public Map<Integer, SaveAdvisor> getInternalAdvisors() {
        if (this.advisors == null) {
            this.advisors = new HashMap<>();
        }

        return this.advisors;
    }

    public Map<Integer, SaveAdvisor> getAdvisors() {
        return this.advisors == null ? new HashMap<>() : this.advisors;
    }

    public void addAdvisor(SaveAdvisor advisor) {
        getInternalAdvisors().put(advisor.getId().getId(), advisor);
    }

    public Map<Integer, SaveAdvisor> getInternalActiveAdvisors() {
        if (this.activeAdvisors == null) {
            this.activeAdvisors = new HashMap<>();
        }

        return this.activeAdvisors;
    }

    public Map<Integer, SaveAdvisor> getActiveAdvisors() {
        return this.activeAdvisors == null ? new HashMap<>() : this.activeAdvisors;
    }

    public void addActiveAdvisor(SaveAdvisor advisor) {
        getInternalActiveAdvisors().put(advisor.getId().getId(), advisor);
    }

    public void setActiveAdvisors(Map<Integer, SaveAdvisor> activeAdvisors) {
        this.activeAdvisors = activeAdvisors;
    }

    public boolean getAssignedEstates() {
        return this.item.getVarAsBool("assigned_estates").map(BooleanUtils::toBoolean).orElse(false);
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

    public Optional<Double> getBlockadedPercent() {
        return this.item.getVarAsDouble("blockaded_percent");
    }

    public List<String> getPreviousCountryTags() {
        return this.item.getVarsAsStrings("previous_country_tags");
    }

    public SortedMap<LocalDate, String> getChangedTags() {
        if (CollectionUtils.isEmpty(getPreviousCountryTags())) {
            return new TreeMap<>();
        } else {
            return this.history.getEvents()
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
                               })
                               .orElse(null);
        }

        return null;
    }

    public List<CustomNationalIdea> getCustomNationalIdeas() {
        return customNationalIdeas;
    }

    public Optional<Integer> getCustomNationalIdeasLevel() {
        return this.item.getVarAsInt("custom_national_ideas_level");
    }

    public Optional<Integer> getNativePolicy() {
        return this.item.getVarAsInt("native_policy");
    }

    public void setNativePolicy(int nativePolicy) {
        this.item.setVariable("native_policy", nativePolicy);
    }

    public Optional<LocalDate> getAntiNationRuiningEndDate() {
        return this.item.getVarAsDate("anti_nation_ruining_end_date");
    }

    public void setAntiNationRuiningEndDate(LocalDate antiNationRuiningEndDate) {
        this.item.setVariable("anti_nation_ruining_end_date", antiNationRuiningEndDate);
    }

    public Optional<Double> getSpyPropensity() {
        return this.item.getVarAsDouble("spy_propensity");
    }

    public PowerSpentIndexed getAdmPowerSpent() {
        return admPowerSpent;
    }

    public PowerSpentIndexed getDipPowerSpent() {
        return dipPowerSpent;
    }

    public PowerSpentIndexed getMilPowerSpent() {
        return milPowerSpent;
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

    public Optional<Double> getInnovativeness() {
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
        return historyStatsCache;
    }

    public Optional<NavalDoctrine> getNavalDoctrine() {
        return this.item.getVarAsString("naval_doctrine").map(s -> this.save.getGame().getNavalDoctrine(s));
    }

    public void setNavalDoctrine(NavalDoctrine navalDoctrine) {
        this.item.setVariable("naval_doctrine", ClausewitzUtils.addQuotes(navalDoctrine.getName()));
    }

    public Missions getCountryMissions() {
        return countryMissions;
    }

    public Optional<Double> getGovernmentReformProgress() {
        return this.item.getVarAsDouble("government_reform_progress");
    }

    public void setGovernmentReformProgress(Double governmentReformProgress) {
        this.item.setVariable("government_reform_progress", governmentReformProgress);
    }

    public Map<SaveArea, CountryState> getStates() {
        return this.states;
    }

    public SortedMap<Integer, Integer> getIncomeStatistics() {
        return incomeStatistics == null ? new TreeMap<>() : this.incomeStatistics;
    }

    public void putAllIncomeStatistics(Map<Integer, Integer> incomeStatistics) {
        if (this.incomeStatistics == null) {
            this.incomeStatistics = new TreeMap<>();
        }

        this.incomeStatistics.putAll(incomeStatistics);
    }

    public SortedMap<Integer, Integer> getNationSizeStatistics() {
        return nationSizeStatistics == null ? new TreeMap<>() : this.nationSizeStatistics;
    }

    public void putAllNationSizeStatistics(Map<Integer, Integer> nationSizeStatistics) {
        if (this.nationSizeStatistics == null) {
            this.nationSizeStatistics = new TreeMap<>();
        }

        this.nationSizeStatistics.putAll(nationSizeStatistics);
    }

    public SortedMap<Integer, Integer> getScoreStatistics() {
        return scoreStatistics == null ? new TreeMap<>() : this.scoreStatistics;
    }

    public void putAllScoreStatistics(Map<Integer, Integer> scoreStatistics) {
        if (this.scoreStatistics == null) {
            this.scoreStatistics = new TreeMap<>();
        }

        this.scoreStatistics.putAll(scoreStatistics);
    }

    public SortedMap<Integer, Integer> getInflationStatistics() {
        return inflationStatistics == null ? new TreeMap<>() : this.inflationStatistics;
    }

    public void putAllInflationStatistics(Map<Integer, Integer> inflationStatistics) {
        if (this.inflationStatistics == null) {
            this.inflationStatistics = new TreeMap<>();
        }

        this.inflationStatistics.putAll(inflationStatistics);
    }

    public List<SaveTradeCompany> getTradeCompanies() {
        return tradeCompanies == null ? new ArrayList<>() : this.tradeCompanies;
    }

    public void addTradeCompany(SaveTradeCompany tradeCompany) {
        if (this.tradeCompanies == null) {
            this.tradeCompanies = new ArrayList<>(0);
        }

        this.tradeCompanies.add(tradeCompany);
    }

    public TradeLeague getTradeLeague() {
        return tradeLeague;
    }

    /* Don't use only for game !!
       Use tradeLeague addMember to add
     */
    public void setTradeLeague(TradeLeague tradeLeague) {
        this.tradeLeague = tradeLeague;
    }

    public SortedSet<ActiveWar> getWars() {
        return wars;
    }

    public SortedSet<ActiveWar> getActiveWars() {
        return this.wars.stream().filter(Predicate.not(ActiveWar::isFinished)).collect(Collectors.toCollection(TreeSet::new));
    }

    public SortedSet<ActiveWar> getFinishedWars() {
        return this.wars.stream().filter(ActiveWar::isFinished).collect(Collectors.toCollection(TreeSet::new));
    }

    public void addWar(ActiveWar war) {
        this.wars.add(war);
    }

    public boolean isFreeCity() {
        return !this.save.getHre().dismantled() && isAlive() && getGovernment() != null && CollectionUtils.isNotEmpty(getGovernment().getReforms())
               && getGovernment().getReforms()
                                 .stream()
                                 .anyMatch(reform -> reform.isFreeCity().getKey() && (reform.isFreeCity().getValue() == null || reform.isFreeCity()
                                                                                                                                      .getValue()
                                                                                                                                      .apply(this, this)));
    }

    public boolean isElector() {
        return !this.save.getHre().dismantled() && isAlive() && this.save.getHre().getElectors().contains(this);
    }

    public boolean isEmperor() {
        return !this.save.getHre().dismantled() && isAlive() && this.equals(this.save.getHre().getEmperor());
    }

    public double getGoverningCapacity() {
        return (NumbersUtils.doubleOrDefault(getModifier(ModifiersUtils.getModifier("governing_capacity")))
                + NumbersUtils.doubleOrDefault(getModifier(ModifiersUtils.getModifier("tech_governing_capacity")))) * (1
                                                                                                                       + NumbersUtils.doubleOrDefault(
                getModifier(ModifiersUtils.getModifier("governing_capacity_modifier"), false)));
    }

    public double getGoverningCapacityUsedPercent() {
        return getUsedGoverningCapacity().orElse(0d) / getGoverningCapacity();
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
        return (NumbersUtils.doubleOrDefault(getModifier(ModifiersUtils.getModifier("land_forcelimit"), false)) + getOwnedProvinces().stream()
                                                                                                                                     .mapToDouble(
                                                                                                                                             SaveProvince::getLandForceLimit)
                                                                                                                                     .sum()) * (1
                                                                                                                                                + NumbersUtils.doubleOrDefault(
                getModifier(ModifiersUtils.getModifier("land_forcelimit_modifier"), false)));
    }

    public double getNavalForceLimit() {
        if (getOwnedProvinces().stream().noneMatch(SaveProvince::isPort)) {
            return 0;
        }

        return (NumbersUtils.doubleOrDefault(getModifier(ModifiersUtils.getModifier("naval_forcelimit"), false))
                + NumbersUtils.doubleOrDefault(getOwnedProvinces().stream().mapToDouble(SaveProvince::getNavalForceLimit).sum())) * (1
                                                                                                                                     + NumbersUtils.doubleOrDefault(
                getModifier(ModifiersUtils.getModifier("naval_forcelimit_modifier"), false)));
    }

    public double getProductionEfficiency() {
        return NumbersUtils.doubleOrDefault(getModifier(ModifiersUtils.getModifier("production_efficiency")))
               + NumbersUtils.doubleOrDefault(getModifier(ModifiersUtils.getModifier("tech_production_efficiency")));
    }

    public double getTradeEfficiency() {
        return NumbersUtils.doubleOrDefault(getModifier(ModifiersUtils.getModifier("trade_efficiency")))
               + NumbersUtils.doubleOrDefault(getModifier(ModifiersUtils.getModifier("tech_trade_efficiency")));
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

        if (CollectionUtils.isNotEmpty(getModifiers())) {
            list.addAll(getModifiers().stream()
                                      .filter(m -> m.getModifier() != null)
                                      .filter(m -> !StaticModifier.class.equals(m.getModifier().getClass()))
                                      .map(m -> m.getModifiers(this, modifier))
                                      .toList());
        }

        if (getIdeaGroups() != null) {
            getIdeaGroups().getIdeaGroups().forEach((key, value) -> list.add(key.getModifier(value, modifier)));
        }

        //Todo neighbours

        if ("ADM_TECH_COST_MODIFIER".equalsIgnoreCase(modifier.getName())) {
            if (getIdeaGroups() != null) {
                list.add(getIdeaGroups().getIdeaGroups()
                                        .entrySet()
                                        .stream()
                                        .filter(entry -> Power.ADM.equals(entry.getKey().getCategory()))
                                        .mapToInt(Map.Entry::getValue)
                                        .sum() * this.save.getGame().getIdeaToTech());
            }

            if (CollectionUtils.isNotEmpty(getOurSpyNetwork())) {
                getOurSpyNetwork().stream().max(Comparator.comparing(o -> o.getTech().getAdm())).ifPresent(country -> {
                    if (country.getTech().getAdm() > getTech().getAdm()) {
                        double mult = Math.max(this.save.getGame().getSpyNetworkTechEffectMax(),
                                               (country.getTech().getAdm() - getTech().getAdm()) * this.save.getGame().getSpyNetworkTechEffect());
                        list.add(mult * NumbersUtils.intOrDefault(getActiveRelation(country).getSpyNetwork()));
                    }
                });
            }
        } else if ("DIP_TECH_COST_MODIFIER".equalsIgnoreCase(modifier.getName())) {
            if (getIdeaGroups() != null) {
                list.add(getIdeaGroups().getIdeaGroups()
                                        .entrySet()
                                        .stream()
                                        .filter(entry -> Power.DIP.equals(entry.getKey().getCategory()))
                                        .mapToInt(Map.Entry::getValue)
                                        .sum() * this.save.getGame().getIdeaToTech());
            }

            if (CollectionUtils.isNotEmpty(getOurSpyNetwork())) {
                getOurSpyNetwork().stream().max(Comparator.comparing(o -> o.getTech().getDip())).ifPresent(country -> {
                    if (country.getTech().getDip() > getTech().getDip()) {
                        double mult = Math.max(this.save.getGame().getSpyNetworkTechEffectMax(),
                                               (country.getTech().getDip() - getTech().getDip()) * this.save.getGame().getSpyNetworkTechEffect());
                        list.add(mult * NumbersUtils.intOrDefault(getActiveRelation(country).getSpyNetwork()));
                    }
                });
            }
        } else if ("MIL_TECH_COST_MODIFIER".equalsIgnoreCase(modifier.getName())) {
            if (getIdeaGroups() != null) {
                list.add(getIdeaGroups().getIdeaGroups()
                                        .entrySet()
                                        .stream()
                                        .filter(entry -> Power.MIL.equals(entry.getKey().getCategory()))
                                        .mapToInt(Map.Entry::getValue)
                                        .sum() * this.save.getGame().getIdeaToTech());
            }

            if (CollectionUtils.isNotEmpty(getOurSpyNetwork())) {
                getOurSpyNetwork().stream().max(Comparator.comparing(o -> o.getTech().getMil())).ifPresent(country -> {
                    if (country.getTech().getMil() > getTech().getMil()) {
                        double mult = Math.max(this.save.getGame().getSpyNetworkTechEffectMax(),
                                               (country.getTech().getMil() - getTech().getMil()) * this.save.getGame().getSpyNetworkTechEffect());
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
                                     .sum() / getNavalForceLimit()) * (1 + getModifier(ModifiersUtils.getModifier("naval_tradition_from_trade"))));
        }

        if (getChurch() != null && CollectionUtils.isNotEmpty(getChurch().getAspects())) {
            list.addAll(getChurch().getAspects()
                                   .stream()
                                   .filter(churchAspect -> churchAspect.getModifiers().hasModifier(modifier))
                                   .map(churchAspect -> churchAspect.getModifiers().getModifier(modifier))
                                   .toList());
        }

        list.add(getTech().getModifier(Power.ADM, getTech().getAdm(), modifier));
        list.add(getTech().getModifier(Power.DIP, getTech().getDip(), modifier));
        list.add(getTech().getModifier(Power.MIL, getTech().getMil(), modifier));

        Monarch mon;
        if ((mon = getMonarch()) != null && mon.getPersonalities() != null && CollectionUtils.isNotEmpty(mon.getPersonalities().getPersonalities())) {
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

        if (getCapital() != null && getCapital().inHre() && !this.save.getHre().dismantled()) {
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

        if (getFervor() != null) {
            list.addAll(getFervor().getActives()
                                   .stream()
                                   .map(Fervor::getModifiers)
                                   .filter(Objects::nonNull)
                                   .filter(m -> m.hasModifier(modifier))
                                   .map(m -> m.getModifier(modifier))
                                   .toList());

            if (modifier.getName().equalsIgnoreCase("MONTHLY_FERVOR_INCREASE")) {
                list.add(ModifiersUtils.scaleWithActivesFervor(this, new Modifiers(new HashSet<>(),
                                                                                   Map.of(ModifiersUtils.getModifier("MONTHLY_FERVOR_INCREASE"), -5d)))
                                       .getModifier(modifier));
            }
        }

        if (getNativeAdvancements() != null) {
            list.addAll(getNativeAdvancements().getNativeAdvancements()
                                               .values()
                                               .stream()
                                               .map(SaveNativeAdvancement::getEmbracedNativeAdvancements)
                                               .flatMap(Collection::stream)
                                               .filter(Objects::nonNull)
                                               .map(NativeAdvancement::getModifiers)
                                               .filter(Objects::nonNull)
                                               .filter(m -> m.hasModifier(modifier))
                                               .map(m -> m.getModifier(modifier))
                                               .toList());
        }

        if (getNavalDoctrine() != null && getNavalDoctrine().getModifiers().hasModifier(modifier)) {
            list.add(getNavalDoctrine().getModifiers().getModifier(modifier));
        }

        if (getPersonalDeity() != null && getPersonalDeity().getModifiers().hasModifier(modifier)) {
            list.add(getPersonalDeity().getModifiers().getModifier(modifier));
        }

        if (getReligiousReforms() != null) {
            list.addAll(getReligiousReforms().getAdoptedReforms()
                                             .stream()
                                             .map(ReligiousReform::getModifiers)
                                             .filter(Objects::nonNull)
                                             .filter(m -> m.hasModifier(modifier))
                                             .map(m -> m.getModifier(modifier))
                                             .toList());
        }

        list.addAll(this.save.getTradeNodes()
                             .values()
                             .stream()
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

        if (MapUtils.isNotEmpty(getAdvisors())) {
            list.addAll(getAdvisors().values()
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
            list.addAll(getOwnedProvinces().stream().map(province -> province.getModifier(modifier)).toList());
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

        if (getFactions() != null) {
            getFactions().stream().max(Comparator.comparing(SaveFaction::getInfluence)).ifPresent(faction -> {
                if (faction.getType().getModifiers().hasModifier(modifier)) {
                    list.add(faction.getType().getModifiers().getModifier(modifier));
                }
            });
        }

        if (getGovernment() != null) {
            list.addAll(getGovernment().getReforms()
                                       .stream()
                                       .map(GovernmentReform::getModifiers)
                                       .filter(Objects::nonNull)
                                       .filter(m -> m.hasModifier(modifier))
                                       .map(m -> m.getModifier(modifier))
                                       .toList());
        }

        if (isStatistsInPower()) {
            list.addAll(getGovernment().getReforms()
                                       .stream()
                                       .filter(reform -> reform.getStatesGeneralMechanic() != null && (reform.getStatesGeneralMechanic().getValue() == null
                                                                                                       || reform.getStatesGeneralMechanic()
                                                                                                                .getValue()
                                                                                                                .apply(this, this)))
                                       .map(GovernmentReform::getStatesGeneralMechanic)
                                       .map(Pair::getKey)
                                       .filter(Objects::nonNull)
                                       .map(m -> m.get(0))
                                       .filter(m -> m.hasModifier(modifier))
                                       .map(m -> m.getModifier(modifier))
                                       .toList());
        } else if (isMonarchistsInPower()) {
            list.addAll(getGovernment().getReforms()
                                       .stream()
                                       .filter(reform -> reform.getStatesGeneralMechanic() != null && (reform.getStatesGeneralMechanic().getValue() == null
                                                                                                       || reform.getStatesGeneralMechanic()
                                                                                                                .getValue()
                                                                                                                .apply(this, this)))
                                       .map(GovernmentReform::getStatesGeneralMechanic)
                                       .map(Pair::getKey)
                                       .filter(Objects::nonNull)
                                       .map(m -> m.get(1))
                                       .filter(m -> m.hasModifier(modifier))
                                       .map(m -> m.getModifier(modifier))
                                       .toList());
        }

        if (getGovernmentRank() != null && getGovernmentRank().getModifiers().hasModifier(modifier)) {
            list.add(getGovernmentRank().getModifiers().getModifier(modifier));
        }

        if (getHegemon() != null && getHegemon().getModifiers().hasModifier(modifier)) {
            list.add(getHegemon().getModifiers().getModifier(modifier));
        }

        if (getReligion() != null && getReligion().getGameReligion().getCountry() != null && getReligion().getGameReligion()
                                                                                                          .getCountry()
                                                                                                          .hasModifier(modifier)) {
            list.add(getReligion().getGameReligion().getCountry().getModifier(modifier));
        }

        if (getSecondaryReligion() != null && getSecondaryReligion().getGameReligion().getCountryAsSecondary() != null
            && getSecondaryReligion().getGameReligion().getCountryAsSecondary().hasModifier(modifier)) {
            list.add(getReligion().getGameReligion().getCountryAsSecondary().getModifier(modifier));
        }

        SavePapacy papacy;
        if (getReligion() != null && (papacy = getReligion().getPapacy()) != null && BooleanUtils.toBoolean(papacy.getPapacyActive())) {
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

        if (CollectionUtils.isNotEmpty(getSubjects())) {
            getSubjects().stream()
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
                list.add(getSubjects().stream()
                                      .filter(subject -> subject.getSubjectType().getForcelimitToOverlord() != 0)
                                      .filter(subject -> !subject.isColony() || subject.getOwnedProvinces().stream().filter(SaveProvince::isCity).count() >= 10)
                                      .mapToDouble(subject -> ((int) subject.getLandForceLimit()) * subject.getSubjectType().getForcelimitToOverlord())
                                      .sum());
            }
        }

        if (CollectionUtils.isNotEmpty(getEstates())) {
            list.addAll(getEstates().stream().map(estate -> estate.getModifiers(modifier)).toList());
        }

        if (getOverlord() != null) {
            this.save.getDiplomacy()
                     .getDependencies()
                     .stream()
                     .filter(dependency -> this.equals(dependency.getSecond()) && getOverlord().equals(dependency.getSecond()))
                     .map(Dependency::getSubjectTypeUpgrades)
                     .filter(Objects::nonNull)
                     .flatMap(Collection::stream)
                     .forEach(upgrade -> list.add(upgrade.getModifiersSubject().getModifier(modifier)));
        }

        if (getCrownLandBonus() != null && getCrownLandBonus().getModifiers().hasModifier(modifier)) {
            list.add(getCrownLandBonus().getModifiers().getModifier(modifier));
        }

        return ModifiersUtils.sumModifiers(modifier, list);
    }

    private void refreshAttributes() {
        this.governmentName = this.item.getVarAsString("government_name")
                                       .filter(StringUtils::isNotBlank)
                                       .map(s -> this.save.getGame().getGovernmentName(s))
                                       .orElse(null);
        this.fervor = this.item.getChild("fervor").map(i -> new SaveFervor(i, this.save.getGame())).orElse(null);
        this.playerAiPrefsCommand = this.item.getChild("player_ai_prefs_command").map(PlayerAiPrefsCommand::new).orElse(null);
        this.cooldowns = this.item.getChild("cooldowns").map(ListOfDates::new).orElse(null);
        this.history = this.item.getChild("history").map(i -> new SaveCountryHistory(i, this)).orElse(null);
        this.flags = this.item.getChild("flags").map(ListOfDates::new).orElse(null);
        this.hiddenFlags = this.item.getChild("hidden_flags").map(ListOfDates::new).orElse(null);
        this.variables = this.item.getChild("variables").map(ListOfDoubles::new).orElse(null);
        this.colors = this.item.getChild("colors").map(Colors::new).orElse(null);
        this.tech = this.item.getChild("technology").map(i -> new CountryTechnology(this.save, i)).orElse(null);
        this.estates = this.item.getChildren("estate").stream().map(i -> new SaveEstate(i, this)).toList();
        this.activeAgenda = this.item.getChild("active_agenda").map(i -> new ActiveAgenda(i, this)).orElse(null);
        this.interactionsLastUsed = this.item.getChild("interactions_last_used")
                                             .map(ClausewitzItem::getLists)
                                             .stream()
                                             .flatMap(Collection::stream)
                                             .map(list -> new EstateInteraction(this.save.getGame(), list))
                                             .toList();
        this.factions = this.item.getChildren("faction").stream().map(child -> new SaveFaction(child, this.save.getGame())).toList();
        this.rivals = this.item.getChildren("rival").stream()
                               .map(child -> new Rival(child, this.save))
                               .collect(Collectors.toMap(rival -> ClausewitzUtils.removeQuotes(rival.getRivalTag().get()), Function.identity()));
        this.victoryCards = this.item.getChildren("victory_card").stream().map(VictoryCard::new).toList();
        this.activePolicies = this.item.getChildren("active_policy").stream().map(child -> new ActivePolicy(child, this.save.getGame())).toList();
        this.powerProjections = this.item.getChildren("power_projection").stream().map(PowerProjection::new).toList();
        this.parliament = this.item.getChild("parliament").map(i -> new Parliament(i, this.save.getGame())).orElse(null);
        this.ledger = this.item.getChild("ledger").map(Ledger::new).orElse(null);
        this.loans = this.item.getChildren("loan").stream().map(Loan::new).toList();
        this.church = this.item.getChild("church").map(i -> new Church(i, this.save.getGame())).orElse(null);
        this.ideaGroups = this.item.getChild("active_idea_groups").map(i -> new IdeaGroups(i, this.save)).orElse(null);
        this.religiousReforms = this.item.getChild("active_religious_reform").map(i -> new SaveReligiousReforms(i, this.save.getGame())).orElse(null);
        this.nativeAdvancements = this.item.getChild("active_native_advancement").map(i -> new SaveNativeAdvancements(i, this.save.getGame())).orElse(null);
        this.government = this.item.getChild("government").map(i -> new SaveGovernment(i, this.save.getGame(), this)).orElse(null);
        this.colonists = this.item.getChild("colonists").map(i -> i.getChildren("envoy")).stream().flatMap(Collection::stream).map(Envoy::new).toList();
        this.merchants = this.item.getChild("merchants").map(i -> i.getChildren("envoy")).stream().flatMap(Collection::stream).map(Envoy::new).toList();
        this.missionaries = this.item.getChild("missionaries").map(i -> i.getChildren("envoy")).stream().flatMap(Collection::stream).map(Envoy::new).toList();
        this.diplomats = this.item.getChild("diplomats").map(i -> i.getChildren("envoy")).stream().flatMap(Collection::stream).map(Envoy::new).toList();
        this.modifiers = this.item.getChildren("modifier").stream().map(child -> new SaveModifier(child, this.save.getGame())).toList();
        this.subUnit = this.item.getChild("sub_unit").map(SubUnit::new).orElse(null);
        this.armies = this.item.getChildren("army")
                               .stream()
                               .map(armyItem -> new Army(armyItem, this))
                               .collect(Collectors.toMap(a -> a.getId().get(), Function.identity()));
        this.navies = this.item.getChildren("navy")
                               .stream()
                               .map(navyItem -> new Navy(navyItem, this))
                               .collect(Collectors.toMap(a -> a.getId().get(), Function.identity()));
        this.mercenaryCompanies = this.item.getChildren("mercenary_company").stream()
                                           .map(armyItem -> new MercenaryCompany(armyItem, this))
                                           .collect(Collectors.toMap(army -> army.getId().get().getId(), Function.identity()));
        this.activeRelations = this.item.getChild("active_relations")
                                        .map(ClausewitzItem::getChildren)
                                        .stream()
                                        .flatMap(Collection::stream)
                                        .map(child -> new ActiveRelation(child, this.save))
                                        .collect(Collectors.toMap(ActiveRelation::getCountryTag, Function.identity()));
        this.previousMonarchs = this.item.getChildren("previous_monarch").stream().map(Id::new).toList();
        this.advisorsIds = this.item.getChildren("advisor").stream().map(Id::new).toList();
        this.admPowerSpent = this.item.getChild("adm_spent_indexed").map(PowerSpentIndexed::new).orElse(null);
        this.dipPowerSpent = this.item.getChild("dip_spent_indexed").map(PowerSpentIndexed::new).orElse(null);
        this.milPowerSpent = this.item.getChild("mil_spent_indexed").map(PowerSpentIndexed::new).orElse(null);
        this.historyStatsCache = this.item.getChild("historic_stats_cache").map(HistoryStatsCache::new).orElse(null);
        this.customNationalIdeas = this.item.getChild("custom_national_ideas")
                                            .map(ClausewitzItem::getChildren)
                                            .stream()
                                            .flatMap(Collection::stream)
                                            .map(CustomNationalIdea::new)
                                            .toList();
        this.countryMissions = this.item.getChild("country_missions").map(i -> new Missions(i, this.save.getGame())).orElse(null);

        Double powerProjection = null;

        for (PowerProjection projection : this.powerProjections) {
            if (projection.getCurrent().filter(aDouble -> 0d != aDouble).isPresent()) {
                double value = BigDecimal.valueOf(projection.getCurrent().get()).setScale(0, RoundingMode.HALF_UP).doubleValue();
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

            if (!Objects.equals(powerProjection, getCurrentPowerProjection())) {
                setCurrentPowerProjection(powerProjection);
            }
        }

        if (this.history != null) {
            if (this.history.getMonarchs() != null) {
                this.item.getChild("monarch").ifPresent(i -> {
                    Id id = new Id(i);
                    this.monarch = this.history.getMonarch(id.getId());
                });
            }

            if (this.history.getHeirs() != null) {
                this.item.getChild("heir").ifPresent(i -> {
                    Id id = new Id(i);
                    this.monarch = this.history.getHeir(id.getId());
                });
            }

            if (this.history.getQueens() != null) {
                this.item.getChild("queen").ifPresent(i -> {
                    Id id = new Id(i);
                    this.monarch = this.history.getQueen(id.getId());
                });
            }


            if (this.history.getLeaders() != null) {
                this.leaders = new HashMap<>();
                List<ClausewitzItem> leadersItems = this.item.getChildren("leader");
                if (!leadersItems.isEmpty()) {
                    leadersItems.forEach(leaderItem -> {
                        Id leaderId = new Id(leaderItem);

                        if (this.history.getLeader(leaderId.getId()) != null) {
                            this.leaders.put(leaderId.getId(), this.history.getLeader(leaderId.getId()));
                        }
                    });
                }
            }
        }
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
