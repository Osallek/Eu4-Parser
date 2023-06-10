package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.model.Power;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    public Optional<Boolean> getInitialized() {
        return this.item.getVarAsBool("initialized");
    }

    public Optional<Boolean> getInitializedAttitudes() {
        return this.item.getVarAsBool("initialized_attitudes");
    }

    public Optional<Boolean> getStatic() {
        return this.item.getVarAsBool("static");
    }

    public Optional<String> getPersonality() {
        return this.item.getVarAsString("personality");
    }

    public Optional<LocalDate> getLastRecalcDate() {
        return this.item.getVarAsDate("last_recalc_date");
    }

    public Optional<Boolean> getHreInterest() {
        return this.item.getVarAsBool("hre_interest");
    }

    public Optional<Boolean> getPapacyInterest() {
        return this.item.getVarAsBool("papacy_interest");
    }

    public Optional<Integer> getNeedsRegiments() {
        return this.item.getVarAsInt("needs_regiments");
    }

    public Optional<Boolean> getNeedsMoney() {
        return this.item.getVarAsBool("needs_money");
    }

    public Optional<Boolean> getNeedsBuildings() {
        return this.item.getVarAsBool("needs_buildings");
    }

    public Optional<Boolean> getNeedsShips() {
        return this.item.getVarAsBool("needs_ships");
    }

    public Map<Power, Integer> getPowers() {
        Map<Power, Integer> powers = new EnumMap<>(Power.class);
        Optional<ClausewitzList> list = this.item.getList("powers");

        if (list.isEmpty()) {
            return powers;
        }

        for (Power power : Power.values()) {
            list.get().getAsInt(power.ordinal()).ifPresent(integer -> powers.put(power, integer));
        }

        return powers;
    }

    public Optional<Double> getTreasury() {
        return this.item.getVarAsDouble("treasury");
    }

    public Optional<String> getPowerBalanceThreat() {
        return this.item.getVarAsString("power_balance_threat");
    }

    public Optional<String> getPowerBalanceThreatCache() {
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

    public Optional<Integer> getDefendedHomeStrait() {
        return this.item.getVarAsInt("defended_home_strait");
    }

    private void refreshAttributes() {
        List<ClausewitzItem> conquerProvItems = this.item.getChildren("conquer_prov");
        this.conquerProvs = conquerProvItems.stream()
                                            .map(ConquerProv::new)
                                            .toList();

        List<ClausewitzItem> threatItems = this.item.getChildren("threat");
        this.threats = threatItems.stream()
                                  .map(Threat::new)
                                  .toList();

        List<ClausewitzItem> antagonizeItems = this.item.getChildren("antagonize");
        this.antagonize = antagonizeItems.stream()
                                         .map(Threat::new)
                                         .toList();

        List<ClausewitzItem> befriendItems = this.item.getChildren("befriend");
        this.befriends = befriendItems.stream()
                                      .map(Threat::new)
                                      .toList();

        List<ClausewitzItem> rivalsItems = this.item.getChildren("rival");
        this.rivals = rivalsItems.stream()
                                 .map(Threat::new)
                                 .toList();

        List<ClausewitzItem> militaryAccessesItems = this.item.getChildren("military_access");
        this.militaryAccesses = militaryAccessesItems.stream()
                                                     .map(Threat::new)
                                                     .toList();
    }
}
