package fr.osallek.eu4parser.model.save.revolution;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.model.save.Save;
import fr.osallek.eu4parser.model.save.country.SaveCountry;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public record Revolution(ClausewitzItem item, Save save) {

    public Optional<SaveCountry> getRevolutionTarget() {
        return this.item.getVarAsString("revolution_target").map(this.save::getCountry);
    }

    public void setRevolutionTarget(SaveCountry country) {
        this.item.setVariable("revolution_target", country.getTag());
        setHasFirstRevolutionStarted(true);
        setDismantleDate(null);
    }

    public Optional<String> getRevolutionTargetOriginalName() {
        return this.item.getVarAsString("revolution_target_original_name");
    }

    public void setRevolutionTargetOriginalName(String revolutionTargetOriginalName) {
        this.item.setVariable("revolution_target_original_name", revolutionTargetOriginalName);
    }

    public Optional<Boolean> hasFirstRevolutionStarted() {
        return this.item.getVarAsBool("has_first_revolution_started");
    }

    void setHasFirstRevolutionStarted(boolean hasFirstRevolutionStarted) {
        this.item.setVariable("has_first_revolution_started", hasFirstRevolutionStarted);
    }

    public Optional<LocalDate> getDismantleDate() {
        return this.item.getVarAsDate("dismantle_date").filter(Predicate.not(Eu4Utils.DEFAULT_DATE::equals));
    }

    public void setDismantleDate(LocalDate dismantleDate) {
        if (dismantleDate == null) {
            dismantleDate = Eu4Utils.DEFAULT_DATE;
        }

        this.item.setVariable("dismantle_date", dismantleDate);
    }

    public Optional<LocalDate> getClaimed() {
        return this.item.getVarAsDate("claimed")
                        .filter(localDate -> this.save.getStartDate().isPresent())
                        .filter(date -> !this.save.getStartDate().get().equals(date));
    }

    public void setClaimed(LocalDate claimed) {
        if (claimed == null) {
            claimed = this.save.getStartDate().get();
        }

        this.item.setVariable("claimed", claimed);
    }

    public List<SaveCountry> getPastTargets() {
        return this.item.getVarsAsStrings("past_targets").stream().map(this.save::getCountry).toList();
    }

    public void addPastTarget(SaveCountry country) {
        this.item.getList("past_targets").ifPresentOrElse(list -> list.add(country.getTag()), () -> this.item.addList("past_targets", country.getTag()));
    }

    public void removePastTarget(SaveCountry country) {
        this.item.getList("past_targets").ifPresent(list -> list.remove(country.getTag()));
    }
}
