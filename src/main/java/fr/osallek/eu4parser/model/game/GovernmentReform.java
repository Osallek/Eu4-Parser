package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.clausewitzparser.model.ClausewitzObject;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class GovernmentReform {

    private Game game;

    private ClausewitzItem item;

    private Map<String, String> customAttributes;

    private List<String> governmentAbilities;

    private Pair<String, ConditionAnd> icon;

    private Pair<String, ConditionAnd> legacyEquivalent;

    private Pair<Boolean, ConditionAnd> basicReform;

    private Pair<Boolean, ConditionAnd> legacyGovernment;

    private Pair<Boolean, ConditionAnd> lockLevelWhenSelected;

    private Pair<Boolean, ConditionAnd> validForNewCountry;

    private Pair<Boolean, ConditionAnd> allowConvert;

    private Pair<Boolean, ConditionAnd> rulersCanBeGenerals;

    private Pair<Boolean, ConditionAnd> heirsCanBeGenerals;

    private Pair<Integer, ConditionAnd> fixedRank;

    private Pair<Boolean, ConditionAnd> republicanName;

    private Pair<Boolean, ConditionAnd> militarisedSociety;

    private Pair<Boolean, ConditionAnd> claimStates;

    private Pair<Boolean, ConditionAnd> religion;

    private Pair<Boolean, ConditionAnd> republic;

    private Pair<Boolean, ConditionAnd> dictatorship;

    private Pair<Boolean, ConditionAnd> isElective;

    private Pair<Boolean, ConditionAnd> queen;

    private Pair<Boolean, ConditionAnd> heir;

    private Pair<Boolean, ConditionAnd> hasParliament;

    private Pair<Boolean, ConditionAnd> hasDevotion;

    private Pair<Boolean, ConditionAnd> hasMeritocracy;

    private Pair<Boolean, ConditionAnd> allowForceTributary;

    private Pair<Integer, ConditionAnd> duration;

    private Pair<Boolean, ConditionAnd> electionOnDeath;

    private Pair<Boolean, ConditionAnd> monarchy;

    private Pair<Boolean, ConditionAnd> tribal;

    private Pair<Integer, ConditionAnd> differentReligionAcceptance;

    private Pair<Integer, ConditionAnd> differentReligionGroupAcceptance;

    private Pair<Boolean, ConditionAnd> boostIncome;

    private Pair<Boolean, ConditionAnd> monastic;

    private Pair<Boolean, ConditionAnd> canUseTradePost;

    private Pair<Boolean, ConditionAnd> nativeMechanic;

    private Pair<Boolean, ConditionAnd> canFormTradeLeague;

    private Pair<Boolean, ConditionAnd> freeCity;

    private Pair<Boolean, ConditionAnd> isTradingCity;

    private Pair<String, ConditionAnd> tradeCityReform;

    private Pair<Boolean, ConditionAnd> maintainDynasty;

    private Pair<Boolean, ConditionAnd> allowMigration;

    private Pair<Integer, ConditionAnd> nationDesignerCost;

    private Pair<Boolean, ConditionAnd> papacy;

    private Pair<Boolean, ConditionAnd> hasHarem;

    private Pair<Boolean, ConditionAnd> hasPashas;

    private Pair<Boolean, ConditionAnd> allowVassalWar;

    private Pair<Boolean, ConditionAnd> allowVassalAlliance;

    private Pair<Double, ConditionAnd> minAutonomy;

    private Pair<Integer, ConditionAnd> factionsFrame;

    private Pair<List<String>, ConditionAnd> factions;

    private Pair<Boolean, ConditionAnd> foreignSlaveRulers;

    private Pair<Boolean, ConditionAnd> royalMarriage;

    private Pair<Boolean, ConditionAnd> nomad;

    private Pair<Map<String, Modifiers>, ConditionAnd> assimilationCultures;

    private Pair<List<Modifiers>, ConditionAnd> statesGeneralMechanic;

    private Pair<Boolean, ConditionAnd> validForNationDesigner;

    private Pair<Boolean, ConditionAnd> allowNormalConversion;

    private Pair<Double, ConditionAnd> startTerritoryToEstates;

    private Pair<Boolean, ConditionAnd> hasTermElection;

    private Pair<Boolean, ConditionAnd> forceAdmiralLeader;

    private Pair<Boolean, ConditionAnd> admiralsBecomeRulers;

    private Pair<Boolean, ConditionAnd> generalsBecomeRulers;

    private Pair<Boolean, ConditionAnd> allowBanners;

    private Pair<Boolean, ConditionAnd> usesRevolutionaryZeal;

    private Pair<Boolean, ConditionAnd> revolutionary;

    private Pair<Boolean, ConditionAnd> revolutionaryClientState;

    private Pair<List<TradeGood>, ConditionAnd> disallowedTradeGoods;

    public GovernmentReform(ClausewitzItem item, Game game, GovernmentReform defaultGovernmentReform) {
        this(defaultGovernmentReform);
        this.game = game;
        this.item = item;

        ClausewitzItem child = item.getChild("custom_attributes");
        this.customAttributes = child == null ? null : child.getVariables()
                                                            .stream()
                                                            .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                                                      ClausewitzVariable::getValue,
                                                                                      (a, b) -> b,
                                                                                      LinkedHashMap::new));

        ClausewitzList list = item.getList("government_abilities");
        this.governmentAbilities = list == null ? null : list.getValues();

        readAttributes(item);

        List<ClausewitzItem> children = item.getChildren("conditional");
        children.forEach(this::readAttributes);
    }

    public GovernmentReform(GovernmentReform other) {
        if (other == null) {
            return;
        }

        this.icon = other.icon;
        this.validForNewCountry = other.validForNewCountry;
        this.allowConvert = other.allowConvert;
        this.rulersCanBeGenerals = other.rulersCanBeGenerals;
        this.heirsCanBeGenerals = other.heirsCanBeGenerals;
        this.fixedRank = other.fixedRank;
        this.republicanName = other.republicanName;
        this.militarisedSociety = other.militarisedSociety;
        this.governmentAbilities = other.governmentAbilities;
        this.claimStates = other.claimStates;
        this.religion = other.religion;
        this.republic = other.republic;
        this.dictatorship = other.dictatorship;
        this.isElective = other.isElective;
        this.queen = other.queen;
        this.heir = other.heir;
        this.hasParliament = other.hasParliament;
        this.hasDevotion = other.hasDevotion;
        this.hasMeritocracy = other.hasMeritocracy;
        this.allowForceTributary = other.allowForceTributary;
        this.duration = other.duration;
        this.electionOnDeath = other.electionOnDeath;
        this.monarchy = other.monarchy;
        this.tribal = other.tribal;
        this.differentReligionAcceptance = other.differentReligionAcceptance;
        this.differentReligionGroupAcceptance = other.differentReligionGroupAcceptance;
        this.boostIncome = other.boostIncome;
        this.monastic = other.monastic;
        this.canUseTradePost = other.canUseTradePost;
        this.nativeMechanic = other.nativeMechanic;
        this.canFormTradeLeague = other.canFormTradeLeague;
        this.freeCity = other.freeCity;
        this.isTradingCity = other.isTradingCity;
        this.tradeCityReform = other.tradeCityReform;
        this.maintainDynasty = other.maintainDynasty;
        this.allowMigration = other.allowMigration;
        this.nationDesignerCost = other.nationDesignerCost;
        this.papacy = other.papacy;
        this.hasHarem = other.hasHarem;
        this.hasPashas = other.hasPashas;
        this.allowVassalWar = other.allowVassalWar;
        this.allowVassalAlliance = other.allowVassalAlliance;
        this.minAutonomy = other.minAutonomy;
        this.factionsFrame = other.factionsFrame;
        this.factions = other.factions;
        this.foreignSlaveRulers = other.foreignSlaveRulers;
        this.royalMarriage = other.royalMarriage;
        this.nomad = other.nomad;
        this.assimilationCultures = other.assimilationCultures;
        this.statesGeneralMechanic = other.statesGeneralMechanic;
        this.validForNationDesigner = other.validForNationDesigner;
        this.allowNormalConversion = other.allowNormalConversion;
        this.startTerritoryToEstates = other.startTerritoryToEstates;
        this.hasTermElection = other.hasTermElection;
        this.forceAdmiralLeader = other.forceAdmiralLeader;
        this.admiralsBecomeRulers = other.admiralsBecomeRulers;
        this.generalsBecomeRulers = other.generalsBecomeRulers;
        this.allowBanners = other.allowBanners;
        this.usesRevolutionaryZeal = other.usesRevolutionaryZeal;
        this.revolutionary = other.revolutionary;
        this.revolutionaryClientState = other.revolutionaryClientState;
        this.disallowedTradeGoods = other.disallowedTradeGoods;
    }

    private void readAttributes(ClausewitzItem item) {
        ClausewitzItem child = item.getChild("allow");
        ConditionAnd condition = child == null ? null : new ConditionAnd(item.getChild("allow"));

        Boolean aBoolean;
        Integer integer;
        Double aDouble;
        String s;

        ClausewitzList list = item.getList("disallowed_trade_goods");
        this.disallowedTradeGoods = list == null ? this.disallowedTradeGoods
                                                 : Pair.of(list.getValues().stream().map(game::getTradeGood).toList(), condition);

        list = item.getList("factions");
        this.factions = list == null ? this.factions : Pair.of(list.getValues(), condition);

        child = item.getChild("assimilation_cultures");
        this.assimilationCultures = child == null ? null : Pair.of(child.getChildren()
                                                                        .stream()
                                                                        .collect(Collectors.toMap(ClausewitzObject::getName, Modifiers::new)), condition);
        child = item.getChild("states_general_mechanic");
        this.statesGeneralMechanic = child == null ? null : Pair.of(child.getChildren()
                                                                         .stream()
                                                                         .map(Modifiers::new)
                                                                         .toList(), condition);

        this.basicReform = Pair.of(BooleanUtils.toBoolean(item.getVarAsBool("basic_reform")), condition);
        this.legacyGovernment = Pair.of(BooleanUtils.toBoolean(item.getVarAsBool("legacy_government")), condition);
        this.legacyEquivalent = Pair.of(item.getVarAsString("legacy_equivalent"), condition);
        this.lockLevelWhenSelected = Pair.of(BooleanUtils.toBoolean(item.getVarAsBool("lock_level_when_selected")), condition);
        this.icon = StringUtils.isBlank(s = item.getVarAsString("icon")) ? this.icon : Pair.of(s, condition);
        this.validForNewCountry = (aBoolean = item.getVarAsBool("valid_for_new_country")) == null ? this.validForNewCountry : Pair.of(aBoolean, condition);
        this.allowConvert = (aBoolean = item.getVarAsBool("allow_convert")) == null ? this.allowConvert : Pair.of(aBoolean, condition);
        this.rulersCanBeGenerals = (aBoolean = item.getVarAsBool("rulers_can_be_generals")) == null ? this.rulersCanBeGenerals : Pair.of(aBoolean, condition);
        this.heirsCanBeGenerals = (aBoolean = item.getVarAsBool("heirs_can_be_generals")) == null ? this.heirsCanBeGenerals : Pair.of(aBoolean, condition);
        this.fixedRank = (integer = item.getVarAsInt("fixed_rank")) == null ? this.fixedRank : Pair.of(integer, condition);
        this.republicanName = (aBoolean = item.getVarAsBool("republican_name")) == null ? this.republicanName : Pair.of(aBoolean, condition);
        this.militarisedSociety = (aBoolean = item.getVarAsBool("militarised_society")) == null ? this.militarisedSociety : Pair.of(aBoolean, condition);
        this.claimStates = (aBoolean = item.getVarAsBool("claim_states")) == null ? this.claimStates : Pair.of(aBoolean, condition);
        this.religion = (aBoolean = item.getVarAsBool("religion")) == null ? this.religion : Pair.of(aBoolean, condition);
        this.republic = (aBoolean = item.getVarAsBool("republic")) == null ? this.republic : Pair.of(aBoolean, condition);
        this.dictatorship = (aBoolean = item.getVarAsBool("dictatorship")) == null ? this.dictatorship : Pair.of(aBoolean, condition);
        this.isElective = (aBoolean = item.getVarAsBool("is_elective")) == null ? this.isElective : Pair.of(aBoolean, condition);
        this.queen = (aBoolean = item.getVarAsBool("queen")) == null ? this.queen : Pair.of(aBoolean, condition);
        this.heir = (aBoolean = item.getVarAsBool("heir")) == null ? this.heir : Pair.of(aBoolean, condition);
        this.hasParliament = (aBoolean = item.getVarAsBool("has_parliament")) == null ? this.hasParliament : Pair.of(aBoolean, condition);
        this.hasDevotion = (aBoolean = item.getVarAsBool("has_devotion")) == null ? this.hasDevotion : Pair.of(aBoolean, condition);
        this.hasMeritocracy = (aBoolean = item.getVarAsBool("has_meritocracy")) == null ? this.hasMeritocracy : Pair.of(aBoolean, condition);
        this.allowForceTributary = (aBoolean = item.getVarAsBool("allow_force_tributary")) == null ? this.allowForceTributary : Pair.of(aBoolean, condition);
        this.duration = (integer = item.getVarAsInt("duration")) == null ? this.duration : Pair.of(integer, condition);
        this.electionOnDeath = (aBoolean = item.getVarAsBool("election_on_death")) == null ? this.electionOnDeath : Pair.of(aBoolean, condition);
        this.monarchy = (aBoolean = item.getVarAsBool("monarchy")) == null ? this.monarchy : Pair.of(aBoolean, condition);
        this.tribal = (aBoolean = item.getVarAsBool("tribal")) == null ? this.tribal : Pair.of(aBoolean, condition);
        this.differentReligionAcceptance = (integer = item.getVarAsInt("different_religion_acceptance")) == null ? this.differentReligionAcceptance
                                                                                                                 : Pair.of(integer, condition);
        this.differentReligionGroupAcceptance = (integer = item.getVarAsInt("different_religion_group_acceptance")) == null
                                                ? this.differentReligionGroupAcceptance : Pair.of(integer, condition);
        this.boostIncome = (aBoolean = item.getVarAsBool("boost_income")) == null ? this.boostIncome : Pair.of(aBoolean, condition);
        this.monastic = (aBoolean = item.getVarAsBool("monastic")) == null ? this.monastic : Pair.of(aBoolean, condition);
        this.canUseTradePost = (aBoolean = item.getVarAsBool("can_use_trade_post")) == null ? this.canUseTradePost : Pair.of(aBoolean, condition);
        this.nativeMechanic = (aBoolean = item.getVarAsBool("native_mechanic")) == null ? this.nativeMechanic : Pair.of(aBoolean, condition);
        this.canFormTradeLeague = (aBoolean = item.getVarAsBool("can_form_trade_league")) == null ? this.canFormTradeLeague : Pair.of(aBoolean, condition);
        this.freeCity = (aBoolean = item.getVarAsBool("free_city")) == null ? this.freeCity : Pair.of(aBoolean, condition);
        this.isTradingCity = (aBoolean = item.getVarAsBool("is_trading_city")) == null ? this.isTradingCity : Pair.of(aBoolean, condition);
        this.tradeCityReform = StringUtils.isBlank(s = item.getVarAsString("trade_city_reform")) ? this.tradeCityReform : Pair.of(s, condition);
        this.maintainDynasty = (aBoolean = item.getVarAsBool("maintain_dynasty")) == null ? this.maintainDynasty : Pair.of(aBoolean, condition);
        this.allowMigration = (aBoolean = item.getVarAsBool("allow_migration")) == null ? this.allowMigration : Pair.of(aBoolean, condition);
        this.nationDesignerCost = (integer = item.getVarAsInt("nation_designer_cost")) == null ? this.nationDesignerCost : Pair.of(integer, condition);
        this.papacy = (aBoolean = item.getVarAsBool("papacy")) == null ? this.papacy : Pair.of(aBoolean, condition);
        this.hasHarem = (aBoolean = item.getVarAsBool("has_harem")) == null ? this.hasHarem : Pair.of(aBoolean, condition);
        this.hasPashas = (aBoolean = item.getVarAsBool("has_pashas")) == null ? this.hasPashas : Pair.of(aBoolean, condition);
        this.allowVassalWar = (aBoolean = item.getVarAsBool("allow_vassal_war")) == null ? this.allowVassalWar : Pair.of(aBoolean, condition);
        this.allowVassalAlliance = (aBoolean = item.getVarAsBool("allow_vassal_alliance")) == null ? this.allowVassalAlliance : Pair.of(aBoolean, condition);
        this.minAutonomy = (aDouble = item.getVarAsDouble("min_autonomy")) == null ? this.minAutonomy : Pair.of(aDouble, condition);
        this.factionsFrame = (integer = item.getVarAsInt("factions_frame")) == null ? this.factionsFrame : Pair.of(integer, condition);
        this.foreignSlaveRulers = (aBoolean = item.getVarAsBool("foreign_slave_rulers")) == null ? this.foreignSlaveRulers : Pair.of(aBoolean, condition);
        this.royalMarriage = (aBoolean = item.getVarAsBool("royal_marriage")) == null ? this.royalMarriage : Pair.of(aBoolean, condition);
        this.nomad = (aBoolean = item.getVarAsBool("nomad")) == null ? this.nomad : Pair.of(aBoolean, condition);
        this.validForNationDesigner = (aBoolean = item.getVarAsBool("valid_for_nation_designer")) == null ? this.validForNationDesigner
                                                                                                          : Pair.of(aBoolean, condition);
        this.allowNormalConversion = (aBoolean = item.getVarAsBool("allow_normal_conversion")) == null ? this.allowNormalConversion
                                                                                                       : Pair.of(aBoolean, condition);
        this.startTerritoryToEstates = (aDouble = item.getVarAsDouble("start_territory_to_estates")) == null ? this.startTerritoryToEstates
                                                                                                             : Pair.of(aDouble, condition);
        this.hasTermElection = (aBoolean = item.getVarAsBool("has_term_election")) == null ? this.hasTermElection : Pair.of(aBoolean, condition);
        this.forceAdmiralLeader = (aBoolean = item.getVarAsBool("force_admiral_leader")) == null ? this.forceAdmiralLeader : Pair.of(aBoolean, condition);
        this.admiralsBecomeRulers = (aBoolean = item.getVarAsBool("admirals_become_rulers")) == null ? this.admiralsBecomeRulers : Pair.of(aBoolean, condition);
        this.generalsBecomeRulers = (aBoolean = item.getVarAsBool("generals_become_rulers")) == null ? this.generalsBecomeRulers : Pair.of(aBoolean, condition);
        this.allowBanners = (aBoolean = item.getVarAsBool("allow_banners")) == null ? this.allowBanners : Pair.of(aBoolean, condition);
        this.usesRevolutionaryZeal = (aBoolean = item.getVarAsBool("uses_revolutionary_zeal")) == null ? this.usesRevolutionaryZeal
                                                                                                       : Pair.of(aBoolean, condition);
        this.revolutionary = (aBoolean = item.getVarAsBool("revolutionary")) == null ? this.revolutionary : Pair.of(aBoolean, condition);
        this.revolutionaryClientState = (aBoolean = item.getVarAsBool("revolutionary_client_state")) == null ? this.revolutionaryClientState
                                                                                                             : Pair.of(aBoolean, condition);
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public Modifiers getModifiers() {
        return new Modifiers(this.item.getChild("modifiers"));
    }

    public Map<String, String> getCustomAttributes() {
        return customAttributes;
    }

    public ConditionAnd getPotential() {
        ClausewitzItem child = this.item.getChild("potential");
        return child == null ? null : new ConditionAnd(child);
    }

    public Pair<Boolean, ConditionAnd> isBasicReform() {
        return basicReform;
    }

    public Pair<Boolean, ConditionAnd> isLegacyGovernment() {
        return legacyGovernment;
    }

    public Pair<String, ConditionAnd> getIcon() {
        return icon;
    }

    public File getImageFile() {
        return this.game.getSpriteTypeImageFile("government_reform_" + ClausewitzUtils.removeQuotes(this.icon.getKey()));
    }

    public Pair<GovernmentReform, ConditionAnd> getLegacyEquivalent() {
        return Pair.of(this.game.getGovernmentReform(this.legacyEquivalent.getKey()), this.legacyEquivalent.getRight());
    }

    public Pair<Boolean, ConditionAnd> isLockLevelWhenSelected() {
        return lockLevelWhenSelected;
    }

    public Pair<Boolean, ConditionAnd> isValidForNewCountry() {
        return validForNewCountry;
    }

    public Pair<Boolean, ConditionAnd> isAllowConvert() {
        return allowConvert;
    }

    public Pair<Boolean, ConditionAnd> isRulersCanBeGenerals() {
        return rulersCanBeGenerals;
    }

    public Pair<Boolean, ConditionAnd> isHeirsCanBeGenerals() {
        return heirsCanBeGenerals;
    }

    public Pair<Integer, ConditionAnd> getFixedRank() {
        return fixedRank;
    }

    public Pair<Boolean, ConditionAnd> isRepublicanName() {
        return republicanName;
    }

    public Pair<Boolean, ConditionAnd> isMilitarisedSociety() {
        return militarisedSociety;
    }

    public List<String> getGovernmentAbilities() {
        return governmentAbilities;
    }

    public Pair<Boolean, ConditionAnd> isClaimStates() {
        return claimStates;
    }

    public Pair<Boolean, ConditionAnd> isReligion() {
        return religion;
    }

    public Pair<Boolean, ConditionAnd> isRepublic() {
        return republic;
    }

    public Pair<Boolean, ConditionAnd> isDictatorship() {
        return dictatorship;
    }

    public Pair<Boolean, ConditionAnd> isElective() {
        return isElective;
    }

    public Pair<Boolean, ConditionAnd> isQueen() {
        return queen;
    }

    public Pair<Boolean, ConditionAnd> isHeir() {
        return heir;
    }

    public Pair<Boolean, ConditionAnd> isHasParliament() {
        return hasParliament;
    }

    public Pair<Boolean, ConditionAnd> isHasDevotion() {
        return hasDevotion;
    }

    public Pair<Boolean, ConditionAnd> isHasMeritocracy() {
        return hasMeritocracy;
    }

    public Pair<Boolean, ConditionAnd> isAllowForceTributary() {
        return allowForceTributary;
    }

    public Pair<Integer, ConditionAnd> getDuration() {
        return duration;
    }

    public Pair<Boolean, ConditionAnd> isElectionOnDeath() {
        return electionOnDeath;
    }

    public Pair<Boolean, ConditionAnd> isMonarchy() {
        return monarchy;
    }

    public Pair<Boolean, ConditionAnd> isTribal() {
        return tribal;
    }

    public Pair<Integer, ConditionAnd> getDifferentReligionAcceptance() {
        return differentReligionAcceptance;
    }

    public Pair<Integer, ConditionAnd> getDifferentReligionGroupAcceptance() {
        return differentReligionGroupAcceptance;
    }

    public Pair<Boolean, ConditionAnd> isBoostIncome() {
        return boostIncome;
    }

    public Pair<Boolean, ConditionAnd> isMonastic() {
        return monastic;
    }

    public Pair<Boolean, ConditionAnd> isCanUseTradePost() {
        return canUseTradePost;
    }

    public Pair<Boolean, ConditionAnd> isNativeMechanic() {
        return nativeMechanic;
    }

    public Pair<Boolean, ConditionAnd> isCanFormTradeLeague() {
        return canFormTradeLeague;
    }

    public Pair<Boolean, ConditionAnd> isFreeCity() {
        return freeCity;
    }

    public Pair<Boolean, ConditionAnd> isTradingCity() {
        return isTradingCity;
    }

    public Pair<String, ConditionAnd> getTradeCityReform() {
        return tradeCityReform;
    }

    public Pair<Boolean, ConditionAnd> isMaintainDynasty() {
        return maintainDynasty;
    }

    public Pair<Boolean, ConditionAnd> isAllowMigration() {
        return allowMigration;
    }

    public ConditionAnd getNationDesignerTrigger() {
        ClausewitzItem child = this.item.getChild("nation_designer_trigger");
        return child == null ? null : new ConditionAnd(child);
    }

    public Pair<Integer, ConditionAnd> getNationDesignerCost() {
        return nationDesignerCost;
    }

    public Pair<Boolean, ConditionAnd> isPapacy() {
        return papacy;
    }

    public Pair<Boolean, ConditionAnd> isHasHarem() {
        return hasHarem;
    }

    public Pair<Boolean, ConditionAnd> isHasPashas() {
        return hasPashas;
    }

    public Pair<Boolean, ConditionAnd> isAllowVassalWar() {
        return allowVassalWar;
    }

    public Pair<Boolean, ConditionAnd> isAllowVassalAlliance() {
        return allowVassalAlliance;
    }

    public Pair<Double, ConditionAnd> getMinAutonomy() {
        return minAutonomy;
    }

    public Pair<Integer, ConditionAnd> getFactionsFrame() {
        return factionsFrame;
    }

    public Pair<List<String>, ConditionAnd> getFactions() {
        return factions;
    }

    public Pair<Boolean, ConditionAnd> isForeignSlaveRulers() {
        return foreignSlaveRulers;
    }

    public Pair<Boolean, ConditionAnd> isRoyalMarriage() {
        return royalMarriage;
    }

    public Pair<Boolean, ConditionAnd> isNomad() {
        return nomad;
    }

    public Pair<Map<String, Modifiers>, ConditionAnd> getAssimilationCultures() {
        return assimilationCultures;
    }

    public Pair<List<Modifiers>, ConditionAnd> getStatesGeneralMechanic() {
        return statesGeneralMechanic;
    }

    public Pair<Boolean, ConditionAnd> isValidForNationDesigner() {
        return validForNationDesigner;
    }

    public Pair<Boolean, ConditionAnd> isAllowNormalConversion() {
        return allowNormalConversion;
    }

    public Pair<Double, ConditionAnd> getStartTerritoryToEstates() {
        return startTerritoryToEstates;
    }

    public Pair<Boolean, ConditionAnd> isHasTermElection() {
        return hasTermElection;
    }

    public Pair<Boolean, ConditionAnd> isForceAdmiralLeader() {
        return forceAdmiralLeader;
    }

    public Pair<Boolean, ConditionAnd> isAdmiralsBecomeRulers() {
        return admiralsBecomeRulers;
    }

    public Pair<Boolean, ConditionAnd> isGeneralsBecomeRulers() {
        return generalsBecomeRulers;
    }

    public Pair<Boolean, ConditionAnd> isAllowBanners() {
        return allowBanners;
    }

    public Pair<Boolean, ConditionAnd> isUsesRevolutionaryZeal() {
        return usesRevolutionaryZeal;
    }

    public Pair<Boolean, ConditionAnd> isRevolutionary() {
        return revolutionary;
    }

    public Pair<Boolean, ConditionAnd> isRevolutionaryClientState() {
        return revolutionaryClientState;
    }

    public Pair<List<TradeGood>, ConditionAnd> getDisallowedTradeGoods() {
        return disallowedTradeGoods;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof GovernmentReform)) {
            return false;
        }

        GovernmentReform governmentReform = (GovernmentReform) o;

        return Objects.equals(getName(), governmentReform.getName());
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
