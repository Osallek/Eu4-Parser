package fr.osallek.eu4parser.model.save;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.model.game.Culture;
import fr.osallek.eu4parser.model.save.country.Leader;
import fr.osallek.eu4parser.model.save.country.SaveCountry;
import fr.osallek.eu4parser.model.save.province.SaveProvince;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RebelFaction {

    private final Save save;

    private final ClausewitzItem item;

    public RebelFaction(ClausewitzItem item, Save save) {
        this.save = save;
        this.item = item;
    }

    public Id getId() {
        ClausewitzItem child = this.item.getChild("id");
        return child == null ? null : new Id(child);
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

    public SaveCountry getCountry() {
        return this.save.getCountry(ClausewitzUtils.removeQuotes(this.item.getVarAsString("country")));
    }

    public SaveCountry getIndependence() {
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
        ClausewitzItem generalItem = this.item.getChild("general");

        if (generalItem != null) {
            return new Leader(generalItem, null);
        }

        return null;
    }

    public SaveCountry getSupportiveCountry() {
        return this.save.getCountry(ClausewitzUtils.removeQuotes(this.item.getVarAsString("supportive_country")));
    }

    public LocalDate getSupportiveCountryDate() {
        return this.item.getVarAsDate("supportive_country_date");
    }

    public Id getLeader() {
        ClausewitzItem leaderItem = this.item.getChild("leader");

        if (leaderItem != null && leaderItem.getVarAsInt("id") != 0) {
            return new Id(leaderItem);
        }

        return null;
    }

    public List<SaveProvince> getPossibleProvinces() {
        ClausewitzList list = this.item.getList("possible_provinces");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValuesAsInt().stream().map(this.save::getProvince).toList();
    }

    public List<SaveCountry> getFriends() {
        ClausewitzList list = this.item.getList("friend");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues().stream().map(this.save::getCountry).toList();
    }

    public Id getArmy() {
        ClausewitzItem armyItem = this.item.getChild("army");

        if (armyItem != null) {
            ClausewitzItem leaderItem = this.item.getChild("leader");
            return leaderItem == null ? null : new Id(leaderItem);
        }

        return null;
    }

    public Boolean isActive() {
        return this.item.getVarAsBool("active");
    }
}
