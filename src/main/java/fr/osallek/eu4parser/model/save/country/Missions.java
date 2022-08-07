package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.clausewitzparser.model.ClausewitzObject;
import fr.osallek.eu4parser.model.game.Game;
import fr.osallek.eu4parser.model.game.Mission;
import fr.osallek.eu4parser.model.game.MissionsTree;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public record Missions(ClausewitzItem item, Game game) {

    public Map<Integer, List<MissionsTree>> getMissionsTrees() {
        List<ClausewitzList> missions = this.item.getLists("mission_slot");
        Map<Integer, List<MissionsTree>> slots = new TreeMap<>();

        for (int i = 0; i < missions.size(); i++) {
            if (missions.get(i) != null && !missions.get(i).isEmpty()) {
                slots.put(i, missions.get(i).getValues().stream().map(this.game::getMissionsTree).filter(Objects::nonNull).toList());
            }
        }

        return slots;
    }

    public List<Mission> getMissions() {
        return getMissionsTrees().values().stream().flatMap(Collection::stream).map(MissionsTree::getMissions).flatMap(Collection::stream).toList();
    }

    public List<MissionsTree> getMissionsTrees(int slot) {
        if (slot < 1 || slot > 5) {
            return null;
        }

        slot--;

        List<ClausewitzObject> missions = this.item.getAllOrdered("mission_slot");

        if (missions.size() > slot) {
            if (missions.get(slot) instanceof ClausewitzList list) {
                return list.getValues().stream().map(this.game::getMissionsTree).filter(Objects::nonNull).toList();
            }
        }

        return null;
    }

    public void setMissions(MissionsTree missions, int slot) {
        if (slot < 1 || slot > 5) {
            return;
        }

        slot--;

        List<ClausewitzObject> missionsObjects = this.item.getAllOrdered("mission_slot");

        if (missionsObjects.size() > slot) {
            if (missionsObjects.get(slot) instanceof ClausewitzList list) {
                list.remove(0);
                list.add(missions.getName());
            } else if (missionsObjects.get(slot) instanceof ClausewitzItem) {
                this.item.changeChildToList(slot, "mission_slot", missions.getName());
            }
        }
    }
}
