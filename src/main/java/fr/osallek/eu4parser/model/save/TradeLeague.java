package fr.osallek.eu4parser.model.save;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.model.save.country.SaveCountry;

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

    public List<SaveCountry> getMembers() {
        ClausewitzList list = this.item.getList("members");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues().stream().map(this.save::getCountry).collect(Collectors.toList());
    }

    public boolean hasMember(SaveCountry country) {
        return getMembers().contains(country);
    }

    public void addMember(SaveCountry member) {
        ClausewitzList list = this.item.getList("members");

        if (!list.contains(member.getTag())) {
            list.add(member.getTag());
            member.setTradeLeague(this);
        }
    }

    public void removeMember(SaveCountry member) {
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
