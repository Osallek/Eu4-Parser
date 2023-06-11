package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.model.Color;
import fr.osallek.eu4parser.model.Power;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SaveCountryHistoryEvent {

    protected final ClausewitzItem item;

    protected final SaveCountry country;

    public SaveCountryHistoryEvent(ClausewitzItem item, SaveCountry country) {
        this.item = item;
        this.country = country;
    }

    public LocalDate getDate() {
        return Eu4Utils.stringToDate(this.item.getName());
    }

    public Optional<Boolean> getAbolishedSerfdom() {
        return this.item.getLastVarAsBool("abolished_serfdom");
    }

    public Optional<Leader> getLeader() {
        return this.item.getLastChild("leader").map(i -> new Leader(i, this.country));
    }

    public Map<String, Boolean> getIdeasLevel() {
        return this.country.getSave()
                           .getGame()
                           .getIdeas()
                           .stream()
                           .filter(this.item::hasVar)
                           .collect(Collectors.toMap(Function.identity(), s -> this.item.getVarAsBool(s).get()));
    }

    public List<String> getAddAcceptedCultures() {
        return this.item.getVarsAsStrings("add_accepted_culture").stream().map(ClausewitzUtils::removeQuotes).toList();
    }

    public List<String> getRemoveAcceptedCultures() {
        return this.item.getVarsAsStrings("remove_accepted_culture").stream().map(ClausewitzUtils::removeQuotes).toList();
    }

    public Optional<Integer> getGovernmentRank() {
        return this.item.getLastVarAsInt("government_rank");
    }

    public Optional<Integer> getCapital() {
        return this.item.getLastVarAsInt("capital");
    }

    public Optional<String> getChangedCountryNameFrom() {
        return this.item.getLastVarAsString("changed_country_name_from").map(ClausewitzUtils::removeQuotes);
    }

    public Optional<String> getChangedCountryAdjectiveFrom() {
        return this.item.getLastVarAsString("changed_country_adjective_from").map(ClausewitzUtils::removeQuotes);
    }

    public Optional<Color> getChangedCountryMapcolorFrom() {
        return this.item.getList("changed_country_mapcolor_from").map(Color::new);
    }

    public Optional<String> getAddGovernmentReform() {
        return this.item.getLastVarAsString("add_government_reform");
    }

    public Optional<String> getPrimaryCulture() {
        return this.item.getLastVarAsString("primary_culture");
    }

    public Optional<String> getGovernment() {
        return this.item.getLastVarAsString("government");
    }

    public Optional<String> getReligion() {
        return this.item.getLastVarAsString("religion");
    }

    public Optional<String> getSecondaryReligion() {
        return this.item.getLastVarAsString("secondary_religion");
    }

    public Optional<String> getTechnologyGroup() {
        return this.item.getLastVarAsString("technology_group");
    }

    public Optional<String> getUnitType() {
        return this.item.getLastVarAsString("unit_type");
    }

    public Optional<String> getChangedTagFrom() {
        return this.item.getLastVarAsString("changed_tag_from").map(ClausewitzUtils::removeQuotes);
    }

    public Optional<String> getReligiousSchool() {
        return this.item.getLastVarAsString("religious_school").map(ClausewitzUtils::removeQuotes);
    }

    public Optional<String> getSetCountryFlag() {
        return this.item.getLastVarAsString("set_country_flag").map(ClausewitzUtils::removeQuotes);
    }

    public Optional<String> getDecision() {
        return this.item.getLastVarAsString("decision").map(ClausewitzUtils::removeQuotes);
    }

    public Optional<Queen> getQueen() {
        return this.item.getLastChild("queen").map(i -> new Queen(i, this.country));
    }

    public Optional<Queen> getMonarchConsort() {
        return this.item.getLastChild("monarch_consort").map(i -> new Queen(i, this.country));
    }

    public Optional<Monarch> getMonarch() {
        return this.item.getLastChild("monarch").map(i -> new Monarch(i, this.country));
    }

    public Optional<Heir> getMonarchHeir() {
        return this.item.getLastChild("monarch_heir").map(i -> new Heir(i, this.country));
    }

    public Optional<Heir> getHeir() {
        return this.item.getLastChild("heir").map(i -> new Heir(i, this.country));
    }

    public Optional<Heir> getMonarchForeignHeir() {
        return this.item.getLastChild("monarch_foreign_heir").map(i -> new Heir(i, this.country));
    }

    public Optional<Integer> getUnion() {
        return this.item.getLastVarAsInt("union");
    }

    public Optional<Integer> getTradePort() {
        return this.item.getLastVarAsInt("trade_port");
    }

    public Optional<Boolean> getElector() {
        return this.item.getLastVarAsBool("elector");
    }

    public Optional<Power> getNationalFocus() {
        return this.item.getLastVarAsString("national_focus").map(Power::byName);
    }

    public Optional<String> getSetEstatePrivilege() {
        return this.item.getLastVarAsString("set_estate_privilege").map(ClausewitzUtils::removeQuotes);
    }

    public SaveCountry getCountry() {
        return country;
    }
}
