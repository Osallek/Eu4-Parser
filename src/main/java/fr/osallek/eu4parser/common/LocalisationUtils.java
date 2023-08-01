package fr.osallek.eu4parser.common;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.eu4parser.model.Power;
import fr.osallek.eu4parser.model.game.Advisor;
import fr.osallek.eu4parser.model.game.Area;
import fr.osallek.eu4parser.model.game.Country;
import fr.osallek.eu4parser.model.game.CountryHistoryItemI;
import fr.osallek.eu4parser.model.game.Culture;
import fr.osallek.eu4parser.model.game.CustomizableLocalization;
import fr.osallek.eu4parser.model.game.CustomizableLocalizationText;
import fr.osallek.eu4parser.model.game.Game;
import fr.osallek.eu4parser.model.game.GovernmentName;
import fr.osallek.eu4parser.model.game.HreEmperor;
import fr.osallek.eu4parser.model.game.Province;
import fr.osallek.eu4parser.model.game.ProvinceHistoryItemI;
import fr.osallek.eu4parser.model.game.Region;
import fr.osallek.eu4parser.model.game.Religion;
import fr.osallek.eu4parser.model.game.ReligionGroup;
import fr.osallek.eu4parser.model.game.SuperRegion;
import fr.osallek.eu4parser.model.game.TradeCompany;
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
import fr.osallek.eu4parser.model.save.empire.Empire;
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
import java.util.function.Predicate;

