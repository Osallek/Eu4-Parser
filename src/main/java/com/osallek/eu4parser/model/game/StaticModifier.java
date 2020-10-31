package com.osallek.eu4parser.model.game;

import com.osallek.eu4parser.common.Eu4Utils;
import com.osallek.eu4parser.common.ModifiersUtils;
import com.osallek.eu4parser.common.NumbersUtils;
import com.osallek.eu4parser.model.save.country.Country;
import com.osallek.eu4parser.model.save.gameplayoptions.Difficulty;
import com.osallek.eu4parser.model.save.province.SaveProvince;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public enum StaticModifier {
    DIFFICULTY_VERY_EASY_PLAYER(new Condition(Pair.of("ai", "no"), Pair.of("difficulty", Difficulty.VERY_EASY.name())), null, null),
    DIFFICULTY_EASY_PLAYER(new Condition(Pair.of("ai", "no"), Pair.of("difficulty", Difficulty.EASY.name())), null, null),
    DIFFICULTY_NORMAL_PLAYER(new Condition(Pair.of("ai", "no"), Pair.of("difficulty", Difficulty.NORMAL.name())), null, null),
    DIFFICULTY_HARD_PLAYER(new Condition(Pair.of("ai", "no"), Pair.of("difficulty", Difficulty.HARD.name())), null, null),
    DIFFICULTY_VERY_HARD_PLAYER(new Condition(Pair.of("ai", "no"), Pair.of("difficulty", Difficulty.VERY_HARD.name())), null, null),
    DIFFICULTY_VERY_EASY_AI(new Condition(Pair.of("ai", "yes"), Pair.of("difficulty", Difficulty.VERY_EASY.name())), null, null),
    DIFFICULTY_EASY_AI(new Condition(Pair.of("ai", "yes"), Pair.of("difficulty", Difficulty.EASY.name())), null, null),
    DIFFICULTY_NORMAL_AI(new Condition(Pair.of("ai", "yes"), Pair.of("difficulty", Difficulty.NORMAL.name())), null, null),
    DIFFICULTY_HARD_AI(new Condition(Pair.of("ai", "yes"), Pair.of("difficulty", Difficulty.HARD.name())), null, null),
    DIFFICULTY_VERY_HARD_AI(new Condition(Pair.of("ai", "yes"), Pair.of("difficulty", Difficulty.VERY_HARD.name())), null, null),
    CITY(new Condition(Pair.of("is_city", "yes")), null, null),
    PORT(new Condition(Pair.of("has_port", "yes")), null, null),
    IN_STATE(new Condition(Pair.of("is_state", "yes")), null, null),
    IN_CAPITAL_STATE(new Condition(Pair.of("in_capital_state", "yes")), null, null),
    COASTAL(new Condition(Pair.of("is_ocean", "yes")), null, null), //Todo sea province with coast adjacent
    SEAT_IN_PARLIAMENT(new Condition(Pair.of("has_seat_in_parliament", "yes")), null, null),
    NON_COASTAL(new Condition(Pair.of("is_ocean", "no")), null, null), //Todo sea province without coast adjacent
    COASTAL_SEA(new Condition(Pair.of("is_ocean", "yes")), null, null), //Todo sea province with coast adjacent
    TROPICAL(new Condition(Pair.of("has_climate", "tropical")), null, null),
    ARCTIC(new Condition(Pair.of("has_climate", "arctic")), null, null),
    ARID(new Condition(Pair.of("has_climate", "arid")), null, null),
    SEA_ZONE(new Condition(Pair.of("is_ocean", "yes")), null, null), //Todo sea province with a port in it
    LAND_PROVINCE(new Condition(Pair.of("is_ocean", "no"), Pair.of("is_lake", "no"), Pair.of("is_wasteland", "no")), null, null),
    MILD_WINTER(new Condition(Pair.of("has_winter", "mild_winter")), null, null),
    NORMAL_WINTER(new Condition(Pair.of("has_winter", "normal_winter")), null, null),
    SEVERE_WINTER(new Condition(Pair.of("has_winter", "severe_winter")), null, null),
    MILD_MONSOON(new Condition(Pair.of("has_monsoon", "mild_monsoon")), null, null),
    NORMAL_MONSOON(new Condition(Pair.of("has_monsoon", "normal_monsoon")), null, null),
    SEVERE_MONSOON(new Condition(Pair.of("has_monsoon", "severe_monsoon")), null, null),
    BLOCKADED(new Condition(Pair.of("is_blockaded", "yes")), null, null),
    NO_ADJACENT_CONTROLLED(new Condition(Pair.of("always", "no")), null, null), //Todo
    PROVINCIAL_TAX_INCOME(new Condition(Pair.of("is_ocean", "no"), Pair.of("is_lake", "no"), Pair.of("is_wasteland", "no")), null,
                          StaticModifier::scaleTax),
    PROVINCIAL_PRODUCTION_SIZE(new Condition(Pair.of("is_ocean", "no"), Pair.of("is_lake", "no"), Pair.of("is_wasteland", "no")), null,
                               StaticModifier::scaleProd),
    KNOWLEDGE_SHARING(new Condition(Pair.of("knowledge_sharing", "yes")), null, null),
    CARDINALS_SPREAD_INSTITUTION(new Condition(Pair.of("has_cardinal", "yes")), null, null),
    MANPOWER(new Condition(Pair.of("is_ocean", "no"), Pair.of("is_lake", "no"), Pair.of("is_wasteland", "no")), null,
             StaticModifier::scaleManpower),
    SAILORS(new Condition(Pair.of("is_ocean", "no"), Pair.of("is_lake", "no"), Pair.of("is_wasteland", "no")), null, StaticModifier::scaleManpower),
    HORDE_DEVELOPMENT(new Condition(Pair.of("nomad_development_scale", "yes")), null, null),
    PROVINCE_RAZED(new Condition(Pair.of("has_province_modifier", "province_razed")), null, null),
    DEVELOPMENT(new Condition(Pair.of("always", "true")), null, StaticModifier::scaleDev),
    DEVELOPMENT_SCALED(new Condition(Pair.of("always", "true")), null, StaticModifier::scaleDevImprove),
    CAPITAL_CITY(new Condition(Pair.of("capital", "yes")), null, null),
    PATRIARCH_STATE(new Condition(Pair.of("has_state_patriach", "yes")), null, null),
    PATRIARCH_AUTHORITY_LOCAL(new Condition(Pair.of("has_owner_religion", "yes"), Pair.of("owner_has_patriarchs", "yes")), null,
                              StaticModifier::scalePatriarchAuthority),
    PATRIARCH_AUTHORITY_GLOBAL(new Condition(Pair.of("has_patriarchs", "yes")), StaticModifier::scalePatriarchAuthority, null),
    PASHA_STATE(new Condition(Pair.of("has_state_pasha", "yes")), null, null),
    COLONY_LEVEL(new Condition(Pair.of("is_colony", "yes")), null, StaticModifier::scaleColonySize100),
    NATIVE_ASSIMILATION(new Condition(Pair.of("is_colony", "yes")), null, StaticModifier::scaleNativeSize), //Fixme modifier
    NATIVE_AGGRESSIVENESS(new Condition(Pair.of("is_colony", "yes")), null, StaticModifier::scaleNativeHostileness),
    CORE(new Condition(Pair.of("is_state_core", "yes")), null, null),
    NON_CORE(new Condition(Pair.of("is_cored", "no"), Pair.of("is_state", "yes")), null, null),
    TERRITORY_CORE(new Condition(Pair.of("is_territorial_core", "yes")), null, null),
    TERRITORY_NON_CORE(new Condition(Pair.of("is_territory", "yes"), Pair.of("is_cored", "no")), null, null),
    MARCH_BONUS(new Condition(Pair.of("is_march", "yes")), null, null),
    SAME_CULTURE_GROUP(new Condition(Pair.of("has_owner_accepted_culture", "no"), Pair.of("has_owner_culture_group", "yes")), null, null),
    NON_ACCEPTED_CULTURE(new Condition(Pair.of("has_owner_accepted_culture", "no")), null, null),
    ACCEPTED_CULTURE_DEMOTED(new Condition(Pair.of("has_province_modifier", "accepted_culture_demoted")), null, null),
    NON_ACCEPTED_CULTURE_REPUBLIC(new Condition(Pair.of("non_accepted_culture_republic", "no")), null, null),
    OCCUPIED(new Condition(Pair.of("is_occupied", "yes")), null, null),
    UNDER_SIEGE(new Condition(Pair.of("unit_in_siege", "yes")), null, null),
    DEVASTATION(new Condition(Pair.of("has_devastation", "yes")), null, StaticModifier::scaleDevastation),
    PROSPERITY(new Condition(Pair.of("is_prosperous", "yes")), null, null),
    SLAVES_RAIDED(new Condition(Pair.of("has_province_modifier", "slaves_raided")), null, null),
    //    TOLERANCE(new Condition(Pair.of("always", "yes")), null, applyToProvince), //Fixme //Scale with 1 tolerance
    //    INTOLERANCE(new Condition(Pair.of("always", "yes")), null, applyToProvince), //Fixme //Scale with -1 tolerance
    UNREST(new Condition(Pair.of("always", "yes")), null, StaticModifier::scaleUnrest),
    NATIONALISM(new Condition(Pair.of("always", "yes")), null, StaticModifier::scaleNationalism),
    HARSH_TREATMENT(new Condition(Pair.of("always", "yes")), null, null), //Fixme Don't know how to parse
    LOCAL_AUTONOMY_MULTIPLICATIVE(new Condition(Pair.of("is_owned_by_trade_company", "no")), null, StaticModifier::scaleAutonomy),
    LOCAL_AUTONOMY(new Condition(Pair.of("always", "yes")), null, null),
    LOCAL_AUTONOMY_TRADE_COMPANY_MULTIPLICATIVE(new Condition(Pair.of("is_owned_by_trade_company", "yes")), null, StaticModifier::scaleAutonomy),
    LOCAL_AUTONOMY_TRADE_COMPANY(new Condition(Pair.of("always", "yes")), null, null),
    RECENT_UPRISING(new Condition(Pair.of("has_province_modifier", "recent_uprising")), null, null),
    FRIENDLY_REGIMENTS(new Condition(Pair.of("units_in_province", "1")), null, StaticModifier::scaleFriendlyRegimentMax20),
    ACTIVE_MISSIONARY(new Condition(Pair.of("has_missionary", "yes")), null, null),
    NATIONAL_DEFENSE(new Condition(Pair.of("national_defense", "yes")), null, null),
    RESOURCE_DEPLETED(new Condition(Pair.of("has_province_modifier", "resource_depleted")), null, null),
    IN_TRADE_COMPANY(new Condition(Pair.of("is_owned_by_trade_company", "yes")), null, null),
    LEFT_TRADE_COMPANY(new Condition(Pair.of("has_province_modifier", "left_trade_company")), null, null),
    SCORCHED_EARTH(new Condition(Pair.of("has_province_modifier", "scorched_earth")), null, null),
    BASE_VALUES(new Condition(Pair.of("always", "yes")), null, null),
    AI_NATION(new Condition(Pair.of("ai", "yes")), null, null),
    WAR_TAXES(new Condition(Pair.of("has_wartaxes", "yes")), null, null),
    STABILITY(new Condition(Pair.of("always", "yes")), StaticModifier::scaleWithStability, null),
    POSITIVE_STABILITY(new Condition(Pair.of("stability", "1")), StaticModifier::scaleWithStability, null),
    PRIVATEERING(new Condition(Pair.of("has_privateers", "yes")), null, null),
    NEGATIVE_STABILITY(new Condition("not", Pair.of("stability", "0")), StaticModifier::scaleWithStability, null),
    LOST_MANDATE_OF_HEAVEN(new Condition(Pair.of("has_country_modifier", "lost_mandate_of_heaven")), null, null),
    CANCELLED_LOAN(new Condition(Pair.of("has_country_modifier", "cancelled_loan")), null, null),
    BANK_LOAN(new Condition(Pair.of("num_of_loans", "1")), null, null),
    INFLATION(new Condition(Pair.of("inflation", "0.001")), StaticModifier::scaleWithInflation, null),
    BANKRUPTCY(new Condition(Pair.of("is_bankrupt", "yes")), null, null),
    WAR(new Condition(Pair.of("is_at_war", "yes")), null, null),
    PEACE(new Condition(Pair.of("is_at_war", "no")), null, null),
    UNCONDITIONAL_SURRENDER(new Condition(Pair.of("has_unconditional_surrender", "yes")), null, null),
    CALL_FOR_PEACE(new Condition(Pair.of("call_for_peace", "1")), StaticModifier::scaleWithCallForPeace, null),
    WAR_EXHAUSTION(new Condition(Pair.of("war_exhaustion", "0.001")), StaticModifier::scaleWithWarExhaustion, null),
    DOOM(new Condition(Pair.of("doom", "0.001")), StaticModifier::scaleWithDoom, null),
    AUTHORITY(new Condition(Pair.of("authority", "0.001")), StaticModifier::scaleWithAuthority, null),
    REGENCY_COUNCIL(new Condition(Pair.of("has_regency", "yes")), null, null),
    //    TRADE_EFFICIENCY(new Condition(Pair.of("always", "yes")), applyToCountry, null), //Fixme //Scale
    //    PRODUCTION_EFFICIENCY(new Condition(Pair.of("always", "yes")), applyToCountry, null), //Fixme //Scale
    TRADE_REFUSAL(new Condition(Pair.of("num_of_trade_embargos", "1")), StaticModifier::scaleWithTradeRefusal, null),
    MERCANTILISM(new Condition(Pair.of("always", "yes")), StaticModifier::scaleWithMercantilism, null),
    ARMY_TRADITION(new Condition(Pair.of("army_tradition", "0.001")), StaticModifier::scaleWithArmyTradition, null),
    NAVY_TRADITION(new Condition(Pair.of("navy_tradition", "0.001")), StaticModifier::scaleWithNavyTradition, null),
    DEFENDER_OF_FAITH(new Condition(Pair.of("is_defender_of_faith", "yes")), null, null),
    DEFENDER_OF_FAITH_REFUSED_CTA(new Condition(Pair.of("has_country_modifier", "defender_of_faith_refused_cta")), null, null),
    EMPEROR(new Condition(Pair.of("is_emperor", "yes")), null, null),
    FREE_CITIES_IN_HRE(new Condition(Pair.of("is_emperor", "yes")), StaticModifier::scaleWithFreeCitiesInHre, null),
    FREE_CITY_IN_HRE(new Condition(Pair.of("is_free_city", "yes")), null, null),
    MEMBER_IN_HRE(new Condition(Pair.of("is_part_of_hre", "yes")), null, null),
    OCCUPIED_IMPERIAL(new Condition(Pair.of("occupied_imperial", "1")), StaticModifier::scaleOccupiedImperial, null),
    NUM_OF_MARRIAGES(new Condition(Pair.of("num_of_royal_marriages", "1")), StaticModifier::scaleWithNumOfRoyalMarriages, null),
    NUM_OF_PROVINCES(new Condition(Pair.of("num_of_cities", "1")), StaticModifier::scaleWithNumOfProvinces, null),
    COUNTRY_DEVELOPMENT(new Condition(Pair.of("total_development", "1")), StaticModifier::scaleCountryDev, null),
    TRIBAL_ALLEGIANCE(new Condition(Pair.of("has_government_ability", "tribal_federation_mechanic")), StaticModifier::scaleWithTribalAllegiance, null),
    LEGITIMACY(new Condition(Pair.of("legitimacy", "0.001")), StaticModifier::scaleWithLegitimacy50, null), //Fixme -> uses_legitimacy
    HORDE_UNITY(new Condition(Pair.of("has_horde_unity", "yes")), StaticModifier::scaleWithHordeUnity50, null),
    DEVOTION(new Condition(Pair.of("uses_devotion", "yes")), StaticModifier::scaleWithDevotion50, null),
    MERITOCRACY(new Condition(Pair.of("has_meritocracy", "yes")), StaticModifier::scaleWithMeritocracy50, null),
    LOW_MERITOCRACY(new Condition(Pair.of("has_meritocracy", "yes")), StaticModifier::scaleWithMeritocracy50Reverse, null),
    CORRUPTION(new Condition(Pair.of("always", "yes")), StaticModifier::scaleWithCorruption, null),
    ROOT_OUT_CORRUPTION(new Condition(Pair.of("always", "yes")), StaticModifier::scaleWithRootOutCorruption, null),
    RECOVERY_MOTIVATION(new Condition(Pair.of("always", "yes")), StaticModifier::scaleWithRecoveryMotivation, null),
    MILITARIZED_SOCIETY(new Condition(Pair.of("has_militarised_society", "yes")), StaticModifier::scaleWithMilitarisedSociety, null),
    LUCK(new Condition(Pair.of("luck", "yes")), null, null),
    OVER_EXTENSION(new Condition(Pair.of("overextension_percentage", "0.001")), StaticModifier::scaleWithOverextension, null),
    PRESTIGE(new Condition(Pair.of("always", "yes")), StaticModifier::scaleWithPrestige, null),
    NO_DEBATE_IN_PARLIAMENT(new Condition(Pair.of("has_active_debate", "no")), null, null),
    REPUBLICAN_TRADITION(new Condition(Pair.of("is_republic", "yes")), StaticModifier::scaleWithRepublicanTradition, null),
    INVERSE_REPUBLICAN_TRADITION(new Condition(Pair.of("is_republic", "yes")), StaticModifier::scaleWithRepublicanTraditionReverse, null),
    CURIA_CONTROLLER(new Condition(Pair.of("is_papal_controller", "yes")), null, null),
    BOUGHT_INDULGENCE(new Condition(Pair.of("has_country_modifier", "bought_indulgence")), null, null),
    RELIGIOUS_UNITY(new Condition(Pair.of("always", "yes")), StaticModifier::scaleWithReligiousUnity, null),
    INVERSE_RELIGIOUS_UNITY(new Condition(Pair.of("always", "yes")), StaticModifier::scaleWithReligiousUnityReverse, null),
    TOTAL_OCCUPATION(new Condition(Pair.of("always", "yes")), StaticModifier::scaleWithOccupiedProvinces, null),
    TOTAL_BLOCKADED(new Condition(Pair.of("always", "yes")), StaticModifier::scaleWithBlockadedProvinces, null),
    UNCONTESTED_CORES(new Condition(Pair.of("num_uncontested_cores", "1")), StaticModifier::scaleWithNotControlledCores, null),
    NUM_OBJECTIVES_FULLFILLED(new Condition(Pair.of("num_of_age_objectives", "1")), StaticModifier::scaleWithNumOfAgeObjectives, null),
    PRODUCTION_LEADER(new Condition(Pair.of("production_leader", "yes")), null, null),
    TRADE_COMPANY_BONUS(new Condition(Pair.of("always", "false")), null, null), //Fixme ?? //Scale
    BONUS_FROM_MERCHANT_REPUBLICS(new Condition(Pair.of("always", "false")), null, null), //Fixme ?? //Scale
    BONUS_FROM_MERCHANT_REPUBLICS_FOR_TRADE_LEAGUE_MEMBER(new Condition(Pair.of("always", "false")), null, null), //Fixme ?? //Scale
    MERCHANT_REPUBLIC_MECHANICS_MODIFIER(new Condition(Pair.of("always", "false")), null, null), //Fixme ?? //Scale
    FEDERATION_LEADER(new Condition(Pair.of("is_federation_leader", "yes")), null, null),
    TRIBUTARY_STATE_BEHIND_OVERLORD_TECH_ADM(new Condition(Pair.of("is_tributary", "yes")), StaticModifier::scaleWithOverlordAdm, null),
    TRIBUTARY_STATE_BEHIND_OVERLORD_TECH_DIP(new Condition(Pair.of("is_tributary", "yes")), StaticModifier::scaleWithOverlordDip, null),
    TRIBUTARY_STATE_BEHIND_OVERLORD_TECH_MIL(new Condition(Pair.of("is_tributary", "yes")), StaticModifier::scaleWithOverlordMil, null),
    LIBERTY_DESIRE(new Condition(Pair.of("liberty_desire", "0.001")), StaticModifier::scaleWithLibertyDesire, null),
    IN_GOLDEN_ERA(new Condition(Pair.of("in_golden_age", "yes")), null, null),
    ABSOLUTISM(new Condition(Pair.of("absolutism", "1")), StaticModifier::scaleWithAbsolutism, null),
    LOW_ARMY_PROFESSIONALISM(new Condition(Pair.of("low_army_professionalism", "yes")), StaticModifier::scaleWithLowProfessionalism, null),
    HIGH_ARMY_PROFESSIONALISM(new Condition(Pair.of("high_army_professionalism", "yes")), StaticModifier::scaleWithHighProfessionalism, null),
    STRELTSY_MODIFIER(new Condition(Pair.of("num_of_streltsy", "1")), StaticModifier::scaleWithStreltsyPercent, null),
    POWER_PROJECTION(new Condition(Pair.of("power_projection", "0.001")), StaticModifier::scaleWithCurrentPowerProjection, null),
    POWER_PROJECTION_25(new Condition(Pair.of("power_projection", "25")), null, null),
    TRADE_COMPANY_STRONG(new Condition(Pair.of("num_of_strong_trade_companies", "1")), StaticModifier::scaleWithStrongCompany, null), //Scale
    TRADE_COMPANY_DOMINANT(new Condition(Pair.of("always", "no")), null, null), //Fixme ?? //Scale
    LARGE_COLONIAL_NATION(new Condition(Pair.of("num_of_large_colonial_nation", "1")), StaticModifier::scaleWithLargeColony, null),
    MARCH_SUBJECT(new Condition(Pair.of("num_of_marches", "1")), StaticModifier::scaleWithMarches, null),
    VASSAL_SUBJECT(new Condition(Pair.of("num_of_vassals", "1")), StaticModifier::scaleWithVassals, null),
    DAIMYO_SUBJECT(new Condition(Pair.of("num_of_daimyos", "1")), StaticModifier::scaleWithDaimyos, null),
    UNION_SUBJECT(new Condition(Pair.of("num_of_unions", "1")), StaticModifier::scaleWithUnions, null),
    ALL_NATIONS(new Condition(Pair.of("always", "yes")), null, null),
    SUBJECT_NATION(new Condition(Pair.of("is_subject", "yes")), null, null),
    VASSAL_NATION(new Condition(Pair.of("is_vassal", "yes")), null, null),
    PRIMITIVE_NATION(new Condition(Pair.of("primitives", "yes")), null, null),
    MAINTAINED_FORTS(new Condition(Pair.of("always", "no")), null, null), //Fixme
    GOV_RANK_1(new Condition(Pair.of("government_rank", "1")), null, null),
    GOV_RANK_2(new Condition(Pair.of("government_rank", "2")), null, null),
    GOV_RANK_3(new Condition(Pair.of("government_rank", "3")), null, null),
    GOV_RANK_4(new Condition(Pair.of("government_rank", "4")), null, null),
    GOV_RANK_5(new Condition(Pair.of("government_rank", "5")), null, null),
    GOV_RANK_6(new Condition(Pair.of("government_rank", "6")), null, null),
    GOV_RANK_7(new Condition(Pair.of("government_rank", "7")), null, null),
    GOV_RANK_8(new Condition(Pair.of("government_rank", "8")), null, null),
    GOV_RANK_9(new Condition(Pair.of("government_rank", "9")), null, null),
    GOV_RANK_10(new Condition(Pair.of("government_rank", "10")), null, null),
    AUTONOMY_INCREASED(new Condition(Pair.of("has_province_modifier", "autonomy_increased")), null, null),
    AUTONOMY_DECREASED(new Condition(Pair.of("has_province_modifier", "autonomy_decreased")), null, null),
    REVOLUTION_TARGET(new Condition(Pair.of("is_revolution_target", "yes")), null, null),
    RECRUITMENT_SABOTAGED(new Condition(Pair.of("has_country_modifier", "recruitment_sabotaged")), null, null),
    MERCHANTS_SLANDERED(new Condition(Pair.of("has_country_modifier", "merchants_slandered")), null, null),
    DISCONTENT_SOWED(new Condition(Pair.of("has_country_modifier", "discontent_sowed")), null, null),
    REPUTATION_SABOTAGED(new Condition(Pair.of("has_country_modifier", "reputation_sabotaged")), null, null),
    CORRUPT_OFFICIALS(new Condition(Pair.of("has_country_modifier", "corrupt_officials")), null, null),
    SCALED_TRADE_LEAGUE_LEADER(new Condition(Pair.of("is_trade_league_leader", "yes")), StaticModifier::scaleWithNumTradeLeagueMembers, null),
    IN_TRADE_LEAGUE(new Condition(Pair.of("is_in_trade_league", "yes")), null, null),
    CUSTOM_SETUP(new Condition(Pair.of("custom_nation_setup", "yes")), null, null),
    EMBARGO_RIVALS(new Condition(Pair.of("has_embargo_rivals", "yes")), null, null),
    SCUTAGE(new Condition(Pair.of("has_scutage", "yes")), null, null),
    SUBSIDIZE_ARMIES(new Condition(Pair.of("has_subsidize_armies", "yes")), null, null),
    SUPPORT_LOYALISTS(new Condition(Pair.of("has_support_loyalists", "yes")), null, null),
    SEND_OFFICERS(new Condition(Pair.of("has_send_officers", "yes")), null, null),
    DIVERT_TRADE(new Condition(Pair.of("has_divert_trade", "yes")), null, null),
    INVASION_NATION(new Condition(Pair.of("invasion_nation", "yes")), null, null),
    NATIVE_POLICY_COEXIST(new Condition(Pair.of("native_policy", "1")), null, null),
    NATIVE_POLICY_TRADE(new Condition(Pair.of("native_policy", "2")), null, null),
    NATIVE_POLICY_HOSTILE(new Condition(Pair.of("native_policy", "3")), null, null),
    HIGH_HARMONY(new Condition(Pair.of("uses_harmony", "yes")), StaticModifier::scaleWithHarmony, null),
    LOW_HARMONY(new Condition(Pair.of("uses_harmony", "yes")), StaticModifier::scaleWithHarmonyReverse, null),
    OVERLORD_DAIMYO_AT_PEACE(new Condition(Pair.of("num_of_daimyos", "1")), StaticModifier::scaleWithDaimyosAtPeace, null),
    //    OVERLORD_DAIMYO_AT_PEACE_MAX(new Condition(Pair.of("num_of_daimyos", "1")), applyToCountry, null),
    //    OVERLORD_DAIMYO_AT_PEACE_MIN(new Condition(Pair.of("num_of_daimyos", "1")), applyToCountry, null),
    OVERLORD_DAIMYO_SAME_ISOLATIONISM(new Condition(Pair.of("num_of_daimyos", "1")), StaticModifier::scaleWithDaimyosSameIsolationism, null),
    OVERLORD_DAIMYO_DIFFERENT_ISOLATIONISM(new Condition(Pair.of("num_of_daimyos", "1")), StaticModifier::scaleWithDaimyosDifferentIsolationism, null),
    //    OVERLORD_DAIMYO_ISOLATIONISM_MAX(new Condition(Pair.of("num_of_daimyos", "1")), applyToCountry, null),
    //    OVERLORD_DAIMYO_ISOLATIONISM_MIN(new Condition(Pair.of("num_of_daimyos", "1")), applyToCountry, null),
    OVERLORD_SANKIN_KOTAI(new Condition(Pair.of("has_country_modifier", "overlord_sankin_kotai")), null, null),
    SUBJECT_SANKIN_KOTAI(new Condition(Pair.of("has_country_modifier", "subject_sankin_kotai")), null, null),
    OVERLORD_EXPEL_RONIN(new Condition(Pair.of("has_country_modifier", "overlord_expel_ronin")), null, null),
    SUBJECT_EXPEL_RONIN(new Condition(Pair.of("has_country_modifier", "subject_expel_ronin")), null, null),
    OVERLORD_SWORD_HUNT(new Condition(Pair.of("num_of_daimyos", "1")), StaticModifier::scaleWithDaimyosSwordHunt, null),
    SUBJECT_SWORD_HUNT(new Condition(Pair.of("has_country_modifier", "subject_sword_hunt")), null, null),
    SUPPLY_DEPOT_AREA(new Condition(Pair.of("has_supply_depot", "yes")), null, null),
    EFFICIENT_TAX_FARMING_MODIFIER(new Condition(Pair.of("has_country_modifier", "efficient_tax_farming_modifier")), null, null),
    LAND_ACQUISITION_MODIFIER(new Condition(Pair.of("has_country_modifier", "land_acquisition_modifier")), null, null),
    LENIENT_TAXATION_MODIFIER(new Condition(Pair.of("has_country_modifier", "lenient_taxation_modifier")), null, null),
    TRAIN_HORSEMANSHIP_MODIFIER(new Condition(Pair.of("has_country_modifier", "train_horsemanship_modifier")), null, null),
    PROMOTE_CULTURE_IN_GOVERNMENT_MODIFIER(new Condition(Pair.of("has_country_modifier", "promote_culture_in_government_modifier")), null, null),
    SEIZE_CLERICAL_HOLDINGS_MODIFIER(new Condition(Pair.of("has_country_modifier", "seize_clerical_holdings_modifier")), null, null),
    INVITE_MINORITIES_MODIFIER(new Condition(Pair.of("has_country_modifier", "invite_minorities_modifier")), null, null),
    HANAFI_SCHOLAR_MODIFIER(new Condition(Pair.of("has_country_modifier", "hanafi_scholar_modifier")), null, null),
    HANBALI_SCHOLAR_MODIFIER(new Condition(Pair.of("has_country_modifier", "hanbali_scholar_modifier")), null, null),
    MALIKI_SCHOLAR_MODIFIER(new Condition(Pair.of("has_country_modifier", "maliki_scholar_modifier")), null, null),
    SHAFII_SCHOLAR_MODIFIER(new Condition(Pair.of("has_country_modifier", "shafii_scholar_modifier")), null, null),
    ISMAILI_SCHOLAR_MODIFIER(new Condition(Pair.of("has_country_modifier", "ismaili_scholar_modifier")), null, null),
    JAFARI_SCHOLAR_MODIFIER(new Condition(Pair.of("has_country_modifier", "jafari_scholar_modifier")), null, null),
    ZAIDI_SCHOLAR_MODIFIER(new Condition(Pair.of("has_country_modifier", "zaidi_scholar_modifier")), null, null),
    //    JANISSARY_REGIMENT(new Condition(Pair.of())),
    //    REVOLUTIONARY_GUARD_REGIMENT(new Condition(Pair.of())),
    INNOVATIVENESS(new Condition(Pair.of("innovativeness", "0.001")), StaticModifier::scaleWithInnovativeness, null), //Scale
    //    RAJPUT_REGIMENT(new Condition(Pair.of())),
    RAIDING_PARTIES_MODIFIER(new Condition(Pair.of("has_country_modifier", "raiding_parties_modifier")), null, null),
    SERFS_RECIEVED_BY_COSSACKS(new Condition(Pair.of("has_country_modifier", "serfs_recieved_by_cossacks")), null, null),
    COSSACKS_MODIFIER(new Condition(Pair.of("num_of_cossacks", "1")), StaticModifier::scaleWithCossacksPercent, null),
    EXPAND_ADMINISTATION_MODIFIER(new Condition(Pair.of("num_expanded_administration", "1")), StaticModifier::scaleWithNumExpandedAdministration, null),
    OVER_GOVERNING_CAPACITY_MODIFIER(new Condition(Pair.of("always", "no")), null, null), //Todo getUsedGoverningCapacity //Scale
    LOST_HEGEMONY(new Condition(Pair.of("has_country_modifier", "lost_hegemony")), null, null);

    public final Condition trigger;

    public Modifiers modifiers;

    public BiFunction<Country, StaticModifier, Modifiers> applyToCountry;

    public BiFunction<SaveProvince, StaticModifier, Modifiers> applyToProvince;

    private static final Map<String, StaticModifier> STATIC_MODIFIERS_MAP;

    private static final List<StaticModifier> APPLIED_TO_COUNTRY;

    private static final List<StaticModifier> APPLIED_TO_PROVINCE;

    static {
        STATIC_MODIFIERS_MAP = Arrays.stream(StaticModifier.values()).collect(Collectors.toMap(Enum::name, Function.identity()));
        APPLIED_TO_COUNTRY = Arrays.stream(StaticModifier.values())
                                   .filter(staticModifier -> staticModifier.applyToCountry != null)
                                   .collect(Collectors.toList());
        APPLIED_TO_PROVINCE = Arrays.stream(StaticModifier.values())
                                    .filter(staticModifier -> staticModifier.applyToProvince != null)
                                    .collect(Collectors.toList());
        DIFFICULTY_VERY_EASY_PLAYER.applyToCountry = (country, modif) -> StaticModifier.DIFFICULTY_VERY_EASY_PLAYER.modifiers;
        DIFFICULTY_EASY_PLAYER.applyToCountry = (country, modif) -> StaticModifier.DIFFICULTY_EASY_PLAYER.modifiers;
        DIFFICULTY_NORMAL_PLAYER.applyToCountry = (country, modif) -> StaticModifier.DIFFICULTY_NORMAL_PLAYER.modifiers;
        DIFFICULTY_HARD_PLAYER.applyToCountry = (country, modif) -> StaticModifier.DIFFICULTY_HARD_PLAYER.modifiers;
        DIFFICULTY_VERY_HARD_PLAYER.applyToCountry = (country, modif) -> StaticModifier.DIFFICULTY_VERY_HARD_PLAYER.modifiers;
        DIFFICULTY_VERY_EASY_AI.applyToCountry = (country, modif) -> StaticModifier.DIFFICULTY_VERY_EASY_AI.modifiers;
        DIFFICULTY_EASY_AI.applyToCountry = (country, modif) -> StaticModifier.DIFFICULTY_EASY_AI.modifiers;
        DIFFICULTY_NORMAL_AI.applyToCountry = (country, modif) -> StaticModifier.DIFFICULTY_NORMAL_AI.modifiers;
        DIFFICULTY_HARD_AI.applyToCountry = (country, modif) -> StaticModifier.DIFFICULTY_HARD_AI.modifiers;
        DIFFICULTY_VERY_HARD_AI.applyToCountry = (country, modif) -> StaticModifier.DIFFICULTY_VERY_HARD_AI.modifiers;
        CITY.applyToProvince = (province, modif) -> StaticModifier.CITY.modifiers;
        PORT.applyToProvince = (province, modif) -> StaticModifier.PORT.modifiers;
        IN_STATE.applyToProvince = (province, modif) -> StaticModifier.IN_STATE.modifiers;
        IN_CAPITAL_STATE.applyToProvince = (province, modif) -> StaticModifier.IN_CAPITAL_STATE.modifiers;
        COASTAL.applyToProvince = (province, modif) -> StaticModifier.COASTAL.modifiers;
        SEAT_IN_PARLIAMENT.applyToProvince = (province, modif) -> StaticModifier.SEAT_IN_PARLIAMENT.modifiers;
        NON_COASTAL.applyToProvince = (province, modif) -> StaticModifier.NON_COASTAL.modifiers;
        COASTAL_SEA.applyToProvince = (province, modif) -> StaticModifier.COASTAL_SEA.modifiers;
        TROPICAL.applyToProvince = (province, modif) -> StaticModifier.TROPICAL.modifiers;
        ARCTIC.applyToProvince = (province, modif) -> StaticModifier.ARCTIC.modifiers;
        ARID.applyToProvince = (province, modif) -> StaticModifier.ARID.modifiers;
        SEA_ZONE.applyToProvince = (province, modif) -> StaticModifier.SEA_ZONE.modifiers;
        MILD_WINTER.applyToProvince = (province, modif) -> StaticModifier.MILD_WINTER.modifiers;
        NORMAL_WINTER.applyToProvince = (province, modif) -> StaticModifier.NORMAL_WINTER.modifiers;
        SEVERE_WINTER.applyToProvince = (province, modif) -> StaticModifier.SEVERE_WINTER.modifiers;
        MILD_MONSOON.applyToProvince = (province, modif) -> StaticModifier.MILD_MONSOON.modifiers;
        NORMAL_MONSOON.applyToProvince = (province, modif) -> StaticModifier.NORMAL_MONSOON.modifiers;
        SEVERE_MONSOON.applyToProvince = (province, modif) -> StaticModifier.SEVERE_MONSOON.modifiers;
        BLOCKADED.applyToProvince = (province, modif) -> StaticModifier.BLOCKADED.modifiers;
        NO_ADJACENT_CONTROLLED.applyToProvince = (province, modif) -> StaticModifier.NO_ADJACENT_CONTROLLED.modifiers;
        KNOWLEDGE_SHARING.applyToCountry = (country, modif) -> StaticModifier.KNOWLEDGE_SHARING.modifiers;
        CARDINALS_SPREAD_INSTITUTION.applyToProvince = (province, modif) -> StaticModifier.CARDINALS_SPREAD_INSTITUTION.modifiers;
        HORDE_DEVELOPMENT.applyToCountry = (country, modif) -> StaticModifier.HORDE_DEVELOPMENT.modifiers;
        PROVINCE_RAZED.applyToProvince = (province, modif) -> StaticModifier.PROVINCE_RAZED.modifiers;
        CAPITAL_CITY.applyToProvince = (province, modif) -> StaticModifier.CAPITAL_CITY.modifiers;
        PATRIARCH_STATE.applyToProvince = (province, modif) -> StaticModifier.PATRIARCH_STATE.modifiers;
        PASHA_STATE.applyToProvince = (province, modif) -> StaticModifier.PASHA_STATE.modifiers;
        CORE.applyToProvince = (province, modif) -> StaticModifier.CORE.modifiers;
        NON_CORE.applyToProvince = (province, modif) -> StaticModifier.NON_CORE.modifiers;
        TERRITORY_CORE.applyToProvince = (province, modif) -> StaticModifier.TERRITORY_CORE.modifiers;
        TERRITORY_NON_CORE.applyToProvince = (province, modif) -> StaticModifier.TERRITORY_NON_CORE.modifiers;
        MARCH_BONUS.applyToCountry = (country, modif) -> StaticModifier.MARCH_BONUS.modifiers;
        SAME_CULTURE_GROUP.applyToProvince = (province, modif) -> StaticModifier.SAME_CULTURE_GROUP.modifiers;
        NON_ACCEPTED_CULTURE.applyToProvince = (province, modif) -> StaticModifier.NON_ACCEPTED_CULTURE.modifiers;
        ACCEPTED_CULTURE_DEMOTED.applyToProvince = (province, modif) -> StaticModifier.ACCEPTED_CULTURE_DEMOTED.modifiers;
        NON_ACCEPTED_CULTURE_REPUBLIC.applyToProvince = (province, modif) -> StaticModifier.NON_ACCEPTED_CULTURE_REPUBLIC.modifiers;
        OCCUPIED.applyToProvince = (province, modif) -> StaticModifier.OCCUPIED.modifiers;
        UNDER_SIEGE.applyToProvince = (province, modif) -> StaticModifier.UNDER_SIEGE.modifiers;
        PROSPERITY.applyToProvince = (province, modif) -> StaticModifier.PROSPERITY.modifiers;
        SLAVES_RAIDED.applyToProvince = (province, modif) -> StaticModifier.SLAVES_RAIDED.modifiers;
        LOCAL_AUTONOMY.applyToProvince = (province, modif) -> StaticModifier.LOCAL_AUTONOMY.modifiers;
        LOCAL_AUTONOMY_TRADE_COMPANY.applyToProvince = (province, modif) -> StaticModifier.LOCAL_AUTONOMY_TRADE_COMPANY.modifiers;
        RECENT_UPRISING.applyToProvince = (province, modif) -> StaticModifier.RECENT_UPRISING.modifiers;
        ACTIVE_MISSIONARY.applyToProvince = (province, modif) -> StaticModifier.ACTIVE_MISSIONARY.modifiers;
        NATIONAL_DEFENSE.applyToProvince = (province, modif) -> StaticModifier.NATIONAL_DEFENSE.modifiers;
        RESOURCE_DEPLETED.applyToProvince = (province, modif) -> StaticModifier.RESOURCE_DEPLETED.modifiers;
        IN_TRADE_COMPANY.applyToProvince = (province, modif) -> StaticModifier.IN_TRADE_COMPANY.modifiers;
        LEFT_TRADE_COMPANY.applyToProvince = (province, modif) -> StaticModifier.LEFT_TRADE_COMPANY.modifiers;
        SCORCHED_EARTH.applyToProvince = (province, modif) -> StaticModifier.SCORCHED_EARTH.modifiers;
        BASE_VALUES.applyToCountry = (country, modif) -> StaticModifier.BASE_VALUES.modifiers;
        AI_NATION.applyToCountry = (country, modif) -> StaticModifier.AI_NATION.modifiers;
        WAR_TAXES.applyToCountry = (country, modif) -> StaticModifier.WAR_TAXES.modifiers;
        PRIVATEERING.applyToCountry = (country, modif) -> StaticModifier.PRIVATEERING.modifiers;
        LOST_MANDATE_OF_HEAVEN.applyToCountry = (country, modif) -> StaticModifier.LOST_MANDATE_OF_HEAVEN.modifiers;
        CANCELLED_LOAN.applyToCountry = (country, modif) -> StaticModifier.CANCELLED_LOAN.modifiers;
        BANK_LOAN.applyToCountry = (country, modif) -> StaticModifier.BANK_LOAN.modifiers;
        BANKRUPTCY.applyToCountry = (country, modif) -> StaticModifier.BANKRUPTCY.modifiers;
        WAR.applyToCountry = (country, modif) -> StaticModifier.WAR.modifiers;
        PEACE.applyToCountry = (country, modif) -> StaticModifier.PEACE.modifiers;
        UNCONDITIONAL_SURRENDER.applyToCountry = (country, modif) -> StaticModifier.UNCONDITIONAL_SURRENDER.modifiers;
        REGENCY_COUNCIL.applyToCountry = (country, modif) -> StaticModifier.REGENCY_COUNCIL.modifiers;
        DEFENDER_OF_FAITH.applyToCountry = (country, modif) -> StaticModifier.DEFENDER_OF_FAITH.modifiers;
        DEFENDER_OF_FAITH_REFUSED_CTA.applyToCountry = (country, modif) -> StaticModifier.DEFENDER_OF_FAITH_REFUSED_CTA.modifiers;
        EMPEROR.applyToCountry = (country, modif) -> StaticModifier.EMPEROR.modifiers;
        FREE_CITY_IN_HRE.applyToCountry = (country, modif) -> StaticModifier.FREE_CITY_IN_HRE.modifiers;
        MEMBER_IN_HRE.applyToCountry = (country, modif) -> StaticModifier.MEMBER_IN_HRE.modifiers;
        LUCK.applyToCountry = (country, modif) -> StaticModifier.LUCK.modifiers;
        NO_DEBATE_IN_PARLIAMENT.applyToCountry = (country, modif) -> StaticModifier.NO_DEBATE_IN_PARLIAMENT.modifiers;
        CURIA_CONTROLLER.applyToCountry = (country, modif) -> StaticModifier.CURIA_CONTROLLER.modifiers;
        BOUGHT_INDULGENCE.applyToCountry = (country, modif) -> StaticModifier.BOUGHT_INDULGENCE.modifiers;
        PRODUCTION_LEADER.applyToProvince = (province, modif) -> StaticModifier.PRODUCTION_LEADER.modifiers;
        FEDERATION_LEADER.applyToCountry = (country, modif) -> StaticModifier.FEDERATION_LEADER.modifiers;
        IN_GOLDEN_ERA.applyToCountry = (country, modif) -> StaticModifier.IN_GOLDEN_ERA.modifiers;
        POWER_PROJECTION_25.applyToCountry = (country, modif) -> StaticModifier.POWER_PROJECTION_25.modifiers;
        ALL_NATIONS.applyToCountry = (country, modif) -> StaticModifier.ALL_NATIONS.modifiers;
        SUBJECT_NATION.applyToCountry = (country, modif) -> StaticModifier.SUBJECT_NATION.modifiers;
        VASSAL_NATION.applyToCountry = (country, modif) -> StaticModifier.VASSAL_NATION.modifiers;
        PRIMITIVE_NATION.applyToCountry = (country, modif) -> StaticModifier.PRIMITIVE_NATION.modifiers;
        GOV_RANK_1.applyToCountry = (country, modif) -> StaticModifier.GOV_RANK_1.modifiers;
        GOV_RANK_2.applyToCountry = (country, modif) -> StaticModifier.GOV_RANK_2.modifiers;
        GOV_RANK_3.applyToCountry = (country, modif) -> StaticModifier.GOV_RANK_3.modifiers;
        GOV_RANK_4.applyToCountry = (country, modif) -> StaticModifier.GOV_RANK_4.modifiers;
        GOV_RANK_5.applyToCountry = (country, modif) -> StaticModifier.GOV_RANK_5.modifiers;
        GOV_RANK_6.applyToCountry = (country, modif) -> StaticModifier.GOV_RANK_6.modifiers;
        GOV_RANK_7.applyToCountry = (country, modif) -> StaticModifier.GOV_RANK_7.modifiers;
        GOV_RANK_8.applyToCountry = (country, modif) -> StaticModifier.GOV_RANK_8.modifiers;
        GOV_RANK_9.applyToCountry = (country, modif) -> StaticModifier.GOV_RANK_9.modifiers;
        GOV_RANK_10.applyToCountry = (country, modif) -> StaticModifier.GOV_RANK_10.modifiers;
        AUTONOMY_INCREASED.applyToProvince = (province, modif) -> StaticModifier.AUTONOMY_INCREASED.modifiers;
        AUTONOMY_DECREASED.applyToProvince = (province, modif) -> StaticModifier.AUTONOMY_DECREASED.modifiers;
        REVOLUTION_TARGET.applyToCountry = (country, modif) -> StaticModifier.REVOLUTION_TARGET.modifiers;
        RECRUITMENT_SABOTAGED.applyToCountry = (country, modif) -> StaticModifier.RECRUITMENT_SABOTAGED.modifiers;
        MERCHANTS_SLANDERED.applyToCountry = (country, modif) -> StaticModifier.MERCHANTS_SLANDERED.modifiers;
        DISCONTENT_SOWED.applyToCountry = (country, modif) -> StaticModifier.DISCONTENT_SOWED.modifiers;
        REPUTATION_SABOTAGED.applyToCountry = (country, modif) -> StaticModifier.REPUTATION_SABOTAGED.modifiers;
        IN_TRADE_LEAGUE.applyToCountry = (country, modif) -> StaticModifier.IN_TRADE_LEAGUE.modifiers;
        CUSTOM_SETUP.applyToCountry = (country, modif) -> StaticModifier.CUSTOM_SETUP.modifiers;
        EMBARGO_RIVALS.applyToCountry = (country, modif) -> StaticModifier.EMBARGO_RIVALS.modifiers;
        SCUTAGE.applyToCountry = (country, modif) -> StaticModifier.SCUTAGE.modifiers;
        SUBSIDIZE_ARMIES.applyToCountry = (country, modif) -> StaticModifier.SUBSIDIZE_ARMIES.modifiers;
        SUPPORT_LOYALISTS.applyToCountry = (country, modif) -> StaticModifier.SUPPORT_LOYALISTS.modifiers;
        SEND_OFFICERS.applyToCountry = (country, modif) -> StaticModifier.SEND_OFFICERS.modifiers;
        DIVERT_TRADE.applyToCountry = (country, modif) -> StaticModifier.DIVERT_TRADE.modifiers;
        INVASION_NATION.applyToCountry = (country, modif) -> StaticModifier.INVASION_NATION.modifiers;
        NATIVE_POLICY_COEXIST.applyToCountry = (country, modif) -> StaticModifier.NATIVE_POLICY_COEXIST.modifiers;
        NATIVE_POLICY_TRADE.applyToCountry = (country, modif) -> StaticModifier.NATIVE_POLICY_TRADE.modifiers;
        NATIVE_POLICY_HOSTILE.applyToCountry = (country, modif) -> StaticModifier.NATIVE_POLICY_HOSTILE.modifiers;
        OVERLORD_SANKIN_KOTAI.applyToCountry = (country, modif) -> StaticModifier.OVERLORD_SANKIN_KOTAI.modifiers;
        SUBJECT_SANKIN_KOTAI.applyToCountry = (country, modif) -> StaticModifier.SUBJECT_SANKIN_KOTAI.modifiers;
        OVERLORD_EXPEL_RONIN.applyToCountry = (country, modif) -> StaticModifier.OVERLORD_EXPEL_RONIN.modifiers;
        SUBJECT_EXPEL_RONIN.applyToCountry = (country, modif) -> StaticModifier.SUBJECT_EXPEL_RONIN.modifiers;
        SUBJECT_SWORD_HUNT.applyToCountry = (country, modif) -> StaticModifier.SUBJECT_SWORD_HUNT.modifiers;
        SUPPLY_DEPOT_AREA.applyToProvince = (province, modif) -> StaticModifier.SUPPLY_DEPOT_AREA.modifiers;
        EFFICIENT_TAX_FARMING_MODIFIER.applyToCountry = (country, modif) -> StaticModifier.EFFICIENT_TAX_FARMING_MODIFIER.modifiers;
        LAND_ACQUISITION_MODIFIER.applyToCountry = (country, modif) -> StaticModifier.LAND_ACQUISITION_MODIFIER.modifiers;
        LENIENT_TAXATION_MODIFIER.applyToCountry = (country, modif) -> StaticModifier.LENIENT_TAXATION_MODIFIER.modifiers;
        TRAIN_HORSEMANSHIP_MODIFIER.applyToCountry = (country, modif) -> StaticModifier.TRAIN_HORSEMANSHIP_MODIFIER.modifiers;
        PROMOTE_CULTURE_IN_GOVERNMENT_MODIFIER.applyToCountry = (country, modif) -> StaticModifier.PROMOTE_CULTURE_IN_GOVERNMENT_MODIFIER.modifiers;
        SEIZE_CLERICAL_HOLDINGS_MODIFIER.applyToCountry = (country, modif) -> StaticModifier.SEIZE_CLERICAL_HOLDINGS_MODIFIER.modifiers;
        INVITE_MINORITIES_MODIFIER.applyToCountry = (country, modif) -> StaticModifier.INVITE_MINORITIES_MODIFIER.modifiers;
        HANAFI_SCHOLAR_MODIFIER.applyToCountry = (country, modif) -> StaticModifier.HANAFI_SCHOLAR_MODIFIER.modifiers;
        HANBALI_SCHOLAR_MODIFIER.applyToCountry = (country, modif) -> StaticModifier.HANBALI_SCHOLAR_MODIFIER.modifiers;
        MALIKI_SCHOLAR_MODIFIER.applyToCountry = (country, modif) -> StaticModifier.MALIKI_SCHOLAR_MODIFIER.modifiers;
        SHAFII_SCHOLAR_MODIFIER.applyToCountry = (country, modif) -> StaticModifier.SHAFII_SCHOLAR_MODIFIER.modifiers;
        ISMAILI_SCHOLAR_MODIFIER.applyToCountry = (country, modif) -> StaticModifier.ISMAILI_SCHOLAR_MODIFIER.modifiers;
        JAFARI_SCHOLAR_MODIFIER.applyToCountry = (country, modif) -> StaticModifier.JAFARI_SCHOLAR_MODIFIER.modifiers;
        ZAIDI_SCHOLAR_MODIFIER.applyToCountry = (country, modif) -> StaticModifier.ZAIDI_SCHOLAR_MODIFIER.modifiers;
        RAIDING_PARTIES_MODIFIER.applyToCountry = (country, modif) -> StaticModifier.RAIDING_PARTIES_MODIFIER.modifiers;
        SERFS_RECIEVED_BY_COSSACKS.applyToCountry = (country, modif) -> StaticModifier.SERFS_RECIEVED_BY_COSSACKS.modifiers;
        LOST_HEGEMONY.applyToCountry = (country, modif) -> StaticModifier.LOST_HEGEMONY.modifiers;
    }

    StaticModifier(Condition trigger, BiFunction<Country, StaticModifier, Modifiers> applyToCountry,
                   BiFunction<SaveProvince, StaticModifier, Modifiers> applyToProvince) {
        this.trigger = trigger;
        this.applyToCountry = applyToCountry;
        this.applyToProvince = applyToProvince;
    }

    public void setModifiers(Modifiers modifiers) {
        this.modifiers = modifiers;
    }

    public static StaticModifier value(String name) {
        return STATIC_MODIFIERS_MAP.get(name.toUpperCase());
    }

    public static Modifiers applyToModifiersCountry(Country country) {
        return ModifiersUtils.sumModifiers(APPLIED_TO_COUNTRY.stream()
                                                             .filter(staticModifier -> staticModifier.trigger.apply(country, country))
                                                             .map(staticModifier -> staticModifier.applyToCountry.apply(country, staticModifier))
                                                             .toArray(Modifiers[]::new));
    }

    public static Modifiers applyToModifiersProvince(SaveProvince province) {
        return ModifiersUtils.sumModifiers(APPLIED_TO_PROVINCE.stream()
                                                              .filter(staticModifier -> staticModifier.trigger.apply(province))
                                                              .map(staticModifier -> staticModifier.applyToProvince.apply(province, staticModifier))
                                                              .toArray(Modifiers[]::new));
    }

    private static Modifiers scaleTax(SaveProvince province, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, province.getBaseTax());
    }

    private static Modifiers scaleProd(SaveProvince province, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, province.getBaseProduction());
    }

    private static Modifiers scaleManpower(SaveProvince province, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, province.getBaseManpower());
    }

    private static Modifiers scaleDev(SaveProvince province, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, province.getDevelopment());
    }

    private static Modifiers scaleDevImprove(SaveProvince province, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, province.getTotalImproveCount());
    }

    private static Modifiers scaleCountryDev(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, country.getDevelopment());
    }

    private static Modifiers scaleColonySize100(SaveProvince province, StaticModifier staticModifier) {
        double colonySize = NumbersUtils.doubleOrDefault(province.getColonySize()) % 100;

        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, colonySize);
    }

    private static Modifiers scaleUnrest(SaveProvince province, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, province.getUnrest());
    }

    private static Modifiers scaleDevastation(SaveProvince province, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, province.getDevastation());
    }

    private static Modifiers scaleNativeSize(SaveProvince province, StaticModifier staticModifier) {
        Integer nativeSize = NumbersUtils.intOrDefault(province.getNativeSize()) / 10;
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, nativeSize);
    }

    private static Modifiers scaleNativeHostileness(SaveProvince province, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, province.getNativeHostileness());
    }

    private static Modifiers scaleNationalism(SaveProvince province, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, province.getNationalism());
    }

    private static Modifiers scaleAutonomy(SaveProvince province, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, province.getLocalAutonomy());
    }

    private static Modifiers scalePatriarchAuthority(SaveProvince province, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, NumbersUtils.doubleOrDefault(province.getOwner().getPatriarchAuthority()));
    }

    private static Modifiers scaleFriendlyRegimentMax20(SaveProvince province, StaticModifier staticModifier) {
        int nbRegiments = province.getArmies()
                                  .stream()
                                  .map(army -> BigDecimal.valueOf(army.getRegiments().size())
                                                         .multiply(BigDecimal.valueOf(NumbersUtils.doubleOrDefault(army.getCountry().getLandMaintenance()))))
                                  .mapToInt(BigDecimal::intValue)
                                  .sum();
        nbRegiments = Math.min(nbRegiments, 20);

        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, nbRegiments);
    }

    private static Modifiers scaleOccupiedImperial(Country country, StaticModifier staticModifier) {
        List<SaveProvince> provinces = country.getOwnedProvinces();
        provinces.retainAll(country.getCoreProvinces());
        provinces.removeIf(Predicate.not(SaveProvince::inHre));

        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, provinces.size());
    }

    private static Modifiers scalePatriarchAuthority(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, NumbersUtils.doubleOrDefault(country.getPatriarchAuthority()));
    }

    private static Modifiers scaleWithStability(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, NumbersUtils.intOrDefault(country.getStability()));
    }

    private static Modifiers scaleWithInflation(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, NumbersUtils.doubleOrDefault(country.getInflation()));
    }

    private static Modifiers scaleWithCallForPeace(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, NumbersUtils.doubleOrDefault(country.getCallForPeace()));
    }

    private static Modifiers scaleWithWarExhaustion(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, NumbersUtils.doubleOrDefault(country.getWarExhaustion()));
    }

    private static Modifiers scaleWithDoom(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, NumbersUtils.doubleOrDefault(country.getDoom()));
    }

    private static Modifiers scaleWithAuthority(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, NumbersUtils.doubleOrDefault(country.getAuthority()));
    }

    private static Modifiers scaleWithTradeRefusal(Country country, StaticModifier staticModifier) {
        int tradeRefusal = (int) country.getTradeEmbargoes().stream().filter(c -> !country.getRivals().containsKey(c.getTag())).count();
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, NumbersUtils.intOrDefault(tradeRefusal));
    }

    private static Modifiers scaleWithMercantilism(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, NumbersUtils.intOrDefault(country.getMercantilism()));
    }

    private static Modifiers scaleWithArmyTradition(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, NumbersUtils.doubleOrDefault(country.getArmyTradition()));
    }

    private static Modifiers scaleWithNavyTradition(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, NumbersUtils.doubleOrDefault(country.getNavyTradition()));
    }

    private static Modifiers scaleWithFreeCitiesInHre(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, country.getSave()
                                                                              .getCountries()
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

    private static Modifiers scaleWithNumOfRoyalMarriages(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, NumbersUtils.intOrDefault(country.getNumOfRoyalMarriages()));
    }

    private static Modifiers scaleWithNumOfProvinces(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, NumbersUtils.intOrDefault(country.getOwnedProvinces().size()));
    }

    private static Modifiers scaleWithTribalAllegiance(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, NumbersUtils.doubleOrDefault(country.getTribalAllegiance()));
    }

    private static Modifiers scaleWithLegitimacy50(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, (NumbersUtils.doubleOrDefault(country.getLegitimacy()) - 50) / 100);
    }

    private static Modifiers scaleWithHordeUnity50(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, (NumbersUtils.doubleOrDefault(country.getHordeUnity()) - 50) / 100);
    }

    private static Modifiers scaleWithDevotion50(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, (NumbersUtils.doubleOrDefault(country.getDevotion()) - 50) / 100);
    }

    private static Modifiers scaleWithMeritocracy50(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, (NumbersUtils.doubleOrDefault(country.getMeritocracy()) - 50) / 100);
    }

    private static Modifiers scaleWithMeritocracy50Reverse(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, (50 - NumbersUtils.doubleOrDefault(country.getMeritocracy())) / 100);
    }

    private static Modifiers scaleWithCorruption(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, NumbersUtils.doubleOrDefault(country.getCorruption()));
    }

    private static Modifiers scaleWithRootOutCorruption(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, NumbersUtils.doubleOrDefault(country.getRootOutCorruptionSlider()));
    }

    private static Modifiers scaleWithRecoveryMotivation(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, NumbersUtils.doubleOrDefault(country.getRecoveryMotivation()));
    }

    private static Modifiers scaleWithMilitarisedSociety(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, NumbersUtils.doubleOrDefault(country.getMilitarisedSociety()) / 100);
    }

    private static Modifiers scaleWithOverextension(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, NumbersUtils.doubleOrDefault(country.getOverextensionPercentage()));
    }

    private static Modifiers scaleWithPrestige(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, NumbersUtils.doubleOrDefault(country.getPrestige()) / 100);
    }

    private static Modifiers scaleWithRepublicanTradition(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, NumbersUtils.doubleOrDefault(country.getRepublicanTradition()) / 100);
    }

    private static Modifiers scaleWithRepublicanTraditionReverse(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, (100 - NumbersUtils.doubleOrDefault(country.getRepublicanTradition())) / 100);
    }

    private static Modifiers scaleWithReligiousUnity(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, NumbersUtils.doubleOrDefault(country.getReligiousUnity()));
    }

    private static Modifiers scaleWithReligiousUnityReverse(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, 1 - NumbersUtils.doubleOrDefault(country.getReligiousUnity()));
    }

    private static Modifiers scaleWithOccupiedProvinces(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers,
                                             country.getOwnedProvinces()
                                                    .stream()
                                                    .filter(province -> !province.getOwner().equals(province.getController()))
                                                    .count());
    }

    private static Modifiers scaleWithBlockadedProvinces(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers,
                                             country.getOwnedProvinces()
                                                    .stream()
                                                    .filter(province -> NumbersUtils.doubleOrDefault(province.getBlockadeEfficiency()) >= 0)
                                                    .count());
    }

    private static Modifiers scaleWithNotControlledCores(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers,
                                             country.getCoreProvinces().stream().filter(province -> !province.getOwner().equals(country)).count());
    }

    private static Modifiers scaleWithNumOfAgeObjectives(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, NumbersUtils.intOrDefault(country.getNumOfAgeObjectives()));
    }

    private static Modifiers scaleWithOverlordAdm(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, (Math.max(0, country.getTech().getAdm() - country.getOverlord().getTech().getAdm())));
    }

    private static Modifiers scaleWithOverlordDip(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, (Math.max(0, country.getTech().getDip() - country.getOverlord().getTech().getDip())));
    }

    private static Modifiers scaleWithOverlordMil(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, (Math.max(0, country.getTech().getMil() - country.getOverlord().getTech().getMil())));
    }

    private static Modifiers scaleWithLibertyDesire(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, NumbersUtils.doubleOrDefault(country.getLibertyDesire()) / 100);
    }

    private static Modifiers scaleWithAbsolutism(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, NumbersUtils.intOrDefault(country.getAbsolutism()) / 100);
    }

    private static Modifiers scaleWithCurrentPowerProjection(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, NumbersUtils.doubleOrDefault(country.getCurrentPowerProjection()) / 100);
    }

    private static Modifiers scaleWithStrongCompany(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, country.getNumOfStrongCompanies());
    }

    private static Modifiers scaleWithLargeColony(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, country.getNumOfLargeColonies());
    }

    private static Modifiers scaleWithVassals(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, country.getNumOfSubjectsOfType(Eu4Utils.SUBJECT_TYPE_CLIENT_VASSAL));
    }

    private static Modifiers scaleWithMarches(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, country.getNumOfSubjectsOfType(Eu4Utils.SUBJECT_TYPE_MARCH));
    }

    private static Modifiers scaleWithUnions(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, country.getNumOfSubjectsOfType(Eu4Utils.SUBJECT_TYPE_PERSONAL_UNION));
    }

    private static Modifiers scaleWithDaimyos(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, country.getNumOfSubjectsOfType(Eu4Utils.SUBJECT_TYPE_DAIMYO_VASSAL));
    }

    private static Modifiers scaleWithInnovativeness(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, NumbersUtils.doubleOrDefault(country.getInnovativeness()) / 100);
    }

    private static Modifiers scaleWithNumExpandedAdministration(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, NumbersUtils.intOrDefault(country.getNumExpandedAdministration()));
    }

    private static Modifiers scaleWithNumTradeLeagueMembers(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, country.getTradeLeague().getMembers().size());
    }

    private static Modifiers scaleWithHarmony(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, NumbersUtils.doubleOrDefault(country.getHarmony()));
    }

    private static Modifiers scaleWithHarmonyReverse(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers, 1 - (NumbersUtils.doubleOrDefault(country.getHarmony()) / 100));
    }

    private static Modifiers scaleWithDaimyosAtPeace(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers,
                                             Math.min(10, country.getSubjects()
                                                                 .stream()
                                                                 .filter(subject -> !subject.isAtWar() &&
                                                                                    Eu4Utils.SUBJECT_TYPE_DAIMYO_VASSAL.equalsIgnoreCase(
                                                                                            subject.getSubjectType().getName()))
                                                                 .count()));
    }

    private static Modifiers scaleWithDaimyosSameIsolationism(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers,
                                             Math.min(10, country.getSubjects()
                                                                 .stream()
                                                                 .filter(subject ->
                                                                                 Eu4Utils.SUBJECT_TYPE_DAIMYO_VASSAL.equalsIgnoreCase(
                                                                                         subject.getSubjectType().getName())
                                                                                 &&
                                                                                 NumbersUtils.intOrDefault(country.getIsolationismLevel())
                                                                                 == NumbersUtils.intOrDefault(subject.getIsolationismLevel()))
                                                                 .count()));
    }

    private static Modifiers scaleWithDaimyosDifferentIsolationism(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers,
                                             Math.min(10, country.getSubjects()
                                                                 .stream()
                                                                 .filter(subject ->
                                                                                 Eu4Utils.SUBJECT_TYPE_DAIMYO_VASSAL.equalsIgnoreCase(
                                                                                         subject.getSubjectType().getName())
                                                                                 &&
                                                                                 NumbersUtils.intOrDefault(country.getIsolationismLevel())
                                                                                 != NumbersUtils.intOrDefault(subject.getIsolationismLevel()))
                                                                 .count()));
    }

    private static Modifiers scaleWithDaimyosSwordHunt(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers,
                                             country.getSubjects()
                                                    .stream()
                                                    .filter(subject -> Eu4Utils.SUBJECT_TYPE_DAIMYO_VASSAL.equalsIgnoreCase(
                                                            subject.getSubjectType().getName())
                                                                       && subject.getModifiers().stream()
                                                                                 .anyMatch(modifier -> "subject_sword_hunt".equalsIgnoreCase(
                                                                                         modifier.getModifier())))
                                                    .count());
    }

    private static Modifiers scaleWithStreltsyPercent(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers,
                                             BigDecimal.valueOf(country.getNbRegimentOfCategory(3))
                                                       .divide(BigDecimal.valueOf(country.getArmySize()), 0, RoundingMode.HALF_EVEN));
    }

    private static Modifiers scaleWithCossacksPercent(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers,
                                             BigDecimal.valueOf(country.getNbRegimentOfCategory(4))
                                                       .divide(BigDecimal.valueOf(country.getArmySize()), 0, RoundingMode.HALF_EVEN));
    }

    private static Modifiers scaleWithLowProfessionalism(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers,
                                             Math.min(Math.max(0, NumbersUtils.doubleOrDefault(country.getArmyProfessionalism()) -
                                                                  country.getSave().getGame().getLowArmyProfessionalismMinRange()),
                                                      country.getSave().getGame().getLowArmyProfessionalismMaxRange()));
    }

    private static Modifiers scaleWithHighProfessionalism(Country country, StaticModifier staticModifier) {
        return ModifiersUtils.scaleModifiers(staticModifier.modifiers,
                                             Math.min(Math.max(0, NumbersUtils.doubleOrDefault(country.getArmyProfessionalism()) -
                                                                  country.getSave().getGame().getHighArmyProfessionalismMinRange()),
                                                      country.getSave().getGame().getHighArmyProfessionalismMaxRange()));
    }
}
