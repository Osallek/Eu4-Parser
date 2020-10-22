package com.osallek.eu4parser.model.save;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.eu4parser.model.save.country.Country;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class TradeLeague {

    private final Save save;

    private final ClausewitzItem item;

    public TradeLeague(ClausewitzItem item, Save save) {
        this.save = save;
        this.item = item;
    }

    public Integer getId() {
        return this.item.getVarAsInt("id");
    }

    public List<Country> getMembers() {
        ClausewitzList list = this.item.getList("members");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues().stream().map(this.save::getCountry).collect(Collectors.toList());
    }

    public boolean hasMember(Country country) {
        return getMembers().contains(country);
    }

    public void addMember(Country member) {
        ClausewitzList list = this.item.getList("members");

        if (!list.contains(member.getTag())) {
            list.add(member.getTag());
            member.setTradeLeague(this);
        }
    }

    public void removeMember(Country member) {
        ClausewitzList list = this.item.getList("members");

        if (list != null) {
            list.remove(member.getTag());
            member.setTradeLeague(null);
        }
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String... members) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "trade_league", parent.getOrder() + 1);
        toItem.addVariable("id", new Random().nextInt(Integer.MAX_VALUE));
        toItem.addList("members", Arrays.stream(members)
                                        .map(ClausewitzUtils::addQuotes)
                                        .filter(s -> s.length() == 5)
                                        .collect(Collectors.toList()));
        parent.addChild(toItem);

        return toItem;
    }
}
