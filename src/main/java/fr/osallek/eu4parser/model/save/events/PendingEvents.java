package fr.osallek.eu4parser.model.save.events;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.List;

public class PendingEvents {

    private final ClausewitzItem item;

    public PendingEvents(ClausewitzItem item) {
        this.item = item;
    }

    public List<PendingEvent> getPendingEvents() {
        return this.item.getChildren().stream().map(PendingEvent::new).toList();
    }

    public void removePendingEvent(String pendingEvent) {
        this.item.removeChild(pendingEvent);
    }

    public void removePendingEvent(int index) {
        this.item.removeChild(index);
    }
}
