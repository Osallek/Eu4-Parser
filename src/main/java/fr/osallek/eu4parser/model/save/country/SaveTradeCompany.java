package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.model.game.TradeCompany;
import fr.osallek.eu4parser.model.save.Save;
import fr.osallek.eu4parser.model.save.province.SaveProvince;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class SaveTradeCompany {

    private final Save save;

    private final ClausewitzItem item;

    public SaveTradeCompany(ClausewitzItem item, Save save) {
        this.save = save;
        this.item = item;
    }

    public Optional<String> getName() {
        return this.item.getVarAsString("name");
    }

    public void setName(String name) {
        this.item.setVariable("name", ClausewitzUtils.addQuotes(name));
    }

    public List<SaveProvince> getProvinces() {
        return this.item.getList("provinces").map(ClausewitzList::getValuesAsInt).stream().flatMap(Collection::stream).map(this.save::getProvince).toList();
    }

    public void addProvince(SaveProvince province) {
        this.item.getList("provinces").ifPresentOrElse(list -> {
            if (!list.contains(province.getId())) {
                list.add(province.getId());
            }
        }, () -> this.item.addList("provinces", province.getId()));

        province.getTradePower().ifPresent(this::addPower);
    }

    public void removeProvince(SaveProvince province) {
        this.item.getList("provinces").ifPresent(list -> list.remove(String.valueOf(province.getId())));
    }

    public Optional<Double> getPower() {
        return this.item.getVarAsDouble("power");
    }

    private void addPower(double power) {
        this.item.getVar("power").ifPresentOrElse(variable -> variable.setValue(variable.getAsDouble() + power), () -> this.item.addVariable("power", power));
    }

    public Optional<SaveCountry> getOwner() {
        return this.item.getVarAsString("owner").map(this.save::getCountry);
    }

    public void setOwner(SaveCountry owner) {
        this.item.setVariable("owner", ClausewitzUtils.addQuotes(owner.getTag()));
    }

    public Optional<Double> getTaxIncome() {
        return this.item.getVarAsDouble("tax_income");
    }

    public void setTaxIncome(double taxIncome) {
        this.item.setVariable("tax_income", taxIncome);
    }

    public boolean strongCompany() {
        return this.item.getVarAsBool("strong_company").map(BooleanUtils::toBoolean).orElse(false);
    }

    public void setStrongCompany(boolean strongCompany) {
        this.item.setVariable("strong_company", strongCompany);
    }

    public boolean promoteInvestments() {
        return this.item.getVarAsBool("promote_investments").map(BooleanUtils::toBoolean).orElse(false);
    }

    public void setPromoteInvestments(boolean promoteInvestments) {
        this.item.setVariable("promote_investments", promoteInvestments);
    }

    public TradeCompany getTradeCompany() {
        return this.save.getGame()
                        .getTradeCompanies()
                        .stream()
                        .filter(c -> CollectionUtils.containsAny(getProvinces(), c.getProvinces()))
                        .findFirst()
                        .orElse(null);
    }

    public static ClausewitzItem addToItem(Save save, ClausewitzItem parent, String name, SaveProvince province) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "trade_company", parent.getMaxOrder() + 1);
        SaveTradeCompany company = new SaveTradeCompany(toItem, save);
        company.setName(name);
        company.addProvince(province);
        province.getOwner().ifPresent(company::setOwner);
        company.setStrongCompany(false);
        company.setPromoteInvestments(false);
        company.setTaxIncome(0d);

        return toItem;
    }
}
