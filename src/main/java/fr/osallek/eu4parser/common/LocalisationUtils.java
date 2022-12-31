package fr.osallek.eu4parser.common;

import fr.osallek.eu4parser.model.game.Culture;
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
import fr.osallek.eu4parser.model.save.country.Ship;
import fr.osallek.eu4parser.model.save.province.SaveAdvisor;
import fr.osallek.eu4parser.model.save.province.SaveProvince;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class LocalisationUtils {

    private LocalisationUtils() {}

    public static String replaceScope(SaveCountry root, String scope) {

    }

    public static DateTimeFormatter getPrettyDateFormat(Eu4Language language) {
        return DateTimeFormatter.ofPattern(
                DateTimeFormatterBuilder.getLocalizedDateTimePattern(FormatStyle.SHORT, null, IsoChronology.INSTANCE, language.locale)
                                        .replace("yyyy", "yy")
                                        .replace("yy", "yyyy")
                                        .replace("uuuu", "uu")
                                        .replace("uu", "uuuu"));
    }

    //Todo GetAdjective from customizable localisations

    //Todo special GetDate, GetMonth

    public static Optional<String> getAreaName(SaveProvince province, Eu4Language language) {
        return Optional.ofNullable(province.getSave().getGame().getLocalisation(province.getArea().getName(), language)).map(Localisation::getValue);
    }

    public static Optional<String> getRegionName(SaveProvince province, Eu4Language language) {
        return Optional.ofNullable(province.getSave().getGame().getLocalisation(province.getArea().getRegion().getName(), language))
                       .map(Localisation::getValue);
    }

    public static Optional<String> getSuperRegionName(SaveProvince province, Eu4Language language) {
        return Optional.ofNullable(province.getSave().getGame().getLocalisation(province.getArea().getRegion().getSuperRegion().getName(), language))
                       .map(Localisation::getValue);
    }

    public static Optional<String> getContinentName(SaveProvince province, Eu4Language language) {
        return province.getSave()
                       .getGame()
                       .getContinents()
                       .stream()
                       .filter(l -> l.getProvinces().contains(province.getId()))
                       .findFirst()
                       .map(l -> province.getSave().getGame().getLocalisation(l.getName(), language))
                       .map(Localisation::getValue);
    }

    public static Optional<String> getAdm(Monarch monarch) {
        return Optional.ofNullable(monarch.getAdm()).map(String::valueOf);
    }

    public static Optional<String> getCapitalName(SaveProvince province) {
        return Optional.ofNullable(province.getCapital());
    }

    public static Optional<String> getDate(Save save, Eu4Language language) {
        return Optional.ofNullable(save.getDate()).map(date -> date.format(getPrettyDateFormat(language)));
    }

    public static Optional<String> getDip(Monarch monarch) {
        return Optional.ofNullable(monarch.getDip()).map(String::valueOf);
    }

    public static Optional<String> getFlagShip(SaveCountry country) {
        return Optional.ofNullable(country.getNavies())
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
        return Optional.ofNullable(save.getDate())
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
        return Optional.ofNullable(culture.getGame().getLocalisation(culture.getName(), language)).map(Localisation::getValue).map(StringUtils::capitalize);
    }

    public static Optional<String> getName(SaveReligion religion, Eu4Language language) {
        return Optional.ofNullable(religion.getSave().getGame().getLocalisation(religion.getName(), language))
                       .map(Localisation::getValue)
                       .map(StringUtils::capitalize);
    }

    public Optional<String> getName(Monarch monarch) {
        return Optional.ofNullable(monarch).map(Monarch::getMonarchName);
    }

    public Optional<String> getName(Heir heir) {
        return Optional.ofNullable(heir).map(Heir::getName);
    }

    public Optional<String> getName(Queen queen) {
        return Optional.ofNullable(queen).map(Queen::getName);
    }

    public Optional<String> getName(SaveAdvisor advisor) {
        return Optional.ofNullable(advisor).map(SaveAdvisor::getName);
    }
}
