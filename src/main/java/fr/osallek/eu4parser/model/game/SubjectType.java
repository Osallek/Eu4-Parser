package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;
import fr.osallek.eu4parser.common.NumbersUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class SubjectType {

    private final ClausewitzItem item;

    private String sprite;

    private String diplomacyOverlordSprite;

    private String diplomacySubjectSprite;

    private boolean hasOverlordsRuler;

    private boolean canFightIndependenceWar;

    private boolean isVoluntary;

    private boolean transferTradePower;

    private boolean transferTradeIfMerchantRepublic;

    private boolean joinsOverlordsWars;

    private boolean joinsColonialWars;

    private boolean canBeIntegrated;

    private boolean canReleaseAndPlay;

    private boolean usesTariffs;

    private boolean dynamicallyCreatedDuringHistory;

    private boolean eatsOverlordsColonies;

    private boolean hasColonialParent;

    private boolean overlordCanAttack;

    private boolean overlordCanBeSubject;

    private boolean canHaveSubjectsOfOtherTypes;

    private boolean canBeAnnexed;

    private boolean takesDiploSlot;

    private boolean hasPowerProjection;

    private boolean canReleaseInPeace;

    private boolean usesMilitaryFocus;

    private boolean overlordProtectsExternal;

    private boolean countsForBorders;

    private boolean overlordEnforcePeaceAttacking;

    private boolean canUseClaims;

    private boolean givesDaimyoBonuses;

    private boolean getsHelpWithRebels;

    private boolean shareRebelPopup;

    private boolean separatistsBecomeSubjects;

    private boolean allowsTakingLandWithoutIndependence;

    private boolean canTransferInPeace;

    private boolean canSetMilFocus;

    private boolean canSendMissionaryToSubject;

    private boolean canUnionBreak;

    private boolean overlordCanFabricateFor;

    private int maxGovernmentRank;

    private int citiesRequiredForBonuses;

    private int trustOnStart;

    private double baseLibertyDesire;

    private double libertyDesireNegativePrestige;

    private double libertyDesireDevelopmentRatio;

    private double libertyDesireSameDynasty;

    private double libertyDesireRevolution;

    private double paysOverlord;

    private double forcelimitBonus;

    private double forcelimitToOverlord;

    private double militaryFocus;

    private double relativePowerClass;

    private int diplomacyViewClass;

    private Map<SubjectTypeRelation, List<String>> canFight;

    private Map<SubjectTypeRelation, List<String>> canRival;

    private Map<SubjectTypeRelation, List<String>> canAlly;

    private Map<SubjectTypeRelation, List<String>> canMarry;

    private boolean embargoRivals;

    private boolean supportLoyalists;

    private boolean subsidizeArmies;

    private boolean scutage;

    private boolean sendOfficers;

    private boolean divertTrade;

    private boolean placateRulers;

    private boolean placeRelativeOnThrone;

    private boolean enforceReligion;

    private boolean customizeSubject;

    private boolean replaceGovernor;

    private boolean grantProvince;

    private boolean enforceCulture;

    private boolean siphonIncome;

    private boolean fortifyMarch;

    private boolean seizeTerritory;

    private boolean startColonialWar;

    private boolean grantCoreClaim;

    private boolean sacrificeRuler;

    private boolean sacrificeHeir;

    private boolean increaseTariffs;

    private boolean decreaseTariffs;

    private boolean takeOnDebt;

    private boolean bestowGifts;

    private boolean sendAdditionalTroops;

    private boolean demandArtifacts;

    private boolean demandAdditionalTribute;

    private boolean forceSeppuku;

    private boolean pressSailors;

    private boolean contributeToCapital;

    private boolean forceIsolation;

    private boolean returnLand;

    private boolean conscriptGeneral;

    private boolean knowledgeSharing;

    private boolean blockSettlementGrowth;

    private boolean allowSettlementGrowth;

    private boolean swordHunt;

    private boolean sankinKotai;

    private boolean expelRonin;

    private List<ModifierSubject> modifierSubjects = new ArrayList<>();

    private List<ModifierSubject> modifierOverlord = new ArrayList<>();

    private String overlordOpinionModifier;

    private String subjectOpinionModifier;

    public SubjectType(ClausewitzItem item, Collection<SubjectType> subjectTypes) {
        subjectTypes.stream()
                    .filter(subjectType -> StringUtils.isNotBlank(subjectType.getSprite()))
                    .filter(subjectType -> subjectType.getName().equalsIgnoreCase(item.getVarAsString("copy_from")))
                    .findFirst()
                    .ifPresent(this::copy);

        this.item = item;

        this.sprite = StringUtils.defaultIfBlank(item.getVarAsString("sprite"), this.sprite);
        this.diplomacyOverlordSprite = StringUtils.defaultIfBlank(item.getVarAsString("diplomacy_overlord_sprite"), this.diplomacyOverlordSprite);
        this.diplomacySubjectSprite = StringUtils.defaultIfBlank(item.getVarAsString("diplomacy_subject_sprite"), this.diplomacySubjectSprite);
        this.hasOverlordsRuler = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("has_overlords_ruler"), this.hasOverlordsRuler);
        this.canFightIndependenceWar = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("can_fight_independence_war"), this.canFightIndependenceWar);
        this.isVoluntary = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("is_voluntary"), this.isVoluntary);
        this.transferTradePower = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("transfer_trade_power"), this.transferTradePower);
        this.transferTradeIfMerchantRepublic = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("transfer_trade_if_merchant_republic"),
                                                                                   this.transferTradeIfMerchantRepublic);
        this.joinsOverlordsWars = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("joins_overlords_wars"), this.joinsOverlordsWars);
        this.joinsColonialWars = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("joins_colonial_wars"), this.joinsColonialWars);
        this.canBeIntegrated = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("can_be_integrated"), this.canBeIntegrated);
        this.canReleaseAndPlay = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("can_release_and_play"), this.canReleaseAndPlay);
        this.usesTariffs = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("uses_tariffs"), this.usesTariffs);
        this.dynamicallyCreatedDuringHistory = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("dynamically_created_during_history"),
                                                                                   this.dynamicallyCreatedDuringHistory);
        this.eatsOverlordsColonies = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("eats_overlords_colonies"), this.eatsOverlordsColonies);
        this.hasColonialParent = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("has_colonial_parent"), this.hasColonialParent);
        this.overlordCanAttack = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("overlord_can_attack"), this.overlordCanAttack);
        this.overlordCanBeSubject = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("overlord_can_be_subject"), this.overlordCanBeSubject);
        this.canHaveSubjectsOfOtherTypes = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("can_have_subjects_of_other_types"),
                                                                               this.canHaveSubjectsOfOtherTypes);
        this.canBeAnnexed = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("can_be_annexed"), this.canBeAnnexed);
        this.takesDiploSlot = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("takes_diplo_slot"), this.takesDiploSlot);
        this.hasPowerProjection = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("has_power_projection"), this.hasPowerProjection);
        this.canReleaseInPeace = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("can_release_in_peace"), this.canReleaseInPeace);
        this.usesMilitaryFocus = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("uses_military_focus"), this.usesMilitaryFocus);
        this.overlordProtectsExternal = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("overlord_protects_external"), this.overlordProtectsExternal);
        this.countsForBorders = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("counts_for_borders"), this.countsForBorders);
        this.overlordEnforcePeaceAttacking = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("overlord_enforce_peace_attacking"),
                                                                                 this.overlordEnforcePeaceAttacking);
        this.canUseClaims = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("can_use_claims"), this.canUseClaims);
        this.givesDaimyoBonuses = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("gives_daimyo_bonuses"), this.givesDaimyoBonuses);
        this.getsHelpWithRebels = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("gets_help_with_rebels"), this.getsHelpWithRebels);
        this.shareRebelPopup = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("share_rebel_popup"), this.shareRebelPopup);
        this.separatistsBecomeSubjects = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("separatists_become_subjects"), this.separatistsBecomeSubjects);
        this.allowsTakingLandWithoutIndependence = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("allows_taking_land_without_independence"),
                                                                                       this.allowsTakingLandWithoutIndependence);
        this.canTransferInPeace = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("can_transfer_in_peace"), this.canTransferInPeace);
        this.canSetMilFocus = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("can_set_mil_focus"), this.canSetMilFocus);
        this.canSendMissionaryToSubject = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("can_send_missionary_to_subject"),
                                                                              this.canSendMissionaryToSubject);
        this.canUnionBreak = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("can_union_break"), this.canUnionBreak);
        this.overlordCanFabricateFor = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("overlord_can_fabricate_for"), this.overlordCanFabricateFor);
        this.maxGovernmentRank = NumbersUtils.intOrDefault(item.getVarAsInt("max_government_rank"), this.maxGovernmentRank);
        this.citiesRequiredForBonuses = NumbersUtils.intOrDefault(item.getVarAsInt("cities_required_for_bonuses"), this.citiesRequiredForBonuses);
        this.trustOnStart = NumbersUtils.intOrDefault(item.getVarAsInt("trust_on_start"), this.trustOnStart);
        this.baseLibertyDesire = NumbersUtils.doubleOrDefault(item.getVarAsDouble("base_liberty_desire"), this.baseLibertyDesire);
        this.libertyDesireNegativePrestige = NumbersUtils.doubleOrDefault(item.getVarAsDouble("liberty_desire_negative_prestige"),
                                                                          this.libertyDesireNegativePrestige);
        this.libertyDesireDevelopmentRatio = NumbersUtils.doubleOrDefault(item.getVarAsDouble("liberty_desire_development_ratio"),
                                                                          this.libertyDesireDevelopmentRatio);
        this.libertyDesireSameDynasty = NumbersUtils.doubleOrDefault(item.getVarAsDouble("liberty_desire_same_dynasty"), this.libertyDesireSameDynasty);
        this.libertyDesireRevolution = NumbersUtils.doubleOrDefault(item.getVarAsDouble("liberty_desire_revolution"), this.libertyDesireRevolution);
        this.paysOverlord = NumbersUtils.doubleOrDefault(item.getVarAsDouble("pays_overlord"), this.paysOverlord);
        this.forcelimitBonus = NumbersUtils.doubleOrDefault(item.getVarAsDouble("forcelimit_bonus"), this.forcelimitBonus);
        this.forcelimitToOverlord = NumbersUtils.doubleOrDefault(item.getVarAsDouble("forcelimit_to_overlord"), this.forcelimitToOverlord);
        this.militaryFocus = NumbersUtils.doubleOrDefault(item.getVarAsDouble("military_focus"), this.militaryFocus);
        this.relativePowerClass = NumbersUtils.doubleOrDefault(item.getVarAsDouble("relative_power_class"), this.relativePowerClass);
        this.diplomacyViewClass = NumbersUtils.intOrDefault(item.getVarAsInt("diplomacy_view_class"), this.diplomacyViewClass);
        this.embargoRivals = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("embargo_rivals"), this.embargoRivals);
        this.supportLoyalists = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("support_loyalists"), this.supportLoyalists);
        this.subsidizeArmies = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("subsidize_armies"), this.subsidizeArmies);
        this.scutage = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("scutage"), this.scutage);
        this.sendOfficers = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("send_officers"), this.sendOfficers);
        this.divertTrade = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("divert_trade"), this.divertTrade);
        this.placateRulers = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("placate_rulers"), this.placateRulers);
        this.placeRelativeOnThrone = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("place_relative_on_throne"), this.placeRelativeOnThrone);
        this.enforceReligion = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("enforce_religion"), this.enforceReligion);
        this.customizeSubject = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("customize_subject"), this.customizeSubject);
        this.replaceGovernor = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("replace_governor"), this.replaceGovernor);
        this.grantProvince = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("grant_province"), this.grantProvince);
        this.enforceCulture = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("enforce_culture"), this.enforceCulture);
        this.siphonIncome = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("siphon_income"), this.siphonIncome);
        this.fortifyMarch = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("fortify_march"), this.fortifyMarch);
        this.seizeTerritory = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("seize_territory"), this.seizeTerritory);
        this.startColonialWar = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("start_colonial_war"), this.startColonialWar);
        this.grantCoreClaim = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("grant_core_claim"), this.grantCoreClaim);
        this.sacrificeRuler = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("sacrifice_ruler"), this.sacrificeRuler);
        this.sacrificeHeir = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("sacrifice_heir"), this.sacrificeHeir);
        this.increaseTariffs = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("increase_tariffs"), this.increaseTariffs);
        this.decreaseTariffs = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("decrease_tariffs"), this.decreaseTariffs);
        this.takeOnDebt = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("takeondebt"), this.takeOnDebt);
        this.bestowGifts = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("bestow_gifts"), this.bestowGifts);
        this.sendAdditionalTroops = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("send_additional_troops"), this.sendAdditionalTroops);
        this.demandArtifacts = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("demand_artifacts"), this.demandArtifacts);
        this.demandAdditionalTribute = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("demand_additional_tribute"), this.demandAdditionalTribute);
        this.forceSeppuku = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("force_seppuku"), this.forceSeppuku);
        this.pressSailors = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("press_sailors"), this.pressSailors);
        this.contributeToCapital = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("contribute_to_capital"), this.contributeToCapital);
        this.forceIsolation = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("force_isolation"), this.forceIsolation);
        this.returnLand = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("return_land"), this.returnLand);
        this.conscriptGeneral = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("conscript_general"), this.conscriptGeneral);
        this.knowledgeSharing = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("knowledge_sharing"), this.knowledgeSharing);
        this.blockSettlementGrowth = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("block_settlement_growth"), this.blockSettlementGrowth);
        this.allowSettlementGrowth = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("allow_settlement_growth"), this.allowSettlementGrowth);
        this.swordHunt = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("sword_hunt"), this.swordHunt);
        this.sankinKotai = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("sankin_kotai"), this.sankinKotai);
        this.expelRonin = BooleanUtils.toBooleanDefaultIfNull(item.getVarAsBool("expel_ronin"), this.expelRonin);
        this.overlordOpinionModifier = StringUtils.defaultIfBlank(item.getVarAsString("overlord_opinion_modifier"), this.overlordOpinionModifier);
        this.subjectOpinionModifier = StringUtils.defaultIfBlank(item.getVarAsString("subject_opinion_modifier"), this.subjectOpinionModifier);

        ClausewitzItem child = item.getChild("can_fight");
        this.canFight = child == null ? null : child.getVariables()
                                                    .stream()
                                                    .collect(Collectors.groupingBy(variable -> SubjectTypeRelation.valueOf(variable.getName().toUpperCase()),
                                                                                   Collectors.mapping(ClausewitzVariable::getValue, Collectors.toList())));

        child = item.getChild("can_rival");
        this.canRival = child == null ? null : child.getVariables()
                                                    .stream()
                                                    .collect(Collectors.groupingBy(variable -> SubjectTypeRelation.valueOf(variable.getName().toUpperCase()),
                                                                                   Collectors.mapping(ClausewitzVariable::getValue, Collectors.toList())));

        child = item.getChild("can_ally");
        this.canAlly = child == null ? null : child.getVariables()
                                                   .stream()
                                                   .collect(Collectors.groupingBy(variable -> SubjectTypeRelation.valueOf(variable.getName().toUpperCase()),
                                                                                  Collectors.mapping(ClausewitzVariable::getValue, Collectors.toList())));

        child = item.getChild("can_marry");
        this.canMarry = child == null ? null : child.getVariables()
                                                    .stream()
                                                    .collect(Collectors.groupingBy(variable -> SubjectTypeRelation.valueOf(variable.getName().toUpperCase()),
                                                                                   Collectors.mapping(ClausewitzVariable::getValue, Collectors.toList())));

        if ("clear".equalsIgnoreCase(item.getVarAsString("modifier_subject"))) {
            this.modifierSubjects.clear();
        }

        item.getChildren("modifier_subject").stream().map(ModifierSubject::new).forEach(this.modifierSubjects::add);

        if ("clear".equalsIgnoreCase(item.getVarAsString("modifier_overlord"))) {
            this.modifierOverlord.clear();
        }

        item.getChildren("modifier_overlord").stream().map(ModifierSubject::new).forEach(this.modifierOverlord::add);
    }

    public void copy(SubjectType other) {
        this.sprite = other.sprite;
        this.diplomacyOverlordSprite = other.diplomacyOverlordSprite;
        this.diplomacySubjectSprite = other.diplomacySubjectSprite;
        this.hasOverlordsRuler = other.hasOverlordsRuler;
        this.canFightIndependenceWar = other.canFightIndependenceWar;
        this.isVoluntary = other.isVoluntary;
        this.transferTradePower = other.transferTradePower;
        this.transferTradeIfMerchantRepublic = other.transferTradeIfMerchantRepublic;
        this.joinsOverlordsWars = other.joinsOverlordsWars;
        this.joinsColonialWars = other.joinsColonialWars;
        this.canBeIntegrated = other.canBeIntegrated;
        this.canReleaseAndPlay = other.canReleaseAndPlay;
        this.usesTariffs = other.usesTariffs;
        this.dynamicallyCreatedDuringHistory = other.dynamicallyCreatedDuringHistory;
        this.eatsOverlordsColonies = other.eatsOverlordsColonies;
        this.hasColonialParent = other.hasColonialParent;
        this.overlordCanAttack = other.overlordCanAttack;
        this.overlordCanBeSubject = other.overlordCanBeSubject;
        this.canHaveSubjectsOfOtherTypes = other.canHaveSubjectsOfOtherTypes;
        this.canBeAnnexed = other.canBeAnnexed;
        this.takesDiploSlot = other.takesDiploSlot;
        this.hasPowerProjection = other.hasPowerProjection;
        this.canReleaseInPeace = other.canReleaseInPeace;
        this.usesMilitaryFocus = other.usesMilitaryFocus;
        this.overlordProtectsExternal = other.overlordProtectsExternal;
        this.countsForBorders = other.countsForBorders;
        this.overlordEnforcePeaceAttacking = other.overlordEnforcePeaceAttacking;
        this.canUseClaims = other.canUseClaims;
        this.givesDaimyoBonuses = other.givesDaimyoBonuses;
        this.getsHelpWithRebels = other.getsHelpWithRebels;
        this.shareRebelPopup = other.shareRebelPopup;
        this.separatistsBecomeSubjects = other.separatistsBecomeSubjects;
        this.allowsTakingLandWithoutIndependence = other.allowsTakingLandWithoutIndependence;
        this.canTransferInPeace = other.canTransferInPeace;
        this.canSetMilFocus = other.canSetMilFocus;
        this.canSendMissionaryToSubject = other.canSendMissionaryToSubject;
        this.canUnionBreak = other.canUnionBreak;
        this.overlordCanFabricateFor = other.overlordCanFabricateFor;
        this.maxGovernmentRank = other.maxGovernmentRank;
        this.citiesRequiredForBonuses = other.citiesRequiredForBonuses;
        this.trustOnStart = other.trustOnStart;
        this.baseLibertyDesire = other.baseLibertyDesire;
        this.libertyDesireNegativePrestige = other.libertyDesireNegativePrestige;
        this.libertyDesireDevelopmentRatio = other.libertyDesireDevelopmentRatio;
        this.libertyDesireSameDynasty = other.libertyDesireSameDynasty;
        this.libertyDesireRevolution = other.libertyDesireRevolution;
        this.paysOverlord = other.paysOverlord;
        this.forcelimitBonus = other.forcelimitBonus;
        this.forcelimitToOverlord = other.forcelimitToOverlord;
        this.militaryFocus = other.militaryFocus;
        this.relativePowerClass = other.relativePowerClass;
        this.diplomacyViewClass = other.diplomacyViewClass;
        this.canFight = other.canFight;
        this.canRival = other.canRival;
        this.canAlly = other.canAlly;
        this.canMarry = other.canMarry;
        this.embargoRivals = other.embargoRivals;
        this.supportLoyalists = other.supportLoyalists;
        this.subsidizeArmies = other.subsidizeArmies;
        this.scutage = other.scutage;
        this.sendOfficers = other.sendOfficers;
        this.divertTrade = other.divertTrade;
        this.placateRulers = other.placateRulers;
        this.placeRelativeOnThrone = other.placeRelativeOnThrone;
        this.enforceReligion = other.enforceReligion;
        this.customizeSubject = other.customizeSubject;
        this.replaceGovernor = other.replaceGovernor;
        this.grantProvince = other.grantProvince;
        this.enforceCulture = other.enforceCulture;
        this.siphonIncome = other.siphonIncome;
        this.fortifyMarch = other.fortifyMarch;
        this.seizeTerritory = other.seizeTerritory;
        this.startColonialWar = other.startColonialWar;
        this.grantCoreClaim = other.grantCoreClaim;
        this.sacrificeRuler = other.sacrificeRuler;
        this.sacrificeHeir = other.sacrificeHeir;
        this.increaseTariffs = other.increaseTariffs;
        this.decreaseTariffs = other.decreaseTariffs;
        this.takeOnDebt = other.takeOnDebt;
        this.bestowGifts = other.bestowGifts;
        this.sendAdditionalTroops = other.sendAdditionalTroops;
        this.demandArtifacts = other.demandArtifacts;
        this.demandAdditionalTribute = other.demandAdditionalTribute;
        this.forceSeppuku = other.forceSeppuku;
        this.pressSailors = other.pressSailors;
        this.contributeToCapital = other.contributeToCapital;
        this.forceIsolation = other.forceIsolation;
        this.returnLand = other.returnLand;
        this.conscriptGeneral = other.conscriptGeneral;
        this.knowledgeSharing = other.knowledgeSharing;
        this.blockSettlementGrowth = other.blockSettlementGrowth;
        this.allowSettlementGrowth = other.allowSettlementGrowth;
        this.swordHunt = other.swordHunt;
        this.sankinKotai = other.sankinKotai;
        this.expelRonin = other.expelRonin;
        this.modifierSubjects = other.modifierSubjects;
        this.modifierOverlord = other.modifierOverlord;
        this.overlordOpinionModifier = other.overlordOpinionModifier;
        this.subjectOpinionModifier = other.subjectOpinionModifier;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public ConditionAnd isPotentialOverlord() {
        ClausewitzItem child = this.item.getChild("is_potential_overlord");
        return child == null ? null : new ConditionAnd(child);
    }

    public String getSprite() {
        return sprite;
    }

    public String getDiplomacyOverlordSprite() {
        return diplomacyOverlordSprite;
    }

    public String getDiplomacySubjectSprite() {
        return diplomacySubjectSprite;
    }

    public boolean isHasOverlordsRuler() {
        return hasOverlordsRuler;
    }

    public boolean isCanFightIndependenceWar() {
        return canFightIndependenceWar;
    }

    public boolean isVoluntary() {
        return isVoluntary;
    }

    public boolean isTransferTradePower() {
        return transferTradePower;
    }

    public boolean isTransferTradeIfMerchantRepublic() {
        return transferTradeIfMerchantRepublic;
    }

    public boolean isJoinsOverlordsWars() {
        return joinsOverlordsWars;
    }

    public boolean isJoinsColonialWars() {
        return joinsColonialWars;
    }

    public boolean isCanBeIntegrated() {
        return canBeIntegrated;
    }

    public boolean isCanReleaseAndPlay() {
        return canReleaseAndPlay;
    }

    public boolean isUsesTariffs() {
        return usesTariffs;
    }

    public boolean isDynamicallyCreatedDuringHistory() {
        return dynamicallyCreatedDuringHistory;
    }

    public boolean isEatsOverlordsColonies() {
        return eatsOverlordsColonies;
    }

    public boolean isHasColonialParent() {
        return hasColonialParent;
    }

    public boolean isOverlordCanAttack() {
        return overlordCanAttack;
    }

    public boolean isOverlordCanBeSubject() {
        return overlordCanBeSubject;
    }

    public boolean isCanHaveSubjectsOfOtherTypes() {
        return canHaveSubjectsOfOtherTypes;
    }

    public boolean isCanBeAnnexed() {
        return canBeAnnexed;
    }

    public boolean isTakesDiploSlot() {
        return takesDiploSlot;
    }

    public boolean isHasPowerProjection() {
        return hasPowerProjection;
    }

    public boolean isCanReleaseInPeace() {
        return canReleaseInPeace;
    }

    public boolean isUsesMilitaryFocus() {
        return usesMilitaryFocus;
    }

    public boolean isOverlordProtectsExternal() {
        return overlordProtectsExternal;
    }

    public boolean isCountsForBorders() {
        return countsForBorders;
    }

    public boolean isOverlordEnforcePeaceAttacking() {
        return overlordEnforcePeaceAttacking;
    }

    public boolean isCanUseClaims() {
        return canUseClaims;
    }

    public boolean isGivesDaimyoBonuses() {
        return givesDaimyoBonuses;
    }

    public boolean isGetsHelpWithRebels() {
        return getsHelpWithRebels;
    }

    public boolean isShareRebelPopup() {
        return shareRebelPopup;
    }

    public boolean isSeparatistsBecomeSubjects() {
        return separatistsBecomeSubjects;
    }

    public boolean isAllowsTakingLandWithoutIndependence() {
        return allowsTakingLandWithoutIndependence;
    }

    public boolean isCanTransferInPeace() {
        return canTransferInPeace;
    }

    public boolean isCanSetMilFocus() {
        return canSetMilFocus;
    }

    public boolean isCanSendMissionaryToSubject() {
        return canSendMissionaryToSubject;
    }

    public boolean isCanUnionBreak() {
        return canUnionBreak;
    }

    public boolean isOverlordCanFabricateFor() {
        return overlordCanFabricateFor;
    }

    public int getMaxGovernmentRank() {
        return maxGovernmentRank;
    }

    public int getCitiesRequiredForBonuses() {
        return citiesRequiredForBonuses;
    }

    public int getTrustOnStart() {
        return trustOnStart;
    }

    public double getBaseLibertyDesire() {
        return baseLibertyDesire;
    }

    public double getLibertyDesireNegativePrestige() {
        return libertyDesireNegativePrestige;
    }

    public double getLibertyDesireDevelopmentRatio() {
        return libertyDesireDevelopmentRatio;
    }

    public double getLibertyDesireSameDynasty() {
        return libertyDesireSameDynasty;
    }

    public double getLibertyDesireRevolution() {
        return libertyDesireRevolution;
    }

    public double getPaysOverlord() {
        return paysOverlord;
    }

    public double getForcelimitBonus() {
        return forcelimitBonus;
    }

    public double getForcelimitToOverlord() {
        return forcelimitToOverlord;
    }

    public double getMilitaryFocus() {
        return militaryFocus;
    }

    public double getRelativePowerClass() {
        return relativePowerClass;
    }

    public int getDiplomacyViewClass() {
        return diplomacyViewClass;
    }

    public Map<SubjectTypeRelation, List<String>> getCanFight() {
        return canFight;
    }

    public Map<SubjectTypeRelation, List<String>> getCanRival() {
        return canRival;
    }

    public Map<SubjectTypeRelation, List<String>> getCanAlly() {
        return canAlly;
    }

    public Map<SubjectTypeRelation, List<String>> getCanMarry() {
        return canMarry;
    }

    public boolean isEmbargoRivals() {
        return embargoRivals;
    }

    public boolean isSupportLoyalists() {
        return supportLoyalists;
    }

    public boolean isSubsidizeArmies() {
        return subsidizeArmies;
    }

    public boolean isScutage() {
        return scutage;
    }

    public boolean isSendOfficers() {
        return sendOfficers;
    }

    public boolean isDivertTrade() {
        return divertTrade;
    }

    public boolean isPlacateRulers() {
        return placateRulers;
    }

    public boolean isPlaceRelativeOnThrone() {
        return placeRelativeOnThrone;
    }

    public boolean isEnforceReligion() {
        return enforceReligion;
    }

    public boolean isCustomizeSubject() {
        return customizeSubject;
    }

    public boolean isReplaceGovernor() {
        return replaceGovernor;
    }

    public boolean isGrantProvince() {
        return grantProvince;
    }

    public boolean isEnforceCulture() {
        return enforceCulture;
    }

    public boolean isSiphonIncome() {
        return siphonIncome;
    }

    public boolean isFortifyMarch() {
        return fortifyMarch;
    }

    public boolean isSeizeTerritory() {
        return seizeTerritory;
    }

    public boolean isStartColonialWar() {
        return startColonialWar;
    }

    public boolean isGrantCoreClaim() {
        return grantCoreClaim;
    }

    public boolean isSacrificeRuler() {
        return sacrificeRuler;
    }

    public boolean isSacrificeHeir() {
        return sacrificeHeir;
    }

    public boolean isIncreaseTariffs() {
        return increaseTariffs;
    }

    public boolean isDecreaseTariffs() {
        return decreaseTariffs;
    }

    public boolean isTakeOnDebt() {
        return takeOnDebt;
    }

    public boolean isBestowGifts() {
        return bestowGifts;
    }

    public boolean isSendAdditionalTroops() {
        return sendAdditionalTroops;
    }

    public boolean isDemandArtifacts() {
        return demandArtifacts;
    }

    public boolean isDemandAdditionalTribute() {
        return demandAdditionalTribute;
    }

    public boolean isForceSeppuku() {
        return forceSeppuku;
    }

    public boolean isPressSailors() {
        return pressSailors;
    }

    public boolean isContributeToCapital() {
        return contributeToCapital;
    }

    public boolean isForceIsolation() {
        return forceIsolation;
    }

    public boolean isReturnLand() {
        return returnLand;
    }

    public boolean isConscriptGeneral() {
        return conscriptGeneral;
    }

    public boolean isKnowledgeSharing() {
        return knowledgeSharing;
    }

    public boolean isBlockSettlementGrowth() {
        return blockSettlementGrowth;
    }

    public boolean isAllowSettlementGrowth() {
        return allowSettlementGrowth;
    }

    public boolean isSwordHunt() {
        return swordHunt;
    }

    public boolean isSankinKotai() {
        return sankinKotai;
    }

    public boolean isExpelRonin() {
        return expelRonin;
    }

    public List<ModifierSubject> getModifierSubjects() {
        return modifierSubjects;
    }

    public List<ModifierSubject> getModifierOverlord() {
        return modifierOverlord;
    }

    public String getOverlordOpinionModifier() {
        return overlordOpinionModifier;
    }

    public String getSubjectOpinionModifier() {
        return subjectOpinionModifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SubjectType subjectType = (SubjectType) o;

        return Objects.equals(getName(), subjectType.getName()) &&
               Objects.equals(getSprite(), subjectType.getSprite());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getSprite());
    }

    @Override
    public String toString() {
        return getName();
    }
}
