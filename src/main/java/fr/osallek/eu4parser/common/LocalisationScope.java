package fr.osallek.eu4parser.common;

import fr.osallek.eu4parser.model.game.Culture;
import fr.osallek.eu4parser.model.save.SaveReligion;
import fr.osallek.eu4parser.model.save.country.Heir;
import fr.osallek.eu4parser.model.save.country.Monarch;
import fr.osallek.eu4parser.model.save.country.Queen;
import fr.osallek.eu4parser.model.save.country.SaveCountry;
import fr.osallek.eu4parser.model.save.province.SaveAdvisor;
import fr.osallek.eu4parser.model.save.province.SaveProvince;

import java.util.Optional;

public abstract class LocalisationScope {

    public Optional<SaveProvince> getCapital() {
        return Optional.empty();
    }

    public Optional<SaveCountry> getColonialParent() {
        return Optional.empty();
    }

    public Optional<Culture> getCulture() {
        return Optional.empty();
    }

    public Optional<Heir> getHeir() {
        return Optional.empty();
    }

    public Optional<Monarch> getMonarch() {
        return Optional.empty();
    }

    public Optional<Queen> getConsort() {
        return Optional.empty();
    }

    public Optional<SaveCountry> getOverlord() {
        return Optional.empty();
    }

    public Optional<SaveCountry> getOwner() {
        return Optional.empty();
    }

    public Optional<SaveReligion> getReligion() {
        return Optional.empty();
    }

    public Optional<SaveAdvisor> getAdmAdvisor() {
        return Optional.empty();
    }

    public Optional<SaveAdvisor> getDipAdvisor() {
        return Optional.empty();
    }

    public Optional<SaveAdvisor> getMilAdvisor() {
        return Optional.empty();
    }
}
