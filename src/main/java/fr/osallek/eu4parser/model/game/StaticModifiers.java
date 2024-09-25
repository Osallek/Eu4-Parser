package fr.osallek.eu4parser.model.game;

import fr.osallek.eu4parser.model.game.condition.ConditionAbstract;
import fr.osallek.eu4parser.model.game.condition.ConditionAnd;
import fr.osallek.eu4parser.model.game.condition.ConditionNot;
import fr.osallek.eu4parser.model.save.country.SaveCountry;
import fr.osallek.eu4parser.model.save.gameplayoptions.Difficulty;
import fr.osallek.eu4parser.model.save.province.SaveProvince;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum StaticModifiers {
    DIFFICULTY_VERY_EASY_PLAYER(new ConditionAnd(Pair.of("ai", "no"), Pair.of("difficulty", Difficulty.VERY_EASY.name())), null, null),
    DIFFICULTY_EASY_PLAYER(new ConditionAnd(Pair.of("ai", "no"), Pair.of("difficulty", Difficulty.EASY.name())), null, null),
    DIFFICULTY_NORMAL_PLAYER(new ConditionAnd(Pair.of("ai", "no"), Pair.of("difficulty", Difficulty.NORMAL.name())), null, null),
    DIFFICULTY_HARD_PLAYER(new ConditionAnd(Pair.of("ai", "no"), Pair.of("difficulty", Difficulty.HARD.name())), null, null),
    DIFFICULTY_VERY_HARD_PLAYER(new ConditionAnd(Pair.of("ai", "no"), Pair.of("difficulty", Difficulty.VERY_HARD.name())), null, null),
    DIFFICULTY_VERY_EASY_AI(new ConditionAnd(Pair.of("ai", "yes"), Pair.of("difficulty", Difficulty.VERY_EASY.name())), null, null),
    DIFFICULTY_EASY_AI(new ConditionAnd(Pair.of("ai", "yes"), Pair.of("difficulty", Difficulty.EASY.name())), null, null),
    DIFFICULTY_NORMAL_AI(new ConditionAnd(Pair.of("ai", "yes"), Pair.of("difficulty", Difficulty.NORMAL.name())), null, null),
    DIFFICULTY_HARD_AI(new ConditionAnd(Pair.of("ai", "yes"), Pair.of("difficulty", Difficulty.HARD.name())), null, null),
    DIFFICULTY_VERY_HARD_AI(new ConditionAnd(Pair.of("ai", "yes"), Pair.of("difficulty", Difficulty.VERY_HARD.name())), null, null),
    CITY(new ConditionAnd(Pair.of("is_city", "yes")), null, null),
    PORT(new ConditionAnd(Pair.of("has_port", "yes")), null, null),
    IN_STATE(new ConditionAnd(Pair.of("is_state", "yes")), null, null),
    IN_CAPITAL_STATE(new ConditionAnd(Pair.of("in_capital_state", "yes")), null, null),
    COASTAL(new ConditionAnd(Pair.of("is_ocean", "yes")), null, null), //Todo sea province with coast adjacent
    SEAT_IN_PARLIAMENT(new ConditionAnd(Pair.of("has_seat_in_parliament", "yes")), null, null),
    NON_COASTAL(new ConditionAnd(Pair.of("is_ocean", "no")), null, null), //Todo sea province without coast adjacent
    COASTAL_SEA(new ConditionAnd(Pair.of("is_ocean", "yes")), null, null), //Todo sea province with coast adjacent
    TROPICAL(new ConditionAnd(Pair.of("has_climate", "tropical")), null, null),
    ARCTIC(new ConditionAnd(Pair.of("has_climate", "arctic")), null, null),
    ARID(new ConditionAnd(Pair.of("has_climate", "arid")), null, null),
    SEA_ZONE(new ConditionAnd(Pair.of("is_ocean", "yes")), null, null), //Todo sea province with a port in it
    LAND_PROVINCE(new ConditionAnd(Pair.of("is_ocean", "no"), Pair.of("is_lake", "no"), Pair.of("is_wasteland", "no")), null, null),
    MILD_WINTER(new ConditionAnd(Pair.of("has_winter", "mild_winter")), null, null),
    NORMAL_WINTER(new ConditionAnd(Pair.of("has_winter", "normal_winter")), null, null),
    SEVERE_WINTER(new ConditionAnd(Pair.of("has_winter", "severe_winter")), null, null),
    MILD_MONSOON(new ConditionAnd(Pair.of("has_monsoon", "mild_monsoon")), null, null),
    NORMAL_MONSOON(new ConditionAnd(Pair.of("has_monsoon", "normal_monsoon")), null, null),
    SEVERE_MONSOON(new ConditionAnd(Pair.of("has_monsoon", "severe_monsoon")), null, null),
    BLOCKADED(new ConditionAnd(Pair.of("is_blockaded", "yes")), null, null),
    NO_ADJACENT_CONTROLLED(new ConditionAnd(Pair.of("always", "no")), null, null), //Todo
    PROVINCIAL_TAX_INCOME(new ConditionAnd(Pair.of("is_ocean", "no"), Pair.of("is_lake", "no"), Pair.of("is_wasteland", "no")), null, null),
    PROVINCIAL_PRODUCTION_SIZE(new ConditionAnd(Pair.of("is_ocean", "no"), Pair.of("is_lake", "no"), Pair.of("is_wasteland", "no")), null, null),
    KNOWLEDGE_SHARING(new ConditionAnd(Pair.of("knowledge_sharing", "yes")), null, null),
    CARDINALS_SPREAD_INSTITUTION(new ConditionAnd(Pair.of("has_cardinal", "yes")), null, null),
    MANPOWER(new ConditionAnd(Pair.of("is_ocean", "no"), Pair.of("is_lake", "no"), Pair.of("is_wasteland", "no")), null, null),
    SAILORS(new ConditionAnd(Pair.of("is_ocean", "no"), Pair.of("is_lake", "no"), Pair.of("is_wasteland", "no")), null, null),
    HORDE_DEVELOPMENT(new ConditionAnd(Pair.of("nomad_development_scale", "yes")), null, null),
    PROVINCE_RAZED(new ConditionAnd(Pair.of("has_province_modifier", "province_razed")), null, null),
    DEVELOPMENT(new ConditionAnd(Pair.of("always", "yes")), null, null),
    DEVELOPMENT_SCALED(new ConditionAnd(Pair.of("always", "yes")), null, null),
    CAPITAL_CITY(new ConditionAnd(Pair.of("capital", "yes")), null, null),
    PATRIARCH_STATE(new ConditionAnd(Pair.of("has_state_patriach", "yes")), null, null),
    PATRIARCH_AUTHORITY_LOCAL(new ConditionAnd(Pair.of("has_owner_religion", "yes"), Pair.of("owner_has_patriarchs", "yes")), null, null),
    PATRIARCH_AUTHORITY_GLOBAL(new ConditionAnd(Pair.of("has_patriarchs", "yes")), null, null),
    PASHA_STATE(new ConditionAnd(Pair.of("has_state_pasha", "yes")), null, null),
    COLONY_LEVEL(new ConditionAnd(Pair.of("is_colony", "yes")), null, null),
    NATIVE_ASSIMILATION(new ConditionAnd(Pair.of("is_colony", "yes")), null, null), //Fixme modifier
    NATIVE_AGGRESSIVENESS(new ConditionAnd(Pair.of("is_colony", "yes")), null, null),
    CORE(new ConditionAnd(Pair.of("is_state", "yes"), Pair.of("is_cored", "yes")), null, null),
    NON_CORE(new ConditionAnd(Pair.of("is_cored", "no"), Pair.of("is_state", "yes")), null, null),
    TERRITORY_CORE(new ConditionAnd(Pair.of("is_territory", "yes"), Pair.of("is_cored", "yes")), null, null),
    TERRITORY_NON_CORE(new ConditionAnd(Pair.of("is_territory", "yes"), Pair.of("is_cored", "no")), null, null),
    MARCH_BONUS(new ConditionAnd(Pair.of("is_march", "yes")), null, null),
    SAME_CULTURE_GROUP(new ConditionAnd(Pair.of("has_owner_accepted_culture", "no"), Pair.of("has_owner_culture_group", "yes")), null, null),
    NON_ACCEPTED_CULTURE(new ConditionAnd(Pair.of("has_owner_accepted_culture", "no")), null, null),
    ACCEPTED_CULTURE_DEMOTED(new ConditionAnd(Pair.of("has_province_modifier", "accepted_culture_demoted")), null, null),
    NON_ACCEPTED_CULTURE_REPUBLIC(new ConditionAnd(Pair.of("non_accepted_culture_republic", "no")), null, null),
    OCCUPIED(new ConditionAnd(Pair.of("is_occupied", "yes")), null, null),
    UNDER_SIEGE(new ConditionAnd(Pair.of("unit_in_siege", "yes")), null, null),
    DEVASTATION(new ConditionAnd(Pair.of("has_devastation", "yes")), null, null),
    PROSPERITY(new ConditionAnd(Pair.of("is_prosperous", "yes")), null, null),
    SLAVES_RAIDED(new ConditionAnd(Pair.of("has_province_modifier", "slaves_raided")), null, null),
    TOLERANCE(new ConditionAnd(Pair.of("always", "yes")), null, null),
    INTOLERANCE(new ConditionAnd(Pair.of("always", "yes")), null, null),
    UNREST(new ConditionAnd(Pair.of("always", "yes")), null, null),
    NATIONALISM(new ConditionAnd(Pair.of("always", "yes")), null, null),
    HARSH_TREATMENT(new ConditionAnd(Pair.of("always", "yes")), null, null), //Fixme Don't know how to parse
    LOCAL_AUTONOMY_MULTIPLICATIVE(new ConditionAnd(Pair.of("is_owned_by_trade_company", "no")), null, null),
    LOCAL_AUTONOMY(new ConditionAnd(Pair.of("always", "yes")), null, null),
    LOCAL_AUTONOMY_TRADE_COMPANY_MULTIPLICATIVE(new ConditionAnd(Pair.of("is_owned_by_trade_company", "yes")), null, null),
    LOCAL_AUTONOMY_TRADE_COMPANY(new ConditionAnd(Pair.of("always", "yes")), null, null),
    RECENT_UPRISING(new ConditionAnd(Pair.of("has_province_modifier", "recent_uprising")), null, null),
    FRIENDLY_REGIMENTS(new ConditionAnd(Pair.of("units_in_province", "1")), null, null),
    ACTIVE_MISSIONARY(new ConditionAnd(Pair.of("has_missionary", "yes")), null, null),
    NATIONAL_DEFENSE(new ConditionAnd(Pair.of("national_defense", "yes")), null, null),
    RESOURCE_DEPLETED(new ConditionAnd(Pair.of("has_province_modifier", "resource_depleted")), null, null),
    IN_TRADE_COMPANY(new ConditionAnd(Pair.of("is_owned_by_trade_company", "yes")), null, null),
    LEFT_TRADE_COMPANY(new ConditionAnd(Pair.of("has_province_modifier", "left_trade_company")), null, null),
    SCORCHED_EARTH(new ConditionAnd(Pair.of("has_province_modifier", "scorched_earth")), null, null),
    BASE_VALUES(new ConditionAnd(Pair.of("always", "yes")), null, null),
    AI_NATION(new ConditionAnd(Pair.of("ai", "yes")), null, null),
    WAR_TAXES(new ConditionAnd(Pair.of("has_wartaxes", "yes")), null, null),
    STABILITY(new ConditionAnd(Pair.of("always", "yes")), null, null),
    POSITIVE_STABILITY(new ConditionAnd(Pair.of("stability", "1")), null, null),
    PRIVATEERING(new ConditionAnd(Pair.of("has_privateers", "yes")), null, null),
    NEGATIVE_STABILITY(new ConditionNot(Pair.of("stability", "0")), null, null),
    LOST_MANDATE_OF_HEAVEN(new ConditionAnd(Pair.of("has_country_modifier", "lost_mandate_of_heaven")), null, null),
    CANCELLED_LOAN(new ConditionAnd(Pair.of("has_country_modifier", "cancelled_loan")), null, null),
    BANK_LOAN(new ConditionAnd(Pair.of("num_of_loans", "1")), null, null),
    INFLATION(new ConditionAnd(Pair.of("inflation", "0.001")), null, null),
    BANKRUPTCY(new ConditionAnd(Pair.of("is_bankrupt", "yes")), null, null),
    WAR(new ConditionAnd(Pair.of("is_at_war", "yes")), null, null),
    PEACE(new ConditionAnd(Pair.of("is_at_war", "no")), null, null),
    UNCONDITIONAL_SURRENDER(new ConditionAnd(Pair.of("has_unconditional_surrender", "yes")), null, null),
    CALL_FOR_PEACE(new ConditionAnd(Pair.of("call_for_peace", "1")), null, null),
    WAR_EXHAUSTION(new ConditionAnd(Pair.of("war_exhaustion", "0.001")), null, null),
    DOOM(new ConditionAnd(Pair.of("doom", "0.001")), null, null),
    AUTHORITY(new ConditionAnd(Pair.of("authority", "0.001")), null, null),
    REGENCY_COUNCIL(new ConditionAnd(Pair.of("has_regency", "yes")), null, null),
    TRADE_EFFICIENCY(new ConditionAnd(Pair.of("always", "yes")), null, null),
    PRODUCTION_EFFICIENCY(new ConditionAnd(Pair.of("always", "yes")), null, null),
    TRADE_REFUSAL(new ConditionAnd(Pair.of("num_of_trade_embargos", "1")), null, null),
    MERCANTILISM(new ConditionAnd(Pair.of("always", "yes")), null, null),
    ARMY_TRADITION(new ConditionAnd(Pair.of("army_tradition", "0.001")), null, null),
    NAVY_TRADITION(new ConditionAnd(Pair.of("navy_tradition", "0.001")), null, null),
    POSITIVE_PIETY(new ConditionAnd(Pair.of("uses_piety", "yes")), null, null),
    NEGATIVE_PIETY(new ConditionAnd(Pair.of("uses_piety", "yes")), null, null),
    DEFENDER_OF_FAITH(new ConditionAnd(Pair.of("is_defender_of_faith", "yes")), null, null),
    DEFENDER_OF_FAITH_REFUSED_CTA(new ConditionAnd(Pair.of("has_country_modifier", "defender_of_faith_refused_cta")), null, null),
    EMPEROR(new ConditionAnd(Pair.of("is_emperor", "yes")), null, null),
    STATES_IN_HRE(new ConditionAnd(Pair.of("is_emperor", "yes")), null, null),
    FREE_CITIES_IN_HRE(new ConditionAnd(Pair.of("is_emperor", "yes")), null, null),
    FREE_CITY_IN_HRE(new ConditionAnd(Pair.of("is_free_city", "yes")), null, null),
    MEMBER_IN_HRE(new ConditionAnd(Pair.of("is_part_of_hre", "yes")), null, null),
    OCCUPIED_IMPERIAL(new ConditionAnd(Pair.of("occupied_imperial", "1")), null, null),
    NUM_OF_MARRIAGES(new ConditionAnd(Pair.of("num_of_royal_marriages", "1")), null, null),
    NUM_OF_PROVINCES(new ConditionAnd(Pair.of("num_of_cities", "1")), null, null),
    COUNTRY_DEVELOPMENT(new ConditionAnd(Pair.of("total_development", "1")), null, null),
    TRIBAL_ALLEGIANCE(new ConditionAnd(Pair.of("has_government_ability", "tribal_federation_mechanic")), null, null),
    LEGITIMACY(new ConditionAnd(Pair.of("legitimacy", "0.001")), null, null), //Fixme -> uses_legitimacy
    HORDE_UNITY(new ConditionAnd(Pair.of("has_horde_unity", "yes")), null, null),
    DEVOTION(new ConditionAnd(Pair.of("uses_devotion", "yes")), null, null),
    MERITOCRACY(new ConditionAnd(Pair.of("has_meritocracy", "yes")), null, null),
    LOW_MERITOCRACY(new ConditionAnd(Pair.of("has_meritocracy", "yes")), null, null),
    CORRUPTION(new ConditionAnd(Pair.of("always", "yes")), null, null),
    ROOT_OUT_CORRUPTION(new ConditionAnd(Pair.of("always", "yes")), null, null),
    RECOVERY_MOTIVATION(new ConditionAnd(Pair.of("always", "yes")), null, null),
    MILITARIZED_SOCIETY(new ConditionAnd(Pair.of("has_militarised_society", "yes")), null, null),
    LUCK(new ConditionAnd(Pair.of("luck", "yes")), null, null),
    OVER_EXTENSION(new ConditionAnd(Pair.of("overextension_percentage", "0.001")), null, null),
    PRESTIGE(new ConditionAnd(Pair.of("always", "yes")), null, null),
    NO_DEBATE_IN_PARLIAMENT(new ConditionAnd(Pair.of("has_active_debate", "no")), null, null),
    REPUBLICAN_TRADITION(new ConditionAnd(Pair.of("is_republic", "yes")), null, null),
    INVERSE_REPUBLICAN_TRADITION(new ConditionAnd(Pair.of("is_republic", "yes")), null, null),
    CURIA_CONTROLLER(new ConditionAnd(Pair.of("is_papal_controller", "yes")), null, null),
    BOUGHT_INDULGENCE(new ConditionAnd(Pair.of("has_country_modifier", "bought_indulgence")), null, null),
    RELIGIOUS_UNITY(new ConditionAnd(Pair.of("always", "yes")), null, null),
    INVERSE_RELIGIOUS_UNITY(new ConditionAnd(Pair.of("always", "yes")), null, null),
    TOTAL_OCCUPATION(new ConditionAnd(Pair.of("always", "yes")), null, null),
    TOTAL_BLOCKADED(new ConditionAnd(Pair.of("always", "yes")), null, null),
    UNCONTESTED_CORES(new ConditionAnd(Pair.of("num_uncontested_cores", "1")), null, null),
    NUM_OBJECTIVES_FULLFILLED(new ConditionAnd(Pair.of("num_of_age_objectives", "1")), null, null),
    PRODUCTION_LEADER(new ConditionAnd(Pair.of("production_leader", "yes")), null, null),
    TRADE_COMPANY_BONUS(new ConditionAnd(Pair.of("always", "false")), null, null), //Fixme ?? //Scale
    BONUS_FROM_MERCHANT_REPUBLICS(new ConditionAnd(Pair.of("always", "false")), null, null), //Fixme ?? //Scale
    BONUS_FROM_MERCHANT_REPUBLICS_FOR_TRADE_LEAGUE_MEMBER(new ConditionAnd(Pair.of("always", "false")), null, null), //Fixme ?? //Scale
    MERCHANT_REPUBLIC_MECHANICS_MODIFIER(new ConditionAnd(Pair.of("always", "false")), null, null), //Fixme ?? //Scale
    FEDERATION_LEADER(new ConditionAnd(Pair.of("is_federation_leader", "yes")), null, null),
    TRIBUTARY_STATE_BEHIND_OVERLORD_TECH_ADM(new ConditionAnd(Pair.of("is_tributary", "yes")), null, null),
    TRIBUTARY_STATE_BEHIND_OVERLORD_TECH_DIP(new ConditionAnd(Pair.of("is_tributary", "yes")), null, null),
    TRIBUTARY_STATE_BEHIND_OVERLORD_TECH_MIL(new ConditionAnd(Pair.of("is_tributary", "yes")), null, null),
    LIBERTY_DESIRE(new ConditionAnd(Pair.of("liberty_desire", "0.001")), null, null),
    IN_GOLDEN_ERA(new ConditionAnd(Pair.of("in_golden_age", "yes")), null, null),
    ABSOLUTISM(new ConditionAnd(Pair.of("absolutism", "0")), null, null),
    LOW_ARMY_PROFESSIONALISM(new ConditionAnd(Pair.of("low_army_professionalism", "yes")), null, null),
    HIGH_ARMY_PROFESSIONALISM(new ConditionAnd(Pair.of("high_army_professionalism", "yes")), null, null),
    STRELTSY_MODIFIER(new ConditionAnd(Pair.of("num_of_streltsy", "1")), null, null),
    POWER_PROJECTION(new ConditionAnd(Pair.of("power_projection", "0.001")), null, null),
    POWER_PROJECTION_25(new ConditionAnd(Pair.of("power_projection", "25")), null, null),
    TRADE_COMPANY_STRONG(new ConditionAnd(Pair.of("num_of_strong_trade_companies", "1")), null, null),
    TRADE_COMPANY_DOMINANT(new ConditionAnd(Pair.of("always", "no")), null, null), //Fixme ?? //Scale
    LARGE_COLONIAL_NATION(new ConditionAnd(Pair.of("num_of_large_colonial_nation", "1")), null, null),
    MARCH_SUBJECT(new ConditionAnd(Pair.of("num_of_marches", "1")), null, null),
    VASSAL_SUBJECT(new ConditionAnd(Pair.of("num_of_vassals", "1")), null, null),
    DAIMYO_SUBJECT(new ConditionAnd(Pair.of("num_of_daimyos", "1")), null, null),
    UNION_SUBJECT(new ConditionAnd(Pair.of("num_of_unions", "1")), null, null),
    ALL_NATIONS(new ConditionAnd(Pair.of("always", "yes")), null, null),
    SUBJECT_NATION(new ConditionAnd(Pair.of("is_subject", "yes")), null, null),
    VASSAL_NATION(new ConditionAnd(Pair.of("is_vassal", "yes")), null, null),
    PRIMITIVE_NATION(new ConditionAnd(Pair.of("primitives", "yes")), null, null),
    MAINTAINED_FORTS(new ConditionAnd(Pair.of("always", "yes")), null, null),
    GOV_RANK_1(new ConditionAnd(Pair.of("government_rank", "1")), null, null),
    GOV_RANK_2(new ConditionAnd(Pair.of("government_rank", "2")), null, null),
    GOV_RANK_3(new ConditionAnd(Pair.of("government_rank", "3")), null, null),
    GOV_RANK_4(new ConditionAnd(Pair.of("government_rank", "4")), null, null),
    GOV_RANK_5(new ConditionAnd(Pair.of("government_rank", "5")), null, null),
    GOV_RANK_6(new ConditionAnd(Pair.of("government_rank", "6")), null, null),
    GOV_RANK_7(new ConditionAnd(Pair.of("government_rank", "7")), null, null),
    GOV_RANK_8(new ConditionAnd(Pair.of("government_rank", "8")), null, null),
    GOV_RANK_9(new ConditionAnd(Pair.of("government_rank", "9")), null, null),
    GOV_RANK_10(new ConditionAnd(Pair.of("government_rank", "10")), null, null),
    AUTONOMY_INCREASED(new ConditionAnd(Pair.of("has_province_modifier", "autonomy_increased")), null, null),
    AUTONOMY_DECREASED(new ConditionAnd(Pair.of("has_province_modifier", "autonomy_decreased")), null, null),
    REVOLUTION_TARGET(new ConditionAnd(Pair.of("is_revolution_target", "yes")), null, null),
    RECRUITMENT_SABOTAGED(new ConditionAnd(Pair.of("has_country_modifier", "recruitment_sabotaged")), null, null),
    MERCHANTS_SLANDERED(new ConditionAnd(Pair.of("has_country_modifier", "merchants_slandered")), null, null),
    DISCONTENT_SOWED(new ConditionAnd(Pair.of("has_country_modifier", "discontent_sowed")), null, null),
    REPUTATION_SABOTAGED(new ConditionAnd(Pair.of("has_country_modifier", "reputation_sabotaged")), null, null),
    CORRUPT_OFFICIALS(new ConditionAnd(Pair.of("has_country_modifier", "corrupt_officials")), null, null),
    SCALED_TRADE_LEAGUE_LEADER(new ConditionAnd(Pair.of("is_trade_league_leader", "yes")), null, null),
    IN_TRADE_LEAGUE(new ConditionAnd(Pair.of("is_in_trade_league", "yes")), null, null),
    CUSTOM_SETUP(new ConditionAnd(Pair.of("custom_nation_setup", "yes")), null, null),
    EMBARGO_RIVALS(new ConditionAnd(Pair.of("has_embargo_rivals", "yes")), null, null),
    SCUTAGE(new ConditionAnd(Pair.of("has_scutage", "yes")), null, null),
    SUBSIDIZE_ARMIES(new ConditionAnd(Pair.of("has_subsidize_armies", "yes")), null, null),
    SUPPORT_LOYALISTS(new ConditionAnd(Pair.of("has_support_loyalists", "yes")), null, null),
    SEND_OFFICERS(new ConditionAnd(Pair.of("has_send_officers", "yes")), null, null),
    DIVERT_TRADE(new ConditionAnd(Pair.of("has_divert_trade", "yes")), null, null),
    INVASION_NATION(new ConditionAnd(Pair.of("invasion_nation", "yes")), null, null),
    NATIVE_POLICY_COEXIST(new ConditionAnd(Pair.of("native_policy", "1")), null, null),
    NATIVE_POLICY_TRADE(new ConditionAnd(Pair.of("native_policy", "2")), null, null),
    NATIVE_POLICY_HOSTILE(new ConditionAnd(Pair.of("native_policy", "3")), null, null),
    HIGH_HARMONY(new ConditionAnd(Pair.of("uses_harmony", "yes")), null, null),
    LOW_HARMONY(new ConditionAnd(Pair.of("uses_harmony", "yes")), null, null),
    OVERLORD_DAIMYO_AT_PEACE(new ConditionAnd(Pair.of("num_of_daimyos", "1")), null, null),
    //    OVERLORD_DAIMYO_AT_PEACE_MAX(new Condition(Pair.of("num_of_daimyos", "1")), applyToCountry, null),
    //    OVERLORD_DAIMYO_AT_PEACE_MIN(new Condition(Pair.of("num_of_daimyos", "1")), applyToCountry, null),
    OVERLORD_DAIMYO_SAME_ISOLATIONISM(new ConditionAnd(Pair.of("num_of_daimyos", "1")), null, null),
    OVERLORD_DAIMYO_DIFFERENT_ISOLATIONISM(new ConditionAnd(Pair.of("num_of_daimyos", "1")), null, null),
    //    OVERLORD_DAIMYO_ISOLATIONISM_MAX(new Condition(Pair.of("num_of_daimyos", "1")), applyToCountry, null),
    //    OVERLORD_DAIMYO_ISOLATIONISM_MIN(new Condition(Pair.of("num_of_daimyos", "1")), applyToCountry, null),
    OVERLORD_SANKIN_KOTAI(new ConditionAnd(Pair.of("has_country_modifier", "overlord_sankin_kotai")), null, null),
    SUBJECT_SANKIN_KOTAI(new ConditionAnd(Pair.of("has_country_modifier", "subject_sankin_kotai")), null, null),
    OVERLORD_EXPEL_RONIN(new ConditionAnd(Pair.of("has_country_modifier", "overlord_expel_ronin")), null, null),
    SUBJECT_EXPEL_RONIN(new ConditionAnd(Pair.of("has_country_modifier", "subject_expel_ronin")), null, null),
    OVERLORD_SWORD_HUNT(new ConditionAnd(Pair.of("num_of_daimyos", "1")), null, null),
    SUBJECT_SWORD_HUNT(new ConditionAnd(Pair.of("has_country_modifier", "subject_sword_hunt")), null, null),
    SUPPLY_DEPOT_AREA(new ConditionAnd(Pair.of("has_supply_depot", "yes")), null, null),
    EFFICIENT_TAX_FARMING_MODIFIER(new ConditionAnd(Pair.of("has_country_modifier", "efficient_tax_farming_modifier")), null, null),
    LAND_ACQUISITION_MODIFIER(new ConditionAnd(Pair.of("has_country_modifier", "land_acquisition_modifier")), null, null),
    LENIENT_TAXATION_MODIFIER(new ConditionAnd(Pair.of("has_country_modifier", "lenient_taxation_modifier")), null, null),
    TRAIN_HORSEMANSHIP_MODIFIER(new ConditionAnd(Pair.of("has_country_modifier", "train_horsemanship_modifier")), null, null),
    PROMOTE_CULTURE_IN_GOVERNMENT_MODIFIER(new ConditionAnd(Pair.of("has_country_modifier", "promote_culture_in_government_modifier")), null, null),
    SEIZE_CLERICAL_HOLDINGS_MODIFIER(new ConditionAnd(Pair.of("has_country_modifier", "seize_clerical_holdings_modifier")), null, null),
    INVITE_MINORITIES_MODIFIER(new ConditionAnd(Pair.of("has_country_modifier", "invite_minorities_modifier")), null, null),
    HANAFI_SCHOLAR_MODIFIER(new ConditionAnd(Pair.of("has_country_modifier", "hanafi_scholar_modifier")), null, null),
    HANBALI_SCHOLAR_MODIFIER(new ConditionAnd(Pair.of("has_country_modifier", "hanbali_scholar_modifier")), null, null),
    MALIKI_SCHOLAR_MODIFIER(new ConditionAnd(Pair.of("has_country_modifier", "maliki_scholar_modifier")), null, null),
    SHAFII_SCHOLAR_MODIFIER(new ConditionAnd(Pair.of("has_country_modifier", "shafii_scholar_modifier")), null, null),
    ISMAILI_SCHOLAR_MODIFIER(new ConditionAnd(Pair.of("has_country_modifier", "ismaili_scholar_modifier")), null, null),
    JAFARI_SCHOLAR_MODIFIER(new ConditionAnd(Pair.of("has_country_modifier", "jafari_scholar_modifier")), null, null),
    ZAIDI_SCHOLAR_MODIFIER(new ConditionAnd(Pair.of("has_country_modifier", "zaidi_scholar_modifier")), null, null),
    DISHONOURED_ALLIANCE(new ConditionAnd(Pair.of("has_country_modifier", "dishonoured_alliance")), null, null),
    //    JANISSARY_REGIMENT(new Condition(Pair.of())),
    //    REVOLUTIONARY_GUARD_REGIMENT(new Condition(Pair.of())),
    INNOVATIVENESS(new ConditionAnd(Pair.of("innovativeness", "0.001")), null, null), //Scale
    //    RAJPUT_REGIMENT(new Condition(Pair.of())),
    RAIDING_PARTIES_MODIFIER(new ConditionAnd(Pair.of("has_country_modifier", "raiding_parties_modifier")), null, null),
    SERFS_RECIEVED_BY_COSSACKS(new ConditionAnd(Pair.of("has_country_modifier", "serfs_recieved_by_cossacks")), null, null),
    COSSACKS_MODIFIER(new ConditionAnd(Pair.of("num_of_cossacks", "1")), null, null),
    EXPAND_ADMINISTATION_MODIFIER(new ConditionAnd(Pair.of("num_expanded_administration", "1")), null, null),
    OVER_GOVERNING_CAPACITY_MODIFIER(new ConditionAnd(Pair.of("always", "yes")), null, null),
    UNDER_GOVERNING_CAPACITY_MODIFIER(new ConditionAnd(Pair.of("always", "yes")), null, null),
    REVERSE_UNDER_GOVERNING_CAPACITY_MODIFIER(new ConditionAnd(Pair.of("always", "yes")), null, null),
    MANDATE(new ConditionAnd(Pair.of("is_emperor_of_china", "yes")), null, null),
    IMPERIAL_AUTHORITY(new ConditionAnd(Pair.of("is_emperor", "yes")), null, null),
    POSITIVE_IMPERIAL_AUTHORITY(new ConditionAnd(Pair.of("is_emperor", "yes")), null, null),
    NEGATIVE_IMPERIAL_AUTHORITY(new ConditionAnd(Pair.of("is_emperor", "yes")), null, null),
    LOST_HEGEMONY(new ConditionAnd(Pair.of("has_country_modifier", "lost_hegemony")), null, null),
    PAPAL_INFLUENCE(new ConditionAnd(Pair.of("papacy_active", "yes")), null, null),
    CHURCH_POWER(new ConditionAnd(Pair.of("uses_church_aspects", "yes")), null, null),
    FERVOR(new ConditionAnd(Pair.of("uses_fervor", "yes")), null, null),
    KARMA(new ConditionAnd(Pair.of("uses_karma", "yes")), null, null),
    POSITIVE_KARMA(new ConditionAnd(Pair.of("uses_karma", "yes")), null, null),
    NEGATIVE_KARMA(new ConditionAnd(Pair.of("uses_karma", "yes")), null, null),
    DIPLOMATIC_REPUTATION(new ConditionAnd(Pair.of("always", "yes")), null, null),
    TOLERANCE_OWN(new ConditionAnd(Pair.of("always", "yes")), null, null),
    TOLERANCE_HERETIC(new ConditionAnd(Pair.of("always", "yes")), null, null),
    TOLERANCE_HEATHEN(new ConditionAnd(Pair.of("always", "yes")), null, null),
    NUM_ACCEPTED_CULTURES(new ConditionAnd(Pair.of("always", "yes")), null, null),
    RULER_ADM(new ConditionAnd(Pair.of("always", "yes")), null, null),
    RULER_DIP(new ConditionAnd(Pair.of("always", "yes")), null, null),
    RULER_MIL(new ConditionAnd(Pair.of("always", "yes")), null, null),
    HEIR_ADM(new ConditionAnd(Pair.of("has_heir", "yes")), null, null),
    HEIR_DIP(new ConditionAnd(Pair.of("has_heir", "yes")), null, null),
    HEIR_MIL(new ConditionAnd(Pair.of("has_heir", "yes")), null, null),
    CONSORT_ADM(new ConditionAnd(Pair.of("has_consort", "yes")), null, null),
    CONSORT_DIP(new ConditionAnd(Pair.of("has_consort", "yes")), null, null),
    CONSORT_MIL(new ConditionAnd(Pair.of("has_consort", "yes")), null, null),
    MANPOWER_PERCENTAGE(new ConditionAnd(Pair.of("always", "yes")), null, null),
    MANPOWER_PERCENTAGE_REVERSE(new ConditionAnd(Pair.of("always", "yes")), null, null),
    SAILORS_PERCENTAGE(new ConditionAnd(Pair.of("always", "yes")), null, null),
    SAILORS_PERCENTAGE_REVERSE(new ConditionAnd(Pair.of("always", "yes")), null, null);

    public final ConditionAbstract trigger;

    public Modifiers modifiers;

    public BiFunction<SaveCountry, StaticModifiers, Modifiers> applyToCountry;

    public BiFunction<SaveProvince, StaticModifiers, Modifiers> applyToProvince;

    private static final Map<String, StaticModifiers> STATIC_MODIFIERS_MAP;

    public static final List<StaticModifiers> APPLIED_TO_COUNTRY = new ArrayList<>();

    public static final List<StaticModifiers> APPLIED_TO_PROVINCE = new ArrayList<>();

    static {
        STATIC_MODIFIERS_MAP = Arrays.stream(StaticModifiers.values()).collect(Collectors.toMap(Enum::name, Function.identity()));

        DIFFICULTY_VERY_EASY_PLAYER.applyToCountry = (country, modif) -> StaticModifiers.DIFFICULTY_VERY_EASY_PLAYER.modifiers;
        DIFFICULTY_EASY_PLAYER.applyToCountry = (country, modif) -> StaticModifiers.DIFFICULTY_EASY_PLAYER.modifiers;
        DIFFICULTY_NORMAL_PLAYER.applyToCountry = (country, modif) -> StaticModifiers.DIFFICULTY_NORMAL_PLAYER.modifiers;
        DIFFICULTY_HARD_PLAYER.applyToCountry = (country, modif) -> StaticModifiers.DIFFICULTY_HARD_PLAYER.modifiers;
        DIFFICULTY_VERY_HARD_PLAYER.applyToCountry = (country, modif) -> StaticModifiers.DIFFICULTY_VERY_HARD_PLAYER.modifiers;
        DIFFICULTY_VERY_EASY_AI.applyToCountry = (country, modif) -> StaticModifiers.DIFFICULTY_VERY_EASY_AI.modifiers;
        DIFFICULTY_EASY_AI.applyToCountry = (country, modif) -> StaticModifiers.DIFFICULTY_EASY_AI.modifiers;
        DIFFICULTY_NORMAL_AI.applyToCountry = (country, modif) -> StaticModifiers.DIFFICULTY_NORMAL_AI.modifiers;
        DIFFICULTY_HARD_AI.applyToCountry = (country, modif) -> StaticModifiers.DIFFICULTY_HARD_AI.modifiers;
        DIFFICULTY_VERY_HARD_AI.applyToCountry = (country, modif) -> StaticModifiers.DIFFICULTY_VERY_HARD_AI.modifiers;
        CITY.applyToProvince = (province, modif) -> StaticModifiers.CITY.modifiers;
        PORT.applyToProvince = (province, modif) -> StaticModifiers.PORT.modifiers;
        IN_STATE.applyToProvince = (province, modif) -> StaticModifiers.IN_STATE.modifiers;
        IN_CAPITAL_STATE.applyToProvince = (province, modif) -> StaticModifiers.IN_CAPITAL_STATE.modifiers;
        COASTAL.applyToProvince = (province, modif) -> StaticModifiers.COASTAL.modifiers;
        SEAT_IN_PARLIAMENT.applyToProvince = (province, modif) -> StaticModifiers.SEAT_IN_PARLIAMENT.modifiers;
        NON_COASTAL.applyToProvince = (province, modif) -> StaticModifiers.NON_COASTAL.modifiers;
        COASTAL_SEA.applyToProvince = (province, modif) -> StaticModifiers.COASTAL_SEA.modifiers;
        TROPICAL.applyToProvince = (province, modif) -> StaticModifiers.TROPICAL.modifiers;
        ARCTIC.applyToProvince = (province, modif) -> StaticModifiers.ARCTIC.modifiers;
        ARID.applyToProvince = (province, modif) -> StaticModifiers.ARID.modifiers;
        SEA_ZONE.applyToProvince = (province, modif) -> StaticModifiers.SEA_ZONE.modifiers;
        MILD_WINTER.applyToProvince = (province, modif) -> StaticModifiers.MILD_WINTER.modifiers;
        NORMAL_WINTER.applyToProvince = (province, modif) -> StaticModifiers.NORMAL_WINTER.modifiers;
        SEVERE_WINTER.applyToProvince = (province, modif) -> StaticModifiers.SEVERE_WINTER.modifiers;
        MILD_MONSOON.applyToProvince = (province, modif) -> StaticModifiers.MILD_MONSOON.modifiers;
        NORMAL_MONSOON.applyToProvince = (province, modif) -> StaticModifiers.NORMAL_MONSOON.modifiers;
        SEVERE_MONSOON.applyToProvince = (province, modif) -> StaticModifiers.SEVERE_MONSOON.modifiers;
        BLOCKADED.applyToProvince = (province, modif) -> StaticModifiers.BLOCKADED.modifiers;
        NO_ADJACENT_CONTROLLED.applyToProvince = (province, modif) -> StaticModifiers.NO_ADJACENT_CONTROLLED.modifiers;
        PROVINCIAL_TAX_INCOME.applyToProvince = (province, modif) -> ModifiersUtils.scaleTax(province, modif.modifiers);
        PROVINCIAL_PRODUCTION_SIZE.applyToProvince = (province, modif) -> ModifiersUtils.scaleProd(province, modif.modifiers);
        KNOWLEDGE_SHARING.applyToCountry = (country, modif) -> StaticModifiers.KNOWLEDGE_SHARING.modifiers;
        CARDINALS_SPREAD_INSTITUTION.applyToProvince = (province, modif) -> StaticModifiers.CARDINALS_SPREAD_INSTITUTION.modifiers;
        MANPOWER.applyToProvince = (province, modif) -> ModifiersUtils.scaleManpower(province, modif.modifiers);
        SAILORS.applyToProvince = (province, modif) -> ModifiersUtils.scaleManpower(province, modif.modifiers);
        HORDE_DEVELOPMENT.applyToCountry = (country, modif) -> StaticModifiers.HORDE_DEVELOPMENT.modifiers;
        PROVINCE_RAZED.applyToProvince = (province, modif) -> StaticModifiers.PROVINCE_RAZED.modifiers;
        DEVELOPMENT_SCALED.applyToProvince = (province, modif) -> ModifiersUtils.scaleDevImprove(province, modif.modifiers);
        CAPITAL_CITY.applyToProvince = (province, modif) -> StaticModifiers.CAPITAL_CITY.modifiers;
        PATRIARCH_STATE.applyToProvince = (province, modif) -> StaticModifiers.PATRIARCH_STATE.modifiers;
        PATRIARCH_AUTHORITY_LOCAL.applyToProvince = (province, modif) -> ModifiersUtils.scalePatriarchAuthority(province, modif.modifiers);
        PATRIARCH_AUTHORITY_GLOBAL.applyToCountry = (country, modif) -> ModifiersUtils.scalePatriarchAuthority(country, modif.modifiers);
        PASHA_STATE.applyToProvince = (province, modif) -> StaticModifiers.PASHA_STATE.modifiers;
        COLONY_LEVEL.applyToProvince = (province, modif) -> ModifiersUtils.scaleColonySize100(province, modif.modifiers);
        NATIVE_ASSIMILATION.applyToProvince = (province, modif) -> ModifiersUtils.scaleNativeSize(province, modif.modifiers);
        NATIVE_AGGRESSIVENESS.applyToProvince = (province, modif) -> ModifiersUtils.scaleNativeHostileness(province, modif.modifiers);
        CORE.applyToProvince = (province, modif) -> StaticModifiers.CORE.modifiers;
        NON_CORE.applyToProvince = (province, modif) -> StaticModifiers.NON_CORE.modifiers;
        TERRITORY_CORE.applyToProvince = (province, modif) -> StaticModifiers.TERRITORY_CORE.modifiers;
        TERRITORY_NON_CORE.applyToProvince = (province, modif) -> StaticModifiers.TERRITORY_NON_CORE.modifiers;
        MARCH_BONUS.applyToCountry = (country, modif) -> StaticModifiers.MARCH_BONUS.modifiers;
        SAME_CULTURE_GROUP.applyToProvince = (province, modif) -> StaticModifiers.SAME_CULTURE_GROUP.modifiers;
        NON_ACCEPTED_CULTURE.applyToProvince = (province, modif) -> StaticModifiers.NON_ACCEPTED_CULTURE.modifiers;
        ACCEPTED_CULTURE_DEMOTED.applyToProvince = (province, modif) -> StaticModifiers.ACCEPTED_CULTURE_DEMOTED.modifiers;
        NON_ACCEPTED_CULTURE_REPUBLIC.applyToProvince = (province, modif) -> StaticModifiers.NON_ACCEPTED_CULTURE_REPUBLIC.modifiers;
        OCCUPIED.applyToProvince = (province, modif) -> StaticModifiers.OCCUPIED.modifiers;
        UNDER_SIEGE.applyToProvince = (province, modif) -> StaticModifiers.UNDER_SIEGE.modifiers;
        DEVASTATION.applyToProvince = (province, modif) -> ModifiersUtils.scaleDevastation(province, modif.modifiers);
        PROSPERITY.applyToProvince = (province, modif) -> StaticModifiers.PROSPERITY.modifiers;
        SLAVES_RAIDED.applyToProvince = (province, modif) -> StaticModifiers.SLAVES_RAIDED.modifiers;
        TOLERANCE.applyToProvince = (province, modif) -> ModifiersUtils.scaleWithTolerance(province, modif.modifiers);
        INTOLERANCE.applyToProvince = (province, modif) -> ModifiersUtils.scaleWithIntolerance(province, modif.modifiers);
        UNREST.applyToProvince = (province, modif) -> ModifiersUtils.scaleUnrest(province, modif.modifiers);
        NATIONALISM.applyToProvince = (province, modif) -> ModifiersUtils.scaleNationalism(province, modif.modifiers);
        LOCAL_AUTONOMY.applyToProvince = (province, modif) -> StaticModifiers.LOCAL_AUTONOMY.modifiers;
        LOCAL_AUTONOMY_TRADE_COMPANY_MULTIPLICATIVE.applyToProvince = (province, modif) -> ModifiersUtils.scaleAutonomy(province, modif.modifiers);
        LOCAL_AUTONOMY_TRADE_COMPANY.applyToProvince = (province, modif) -> StaticModifiers.LOCAL_AUTONOMY_TRADE_COMPANY.modifiers;
        RECENT_UPRISING.applyToProvince = (province, modif) -> StaticModifiers.RECENT_UPRISING.modifiers;
        FRIENDLY_REGIMENTS.applyToProvince = (province, modif) -> ModifiersUtils.scaleFriendlyRegimentMax20(province, modif.modifiers);
        ACTIVE_MISSIONARY.applyToProvince = (province, modif) -> StaticModifiers.ACTIVE_MISSIONARY.modifiers;
        NATIONAL_DEFENSE.applyToProvince = (province, modif) -> StaticModifiers.NATIONAL_DEFENSE.modifiers;
        RESOURCE_DEPLETED.applyToProvince = (province, modif) -> StaticModifiers.RESOURCE_DEPLETED.modifiers;
        IN_TRADE_COMPANY.applyToProvince = (province, modif) -> StaticModifiers.IN_TRADE_COMPANY.modifiers;
        LEFT_TRADE_COMPANY.applyToProvince = (province, modif) -> StaticModifiers.LEFT_TRADE_COMPANY.modifiers;
        SCORCHED_EARTH.applyToProvince = (province, modif) -> StaticModifiers.SCORCHED_EARTH.modifiers;
        BASE_VALUES.applyToCountry = (country, modif) -> StaticModifiers.BASE_VALUES.modifiers;
        AI_NATION.applyToCountry = (country, modif) -> StaticModifiers.AI_NATION.modifiers;
        WAR_TAXES.applyToCountry = (country, modif) -> StaticModifiers.WAR_TAXES.modifiers;
        STABILITY.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithStability(country, modif.modifiers);
        POSITIVE_STABILITY.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithStability(country, modif.modifiers);
        PRIVATEERING.applyToCountry = (country, modif) -> StaticModifiers.PRIVATEERING.modifiers;
        NEGATIVE_STABILITY.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithStability(country, modif.modifiers);
        LOST_MANDATE_OF_HEAVEN.applyToCountry = (country, modif) -> StaticModifiers.LOST_MANDATE_OF_HEAVEN.modifiers;
        CANCELLED_LOAN.applyToCountry = (country, modif) -> StaticModifiers.CANCELLED_LOAN.modifiers;
        BANK_LOAN.applyToCountry = (country, modif) -> StaticModifiers.BANK_LOAN.modifiers;
        INFLATION.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithInflation(country, modif.modifiers);
        BANKRUPTCY.applyToCountry = (country, modif) -> StaticModifiers.BANKRUPTCY.modifiers;
        WAR.applyToCountry = (country, modif) -> StaticModifiers.WAR.modifiers;
        PEACE.applyToCountry = (country, modif) -> StaticModifiers.PEACE.modifiers;
        UNCONDITIONAL_SURRENDER.applyToCountry = (country, modif) -> StaticModifiers.UNCONDITIONAL_SURRENDER.modifiers;
        CALL_FOR_PEACE.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithCallForPeace(country, modif.modifiers);
        WAR_EXHAUSTION.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithWarExhaustion(country, modif.modifiers);
        DOOM.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithDoom(country, modif.modifiers);
        AUTHORITY.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithAuthority(country, modif.modifiers);
        REGENCY_COUNCIL.applyToCountry = (country, modif) -> StaticModifiers.REGENCY_COUNCIL.modifiers;
        TRADE_EFFICIENCY.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithTradeEfficiency(country, modif.modifiers);
        PRODUCTION_EFFICIENCY.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithProductionEfficiency(country, modif.modifiers);
        TRADE_REFUSAL.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithTradeRefusal(country, modif.modifiers);
        MERCANTILISM.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithMercantilism(country, modif.modifiers);
        ARMY_TRADITION.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithArmyTradition(country, modif.modifiers);
        NAVY_TRADITION.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithNavyTradition(country, modif.modifiers);
        POSITIVE_PIETY.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithPositivePiety(country, modif.modifiers);
        NEGATIVE_PIETY.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithNegativePiety(country, modif.modifiers);
        STATES_IN_HRE.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithCountriesInHre(country, modif.modifiers);
        FREE_CITIES_IN_HRE.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithFreeCitiesInHre(country, modif.modifiers);
        OCCUPIED_IMPERIAL.applyToCountry = (country, modif) -> ModifiersUtils.scaleOccupiedImperial(country, modif.modifiers);
        NUM_OF_MARRIAGES.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithNumOfRoyalMarriages(country, modif.modifiers);
        NUM_OF_PROVINCES.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithNumOfProvinces(country, modif.modifiers);
        COUNTRY_DEVELOPMENT.applyToCountry = (country, modif) -> ModifiersUtils.scaleCountryDev(country, modif.modifiers);
        TRIBAL_ALLEGIANCE.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithTribalAllegiance(country, modif.modifiers);
        LEGITIMACY.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithLegitimacy50(country, modif.modifiers);
        HORDE_UNITY.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithHordeUnity50(country, modif.modifiers);
        DEVOTION.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithDevotion50(country, modif.modifiers);
        MERITOCRACY.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithMeritocracy50(country, modif.modifiers);
        LOW_MERITOCRACY.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithMeritocracy50Reverse(country, modif.modifiers);
        CORRUPTION.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithCorruption(country, modif.modifiers);
        ROOT_OUT_CORRUPTION.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithRootOutCorruption(country, modif.modifiers);
        RECOVERY_MOTIVATION.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithRecoveryMotivation(country, modif.modifiers);
        MILITARIZED_SOCIETY.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithMilitarisedSociety(country, modif.modifiers);
        OVER_EXTENSION.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithOverextension(country, modif.modifiers);
        PRESTIGE.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithPrestige(country, modif.modifiers);
        REPUBLICAN_TRADITION.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithRepublicanTradition(country, modif.modifiers);
        INVERSE_REPUBLICAN_TRADITION.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithRepublicanTraditionReverse(country, modif.modifiers);
        RELIGIOUS_UNITY.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithReligiousUnity(country, modif.modifiers);
        INVERSE_RELIGIOUS_UNITY.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithReligiousUnityReverse(country, modif.modifiers);
        TOTAL_OCCUPATION.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithOccupiedProvinces(country, modif.modifiers);
        TOTAL_BLOCKADED.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithBlockadedProvinces(country, modif.modifiers);
        UNCONTESTED_CORES.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithNotControlledCores(country, modif.modifiers);
        NUM_OBJECTIVES_FULLFILLED.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithNumOfAgeObjectives(country, modif.modifiers);
        TRIBUTARY_STATE_BEHIND_OVERLORD_TECH_ADM.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithOverlordAdm(country, modif.modifiers);
        TRIBUTARY_STATE_BEHIND_OVERLORD_TECH_DIP.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithOverlordDip(country, modif.modifiers);
        TRIBUTARY_STATE_BEHIND_OVERLORD_TECH_MIL.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithOverlordMil(country, modif.modifiers);
        LIBERTY_DESIRE.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithLibertyDesire(country, modif.modifiers);
        ABSOLUTISM.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithAbsolutism(country, modif.modifiers);
        LOW_ARMY_PROFESSIONALISM.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithLowProfessionalism(country, modif.modifiers);
        HIGH_ARMY_PROFESSIONALISM.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithHighProfessionalism(country, modif.modifiers);
        STRELTSY_MODIFIER.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithStreltsyPercent(country, modif.modifiers);
        POWER_PROJECTION.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithCurrentPowerProjection(country, modif.modifiers);
        TRADE_COMPANY_STRONG.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithStrongCompany(country, modif.modifiers);
        //        LARGE_COLONIAL_NATION.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithLargeColony(country, modif.modifiers);
        MARCH_SUBJECT.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithMarches(country, modif.modifiers);
        VASSAL_SUBJECT.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithVassals(country, modif.modifiers);
        DAIMYO_SUBJECT.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithDaimyos(country, modif.modifiers);
        UNION_SUBJECT.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithUnions(country, modif.modifiers);
        DEFENDER_OF_FAITH.applyToCountry = (country, modif) -> StaticModifiers.DEFENDER_OF_FAITH.modifiers;
        DEFENDER_OF_FAITH_REFUSED_CTA.applyToCountry = (country, modif) -> StaticModifiers.DEFENDER_OF_FAITH_REFUSED_CTA.modifiers;
        EMPEROR.applyToCountry = (country, modif) -> StaticModifiers.EMPEROR.modifiers;
        FREE_CITY_IN_HRE.applyToCountry = (country, modif) -> StaticModifiers.FREE_CITY_IN_HRE.modifiers;
        MEMBER_IN_HRE.applyToCountry = (country, modif) -> StaticModifiers.MEMBER_IN_HRE.modifiers;
        LUCK.applyToCountry = (country, modif) -> StaticModifiers.LUCK.modifiers;
        NO_DEBATE_IN_PARLIAMENT.applyToCountry = (country, modif) -> StaticModifiers.NO_DEBATE_IN_PARLIAMENT.modifiers;
        CURIA_CONTROLLER.applyToCountry = (country, modif) -> StaticModifiers.CURIA_CONTROLLER.modifiers;
        BOUGHT_INDULGENCE.applyToCountry = (country, modif) -> StaticModifiers.BOUGHT_INDULGENCE.modifiers;
        PRODUCTION_LEADER.applyToProvince = (province, modif) -> StaticModifiers.PRODUCTION_LEADER.modifiers;
        FEDERATION_LEADER.applyToCountry = (country, modif) -> StaticModifiers.FEDERATION_LEADER.modifiers;
        IN_GOLDEN_ERA.applyToCountry = (country, modif) -> StaticModifiers.IN_GOLDEN_ERA.modifiers;
        POWER_PROJECTION_25.applyToCountry = (country, modif) -> StaticModifiers.POWER_PROJECTION_25.modifiers;
        ALL_NATIONS.applyToCountry = (country, modif) -> StaticModifiers.ALL_NATIONS.modifiers;
        SUBJECT_NATION.applyToCountry = (country, modif) -> StaticModifiers.SUBJECT_NATION.modifiers;
        VASSAL_NATION.applyToCountry = (country, modif) -> StaticModifiers.VASSAL_NATION.modifiers;
        PRIMITIVE_NATION.applyToCountry = (country, modif) -> StaticModifiers.PRIMITIVE_NATION.modifiers;
        MAINTAINED_FORTS.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithMaintainedForts(country, modif.modifiers);
        GOV_RANK_1.applyToCountry = (country, modif) -> StaticModifiers.GOV_RANK_1.modifiers;
        GOV_RANK_2.applyToCountry = (country, modif) -> StaticModifiers.GOV_RANK_2.modifiers;
        GOV_RANK_3.applyToCountry = (country, modif) -> StaticModifiers.GOV_RANK_3.modifiers;
        GOV_RANK_4.applyToCountry = (country, modif) -> StaticModifiers.GOV_RANK_4.modifiers;
        GOV_RANK_5.applyToCountry = (country, modif) -> StaticModifiers.GOV_RANK_5.modifiers;
        GOV_RANK_6.applyToCountry = (country, modif) -> StaticModifiers.GOV_RANK_6.modifiers;
        GOV_RANK_7.applyToCountry = (country, modif) -> StaticModifiers.GOV_RANK_7.modifiers;
        GOV_RANK_8.applyToCountry = (country, modif) -> StaticModifiers.GOV_RANK_8.modifiers;
        GOV_RANK_9.applyToCountry = (country, modif) -> StaticModifiers.GOV_RANK_9.modifiers;
        GOV_RANK_10.applyToCountry = (country, modif) -> StaticModifiers.GOV_RANK_10.modifiers;
        AUTONOMY_INCREASED.applyToProvince = (province, modif) -> StaticModifiers.AUTONOMY_INCREASED.modifiers;
        AUTONOMY_DECREASED.applyToProvince = (province, modif) -> StaticModifiers.AUTONOMY_DECREASED.modifiers;
        REVOLUTION_TARGET.applyToCountry = (country, modif) -> StaticModifiers.REVOLUTION_TARGET.modifiers;
        RECRUITMENT_SABOTAGED.applyToCountry = (country, modif) -> StaticModifiers.RECRUITMENT_SABOTAGED.modifiers;
        MERCHANTS_SLANDERED.applyToCountry = (country, modif) -> StaticModifiers.MERCHANTS_SLANDERED.modifiers;
        DISCONTENT_SOWED.applyToCountry = (country, modif) -> StaticModifiers.DISCONTENT_SOWED.modifiers;
        REPUTATION_SABOTAGED.applyToCountry = (country, modif) -> StaticModifiers.REPUTATION_SABOTAGED.modifiers;
        CORRUPT_OFFICIALS.applyToCountry = (country, modif) -> StaticModifiers.CORRUPT_OFFICIALS.modifiers;
        SCALED_TRADE_LEAGUE_LEADER.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithNumTradeLeagueMembers(country, modif.modifiers);
        IN_TRADE_LEAGUE.applyToCountry = (country, modif) -> StaticModifiers.IN_TRADE_LEAGUE.modifiers;
        CUSTOM_SETUP.applyToCountry = (country, modif) -> StaticModifiers.CUSTOM_SETUP.modifiers;
        EMBARGO_RIVALS.applyToCountry = (country, modif) -> StaticModifiers.EMBARGO_RIVALS.modifiers;
        SCUTAGE.applyToCountry = (country, modif) -> StaticModifiers.SCUTAGE.modifiers;
        SUBSIDIZE_ARMIES.applyToCountry = (country, modif) -> StaticModifiers.SUBSIDIZE_ARMIES.modifiers;
        SUPPORT_LOYALISTS.applyToCountry = (country, modif) -> StaticModifiers.SUPPORT_LOYALISTS.modifiers;
        SEND_OFFICERS.applyToCountry = (country, modif) -> StaticModifiers.SEND_OFFICERS.modifiers;
        DIVERT_TRADE.applyToCountry = (country, modif) -> StaticModifiers.DIVERT_TRADE.modifiers;
        INVASION_NATION.applyToCountry = (country, modif) -> StaticModifiers.INVASION_NATION.modifiers;
        NATIVE_POLICY_COEXIST.applyToCountry = (country, modif) -> StaticModifiers.NATIVE_POLICY_COEXIST.modifiers;
        NATIVE_POLICY_TRADE.applyToCountry = (country, modif) -> StaticModifiers.NATIVE_POLICY_TRADE.modifiers;
        NATIVE_POLICY_HOSTILE.applyToCountry = (country, modif) -> StaticModifiers.NATIVE_POLICY_HOSTILE.modifiers;
        HIGH_HARMONY.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithHarmony(country, modif.modifiers);
        LOW_HARMONY.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithHarmonyReverse(country, modif.modifiers);
        OVERLORD_DAIMYO_AT_PEACE.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithDaimyosAtPeace(country, modif.modifiers);
        OVERLORD_DAIMYO_SAME_ISOLATIONISM.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithDaimyosSameIsolationism(country, modif.modifiers);
        OVERLORD_DAIMYO_DIFFERENT_ISOLATIONISM.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithDaimyosDifferentIsolationism(country,
                                                                                                                                         modif.modifiers);
        OVERLORD_SANKIN_KOTAI.applyToCountry = (country, modif) -> StaticModifiers.OVERLORD_SANKIN_KOTAI.modifiers;
        SUBJECT_SANKIN_KOTAI.applyToCountry = (country, modif) -> StaticModifiers.SUBJECT_SANKIN_KOTAI.modifiers;
        OVERLORD_EXPEL_RONIN.applyToCountry = (country, modif) -> StaticModifiers.OVERLORD_EXPEL_RONIN.modifiers;
        OVERLORD_SWORD_HUNT.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithDaimyosSwordHunt(country, modif.modifiers);
        SUBJECT_EXPEL_RONIN.applyToCountry = (country, modif) -> StaticModifiers.SUBJECT_EXPEL_RONIN.modifiers;
        SUBJECT_SWORD_HUNT.applyToCountry = (country, modif) -> StaticModifiers.SUBJECT_SWORD_HUNT.modifiers;
        SUPPLY_DEPOT_AREA.applyToProvince = (province, modif) -> StaticModifiers.SUPPLY_DEPOT_AREA.modifiers;
        EFFICIENT_TAX_FARMING_MODIFIER.applyToCountry = (country, modif) -> StaticModifiers.EFFICIENT_TAX_FARMING_MODIFIER.modifiers;
        LAND_ACQUISITION_MODIFIER.applyToCountry = (country, modif) -> StaticModifiers.LAND_ACQUISITION_MODIFIER.modifiers;
        LENIENT_TAXATION_MODIFIER.applyToCountry = (country, modif) -> StaticModifiers.LENIENT_TAXATION_MODIFIER.modifiers;
        TRAIN_HORSEMANSHIP_MODIFIER.applyToCountry = (country, modif) -> StaticModifiers.TRAIN_HORSEMANSHIP_MODIFIER.modifiers;
        PROMOTE_CULTURE_IN_GOVERNMENT_MODIFIER.applyToCountry = (country, modif) -> StaticModifiers.PROMOTE_CULTURE_IN_GOVERNMENT_MODIFIER.modifiers;
        SEIZE_CLERICAL_HOLDINGS_MODIFIER.applyToCountry = (country, modif) -> StaticModifiers.SEIZE_CLERICAL_HOLDINGS_MODIFIER.modifiers;
        INVITE_MINORITIES_MODIFIER.applyToCountry = (country, modif) -> StaticModifiers.INVITE_MINORITIES_MODIFIER.modifiers;
        HANAFI_SCHOLAR_MODIFIER.applyToCountry = (country, modif) -> StaticModifiers.HANAFI_SCHOLAR_MODIFIER.modifiers;
        HANBALI_SCHOLAR_MODIFIER.applyToCountry = (country, modif) -> StaticModifiers.HANBALI_SCHOLAR_MODIFIER.modifiers;
        MALIKI_SCHOLAR_MODIFIER.applyToCountry = (country, modif) -> StaticModifiers.MALIKI_SCHOLAR_MODIFIER.modifiers;
        SHAFII_SCHOLAR_MODIFIER.applyToCountry = (country, modif) -> StaticModifiers.SHAFII_SCHOLAR_MODIFIER.modifiers;
        ISMAILI_SCHOLAR_MODIFIER.applyToCountry = (country, modif) -> StaticModifiers.ISMAILI_SCHOLAR_MODIFIER.modifiers;
        JAFARI_SCHOLAR_MODIFIER.applyToCountry = (country, modif) -> StaticModifiers.JAFARI_SCHOLAR_MODIFIER.modifiers;
        ZAIDI_SCHOLAR_MODIFIER.applyToCountry = (country, modif) -> StaticModifiers.ZAIDI_SCHOLAR_MODIFIER.modifiers;
        DISHONOURED_ALLIANCE.applyToCountry = (country, modif) -> StaticModifiers.DISHONOURED_ALLIANCE.modifiers;
        INNOVATIVENESS.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithInnovativeness(country, modif.modifiers);
        RAIDING_PARTIES_MODIFIER.applyToCountry = (country, modif) -> StaticModifiers.RAIDING_PARTIES_MODIFIER.modifiers;
        SERFS_RECIEVED_BY_COSSACKS.applyToCountry = (country, modif) -> StaticModifiers.SERFS_RECIEVED_BY_COSSACKS.modifiers;
        COSSACKS_MODIFIER.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithCossacksPercent(country, modif.modifiers);
        EXPAND_ADMINISTATION_MODIFIER.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithNumExpandedAdministration(country, modif.modifiers);
        OVER_GOVERNING_CAPACITY_MODIFIER.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithOverGoverningCapacity(country, modif.modifiers);
        UNDER_GOVERNING_CAPACITY_MODIFIER.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithUnderGoverningCapacity(country, modif.modifiers);
        REVERSE_UNDER_GOVERNING_CAPACITY_MODIFIER.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithUnderGoverningCapacityReverse(country,
                                                                                                                                             modif.modifiers);
        MANDATE.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithMandate(country, modif.modifiers);
        IMPERIAL_AUTHORITY.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithImperialAuthority(country, modif.modifiers);
        POSITIVE_IMPERIAL_AUTHORITY.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithPositiveImperialAuthority(country, modif.modifiers);
        NEGATIVE_IMPERIAL_AUTHORITY.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithNegativeImperialAuthority(country, modif.modifiers);
        LOST_HEGEMONY.applyToCountry = (country, modif) -> StaticModifiers.LOST_HEGEMONY.modifiers;
        PAPAL_INFLUENCE.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithPapalInfluence(country, modif.modifiers);
        CHURCH_POWER.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithChurchPower(country, modif.modifiers);
        FERVOR.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithFervor(country, modif.modifiers);
        KARMA.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithKarma(country, modif.modifiers);
        POSITIVE_KARMA.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithPositiveKarma(country, modif.modifiers);
        NEGATIVE_KARMA.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithNegativeKarma(country, modif.modifiers);
        DIPLOMATIC_REPUTATION.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithDiplomaticReputation(country, modif.modifiers);
        TOLERANCE_OWN.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithToleranceOwn(country, modif.modifiers);
        TOLERANCE_HERETIC.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithToleranceHeretic(country, modif.modifiers);
        TOLERANCE_HEATHEN.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithToleranceHeathen(country, modif.modifiers);
        NUM_ACCEPTED_CULTURES.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithNumAcceptedCultures(country, modif.modifiers);
        RULER_ADM.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithRulerAdm(country, modif.modifiers);
        RULER_DIP.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithRulerDip(country, modif.modifiers);
        RULER_MIL.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithRulerMil(country, modif.modifiers);
        HEIR_ADM.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithHeirAdm(country, modif.modifiers);
        HEIR_DIP.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithHeirDip(country, modif.modifiers);
        HEIR_MIL.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithHeirMil(country, modif.modifiers);
        CONSORT_ADM.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithConsortAdm(country, modif.modifiers);
        CONSORT_DIP.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithConsortDip(country, modif.modifiers);
        CONSORT_MIL.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithConsortMil(country, modif.modifiers);
        MANPOWER_PERCENTAGE.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithManpowerPercent(country, modif.modifiers);
        MANPOWER_PERCENTAGE_REVERSE.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithManpowerPercentReverse(country, modif.modifiers);
        SAILORS_PERCENTAGE.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithSailorsPercent(country, modif.modifiers);
        SAILORS_PERCENTAGE_REVERSE.applyToCountry = (country, modif) -> ModifiersUtils.scaleWithSailorsPercentReverse(country, modif.modifiers);
        DEVELOPMENT.applyToProvince = (province, modif) -> { //Ugly but thanks to Paradox
            Modifiers m = Modifiers.copy(modif.modifiers);

            if (m.hasModifier("naval_forcelimit")) {
                Modifier modifier = ModifiersUtils.getModifier("naval_forcelimit");

                if (!province.isPort()) {
                    m.getModifiers().put(modifier, 0d);
                }
            }

            if (m.hasModifier("local_sailors")) {
                Modifier modifier = ModifiersUtils.getModifier("local_sailors");

                if (!province.isPort()) {
                    m.getModifiers().put(modifier, 0d);
                } else {
                    Modifiers newM = ModifiersUtils.scaleAutonomy(province, new Modifiers(new HashSet<>(), Map.of(modifier, m.getModifier(modifier))));
                    m.getModifiers().put(modifier, newM.getModifier(modifier));
                }
            }

            return ModifiersUtils.scaleDev(province, m);
        };
        LOCAL_AUTONOMY_MULTIPLICATIVE.applyToProvince = (province, modif) -> { //Ugly but thanks to Paradox
            Modifiers m = Modifiers.copy(modif.modifiers);

            if (m.hasModifier("land_forcelimit_modifier")) {
                m.removeModifier("land_forcelimit_modifier");
            }

            if (m.hasModifier("naval_forcelimit_modifier")) {
                m.removeModifier("naval_forcelimit_modifier");
            }

            return ModifiersUtils.scaleDev(province, m);
        };
    }

    StaticModifiers(ConditionAbstract trigger, BiFunction<SaveCountry, StaticModifiers, Modifiers> applyToCountry,
                    BiFunction<SaveProvince, StaticModifiers, Modifiers> applyToProvince) {
        this.trigger = trigger;
        this.applyToCountry = applyToCountry;
        this.applyToProvince = applyToProvince;
    }

    public static void compute() {
        APPLIED_TO_COUNTRY.clear();
        APPLIED_TO_COUNTRY.addAll(Arrays.stream(StaticModifiers.values())
                                        .filter(staticModifiers -> staticModifiers.modifiers != null
                                                                   && !staticModifiers.modifiers.isEmpty())
                                        .filter(staticModifiers -> staticModifiers.applyToCountry != null)
                                        .toList());
        APPLIED_TO_PROVINCE.clear();
        APPLIED_TO_PROVINCE.addAll(Arrays.stream(StaticModifiers.values())
                                         .filter(staticModifiers -> staticModifiers.modifiers != null
                                                                    && !staticModifiers.modifiers.isEmpty())
                                         .filter(staticModifiers -> staticModifiers.applyToProvince != null)
                                         .toList());
    }

    public void setModifiers(Modifiers modifiers) {
        this.modifiers = modifiers;
    }

    public static StaticModifiers value(String name) {
        return STATIC_MODIFIERS_MAP.get(name.toUpperCase());
    }

    public static Double applyToModifiersCountry(SaveCountry country, Modifier modifier) {
        return ModifiersUtils.sumModifiers(modifier,
                                           APPLIED_TO_COUNTRY.stream()
                                                             .filter(staticModifiers -> staticModifiers.modifiers.hasModifier(modifier))
                                                             .filter(staticModifiers -> staticModifiers.trigger.apply(country, country))
                                                             .map(staticModifiers -> staticModifiers.applyToCountry.apply(country, staticModifiers)
                                                                                                                   .getModifier(modifier))
                                                             .toList());
    }

    public static Double applyToModifiersProvince(SaveProvince province, Modifier modifier) {
        return ModifiersUtils.sumModifiers(modifier, APPLIED_TO_PROVINCE.stream()
                                                                        .filter(staticModifiers -> staticModifiers.modifiers.hasModifier(modifier))
                                                                        .filter(staticModifiers -> staticModifiers.trigger.apply(province))
                                                                        .map(staticModifiers -> staticModifiers.applyToProvince.apply(province, staticModifiers)
                                                                                                                               .getModifier(modifier))
                                                                        .toList());
    }
}
