package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;
import fr.osallek.eu4parser.model.game.condition.ConditionAnd;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
        item.getVarAsString("copy_from")
            .flatMap(s -> subjectTypes.stream()
                                      .filter(subjectType -> StringUtils.isNotBlank(subjectType.getSprite()))
                                      .filter(subjectType -> subjectType.getName().equalsIgnoreCase(s))
                                      .findFirst())
            .ifPresent(this::copy);

        this.item = item;

        this.sprite = item.getVarAsString("sprite").filter(StringUtils::isNotBlank).orElse(this.sprite);
        this.diplomacyOverlordSprite = item.getVarAsString("diplomacy_overlord_sprite").filter(StringUtils::isNotBlank).orElse(this.diplomacyOverlordSprite);
        this.diplomacySubjectSprite = item.getVarAsString("diplomacy_subject_sprite").filter(StringUtils::isNotBlank).orElse(this.diplomacySubjectSprite);
        this.hasOverlordsRuler = item.getVarAsBool("has_overlords_ruler").orElse(this.hasOverlordsRuler);
        this.canFightIndependenceWar = item.getVarAsBool("can_fight_independence_war").orElse(this.canFightIndependenceWar);
        this.isVoluntary = item.getVarAsBool("is_voluntary").orElse(this.isVoluntary);
        this.transferTradePower = item.getVarAsBool("transfer_trade_power").orElse(this.transferTradePower);
        this.transferTradeIfMerchantRepublic = item.getVarAsBool("transfer_trade_if_merchant_republic").orElse(this.transferTradeIfMerchantRepublic);
        this.joinsOverlordsWars = item.getVarAsBool("joins_overlords_wars").orElse(this.joinsOverlordsWars);
        this.joinsColonialWars = item.getVarAsBool("joins_colonial_wars").orElse(this.joinsColonialWars);
        this.canBeIntegrated = item.getVarAsBool("can_be_integrated").orElse(this.canBeIntegrated);
        this.canReleaseAndPlay = item.getVarAsBool("can_release_and_play").orElse(this.canReleaseAndPlay);
        this.usesTariffs = item.getVarAsBool("uses_tariffs").orElse(this.usesTariffs);
        this.dynamicallyCreatedDuringHistory = item.getVarAsBool("dynamically_created_during_history").orElse(this.dynamicallyCreatedDuringHistory);
        this.eatsOverlordsColonies = item.getVarAsBool("eats_overlords_colonies").orElse(this.eatsOverlordsColonies);
        this.hasColonialParent = item.getVarAsBool("has_colonial_parent").orElse(this.hasColonialParent);
        this.overlordCanAttack = item.getVarAsBool("overlord_can_attack").orElse(this.overlordCanAttack);
        this.overlordCanBeSubject = item.getVarAsBool("overlord_can_be_subject").orElse(this.overlordCanBeSubject);
        this.canHaveSubjectsOfOtherTypes = item.getVarAsBool("can_have_subjects_of_other_types").orElse(this.canHaveSubjectsOfOtherTypes);
        this.canBeAnnexed = item.getVarAsBool("can_be_annexed").orElse(this.canBeAnnexed);
        this.takesDiploSlot = item.getVarAsBool("takes_diplo_slot").orElse(this.takesDiploSlot);
        this.hasPowerProjection = item.getVarAsBool("has_power_projection").orElse(this.hasPowerProjection);
        this.canReleaseInPeace = item.getVarAsBool("can_release_in_peace").orElse(this.canReleaseInPeace);
        this.usesMilitaryFocus = item.getVarAsBool("uses_military_focus").orElse(this.usesMilitaryFocus);
        this.overlordProtectsExternal = item.getVarAsBool("overlord_protects_external").orElse(this.overlordProtectsExternal);
        this.countsForBorders = item.getVarAsBool("counts_for_borders").orElse(this.countsForBorders);
        this.overlordEnforcePeaceAttacking = item.getVarAsBool("overlord_enforce_peace_attacking").orElse(this.overlordEnforcePeaceAttacking);
        this.canUseClaims = item.getVarAsBool("can_use_claims").orElse(this.canUseClaims);
        this.givesDaimyoBonuses = item.getVarAsBool("gives_daimyo_bonuses").orElse(this.givesDaimyoBonuses);
        this.getsHelpWithRebels = item.getVarAsBool("gets_help_with_rebels").orElse(this.getsHelpWithRebels);
        this.shareRebelPopup = item.getVarAsBool("share_rebel_popup").orElse(this.shareRebelPopup);
        this.separatistsBecomeSubjects = item.getVarAsBool("separatists_become_subjects").orElse(this.separatistsBecomeSubjects);
        this.allowsTakingLandWithoutIndependence = item.getVarAsBool("allows_taking_land_without_independence")
                                                       .orElse(this.allowsTakingLandWithoutIndependence);
        this.canTransferInPeace = item.getVarAsBool("can_transfer_in_peace").orElse(this.canTransferInPeace);
        this.canSetMilFocus = item.getVarAsBool("can_set_mil_focus").orElse(this.canSetMilFocus);
        this.canSendMissionaryToSubject = item.getVarAsBool("can_send_missionary_to_subject").orElse(this.canSendMissionaryToSubject);
        this.canUnionBreak = item.getVarAsBool("can_union_break").orElse(this.canUnionBreak);
        this.overlordCanFabricateFor = item.getVarAsBool("overlord_can_fabricate_for").orElse(this.overlordCanFabricateFor);
        this.maxGovernmentRank = item.getVarAsInt("max_government_rank").orElse(maxGovernmentRank);
        this.citiesRequiredForBonuses = item.getVarAsInt("cities_required_for_bonuses").orElse(citiesRequiredForBonuses);
        this.trustOnStart = item.getVarAsInt("trust_on_start").orElse(trustOnStart);
        this.baseLibertyDesire = item.getVarAsDouble("base_liberty_desire").orElse(baseLibertyDesire);
        this.libertyDesireNegativePrestige = item.getVarAsDouble("liberty_desire_negative_prestige").orElse(libertyDesireNegativePrestige);
        this.libertyDesireDevelopmentRatio = item.getVarAsDouble("liberty_desire_development_ratio").orElse(libertyDesireDevelopmentRatio);
        this.libertyDesireSameDynasty = item.getVarAsDouble("liberty_desire_same_dynasty").orElse(libertyDesireSameDynasty);
        this.libertyDesireRevolution = item.getVarAsDouble("liberty_desire_revolution").orElse(libertyDesireRevolution);
        this.paysOverlord = item.getVarAsDouble("pays_overlord").orElse(paysOverlord);
        this.forcelimitBonus = item.getVarAsDouble("forcelimit_bonus").orElse(forcelimitBonus);
        this.forcelimitToOverlord = item.getVarAsDouble("forcelimit_to_overlord").orElse(forcelimitToOverlord);
        this.militaryFocus = item.getVarAsDouble("military_focus").orElse(militaryFocus);
        this.relativePowerClass = item.getVarAsDouble("relative_power_class").orElse(relativePowerClass);
        this.diplomacyViewClass = item.getVarAsInt("diplomacy_view_class").orElse(diplomacyViewClass);
        this.embargoRivals = item.getVarAsBool("embargo_rivals").orElse(this.embargoRivals);
        this.supportLoyalists = item.getVarAsBool("support_loyalists").orElse(this.supportLoyalists);
        this.subsidizeArmies = item.getVarAsBool("subsidize_armies").orElse(this.subsidizeArmies);
        this.scutage = item.getVarAsBool("scutage").orElse(this.scutage);
        this.sendOfficers = item.getVarAsBool("send_officers").orElse(this.sendOfficers);
        this.divertTrade = item.getVarAsBool("divert_trade").orElse(this.divertTrade);
        this.placateRulers = item.getVarAsBool("placate_rulers").orElse(this.placateRulers);
        this.placeRelativeOnThrone = item.getVarAsBool("place_relative_on_throne").orElse(this.placeRelativeOnThrone);
        this.enforceReligion = item.getVarAsBool("enforce_religion").orElse(this.enforceReligion);
        this.customizeSubject = item.getVarAsBool("customize_subject").orElse(this.customizeSubject);
        this.replaceGovernor = item.getVarAsBool("replace_governor").orElse(this.replaceGovernor);
        this.grantProvince = item.getVarAsBool("grant_province").orElse(this.grantProvince);
        this.enforceCulture = item.getVarAsBool("enforce_culture").orElse(this.enforceCulture);
        this.siphonIncome = item.getVarAsBool("siphon_income").orElse(this.siphonIncome);
        this.fortifyMarch = item.getVarAsBool("fortify_march").orElse(this.fortifyMarch);
        this.seizeTerritory = item.getVarAsBool("seize_territory").orElse(this.seizeTerritory);
        this.startColonialWar = item.getVarAsBool("start_colonial_war").orElse(this.startColonialWar);
        this.grantCoreClaim = item.getVarAsBool("grant_core_claim").orElse(this.grantCoreClaim);
        this.sacrificeRuler = item.getVarAsBool("sacrifice_ruler").orElse(this.sacrificeRuler);
        this.sacrificeHeir = item.getVarAsBool("sacrifice_heir").orElse(this.sacrificeHeir);
        this.increaseTariffs = item.getVarAsBool("increase_tariffs").orElse(this.increaseTariffs);
        this.decreaseTariffs = item.getVarAsBool("decrease_tariffs").orElse(this.decreaseTariffs);
        this.takeOnDebt = item.getVarAsBool("takeondebt").orElse(this.takeOnDebt);
        this.bestowGifts = item.getVarAsBool("bestow_gifts").orElse(this.bestowGifts);
        this.sendAdditionalTroops = item.getVarAsBool("send_additional_troops").orElse(this.sendAdditionalTroops);
        this.demandArtifacts = item.getVarAsBool("demand_artifacts").orElse(this.demandArtifacts);
        this.demandAdditionalTribute = item.getVarAsBool("demand_additional_tribute").orElse(this.demandAdditionalTribute);
        this.forceSeppuku = item.getVarAsBool("force_seppuku").orElse(this.forceSeppuku);
        this.pressSailors = item.getVarAsBool("press_sailors").orElse(this.pressSailors);
        this.contributeToCapital = item.getVarAsBool("contribute_to_capital").orElse(this.contributeToCapital);
        this.forceIsolation = item.getVarAsBool("force_isolation").orElse(this.forceIsolation);
        this.returnLand = item.getVarAsBool("return_land").orElse(this.returnLand);
        this.conscriptGeneral = item.getVarAsBool("conscript_general").orElse(this.conscriptGeneral);
        this.knowledgeSharing = item.getVarAsBool("knowledge_sharing").orElse(this.knowledgeSharing);
        this.blockSettlementGrowth = item.getVarAsBool("block_settlement_growth").orElse(this.blockSettlementGrowth);
        this.allowSettlementGrowth = item.getVarAsBool("allow_settlement_growth").orElse(this.allowSettlementGrowth);
        this.swordHunt = item.getVarAsBool("sword_hunt").orElse(this.swordHunt);
        this.sankinKotai = item.getVarAsBool("sankin_kotai").orElse(this.sankinKotai);
        this.expelRonin = item.getVarAsBool("expel_ronin").orElse(this.expelRonin);
        this.overlordOpinionModifier = item.getVarAsString("overlord_opinion_modifier").filter(StringUtils::isNotBlank).orElse(this.overlordOpinionModifier);
        this.subjectOpinionModifier = item.getVarAsString("subject_opinion_modifier").filter(StringUtils::isNotBlank).orElse(this.subjectOpinionModifier);
        this.canFight = item.getChild("can_fight")
                            .map(ClausewitzItem::getVariables)
                            .stream()
                            .flatMap(Collection::stream)
                            .collect(Collectors.groupingBy(variable -> SubjectTypeRelation.valueOf(variable.getName().toUpperCase()),
                                                           Collectors.mapping(ClausewitzVariable::getValue, Collectors.toList())));
        this.canRival = item.getChild("can_rival")
                            .map(ClausewitzItem::getVariables)
                            .stream()
                            .flatMap(Collection::stream)
                            .collect(Collectors.groupingBy(variable -> SubjectTypeRelation.valueOf(variable.getName().toUpperCase()),
                                                           Collectors.mapping(ClausewitzVariable::getValue, Collectors.toList())));
        this.canAlly = item.getChild("can_ally")
                            .map(ClausewitzItem::getVariables)
                            .stream()
                            .flatMap(Collection::stream)
                            .collect(Collectors.groupingBy(variable -> SubjectTypeRelation.valueOf(variable.getName().toUpperCase()),
                                                           Collectors.mapping(ClausewitzVariable::getValue, Collectors.toList())));
        this.canMarry = item.getChild("can_marry")
                            .map(ClausewitzItem::getVariables)
                            .stream()
                            .flatMap(Collection::stream)
                            .collect(Collectors.groupingBy(variable -> SubjectTypeRelation.valueOf(variable.getName().toUpperCase()),
                                                           Collectors.mapping(ClausewitzVariable::getValue, Collectors.toList())));

        if (item.getVarAsString("modifier_subject").isPresent() && "clear".equalsIgnoreCase(item.getVarAsString("modifier_subject").get())) {
            this.modifierSubjects.clear();
        }

        item.getChildren("modifier_subject").stream().map(ModifierSubject::new).forEach(this.modifierSubjects::add);

        if (item.getVarAsString("modifier_overlord").isPresent() && "clear".equalsIgnoreCase(item.getVarAsString("modifier_overlord").get())) {
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

    public Optional<ConditionAnd> isPotentialOverlord() {
        return this.item.getChild("is_potential_overlord").map(ConditionAnd::new);
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
