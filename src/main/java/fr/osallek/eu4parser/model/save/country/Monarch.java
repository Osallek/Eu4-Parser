package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.Culture;
import fr.osallek.eu4parser.model.game.RulerPersonality;
import fr.osallek.eu4parser.model.save.Id;
import fr.osallek.eu4parser.model.save.ListOfDates;
import fr.osallek.eu4parser.model.save.Save;
import fr.osallek.eu4parser.model.save.SaveReligion;

import java.time.LocalDate;

public class Monarch {

    protected final Save save;

    protected final Country country;

    protected final ClausewitzItem item;

    protected LocalDate monarchDate;

    private Id id;

    private Personalities personalities;

    private HasDeclaredWar hasDeclaredWar;

    private ListOfDates rulerFlags;

    private Id leaderId;

    private Leader leader;

    public Monarch(ClausewitzItem item, Save save, Country country) {
        this.save = save;
        this.item = item;
        this.country = country;
        refreshAttributes();
    }

    public Monarch(ClausewitzItem item, Save save, Country country, LocalDate date) {
        this.save = save;
        this.item = item;
        this.country = country;
        this.monarchDate = date;
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

        if (this.item.getName().endsWith("_heir")) {
            this.getCountry().getHistory().getHeir(this.getId().getId()).setName(name);
        } else if (this.item.getName().endsWith("_consort")) {
            this.getCountry().getHistory().getQueen(this.getId().getId()).setName(name);
        }
    }

    public LocalDate getMonarchDate() {
        return monarchDate;
    }

    public Country getCountry() {
        return this.save.getCountry(this.item.getVarAsString("country"));
    }

    public Integer getAdm() {
        return this.item.getVarAsInt("ADM");
    }

    public void setAdm(int adm) {
        this.item.setVariable("ADM", adm);

        if (this.item.getName().endsWith("_heir")) {
            this.getCountry().getHistory().getHeir(this.getId().getId()).setAdm(adm);
        } else if (this.item.getName().endsWith("_consort")) {
            this.getCountry().getHistory().getQueen(this.getId().getId()).setAdm(adm);
        }
    }

    public Integer getDip() {
        return this.item.getVarAsInt("DIP");
    }

    public void setDip(int dip) {
        this.item.setVariable("DIP", dip);

        if (this.item.getName().endsWith("_heir")) {
            this.getCountry().getHistory().getHeir(this.getId().getId()).setDip(dip);
        } else if (this.item.getName().endsWith("_consort")) {
            this.getCountry().getHistory().getQueen(this.getId().getId()).setDip(dip);
        }
    }

    public Integer getMil() {
        return this.item.getVarAsInt("MIL");
    }

    public void setMil(int mil) {
        this.item.setVariable("MIL", mil);

        if (this.item.getName().endsWith("_heir")) {
            this.getCountry().getHistory().getHeir(this.getId().getId()).setMil(mil);
        } else if (this.item.getName().endsWith("_consort")) {
            this.getCountry().getHistory().getQueen(this.getId().getId()).setMil(mil);
        }
    }

    public Boolean getFemale() {
        return this.item.getVarAsBool("female");
    }

    public Boolean getRegent() {
        return this.item.getVarAsBool("regent");
    }

    public Culture getCulture() {
        return this.save.getGame().getCulture(this.item.getVarAsString("culture"));
    }

    public void setCulture(Culture culture) {
        this.item.setVariable("culture", culture.getName());

        if (this.item.getName().endsWith("_heir")) {
            this.getCountry().getHistory().getHeir(this.getId().getId()).setCulture(culture);
        } else if (this.item.getName().endsWith("_consort")) {
            this.getCountry().getHistory().getQueen(this.getId().getId()).setCulture(culture);
        }
    }

    public SaveReligion getReligion() {
        return this.save.getReligions().getReligion(this.item.getVarAsString("religion"));
    }

    public void setReligion(SaveReligion religion) {
        this.item.setVariable("religion", religion.getName());

        if (this.item.getName().endsWith("_heir")) {
            this.getCountry().getHistory().getHeir(this.getId().getId()).setReligion(religion);
        } else if (this.item.getName().endsWith("_consort")) {
            this.getCountry().getHistory().getQueen(this.getId().getId()).setReligion(religion);
        }
    }

    public String getDynasty() {
        return this.item.getVarAsString("dynasty");
    }

    public void setDynasty(String dynasty) {
        this.item.setVariable("dynasty", ClausewitzUtils.addQuotes(dynasty));

        if (this.item.getName().endsWith("_heir")) {
            this.getCountry().getHistory().getHeir(this.getId().getId()).setDynasty(dynasty);
        } else if (this.item.getName().endsWith("_consort")) {
            this.getCountry().getHistory().getQueen(this.getId().getId()).setDynasty(dynasty);
        }
    }

    public LocalDate getBirthDate() {
        return this.item.getVarAsDate("birth_date");
    }

    public void setBirthDate(LocalDate birthDate) {
        this.item.setVariable("birth_date", birthDate);

        if (this.item.getName().endsWith("_heir")) {
            this.getCountry().getHistory().getHeir(this.getId().getId()).setBirthDate(birthDate);
        } else if (this.item.getName().endsWith("_consort")) {
            this.getCountry().getHistory().getQueen(this.getId().getId()).setBirthDate(birthDate);
        }
    }

