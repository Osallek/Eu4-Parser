package fr.osallek.eu4parser.model.save.events;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;
import fr.osallek.eu4parser.model.game.Event;
import fr.osallek.eu4parser.model.game.Game;
import fr.osallek.eu4parser.model.save.Id;

import java.util.ArrayList;
import java.util.List;

public record FiredEvents(ClausewitzItem item, Game game) {

    public List<String> getFiredEvents() {
        List<String> events = new ArrayList<>();
        this.item.getAllOrdered().forEach(clausewitzObject -> {
            if (clausewitzObject instanceof ClausewitzItem item) {
                events.add(String.valueOf(new Id(item).getId()));
            } else if (clausewitzObject instanceof ClausewitzVariable variable) {
                events.add((variable).getValue());
            }
        });

        return events;
    }

    public List<Event> getEvents() {
        return this.item.getVariables()
                        .stream()
                        .map(variable -> this.game.getEvent(ClausewitzUtils.removeQuotes(variable.getValue())))
                        .toList();
    }

    public void addFiredEvent(Event firedEvent) {
        addFiredEvent(firedEvent.getId());
    }

    public void addFiredEvent(String firedEvent) {
        this.item.addVariable("id", ClausewitzUtils.addQuotes(firedEvent));
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

    public void setEvents(List<Event> events) {
        List<String> eventIds = events.stream().map(Event::getId).toList();

        this.item.removeVariableIf(variable -> !eventIds.contains(variable.getValue()));
        events.removeIf(event -> this.item.hasVar(event.getId()));
        events.forEach(this::addFiredEvent);
    }
}
