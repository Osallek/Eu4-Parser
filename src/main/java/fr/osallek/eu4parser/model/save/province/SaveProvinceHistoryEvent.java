package fr.osallek.eu4parser.model.save.province;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzObject;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.common.NumbersUtils;
import fr.osallek.eu4parser.model.game.Building;
import fr.osallek.eu4parser.model.game.ProvinceRevolt;
import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    public Optional<String> getCapital() {
        return this.item.getLastVarAsString("capital").map(ClausewitzUtils::removeQuotes);
    }

    public Optional<Double> getColonySize() {
        return this.item.getLastVarAsDouble("colonysize");
    }

    public Optional<Double> getUnrest() {
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

    public Optional<Boolean> getHre() {
        return this.item.getLastVarAsBool("hre");
    }

    public Optional<Double> getBaseTax() {
        return this.item.getLastVarAsDouble("base_tax");
    }

    public Optional<Double> getBaseProduction() {
        return this.item.getLastVarAsDouble("base_production");
    }

    public Optional<Double> getBaseManpower() {
        return this.item.getLastVarAsDouble("base_manpower");
    }

    public Optional<String> getTradeGood() {
        return this.item.getLastVarAsString("trade_goods");
    }

    public Optional<Pair<String, String>> getName() {
        return this.item.getLastChild("name")
                        .map(i -> Pair.of(i.getVarAsString("name"), i.getVarAsString("old_name")))
                        .filter(pair -> pair.getKey().isPresent() && pair.getValue().isPresent())
                        .map(pair -> Pair.of(pair.getKey().get(), pair.getValue().get()));
    }

    public Optional<String> getTribalOwner() {
        return this.item.getLastVarAsString("tribal_owner");
    }

    public Optional<SaveAdvisor> getAdvisor() {
        return this.item.getLastChild("advisor").map(i -> new SaveAdvisor(i, this.province.getSave()));
    }

    public Optional<Integer> getNativeHostileness() {
        return this.item.getLastVarAsInt("native_hostileness");
    }

    public Optional<Integer> getNativeFerocity() {
        return this.item.getLastVarAsInt("native_ferocity");
    }

    public Optional<Integer> getNativeSize() {
        return this.item.getLastVarAsDouble("native_size").map(NumbersUtils::doubleToInt);
    }

    public Optional<String> getOwner() {
        return this.item.getLastVarAsString("owner").map(ClausewitzUtils::removeQuotes);
    }

    public Optional<String> getFakeOwner() {
        return this.item.getLastVarAsString("fake_owner").map(ClausewitzUtils::removeQuotes);
    }

    public Optional<String> getController() {
        return this.item.getLastChild("controller").filter(i -> i.hasVar("tag")).flatMap(i -> i.getVarAsString("tag")).map(ClausewitzUtils::removeQuotes);
    }

    public Optional<String> getRemoveClaim() {
        return this.item.getLastVarAsString("remove_claim").map(ClausewitzUtils::removeQuotes);
    }

    public List<String> getDiscoveredBy() {
        return this.item.getVarsAsStrings("discovered_by").stream().map(ClausewitzUtils::removeQuotes).toList();
    }

    public Optional<String> getCulture() {
        return this.item.getLastVarAsString("culture");
    }

    public Optional<String> getReligion() {
        return this.item.getLastVarAsString("religion");
    }

    public Optional<Boolean> getIsCity() {
        return this.item.getLastVarAsBool("is_city");
    }

    public Optional<ProvinceRevolt> getRevolt() {
        return this.item.getLastChild("revolt").map(ProvinceRevolt::new);
    }

    public Map<String, Boolean> getBuildings() {
        return this.province.getSave()
                            .getGame()
                            .getBuildings()
                            .stream()
                            .map(Building::getName)
                            .map(this.item::getVar)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .collect(Collectors.toMap(ClausewitzObject::getName, ClausewitzVariable::getAsBool));
    }
}
