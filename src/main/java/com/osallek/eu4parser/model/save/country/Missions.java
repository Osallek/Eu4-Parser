package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.clausewitzparser.model.ClausewitzObject;

import java.util.List;

public class Missions {

    private final ClausewitzItem item;

    public Missions(ClausewitzItem item) {
        this.item = item;
    }

    public String getMissions(int slot) {
        if (slot < 1 || slot > 5) {
            return null;
        }

        slot--;

        List<ClausewitzObject> missions = this.item.getAllOrdered("mission_slot");

        if (missions.size() > slot) {
            if (missions.get(slot) instanceof ClausewitzList) {
                return ((ClausewitzList) missions.get(slot)).get(0);
            }
        }

        return null;
    }

    public void setMissions(String missions, int slot) {
        if (slot < 1 || slot > 5) {
            return;
        }

        slot--;

        List<ClausewitzObject> missionsObjects = this.item.getAllOrdered("mission_slot");

        if (missionsObjects.size() > slot) {
            if (missionsObjects.get(slot) instanceof ClausewitzList) {
                ((ClausewitzList) missionsObjects.get(slot)).remove(0);
                ((ClausewitzList) missionsObjects.get(slot)).add(missions);
            } else if (missionsObjects.get(slot) instanceof ClausewitzItem) {
                this.item.changeChildToList(slot, "mission_slot", missions);
            }
        }
    }
}
