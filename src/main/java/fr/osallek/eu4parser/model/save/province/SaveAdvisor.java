package fr.osallek.eu4parser.model.save.province;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.Advisor;
import fr.osallek.eu4parser.model.game.Culture;
import fr.osallek.eu4parser.model.game.Modifiers;
import fr.osallek.eu4parser.model.game.ModifiersUtils;
import fr.osallek.eu4parser.model.save.Id;
import fr.osallek.eu4parser.model.save.Save;
import fr.osallek.eu4parser.model.save.SaveReligion;

import java.io.File;
import java.time.LocalDate;

public class SaveAdvisor {

    protected final Save save;

    protected final ClausewitzItem item;

    private final Advisor gameAdvisor;

    private Id id;

    public SaveAdvisor(ClausewitzItem item, Save save) {
        this.save = save;
        this.item = item;
        this.gameAdvisor = this.save.getGame().getAdvisor(getType());
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

    public Advisor getGameAdvisor() {
        return gameAdvisor;
    }

    public String getType() {
        return this.item.getVarAsString("type");
    }

    public void setType(String type) {
        this.item.setVariable("type", type);
    }

    public Integer getSkill() {
        return this.item.getVarAsInt("skill");
    }

    public void setSkill(int skill) {
        this.item.setVariable("skill", skill);
    }

    public SaveProvince getLocation() {
        return this.save.getProvince(this.item.getVarAsInt("location"));
    }

    public void setLocation(SaveProvince location) {
        this.item.setVariable("location", location.getId());
    }

    public Boolean getFemale() {
        return this.item.getVarAsBool("female");
    }

    public Culture getCulture() {
        return this.save.getGame().getCulture(this.item.getVarAsString("culture"));
    }

    public String getCultureName() {
        return this.item.getVarAsString("culture");
    }

    public void setCulture(Culture culture) {
        this.item.setVariable("culture", culture.getName());
    }

    public SaveReligion getReligion() {
        return this.save.getReligions().getReligion(this.item.getVarAsString("religion"));
    }

    public String getReligionName() {
        return this.item.getVarAsString("religion");
    }

    public void setReligion(SaveReligion religion) {
        this.item.setVariable("religion", religion.getName());
    }

    public LocalDate getDate() {
        return this.item.getVarAsDate("date");
    }

    public void setDate(LocalDate date) {
        this.item.setVariable("date", date);
    }

    public LocalDate getHireDate() {
        return this.item.getVarAsDate("hire_date");
    }

    public void setHireDate(LocalDate hireDate) {
        this.item.setVariable("hire_date", hireDate);
    }

    public LocalDate getDeathDate() {
        return this.item.getVarAsDate("death_date");
    }

    public void setDeathDate(LocalDate deathDate) {
        this.item.setVariable("death_date", deathDate);
    }

    public File getImage() {
        return this.gameAdvisor.getDefaultImage();
    }

    public Modifiers getModifiers() {
        if (getGameAdvisor().getSkillScaledModifier() == null) {
            return getGameAdvisor().getModifiers();
        } else {
            return ModifiersUtils.sumModifiers(getGameAdvisor().getModifiers(),
                                               ModifiersUtils.scaleModifiers(getGameAdvisor().getSkillScaledModifier(), getSkill()));
        }
    }

    private void refreshAttributes() {
        ClausewitzItem idChild = this.item.getChild("id");

        if (idChild != null) {
            this.id = new Id(idChild);
        }
    }
}
