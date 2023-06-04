package fr.osallek.eu4parser.model.save;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.model.game.Culture;
import fr.osallek.eu4parser.model.save.country.Leader;
import fr.osallek.eu4parser.model.save.country.SaveCountry;
import fr.osallek.eu4parser.model.save.province.SaveProvince;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class RebelFaction {

    private final Save save;

    private final ClausewitzItem item;

    private Id id;

    private Id leader;

    private Id army;

    private Leader general;

    public RebelFaction(ClausewitzItem item, Save save) {
        this.save = save;
        this.item = item;
        refreshAttributes();
    }

    public Id getId() {
        return id;
    }

    public Optional<String> getType() {
        return this.item.getVarAsString("type");
    }

    public Optional<String> getName() {
        return this.item.getVarAsString("name");
    }

    public Optional<Double> getProgress() {
        return this.item.getVarAsDouble("progress");
    }

    public Optional<String> getHeretic() {
        return this.item.getVarAsString("heretic");
    }

    public Optional<SaveCountry> getCountry() {
        return this.item.getVarAsString("country").map(ClausewitzUtils::removeQuotes).map(this.save::getCountry);
    }

    public Optional<SaveCountry> getIndependence() {
        return this.item.getVarAsString("independence").map(ClausewitzUtils::removeQuotes).map(this.save::getCountry);
    }

    public Optional<SaveReligion> getReligion() {
        return this.item.getVarAsString("religion").map(ClausewitzUtils::removeQuotes).map(s -> this.save.getReligions().getReligion(s));
    }

    public Optional<Culture> getCulture() {
        return this.item.getVarAsString("culture").map(ClausewitzUtils::removeQuotes).map(s -> this.save.getGame().getCulture(s));
    }

    public Optional<String> getGovernment() {
        return this.item.getVarAsString("government");
    }

    public Optional<SaveProvince> getProvince() {
        return this.item.getVarAsInt("province").map(this.save::getProvince);
    }

    public Optional<Integer> getSeed() {
        return this.item.getVarAsInt("seed");
    }

    public Leader getGeneral() {
        return general;
    }

    public Optional<SaveCountry> getSupportiveCountry() {
        return this.item.getVarAsString("supportive_country").map(ClausewitzUtils::removeQuotes).map(this.save::getCountry);
    }

    public Optional<LocalDate> getSupportiveCountryDate() {
        return this.item.getVarAsDate("supportive_country_date");
    }

    public Id getLeader() {
        return leader;
    }

    public List<SaveProvince> getPossibleProvinces() {
        return this.item.getList("possible_provinces")
                        .map(ClausewitzList::getValuesAsInt)
                        .stream()
                        .flatMap(Collection::stream)
                        .map(this.save::getProvince)
                        .toList();
    }

    public List<SaveCountry> getFriends() {
        return this.item.getList("friend").map(ClausewitzList::getValues).stream().flatMap(Collection::stream).map(this.save::getCountry).toList();
    }

    public Optional<Boolean> isActive() {
        return this.item.getVarAsBool("active");
    }

    private void refreshAttributes() {
        this.id = this.item.getChild("id").map(Id::new).orElse(null);
        this.leader = this.item.getChild("leader").filter(i -> i.getVarAsInt("id").filter(integer -> integer != 0).isPresent()).map(Id::new).orElse(null);
        this.army = this.item.getChild("army").map(Id::new).orElse(null);
        this.general = this.item.getChild("general").map(i -> new Leader(i, null)).orElse(null);
    }
}
