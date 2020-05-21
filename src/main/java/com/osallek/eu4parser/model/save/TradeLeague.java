package com.osallek.eu4parser.model.save;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class TradeLeague {

    private final ClausewitzItem item;

    public TradeLeague(ClausewitzItem item) {
        this.item = item;
    }

    public Integer getId() {
        return this.item.getVarAsInt("id");
    }

    public List<String> getMembers() {
        ClausewitzList list = this.item.getList("members");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues();
    }

    public void addMember(String member) {
        ClausewitzList list = this.item.getList("members");

        member = ClausewitzUtils.addQuotes(member);

        if (member.length() == 5 && !list.contains(member)) {
            list.add(member);
        }
    }

    public void removeMember(String member) {
        ClausewitzList list = this.item.getList("members");

        if (list != null) {
            list.remove(member);
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
