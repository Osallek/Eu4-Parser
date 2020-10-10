package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Mission {

    private final String name;

    private String localizedName;

    private final String icon;

    private final boolean generic;

    private final Integer position;

    private final Date completedBy;

    private final Condition provincesToHighlight;

    private final Condition trigger;

    private final List<String> requiredMissionsNames;

    private List<Mission> requiredMissions;

    public Mission(ClausewitzItem item) {
        this.name = item.getName();
        this.icon = item.getVarAsString("icon");
        this.generic = BooleanUtils.toBoolean(item.getVarAsBool("generic"));
        this.position = item.getVarAsInt("position");
        this.completedBy = item.getVarAsDate("completed_by");

        ClausewitzItem child = item.getChild("provinces_to_highlight");
        this.provincesToHighlight = child == null ? null : new Condition(child);

        child = item.getChild("trigger");
        this.trigger = child == null ? null : new Condition(child);

        ClausewitzList list = item.getList("required_missions");
        this.requiredMissionsNames = list == null ? null : list.getValues();
    }

    public String getName() {
        return name;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    public void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public String getIcon() {
        return icon;
    }

    public boolean isGeneric() {
        return generic;
    }

    public Integer getPosition() {
        return position;
    }

    public Date getCompletedBy() {
        return completedBy;
    }

    public Condition getProvincesToHighlight() {
        return provincesToHighlight;
    }

    public Condition getTrigger() {
        return trigger;
    }

    public List<Mission> getRequiredMissions() {
        return requiredMissions;
    }

    void setRequiredMissions(Game game) {
        if (CollectionUtils.isNotEmpty(requiredMissionsNames)) {
            this.requiredMissions = this.requiredMissionsNames.stream().map(game::getMission).collect(Collectors.toList());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Mission)) {
            return false;
        }

        Mission area = (Mission) o;

        return Objects.equals(name, area.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
