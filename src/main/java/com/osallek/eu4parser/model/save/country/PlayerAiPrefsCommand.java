package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.model.ClausewitzItem;

public class PlayerAiPrefsCommand {

    private final ClausewitzItem item;

    public PlayerAiPrefsCommand(ClausewitzItem item) {
        this.item = item;
    }

    public boolean startWars() {
        return this.item.getVarAsBool("start_wars");
    }

    public void setStartWars(boolean startWars) {
        this.item.setVariable("start_wars", startWars);
    }

    public boolean keepAlliances() {
        return this.item.getVarAsBool("keep_alliances");
    }

    public void setKeepAlliances(boolean keepAlliances) {
        this.item.setVariable("keep_alliances", keepAlliances);
    }

    public boolean keepTreaties() {
        return this.item.getVarAsBool("keep_treaties");
    }

    public void setKeepTreaties(boolean keepTreaties) {
        this.item.setVariable("keep_treaties", keepTreaties);
    }

    public boolean quickPeace() {
        return this.item.getVarAsBool("quick_peace");
    }

    public void setQuickPeace(boolean quickPeace) {
        this.item.setVariable("quick_peace", quickPeace);
    }

    public boolean moveTraders() {
        return this.item.getVarAsBool("move_traders");
    }

    public void setMoveTraders(boolean moveTraders) {
        this.item.setVariable("move_traders", moveTraders);
    }

    public boolean takeDecisions() {
        return this.item.getVarAsBool("take_decisions");
    }

    public void setTakeDecisions(boolean takeDecisions) {
        this.item.setVariable("take_decisions", takeDecisions);
    }

    public boolean embraceInstitutions() {
        return this.item.getVarAsBool("embrace_institutions");
    }

    public void setEmbraceInstitutions(boolean embraceInstitutions) {
        this.item.setVariable("embrace_institutions", embraceInstitutions);
    }

    public boolean developProvinces() {
        return this.item.getVarAsBool("develop_provinces");
    }

    public void setDevelopProvinces(boolean developProvinces) {
        this.item.setVariable("develop_provinces", developProvinces);
    }

    public boolean disbandUnits() {
        return this.item.getVarAsBool("disband_units");
    }

    public void setDisbandUnits(boolean disbandUnits) {
        this.item.setVariable("disband_units", disbandUnits);
    }

    public boolean changeFleetMissions() {
        return this.item.getVarAsBool("change_fleet_missions");
    }

    public void setChangeFleetMissions(boolean changeFleetMissions) {
        this.item.setVariable("change_fleet_missions", changeFleetMissions);
    }

    public boolean sendMissionaries() {
        return this.item.getVarAsBool("send_missionaries");
    }

    public void setSendMissionaries(boolean sendMissionaries) {
        this.item.setVariable("send_missionaries", sendMissionaries);
    }

    public boolean convertCultures() {
        return this.item.getVarAsBool("convert_cultures");
    }

    public void setConvertCultures(boolean convertCultures) {
        this.item.setVariable("convert_cultures", convertCultures);
    }

    public boolean promoteCultures() {
        return this.item.getVarAsBool("promote_cultures");
    }

    public void setPromoteCultures(boolean promoteCultures) {
        this.item.setVariable("promote_cultures", promoteCultures);
    }

    public boolean braindead() {
        return this.item.getVarAsBool("braindead");
    }

    public void setBraindead(boolean braindead) {
        this.item.setVariable("braindead", braindead);
    }

    public int timeout() {
        return this.item.getVarAsInt("timeout");
    }

    public void setTimeout(int timeout) {
        this.item.setVariable("timeout", timeout);
    }

    public void setDontTimeout() {
        setTimeout(-1);
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, boolean startWars, boolean keepAlliances,
                                           boolean keepTreaties, boolean quickPeace, boolean moveTraders,
                                           boolean takeDecisions, boolean embraceInstitutions, boolean developProvinces,
                                           boolean disbandUnits, boolean changeFleetMissions, boolean sendMissionaries,
                                           boolean convertCultures, boolean promoteCultures, boolean braindead, int timeout) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "player_ai_prefs_command", parent.getOrder() + 1);
        PlayerAiPrefsCommand playerAiPrefsCommand = new PlayerAiPrefsCommand(toItem);
        playerAiPrefsCommand.setStartWars(startWars);
        playerAiPrefsCommand.setKeepAlliances(keepAlliances);
        playerAiPrefsCommand.setKeepTreaties(keepTreaties);
        playerAiPrefsCommand.setQuickPeace(quickPeace);
        playerAiPrefsCommand.setMoveTraders(moveTraders);
        playerAiPrefsCommand.setTakeDecisions(takeDecisions);
        playerAiPrefsCommand.setEmbraceInstitutions(embraceInstitutions);
        playerAiPrefsCommand.setDevelopProvinces(developProvinces);
        playerAiPrefsCommand.setDisbandUnits(disbandUnits);
        playerAiPrefsCommand.setChangeFleetMissions(changeFleetMissions);
        playerAiPrefsCommand.setSendMissionaries(sendMissionaries);
        playerAiPrefsCommand.setConvertCultures(convertCultures);
        playerAiPrefsCommand.setPromoteCultures(promoteCultures);
        playerAiPrefsCommand.setBraindead(braindead);
        playerAiPrefsCommand.setTimeout(timeout);

        parent.addChild(toItem);

        return toItem;
    }
}
