package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.common.NumbersUtils;
import com.osallek.eu4parser.model.game.LeaderPersonality;
import com.osallek.eu4parser.model.save.Id;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class Leader {

    private final ClausewitzItem item;

    private final Country country;

    private Id id;

    private Id monarchId;

    public Leader(ClausewitzItem item, Country country) {
        this.item = item;
        this.country = country;
        refreshAttributes();
    }

    public Country getCountry() {
        return country;
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

    public int getManuever() {
        return NumbersUtils.intOrDefault(this.item.getVarAsInt("manuever"));
    }

    public void setManuever(int manuever) {
        this.item.setVariable("manuever", manuever);
    }

    public int getFire() {
        return NumbersUtils.intOrDefault(this.item.getVarAsInt("fire"));
    }

    public void setFire(int fire) {
        this.item.setVariable("fire", fire);
    }

    public int getShock() {
        return NumbersUtils.intOrDefault(this.item.getVarAsInt("shock"));
    }

    public void setShock(int shock) {
        this.item.setVariable("shock", shock);
    }

    public int getSiege() {
        return NumbersUtils.intOrDefault(this.item.getVarAsInt("siege"));
    }

    public void setSiege(int siege) {
        this.item.setVariable("siege", siege);
    }

    public LeaderPersonality getPersonality() {
        return this.country.getSave().getGame().getLeaderPersonality(this.item.getVarAsString("personality"));
    }

    public void setPersonality(LeaderPersonality personality) {
        this.item.setVariable("personality", personality.getName());
    }

    public LocalDate getActivation() {
        return this.item.getVarAsDate("activation");
    }

    public void setActivation(LocalDate activation) {
        this.item.setVariable("activation", activation);
    }

    public LocalDate getBirthDate() {
        return this.item.getVarAsDate("birth_date");
    }

    public void setBirthDate(LocalDate birthDate) {
        this.item.setVariable("birth_date", birthDate);
    }

    public LocalDate getDeathDate() {
        return this.item.getVarAsDate("death_date");
    }

    public void setDeathDate(LocalDate deathDate) {
        this.item.setVariable("death_date", deathDate);
    }

    public Id getMonarchId() {
        return monarchId;
    }

    public int getTotalPips() {
        return BigDecimal.valueOf(getFire())
                         .add(BigDecimal.valueOf(getShock()))
                         .add(BigDecimal.valueOf(getManuever()))
                         .add(BigDecimal.valueOf(getSiege()))
                         .intValue();
    }

    public int getNbStars() {
        return BigDecimal.valueOf(getFire()).add(BigDecimal.valueOf(getShock())).divide(BigDecimal.valueOf(6), 0, RoundingMode.HALF_EVEN)
                         .add(BigDecimal.valueOf(getManuever()).add(BigDecimal.valueOf(getSiege())).divide(BigDecimal.valueOf(18), 0, RoundingMode.HALF_EVEN))
                         .add(BigDecimal.ONE).min(BigDecimal.valueOf(3)).intValue();
    }

    private void refreshAttributes() {
        ClausewitzItem idChild = this.item.getChild("id");

        if (idChild != null) {
            this.id = new Id(idChild);
        }
        ClausewitzItem monarchIdChild = this.item.getChild("monarch_id"); //10305

        if (monarchIdChild != null) {
            this.monarchId = new Id(monarchIdChild);
        }
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, Leader leader) {
        return addToItem(parent, leader.getName(), leader.getType(), leader.getManuever(), leader.getFire(), leader.getShock(), leader.getSiege(),
                         leader.getPersonality(), leader.getActivation(), leader.getBirthDate(), leader.getId().getId(), leader.getCountry());
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String name, LeaderType type, int manuever, int fire, int shock, int siege,
                                           LeaderPersonality personality, LocalDate activation, LocalDate birthDate, int id, Country country) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "leader", parent.getOrder() + 1);
        toItem.addVariable("name", ClausewitzUtils.addQuotes(name));
        toItem.addVariable("type", type.name().toLowerCase());
        toItem.addVariable("manuever", manuever);
        toItem.addVariable("fire", fire);
        toItem.addVariable("shock", shock);
        toItem.addVariable("siege", siege);
        toItem.addVariable("country", ClausewitzUtils.addQuotes(country.getTag()));

        if (personality != null) {
            toItem.addVariable("personality", personality.getName().toLowerCase());
        }

        toItem.addVariable("activation", activation);
        toItem.addVariable("birth_date", birthDate);
        Id.addToItem(toItem, id, 49);

        parent.addChild(toItem);

        return toItem;
    }

    @Override
    public String toString() {
        return "id=" + id.getId();
    }
}
