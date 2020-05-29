package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;

import java.util.ArrayList;
import java.util.List;

public class TradeCompany {

    private final ClausewitzItem item;

    public TradeCompany(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getVarAsString("name");
    }

    public List<Integer> getProvinces() {
        ClausewitzList list = this.item.getList("provinces");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValuesAsInt();
    }

    public void addProvince(int province) {
        ClausewitzList list = this.item.getList("provinces");

        if (!list.contains(province)) {
            list.add(province);
        }
    }

    public void removeProvince(int province) {
        ClausewitzList list = this.item.getList("provinces");

        if (list != null) {
            list.remove(String.valueOf(province));
        }
    }

    public Double getPower() {
        return this.item.getVarAsDouble("power");
    }

    public String getOwner() {
        return this.item.getVarAsString("owner");
    }

    public Double getTaxIncome() {
        return this.item.getVarAsDouble("tax_income");
    }

    public boolean strongCompany() {
        return Boolean.TRUE.equals(this.item.getVarAsBool("strong_company"));
    }

    public boolean promoteInvestments() {
        return Boolean.TRUE.equals(this.item.getVarAsBool("promote_investments"));
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