    public LocalDate getDeathDate() {
        return this.item.getVarAsDate("death_date");
    }

    public void setDeathDate(LocalDate deathDate) {
        this.item.setVariable("death_date", deathDate);

        if (this.item.getName().endsWith("_heir")) {
            this.getCountry().getHistory().getHeir(this.getId().getId()).setDeathDate(deathDate);
        } else if (this.item.getName().endsWith("_consort")) {
            this.getCountry().getHistory().getQueen(this.getId().getId()).setDeathDate(deathDate);
        }
    }

    public Boolean getSucceeded() {
        return this.item.getVarAsBool("succeeded");
    }

    public String getMonarchName() {
        return this.item.getVarAsString("monarch_name");
    }

    public void setMonarchName(String monarchName) {
        this.item.setVariable("monarch_name", ClausewitzUtils.addQuotes(monarchName));

        if (this.item.getName().endsWith("_heir")) {
            this.getCountry().getHistory().getHeir(this.getId().getId()).setMonarchName(monarchName);
        } else if (this.item.getName().endsWith("_consort")) {
            this.getCountry().getHistory().getQueen(this.getId().getId()).setMonarchName(monarchName);
        }
    }

    public Personalities getPersonalities() {
        return personalities;
    }

    public void addPersonality(RulerPersonality personality) {
        if (this.personalities == null) {
            this.item.addChild("personalities");
            refreshAttributes();
        }

        this.personalities.addPersonality(personality);

        if (this.item.getName().endsWith("_heir")) {
            this.getCountry().getHistory().getHeir(this.getId().getId()).addPersonality(personality);
        } else if (this.item.getName().endsWith("_consort")) {
            this.getCountry().getHistory().getQueen(this.getId().getId()).addPersonality(personality);
        }
    }

    public void removePersonality(int index) {
        if (this.personalities != null) {
            this.personalities.removePersonality(index);

            if (this.item.getName().endsWith("_heir")) {
                this.getCountry().getHistory().getHeir(this.getId().getId()).removePersonality(index);
            } else if (this.item.getName().endsWith("_consort")) {
                this.getCountry().getHistory().getQueen(this.getId().getId()).removePersonality(index);
            }

            if (this.personalities.item.getAllOrdered().isEmpty()) {
                this.item.removeChild("personalities");
                refreshAttributes();
            }
        }
    }

    public void removePersonality(RulerPersonality personality) {
        if (this.personalities != null) {
            this.personalities.removePersonality(personality);

            if (this.item.getName().endsWith("_heir")) {
                this.getCountry().getHistory().getHeir(this.getId().getId()).removePersonality(personality);
            } else if (this.item.getName().endsWith("_consort")) {
                this.getCountry().getHistory().getQueen(this.getId().getId()).removePersonality(personality);
            }

            if (this.personalities.item.getAllOrdered().isEmpty()) {
                this.item.removeChild("personalities");
                refreshAttributes();
            }
        }
    }

    public HasDeclaredWar getHasDeclaredWar() {
        return hasDeclaredWar;
    }

    public Id getLeaderId() {
        return leaderId;
    }

    public Leader getLeader() {
        return leader;
    }

    public ListOfDates getRulerFlags() {
        return rulerFlags;
    }

    public Country getWho() {
        return this.save.getCountry(ClausewitzUtils.removeQuotes(this.item.getVarAsString("who")));
    }

    public void setWho(Country who) {
        this.item.setVariable("who", ClausewitzUtils.addQuotes(who.getTag()));
    }

    public Double getAmount() {
        return this.item.getVarAsDouble("amount");
    }

    public void setAmount(double amount) {
        if (amount < 0) {
            amount = 0;
        } else if (amount > 100) {
            amount = 100;
        }

        this.item.setVariable("amount", amount);
    }

    public Boolean getHistory() {
        return this.item.getVarAsBool("history");
    }

    private void refreshAttributes() {
        ClausewitzItem idChild = this.item.getChild("id");

        if (idChild != null) {
            this.id = new Id(idChild);
        }

        ClausewitzItem personalitiesItem = this.item.getChild("personalities");

        if (personalitiesItem != null) {
            this.personalities = new Personalities(personalitiesItem, this.save);
        }

        ClausewitzItem hasDeclaredWarItem = this.item.getChild("has_declared_war");

        if (hasDeclaredWarItem != null) {
            this.hasDeclaredWar = new HasDeclaredWar(hasDeclaredWarItem);
        }

        ClausewitzItem rulerFlagsItem = this.item.getChild("ruler_flags");

        if (rulerFlagsItem != null) {
            this.rulerFlags = new ListOfDates(rulerFlagsItem);
        }

        ClausewitzItem leaderIdChild = this.item.getChild("leader_id");

        if (leaderIdChild != null) {
            this.leaderId = new Id(leaderIdChild);
        }

        ClausewitzItem leaderChild = this.item.getChild("leader");

        if (leaderChild != null) {
            this.leader = new Leader(leaderChild, this.country);
        }
    }
}
