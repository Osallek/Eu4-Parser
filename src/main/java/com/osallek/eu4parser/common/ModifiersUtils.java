package com.osallek.eu4parser.common;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.eu4parser.model.game.GovernmentReform;
import com.osallek.eu4parser.model.game.Modifiers;
import com.osallek.eu4parser.model.save.country.Country;
import com.osallek.eu4parser.model.save.province.SaveProvince;
import org.apache.commons.collections4.CollectionUtils;

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

    private static final Map<String, Modifier.ModifierType> MODIFIERS_MAP = new HashMap<>();

    static {
        Arrays.stream(Modifier.values()).forEach(staticModifier -> MODIFIERS_MAP.put(staticModifier.name(), staticModifier.type));
    }

    public static void addModifier(String name, Modifier.ModifierType type) {
        MODIFIERS_MAP.put(name.toUpperCase(), type);
    }

    public static Modifier.ModifierType getType(String name) {
        return MODIFIERS_MAP.get(ClausewitzUtils.removeQuotes(name).toUpperCase());
    }

    /**
     * For constants (i.e. = yes) return 1 for yes, 0 for false
     */
    public static double getSum(double value, String name, Modifiers... modifiers) {
        Modifier.ModifierType type = getType(name);

        if (type == null) {
            return value;
        }

        switch (type) {
            case ADDITIVE:
                return value + Arrays.stream(modifiers)
                                     .map(m -> m.getModifier(name))
                                     .filter(Objects::nonNull)
                                     .map(NumbersUtils::toDouble)
                                     .filter(Objects::nonNull)
                                     .mapToDouble(Double::doubleValue)
                                     .sum();
            case MULTIPLICATIVE:
                return value * (1 + Arrays.stream(modifiers)
                                          .map(m -> m.getModifier(name))
                                          .filter(Objects::nonNull)
                                          .map(NumbersUtils::toDouble)
                                          .filter(Objects::nonNull)
                                          .mapToDouble(Double::doubleValue)
                                          .sum());
            case CONSTANT:
                return (value == 1 || Arrays.stream(modifiers).map(m -> m.getModifier(name)).filter(Objects::nonNull).anyMatch("yes"::equalsIgnoreCase)) ? 1
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
            if (Modifier.ModifierType.ADDITIVE.equals(ModifiersUtils.getType(key)) ||
                Modifier.ModifierType.MULTIPLICATIVE.equals(ModifiersUtils.getType(key))) {
                return ClausewitzUtils.doubleToString(BigDecimal.valueOf(NumbersUtils.toDouble(value)).multiply(BigDecimal.valueOf(finalScale)).doubleValue());
            }

            return value;
        });

        return toReturn;
    }

    public static Modifiers sumModifiers(Modifiers... modifiers) {
        if (modifiers.length > 0) {
            Set<String> enables = Arrays.stream(modifiers).map(Modifiers::getEnables).flatMap(Collection::stream).collect(Collectors.toSet());
            Map<String, String> modifier = Arrays.stream(modifiers)
                                                 .map(Modifiers::getModifiers)
                                                 .map(Map::entrySet)
                                                 .flatMap(Collection::stream)
                                                 .collect(Collectors.groupingBy(Map.Entry::getKey,
                                                                                Collectors.mapping(Map.Entry::getValue, Collectors.toList())))
                                                 .entrySet()
                                                 .stream()
                                                 .filter(entry -> CollectionUtils.isNotEmpty(entry.getValue()))
                                                 .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                                                     Modifier.ModifierType type = getType(entry.getKey());

                                                     if (type == null) {
                                                         return entry.getValue().get(0);
                                                     }

                                                     switch (type) {
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
                                                             return entry.getValue().stream().filter(Objects::nonNull).anyMatch("yes"::equalsIgnoreCase) ? "yes"
                                                                                                                                                         : "no";
                                                     }

                                                     return entry.getValue().get(0);
                                                 }));

            return new Modifiers(enables, modifier);
        }

        return new Modifiers();
    }

    public static void sumModifiers(String name, String value, Modifiers modifiers) {
        name = ClausewitzUtils.removeQuotes(name).toLowerCase();
        Modifier.ModifierType type = getType(name);

        if (type == null) {
            return;
        }

        switch (type) {
            case ADDITIVE:
            case MULTIPLICATIVE:
                modifiers.getModifiers().put(name, ClausewitzUtils.doubleToString(NumbersUtils.toDouble(modifiers.getModifiers().getOrDefault(name, "0"))
                                                                                  + NumbersUtils.doubleOrDefault(NumbersUtils.toDouble(value))));
                break;
            case CONSTANT:
                modifiers.getModifiers()
                         .put(name,
                              ("yes".equalsIgnoreCase(modifiers.getModifiers().getOrDefault(name, value)) || "yes".equalsIgnoreCase(value)) ? "yes" : "no");
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
