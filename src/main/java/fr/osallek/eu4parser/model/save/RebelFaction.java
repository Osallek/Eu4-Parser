package fr.osallek.eu4parser.model.save;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.model.game.Culture;
import fr.osallek.eu4parser.model.save.country.Country;
import fr.osallek.eu4parser.model.save.country.Leader;
import fr.osallek.eu4parser.model.save.province.SaveProvince;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public String getType() {
        return this.item.getVarAsString("type");
    }

    public String getName() {
        return this.item.getVarAsString("name");
    }

    public Double getProgress() {
        return this.item.getVarAsDouble("progress");
    }

    public String getHeretic() {
        return this.item.getVarAsString("heretic");
    }

    public Country getCountry() {
        return this.save.getCountry(ClausewitzUtils.removeQuotes(this.item.getVarAsString("country")));
    }

    public Country getIndependence() {
        return this.save.getCountry(ClausewitzUtils.removeQuotes(this.item.getVarAsString("independence")));
    }

    public SaveReligion getReligion() {
        return this.save.getReligions().getReligion(ClausewitzUtils.removeQuotes(this.item.getVarAsString("religion")));
    }

    public Culture getCulture() {
        return this.save.getGame().getCulture(ClausewitzUtils.removeQuotes(this.item.getVarAsString("culture")));
    }

    public String getGovernment() {
        return this.item.getVarAsString("government");
    }

    public SaveProvince getProvince() {
        return this.save.getProvince(this.item.getVarAsInt("province"));
    }

    public Integer getSeed() {
        return this.item.getVarAsInt("seed");
    }

    public Leader getGeneral() {
        return general;
    }

    public Country getSupportiveCountry() {
        return this.save.getCountry(ClausewitzUtils.removeQuotes(this.item.getVarAsString("supportive_country")));
    }

    public LocalDate getSupportiveCountryDate() {
        return this.item.getVarAsDate("supportive_country_date");
    }

    public Id getLeader() {
        return leader;
    }

    public List<SaveProvince> getPossibleProvinces() {
        ClausewitzList list = this.item.getList("possible_provinces");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValuesAsInt().stream().map(this.save::getProvince).collect(Collectors.toList());
    }

    public List<Country> getFriends() {
        ClausewitzList list = this.item.getList("friend");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues().stream().map(this.save::getCountry).collect(Collectors.toList());
    }

    public Boolean isActive() {
        return this.item.getVarAsBool("active");
    }

    private void refreshAttributes() {
        ClausewitzItem idItem = this.item.getChild("id");

        if (idItem != null) {
            this.id = new Id(idItem);
        }

        ClausewitzItem leaderItem = this.item.getChild("leader");

        if (leaderItem != null && leaderItem.getVarAsInt("id") != 0) {
            this.leader = new Id(leaderItem);
        }

        ClausewitzItem armyItem = this.item.getChild("army");

        if (armyItem != null) {
            this.army = new Id(leaderItem);
        }

        ClausewitzItem generalItem = this.item.getChild("general");

        if (generalItem != null) {
            this.general = new Leader(generalItem, null);
        }
    }
}
