package com.osallek.eu4parser.model.events;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import com.osallek.eu4parser.model.Id;

import java.util.ArrayList;
import java.util.List;

public class FiredEvents {

    private final ClausewitzItem item;

    public FiredEvents(ClausewitzItem item) {
        this.item = item;
    }

    public List<String> getFiredEvents() {
        List<String> events = new ArrayList<>();
        this.item.getAllOrdered().forEach(clausewitzObject -> {
            if (clausewitzObject instanceof ClausewitzItem) {
                events.add(String.valueOf(new Id((ClausewitzItem) clausewitzObject).getId()));
            } else if (clausewitzObject instanceof ClausewitzVariable) {
                events.add(((ClausewitzVariable) clausewitzObject).getValue());
            }
        });

        return events;
    }

    public void addFiredEvent(String firedEvent) {
        this.item.addVariable("id", firedEvent);
    }

    public void addFiredEvent(int id) {
        Id.addToItem(this.item, id, 53);
    }

    public void removeFiredEvent(String firedEvent) {
        this.item.removeVariableByValue(firedEvent);
    }

    public void removeFiredEvent(int index) {
        this.item.removeByOrder(index);
    }
}
