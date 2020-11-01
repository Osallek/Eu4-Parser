package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.eu4parser.model.Power;

import java.time.LocalDate;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Ai {

    private final ClausewitzItem item;

    private List<ConquerProv> conquerProvs;

    private List<Threat> threats;

    private List<Threat> antagonize;

    private List<Threat> befriends;

    private List<Threat> rivals;

    private List<Threat> militaryAccesses;

    public Ai(ClausewitzItem item) {
        this.item = item;
        refreshAttributes();
    }

    public Boolean getInitialized() {
        return this.item.getVarAsBool("initialized");
    }

    public Boolean getInitializedAttitudes() {
        return this.item.getVarAsBool("initialized_attitudes");
    }

    public Boolean getStatic() {
        return this.item.getVarAsBool("static");
    }

    public String getPersonality() {
        return this.item.getVarAsString("personality");
    }

    public LocalDate getLastRecalcDate() {
        return this.item.getVarAsDate("last_recalc_date");
    }

    public Boolean getHreInterest() {
        return this.item.getVarAsBool("hre_interest");
    }

    public Boolean getPapacyInterest() {
        return this.item.getVarAsBool("papacy_interest");
    }

    public Integer getNeedsRegiments() {
        return this.item.getVarAsInt("needs_regiments");
    }

    public Boolean getNeedsMoney() {
        return this.item.getVarAsBool("needs_money");
    }

    public Boolean getNeedsBuildings() {
        return this.item.getVarAsBool("needs_buildings");
    }

    public Boolean getNeedsShips() {
        return this.item.getVarAsBool("needs_ships");
    }

    public Map<Power, Integer> getPowers() {
        ClausewitzList list = this.item.getList("powers");
        Map<Power, Integer> powers = new EnumMap<>(Power.class);

        if (list == null) {
            return powers;
        }

        for (Power power : Power.values()) {
            powers.put(power, list.getAsInt(power.ordinal()));
        }

        return powers;
    }

    public Double getTreasury() {
        return this.item.getVarAsDouble("treasury");
    }

    public String getPowerBalanceThreat() {
        return this.item.getVarAsString("power_balance_threat");
    }

    public String getPowerBalanceThreatCache() {
        return this.item.getVarAsString("power_balance_threat_cache");
    }

    public List<ConquerProv> getConquerProvs() {
        return conquerProvs;
    }

    public List<Threat> getThreats() {
        return threats;
    }

    public List<Threat> getAntagonize() {
        return antagonize;
    }

    public List<Threat> getBefriends() {
        return befriends;
    }

    public List<Threat> getRivals() {
        return rivals;
    }

    public List<Threat> getMilitaryAccesses() {
        return militaryAccesses;
    }

    public Integer getDefendedHomeStrait() {
        return this.item.getVarAsInt("defended_home_strait");
    }

    private void refreshAttributes() {
        List<ClausewitzItem> conquerProvItems = this.item.getChildren("conquer_prov");
        this.conquerProvs = conquerProvItems.stream()
                                            .map(ConquerProv::new)
                                            .collect(Collectors.toList());

        List<ClausewitzItem> threatItems = this.item.getChildren("threat");
        this.threats = threatItems.stream()
                                  .map(Threat::new)
                                  .collect(Collectors.toList());

        List<ClausewitzItem> antagonizeItems = this.item.getChildren("antagonize");
        this.antagonize = antagonizeItems.stream()
                                         .map(Threat::new)
                                         .collect(Collectors.toList());

        List<ClausewitzItem> befriendItems = this.item.getChildren("befriend");
        this.befriends = befriendItems.stream()
                                      .map(Threat::new)
                                      .collect(Collectors.toList());

        List<ClausewitzItem> rivalsItems = this.item.getChildren("rival");
        this.rivals = rivalsItems.stream()
                                 .map(Threat::new)
                                 .collect(Collectors.toList());

        List<ClausewitzItem> militaryAccessesItems = this.item.getChildren("military_access");
        this.militaryAccesses = militaryAccessesItems.stream()
                                                     .map(Threat::new)
                                                     .collect(Collectors.toList());
    }
}
