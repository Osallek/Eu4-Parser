package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.save.Save;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ActiveRelation {

    private final Save save;

    private final ClausewitzItem item;

    private List<Opinion> opinions;

    public ActiveRelation(ClausewitzItem item, Save save) {
        this.save = save;
        this.item = item;
        refreshAttributes();
    }

    public Country getCountry() {
        return this.save.getCountry(this.item.getName());
    }

    public Integer getTrustValue() {
        return this.item.getVarAsInt("trust_value");
    }

    public void setTrustValue(Integer trustValue) {
        if (trustValue < 0) {
            trustValue = 0;
        } else if (trustValue > 100) {
            trustValue = 100;
        }

        this.item.setVariable("trust_value", trustValue);
    }

    public Integer getCachedSum() {
        return this.item.getVarAsInt("cached_sum");
    }

    public Date getLastSendDiplomat() {
        return this.item.getVarAsDate("last_send_diplomat");
    }

    public void setLastSendDiplomat(Date lastSendDiplomat) {
        this.item.setVariable("last_send_diplomat", lastSendDiplomat);
    }

    public Date getLastSpyDiscovery() {
        return this.item.getVarAsDate("last_spy_discovery");
    }

    public void setLastSpyDiscovery(Date lastSpyDiscovery) {
        this.item.setVariable("last_spy_discovery", lastSpyDiscovery);
    }

    /**
     * @return Spy network: * 100 (ie: 5000 => 50 spy network)
     */
    public Integer getSpyNetwork() {
        return this.item.getVarAsInt("spy_network");
    }

    public void setSpyNetwork(Integer spyNetwork) {
        if (spyNetwork < 0) {
            spyNetwork = 0;
        } else if (spyNetwork > 10000) {
            spyNetwork = 10000;
        }

        this.item.setVariable("spy_network", spyNetwork);
    }

    public Date getLastWar() {
        return this.item.getVarAsDate("last_war");
    }

    public void setLastWar(Date lastWar) {
        this.item.setVariable("last_war", lastWar);
    }

    public Integer getLastWarScore() {
        return this.item.getVarAsInt("last_warscore");
    }

    public void setLastWarScore(Integer lastWarScore) {
        if (lastWarScore < 0) {
            lastWarScore = 0;
        } else if (lastWarScore > 100) {
            lastWarScore = 100;
        }

        this.item.setVariable("last_warscore", lastWarScore);
    }

    public String getAttitude() {
        return this.item.getVarAsString("attitude");
    }

    public Boolean isEmbargoing() {
        return this.item.getVarAsBool("is_embargoing");
    }

    public Boolean truce() {
        return this.item.getVarAsBool("truce");
    }

    void setEmbargoing(boolean embargoing) {
        this.item.setVariable("is_embargoing", embargoing);
    }

    public Boolean isBuildingSpyNetwork() {
        return this.item.getVarAsBool("is_building_spy_network");
    }

    public Boolean getRecalcAttitude() {
        return this.item.getVarAsBool("recalc_attitude");
    }

    public Boolean hasCultureGroupClaim() {
        return this.item.getVarAsBool("has_culture_group_claim");
    }

    public Boolean hasClaim() {
        return this.item.getVarAsBool("has_claim");
    }

    public Boolean hasCoreClaim() {
        return this.item.getVarAsBool("has_core_claim");
    }

    public List<Opinion> getOpinions() {
        return opinions;
    }

    public void addOpinion(String modifier, Date date, Double currentOpinion) {
        Opinion.addToItem(this.item, modifier, date, currentOpinion);
        refreshAttributes();
    }

    public void removeOpinion(int index) {
        this.item.removeVariable("opinion", index);
        refreshAttributes();
    }

    private void refreshAttributes() {
        List<ClausewitzItem> naviesItems = this.item.getChildren("opinion");
        this.opinions = naviesItems.stream()
                                   .map(Opinion::new)
                                   .collect(Collectors.toList());
    }
}
