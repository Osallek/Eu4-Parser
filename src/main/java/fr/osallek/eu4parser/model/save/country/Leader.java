package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.LeaderPersonality;
import fr.osallek.eu4parser.model.save.Id;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Optional;

public class Leader {

    private final ClausewitzItem item;

    private final SaveCountry country;

    public Leader(ClausewitzItem item, SaveCountry country) {
        this.item = item;
        this.country = country;
    }

    public SaveCountry getCountry() {
        return country;
    }

    public Optional<Id> getId() {
        return this.item.getChild("id").map(Id::new);
    }

    public Optional<String> getName() {
        return this.item.getVarAsString("name");
    }

    public void setName(String name) {
        this.item.setVariable("name", ClausewitzUtils.addQuotes(name));
    }

    public Optional<LeaderType> getType() {
        return this.item.getVarAsString("type").map(String::toUpperCase).map(LeaderType::valueOf);
    }

    public void setType(LeaderType type) {
        this.item.setVariable("type", type.name().toLowerCase());
    }

    public Optional<Boolean> getFemale() {
        return this.item.getVarAsBool("female");
    }

    public void setFemale(boolean female) {
        this.item.setVariable("female", female);
    }

    public int getManuever() {
        return this.item.getVarAsInt("manuever").orElse(0);
    }

    public void setManuever(int manuever) {
        this.item.setVariable("manuever", manuever);
    }

    public int getFire() {
        return this.item.getVarAsInt("fire").orElse(0);
    }

    public void setFire(int fire) {
        this.item.setVariable("fire", fire);
    }

    public int getShock() {
        return this.item.getVarAsInt("shock").orElse(0);
    }

    public void setShock(int shock) {
        this.item.setVariable("shock", shock);
    }

    public int getSiege() {
        return this.item.getVarAsInt("siege").orElse(0);
    }

    public void setSiege(int siege) {
        this.item.setVariable("siege", siege);
    }

    public Optional<LeaderPersonality> getPersonality() {
        return this.item.getVarAsString("personality").map(s -> this.country.getSave().getGame().getLeaderPersonality(s));
    }

    public void setPersonality(LeaderPersonality personality) {
        this.item.setVariable("personality", personality.getName());
    }

    public Optional<LocalDate> getActivation() {
        return this.item.getVarAsDate("activation");
    }

    public void setActivation(LocalDate activation) {
        this.item.setVariable("activation", activation);
    }

    public Optional<LocalDate> getBirthDate() {
        return this.item.getVarAsDate("birth_date");
    }

    public void setBirthDate(LocalDate birthDate) {
        this.item.setVariable("birth_date", birthDate);
    }

    public Optional<LocalDate> getDeathDate() {
        return this.item.getVarAsDate("death_date");
    }

    public void setDeathDate(LocalDate deathDate) {
        this.item.setVariable("death_date", deathDate);
    }

    public Optional<Id> getMonarchId() {
        return this.item.getChild("monarch_id").map(Id::new);
    }

    public int getTotalPips() {
        return BigDecimal.valueOf(getFire())
                         .add(BigDecimal.valueOf(getShock()))
                         .add(BigDecimal.valueOf(getManuever()))
                         .add(BigDecimal.valueOf(getSiege()))
                         .intValue();
    }

    public int getNbStars() {
        return BigDecimal.valueOf(getFire())
                         .add(BigDecimal.valueOf(getShock()))
                         .divide(BigDecimal.valueOf(6), 0, RoundingMode.HALF_EVEN)
                         .add(BigDecimal.valueOf(getManuever()).add(BigDecimal.valueOf(getSiege())).divide(BigDecimal.valueOf(18), 0, RoundingMode.HALF_EVEN))
                         .add(BigDecimal.ONE)
                         .min(BigDecimal.valueOf(3))
                         .intValue();
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, Leader leader) {
        return addToItem(parent, leader.getName().orElse(""), leader.getType().orElse(null), leader.getManuever(), leader.getFire(), leader.getShock(),
                         leader.getSiege(), leader.getPersonality().orElse(null), leader.getActivation().orElse(null), leader.getBirthDate().orElse(null),
                         leader.getId().get().getId(), leader.getCountry());
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String name, LeaderType type, int manuever, int fire, int shock, int siege,
                                           LeaderPersonality personality, LocalDate activation, LocalDate birthDate, int id, SaveCountry country) {
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
        return String.valueOf(getId().map(Id::getId).orElse(0));
    }
}
