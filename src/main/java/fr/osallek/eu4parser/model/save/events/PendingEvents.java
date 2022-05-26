package fr.osallek.eu4parser.model.save.events;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.List;

public class PendingEvents {

    private final ClausewitzItem item;

    private List<PendingEvent> pendingEvents;

    public PendingEvents(ClausewitzItem item) {
        this.item = item;
        refreshAttributes();
    }

    public List<PendingEvent> getPendingEvents() {
        return this.pendingEvents;
    }

    public void removePendingEvent(String pendingEvent) {
        this.item.removeChild(pendingEvent);
        refreshAttributes();
    }

    public void removePendingEvent(int index) {
        this.item.removeChild(index);
        refreshAttributes();
    }

    private void refreshAttributes() {
        this.pendingEvents = this.item.getChildren().stream().map(PendingEvent::new).toList();
    }
}
