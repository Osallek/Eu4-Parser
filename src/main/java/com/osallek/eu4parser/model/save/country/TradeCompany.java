package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.eu4parser.model.save.Save;
import com.osallek.eu4parser.model.save.province.SaveProvince;
import org.apache.commons.lang3.BooleanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TradeCompany {

    private final Save save;

    private final ClausewitzItem item;

    public TradeCompany(ClausewitzItem item, Save save) {
        this.save = save;
        this.item = item;
    }

    public String getName() {
        return this.item.getVarAsString("name");
    }

    public List<SaveProvince> getProvinces() {
        ClausewitzList list = this.item.getList("provinces");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValuesAsInt().stream().map(this.save::getProvince).collect(Collectors.toList());
    }

    public void addProvince(SaveProvince province) {
        ClausewitzList list = this.item.getList("provinces");

        if (!list.contains(province.getId())) {
            list.add(province.getId());
        }
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

    public Country getOwner() {
        return this.save.getCountry(ClausewitzUtils.removeQuotes(this.item.getVarAsString("owner")));
    }

    public Double getTaxIncome() {
        return this.item.getVarAsDouble("tax_income");
    }

    public boolean strongCompany() {
        return BooleanUtils.toBoolean(this.item.getVarAsBool("strong_company"));
    }

    public boolean promoteInvestments() {
        return BooleanUtils.toBoolean(this.item.getVarAsBool("promote_investments"));
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String name, String owner, Integer... provinces) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "trade_company", parent.getOrder() + 1);
        toItem.addVariable("name", ClausewitzUtils.addQuotes(name));
        toItem.addList("provinces", provinces);
        toItem.addVariable("owner", ClausewitzUtils.addQuotes(owner));
        parent.addChild(toItem);

        return toItem;
    }
}
