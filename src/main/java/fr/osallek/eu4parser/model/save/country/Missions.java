package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.clausewitzparser.model.ClausewitzObject;
import fr.osallek.eu4parser.model.game.Game;
import fr.osallek.eu4parser.model.game.Mission;
import fr.osallek.eu4parser.model.game.MissionsTree;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public record Missions(ClausewitzItem item, Game game) {

    public List<MissionsTree> getMissionsTrees() {
        List<ClausewitzList> missions = this.item.getLists("mission_slot");

        return missions.stream()
                       .filter(Predicate.not(ClausewitzList::isEmpty))
                       .map(list -> list.get(0))
                       .map(this.game::getMissionsTree)
                       .toList();
    }

    public List<Mission> getMissions() {
        return getMissionsTrees().stream().map(MissionsTree::getMissions).flatMap(Collection::stream).toList();
    }

    public MissionsTree getMissionsTree(int slot) {
        if (slot < 1 || slot > 5) {
            return null;
        }

        slot--;

        List<ClausewitzObject> missions = this.item.getAllOrdered("mission_slot");

        if (missions.size() > slot) {
            if (missions.get(slot) instanceof ClausewitzList list) {
                return this.game.getMissionsTree((list).get(0));
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
