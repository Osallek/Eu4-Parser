package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.clausewitzparser.model.ClausewitzObject;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import com.osallek.eu4parser.common.Modifier;
import com.osallek.eu4parser.common.NumbersUtils;
import org.apache.commons.lang3.BooleanUtils;

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

    private Map<Condition, Map<String, String>> conditional;

    private boolean basicReform;

    private boolean legacyGovernment;

    private String legacyEquivalent;

    private boolean lockLevelWhenSelected;

    private boolean validForNewCountry;

    private boolean allowConvert;

    private boolean rulersCanBeGenerals;

    private boolean heirsCanBeGenerals;

    private int fixedRank;

    private boolean republicanName;

    private boolean militarisedSociety;

    private List<String> governmentAbilities;

    private boolean claimStates;

    private boolean religion;

    private boolean republic;

    private boolean dictatorship;

    private boolean isElective;

    private boolean queen;

    private boolean heir;

    private boolean hasParliament;

    private boolean hasDevotion;

    private boolean hasMeritocracy;

    private boolean allowForceTributary;

    private int duration;

    private boolean electionOnDeath;

    private boolean monarchy;

    private boolean tribal;

    private int differentReligionAcceptance;

    private int differentReligionGroupAcceptance;

    private boolean boostIncome;

    private boolean monastic;

    private boolean canUseTradePost;

    private boolean nativeMechanic;

    private boolean canFormTradeLeague;

    private boolean freeCity;

    private boolean isTradingCity;

    private String tradeCityReform;

    private boolean maintainDynasty;

    private boolean allowMigration;

    private Condition nationDesignerTrigger;

    private int nationDesignerCost;

    private boolean papacy;

    private boolean hasHarem;

    private boolean hasPashas;

    private boolean allowVassalWar;

    private boolean allowVassalAlliance;

    private double minAutonomy;

    private int factionsFrame;

    private List<String> factions;

    private boolean foreignSlaveRulers;

    private boolean royalMarriage;

    private boolean nomad;

    private Map<String, Modifiers> assimilationCultures;

    private Map<String, Modifiers> statesGeneralMechanic;

    private boolean validForNationDesigner;

    private boolean allowNormalConversion;

    private double startTerritoryToEstates;

    private boolean hasTermElection;

    private boolean forceAdmiralLeader;

    private boolean admiralsBecomeRulers;

    private boolean generalsBecomeRulers;

    private boolean allowBanners;

    private boolean usesRevolutionaryZeal;

    private boolean revolutionary;

    private boolean revolutionaryClientState;

    private List<TradeGood> disallowedTradeGoods;

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

        List<ClausewitzItem> children = item.getChildren("conditional");
        this.conditional = children.stream().collect(Collectors.toMap(item1 -> new Condition(item1.getChild("allow")),
                                                                      item1 -> item1.getVariables()
                                                                                    .stream()
                                                                                    .collect(Collectors.toMap(
                                                                                            ClausewitzVariable::getName,
                                                                                            ClausewitzVariable::getValue,
                                                                                            (a, b) -> b,
                                                                                            LinkedHashMap::new))));

        ClausewitzList list = item.getList("government_abilities");
        this.governmentAbilities = list == null ? null : list.getValues();

        child = item.getChild("nation_designer_trigger");
        this.nationDesignerTrigger = child == null ? null : new Condition(child);

        list = item.getList("factions");
        this.factions = list == null ? null : list.getValues();

        child = item.getChild("assimilation_cultures");
        this.assimilationCultures = child == null ? null : child.getChildren()
                                                                .stream()
                                                                .collect(Collectors.toMap(ClausewitzObject::getName, Modifiers::new));
        child = item.getChild("states_general_mechanic");
        this.statesGeneralMechanic = child == null ? null : child.getChildren()
                                                                 .stream()
                                                                 .collect(Collectors.toMap(ClausewitzObject::getName, Modifiers::new));
        list = item.getList("disallowed_trade_goods");
        this.disallowedTradeGoods = list == null ? null : list.getValues().stream().map(game::getTradeGood).collect(Collectors.toList());

        this.basicReform = BooleanUtils.toBoolean(item.getVarAsBool("basic_reform"));
        this.legacyGovernment = BooleanUtils.toBoolean(item.getVarAsBool("legacy_government"));
        this.legacyEquivalent = item.getVarAsString("legacy_equivalent");
        this.lockLevelWhenSelected = BooleanUtils.toBoolean(item.getVarAsBool("lock_level_when_selected"));

        this.validForNewCountry = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("valid_for_new_country"), this.validForNewCountry);
        this.allowConvert = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("allow_convert"), this.allowConvert);
        this.rulersCanBeGenerals = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("rulers_can_be_generals"), this.rulersCanBeGenerals);
        this.heirsCanBeGenerals = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("heirs_can_be_generals"), this.heirsCanBeGenerals);
        this.fixedRank = NumbersUtils.intOrDefault(item.getVarAsInt("fixed_rank"), this.fixedRank);
        this.republicanName = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("republican_name"), this.republicanName);
        this.militarisedSociety = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("militarised_society"), this.militarisedSociety);
        this.claimStates = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("claim_states"), this.claimStates);
        this.religion = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("religion"), this.religion);
        this.republic = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("republic"), this.republic);
        this.dictatorship = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("dictatorship"), this.dictatorship);
        this.isElective = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("is_elective"), this.isElective);
        this.queen = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("queen"), this.queen);
        this.heir = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("heir"), this.heir);
        this.hasParliament = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("has_parliament"), this.hasParliament);
        this.hasDevotion = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("has_devotion"), this.hasDevotion);
        this.hasMeritocracy = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("has_meritocracy"), this.hasMeritocracy);
        this.allowForceTributary = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("allow_force_tributary"), this.allowForceTributary);
        this.duration = NumbersUtils.intOrDefault(item.getVarAsInt("duration"), this.duration);
        this.electionOnDeath = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("election_on_death"), this.electionOnDeath);
        this.monarchy = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("monarchy"), this.monarchy);
        this.tribal = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("tribal"), this.tribal);
        this.differentReligionAcceptance = NumbersUtils.intOrDefault(item.getVarAsInt("different_religion_acceptance"), this.differentReligionAcceptance);
        this.differentReligionGroupAcceptance = NumbersUtils.intOrDefault(item.getVarAsInt("different_religion_group_acceptance"),
                                                                          this.differentReligionGroupAcceptance);
        this.boostIncome = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("boost_income"), this.boostIncome);
        this.monastic = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("monastic"), this.monastic);
        this.canUseTradePost = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("can_use_trade_post"), this.canUseTradePost);
        this.nativeMechanic = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("native_mechanic"), this.nativeMechanic);
        this.canFormTradeLeague = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("can_form_trade_league"), this.canFormTradeLeague);
        this.freeCity = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("free_city"), this.freeCity);
        this.isTradingCity = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("is_trading_city"), this.isTradingCity);
        this.tradeCityReform = item.getVarAsString("trade_city_reform");
        this.maintainDynasty = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("maintain_dynasty"), this.maintainDynasty);
        this.allowMigration = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("allow_migration"), this.allowMigration);
        this.nationDesignerCost = NumbersUtils.intOrDefault(item.getVarAsInt("nation_designer_cost"), this.nationDesignerCost);
        this.papacy = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("papacy"), this.papacy);
        this.hasHarem = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("has_harem"), this.hasHarem);
        this.hasPashas = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("has_pashas"), this.hasPashas);
        this.allowVassalWar = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("allow_vassal_war"), this.allowVassalWar);
        this.allowVassalAlliance = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("allow_vassal_alliance"), this.allowVassalAlliance);
        this.minAutonomy = NumbersUtils.doubleOrDefault(item.getVarAsDouble("min_autonomy"), this.minAutonomy);
        this.factionsFrame = NumbersUtils.intOrDefault(item.getVarAsInt("factions_frame"), this.factionsFrame);
        this.foreignSlaveRulers = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("foreign_slave_rulers"), this.foreignSlaveRulers);
        this.royalMarriage = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("royal_marriage"), this.royalMarriage);
        this.nomad = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("nomad"), this.nomad);
        this.validForNationDesigner = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("valid_for_nation_designer"), this.validForNationDesigner);
        this.allowNormalConversion = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("allow_normal_conversion"), this.allowNormalConversion);
        this.startTerritoryToEstates = NumbersUtils.doubleOrDefault(item.getVarAsDouble("start_territory_to_estates"), this.startTerritoryToEstates);
        this.hasTermElection = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("has_term_election"), this.hasTermElection);
        this.forceAdmiralLeader = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("force_admiral_leader"), this.forceAdmiralLeader);
        this.admiralsBecomeRulers = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("admirals_become_rulers"), this.admiralsBecomeRulers);
        this.generalsBecomeRulers = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("generals_become_rulers"), this.generalsBecomeRulers);
        this.allowBanners = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("allow_banners"), this.allowBanners);
        this.usesRevolutionaryZeal = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("uses_revolutionary_zeal"), this.usesRevolutionaryZeal);
        this.revolutionary = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("revolutionary"), this.revolutionary);
        this.revolutionaryClientState = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("revolutionary_client_state"), this.revolutionaryClientState);
    }

    public GovernmentReform(GovernmentReform other) {
        if (other == null) {
            return;
        }

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

    public Map<Condition, Map<String, String>> getConditional() {
        return conditional;
    }

    public boolean isBasicReform() {
        return basicReform;
    }

    public boolean isLegacyGovernment() {
        return legacyGovernment;
    }

    public GovernmentReform getLegacyEquivalent() {
        return this.game.getGovernmentReform(legacyEquivalent);
    }

    public boolean isLockLevelWhenSelected() {
        return lockLevelWhenSelected;
    }

    public boolean isValidForNewCountry() {
        return validForNewCountry;
    }

    public boolean isAllowConvert() {
        return allowConvert;
    }

    public boolean isRulersCanBeGenerals() {
        return rulersCanBeGenerals;
    }

    public boolean isHeirsCanBeGenerals() {
        return heirsCanBeGenerals;
    }

    public int getFixedRank() {
        return fixedRank;
    }

    public boolean isRepublicanName() {
        return republicanName;
    }

    public boolean isMilitarisedSociety() {
        return militarisedSociety;
    }

    public List<String> getGovernmentAbilities() {
        return governmentAbilities;
    }

    public boolean isClaimStates() {
        return claimStates;
    }

    public boolean isReligion() {
        return religion;
    }

    public boolean isRepublic() {
        return republic;
    }

    public boolean isDictatorship() {
        return dictatorship;
    }

    public boolean isElective() {
        return isElective;
    }

    public boolean isQueen() {
        return queen;
    }

    public boolean isHeir() {
        return heir;
    }

    public boolean isHasParliament() {
        return hasParliament;
    }

    public boolean isHasDevotion() {
        return hasDevotion;
    }

    public boolean isHasMeritocracy() {
        return hasMeritocracy;
    }

    public boolean isAllowForceTributary() {
        return allowForceTributary;
    }

    public int getDuration() {
        return duration;
    }

    public boolean isElectionOnDeath() {
        return electionOnDeath;
    }

    public boolean isMonarchy() {
        return monarchy;
    }

    public boolean isTribal() {
        return tribal;
    }

    public int getDifferentReligionAcceptance() {
        return differentReligionAcceptance;
    }

    public int getDifferentReligionGroupAcceptance() {
        return differentReligionGroupAcceptance;
    }

    public boolean isBoostIncome() {
        return boostIncome;
    }

    public boolean isMonastic() {
        return monastic;
    }

    public boolean isCanUseTradePost() {
        return canUseTradePost;
    }

    public boolean isNativeMechanic() {
        return nativeMechanic;
    }

    public boolean isCanFormTradeLeague() {
        return canFormTradeLeague;
    }

    public boolean isFreeCity() {
        return freeCity;
    }

    public boolean isTradingCity() {
        return isTradingCity;
    }

    public String getTradeCityReform() {
        return tradeCityReform;
    }

    public boolean isMaintainDynasty() {
        return maintainDynasty;
    }

    public boolean isAllowMigration() {
        return allowMigration;
    }

    public Condition getNationDesignerTrigger() {
        return nationDesignerTrigger;
    }

    public int getNationDesignerCost() {
        return nationDesignerCost;
    }

    public boolean isPapacy() {
        return papacy;
    }

    public boolean isHasHarem() {
        return hasHarem;
    }

    public boolean isHasPashas() {
        return hasPashas;
    }

    public boolean isAllowVassalWar() {
        return allowVassalWar;
    }

    public boolean isAllowVassalAlliance() {
        return allowVassalAlliance;
    }

    public double getMinAutonomy() {
        return minAutonomy;
    }

    public int getFactionsFrame() {
        return factionsFrame;
    }

    public List<String> getFactions() {
        return factions;
    }

    public boolean isForeignSlaveRulers() {
        return foreignSlaveRulers;
    }

    public boolean isRoyalMarriage() {
        return royalMarriage;
    }

    public boolean isNomad() {
        return nomad;
    }

    public Map<String, Modifiers> getAssimilationCultures() {
        return assimilationCultures;
    }

    public Map<String, Modifiers> getStatesGeneralMechanic() {
        return statesGeneralMechanic;
    }

    public boolean isValidForNationDesigner() {
        return validForNationDesigner;
    }

    public boolean isAllowNormalConversion() {
        return allowNormalConversion;
    }

    public double getStartTerritoryToEstates() {
        return startTerritoryToEstates;
    }

    public boolean isHasTermElection() {
        return hasTermElection;
    }

    public boolean isForceAdmiralLeader() {
        return forceAdmiralLeader;
    }

    public boolean isAdmiralsBecomeRulers() {
        return admiralsBecomeRulers;
    }

    public boolean isGeneralsBecomeRulers() {
        return generalsBecomeRulers;
    }

    public boolean isAllowBanners() {
        return allowBanners;
    }

    public boolean isUsesRevolutionaryZeal() {
        return usesRevolutionaryZeal;
    }

    public boolean isRevolutionary() {
        return revolutionary;
    }

    public boolean isRevolutionaryClientState() {
        return revolutionaryClientState;
    }

    public List<TradeGood> getDisallowedTradeGoods() {
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
