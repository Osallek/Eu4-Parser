package com.osallek.eu4parser.model.country;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ActiveRelation {

    private final ClausewitzItem item;

    private List<Opinion> opinions;

    public ActiveRelation(ClausewitzItem item) {
        this.item = item;
        refreshAttributes();
    }

    public String getCountry() {
        return this.item.getName();
    }

    public Integer getTrustValue() {
        return this.item.getVarAsInt("trust_value");
    }

    public void setTrustValue(Integer trustValue) {
        ClausewitzVariable nameVar = this.item.getVar("trust_value");

        if (trustValue < 0) {
            trustValue = 0;
        } else if (trustValue > 100) {
            trustValue = 100;
        }

        if (nameVar != null) {
            nameVar.setValue(trustValue);
        } else {
            this.item.addVariable("trust_value", trustValue);
        }
    }

    public Integer getCachedSum() {
        return this.item.getVarAsInt("cached_sum");
    }

    public Date getLastSendDiplomat() {
        return this.item.getVarAsDate("last_send_diplomat");
    }

    public void setLastSendDiplomat(Date lastSendDiplomat) {
        ClausewitzVariable nameVar = this.item.getVar("last_send_diplomat");

        if (nameVar != null) {
            nameVar.setValue(lastSendDiplomat);
        } else {
            this.item.addVariable("last_send_diplomat", lastSendDiplomat);
        }
    }

    public Date getLastSpyDiscovery() {
        return this.item.getVarAsDate("last_spy_discovery");
    }

    public void setLastSpyDiscovery(Date lastSpyDiscovery) {
        ClausewitzVariable nameVar = this.item.getVar("last_spy_discovery");

        if (nameVar != null) {
            nameVar.setValue(lastSpyDiscovery);
        } else {
            this.item.addVariable("last_spy_discovery", lastSpyDiscovery);
        }
    }

    /**
     * @return Spy network: * 100 (ie: 5000 => 50 spy network)
     */
    public Integer getSpyNetwork() {
        return this.item.getVarAsInt("spy_network");
    }

    public void setSpyNetwork(Integer spyNetwork) {
        ClausewitzVariable nameVar = this.item.getVar("spy_network");

        if (spyNetwork < 0) {
            spyNetwork = 0;
        } else if (spyNetwork > 10000) {
            spyNetwork = 10000;
        }

        if (nameVar != null) {
            nameVar.setValue(spyNetwork);
        } else {
            this.item.addVariable("spy_network", spyNetwork);
        }
    }

    public Date getLastWar() {
        return this.item.getVarAsDate("last_war");
    }

    public void setLastWar(Date lastWar) {
        ClausewitzVariable nameVar = this.item.getVar("last_war");

        if (nameVar != null) {
            nameVar.setValue(lastWar);
        } else {
            this.item.addVariable("last_war", lastWar);
        }
    }

    public Integer getLastWarScore() {
        return this.item.getVarAsInt("last_warscore");
    }

    public void setLastWarScore(Integer lastWarScore) {
        ClausewitzVariable nameVar = this.item.getVar("last_warscore");

        if (lastWarScore < 0) {
            lastWarScore = 0;
        } else if (lastWarScore > 100) {
            lastWarScore = 100;
        }

        if (nameVar != null) {
            nameVar.setValue(lastWarScore);
        } else {
            this.item.addVariable("last_warscore", lastWarScore);
        }
    }

    public String getAttitude() {
        return this.item.getVarAsString("attitude");
    }

    public Boolean isEmbargoing() {
        return this.item.getVarAsBool("is_embargoing");
    }

    void setEmbargoing(boolean embargoing) {
        ClausewitzVariable nameVar = this.item.getVar("is_embargoing");

        if (nameVar != null) {
            this.item.removeVariable("is_embargoing");
        } else {
            this.item.addVariable("is_embargoing", embargoing);
        }
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
