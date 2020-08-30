package com.osallek.eu4parser.common;

import com.osallek.eu4parser.model.save.Power;
import com.osallek.eu4parser.model.save.country.Country;

import java.math.BigDecimal;

public class ConditionsUtils {

    private ConditionsUtils() {}

    public static boolean applyConditionToCountry(Country country, String condition, String value) {
        Country other;
        Integer integer;
        Double aDouble;

        switch (condition) {
            case "absolutism":
                return country.getAbsolutism() >= Integer.parseInt(value);
            case "accepted_culture":
                return country.getAcceptedCultures().contains(value);
            case "adm":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getMonarch() != null && country.getMonarch().getAdm() != null && country.getMonarch().getAdm() >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return other.getMonarch() == null
                           || other.getMonarch().getAdm() == null
                           || (country.getMonarch() != null
                               && country.getMonarch().getAdm() != null
                               && country.getMonarch().getAdm() >= other.getMonarch().getAdm());
                }
            case "adm_power":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getPowers() != null && country.getPowers().get(Power.ADM) != null && country.getPowers().get(Power.ADM) >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return other.getPowers() == null
                           || other.getPowers().get(Power.ADM) == null
                           || (country.getPowers() != null
                               && country.getPowers().get(Power.ADM) != null
                               && country.getPowers().get(Power.ADM) >= other.getPowers().get(Power.ADM));
                }
            case "adm_tech":
                return country.getTech().getAdm() >= Integer.parseInt(value);
            case "advisor":
                return country.getAdvisors().values().stream().anyMatch(advisor -> advisor.getType().equals(value));
            case "advisor_exists": //Todo global
                return country.getSave().getAdvisors().containsKey(Integer.parseInt(value));
            case "ai":
                return !country.isHuman();
            case "ai_attitude": //Todo Object
                break;
            case "alliance_with":
                return country.getAllies().contains(value);
            case "allows_female_emperor": //Todo global
                return country.getSave().getHre() != null && Boolean.TRUE.equals(country.getSave().getHre().getAllowsFemaleEmperor());
            case "always":
                return "yes".equals(value);
            case "area": //Todo province
                break;
            case "army_size":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getArmySize() >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getArmySize() >= other.getArmySize();
                }
            case "army_size_percentage": //Fixme do land limit
                break;
            case "army_strength": // Todo object
                break;
            case "army_professionalism":
                if ((aDouble = NumbersUtils.toDouble(value)) != null) {
                    return country.getArmyProfessionalism() != null && country.getArmyProfessionalism() >= aDouble;
                }
            case "army_tradition":
                if ((aDouble = NumbersUtils.toDouble(value)) != null) {
                    return country.getArmyTradition() != null && country.getArmyTradition() >= aDouble;
                } else {
                    other = country.getSave().getCountry(value);
                    return other.getArmyTradition() == null || (country.getArmyTradition() != null && country.getArmyTradition() >= other.getArmyTradition());
                }
            case "artillery_fraction":
                return new BigDecimal(value).multiply(BigDecimal.valueOf(country.getArmySize())).compareTo(BigDecimal.valueOf(country.getNbArtillery())) >= 0;
            case "artillery_in_province": //Todo province
                break;
            case "at_war_with_religious_enemy":
                return country.getCurrentAtWarWith().stream().anyMatch(c -> !c.getReligion().equals(country.getReligion()));
            case "authority":
                return country.getAuthority() != null && country.getAuthority() >= NumbersUtils.toDouble(value);
            case "average_autonomy":
                return country.getAverageAutonomy() >= NumbersUtils.toDouble(value);
            case "average_autonomy_above_min": //Fixme ????
                break;
            case "average_effective_unrest": //Fixme ????
                break;
        }

        return true;
    }
}
