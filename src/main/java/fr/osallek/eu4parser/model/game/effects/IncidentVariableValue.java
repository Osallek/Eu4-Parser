package fr.osallek.eu4parser.model.game.effects;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

//add_incident_variable_value, set_incident_variable_value
public class IncidentVariableValue {

    private final ClausewitzItem item;

    public IncidentVariableValue(ClausewitzItem item) {
        this.item = item;
    }

    public String getIncident() {
        return this.item.getVarAsString("incident");
    }

    public Integer getOpinion() {
        return this.item.getVarAsInt("opinion");
    }
}
