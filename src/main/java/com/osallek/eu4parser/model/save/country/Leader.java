package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.save.Id;

import java.util.Date;

public class Leader {

    private final ClausewitzItem item;

    private Id id;

    private Id monarchId;

    public Leader(ClausewitzItem item) {
        this.item = item;
        refreshAttributes();
    }

    public Id getId() {
        return id;
    }

    public String getName() {
        return this.item.getVarAsString("name");
    }

    public void setName(String name) {
        this.item.setVariable("name", ClausewitzUtils.addQuotes(name));
    }

    public LeaderType getType() {
        String type = this.item.getVarAsString("type");

        if (type != null) {
            return LeaderType.valueOf(type.toUpperCase());
        }

        return null;
    }

    public void setType(LeaderType type) {
        this.item.setVariable("type", type.name().toLowerCase());
    }

    public Boolean getFemale() {
        return this.item.getVarAsBool("female");
    }

    public void setFemale(boolean female) {
        this.item.setVariable("female", female);
    }

    public Integer getManuever() {
        return this.item.getVarAsInt("manuever");
    }

    public void setManuever(int manuever) {
        this.item.setVariable("manuever", manuever);
    }

    public Integer getFire() {
        return this.item.getVarAsInt("fire");
    }

    public void setFire(int fire) {
        this.item.setVariable("fire", fire);
    }

    public Integer getShock() {
        return this.item.getVarAsInt("shock");
    }

    public void setShock(int shock) {
        this.item.setVariable("shock", shock);
    }

    public Integer getSiege() {
        return this.item.getVarAsInt("siege");
    }

    public void setSiege(int siege) {
        this.item.setVariable("siege", siege);
    }

    public String getPersonality() {
        return this.item.getVarAsString("personality");
    }

    public void setPersonality(String personality) {
        this.item.setVariable("personality", personality);
    }

    public Date getActivation() {
        return this.item.getVarAsDate("activation");
    }

    public void setActivation(Date activation) {
        this.item.setVariable("activation", activation);
    }

    public Id getMonarchId() {
        return monarchId;
    }

    private void refreshAttributes() {
        ClausewitzItem idChild = this.item.getChild("id");

        if (idChild != null) {
            this.id = new Id(idChild);
        }
        ClausewitzItem monarchIdChild = this.item.getChild("monarch_id");

        if (monarchIdChild != null) {
            this.monarchId = new Id(monarchIdChild);
        }
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, Leader leader) {
        return addToItem(parent, leader.getName(), leader.getType(), leader.getManuever(), leader.getFire(),
                         leader.getShock(), leader.getSiege(), leader.getPersonality(), leader.getActivation(),
                         leader.getId().getId());
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String name, LeaderType type, int manuever, int fire, int shock, int siege, String personality, Date activation, long id) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "leader", parent.getOrder() + 1);
        toItem.addVariable("name", ClausewitzUtils.addQuotes(name));
        toItem.addVariable("type", type.name());
        toItem.addVariable("manuever", manuever);
        toItem.addVariable("fire", fire);
        toItem.addVariable("shock", shock);
        toItem.addVariable("siege", siege);
        toItem.addVariable("personality", personality);
        toItem.addVariable("activation", activation);
        Id.addToItem(toItem, id, 49);

        parent.addChild(toItem);

        return toItem;
    }
}
