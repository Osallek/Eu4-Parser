package fr.osallek.eu4parser.common;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.eu4parser.model.Power;
import fr.osallek.eu4parser.model.game.Advisor;
import fr.osallek.eu4parser.model.game.Area;
import fr.osallek.eu4parser.model.game.Culture;
import fr.osallek.eu4parser.model.game.CustomizableLocalization;
import fr.osallek.eu4parser.model.game.Game;
import fr.osallek.eu4parser.model.game.GovernmentName;
import fr.osallek.eu4parser.model.game.Region;
import fr.osallek.eu4parser.model.game.SuperRegion;
import fr.osallek.eu4parser.model.game.localisation.Eu4Language;
import fr.osallek.eu4parser.model.game.localisation.Localisation;
import fr.osallek.eu4parser.model.save.Save;
import fr.osallek.eu4parser.model.save.SaveReligion;
import fr.osallek.eu4parser.model.save.country.FlagShip;
import fr.osallek.eu4parser.model.save.country.Heir;
import fr.osallek.eu4parser.model.save.country.Monarch;
import fr.osallek.eu4parser.model.save.country.Navy;
import fr.osallek.eu4parser.model.save.country.Queen;
import fr.osallek.eu4parser.model.save.country.SaveCountry;
import fr.osallek.eu4parser.model.save.country.SaveTradeCompany;
import fr.osallek.eu4parser.model.save.country.Ship;
import fr.osallek.eu4parser.model.save.province.SaveAdvisor;
import fr.osallek.eu4parser.model.save.province.SaveProvince;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class LocalisationUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalisationUtils.class);

    private LocalisationUtils() {}

    public static String replaceScope(Save save, Object root, String scope, Eu4Language language) {
        if (save == null || root == null || StringUtils.isBlank(scope) || language == null) {
            return "";
        }

        scope = StringUtils.stripEnd(StringUtils.stripStart(scope, "["), "]");

        Iterator<String> keys = List.of(StringUtils.split(scope, '.')).iterator();
        Optional<?> current = Optional.of(root);

        while (keys.hasNext()) {
            String key = keys.next();

            if (current.get() instanceof SaveCountry country) {
                switch (key) {
                    case "Capital" -> current = getCapital(country);
                    case "ColonialParent" -> current = getColonialParent(country);
                    case "Culture" -> current = getCulture(country);
                    case "From" -> current = getFrom(country);
                    case "Heir" -> current = getHeir(country);
                    case "Monarch" -> current = getMonarch(country);
                    case "Consort" -> current = getConsort(country);
                    case "Overlord" -> current = getOverlord(country);
                    case "Religion" -> current = getReligion(country);
                    case "Root" -> current = getRoot(country);
                    case "This" -> current = getThis(country);
                    case "Dip_Advisor" -> current = getAdmAdvisor(country);
                    case "Adm_Advisor" -> current = getDipAdvisor(country);
                    case "Mil_Advisor" -> current = getMilAdvisor(country);
                    case "GetAdm" -> current = getAdmAdvisor(country).map(a -> getAdvisorType(a, language));
                    case "GetDip" -> current = getDipAdvisor(country).map(a -> getAdvisorType(a, language));
                    case "GetMil" -> current = getMilAdvisor(country).map(a -> getAdvisorType(a, language));
                    //Commands
                    case "GetAdjective" -> current = getAdjective(country, language);
                    case "GetDate" -> current = getDate(save, language);
                    case "GetFlagshipName" -> current = getFlagShip(country);
                    case "GetMonth" -> current = getMonth(save, language);
                    case "GetName" -> current = getName(country, language);
                    case "GetTag" -> current = getTag(country);
                    case "GetYear" -> current = getYear(save);
                    case "GovernmentName" -> current = getGovernmentName(country, language);
                    default -> {
                        CustomizableLocalization localization = save.getGame().getCustomizableLocalization(key);

                        if (localization != null) {
                            current = getCustomisableLocalisation(save, current.orElse(null), key, language);
                        } else {
                            current = getTag(save, key);
                        }

                        if (current.isEmpty()) {
                            LOGGER.warn("Could not find scope {} in country", key);
                        }
                    }
                }
            } else if (current.get() instanceof SaveProvince province) {
                switch (key) {
                    case "Culture" -> current = getCulture(province);
                    case "Owner" -> current = getOwner(province);
                    case "Religion" -> current = getReligion(province);
                    case "TradeCompany" -> current = getTradeCompany(province);
                    case "From" -> current = getFrom(province);
                    case "Root" -> current = getRoot(province);
                    case "This" -> current = getThis(province);
                    //Commands
                    case "GetAreaName" -> current = getAreaName(province, language);
                    case "GetRegionName" -> current = getRegionName(province, language);
                    case "GetContinentName" -> current = getContinentName(province, language);
                    case "GetSuperRegionName" -> current = getSuperRegionName(province, language);
                    case "GetCapitalName" -> current = getCapitalName(province);
                    case "GetDate" -> current = getDate(save, language);
                    case "GetMonth" -> current = getMonth(save, language);
                    case "GetName" -> current = getName(province);
                    case "GetTradeGoodsName" -> current = getTradeGoodName(province, language);
                    case "GetYear" -> current = getYear(save);
                    default -> {
                        CustomizableLocalization localization = save.getGame().getCustomizableLocalization(key);

                        if (localization != null) {
                            current = getCustomisableLocalisation(save, current.orElse(null), key, language);
                        } else {
                            current = getTag(save, key);
                        }

                        if (current.isEmpty()) {
                            LOGGER.warn("Could not find scope {} in province", key);
                        }
                    }
                }
            } else if (current.get() instanceof SaveAdvisor advisor) {
                switch (key) {
                    //Commands
                    case "GetDate" -> current = getDate(save, language);
                    case "GetMonth" -> current = getMonth(save, language);
                    case "GetName" -> current = getName(advisor);
                    case "GetYear" -> current = getYear(save);
                    case "GetWomanMan" -> current = getAdvisorType(advisor, language);
                    default -> {
                        CustomizableLocalization localization = save.getGame().getCustomizableLocalization(key);

                        if (localization != null) {
                            current = getCustomisableLocalisation(save, current.orElse(null), key, language);
                        } else {
                            current = getTag(save, key);
                        }

                        if (current.isEmpty()) {
                            LOGGER.warn("Could not find scope {} in advisor", key);
                        }
                    }
                }
            } else if (current.get() instanceof Monarch monarch) {
                switch (key) {
                    case "Dynasty" -> current = getDynasty(monarch);
                    //Commands
                    case "GetDate" -> current = getDate(save, language);
                    case "GetMonth" -> current = getMonth(save, language);
                    case "GetName" -> current = getName(monarch);
                    case "GetYear" -> current = getYear(save);
                    case "GetAdm" -> current = getAdm(monarch);
                    case "GetDip" -> current = getDip(monarch);
                    case "GetMil" -> current = getMil(monarch);
                    case "GetTitle" -> current = getTitle(monarch, language);
                    case "GetWomanMan" -> current = getTitle(monarch, language);
                    default -> {
                        CustomizableLocalization localization = save.getGame().getCustomizableLocalization(key);

                        if (localization != null) {
                            current = getCustomisableLocalisation(save, current.orElse(null), key, language);
                        } else {
                            current = getTag(save, key);
                        }

                        if (current.isEmpty()) {
                            LOGGER.warn("Could not find scope {} in monarch", key);
                        }
                    }
                }
            } else if (current.get() instanceof SaveReligion religion) {
                switch (key) {
                    //Commands
                    case "GetDate" -> current = getDate(save, language);
                    case "GetMonth" -> current = getMonth(save, language);
                    case "GetName" -> current = getName(religion, language);
                    case "GetYear" -> current = getYear(save);
                    case "GetGroupName" -> current = getGroup(religion, language);
                    default -> {
                        CustomizableLocalization localization = save.getGame().getCustomizableLocalization(key);

                        if (localization != null) {
                            current = getCustomisableLocalisation(save, current.orElse(null), key, language);
                        } else {
                            current = getTag(save, key);
                        }

                        if (current.isEmpty()) {
                            LOGGER.warn("Could not find scope {} in religion", key);
                        }
                    }
                }
            } else if (current.get() instanceof Culture culture) {
                switch (key) {
                    //Commands
                    case "GetDate" -> current = getDate(save, language);
                    case "GetMonth" -> current = getMonth(save, language);
                    case "GetName" -> current = getName(culture, language);
                    case "GetYear" -> current = getYear(save);
                    case "GetGroupName" -> current = getGroup(culture, language);
                    default -> {
                        CustomizableLocalization localization = save.getGame().getCustomizableLocalization(key);

                        if (localization != null) {
                            current = getCustomisableLocalisation(save, current.orElse(null), key, language);
                        } else {
                            current = getTag(save, key);
                        }

                        if (current.isEmpty()) {
                            LOGGER.warn("Could not find scope {} in culture", key);
                        }
                    }
                }
            } else if (current.get() instanceof SaveTradeCompany tradeCompany) {
                switch (key) {
                    //Commands
                    case "GetDate" -> current = getDate(save, language);
                    case "GetMonth" -> current = getMonth(save, language);
                    case "GetName" -> current = getName(tradeCompany);
                    case "GetYear" -> current = getYear(save);
                    default -> {
                        CustomizableLocalization localization = save.getGame().getCustomizableLocalization(key);

                        if (localization != null) {
                            current = getCustomisableLocalisation(save, current.orElse(null), key, language);
                        } else {
                            current = getTag(save, key);
                        }

                        if (current.isEmpty()) {
                            LOGGER.warn("Could not find scope {} in culture", key);
                        }
                    }
                }
            }

            if (current.isEmpty()) {
                break;
            } else if (current.get() instanceof String) {
                return (String) current.get();
            }
        }

        return "";
    }

    public static DateTimeFormatter getPrettyDateFormat(Eu4Language language) {
        return DateTimeFormatter.ofPattern(
                DateTimeFormatterBuilder.getLocalizedDateTimePattern(FormatStyle.SHORT, null, IsoChronology.INSTANCE, language.locale)
                                        .replace("yyyy", "yy")
                                        .replace("yy", "yyyy")
                                        .replace("uuuu", "uu")
                                        .replace("uu", "uuuu"));
    }

    public static Optional<String> getCustomisableLocalisation(Save save, Object root, String key, Eu4Language language) {
        return Optional.ofNullable(key)
                       .flatMap(s -> Optional.ofNullable(save).map(Save::getGame).map(game -> game.getCustomizableLocalization(s)))
                       .filter(l -> CollectionUtils.isNotEmpty(l.getTexts()))
                       .flatMap(l -> {
                           if (root instanceof SaveCountry country) {
                               return l.getTexts().stream().filter(t -> t.getTrigger() == null || t.getTrigger().apply(country, country)).findFirst();
                           } else if (root instanceof SaveProvince province) {
                               return l.getTexts().stream().filter(t -> t.getTrigger() == null || t.getTrigger().apply(province, province)).findFirst();
                           } else {
                               return Optional.empty();
                           }
                       })
                       .flatMap(t -> Optional.of(save)
                                             .map(Save::getGame)
                                             .map(game -> game.getComputedLocalisation(save, root, t.getLocalisationKey(), language)));
    }

    public static Optional<SaveProvince> getCapital(SaveCountry country) {
        return Optional.ofNullable(country).map(SaveCountry::getCapital);
    }

    public static Optional<SaveCountry> getColonialParent(SaveCountry country) {
        return Optional.ofNullable(country).map(SaveCountry::getColonialParent);
    }

    public static Optional<Culture> getCulture(SaveCountry country) {
        return Optional.ofNullable(country).map(SaveCountry::getPrimaryCulture);
    }

    public static Optional<Culture> getCulture(SaveProvince province) {
        return Optional.ofNullable(province).map(SaveProvince::getCulture);
    }

    public static Optional<SaveCountry> getFrom(SaveCountry country) {
        return Optional.ofNullable(country);
    }

    public static Optional<SaveProvince> getFrom(SaveProvince province) {
        return Optional.ofNullable(province);
    }

    public static Optional<SaveCountry> getRoot(SaveCountry country) {
        return Optional.ofNullable(country);
    }

    public static Optional<SaveProvince> getRoot(SaveProvince province) {
        return Optional.ofNullable(province);
    }

    public static Optional<SaveCountry> getThis(SaveCountry country) {
        return Optional.ofNullable(country);
    }

    public static Optional<SaveProvince> getThis(SaveProvince province) {
        return Optional.ofNullable(province);
    }

    public static Optional<Heir> getHeir(SaveCountry country) {
        return Optional.ofNullable(country).map(SaveCountry::getHeir);
    }

    public static Optional<Monarch> getMonarch(SaveCountry country) {
        return Optional.ofNullable(country).map(SaveCountry::getMonarch);
    }

    public static Optional<Queen> getConsort(SaveCountry country) {
        return Optional.ofNullable(country).map(SaveCountry::getQueen);
    }

    public static Optional<SaveCountry> getOverlord(SaveCountry country) {
        return Optional.ofNullable(country).map(SaveCountry::getOverlord);
    }

    public static Optional<SaveCountry> getOwner(SaveProvince province) {
        return Optional.ofNullable(province).map(SaveProvince::getOwner);
    }

    public static Optional<SaveReligion> getReligion(SaveProvince province) {
        return Optional.ofNullable(province).map(SaveProvince::getReligion);
    }

    public static Optional<SaveReligion> getReligion(SaveCountry country) {
        return Optional.ofNullable(country).map(SaveCountry::getReligion);
    }

    public static Optional<SaveTradeCompany> getTradeCompany(SaveProvince province) {
        return Optional.ofNullable(province).map(SaveProvince::getTradeCompany);
    }

    public static Optional<SaveAdvisor> getAdmAdvisor(SaveCountry country) {
        return Optional.ofNullable(country)
                       .map(SaveCountry::getActiveAdvisors)
                       .filter(MapUtils::isNotEmpty)
                       .map(Map::values)
                       .stream()
                       .flatMap(Collection::stream)
                       .filter(a -> Power.ADM.equals(a.getGameAdvisor().getPower()))
                       .findFirst();
    }

    public static Optional<SaveAdvisor> getDipAdvisor(SaveCountry country) {
        return Optional.ofNullable(country)
                       .map(SaveCountry::getActiveAdvisors)
                       .filter(MapUtils::isNotEmpty)
                       .map(Map::values)
                       .stream()
                       .flatMap(Collection::stream)
                       .filter(a -> Power.DIP.equals(a.getGameAdvisor().getPower()))
                       .findFirst();
    }

    public static Optional<SaveAdvisor> getMilAdvisor(SaveCountry country) {
        return Optional.ofNullable(country)
                       .map(SaveCountry::getActiveAdvisors)
                       .filter(MapUtils::isNotEmpty)
                       .map(Map::values)
                       .stream()
                       .flatMap(Collection::stream)
                       .filter(a -> Power.MIL.equals(a.getGameAdvisor().getPower()))
                       .findFirst();
    }

    public static Optional<SaveCountry> getTag(Save save, String tag) {
        return Optional.ofNullable(save).map(s -> save.getCountry(tag));
    }

    public static Optional<String> getAreaName(SaveProvince province, Eu4Language language) {
        return Optional.ofNullable(province)
                       .map(SaveProvince::getSave)
                       .map(Save::getGame)
                       .flatMap(game -> Optional.of(province)
                                                .map(SaveProvince::getArea)
                                                .map(Area::getName)
                                                .map(s -> game.getLocalisation(s, language))
                                                .map(Localisation::getValue));
    }

    public static Optional<String> getRegionName(SaveProvince province, Eu4Language language) {
        return Optional.ofNullable(province)
                       .map(SaveProvince::getSave)
                       .map(Save::getGame)
                       .flatMap(game -> Optional.of(province)
                                                .map(SaveProvince::getArea)
                                                .map(Area::getRegion)
                                                .map(Region::getName)
                                                .map(s -> game.getLocalisation(s, language))
                                                .map(Localisation::getValue));
    }

    public static Optional<String> getSuperRegionName(SaveProvince province, Eu4Language language) {
        return Optional.ofNullable(province)
                       .map(SaveProvince::getSave)
                       .map(Save::getGame)
                       .flatMap(game -> Optional.of(province)
                                                .map(SaveProvince::getArea)
                                                .map(Area::getRegion)
                                                .map(Region::getSuperRegion)
                                                .map(SuperRegion::getName)
                                                .map(s -> game.getLocalisation(s, language))
                                                .map(Localisation::getValue));
    }

    public static Optional<String> getContinentName(SaveProvince province, Eu4Language language) {
        return Optional.ofNullable(province)
                       .map(SaveProvince::getSave)
                       .map(Save::getGame)
                       .map(Game::getContinents)
                       .filter(CollectionUtils::isNotEmpty)
                       .flatMap(list -> list
                               .stream()
                               .filter(l -> l.getProvinces().contains(province.getId()))
                               .findFirst()
                               .map(l -> province.getSave().getGame().getLocalisation(l.getName(), language))
                               .map(Localisation::getValue));
    }

    public static Optional<String> getAdm(Monarch monarch) {
        return Optional.ofNullable(monarch).map(Monarch::getAdm).map(String::valueOf);
    }

    public static Optional<String> getAdvisorType(SaveAdvisor advisor, Eu4Language language) {
        return Optional.ofNullable(advisor)
                       .map(SaveAdvisor::getGameAdvisor)
                       .map(Advisor::getName)
                       .flatMap(s -> Optional.of(advisor).map(SaveAdvisor::getSave).map(Save::getGame).map(game -> game.getLocalisation(s, language)))
                       .map(Localisation::getValue);
    }

    public static Optional<String> getCapitalName(SaveProvince province) {
        return Optional.ofNullable(province).map(p -> ObjectUtils.firstNonNull(p.getCapital(), p.getName()));
    }

    public static Optional<String> getDate(Save save, Eu4Language language) {
        return Optional.ofNullable(save).map(Save::getDate).map(date -> date.format(getPrettyDateFormat(language)));
    }

    public static Optional<String> getDip(Monarch monarch) {
        return Optional.ofNullable(monarch).map(Monarch::getDip).map(String::valueOf);
    }

    public static Optional<String> getFlagShip(SaveCountry country) {
        return Optional.ofNullable(country)
                       .map(SaveCountry::getNavies)
                       .filter(MapUtils::isNotEmpty)
                       .map(Map::values)
                       .filter(CollectionUtils::isNotEmpty)
                       .stream()
                       .flatMap(Collection::stream)
                       .map(Navy::getShips)
                       .filter(CollectionUtils::isNotEmpty)
                       .flatMap(Collection::stream)
                       .map(Ship::getFlagShip)
                       .filter(Objects::nonNull)
                       .findFirst()
                       .map(FlagShip::getName);
    }

    public static Optional<String> getGroup(Culture culture, Eu4Language language) {
        return Optional.ofNullable(culture)
                       .map(Culture::getCultureGroup)
                       .map(g -> culture.getGame().getLocalisation(g.getName(), language))
                       .map(Localisation::getValue);
    }

    public static Optional<String> getGroup(SaveReligion saveReligion, Eu4Language language) {
        return Optional.ofNullable(saveReligion)
                       .map(SaveReligion::getReligionGroup)
                       .map(g -> saveReligion.getSave().getGame().getLocalisation(g.getName(), language))
                       .map(Localisation::getValue);
    }

    public static Optional<String> getMil(Monarch monarch) {
        return Optional.ofNullable(monarch.getMil()).map(String::valueOf);
    }

    public static Optional<String> getMonth(Save save, Eu4Language language) {
        return Optional.ofNullable(save)
                       .map(Save::getDate)
                       .map(LocalDate::getMonth)
                       .map(month -> month.getDisplayName(TextStyle.FULL, language.locale))
                       .map(StringUtils::capitalize);
    }

    public static Optional<String> getName(SaveProvince province) {
        return Optional.ofNullable(province.getName());
    }

    public static Optional<String> getName(SaveCountry country, Eu4Language language) {
        return Optional.ofNullable(ObjectUtils.firstNonNull(country.getCustomName(),
                                                            country.getName(),
                                                            country.getLocalizedName(),
                                                            Optional.ofNullable(country.getSave().getGame().getLocalisation(country.getTag(), language))
                                                                    .map(Localisation::getValue)
                                                                    .orElse(null)));
    }

    public static Optional<String> getName(Culture culture, Eu4Language language) {
        return Optional.ofNullable(culture)
                       .map(Culture::getGame)
                       .map(game -> game.getLocalisation(culture.getName(), language))
                       .map(Localisation::getValue)
                       .map(StringUtils::capitalize);
    }

    public static Optional<String> getName(SaveReligion religion, Eu4Language language) {
        return Optional.ofNullable(religion)
                       .map(SaveReligion::getSave)
                       .map(Save::getGame)
                       .map(game -> game.getLocalisation(religion.getName(), language))
                       .map(Localisation::getValue)
                       .map(StringUtils::capitalize);
    }

    public static Optional<String> getName(Monarch monarch) {
        return Optional.ofNullable(monarch).map(Monarch::getMonarchName);
    }

    public static Optional<String> getName(Heir heir) {
        return Optional.ofNullable(heir).map(Heir::getName);
    }

    public static Optional<String> getName(Queen queen) {
        return Optional.ofNullable(queen).map(Queen::getName);
    }

    public static Optional<String> getName(SaveAdvisor advisor) {
        return Optional.ofNullable(advisor).map(SaveAdvisor::getName);
    }

    public static Optional<String> getName(SaveTradeCompany tradeCompany) {
        return Optional.ofNullable(tradeCompany).map(SaveTradeCompany::getName);
    }

    public static Optional<String> getTag(SaveCountry country) {
        return Optional.ofNullable(country).map(SaveCountry::getTag);
    }

    public static Optional<String> getTitle(Monarch monarch, Eu4Language language) {
        return Optional.ofNullable(monarch)
                       .flatMap(m -> Optional.ofNullable(m.getCountry())
                                             .flatMap(c -> Optional.ofNullable(c.getGovernmentName())
                                                                   .map(name -> {
                                                                       if (Monarch.class.equals(monarch.getClass())) {
                                                                           return BooleanUtils.toBoolean(monarch.getFemale()) ? name.getRulersFemale()
                                                                                                                              : name.getRulersMale();
                                                                       } else if (Queen.class.equals(monarch.getClass())) {
                                                                           return BooleanUtils.toBoolean(monarch.getFemale()) ? name.getConsortsFemale()
                                                                                                                              : name.getConsortsMale();
                                                                       } else if (Heir.class.equals(monarch.getClass())) {
                                                                           return BooleanUtils.toBoolean(monarch.getFemale()) ? name.getHeirsFemale()
                                                                                                                              : name.getHeirsMale();
                                                                       } else {
                                                                           return null;
                                                                       }
                                                                   })
                                                                   .map(ranks -> ranks.get(c.getGovernmentLevel()))))
                       .flatMap(s -> Optional.of(monarch)
                                             .map(Monarch::getCountry)
                                             .map(SaveCountry::getSave)
                                             .map(Save::getGame)
                                             .map(game -> game.getLocalisation(s, language)))
                       .map(Localisation::getValue)
                       .map(StringUtils::capitalize);
    }

    public static Optional<String> getTradeGoodName(SaveProvince province, Eu4Language language) {
        return Optional.ofNullable(province)
                       .map(SaveProvince::getTradeGood)
                       .flatMap(good -> Optional.of(province)
                                                .map(SaveProvince::getSave)
                                                .map(Save::getGame)
                                                .map(game -> game.getLocalisation(good.getName(), language)))
                       .map(Localisation::getValue);
    }

    public static Optional<String> getYear(Save save) {
        return Optional.ofNullable(save).map(Save::getDate).map(LocalDate::getYear).map(String::valueOf);
    }

    public static Optional<String> getGovernmentName(SaveCountry country, Eu4Language language) {
        return Optional.ofNullable(country)
                       .map(SaveCountry::getGovernmentName)
                       .map(GovernmentName::getRanks)
                       .map(map -> map.get(country.getGovernmentLevel()))
                       .flatMap(s -> Optional.of(country).map(SaveCountry::getSave).map(Save::getGame).map(game -> game.getLocalisation(s, language)))
                       .map(Localisation::getValue);
    }

    public static Optional<String> getAdjective(SaveCountry country, Eu4Language language) {
        return Optional.ofNullable(country)
                       .map(SaveCountry::getTag)
                       .flatMap(s -> Optional.of(country)
                                             .map(SaveCountry::getSave)
                                             .map(Save::getGame)
                                             .map(game -> ObjectUtils.firstNonNull(game.getLocalisation(s + "_ADJ", language),
                                                                                   game.getLocalisation(s + "_ADJ2", language))))
                       .map(Localisation::getValue);
    }

    public static Optional<String> getDynasty(Monarch monarch) {
        return Optional.ofNullable(monarch).map(Monarch::getDynasty);
    }

    public static String cleanLocalisation(String s) {
        if (s == null) {
            return null;
        }

        StringBuilder localisationBuilder = new StringBuilder(s);

        if (localisationBuilder.length() == 0) {
            return s;
        }

        int indexOf;
        while (localisationBuilder.toString().indexOf('§') >= 0) {
            for (int i = 0; i < localisationBuilder.length(); i++) {
                if (localisationBuilder.charAt(i) == '§') {
                    localisationBuilder.deleteCharAt(i);//Remove char
                    localisationBuilder.deleteCharAt(i);//Remove color code
                    indexOf = localisationBuilder.indexOf("§", i);
                    localisationBuilder.deleteCharAt(indexOf);//Remove closing char
                    localisationBuilder.deleteCharAt(indexOf);//Remove closing code
                    break;
                }
            }
        }

        if ((indexOf = localisationBuilder.toString().indexOf('$')) >= 0) {
            if (ClausewitzUtils.hasAtLeast(localisationBuilder.toString(), '$', 2)) {
                String[] splits = localisationBuilder.toString().split("\\$");
                localisationBuilder = new StringBuilder();
                for (int i = 0; i < splits.length; i += 2) {
                    localisationBuilder.append(splits[i]).append(" ");
                }
            } else {
                localisationBuilder = new StringBuilder(localisationBuilder.substring(0, indexOf));
            }
        }

        return localisationBuilder.toString().replace("\\r\\n", "")
                                  .replace("\\n", " ")
                                  .replaceAll("[^'.\\p{L}\\p{M}\\p{Alnum}\\p{Space}]", "")
                                  .trim();
    }
}
