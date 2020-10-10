package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.clausewitzparser.model.ClausewitzObject;
import com.osallek.eu4parser.model.game.Game;
import com.osallek.eu4parser.model.game.Mission;
import com.osallek.eu4parser.model.game.MissionTree;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Missions {

    private final Game game;

    private final ClausewitzItem item;

    public Missions(ClausewitzItem item, Game game) {
        this.game = game;
        this.item = item;
    }

    public List<MissionTree> getMissionTrees() {
        List<ClausewitzList> missions = this.item.getLists("mission_slot");

        return missions.stream()
                       .filter(Predicate.not(ClausewitzList::isEmpty))
                       .map(list -> list.get(0))
                       .map(this.game::getMissionTree)
                       .collect(Collectors.toList());
    }

    public List<Mission> getMissions() {
        return getMissionTrees().stream().map(MissionTree::getMissions).map(Map::values).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public MissionTree getMissionTree(int slot) {
        if (slot < 1 || slot > 5) {
            return null;
        }

        slot--;

        List<ClausewitzObject> missions = this.item.getAllOrdered("mission_slot");

        if (missions.size() > slot) {
            if (missions.get(slot) instanceof ClausewitzList) {
                return this.game.getMissionTree(((ClausewitzList) missions.get(slot)).get(0));
            }
        }

        return null;
    }

    public void setMissions(MissionTree missions, int slot) {
        if (slot < 1 || slot > 5) {
            return;
        }

        slot--;

        List<ClausewitzObject> missionsObjects = this.item.getAllOrdered("mission_slot");

        if (missionsObjects.size() > slot) {
            if (missionsObjects.get(slot) instanceof ClausewitzList) {
                ((ClausewitzList) missionsObjects.get(slot)).remove(0);
                ((ClausewitzList) missionsObjects.get(slot)).add(missions.getName());
            } else if (missionsObjects.get(slot) instanceof ClausewitzItem) {
                this.item.changeChildToList(slot, "mission_slot", missions.getName());
            }
        }
    }
}