public final class LocalisationUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalisationUtils.class);

    private LocalisationUtils() {}

    public static Optional<String> replaceScope(Save save, Object root, String scope, Eu4Language language) {
        if (save == null || root == null || StringUtils.isBlank(scope) || language == null) {
            return Optional.empty();
        }

        scope = StringUtils.stripEnd(StringUtils.stripStart(scope, "["), "]");

        Iterator<String> keys = List.of(StringUtils.split(scope, '.')).iterator();
        Optional<?> current = Optional.of(root);

        while (keys.hasNext()) {
            String key = keys.next();

            if (current.get() instanceof SaveCountry country) {
                switch (key) {
                    case "Country" -> current = Optional.of(country);
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
                    case "GetClergyName" -> current = getClergyEstate(country, language);
                    //Commands
                    case "GetAdjective" -> current = getAdjective(country, language);
                    case "GetDate" -> current = getDate(save, language);
                    case "GetFlagshipName" -> current = getFlagShip(country);
                    case "GetMonth" -> current = getMonth(save, language);
                    case "GetName" -> current = getName(country, language);
                    case "Emperor" -> current = getEmperor(save);
                    case "GetTag" -> current = getTag(country);
                    case "GetYear" -> current = getYear(save);
                    case "GovernmentName" -> current = getGovernmentName(country, language);
                    default -> {
                        CustomizableLocalization localization = save.getGame().getCustomizableLocalization(key);

                        if (localization != null) {
                            current = getCustomisableLocalisation(save.getGame(), current.orElse(null), localization, language);
                        } else {
                            current = getTag(save, key);

                            if (current.isEmpty()) {
                                current = NumbersUtils.parseInt(key).flatMap(id -> getProvince(save, id));
                            }
                        }

                        if (current.isEmpty()) {
                            LOGGER.warn("Could not find scope {} in country", key);
                        }
                    }
                }
            } else if (current.get() instanceof SaveProvince province) {
                switch (key) {
                    case "Country" -> current = getOwner(province);
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
                    case "Emperor" -> current = getEmperor(save);
                    case "GetName" -> current = getName(province);
                    case "GetTradeGoodsName" -> current = getTradeGoodName(province, language);
                    case "GetYear" -> current = getYear(save);
                    default -> {
                        CustomizableLocalization localization = save.getGame().getCustomizableLocalization(key);

                        if (localization != null) {
                            current = getCustomisableLocalisation(save.getGame(), current.orElse(null), localization, language);
                        } else {
                            current = getTag(save, key);

                            if (current.isEmpty()) {
                                current = NumbersUtils.parseInt(key).flatMap(id -> getProvince(save, id));
                            }

                            if (current.isEmpty() && MapUtils.isNotEmpty(province.getVariables()) && province.getVariables().containsKey(key)) {
                                current = Optional.of(String.valueOf(province.getVariables().get(key)));
                            }
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
                    case "Emperor" -> current = getEmperor(save);
                    case "GetName" -> current = getName(advisor);
                    case "GetYear" -> current = getYear(save);
                    case "GetWomanMan" -> current = getAdvisorType(advisor, language);
                    default -> {
                        CustomizableLocalization localization = save.getGame().getCustomizableLocalization(key);

                        if (localization != null) {
                            current = getCustomisableLocalisation(save.getGame(), current.orElse(null), localization, language);
                        } else {
                            current = getTag(save, key);

                            if (current.isEmpty()) {
                                current = NumbersUtils.parseInt(key).flatMap(id -> getProvince(save, id));
                            }
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
                    case "Emperor" -> current = getEmperor(save);
                    case "GetName" -> current = getName(monarch);
                    case "GetYear" -> current = getYear(save);
                    case "GetAdm" -> current = getAdm(monarch);
                    case "GetDip" -> current = getDip(monarch);
                    case "GetMil" -> current = getMil(monarch);
                    case "GetTitle" -> current = getTitle(monarch, language);
                    case "GetWomanMan" -> current = getTitle(monarch, language);
                    case "GetHerHis" -> current = getHerHis(monarch, language);
                    case "GetHerHisCap" -> current = getHerHis(monarch, language).map(StringUtils::capitalize);
                    case "GetHerselfHimself" -> current = getHerselfHimself(monarch, language);
                    case "GetHerselfHimselfCap" -> current = getHerselfHimself(monarch, language).map(StringUtils::capitalize);
                    case "GetSheHe" -> current = getSheHe(monarch, language);
                    case "GetSheHeCap" -> current = getSheHe(monarch, language).map(StringUtils::capitalize);
                    case "GetSisterBrother" -> current = getSisterBrother(monarch, language);
                    default -> {
                        CustomizableLocalization localization = save.getGame().getCustomizableLocalization(key);

                        if (localization != null) {
                            current = getCustomisableLocalisation(save.getGame(), current.orElse(null), localization, language);
                        } else {
                            current = getTag(save, key);

                            if (current.isEmpty()) {
                                current = NumbersUtils.parseInt(key).flatMap(id -> getProvince(save, id));
                            }
                        }

                        if (current.isEmpty()) {
                            LOGGER.warn("Could not find scope {} in monarch", key);
                        }
                    }
                }
            } else if (current.get() instanceof Religion religion) {
                switch (key) {
                    case "GetGroupName" -> current = getGroup(religion, language);
                    //Commands
                    case "GetDate" -> current = getDate(save, language);
                    case "GetMonth" -> current = getMonth(save, language);
                    case "Emperor" -> current = getEmperor(save);
                    case "GetName" -> current = getName(religion, language);
                    case "GetYear" -> current = getYear(save);
                    default -> {
                        CustomizableLocalization localization = save.getGame().getCustomizableLocalization(key);

                        if (localization != null) {
                            current = getCustomisableLocalisation(save.getGame(), current.orElse(null), localization, language);
                        } else {
                            current = getTag(save, key);

                            if (current.isEmpty()) {
                                current = NumbersUtils.parseInt(key).flatMap(id -> getProvince(save, id));
                            }
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
                    case "Emperor" -> current = getEmperor(save);
                    case "GetName" -> current = getName(culture, language);
                    case "GetYear" -> current = getYear(save);
                    case "GetGroupName" -> current = getGroup(culture, language);
                    default -> {
                        CustomizableLocalization localization = save.getGame().getCustomizableLocalization(key);

                        if (localization != null) {
                            current = getCustomisableLocalisation(save.getGame(), current.orElse(null), localization, language);
                        } else {
                            current = getTag(save, key);

                            if (current.isEmpty()) {
                                current = NumbersUtils.parseInt(key).flatMap(id -> getProvince(save, id));
                            }
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
                    case "Emperor" -> current = getEmperor(save);
                    case "GetName" -> current = getName(tradeCompany);
                    case "GetYear" -> current = getYear(save);
                    default -> {
                        CustomizableLocalization localization = save.getGame().getCustomizableLocalization(key);

                        if (localization != null) {
                            current = getCustomisableLocalisation(save.getGame(), current.orElse(null), localization, language);
                        } else {
                            current = getTag(save, key);

                            if (current.isEmpty()) {
                                current = NumbersUtils.parseInt(key).flatMap(id -> getProvince(save, id));
                            }
                        }

                        if (current.isEmpty()) {
                            LOGGER.warn("Could not find scope {} in culture", key);
                        }
                    }
                }
            }

            if (current.isEmpty()) {
                break;
            } else if (current.get() instanceof String s) {
                return Optional.ofNullable(ClausewitzUtils.removeQuotes(s));
            }
        }

        return Optional.empty();
    }

    public static Optional<String> replaceScope(Game game, Object root, String scope, Eu4Language language) {
        if (game == null || root == null || StringUtils.isBlank(scope) || language == null) {
            return Optional.empty();
        }

        scope = StringUtils.stripEnd(StringUtils.stripStart(scope, "["), "]");

        Iterator<String> keys = List.of(StringUtils.split(scope, '.')).iterator();
        Optional<?> current = Optional.of(root);

        while (keys.hasNext()) {
            String key = keys.next();

            if (current.get() instanceof Country country) {
                switch (key) {
                    case "Country" -> current = Optional.of(country);
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
                    case "GetClergyName" -> current = getClergyEstate(country, language);
                    //Commands
                    case "GetAdjective" -> current = getAdjective(country, language);
                    case "GetDate" -> current = getDate(game, language);
                    case "GetFlagshipName" -> current = getFlagShip(country);
                    case "GetMonth" -> current = getMonth(game, language);
                    case "GetName" -> current = getName(country, language);
                    case "Emperor" -> current = getEmperor(game);
                    case "GetTag" -> current = getTag(country);
                    case "GetYear" -> current = getYear(game);
                    case "GovernmentName" -> current = getGovernmentName(country, language);
                    default -> {
                        CustomizableLocalization localization = game.getCustomizableLocalization(key);

                        if (localization != null) {
                            current = getCustomisableLocalisation(game, current.orElse(null), localization, language);
                        } else {
                            current = getTag(game, key);

                            if (current.isEmpty()) {
                                current = NumbersUtils.parseInt(key).flatMap(id -> getProvince(game, id));
                            }
                        }

                        if (current.isEmpty()) {
                            LOGGER.warn("Could not find scope {} in country", key);
                        }
                    }
                }
            } else if (current.get() instanceof Province province) {
                switch (key) {
                    case "Country" -> current = getOwner(province);
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
                    case "GetDate" -> current = getDate(province.getGame(), language);
                    case "GetMonth" -> current = getMonth(province.getGame(), language);
                    case "Emperor" -> current = getEmperor(province.getGame());
                    case "GetName" -> current = getName(province);
                    case "GetTradeGoodsName" -> current = getTradeGoodName(province, language);
                    case "GetYear" -> current = getYear(province.getGame());
                    default -> {
                        CustomizableLocalization localization = game.getCustomizableLocalization(key);

                        if (localization != null) {
                            current = getCustomisableLocalisation(game, current.orElse(null), localization, language);
                        } else {
                            current = getTag(game, key);

                            if (current.isEmpty()) {
                                current = NumbersUtils.parseInt(key).flatMap(id -> getProvince(game, id));
                            }
                        }

                        if (current.isEmpty()) {
                            LOGGER.warn("Could not find scope {} in province", key);
                        }
                    }
                }
            } else if (current.get() instanceof fr.osallek.eu4parser.model.game.Monarch monarch) {
                switch (key) {
                    case "Dynasty" -> current = getDynasty(monarch);
                    case "GetTitle" -> current = getTitle(monarch, language);
                    case "GetWomanMan" -> current = getTitle(monarch, language);
                    case "GetAdm" -> current = getAdm(monarch);
                    case "GetDip" -> current = getDip(monarch);
                    case "GetMil" -> current = getMil(monarch);
                    case "GetName" -> current = getName(monarch);
                    case "GetHerHis" -> current = getHerHis(monarch, language);
                    case "GetHerHisCap" -> current = getHerHis(monarch, language).map(StringUtils::capitalize);
                    case "GetHerselfHimself" -> current = getHerselfHimself(monarch, language);
                    case "GetHerselfHimselfCap" -> current = getHerselfHimself(monarch, language).map(StringUtils::capitalize);
                    case "GetSheHe" -> current = getSheHe(monarch, language);
                    case "GetSheHeCap" -> current = getSheHe(monarch, language).map(StringUtils::capitalize);
                    case "GetSisterBrother" -> current = getSisterBrother(monarch, language);
                    //Commands
                    case "GetDate" -> current = getDate(game, language);
                    case "GetMonth" -> current = getMonth(game, language);
                    case "Emperor" -> current = getEmperor(game);
                    case "GetYear" -> current = getYear(game);
                    default -> {
                        CustomizableLocalization localization = game.getCustomizableLocalization(key);

                        if (localization != null) {
                            current = getCustomisableLocalisation(game, current.orElse(null), localization, language);
                        } else {
                            current = getTag(game, key);

                            if (current.isEmpty()) {
                                current = NumbersUtils.parseInt(key).flatMap(id -> getProvince(game, id));
                            }
                        }

                        if (current.isEmpty()) {
                            LOGGER.warn("Could not find scope {} in monarch", key);
                        }
                    }
                }
            } else if (current.get() instanceof Religion religion) {
                switch (key) {
                    case "GetGroupName" -> current = getGroup(religion, language);
                    //Commands
                    case "GetDate" -> current = getDate(game, language);
                    case "GetMonth" -> current = getMonth(game, language);
                    case "Emperor" -> current = getEmperor(game);
                    case "GetName" -> current = getName(religion, language);
                    case "GetYear" -> current = getYear(game);
                    default -> {
                        CustomizableLocalization localization = game.getCustomizableLocalization(key);

                        if (localization != null) {
                            current = getCustomisableLocalisation(game, current.orElse(null), localization, language);
                        } else {
                            current = getTag(game, key);

                            if (current.isEmpty()) {
                                current = NumbersUtils.parseInt(key).flatMap(id -> getProvince(game, id));
                            }
                        }

                        if (current.isEmpty()) {
                            LOGGER.warn("Could not find scope {} in religion", key);
                        }
                    }
                }
            } else if (current.get() instanceof Culture culture) {
                switch (key) {
                    //Commands
                    case "GetDate" -> current = getDate(game, language);
                    case "GetMonth" -> current = getMonth(game, language);
                    case "Emperor" -> current = getEmperor(game);
                    case "GetName" -> current = getName(culture, language);
                    case "GetYear" -> current = getYear(game);
                    case "GetGroupName" -> current = getGroup(culture, language);
                    default -> {
                        CustomizableLocalization localization = game.getCustomizableLocalization(key);

                        if (localization != null) {
                            current = getCustomisableLocalisation(game, current.orElse(null), localization, language);
                        } else {
                            current = getTag(game, key);

                            if (current.isEmpty()) {
                                current = NumbersUtils.parseInt(key).flatMap(id -> getProvince(game, id));
                            }
                        }

                        if (current.isEmpty()) {
                            LOGGER.warn("Could not find scope {} in culture", key);
                        }
                    }
                }
            } else if (current.get() instanceof TradeCompany tradeCompany) {
                switch (key) {
                    //Commands
                    case "GetDate" -> current = getDate(game, language);
                    case "GetMonth" -> current = getMonth(game, language);
                    case "Emperor" -> current = getEmperor(game);
                    case "GetName" -> current = getName(tradeCompany, language);
                    case "GetYear" -> current = getYear(game);
                    default -> {
                        CustomizableLocalization localization = game.getCustomizableLocalization(key);

                        if (localization != null) {
                            current = getCustomisableLocalisation(game, current.orElse(null), localization, language);
                        } else {
                            current = getTag(game, key);

                            if (current.isEmpty()) {
                                current = NumbersUtils.parseInt(key).flatMap(id -> getProvince(game, id));
                            }
                        }

                        if (current.isEmpty()) {
                            LOGGER.warn("Could not find scope {} in culture", key);
                        }
                    }
                }
            }

            if (current.isEmpty()) {
                break;
            } else if (current.get() instanceof String s) {
                return Optional.ofNullable(ClausewitzUtils.removeQuotes(s));
            }
        }

        return Optional.empty();
    }

    private static Optional<String> getHerHis(Monarch monarch, Eu4Language language) {
        return Optional.ofNullable(monarch)
                       .map(m -> BooleanUtils.toBoolean(m.getFemale()))
                       .map(bool -> switch (language) {
                           case FRENCH -> "son";
                           case SPANISH -> "su";
                           case ENGLISH -> bool ? "her" : "his";
                           case GERMAN -> bool ? "seine" : "ihr";
                       });
    }

    private static Optional<String> getHerHis(fr.osallek.eu4parser.model.game.Monarch monarch, Eu4Language language) {
        return Optional.ofNullable(monarch)
                       .map(m -> BooleanUtils.toBoolean(m.getFemale()))
                       .map(bool -> switch (language) {
                           case FRENCH -> "son";
                           case SPANISH -> "su";
                           case ENGLISH -> bool ? "her" : "his";
                           case GERMAN -> bool ? "seine" : "ihr";
                       });
    }

    private static Optional<String> getHerselfHimself(Monarch monarch, Eu4Language language) {
        return Optional.ofNullable(monarch)
                       .map(m -> BooleanUtils.toBoolean(m.getFemale()))
                       .map(bool -> switch (language) {
                           case FRENCH -> bool ? "elle-même" : "lui-même";
                           case SPANISH -> bool ? "ella misma" : "él mismo";
                           case ENGLISH -> bool ? "herself" : "himself";
                           case GERMAN -> bool ? "sie selbst" : "er selbst";
                       });
    }

    private static Optional<String> getHerselfHimself(fr.osallek.eu4parser.model.game.Monarch monarch, Eu4Language language) {
        return Optional.ofNullable(monarch)
                       .map(m -> BooleanUtils.toBoolean(m.getFemale()))
                       .map(bool -> switch (language) {
                           case FRENCH -> bool ? "elle-même" : "lui-même";
                           case SPANISH -> bool ? "ella misma" : "él mismo";
                           case ENGLISH -> bool ? "herself" : "himself";
                           case GERMAN -> bool ? "sie selbst" : "er selbst";
                       });
    }

    private static Optional<String> getSheHe(Monarch monarch, Eu4Language language) {
        return Optional.ofNullable(monarch)
                       .map(m -> BooleanUtils.toBoolean(m.getFemale()))
                       .map(bool -> switch (language) {
                           case FRENCH -> bool ? "elle" : "il";
                           case SPANISH -> bool ? "ella" : "él";
                           case ENGLISH -> bool ? "she" : "he";
                           case GERMAN -> bool ? "sie" : "er";
                       });
    }

    private static Optional<String> getSheHe(fr.osallek.eu4parser.model.game.Monarch monarch, Eu4Language language) {
        return Optional.ofNullable(monarch)
                       .map(m -> BooleanUtils.toBoolean(m.getFemale()))
                       .map(bool -> switch (language) {
                           case FRENCH -> bool ? "elle" : "il";
                           case SPANISH -> bool ? "ella" : "él";
                           case ENGLISH -> bool ? "she" : "he";
                           case GERMAN -> bool ? "sie" : "er";
                       });
    }

    private static Optional<String> getSisterBrother(Monarch monarch, Eu4Language language) {
        return Optional.ofNullable(monarch)
                       .map(m -> BooleanUtils.toBoolean(m.getFemale()))
                       .map(bool -> switch (language) {
                           case FRENCH -> bool ? "sœur" : "frère";
                           case SPANISH -> bool ? "hermana" : "hermano";
                           case ENGLISH -> bool ? "sister" : "brother";
                           case GERMAN -> bool ? "schwester" : "bruder";
                       });
    }

    private static Optional<String> getSisterBrother(fr.osallek.eu4parser.model.game.Monarch monarch, Eu4Language language) {
        return Optional.ofNullable(monarch)
                       .map(m -> BooleanUtils.toBoolean(m.getFemale()))
                       .map(bool -> switch (language) {
                           case FRENCH -> bool ? "sœur" : "frère";
                           case SPANISH -> bool ? "hermana" : "hermano";
                           case ENGLISH -> bool ? "sister" : "brother";
                           case GERMAN -> bool ? "schwester" : "bruder";
                       });
    }

    public static DateTimeFormatter getPrettyDateFormat(Eu4Language language) {
        return DateTimeFormatter.ofPattern(
                DateTimeFormatterBuilder.getLocalizedDateTimePattern(FormatStyle.SHORT, null, IsoChronology.INSTANCE, language.locale)
                                        .replace("yyyy", "yy")
                                        .replace("yy", "yyyy")
                                        .replace("uuuu", "uu")
                                        .replace("uu", "uuuu"));
    }

    public static Optional<String> getCustomisableLocalisation(Game game, Object root, CustomizableLocalization localization, Eu4Language language) {
        return Optional.ofNullable(localization)
                       .filter(l -> CollectionUtils.isNotEmpty(l.getTexts()))
                       .flatMap(l -> {
                           if (root instanceof Country country) {
                               return l.getTexts().stream().filter(t -> t.getTrigger() == null || t.getTrigger().apply(country, country)).findFirst();
                           } else if (root instanceof Province province) {
                               return l.getTexts().stream().filter(t -> t.getTrigger() == null || t.getTrigger().apply(province)).findFirst();
                           } else {
                               return Optional.empty();
                           }
                       })
                       .map(CustomizableLocalizationText::getLocalisationKey)
                       .map(key -> game.getComputedLocalisation(root, key, language));
    }

    public static Optional<SaveProvince> getCapital(SaveCountry country) {
        return Optional.ofNullable(country).map(SaveCountry::getCapital);
    }

    public static Optional<Province> getCapital(Country country) {
        return Optional.ofNullable(country).map(c -> c.getHistoryItemAt(c.getGame().getStartDate())).map(CountryHistoryItemI::getCapital);
    }

    public static Optional<SaveCountry> getColonialParent(SaveCountry country) {
        return Optional.ofNullable(country).map(SaveCountry::getColonialParent);
    }

    public static Optional<SaveCountry> getColonialParent(Country country) {
        return Optional.empty();
    }

    public static Optional<Culture> getCulture(SaveCountry country) {
        return Optional.ofNullable(country).map(SaveCountry::getPrimaryCulture);
    }

    public static Optional<Culture> getCulture(Country country) {
        return Optional.ofNullable(country).map(c -> c.getHistoryItemAt(c.getGame().getStartDate())).map(CountryHistoryItemI::getPrimaryCulture);
    }

    public static Optional<Culture> getCulture(SaveProvince province) {
        return Optional.ofNullable(province).map(SaveProvince::getCulture);
    }

    public static Optional<Culture> getCulture(Province province) {
        return Optional.ofNullable(province).map(p -> p.getHistoryItemAt(p.getGame().getStartDate())).map(ProvinceHistoryItemI::getCulture);
    }

    public static Optional<SaveCountry> getFrom(SaveCountry country) {
        return Optional.ofNullable(country);
    }

    public static Optional<Country> getFrom(Country country) {
        return Optional.ofNullable(country);
    }

    public static Optional<SaveProvince> getFrom(SaveProvince province) {
        return Optional.ofNullable(province);
    }

    public static Optional<Province> getFrom(Province province) {
        return Optional.ofNullable(province);
    }

    public static Optional<SaveCountry> getRoot(SaveCountry country) {
        return Optional.ofNullable(country);
    }

    public static Optional<Country> getRoot(Country country) {
        return Optional.ofNullable(country);
    }

    public static Optional<SaveProvince> getRoot(SaveProvince province) {
        return Optional.ofNullable(province);
    }

    public static Optional<Province> getRoot(Province province) {
        return Optional.ofNullable(province);
    }

    public static Optional<SaveCountry> getThis(SaveCountry country) {
        return Optional.ofNullable(country);
    }

    public static Optional<Country> getThis(Country country) {
        return Optional.ofNullable(country);
    }

    public static Optional<SaveProvince> getThis(SaveProvince province) {
        return Optional.ofNullable(province);
    }

    public static Optional<Province> getThis(Province province) {
        return Optional.ofNullable(province);
    }

    public static Optional<Heir> getHeir(SaveCountry country) {
        return Optional.ofNullable(country).map(SaveCountry::getHeir);
    }

    public static Optional<fr.osallek.eu4parser.model.game.Heir> getHeir(Country country) {
        return Optional.ofNullable(country).map(c -> c.getHistoryItemAt(c.getGame().getStartDate())).map(CountryHistoryItemI::getHeir);
    }

    public static Optional<Monarch> getMonarch(SaveCountry country) {
        return Optional.ofNullable(country).map(SaveCountry::getMonarch);
    }

    public static Optional<fr.osallek.eu4parser.model.game.Monarch> getMonarch(Country country) {
        return Optional.ofNullable(country).map(c -> c.getHistoryItemAt(c.getGame().getStartDate())).map(CountryHistoryItemI::getMonarch);
    }

    public static Optional<Queen> getConsort(SaveCountry country) {
        return Optional.ofNullable(country).map(SaveCountry::getQueen);
    }

    public static Optional<fr.osallek.eu4parser.model.game.Queen> getConsort(Country country) {
        return Optional.ofNullable(country).map(c -> c.getHistoryItemAt(c.getGame().getStartDate())).map(CountryHistoryItemI::getQueen);
    }

    public static Optional<SaveCountry> getOverlord(SaveCountry country) {
        return Optional.ofNullable(country).map(SaveCountry::getOverlord);
    }

    public static Optional<Country> getOverlord(Country country) {
        return Optional.ofNullable(country).map(c -> c.getOverlordAt(c.getGame().getStartDate()));
    }

    public static Optional<SaveCountry> getOwner(SaveProvince province) {
        return Optional.ofNullable(province).map(SaveProvince::getOwner);
    }

    public static Optional<Country> getOwner(Province province) {
        return Optional.ofNullable(province).map(p -> p.getHistoryItemAt(p.getGame().getStartDate())).map(ProvinceHistoryItemI::getOwner);
    }

    public static Optional<Religion> getReligion(SaveProvince province) {
        return Optional.ofNullable(province).map(SaveProvince::getReligion).map(SaveReligion::getGameReligion);
    }

    public static Optional<Religion> getReligion(Province province) {
        return Optional.ofNullable(province).map(p -> p.getHistoryItemAt(p.getGame().getStartDate())).map(ProvinceHistoryItemI::getReligion);
    }

    public static Optional<Religion> getReligion(SaveCountry country) {
        return Optional.ofNullable(country).map(SaveCountry::getReligion).map(SaveReligion::getGameReligion);
    }

    public static Optional<Religion> getReligion(Country country) {
        return Optional.ofNullable(country).map(c -> c.getHistoryItemAt(c.getGame().getStartDate())).map(CountryHistoryItemI::getReligion);
    }

    public static Optional<SaveTradeCompany> getTradeCompany(SaveProvince province) {
        return Optional.ofNullable(province).map(SaveProvince::getSaveTradeCompany);
    }

    public static Optional<TradeCompany> getTradeCompany(Province province) {
        return Optional.ofNullable(province).map(Province::getTradeCompany);
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

    public static Optional<SaveAdvisor> getAdmAdvisor(Country country) {
        return Optional.empty();
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

    public static Optional<SaveAdvisor> getDipAdvisor(Country country) {
        return Optional.empty();
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

    public static Optional<SaveAdvisor> getMilAdvisor(Country country) {
        return Optional.empty();
    }

    public static Optional<String> getClergyEstate(SaveCountry country, Eu4Language language) {
        return Optional.ofNullable(country)
                       .map(c -> c.getEstate("estate_church"))
                       .map(e -> e.getEstateGame().getName())
                       .map(s -> country.getSave().getGame().getLocalisation(s, language))
                       .map(Localisation::getValue);
    }

    public static Optional<String> getClergyEstate(Country country, Eu4Language language) {
        return Optional.ofNullable(country)
                       .map(c -> c.getGame().getLocalisation("estate_church", language))
                       .map(Localisation::getValue);
    }

    public static Optional<SaveCountry> getTag(Save save, String tag) {
        return Optional.ofNullable(save).map(s -> save.getCountry(tag));
    }

    public static Optional<Country> getTag(Game game, String tag) {
        return Optional.ofNullable(game).map(g -> g.getCountry(tag));
    }


    public static Optional<SaveProvince> getProvince(Save save, Integer id) {
        return Optional.ofNullable(save).map(s -> save.getProvince(id));
    }

    public static Optional<Province> getProvince(Game game, Integer id) {
        return Optional.ofNullable(game).map(g -> g.getProvince(id));
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

    public static Optional<String> getAreaName(Province province, Eu4Language language) {
        return Optional.ofNullable(province)
                       .map(Province::getGame)
                       .flatMap(game -> Optional.of(province)
                                                .map(Province::getArea)
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

    public static Optional<String> getRegionName(Province province, Eu4Language language) {
        return Optional.ofNullable(province)
                       .map(Province::getGame)
                       .flatMap(game -> Optional.of(province)
                                                .map(Province::getArea)
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

    public static Optional<String> getSuperRegionName(Province province, Eu4Language language) {
        return Optional.ofNullable(province)
                       .map(Province::getGame)
                       .flatMap(game -> Optional.of(province)
                                                .map(Province::getArea)
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

    public static Optional<String> getContinentName(Province province, Eu4Language language) {
        return Optional.ofNullable(province)
                       .map(Province::getGame)
                       .map(Game::getContinents)
                       .filter(CollectionUtils::isNotEmpty)
                       .flatMap(list -> list
                               .stream()
                               .filter(l -> l.getProvinces().contains(province.getId()))
                               .findFirst()
                               .map(l -> province.getGame().getLocalisation(l.getName(), language))
                               .map(Localisation::getValue));
    }

    public static Optional<String> getAdm(Monarch monarch) {
        return Optional.ofNullable(monarch).map(Monarch::getAdm).map(String::valueOf);
    }

    public static Optional<String> getAdm(fr.osallek.eu4parser.model.game.Monarch monarch) {
        return Optional.ofNullable(monarch).map(fr.osallek.eu4parser.model.game.Monarch::getAdm).map(String::valueOf);
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

    public static Optional<String> getCapitalName(Province province) {
        return Optional.ofNullable(province).map(p -> p.getHistoryItemAt(p.getGame().getStartDate())).map(ProvinceHistoryItemI::getCapital);
    }

    public static Optional<String> getDate(Save save, Eu4Language language) {
        return Optional.ofNullable(save).map(Save::getDate).map(date -> date.format(getPrettyDateFormat(language)));
    }

    public static Optional<String> getDate(Game game, Eu4Language language) {
        return Optional.ofNullable(game).map(Game::getStartDate).map(date -> date.format(getPrettyDateFormat(language)));
    }

    public static Optional<String> getDip(Monarch monarch) {
        return Optional.ofNullable(monarch).map(Monarch::getDip).map(String::valueOf);
    }

    public static Optional<String> getDip(fr.osallek.eu4parser.model.game.Monarch monarch) {
        return Optional.ofNullable(monarch).map(fr.osallek.eu4parser.model.game.Monarch::getDip).map(String::valueOf);
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

    public static Optional<String> getFlagShip(Country country) {
        return Optional.empty();
    }

    public static Optional<String> getGroup(Culture culture, Eu4Language language) {
        return Optional.ofNullable(culture)
                       .map(Culture::getCultureGroup)
                       .map(g -> culture.getGame().getLocalisation(g.getName(), language))
                       .map(Localisation::getValue);
    }

    public static Optional<String> getGroup(Religion religion, Eu4Language language) {
        return Optional.ofNullable(religion)
                       .map(Religion::getReligionGroup)
                       .map(g -> g.getGame().getLocalisation(g.getName(), language))
                       .map(Localisation::getValue);
    }

    public static Optional<String> getMil(Monarch monarch) {
        return Optional.ofNullable(monarch.getMil()).map(String::valueOf);
    }

    public static Optional<String> getMil(fr.osallek.eu4parser.model.game.Monarch monarch) {
        return Optional.ofNullable(monarch).map(fr.osallek.eu4parser.model.game.Monarch::getMil).map(String::valueOf);
    }

    public static Optional<String> getMonth(Save save, Eu4Language language) {
        return Optional.ofNullable(save)
                       .map(Save::getDate)
                       .map(LocalDate::getMonth)
                       .map(month -> month.getDisplayName(TextStyle.FULL, language.locale))
                       .map(StringUtils::capitalize);
    }

    public static Optional<String> getMonth(Game game, Eu4Language language) {
        return Optional.ofNullable(game)
                       .map(Game::getStartDate)
                       .map(LocalDate::getMonth)
                       .map(month -> month.getDisplayName(TextStyle.FULL, language.locale))
                       .map(StringUtils::capitalize);
    }

    public static Optional<SaveCountry> getEmperor(Save save) {
        return Optional.ofNullable(save).map(Save::getHre).filter(Predicate.not(Empire::dismantled)).map(Empire::getEmperor);
    }

    public static Optional<Country> getEmperor(Game game) {
        return Optional.ofNullable(game).map(g -> g.getHreEmperorAt(g.getStartDate())).map(HreEmperor::getTag).map(game::getCountry);
    }

    public static Optional<String> getName(SaveProvince province) {
        return Optional.ofNullable(province).map(SaveProvince::getName);
    }

    public static Optional<String> getName(Province province) {
        return Optional.ofNullable(province).map(Province::getName);
    }

    public static Optional<String> getName(SaveCountry country, Eu4Language language) {
        return Optional.ofNullable(ObjectUtils.firstNonNull(country.getCustomName(),
                                                            country.getName(),
                                                            country.getLocalizedName(),
                                                            Optional.ofNullable(country.getSave().getGame().getLocalisation(country.getTag(), language))
                                                                    .map(Localisation::getValue)
                                                                    .orElse(null)));
    }

    public static Optional<String> getName(Country country, Eu4Language language) {
        return Optional.ofNullable(ObjectUtils.firstNonNull(Optional.ofNullable(country.getGame().getLocalisation(country.getTag(), language))
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

    public static Optional<String> getName(Religion religion, Eu4Language language) {
        return Optional.ofNullable(religion)
                       .map(Religion::getReligionGroup)
                       .map(ReligionGroup::getGame)
                       .map(game -> game.getLocalisation(religion.getName(), language))
                       .map(Localisation::getValue)
                       .map(StringUtils::capitalize);
    }

    public static Optional<String> getName(Monarch monarch) {
        return Optional.ofNullable(monarch).map(Monarch::getMonarchName).or(() -> Optional.ofNullable(monarch).map(Monarch::getName));
    }

    public static Optional<String> getName(fr.osallek.eu4parser.model.game.Monarch monarch) {
        return Optional.ofNullable(monarch)
                       .map(fr.osallek.eu4parser.model.game.Monarch::getMonarchName)
                       .or(() -> Optional.ofNullable(monarch).map(fr.osallek.eu4parser.model.game.Monarch::getName));
    }

    public static Optional<String> getName(Heir heir) {
        return Optional.ofNullable(heir).map(Heir::getName);
    }

    public static Optional<String> getName(fr.osallek.eu4parser.model.game.Heir heir) {
        return Optional.ofNullable(heir).map(fr.osallek.eu4parser.model.game.Heir::getName);
    }

    public static Optional<String> getName(Queen queen) {
        return Optional.ofNullable(queen).map(Queen::getName);
    }

    public static Optional<String> getName(fr.osallek.eu4parser.model.game.Queen queen) {
        return Optional.ofNullable(queen).map(fr.osallek.eu4parser.model.game.Queen::getName);
    }

    public static Optional<String> getName(SaveAdvisor advisor) {
        return Optional.ofNullable(advisor).map(SaveAdvisor::getName);
    }

    public static Optional<String> getName(SaveTradeCompany tradeCompany) {
        return Optional.ofNullable(tradeCompany).map(SaveTradeCompany::getName);
    }

    public static Optional<String> getName(TradeCompany tradeCompany, Eu4Language language) {
        return Optional.ofNullable(tradeCompany)
                       .map(TradeCompany::getGame)
                       .map(game -> game.getLocalisation(tradeCompany.getName(), language))
                       .map(Localisation::getValue)
                       .map(StringUtils::capitalize);
    }

    public static Optional<String> getTag(SaveCountry country) {
        return Optional.ofNullable(country).map(SaveCountry::getTag);
    }

    public static Optional<String> getTag(Country country) {
        return Optional.ofNullable(country).map(Country::getTag);
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

    public static Optional<String> getTitle(fr.osallek.eu4parser.model.game.Monarch monarch, Eu4Language language) {
        return Optional.ofNullable(monarch)
                       .flatMap(m -> Optional.ofNullable(m.getCountry())
                                             .flatMap(c -> Optional.ofNullable(c.getGame().getGovernmentNames())
                                                                   .filter(CollectionUtils::isNotEmpty)
                                                                   .flatMap(names -> names.stream().filter(name -> name.getTrigger().apply(c, c)).findFirst())
                                                                   .map(name -> {
                                                                       if (fr.osallek.eu4parser.model.game.Monarch.class.equals(monarch.getClass())) {
                                                                           return BooleanUtils.toBoolean(monarch.getFemale()) ? name.getRulersFemale()
                                                                                                                              : name.getRulersMale();
                                                                       } else if (fr.osallek.eu4parser.model.game.Queen.class.equals(monarch.getClass())) {
                                                                           return BooleanUtils.toBoolean(monarch.getFemale()) ? name.getConsortsFemale()
                                                                                                                              : name.getConsortsMale();
                                                                       } else if (fr.osallek.eu4parser.model.game.Heir.class.equals(monarch.getClass())) {
                                                                           return BooleanUtils.toBoolean(monarch.getFemale()) ? name.getHeirsFemale()
                                                                                                                              : name.getHeirsMale();
                                                                       } else {
                                                                           return null;
                                                                       }
                                                                   })
                                                                   .map(ranks -> ranks.get(
                                                                           c.getHistoryItemAt(c.getGame().getStartDate()).getGovernmentLevel())))
                                             .flatMap(s -> Optional.of(monarch)
                                                                   .map(fr.osallek.eu4parser.model.game.Monarch::getCountry)
                                                                   .map(Country::getGame)
                                                                   .map(game -> game.getLocalisation(s, language)))
                                             .map(Localisation::getValue)
                                             .map(StringUtils::capitalize));
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

    public static Optional<String> getTradeGoodName(Province province, Eu4Language language) {
        return Optional.ofNullable(province)
                       .map(p -> p.getHistoryItemAt(p.getGame().getStartDate()))
                       .map(ProvinceHistoryItemI::getTradeGoods)
                       .flatMap(good -> Optional.of(province).map(Province::getGame).map(game -> game.getLocalisation(good.getName(), language)))
                       .map(Localisation::getValue);
    }

    public static Optional<String> getYear(Save save) {
        return Optional.ofNullable(save).map(Save::getDate).map(LocalDate::getYear).map(String::valueOf);
    }

    public static Optional<String> getYear(Game game) {
        return Optional.ofNullable(game).map(Game::getStartDate).map(LocalDate::getYear).map(String::valueOf);
    }

    public static Optional<String> getGovernmentName(SaveCountry country, Eu4Language language) {
        return Optional.ofNullable(country)
                       .map(SaveCountry::getGovernmentName)
                       .map(GovernmentName::getRanks)
                       .map(map -> map.get(country.getGovernmentLevel()))
                       .flatMap(s -> Optional.of(country).map(SaveCountry::getSave).map(Save::getGame).map(game -> game.getLocalisation(s, language)))
                       .map(Localisation::getValue);
    }

    public static Optional<String> getGovernmentName(Country country, Eu4Language language) {
        return Optional.ofNullable(country)
                       .map(c -> c.getGame().getGovernmentNames())
                       .filter(CollectionUtils::isNotEmpty)
                       .flatMap(names -> names.stream().filter(name -> name.getTrigger().apply(country, country)).findFirst())
                       .map(GovernmentName::getRanks)
                       .map(map -> map.get(country.getHistoryItemAt(country.getGame().getStartDate()).getGovernmentLevel()))
                       .flatMap(s -> Optional.of(country).map(Country::getGame).map(game -> game.getLocalisation(s, language)))
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

    public static Optional<String> getAdjective(Country country, Eu4Language language) {
        return Optional.ofNullable(country)
                       .map(Country::getTag)
                       .flatMap(s -> Optional.of(country)
                                             .map(Country::getGame)
                                             .map(game -> ObjectUtils.firstNonNull(game.getLocalisation(s + "_ADJ", language),
                                                                                   game.getLocalisation(s + "_ADJ2", language))))
                       .map(Localisation::getValue);
    }

    public static Optional<String> getDynasty(Monarch monarch) {
        return Optional.ofNullable(monarch).map(Monarch::getDynasty);
    }

    public static Optional<String> getDynasty(fr.osallek.eu4parser.model.game.Monarch monarch) {
        return Optional.ofNullable(monarch).map(fr.osallek.eu4parser.model.game.Monarch::getDynasty);
    }

    public static String cleanLocalisation(String s) {
        if (StringUtils.isEmpty(s)) {
            return s;
        }

        StringBuilder localisationBuilder = new StringBuilder(s);

        int indexOf;
        while (localisationBuilder.toString().indexOf('§') >= 0) {
            for (int i = 0; i < localisationBuilder.length(); i++) {
                if (localisationBuilder.charAt(i) == '§') {
                    localisationBuilder.deleteCharAt(i);//Remove char

                    if (localisationBuilder.length() > i) {
                        localisationBuilder.deleteCharAt(i);//Remove color code
                        indexOf = localisationBuilder.indexOf("§", i);

                        if (indexOf >= 0) {
                            localisationBuilder.deleteCharAt(indexOf);//Remove closing char
                            localisationBuilder.deleteCharAt(indexOf);//Remove closing code
                        }
                    }
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

        return StringUtils.normalizeSpace(localisationBuilder.toString()
                                                             .replace("\\r\\n", "")
                                                             .replace("\\n", " ")
                                                             .replaceAll("[^«»\\p{Punct}\\p{L}\\p{M}\\p{Alnum}\\s ]", ""));
    }
}
