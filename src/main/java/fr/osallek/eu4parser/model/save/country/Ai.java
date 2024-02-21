package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.model.Power;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class Ai {

    private final ClausewitzItem item;

    public Ai(ClausewitzItem item) {
        this.item = item;
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
        return this.item.getChildren("conquer_prov").stream().map(ConquerProv::new).toList();
    }

    public List<Threat> getThreats() {
        return this.item.getChildren("threat").stream().map(Threat::new).toList();
    }

    public List<Threat> getAntagonize() {
        return this.item.getChildren("antagonize").stream().map(Threat::new).toList();
    }

    public List<Threat> getBefriends() {
        return this.item.getChildren("befriend").stream().map(Threat::new).toList();
    }

    public List<Threat> getRivals() {
        return this.item.getChildren("rival").stream().map(Threat::new).toList();
    }

    public List<Threat> getMilitaryAccesses() {
        return this.item.getChildren("military_access").stream().map(Threat::new).toList();
    }

    public Integer getDefendedHomeStrait() {
        return this.item.getVarAsInt("defended_home_strait");
    }
}
