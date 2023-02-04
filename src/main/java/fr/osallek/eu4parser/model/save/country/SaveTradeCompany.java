package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;
import fr.osallek.eu4parser.model.game.TradeCompany;
import fr.osallek.eu4parser.model.save.Save;
import fr.osallek.eu4parser.model.save.province.SaveProvince;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.util.ArrayList;
import java.util.List;

public class SaveTradeCompany {

    private final Save save;

    private final ClausewitzItem item;

    public SaveTradeCompany(ClausewitzItem item, Save save) {
        this.save = save;
        this.item = item;
    }

    public String getName() {
        return this.item.getVarAsString("name");
    }

    public void setName(String name) {
        this.item.setVariable("name", ClausewitzUtils.addQuotes(name));
    }

    public List<SaveProvince> getProvinces() {
        ClausewitzList list = this.item.getList("provinces");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValuesAsInt().stream().map(this.save::getProvince).toList();
    }

    public void addProvince(SaveProvince province) {
        ClausewitzList list = this.item.getList("provinces");

        if (list == null) {
            this.item.addList("provinces", province.getId());
        } else if (!list.contains(province.getId())) {
            list.add(province.getId());
        }

        addPower(province.getTradePower());
    }

    public void removeProvince(SaveProvince province) {
        ClausewitzList list = this.item.getList("provinces");

        if (list != null) {
            list.remove(String.valueOf(province.getId()));
        }
    }

    public Double getPower() {
        return this.item.getVarAsDouble("power");
    }

    private void addPower(double power) {
        ClausewitzVariable variable = this.item.getVar("power");

        if (variable == null) {
            this.item.addVariable("power", power);
        } else {
            variable.setValue(variable.getAsDouble() + power);
        }
    }

    public SaveCountry getOwner() {
        return this.save.getCountry(ClausewitzUtils.removeQuotes(this.item.getVarAsString("owner")));
    }

    public void setOwner(SaveCountry owner) {
        this.item.setVariable("owner", ClausewitzUtils.addQuotes(owner.getTag()));
    }

    public Double getTaxIncome() {
        return this.item.getVarAsDouble("tax_income");
    }

    public void setTaxIncome(double taxIncome) {
        this.item.setVariable("tax_income", taxIncome);
    }

    public boolean strongCompany() {
        return BooleanUtils.toBoolean(this.item.getVarAsBool("strong_company"));
    }

    public void setStrongCompany(boolean strongCompany) {
        this.item.setVariable("strong_company", strongCompany);
    }

    public boolean promoteInvestments() {
        return BooleanUtils.toBoolean(this.item.getVarAsBool("promote_investments"));
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
        company.setOwner(province.getOwner());
        company.setStrongCompany(false);
        company.setPromoteInvestments(false);
        company.setTaxIncome(0d);

        return toItem;
    }
}
