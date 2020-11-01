package com.osallek.eu4parser.common;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import com.osallek.eu4parser.model.game.GovernmentReform;
import com.osallek.eu4parser.model.game.Modifiers;
import com.osallek.eu4parser.model.save.country.Country;
import com.osallek.eu4parser.model.save.province.SaveProvince;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ModifiersUtils {

    private ModifiersUtils() {}

    private static final Logger LOGGER = LoggerFactory.getLogger(ModifiersUtils.class);

    private static final Map<String, Modifier> MODIFIERS_MAP = new HashMap<>();

    static {
        ModifiersUtils.addModifier("ARMY_TRADITION", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("ARMY_TRADITION_DECAY", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("ARMY_TRADITION_FROM_BATTLE", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("YEARLY_ARMY_PROFESSIONALISM", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("DRILL_GAIN_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("DRILL_DECAY_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("INFANTRY_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("INFANTRY_POWER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("INFANTRY_FIRE", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("INFANTRY_SHOCK", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("CAVALRY_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("CAVALRY_POWER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("CAVALRY_FIRE", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("CAVALRY_SHOCK", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("ARTILLERY_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("ARTILLERY_POWER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("ARTILLERY_FIRE", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("ARTILLERY_SHOCK", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("CAV_TO_INF_RATIO", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("CAVALRY_FLANKING", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("ARTILLERY_BONUS_VS_FORT", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("BACKROW_ARTILLERY_DAMAGE", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("DISCIPLINE", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("MERCENARY_DISCIPLINE", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("LAND_MORALE", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("DEFENSIVENESS", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("SIEGE_ABILITY", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("MOVEMENT_SPEED", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("FIRE_DAMAGE", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("FIRE_DAMAGE_RECEIVED", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("SHOCK_DAMAGE", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("SHOCK_DAMAGE_RECEIVED", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("RECOVER_ARMY_MORALE_SPEED", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("SIEGE_BLOCKADE_PROGRESS", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("RESERVES_ORGANISATION", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("LAND_ATTRITION", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("REINFORCE_COST_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("REINFORCE_SPEED", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("MANPOWER_RECOVERY_SPEED", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("GLOBAL_MANPOWER", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("GLOBAL_MANPOWER_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("GLOBAL_REGIMENT_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("GLOBAL_REGIMENT_RECRUIT_SPEED", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("GLOBAL_SUPPLY_LIMIT_MODIFIER", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("LAND_FORCELIMIT", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("LAND_FORCELIMIT_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("LAND_MAINTENANCE_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("MERCENARY_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("MERC_MAINTENANCE_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("POSSIBLE_CONDOTTIERI", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("HOSTILE_ATTRITION", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("GARRISON_SIZE", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("GLOBAL_GARRISON_GROWTH", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("FORT_MAINTENANCE_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("RIVAL_BORDER_FORT_MAINTENANCE", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("WAR_EXHAUSTION", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("WAR_EXHAUSTION_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("LEADER_LAND_FIRE", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("LEADER_LAND_MANUEVER", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("LEADER_LAND_SHOCK", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("LEADER_SIEGE", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("GENERAL_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("FREE_LEADER_POOL", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("RAZE_POWER_GAIN", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("LOOT_AMOUNT", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("AVAILABLE_PROVINCE_LOOT", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("PRESTIGE_FROM_LAND", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("AMOUNT_OF_BANNERS", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("WAR_TAXES_COST_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("LEADER_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("MAY_RECRUIT_FEMALE_GENERALS", ModifierType.CONSTANT, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("SPECIAL_UNIT_FORCELIMIT", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("MANPOWER_IN_TRUE_FAITH_PROVINCES", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("MERCENARY_MANPOWER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("NAVY_TRADITION", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("NAVY_TRADITION_DECAY", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("NAVAL_TRADITION_FROM_BATTLE", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("NAVAL_TRADITION_FROM_TRADE", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("HEAVY_SHIP_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("HEAVY_SHIP_POWER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("LIGHT_SHIP_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("LIGHT_SHIP_POWER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("GALLEY_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("GALLEY_POWER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("TRANSPORT_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("TRANSPORT_POWER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("GLOBAL_SHIP_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("GLOBAL_SHIP_RECRUIT_SPEED", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("GLOBAL_SHIP_REPAIR", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("NAVAL_FORCELIMIT", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("NAVAL_FORCELIMIT_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("NAVAL_MAINTENANCE_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("GLOBAL_SAILORS", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("GLOBAL_SAILORS_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("SAILOR_MAINTENANCE_MODIFER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("SAILORS_RECOVERY_SPEED", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("BLOCKADE_EFFICIENCY", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("CAPTURE_SHIP_CHANCE", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("GLOBAL_NAVAL_ENGAGEMENT_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("NAVAL_ATTRITION", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("NAVAL_MORALE", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("SHIP_DURABILITY", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("SUNK_SHIP_MORALE_HIT_RECIEVED", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("RECOVER_NAVY_MORALE_SPEED", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("PRESTIGE_FROM_NAVAL", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("LEADER_NAVAL_FIRE", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("LEADER_NAVAL_MANUEVER", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("LEADER_NAVAL_SHOCK", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("OWN_COAST_NAVAL_COMBAT_BONUS", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("ADMIRAL_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("MOVEMENT_SPEED_ONTO_OFF_BOAT_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("GLOBAL_NAVAL_BARRAGE_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("ALLOWED_MARINE_FRACTION", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("FLAGSHIP_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("MAY_PERFORM_SLAVE_RAID", ModifierType.CONSTANT, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("MAY_PERFORM_SLAVE_RAID_ON_SAME_RELIGION", ModifierType.CONSTANT, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("ADMIRAL_SKILL_GAIN_MODIFIER", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("FLAGSHIP_DURABILITY", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("FLAGSHIP_MORALE", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("FLAGSHIP_NAVAL_ENGAGEMENT_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("FLAGSHIP_LANDING_PENALTY", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("NUMBER_OF_CANNONS_FLAGSHIP_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("NAVAL_MAINTENANCE_FLAGSHIP_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("TRADE_POWER_IN_FLEET_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("MORALE_IN_FLEET_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("BLOCKADE_IMPACT_ON_SIEGE_IN_FLEET_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("EXPLORATION_MISSION_RANGE_IN_FLEET_MODIFIER", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("BARRAGE_COST_IN_FLEET_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("NAVAL_ATTRITION_IN_FLEET_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("PRIVATEERING_EFFICIENCY_IN_FLEET_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("PRESTIGE_FROM_BATTLES_IN_FLEET_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("NAVAL_TRADITION_IN_FLEET_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("CANNONS_FOR_HUNTING_PIRATES_IN_FLEET", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("MOVEMENT_SPEED_IN_FLEET_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("DIPLOMATS", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("DIPLOMATIC_REPUTATION", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("DIPLOMATIC_UPKEEP", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("ENVOY_TRAVEL_TIME", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("FABRICATE_CLAIMS_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("IMPROVE_RELATION_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("VASSAL_FORCELIMIT_BONUS", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("VASSAL_INCOME", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("AE_IMPACT", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("CLAIM_DURATION", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("DIPLOMATIC_ANNEXATION_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("PROVINCE_WARSCORE_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("UNJUSTIFIED_DEMANDS", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("ENEMY_CORE_CREATION", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("RIVAL_CHANGE_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("JUSTIFY_TRADE_CONFLICT_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("STABILITY_COST_TO_DECLARE_WAR", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("GLOBAL_TAX_INCOME", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("GLOBAL_TAX_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("PRODUCTION_EFFICIENCY", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("STATE_MAINTENANCE_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("INFLATION_ACTION_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("INFLATION_REDUCTION", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("INTEREST", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("DEVELOPMENT_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("BUILD_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("BUILD_TIME", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("ADMINISTRATIVE_EFFICIENCY", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("CORE_CREATION", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("CORE_DECAY_ON_YOUR_OWN", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("ADM_TECH_COST_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("DIP_TECH_COST_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("MIL_TECH_COST_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("TECHNOLOGY_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("IDEA_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("EMBRACEMENT_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("GLOBAL_INSTITUTION_SPREAD", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("INSTITUTION_SPREAD_FROM_TRUE_FAITH", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("NATIVE_ADVANCEMENT_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("ALL_POWER_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("INNOVATIVENESS_GAIN", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("FREE_ADM_POLICY", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("FREE_DIP_POLICY", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("FREE_MIL_POLICY", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("POSSIBLE_ADM_POLICY", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("POSSIBLE_DIP_POLICY", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("POSSIBLE_MIL_POLICY", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("POSSIBLE_POLICY", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("FREE_POLICY", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("COUNTRY_ADMIN_POWER", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("COUNTRY_DIPLOMATIC_POWER", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("COUNTRY_MILITARY_POWER", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("PRESTIGE", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("PRESTIGE_DECAY", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("MONTHLY_SPLENDOR", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("YEARLY_CORRUPTION", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("ADVISOR_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("ADVISOR_POOL", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("FEMALE_ADVISOR_CHANCE", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("HEIR_CHANCE", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("MONARCH_ADMIN_POWER", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("MONARCH_DIPLOMATIC_POWER", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("MONARCH_MILITARY_POWER", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("ADM_ADVISOR_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("DIP_ADVISOR_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("MIL_ADVISOR_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("MONTHLY_SUPPORT_HEIR_GAIN", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("POWER_PROJECTION_FROM_INSULTS", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("YEARLY_ABSOLUTISM", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("MAX_ABSOLUTISM", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("LEGITIMACY", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("REPUBLICAN_TRADITION", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("DEVOTION", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("HORDE_UNITY", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("MERITOCRACY", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("MONTHLY_MILITARIZED_SOCIETY", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("YEARLY_TRIBAL_ALLEGIANCE", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("IMPERIAL_MANDATE", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("ELECTION_CYCLE", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("CANDIDATE_RANDOM_BONUS", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("REELECTION_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("REFORM_PROGRESS_GROWTH", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("GOVERNING_CAPACITY", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("GOVERNING_CAPACITY_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("STATE_GOVERNING_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("TRADE_COMPANY_GOVERNING_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("EXPAND_ADMINISTRATION_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("YEARLY_REVOLUTIONARY_ZEAL", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("MAX_REVOLUTIONARY_ZEAL", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("IMPERIAL_AUTHORITY", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("IMPERIAL_AUTHORITY_VALUE", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("FREE_CITY_IMPERIAL_AUTHORITY", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("IMPERIAL_MERCENARY_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("MAX_FREE_CITIES", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("MAX_ELECTORS", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("LEGITIMATE_SUBJECT_ELECTOR", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("CULTURE_CONVERSION_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("NUM_ACCEPTED_CULTURES", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("SAME_CULTURE_ADVISOR_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("PROMOTE_CULTURE_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("GLOBAL_UNREST", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("STABILITY_COST_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("GLOBAL_AUTONOMY", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("MIN_AUTONOMY", ModifierType.CONSTANT, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("AUTONOMY_CHANGE_TIME", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("HARSH_TREATMENT_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("YEARS_OF_NATIONALISM", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("MIN_AUTONOMY_IN_TERRITORIES", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("UNREST_CATHOLIC_PROVINCES", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("LIBERTY_DESIRE", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("LIBERTY_DESIRE_FROM_SUBJECT_DEVELOPMENT", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("REDUCED_LIBERTY_DESIRE", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("REDUCED_LIBERTY_DESIRE_ON_SAME_CONTINENT", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("SPY_OFFENCE", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("GLOBAL_SPY_DEFENCE", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("DISCOVERED_RELATIONS_IMPACT", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("REBEL_SUPPORT_EFFICIENCY", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("GLOBAL_MISSIONARY_STRENGTH", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("GLOBAL_HERETIC_MISSIONARY_STRENGTH", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("MISSIONARIES", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("MISSIONARY_MAINTENANCE_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("RELIGIOUS_UNITY", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("TOLERANCE_OWN", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("TOLERANCE_HERETIC", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("TOLERANCE_HEATHEN", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("PAPAL_INFLUENCE", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("CHURCH_POWER_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("MONTHLY_FERVOR_INCREASE", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("HARMONIZATION_SPEED", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("YEARLY_HARMONY", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("MONTHLY_PIETY", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("MONTHLY_KARMA", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("ENFORCE_RELIGION_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("PRESTIGE_PER_DEVELOPMENT_FROM_CONVERSION", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("WARSCORE_COST_VS_OTHER_RELIGION", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("ESTABLISH_ORDER_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("GLOBAL_RELIGIOUS_CONVERSION_RESISTANCE", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("RELATION_WITH_HERETICS", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("CURIA_TREASURY_CONTRIBUTION", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("CURIA_POWERS_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("YEARLY_PATRIARCH_AUTHORITY", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("CB_ON_RELIGIOUS_ENEMIES", ModifierType.CONSTANT, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("COLONISTS", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("COLONIST_PLACEMENT_CHANCE", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("GLOBAL_COLONIAL_GROWTH", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("RANGE", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("NATIVE_UPRISING_CHANCE", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("NATIVE_ASSIMILATION", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("MIGRATION_COOLDOWN", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("GLOBAL_TARIFFS", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("TREASURE_FLEET_INCOME", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("EXPEL_MINORITIES_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("CARAVAN_POWER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("MERCHANTS", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("PLACED_MERCHANT_POWER", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("GLOBAL_TRADE_POWER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("GLOBAL_FOREIGN_TRADE_POWER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("GLOBAL_OWN_TRADE_POWER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("GLOBAL_PROV_TRADE_POWER_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("GLOBAL_TRADE_GOODS_SIZE_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("TRADE_EFFICIENCY", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("TRADE_RANGE_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("TRADE_STEERING", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("GLOBAL_SHIP_TRADE_POWER", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("PRIVATEER_EFFICIENCY", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("EMBARGO_EFFICIENCY", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("SHIP_POWER_PROPAGATION", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("CENTER_OF_TRADE_UPGRADE_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("TRADE_COMPANY_INVESTMENT_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("MERCANTILISM_COST", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("INSTITUTION_GROWTH", ModifierType.ADDITIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("MAX_ATTRITION", ModifierType.ADDITIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("ATTRITION", ModifierType.ADDITIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("LOCAL_HOSTILE_ATTRITION", ModifierType.ADDITIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("FORT_LEVEL", ModifierType.ADDITIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("GARRISON_GROWTH", ModifierType.MULTIPLICATIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("LOCAL_DEFENSIVENESS", ModifierType.MULTIPLICATIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("LOCAL_FRIENDLY_MOVEMENT_SPEED", ModifierType.MULTIPLICATIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("LOCAL_HOSTILE_MOVEMENT_SPEED", ModifierType.MULTIPLICATIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("LOCAL_MANPOWER", ModifierType.ADDITIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("LOCAL_MANPOWER_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("LOCAL_REGIMENT_COST", ModifierType.MULTIPLICATIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("REGIMENT_RECRUIT_SPEED", ModifierType.MULTIPLICATIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("SUPPLY_LIMIT", ModifierType.ADDITIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("SUPPLY_LIMIT_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("LOCAL_AMOUNT_OF_BANNERS", ModifierType.MULTIPLICATIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("LOCAL_NAVAL_ENGAGEMENT_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("LOCAL_SAILORS", ModifierType.ADDITIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("LOCAL_SAILORS_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("LOCAL_SHIP_COST", ModifierType.MULTIPLICATIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("LOCAL_SHIP_REPAIR", ModifierType.MULTIPLICATIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("SHIP_RECRUIT_SPEED", ModifierType.MULTIPLICATIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("BLOCKADE_FORCE_REQUIRED", ModifierType.MULTIPLICATIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("HOSTILE_DISEMBARK_SPEED", ModifierType.MULTIPLICATIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("HOSTILE_FLEET_ATTRITION", ModifierType.MULTIPLICATIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("LOCAL_COLONIAL_GROWTH", ModifierType.ADDITIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("LOCAL_COLONIST_PLACEMENT_CHANCE", ModifierType.ADDITIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("INFLATION_REDUCTION_LOCAL", ModifierType.ADDITIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("LOCAL_STATE_MAINTENANCE_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("LOCAL_BUILD_COST", ModifierType.MULTIPLICATIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("LOCAL_BUILD_TIME", ModifierType.MULTIPLICATIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("LOCAL_MONTHLY_DEVASTATION", ModifierType.ADDITIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("LOCAL_PRODUCTION_EFFICIENCY", ModifierType.MULTIPLICATIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("LOCAL_TAX_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("TAX_INCOME", ModifierType.ADDITIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("ALLOWED_NUM_OF_BUILDINGS", ModifierType.ADDITIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("LOCAL_DEVELOPMENT_COST", ModifierType.MULTIPLICATIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("LOCAL_INSTITUTION_SPREAD", ModifierType.MULTIPLICATIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("LOCAL_CORE_CREATION", ModifierType.MULTIPLICATIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("LOCAL_GOVERNING_COST", ModifierType.MULTIPLICATIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("STATEWIDE_GOVERNING_COST", ModifierType.MULTIPLICATIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("PROVINCE_TRADE_POWER_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("PROVINCE_TRADE_POWER_VALUE", ModifierType.ADDITIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("TRADE_GOODS_SIZE_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("TRADE_GOODS_SIZE", ModifierType.ADDITIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("TRADE_VALUE_MODIFIER", ModifierType.MULTIPLICATIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("TRADE_VALUE", ModifierType.ADDITIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("LOCAL_MISSIONARY_STRENGTH", ModifierType.ADDITIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("LOCAL_RELIGIOUS_UNITY_CONTRIBUTION", ModifierType.ADDITIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("LOCAL_MISSIONARY_MAINTENANCE_COST", ModifierType.ADDITIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("LOCAL_RELIGIOUS_CONVERSION_RESISTANCE", ModifierType.ADDITIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("LOCAL_CULTURE_CONVERSION_COST", ModifierType.MULTIPLICATIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("LOCAL_UNREST", ModifierType.ADDITIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("LOCAL_AUTONOMY", ModifierType.ADDITIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("LOCAL_YEARS_OF_NATIONALISM", ModifierType.ADDITIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("MIN_LOCAL_AUTONOMY", ModifierType.CONSTANT, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("ATTACK_BONUS_IN_CAPITAL_TERRAIN", ModifierType.CONSTANT, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("CAN_BYPASS_FORTS", ModifierType.CONSTANT, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("CAN_CHAIN_CLAIM", ModifierType.CONSTANT, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("CAN_COLONY_BOOST_DEVELOPMENT", ModifierType.CONSTANT, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("CAN_TRANSFER_VASSAL_WARGOAL", ModifierType.CONSTANT, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("FORCE_MARCH_FREE", ModifierType.CONSTANT, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("FREE_MAINTENANCE_ON_EXPL_CONQ", ModifierType.CONSTANT, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("IGNORE_CORING_DISTANCE", ModifierType.CONSTANT, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("CB_ON_GOVERNMENT_ENEMIES", ModifierType.CONSTANT, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("CB_ON_PRIMITIVES", ModifierType.CONSTANT, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("CB_ON_OVERSEAS", ModifierType.CONSTANT, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("IDEA_CLAIM_COLONIES", ModifierType.CONSTANT, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("MAY_EXPLORE", ModifierType.CONSTANT, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("NO_RELIGION_PENALTY", ModifierType.CONSTANT, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("REDUCED_STAB_IMPACTS", ModifierType.CONSTANT, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("SEA_REPAIR", ModifierType.CONSTANT, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("MAY_ESTABLISH_FRONTIER", ModifierType.CONSTANT, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("EXTRA_MANPOWER_AT_RELIGIOUS_WAR", ModifierType.CONSTANT, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("AUTO_EXPLORE_ADJACENT_TO_COLONY", ModifierType.CONSTANT, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("CAN_FABRICATE_FOR_VASSALS", ModifierType.CONSTANT, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("HAS_BANNERS", ModifierType.CONSTANT, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("LOCAL_HAS_BANNERS", ModifierType.CONSTANT, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("MAY_BUILD_SUPPLY_DEPOT", ModifierType.CONSTANT, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("MAY_REFILL_GARRISON", ModifierType.CONSTANT, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("MAY_RETURN_MANPOWER_ON_DISBAND", ModifierType.CONSTANT, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("yearly_karma_decay", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("disengagement_chance", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("local_heir_adm", ModifierType.ADDITIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("local_heir_dip", ModifierType.ADDITIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("local_heir_mil", ModifierType.ADDITIVE, ModifierScope.PROVINCE);
        ModifiersUtils.addModifier("imperial_reform_catholic_approval", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("governing_cost", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("appoint_cardinal_cost", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("papal_influence_from_cardinals", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("tribal_despotism_legacy", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("tribal_federation_legacy", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("allowed_idea_groups", ModifierType.CONSTANT, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("development_efficiency", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("trade_range", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("naval_maintenance", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("combat_width", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("military_tactics", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("maneuver_value", ModifierType.ADDITIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("sprite_level", ModifierType.CONSTANT, ModifierScope.COUNTRY); //Sets the graphical sprite level to use
        ModifiersUtils.addModifier("easy_war_chance_multiplier", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("war_chance_multiplier", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("nation_designer_cost", ModifierType.CONSTANT, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("gift_chance", ModifierType.ADDITIVE, ModifierScope.COUNTRY); //Ai
        ModifiersUtils.addModifier("alliance_acceptance", ModifierType.ADDITIVE, ModifierScope.COUNTRY); //Ai
        ModifiersUtils.addModifier("other_ai_help_us_multiplier", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY); //Ai
        ModifiersUtils.addModifier("royal_marriage_acceptance", ModifierType.ADDITIVE, ModifierScope.COUNTRY); //Ai
        ModifiersUtils.addModifier("different_religion_war_multiplier", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY); //Ai
        ModifiersUtils.addModifier("building_budget_multiplier", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY); //Ai
        ModifiersUtils.addModifier("accept_vassalization_reasons", ModifierType.ADDITIVE, ModifierScope.COUNTRY); //Ai
        ModifiersUtils.addModifier("heretic_ally_acceptance", ModifierType.ADDITIVE, ModifierScope.COUNTRY); //Ai
        ModifiersUtils.addModifier("heathen_ally_acceptance", ModifierType.ADDITIVE, ModifierScope.COUNTRY); //Ai
        ModifiersUtils.addModifier("trade_league_acceptance", ModifierType.ADDITIVE, ModifierScope.COUNTRY); //Ai
        ModifiersUtils.addModifier("other_ai_peace_term_bonus", ModifierType.ADDITIVE, ModifierScope.COUNTRY); //Ai
        ModifiersUtils.addModifier("peace_desire", ModifierType.ADDITIVE, ModifierScope.COUNTRY); //Ai
        ModifiersUtils.addModifier("send_warning_desire", ModifierType.ADDITIVE, ModifierScope.COUNTRY); //Ai
        ModifiersUtils.addModifier("other_ai_war_chance_multiplier", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY); //Ai
        ModifiersUtils.addModifier("enemy_strength_multiplier", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY); //Ai
        ModifiersUtils.addModifier("custom_ai_explanation", ModifierType.CONSTANT, ModifierScope.COUNTRY); //No sense here used as tooltip in modifiers
        ModifiersUtils.addModifier("expire_message_type", ModifierType.CONSTANT, ModifierScope.COUNTRY); //No sense here used as tooltip in modifiers
        ModifiersUtils.addModifier("picture", ModifierType.CONSTANT, ModifierScope.COUNTRY); //No sense here used as tooltip in modifiers
        ModifiersUtils.addModifier("duration", ModifierType.CONSTANT, ModifierScope.COUNTRY); //No sense here used as tooltip in modifiers
        ModifiersUtils.addModifier("key", ModifierType.CONSTANT, ModifierScope.COUNTRY); //No sense here used as tooltip in modifiers
        ModifiersUtils.addModifier("immortal", ModifierType.CONSTANT, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("monarch_lifespan", ModifierType.MULTIPLICATIVE, ModifierScope.COUNTRY);
        ModifiersUtils.addModifier("power_modifier", ModifierType.MULTIPLICATIVE, ModifierScope.PROVINCE);
    }

    public static void addModifier(String name, ModifierType type, ModifierScope scope) {
        Modifier modifier = new Modifier(name, type, scope);
        MODIFIERS_MAP.put(modifier.getName(), modifier);
    }

    public static Modifier getModifier(ClausewitzVariable variable) {
        return getModifier(variable.getName());
    }

    public static Modifier getModifier(String name) {
        Modifier modifier = MODIFIERS_MAP.get(ClausewitzUtils.removeQuotes(name.toLowerCase()));

        if (modifier == null) {
            LOGGER.warn("Could not find modifier: {} !", name);
        }

        return modifier;
    }

    /**
     * For constants (i.e. = yes) return 1 for yes, 0 for false
     */
    public static double getSum(double value, Modifier modifier, Modifiers... modifiers) {

        switch (modifier.getType()) {
            case ADDITIVE:
                return value + Arrays.stream(modifiers)
                                     .map(m -> m.getModifier(modifier))
                                     .filter(Objects::nonNull)
                                     .map(NumbersUtils::toDouble)
                                     .filter(Objects::nonNull)
                                     .mapToDouble(Double::doubleValue)
                                     .sum();
            case MULTIPLICATIVE:
                return value * (1 + Arrays.stream(modifiers)
                                          .map(m -> m.getModifier(modifier))
                                          .filter(Objects::nonNull)
                                          .map(NumbersUtils::toDouble)
                                          .filter(Objects::nonNull)
                                          .mapToDouble(Double::doubleValue)
                                          .sum());
            case CONSTANT:
                return (value == 1 || Arrays.stream(modifiers).map(m -> m.getModifier(modifier)).filter(Objects::nonNull).anyMatch("yes"::equalsIgnoreCase)) ? 1
                                                                                                                                                             : 0;
            default:
                return value;
        }
    }

    public static Modifiers scaleModifiers(Modifiers modifiers, Number scale) {
        scale = NumbersUtils.numberOrDefault(scale);
        double finalScale = scale.doubleValue();
        Modifiers toReturn = Modifiers.copy(modifiers);

        toReturn.getModifiers().replaceAll((key, value) -> {
            if (ModifierType.ADDITIVE.equals(key.getType()) || ModifierType.MULTIPLICATIVE.equals(key.getType())) {
                return ClausewitzUtils.doubleToString(BigDecimal.valueOf(NumbersUtils.toDouble(value)).multiply(BigDecimal.valueOf(finalScale)).doubleValue());
            }

            return value;
        });

        return toReturn;
    }

    public static Modifiers sumModifiers(Modifiers... modifiers) {
        if (modifiers.length > 0) {
            Set<String> enables = Arrays.stream(modifiers).map(Modifiers::getEnables).flatMap(Collection::stream).collect(Collectors.toSet());
            Map<Modifier, String> modifier = Arrays.stream(modifiers)
                                                   .map(Modifiers::getModifiers)
                                                   .map(Map::entrySet)
                                                   .flatMap(Collection::stream)
                                                   .collect(Collectors.groupingBy(Map.Entry::getKey,
                                                                                  Collectors.mapping(Map.Entry::getValue, Collectors.toList())))
                                                   .entrySet()
                                                   .stream()
                                                   .filter(entry -> CollectionUtils.isNotEmpty(entry.getValue()))
                                                   .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                                                       switch (entry.getKey().getType()) {
                                                           case ADDITIVE:
                                                           case MULTIPLICATIVE:
                                                               return ClausewitzUtils.doubleToString(entry.getValue()
                                                                                                          .stream()
                                                                                                          .filter(Objects::nonNull)
                                                                                                          .map(NumbersUtils::toDouble)
                                                                                                          .filter(Objects::nonNull)
                                                                                                          .mapToDouble(Double::doubleValue)
                                                                                                          .sum());
                                                           case CONSTANT:
                                                               return entry.getValue().stream().filter(Objects::nonNull).anyMatch("yes"::equalsIgnoreCase)
                                                                      ? "yes"
                                                                      : "no";
                                                       }

                                                       return entry.getValue().get(0);
                                                   }));

            return new Modifiers(enables, modifier);
        }

        return new Modifiers();
    }

    public static void sumModifiers(String name, String value, Modifiers modifiers) {
        sumModifiers(ModifiersUtils.getModifier(name), value, modifiers);
    }

    public static void sumModifiers(Modifier modifier, String value, Modifiers modifiers) {
        switch (modifier.getType()) {
            case ADDITIVE:
            case MULTIPLICATIVE:
                modifiers.getModifiers()
                         .put(modifier, ClausewitzUtils.doubleToString(NumbersUtils.toDouble(modifiers.getModifiers().getOrDefault(modifier, "0"))
                                                                       + NumbersUtils.doubleOrDefault(NumbersUtils.toDouble(value))));
                break;
            case CONSTANT:
                modifiers.getModifiers()
                         .put(modifier,
                              ("yes".equalsIgnoreCase(modifiers.getModifiers().getOrDefault(modifier, value)) || "yes".equalsIgnoreCase(value)) ? "yes" : "no");
                break;
        }
    }

    public static Modifiers scaleTax(SaveProvince province, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, province.getBaseTax());
    }

    public static Modifiers scaleProd(SaveProvince province, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, province.getBaseProduction());
    }

    public static Modifiers scaleManpower(SaveProvince province, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, province.getBaseManpower());
    }

    public static Modifiers scaleDev(SaveProvince province, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, province.getDevelopment());
    }

    public static Modifiers scaleDevImprove(SaveProvince province, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, province.getTotalImproveCount());
    }

    public static Modifiers scaleCountryDev(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, country.getDevelopment());
    }

    public static Modifiers scaleColonySize100(SaveProvince province, Modifiers modifiers) {
        double colonySize = NumbersUtils.doubleOrDefault(province.getColonySize()) % 100;

        return ModifiersUtils.scaleModifiers(modifiers, colonySize);
    }

    public static Modifiers scaleUnrest(SaveProvince province, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, province.getUnrest());
    }

    public static Modifiers scaleDevastation(SaveProvince province, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, province.getDevastation());
    }

    public static Modifiers scaleNativeSize(SaveProvince province, Modifiers modifiers) {
        Integer nativeSize = NumbersUtils.intOrDefault(province.getNativeSize()) / 10;
        return ModifiersUtils.scaleModifiers(modifiers, nativeSize);
    }

    public static Modifiers scaleNativeHostileness(SaveProvince province, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, province.getNativeHostileness());
    }

    public static Modifiers scaleNationalism(SaveProvince province, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, province.getNationalism());
    }

    public static Modifiers scaleAutonomy(SaveProvince province, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, province.getLocalAutonomy());
    }

    public static Modifiers scalePatriarchAuthority(SaveProvince province, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, NumbersUtils.doubleOrDefault(province.getOwner().getPatriarchAuthority()));
    }

    public static Modifiers scaleFriendlyRegimentMax20(SaveProvince province, Modifiers modifiers) {
        int nbRegiments = province.getArmies()
                                  .stream()
                                  .map(army -> BigDecimal.valueOf(army.getRegiments().size())
                                                         .multiply(BigDecimal.valueOf(NumbersUtils.doubleOrDefault(army.getCountry().getLandMaintenance()))))
                                  .mapToInt(BigDecimal::intValue)
                                  .sum();
        nbRegiments = Math.min(nbRegiments, 20);

        return ModifiersUtils.scaleModifiers(modifiers, nbRegiments);
    }

    public static Modifiers scaleOccupiedImperial(Country country, Modifiers modifiers) {
        List<SaveProvince> provinces = country.getOwnedProvinces();
        provinces.retainAll(country.getCoreProvinces());
        provinces.removeIf(Predicate.not(SaveProvince::inHre));

        return ModifiersUtils.scaleModifiers(modifiers, provinces.size());
    }

    public static Modifiers scalePatriarchAuthority(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, NumbersUtils.doubleOrDefault(country.getPatriarchAuthority()));
    }

    public static Modifiers scaleWithStability(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, NumbersUtils.intOrDefault(country.getStability()));
    }

    public static Modifiers scaleWithInflation(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, NumbersUtils.doubleOrDefault(country.getInflation()));
    }

    public static Modifiers scaleWithCallForPeace(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, NumbersUtils.doubleOrDefault(country.getCallForPeace()));
    }

    public static Modifiers scaleWithWarExhaustion(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, NumbersUtils.doubleOrDefault(country.getWarExhaustion()));
    }

    public static Modifiers scaleWithDoom(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, NumbersUtils.doubleOrDefault(country.getDoom()));
    }

    public static Modifiers scaleWithAuthority(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, NumbersUtils.doubleOrDefault(country.getAuthority()));
    }

    public static Modifiers scaleWithTradeRefusal(Country country, Modifiers modifiers) {
        int tradeRefusal = (int) country.getTradeEmbargoes().stream().filter(c -> !country.getRivals().containsKey(c.getTag())).count();
        return ModifiersUtils.scaleModifiers(modifiers, NumbersUtils.intOrDefault(tradeRefusal));
    }

    public static Modifiers scaleWithMercantilism(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, NumbersUtils.intOrDefault(country.getMercantilism()));
    }

    public static Modifiers scaleWithArmyTradition(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, NumbersUtils.doubleOrDefault(country.getArmyTradition()));
    }

    public static Modifiers scaleWithNavyTradition(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, NumbersUtils.doubleOrDefault(country.getNavyTradition()));
    }

    public static Modifiers scaleWithFreeCitiesInHre(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, country.getSave().getCountries()
                                                               .values()
                                                               .stream()
                                                               .filter(c -> c.getGovernment() != null)
                                                               .filter(c -> CollectionUtils.isNotEmpty(c.getGovernment().getReforms()))
                                                               .filter(c -> c.getGovernment()
                                                                             .getReforms()
                                                                             .stream()
                                                                             .anyMatch(GovernmentReform::isFreeCity))
                                                               .count());
    }

    public static Modifiers scaleWithNumOfRoyalMarriages(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, NumbersUtils.intOrDefault(country.getNumOfRoyalMarriages()));
    }

    public static Modifiers scaleWithNumOfProvinces(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, NumbersUtils.intOrDefault(country.getOwnedProvinces().size()));
    }

    public static Modifiers scaleWithTribalAllegiance(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, NumbersUtils.doubleOrDefault(country.getTribalAllegiance()));
    }

    public static Modifiers scaleWithLegitimacy50(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, (NumbersUtils.doubleOrDefault(country.getLegitimacy()) - 50) / 100);
    }

    public static Modifiers scaleWithHordeUnity50(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, (NumbersUtils.doubleOrDefault(country.getHordeUnity()) - 50) / 100);
    }

    public static Modifiers scaleWithDevotion50(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, (NumbersUtils.doubleOrDefault(country.getDevotion()) - 50) / 100);
    }

    public static Modifiers scaleWithMeritocracy50(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, (NumbersUtils.doubleOrDefault(country.getMeritocracy()) - 50) / 100);
    }

    public static Modifiers scaleWithMeritocracy50Reverse(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, (50 - NumbersUtils.doubleOrDefault(country.getMeritocracy())) / 100);
    }

    public static Modifiers scaleWithCorruption(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, NumbersUtils.doubleOrDefault(country.getCorruption()));
    }

    public static Modifiers scaleWithRootOutCorruption(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, NumbersUtils.doubleOrDefault(country.getRootOutCorruptionSlider()));
    }

    public static Modifiers scaleWithRecoveryMotivation(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, NumbersUtils.doubleOrDefault(country.getRecoveryMotivation()));
    }

    public static Modifiers scaleWithMilitarisedSociety(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, NumbersUtils.doubleOrDefault(country.getMilitarisedSociety()) / 100);
    }

    public static Modifiers scaleWithOverextension(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, NumbersUtils.doubleOrDefault(country.getOverextensionPercentage()));
    }

    public static Modifiers scaleWithPrestige(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, NumbersUtils.doubleOrDefault(country.getPrestige()) / 100);
    }

    public static Modifiers scaleWithRepublicanTradition(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, NumbersUtils.doubleOrDefault(country.getRepublicanTradition()) / 100);
    }

    public static Modifiers scaleWithRepublicanTraditionReverse(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, (100 - NumbersUtils.doubleOrDefault(country.getRepublicanTradition())) / 100);
    }

    public static Modifiers scaleWithReligiousUnity(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, NumbersUtils.doubleOrDefault(country.getReligiousUnity()));
    }

    public static Modifiers scaleWithReligiousUnityReverse(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, 1 - NumbersUtils.doubleOrDefault(country.getReligiousUnity()));
    }

    public static Modifiers scaleWithOccupiedProvinces(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, country.getOwnedProvinces()
                                                               .stream()
                                                               .filter(province -> !province.getOwner().equals(province.getController()))
                                                               .count());
    }

    public static Modifiers scaleWithBlockadedProvinces(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, country.getOwnedProvinces()
                                                               .stream()
                                                               .filter(province -> NumbersUtils.doubleOrDefault(province.getBlockadeEfficiency()) >= 0)
                                                               .count());
    }

    public static Modifiers scaleWithNotControlledCores(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, country.getCoreProvinces().stream().filter(province -> !province.getOwner().equals(country)).count());
    }

    public static Modifiers scaleWithNumOfAgeObjectives(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, NumbersUtils.intOrDefault(country.getNumOfAgeObjectives()));
    }

    public static Modifiers scaleWithOverlordAdm(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, (Math.max(0, country.getTech().getAdm() - country.getOverlord().getTech().getAdm())));
    }

    public static Modifiers scaleWithOverlordDip(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, (Math.max(0, country.getTech().getDip() - country.getOverlord().getTech().getDip())));
    }

    public static Modifiers scaleWithOverlordMil(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, (Math.max(0, country.getTech().getMil() - country.getOverlord().getTech().getMil())));
    }

    public static Modifiers scaleWithLibertyDesire(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, NumbersUtils.doubleOrDefault(country.getLibertyDesire()) / 100);
    }

    public static Modifiers scaleWithAbsolutism(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, NumbersUtils.intOrDefault(country.getAbsolutism()) / 100);
    }

    public static Modifiers scaleWithCurrentPowerProjection(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, NumbersUtils.doubleOrDefault(country.getCurrentPowerProjection()) / 100);
    }

    public static Modifiers scaleWithStrongCompany(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, country.getNumOfStrongCompanies());
    }

    public static Modifiers scaleWithLargeColony(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, country.getNumOfLargeColonies());
    }

    public static Modifiers scaleWithVassals(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, country.getNumOfSubjectsOfType(Eu4Utils.SUBJECT_TYPE_CLIENT_VASSAL));
    }

    public static Modifiers scaleWithMarches(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, country.getNumOfSubjectsOfType(Eu4Utils.SUBJECT_TYPE_MARCH));
    }

    public static Modifiers scaleWithUnions(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, country.getNumOfSubjectsOfType(Eu4Utils.SUBJECT_TYPE_PERSONAL_UNION));
    }

    public static Modifiers scaleWithDaimyos(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, country.getNumOfSubjectsOfType(Eu4Utils.SUBJECT_TYPE_DAIMYO_VASSAL));
    }

    public static Modifiers scaleWithInnovativeness(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, NumbersUtils.doubleOrDefault(country.getInnovativeness()) / 100);
    }

    public static Modifiers scaleWithNumExpandedAdministration(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, NumbersUtils.intOrDefault(country.getNumExpandedAdministration()));
    }

    public static Modifiers scaleWithNumTradeLeagueMembers(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, country.getTradeLeague().getMembers().size());
    }

    public static Modifiers scaleWithHarmony(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, NumbersUtils.doubleOrDefault(country.getHarmony()));
    }

    public static Modifiers scaleWithHarmonyReverse(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, 1 - (NumbersUtils.doubleOrDefault(country.getHarmony()) / 100));
    }

    public static Modifiers scaleWithDaimyosAtPeace(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, Math.min(10, country.getSubjects()
                                                                            .stream()
                                                                            .filter(subject -> !subject.isAtWar() &&
                                                                                               Eu4Utils.SUBJECT_TYPE_DAIMYO_VASSAL.equalsIgnoreCase(
                                                                                                       subject.getSubjectType().getName()))
                                                                            .count()));
    }

    public static Modifiers scaleWithDaimyosSameIsolationism(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, Math.min(10, country.getSubjects()
                                                                            .stream()
                                                                            .filter(subject ->
                                                                                            Eu4Utils.SUBJECT_TYPE_DAIMYO_VASSAL.equalsIgnoreCase(
                                                                                                    subject.getSubjectType().getName())
                                                                                            &&
                                                                                            NumbersUtils.intOrDefault(country.getIsolationismLevel())
                                                                                            == NumbersUtils.intOrDefault(subject.getIsolationismLevel()))
                                                                            .count()));
    }

    public static Modifiers scaleWithDaimyosDifferentIsolationism(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, Math.min(10, country.getSubjects()
                                                                            .stream()
                                                                            .filter(subject ->
                                                                                            Eu4Utils.SUBJECT_TYPE_DAIMYO_VASSAL.equalsIgnoreCase(
                                                                                                    subject.getSubjectType().getName())
                                                                                            &&
                                                                                            NumbersUtils.intOrDefault(country.getIsolationismLevel())
                                                                                            != NumbersUtils.intOrDefault(subject.getIsolationismLevel()))
                                                                            .count()));
    }

    public static Modifiers scaleWithDaimyosSwordHunt(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, country.getSubjects()
                                                               .stream()
                                                               .filter(subject -> Eu4Utils.SUBJECT_TYPE_DAIMYO_VASSAL.equalsIgnoreCase(
                                                                       subject.getSubjectType().getName())
                                                                                  && subject.getModifiers().stream()
                                                                                            .anyMatch(modifier -> "subject_sword_hunt".equalsIgnoreCase(
                                                                                                    modifier.getModifierName())))
                                                               .count());
    }

    public static Modifiers scaleWithStreltsyPercent(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, BigDecimal.valueOf(country.getNbRegimentOfCategory(3))
                                                                  .divide(BigDecimal.valueOf(country.getArmySize()), 0, RoundingMode.HALF_EVEN));
    }

    public static Modifiers scaleWithCossacksPercent(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, BigDecimal.valueOf(country.getNbRegimentOfCategory(4))
                                                                  .divide(BigDecimal.valueOf(country.getArmySize()), 0, RoundingMode.HALF_EVEN));
    }

    public static Modifiers scaleWithLowProfessionalism(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, Math.min(Math.max(0, NumbersUtils.doubleOrDefault(country.getArmyProfessionalism()) -
                                                                             country.getSave().getGame().getLowArmyProfessionalismMinRange()),
                                                                 country.getSave().getGame().getLowArmyProfessionalismMaxRange()));
    }

    public static Modifiers scaleWithHighProfessionalism(Country country, Modifiers modifiers) {
        return ModifiersUtils.scaleModifiers(modifiers, Math.min(Math.max(0, NumbersUtils.doubleOrDefault(country.getArmyProfessionalism()) -
                                                                             country.getSave().getGame().getHighArmyProfessionalismMinRange()),
                                                                 country.getSave().getGame().getHighArmyProfessionalismMaxRange()));
    }
}
