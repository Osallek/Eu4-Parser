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
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        return ClausewitzUtils.removeQuotes(this.item.getLastVarAsString("capital"));
    }

    public Double getColonySize() {
        return this.item.getLastVarAsDouble("colonysize");
    }

    public Double getUnrest() {
        return this.item.getLastVarAsDouble("unrest");
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
        return this.item.getLastVarAsBool("hre");
    }

    public Double getBaseTax() {
        return this.item.getLastVarAsDouble("base_tax");
    }

    public Double getBaseProduction() {
        return this.item.getLastVarAsDouble("base_production");
    }

    public Double getBaseManpower() {
        return this.item.getLastVarAsDouble("base_manpower");
    }

    public String getTradeGood() {
        return this.item.getLastVarAsString("trade_goods");
    }

    public Pair<String, String> getName() {
        if (this.item.hasChild("name")) {
            return Pair.of(this.item.getLastChild("name").getVarAsString("name"), this.item.getLastChild("name").getVarAsString("old_name"));
        }

        return null;
    }

    public String getTribalOwner() {
        return this.item.getLastVarAsString("tribal_owner");
    }

    public SaveAdvisor getAdvisor() {
        if (this.item.hasChild("advisor")) {
            return new SaveAdvisor(this.item.getLastChild("advisor"), this.province.getSave());
        }

        return null;
    }

    public Integer getNativeHostileness() {
        return this.item.getLastVarAsInt("native_hostileness");
    }

    public Integer getNativeFerocity() {
        return this.item.getLastVarAsInt("native_ferocity");
    }

    public Integer getNativeSize() {
        return NumbersUtils.doubleToInt(this.item.getLastVarAsDouble("native_size"));
    }

    public String getOwner() {
        return ClausewitzUtils.removeQuotes(this.item.getLastVarAsString("owner"));
    }

    public String getController() {
        if (this.item.hasChild("controller") && this.item.getLastChild("controller").hasVar("tag")) {
            return ClausewitzUtils.removeQuotes(this.item.getLastChild("controller").getVarAsString("tag"));
        }

        return null;
    }

    public String getRemoveClaim() {
        return ClausewitzUtils.removeQuotes(this.item.getLastVarAsString("remove_claim"));
    }

    public List<String> getDiscoveredBy() {
        return this.item.getVarsAsStrings("discovered_by").stream().map(ClausewitzUtils::removeQuotes).toList();
    }

    public String getCulture() {
        return this.item.getLastVarAsString("culture");
    }

    public String getReligion() {
        return this.item.getLastVarAsString("religion");
    }

    public Boolean getIsCity() {
        return this.item.getLastVarAsBool("is_city");
    }

    public ProvinceRevolt getRevolt() {
        if (this.item.hasChild("revolt")) {
            return new ProvinceRevolt(this.item.getLastChild("revolt"));
        }

        return null;
    }

    public Map<String, Boolean> getBuildings() {
        return this.province.getSave()
                            .getGame()
                            .getBuildings()
                            .stream()
                            .map(Building::getName)
                            .filter(this.item::hasVar)
                            .collect(Collectors.toMap(Function.identity(), this.item::getVarAsBool));
    }
}
