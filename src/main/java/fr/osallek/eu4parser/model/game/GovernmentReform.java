package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.clausewitzparser.model.ClausewitzObject;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;
import fr.osallek.eu4parser.model.game.condition.ConditionAnd;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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

        this.customAttributes = item.getChild("custom_attributes")
                                    .map(child -> child.getVariables()
                                                       .stream()
                                                       .collect(Collectors.toMap(ClausewitzVariable::getName, ClausewitzVariable::getValue, (a, b) -> b,
                                                                                 LinkedHashMap::new)))
                                    .orElse(null);

        this.governmentAbilities = item.getList("government_abilities").map(ClausewitzList::getValues).orElse(null);
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
        ConditionAnd condition = item.getChild("allow").map(ConditionAnd::new).orElse(null);

        this.disallowedTradeGoods = item.getList("disallowed_trade_goods")
                                        .map(list -> Pair.of(list.getValues().stream().map(game::getTradeGood).toList(), condition))
                                        .orElse(this.disallowedTradeGoods);
        this.factions = item.getList("factions").map(list -> Pair.of(list.getValues(), condition)).orElse(this.factions);
        this.assimilationCultures = item.getChild("assimilation_cultures")
                                        .map(child -> Pair.of(child.getChildren().stream().collect(Collectors.toMap(ClausewitzObject::getName, Modifiers::new)),
                                                              condition))
                                        .orElse(null);
        this.statesGeneralMechanic = item.getChild("states_general_mechanic")
                                         .map(child -> Pair.of(child.getChildren().stream().map(Modifiers::new).toList(), condition))
                                         .orElse(null);
        this.basicReform = Pair.of(BooleanUtils.toBoolean(item.getVarAsBool("basic_reform").orElse(null)), condition);
        this.legacyGovernment = Pair.of(BooleanUtils.toBoolean(item.getVarAsBool("legacy_government").orElse(null)), condition);
        this.legacyEquivalent = Pair.of(item.getVarAsString("legacy_equivalent").orElse(null), condition);
        this.lockLevelWhenSelected = Pair.of(BooleanUtils.toBoolean(item.getVarAsBool("lock_level_when_selected").orElse(null)), condition);
        this.icon = item.getVarAsString("icon").map(s -> Pair.of(s, condition)).orElse(this.icon);
        this.validForNewCountry = item.getVarAsBool("valid_for_new_country").map(b -> Pair.of(b, condition)).orElse(this.validForNewCountry);
        this.allowConvert = item.getVarAsBool("allow_convert").map(aBoolean -> Pair.of(aBoolean, condition)).orElse(this.allowConvert);
        this.rulersCanBeGenerals = item.getVarAsBool("rulers_can_be_generals").map(aBoolean -> Pair.of(aBoolean, condition)).orElse(this.rulersCanBeGenerals);
        this.heirsCanBeGenerals = item.getVarAsBool("heirs_can_be_generals").map(aBoolean -> Pair.of(aBoolean, condition)).orElse(this.heirsCanBeGenerals);
        this.fixedRank = item.getVarAsInt("fixed_rank").map(integer -> Pair.of(integer, condition)).orElse(this.fixedRank);
        this.republicanName = item.getVarAsBool("republican_name").map(aBoolean -> Pair.of(aBoolean, condition)).orElse(this.republicanName);
        this.militarisedSociety = item.getVarAsBool("militarised_society").map(aBoolean -> Pair.of(aBoolean, condition)).orElse(this.militarisedSociety);
        this.claimStates = item.getVarAsBool("claim_states").map(aBoolean -> Pair.of(aBoolean, condition)).orElse(this.claimStates);
        this.religion = item.getVarAsBool("religion").map(aBoolean -> Pair.of(aBoolean, condition)).orElse(this.religion);
        this.republic = item.getVarAsBool("republic").map(aBoolean -> Pair.of(aBoolean, condition)).orElse(this.republic);
        this.dictatorship = item.getVarAsBool("dictatorship").map(aBoolean -> Pair.of(aBoolean, condition)).orElse(this.dictatorship);
        this.isElective = item.getVarAsBool("is_elective").map(aBoolean -> Pair.of(aBoolean, condition)).orElse(this.isElective);
        this.queen = item.getVarAsBool("queen").map(aBoolean -> Pair.of(aBoolean, condition)).orElse(this.queen);
        this.heir = item.getVarAsBool("heir").map(aBoolean -> Pair.of(aBoolean, condition)).orElse(this.heir);
        this.hasParliament = item.getVarAsBool("has_parliament").map(aBoolean -> Pair.of(aBoolean, condition)).orElse(this.hasParliament);
        this.hasDevotion = item.getVarAsBool("has_devotion").map(aBoolean -> Pair.of(aBoolean, condition)).orElse(this.hasDevotion);
        this.hasMeritocracy = item.getVarAsBool("has_meritocracy").map(aBoolean -> Pair.of(aBoolean, condition)).orElse(this.hasMeritocracy);
        this.allowForceTributary = item.getVarAsBool("allow_force_tributary").map(aBoolean -> Pair.of(aBoolean, condition)).orElse(this.allowForceTributary);
        this.duration = item.getVarAsInt("duration").map(integer -> Pair.of(integer, condition)).orElse(this.duration);
        this.electionOnDeath = item.getVarAsBool("election_on_death").map(aBoolean -> Pair.of(aBoolean, condition)).orElse(this.electionOnDeath);
        this.monarchy = item.getVarAsBool("monarchy").map(aBoolean -> Pair.of(aBoolean, condition)).orElse(this.monarchy);
        this.tribal = item.getVarAsBool("tribal").map(aBoolean -> Pair.of(aBoolean, condition)).orElse(this.tribal);
        this.differentReligionAcceptance = item.getVarAsInt("different_religion_acceptance")
                                               .map(integer -> Pair.of(integer, condition))
                                               .orElse(this.differentReligionAcceptance);
        this.differentReligionGroupAcceptance = item.getVarAsInt("different_religion_group_acceptance")
                                                    .map(integer -> Pair.of(integer, condition))
                                                    .orElse(this.differentReligionGroupAcceptance);
        this.boostIncome = item.getVarAsBool("boost_income").map(aBoolean -> Pair.of(aBoolean, condition)).orElse(this.boostIncome);
        this.monastic = item.getVarAsBool("monastic").map(aBoolean -> Pair.of(aBoolean, condition)).orElse(this.monastic);
        this.canUseTradePost = item.getVarAsBool("can_use_trade_post").map(aBoolean -> Pair.of(aBoolean, condition)).orElse(this.canUseTradePost);
        this.nativeMechanic = item.getVarAsBool("native_mechanic").map(aBoolean -> Pair.of(aBoolean, condition)).orElse(this.nativeMechanic);
        this.canFormTradeLeague = item.getVarAsBool("can_form_trade_league").map(aBoolean -> Pair.of(aBoolean, condition)).orElse(this.canFormTradeLeague);
        this.freeCity = item.getVarAsBool("free_city").map(aBoolean -> Pair.of(aBoolean, condition)).orElse(this.freeCity);
        this.isTradingCity = item.getVarAsBool("is_trading_city").map(aBoolean -> Pair.of(aBoolean, condition)).orElse(this.isTradingCity);
        this.tradeCityReform = item.getVarAsString("trade_city_reform").map(s -> Pair.of(s, condition)).orElse(this.tradeCityReform);
        this.maintainDynasty = item.getVarAsBool("maintain_dynasty").map(aBoolean -> Pair.of(aBoolean, condition)).orElse(this.maintainDynasty);
        this.allowMigration = item.getVarAsBool("allow_migration").map(aBoolean -> Pair.of(aBoolean, condition)).orElse(this.allowMigration);
        this.nationDesignerCost = item.getVarAsInt("nation_designer_cost").map(integer -> Pair.of(integer, condition)).orElse(this.nationDesignerCost);
        this.papacy = item.getVarAsBool("papacy").map(aBoolean -> Pair.of(aBoolean, condition)).orElse(this.papacy);
        this.hasHarem = item.getVarAsBool("has_harem").map(aBoolean -> Pair.of(aBoolean, condition)).orElse(this.hasHarem);
        this.hasPashas = item.getVarAsBool("has_pashas").map(aBoolean -> Pair.of(aBoolean, condition)).orElse(this.hasPashas);
        this.allowVassalWar = item.getVarAsBool("allow_vassal_war").map(aBoolean -> Pair.of(aBoolean, condition)).orElse(this.allowVassalWar);
        this.allowVassalAlliance = item.getVarAsBool("allow_vassal_alliance").map(aBoolean -> Pair.of(aBoolean, condition)).orElse(this.allowVassalAlliance);
        this.minAutonomy = item.getVarAsDouble("min_autonomy").map(aDouble -> Pair.of(aDouble, condition)).orElse(this.minAutonomy);
        this.factionsFrame = item.getVarAsInt("factions_frame").map(integer -> Pair.of(integer, condition)).orElse(this.factionsFrame);
        this.foreignSlaveRulers = item.getVarAsBool("foreign_slave_rulers").map(aBoolean -> Pair.of(aBoolean, condition)).orElse(this.foreignSlaveRulers);
        this.royalMarriage = item.getVarAsBool("royal_marriage").map(aBoolean -> Pair.of(aBoolean, condition)).orElse(this.royalMarriage);
        this.nomad = item.getVarAsBool("nomad").map(aBoolean -> Pair.of(aBoolean, condition)).orElse(this.nomad);
        this.validForNationDesigner = item.getVarAsBool("valid_for_nation_designer")
                                          .map(aBoolean -> Pair.of(aBoolean, condition))
                                          .orElse(this.validForNationDesigner);
        this.allowNormalConversion = item.getVarAsBool("allow_normal_conversion")
                                         .map(aBoolean -> Pair.of(aBoolean, condition))
                                         .orElse(this.allowNormalConversion);
        this.startTerritoryToEstates = item.getVarAsDouble("start_territory_to_estates")
                                           .map(aDouble -> Pair.of(aDouble, condition))
                                           .orElse(this.startTerritoryToEstates);
        this.hasTermElection = item.getVarAsBool("has_term_election").map(aBoolean -> Pair.of(aBoolean, condition)).orElse(this.hasTermElection);
        this.forceAdmiralLeader = item.getVarAsBool("force_admiral_leader").map(aBoolean -> Pair.of(aBoolean, condition)).orElse(this.forceAdmiralLeader);
        this.admiralsBecomeRulers = item.getVarAsBool("admirals_become_rulers").map(aBoolean -> Pair.of(aBoolean, condition)).orElse(this.admiralsBecomeRulers);
        this.generalsBecomeRulers = item.getVarAsBool("generals_become_rulers").map(aBoolean -> Pair.of(aBoolean, condition)).orElse(this.generalsBecomeRulers);
        this.allowBanners = item.getVarAsBool("allow_banners").map(aBoolean -> Pair.of(aBoolean, condition)).orElse(this.allowBanners);
        this.usesRevolutionaryZeal = item.getVarAsBool("uses_revolutionary_zeal")
                                         .map(aBoolean -> Pair.of(aBoolean, condition))
                                         .orElse(this.usesRevolutionaryZeal);
        this.revolutionary = item.getVarAsBool("revolutionary").map(aBoolean -> Pair.of(aBoolean, condition)).orElse(this.revolutionary);
        this.revolutionaryClientState = item.getVarAsBool("revolutionary_client_state")
                                            .map(aBoolean -> Pair.of(aBoolean, condition))
                                            .orElse(this.revolutionaryClientState);
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public Optional<Modifiers> getModifiers() {
        return this.item.getChild("modifiers").map(Modifiers::new);
    }

    public Map<String, String> getCustomAttributes() {
        return customAttributes;
    }

    public Optional<ConditionAnd> getPotential() {
        return this.item.getChild("potential").map(ConditionAnd::new);
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

    public Optional<ConditionAnd> getNationDesignerTrigger() {
        return this.item.getChild("nation_designer_trigger").map(ConditionAnd::new);
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

        if (!(o instanceof GovernmentReform governmentReform)) {
            return false;
        }

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
