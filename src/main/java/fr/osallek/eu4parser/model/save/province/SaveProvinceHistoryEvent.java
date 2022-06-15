package fr.osallek.eu4parser.model.save.province;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.common.NumbersUtils;
import fr.osallek.eu4parser.model.game.Building;
import fr.osallek.eu4parser.model.game.ProvinceRevolt;
import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDate;
import java.util.List;

public class SaveProvinceHistoryEvent {

    private final ClausewitzItem item;

    private final SaveProvince province;

    public SaveProvinceHistoryEvent(ClausewitzItem item, SaveProvince province) {
        this.item = item;
        this.province = province;
    }

    public LocalDate getDate() {
        return Eu4Utils.stringToDate(this.item.getName());
    }

    public String getCapital() {
        return ClausewitzUtils.removeQuotes(this.item.getVarAsString("capital"));
    }

    public Double getColonySize() {
        return this.item.getVarAsDouble("colonysize");
    }

    public Double getUnrest() {
        return this.item.getVarAsDouble("unrest");
    }

    public List<String> getAddCores() {
        return this.item.getVarsAsStrings("add_core").stream().map(ClausewitzUtils::removeQuotes).toList();
    }

    public List<String> getAddClaims() {
        return this.item.getVarsAsStrings("add_claim").stream().map(ClausewitzUtils::removeQuotes).toList();
    }

    public List<String> getRemoveCores() {
        return this.item.getVarsAsStrings("remove_core").stream().map(ClausewitzUtils::removeQuotes).toList();
    }

    public List<String> getRemoveClaims() {
        return this.item.getVarsAsStrings("remove_claim").stream().map(ClausewitzUtils::removeQuotes).toList();
    }

    public Boolean getHre() {
        return this.item.getVarAsBool("hre");
    }

    public Integer getBaseTax() {
        return NumbersUtils.doubleToInt(this.item.getVarAsDouble("base_tax"));
    }

    public Integer getBaseProduction() {
        return NumbersUtils.doubleToInt(this.item.getVarAsDouble("base_production"));
    }

    public Integer getBaseManpower() {
        return NumbersUtils.doubleToInt(this.item.getVarAsDouble("base_manpower"));
    }

    public String getTradeGood() {
        return this.item.getVarAsString("trade_goods");
    }

    public Pair<String, String> getName() {
        if (this.item.hasChild("name")) {
            return Pair.of(this.item.getChild("name").getVarAsString("name"), this.item.getChild("name").getVarAsString("old_name"));
        }

        return null;
    }

    public String getTribalOwner() {
        return this.item.getVarAsString("tribal_owner");
    }

    public SaveAdvisor getAdvisor() {
        if (this.item.hasChild("advisor")) {
            return new SaveAdvisor(this.item.getChild("advisor"), this.province.getSave());
        }

        return null;
    }

    public Integer getNativeHostileness() {
        return this.item.getVarAsInt("native_hostileness");
    }

    public Integer getNativeFerocity() {
        return this.item.getVarAsInt("native_ferocity");
    }

    public Integer getNativeSize() {
        return NumbersUtils.doubleToInt(this.item.getVarAsDouble("native_size"));
    }

    public String getOwner() {
        return ClausewitzUtils.removeQuotes(this.item.getVarAsString("owner"));
    }

    public String getController() {
        if (this.item.hasChild("controller") && this.item.getChild("controller").hasVar("tag")) {
            return ClausewitzUtils.removeQuotes(this.item.getChild("controller").getVarAsString("tag"));
        }

        return null;
    }

    public String getRemoveClaim() {
        return ClausewitzUtils.removeQuotes(this.item.getVarAsString("remove_claim"));
    }

    public List<String> getDiscoveredBy() {
        return this.item.getVarsAsStrings("discovered_by").stream().map(ClausewitzUtils::removeQuotes).toList();
    }

    public String getCulture() {
        return this.item.getVarAsString("culture");
    }

    public String getReligion() {
        return this.item.getVarAsString("religion");
    }

    public Boolean getIsCity() {
        return this.item.getVarAsBool("is_city");
    }

    public ProvinceRevolt getRevolt() {
        if (this.item.hasChild("revolt")) {
            return new ProvinceRevolt(this.item.getChild("revolt"));
        }

        return null;
    }

    public List<String> getBuildings() {
        return this.province.getSave()
                            .getGame()
                            .getBuildings()
                            .stream()
                            .map(Building::getName)
                            .filter(name -> this.item.hasVar(name, "yes"))
                            .toList();
    }
}
