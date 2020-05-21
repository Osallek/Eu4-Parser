package com.osallek.eu4parser.model.save.religion;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import com.osallek.eu4parser.common.Eu4Utils;
import com.osallek.eu4parser.model.save.Id;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class Papacy {

    private final ClausewitzItem item;

    private List<Cardinal> cardinals;

    private ColoniesClaims coloniesClaims;

    public Papacy(ClausewitzItem item) {
        this.item = item;
        refreshAttributes();
    }

    public String getCrusadeTarget() {
        String target = this.item.getVarAsString("crusade_target");

        if (target == null || Eu4Utils.DEFAULT_TAG_QUOTES.equals(target)) {
            return null;
        }

        return target;
    }

    public Date getCrusadeStart() {
        Date date = this.item.getVarAsDate("crusade_start");

        if (date == null || Eu4Utils.DEFAULT_DATE.equals(date)) {
            return null;
        }

        return date;
    }

    public void setCrusade(String target, Date date) {
        ClausewitzVariable targetVar = this.item.getVar("crusade_target");

        if (targetVar != null) {
            targetVar.setValue(target);
        } else {
            this.item.addVariable("crusade_target", target);
        }

        ClausewitzVariable dateVar = this.item.getVar("crusade_start");

        if (dateVar != null) {
            dateVar.setValue(date);
        } else {
            this.item.addVariable("crusade_start", date);
        }
    }

    public void removeCrusade() {
        ClausewitzVariable targetVar = this.item.getVar("crusade_target");

        if (targetVar != null) {
            targetVar.setValue(Eu4Utils.DEFAULT_TAG_QUOTES);
        }

        ClausewitzVariable dateVar = this.item.getVar("crusade_start");

        if (dateVar != null) {
            dateVar.setValue(Eu4Utils.DEFAULT_DATE);
        }
    }

    public Double getReformDesire() {
        return this.item.getVarAsDouble("reform_desire");
    }

    public void setReformDesire(Double reformDesire) {
        ClausewitzVariable reformDesireVar = this.item.getVar("reform_desire");

        if (reformDesireVar != null) {
            reformDesireVar.setValue(reformDesire);
        } else {
            this.item.addVariable("reform_desire", reformDesire);
        }
    }

    public String getController() {
        return this.item.getVarAsString("controller");
    }

    public void setController(String controller) {
        ClausewitzVariable controllerVar = this.item.getVar("controller");

        if (controllerVar != null) {
            controllerVar.setValue(controller);
        } else {
            this.item.addVariable("controller", controller);
        }
    }

    public String getPreviousController() {
        return this.item.getVarAsString("previous_controller");
    }

    public void setPreviousController(String previousController) {
        ClausewitzVariable previousControllerVar = this.item.getVar("previous_controller");

        if (previousControllerVar != null) {
            previousControllerVar.setValue(previousController);
        } else {
            this.item.addVariable("previous_controller", previousController);
        }
    }

    public Date getLastExcommunication() {
        Date date = this.item.getVarAsDate("last_excom");

        if (date == null || Eu4Utils.DEFAULT_DATE.equals(date)) {
            return null;
        }

        return date;
    }

    public void setLastExcommunication(Date lastExcommunication) {
        ClausewitzVariable lastExcommunicationVar = this.item.getVar("last_excom");

        if (lastExcommunicationVar != null) {
            lastExcommunicationVar.setValue(lastExcommunication);
        } else {
            this.item.addVariable("last_excom", lastExcommunication);
        }
    }

    public Boolean getPapacyActive() {
        return this.item.getVarAsBool("papacy_active");
    }

    public void setPapacyActive(boolean papacyActive) {
        ClausewitzVariable papacyActiveVar = this.item.getVar("papacy_active");

        if (papacyActiveVar != null) {
            papacyActiveVar.setValue(papacyActive);
        } else {
            this.item.addVariable("papacy_active", papacyActive);
        }
    }

    public Double getPapalInvestment() {
        return this.item.getVarAsDouble("papal_investment");
    }

    public void setPapalInvestment(Double papalInvestment) {
        ClausewitzVariable papalInvestmentVar = this.item.getVar("papal_investment");

        if (papalInvestmentVar != null) {
            papalInvestmentVar.setValue(papalInvestment);
        } else {
            this.item.addVariable("papal_investment", papalInvestment);
        }
    }

    public Map<Integer, Integer> getInvestInCardinals() {
        ClausewitzItem investItem = this.item.getChild("invest_in_cardinal");

        if (investItem == null) {
            return new HashMap<>();
        }

        return investItem.getVariables()
                         .stream()
                         .collect(Collectors.toMap(var -> Integer.parseInt(var.getName()), ClausewitzVariable::getAsInt));
    }

    public void investInCardinals(Integer id, Integer value) {
        ClausewitzItem investItem = this.item.getChild("invest_in_cardinal");

        if (investItem == null) {
            investItem = this.item.addChild("papal_investment");
        }

        ClausewitzVariable var = investItem.getVar(id);

        if (var != null) {
            var.setValue(value);
        } else {
            investItem.addVariable(id.toString(), value);
        }
    }

    public List<Cardinal> getCardinals() {
        return cardinals;
    }

    public void addCardinal(Integer provinceId) {
        ClausewitzItem activeCardinalsItem = this.item.getChild("active_cardinals");

        if (activeCardinalsItem != null) {
            Long id = getCardinals().stream()
                                    .map(Cardinal::getId)
                                    .map(Id::getId)
                                    .max(Long::compareTo)
                                    .orElse((long) new Random().nextInt(90000));
            Cardinal.addToItem(activeCardinalsItem, id, provinceId);
        }

        refreshAttributes();
    }

    public void removeCardinal(Integer index) {
        ClausewitzItem activeCardinalsItem = this.item.getChild("active_cardinals");

        if (activeCardinalsItem != null) {
            activeCardinalsItem.removeChild("cardinal", index);
        }

        refreshAttributes();
    }

    public Map<Colony, String> getColonyClaims() {
        if (this.coloniesClaims == null) {
            return new LinkedHashMap<>();
        }

        return this.coloniesClaims.getColonyClaims();
    }

    public String getColonyClaim(Colony colony) {
        if (this.coloniesClaims == null) {
            return null;
        }

        return this.coloniesClaims.getColonyClaim(colony);
    }

    public void setColonyClaim(Colony colony, String tag) {
        if (this.coloniesClaims != null) {
            this.coloniesClaims.setColonyClaim(colony, tag);
        }
    }

    public void removeColonyClaim(Colony colony) {
        if (this.coloniesClaims != null) {
            this.coloniesClaims.removeColonyClaim(colony);
        }
    }

    private void refreshAttributes() {
        ClausewitzItem activeCardinalsItem = this.item.getChild("active_cardinals");

        if (activeCardinalsItem != null) {
            List<ClausewitzItem> cardinalsItems = activeCardinalsItem.getChildren("cardinal");
            this.cardinals = cardinalsItems.stream().map(Cardinal::new).collect(Collectors.toList());
        }

        ClausewitzList claimsList = this.item.getList("colony_claim");

        if (claimsList != null) {
            this.coloniesClaims = new ColoniesClaims(claimsList);
        }
    }
}
