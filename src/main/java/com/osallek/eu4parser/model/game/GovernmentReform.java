package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.clausewitzparser.model.ClausewitzObject;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
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

    private String name;

    private String localizedName;

    private Modifiers modifiers;

    private Map<String, String> customAttributes;

    private Condition potential;

    private Condition nationDesignerTrigger;

    private List<String> governmentAbilities;

    private Pair<String, Condition> icon;

    private Pair<String, Condition> legacyEquivalent;

    private Pair<Boolean, Condition> basicReform;

    private Pair<Boolean, Condition> legacyGovernment;

    private Pair<Boolean, Condition> lockLevelWhenSelected;

    private Pair<Boolean, Condition> validForNewCountry;

    private Pair<Boolean, Condition> allowConvert;

    private Pair<Boolean, Condition> rulersCanBeGenerals;

    private Pair<Boolean, Condition> heirsCanBeGenerals;

    private Pair<Integer, Condition> fixedRank;

    private Pair<Boolean, Condition> republicanName;

    private Pair<Boolean, Condition> militarisedSociety;

    private Pair<Boolean, Condition> claimStates;

    private Pair<Boolean, Condition> religion;

    private Pair<Boolean, Condition> republic;

    private Pair<Boolean, Condition> dictatorship;

    private Pair<Boolean, Condition> isElective;

    private Pair<Boolean, Condition> queen;

    private Pair<Boolean, Condition> heir;

    private Pair<Boolean, Condition> hasParliament;

    private Pair<Boolean, Condition> hasDevotion;

    private Pair<Boolean, Condition> hasMeritocracy;

    private Pair<Boolean, Condition> allowForceTributary;

    private Pair<Integer, Condition> duration;

    private Pair<Boolean, Condition> electionOnDeath;

    private Pair<Boolean, Condition> monarchy;

    private Pair<Boolean, Condition> tribal;

    private Pair<Integer, Condition> differentReligionAcceptance;

    private Pair<Integer, Condition> differentReligionGroupAcceptance;

    private Pair<Boolean, Condition> boostIncome;

    private Pair<Boolean, Condition> monastic;

    private Pair<Boolean, Condition> canUseTradePost;

    private Pair<Boolean, Condition> nativeMechanic;

    private Pair<Boolean, Condition> canFormTradeLeague;

    private Pair<Boolean, Condition> freeCity;

    private Pair<Boolean, Condition> isTradingCity;

    private Pair<String, Condition> tradeCityReform;

    private Pair<Boolean, Condition> maintainDynasty;

    private Pair<Boolean, Condition> allowMigration;

    private Pair<Integer, Condition> nationDesignerCost;

    private Pair<Boolean, Condition> papacy;

    private Pair<Boolean, Condition> hasHarem;

    private Pair<Boolean, Condition> hasPashas;

    private Pair<Boolean, Condition> allowVassalWar;

    private Pair<Boolean, Condition> allowVassalAlliance;

    private Pair<Double, Condition> minAutonomy;

    private Pair<Integer, Condition> factionsFrame;

    private Pair<List<String>, Condition> factions;

    private Pair<Boolean, Condition> foreignSlaveRulers;

    private Pair<Boolean, Condition> royalMarriage;

    private Pair<Boolean, Condition> nomad;

    private Pair<Map<String, Modifiers>, Condition> assimilationCultures;

    private Pair<List<Modifiers>, Condition> statesGeneralMechanic;

    private Pair<Boolean, Condition> validForNationDesigner;

    private Pair<Boolean, Condition> allowNormalConversion;

    private Pair<Double, Condition> startTerritoryToEstates;

    private Pair<Boolean, Condition> hasTermElection;

    private Pair<Boolean, Condition> forceAdmiralLeader;

    private Pair<Boolean, Condition> admiralsBecomeRulers;

    private Pair<Boolean, Condition> generalsBecomeRulers;

    private Pair<Boolean, Condition> allowBanners;

    private Pair<Boolean, Condition> usesRevolutionaryZeal;

    private Pair<Boolean, Condition> revolutionary;

    private Pair<Boolean, Condition> revolutionaryClientState;

    private Pair<List<TradeGood>, Condition> disallowedTradeGoods;

    public GovernmentReform(ClausewitzItem item, Game game, GovernmentReform defaultGovernmentReform) {
        this(defaultGovernmentReform);
        this.game = game;
        this.name = item.getName();

        this.modifiers = new Modifiers(item.getChild("modifiers"));

        ClausewitzItem child = item.getChild("custom_attributes");
        this.customAttributes = child == null ? null : child.getVariables()
                                                            .stream()
                                                            .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                                                      ClausewitzVariable::getValue,
                                                                                      (a, b) -> b,
                                                                                      LinkedHashMap::new));

        child = item.getChild("potential");
        this.potential = child == null ? null : new Condition(child);

        ClausewitzList list = item.getList("government_abilities");
        this.governmentAbilities = list == null ? null : list.getValues();

        child = item.getChild("nation_designer_trigger");
        this.nationDesignerTrigger = child == null ? null : new Condition(child);

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
        this.nationDesignerTrigger = other.nationDesignerTrigger;
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
        Condition condition = child == null ? null : new Condition(item.getChild("allow"));

        Boolean aBoolean;
        Integer integer;
        Double aDouble;
        String s;

        ClausewitzList list = item.getList("disallowed_trade_goods");
        this.disallowedTradeGoods = list == null ? this.disallowedTradeGoods
                                                 : Pair.of(list.getValues().stream().map(game::getTradeGood).collect(Collectors.toList()), condition);

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
                                                                         .collect(Collectors.toList()), condition);

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
        return name;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public Modifiers getModifiers() {
        return modifiers;
    }

    public Map<String, String> getCustomAttributes() {
        return customAttributes;
    }

    public Condition getPotential() {
        return potential;
    }

    public Pair<Boolean, Condition> isBasicReform() {
        return basicReform;
    }

    public Pair<Boolean, Condition> isLegacyGovernment() {
        return legacyGovernment;
    }

    public Pair<String, Condition> getIcon() {
        return icon;
    }

    public File getImageFile() {
        return this.game.getSpriteTypeImageFile("government_reform_" + ClausewitzUtils.removeQuotes(this.icon.getKey()));
    }

    public Pair<GovernmentReform, Condition> getLegacyEquivalent() {
        return Pair.of(this.game.getGovernmentReform(this.legacyEquivalent.getKey()), this.legacyEquivalent.getRight());
    }

    public Pair<Boolean, Condition> isLockLevelWhenSelected() {
        return lockLevelWhenSelected;
    }

    public Pair<Boolean, Condition> isValidForNewCountry() {
        return validForNewCountry;
    }

    public Pair<Boolean, Condition> isAllowConvert() {
        return allowConvert;
    }

    public Pair<Boolean, Condition> isRulersCanBeGenerals() {
        return rulersCanBeGenerals;
    }

    public Pair<Boolean, Condition> isHeirsCanBeGenerals() {
        return heirsCanBeGenerals;
    }

    public Pair<Integer, Condition> getFixedRank() {
        return fixedRank;
    }

    public Pair<Boolean, Condition> isRepublicanName() {
        return republicanName;
    }

    public Pair<Boolean, Condition> isMilitarisedSociety() {
        return militarisedSociety;
    }

    public List<String> getGovernmentAbilities() {
        return governmentAbilities;
    }

    public Pair<Boolean, Condition> isClaimStates() {
        return claimStates;
    }

    public Pair<Boolean, Condition> isReligion() {
        return religion;
    }

    public Pair<Boolean, Condition> isRepublic() {
        return republic;
    }

    public Pair<Boolean, Condition> isDictatorship() {
        return dictatorship;
    }

    public Pair<Boolean, Condition> isElective() {
        return isElective;
    }

    public Pair<Boolean, Condition> isQueen() {
        return queen;
    }

    public Pair<Boolean, Condition> isHeir() {
        return heir;
    }

    public Pair<Boolean, Condition> isHasParliament() {
        return hasParliament;
    }

    public Pair<Boolean, Condition> isHasDevotion() {
        return hasDevotion;
    }

    public Pair<Boolean, Condition> isHasMeritocracy() {
        return hasMeritocracy;
    }

    public Pair<Boolean, Condition> isAllowForceTributary() {
        return allowForceTributary;
    }

    public Pair<Integer, Condition> getDuration() {
        return duration;
    }

    public Pair<Boolean, Condition> isElectionOnDeath() {
        return electionOnDeath;
    }

    public Pair<Boolean, Condition> isMonarchy() {
        return monarchy;
    }

    public Pair<Boolean, Condition> isTribal() {
        return tribal;
    }

    public Pair<Integer, Condition> getDifferentReligionAcceptance() {
        return differentReligionAcceptance;
    }

    public Pair<Integer, Condition> getDifferentReligionGroupAcceptance() {
        return differentReligionGroupAcceptance;
    }

    public Pair<Boolean, Condition> isBoostIncome() {
        return boostIncome;
    }

    public Pair<Boolean, Condition> isMonastic() {
        return monastic;
    }

    public Pair<Boolean, Condition> isCanUseTradePost() {
        return canUseTradePost;
    }

    public Pair<Boolean, Condition> isNativeMechanic() {
        return nativeMechanic;
    }

    public Pair<Boolean, Condition> isCanFormTradeLeague() {
        return canFormTradeLeague;
    }

    public Pair<Boolean, Condition> isFreeCity() {
        return freeCity;
    }

    public Pair<Boolean, Condition> isTradingCity() {
        return isTradingCity;
    }

    public Pair<String, Condition> getTradeCityReform() {
        return tradeCityReform;
    }

    public Pair<Boolean, Condition> isMaintainDynasty() {
        return maintainDynasty;
    }

    public Pair<Boolean, Condition> isAllowMigration() {
        return allowMigration;
    }

    public Condition getNationDesignerTrigger() {
        return nationDesignerTrigger;
    }

    public Pair<Integer, Condition> getNationDesignerCost() {
        return nationDesignerCost;
    }

    public Pair<Boolean, Condition> isPapacy() {
        return papacy;
    }

    public Pair<Boolean, Condition> isHasHarem() {
        return hasHarem;
    }

    public Pair<Boolean, Condition> isHasPashas() {
        return hasPashas;
    }

    public Pair<Boolean, Condition> isAllowVassalWar() {
        return allowVassalWar;
    }

    public Pair<Boolean, Condition> isAllowVassalAlliance() {
        return allowVassalAlliance;
    }

    public Pair<Double, Condition> getMinAutonomy() {
        return minAutonomy;
    }

    public Pair<Integer, Condition> getFactionsFrame() {
        return factionsFrame;
    }

    public Pair<List<String>, Condition> getFactions() {
        return factions;
    }

    public Pair<Boolean, Condition> isForeignSlaveRulers() {
        return foreignSlaveRulers;
    }

    public Pair<Boolean, Condition> isRoyalMarriage() {
        return royalMarriage;
    }

    public Pair<Boolean, Condition> isNomad() {
        return nomad;
    }

    public Pair<Map<String, Modifiers>, Condition> getAssimilationCultures() {
        return assimilationCultures;
    }

    public Pair<List<Modifiers>, Condition> getStatesGeneralMechanic() {
        return statesGeneralMechanic;
    }

    public Pair<Boolean, Condition> isValidForNationDesigner() {
        return validForNationDesigner;
    }

    public Pair<Boolean, Condition> isAllowNormalConversion() {
        return allowNormalConversion;
    }

    public Pair<Double, Condition> getStartTerritoryToEstates() {
        return startTerritoryToEstates;
    }

    public Pair<Boolean, Condition> isHasTermElection() {
        return hasTermElection;
    }

    public Pair<Boolean, Condition> isForceAdmiralLeader() {
        return forceAdmiralLeader;
    }

    public Pair<Boolean, Condition> isAdmiralsBecomeRulers() {
        return admiralsBecomeRulers;
    }

    public Pair<Boolean, Condition> isGeneralsBecomeRulers() {
        return generalsBecomeRulers;
    }

    public Pair<Boolean, Condition> isAllowBanners() {
        return allowBanners;
    }

    public Pair<Boolean, Condition> isUsesRevolutionaryZeal() {
        return usesRevolutionaryZeal;
    }

    public Pair<Boolean, Condition> isRevolutionary() {
        return revolutionary;
    }

    public Pair<Boolean, Condition> isRevolutionaryClientState() {
        return revolutionaryClientState;
    }

    public Pair<List<TradeGood>, Condition> getDisallowedTradeGoods() {
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

        GovernmentReform reform = (GovernmentReform) o;

        return Objects.equals(name, reform.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
