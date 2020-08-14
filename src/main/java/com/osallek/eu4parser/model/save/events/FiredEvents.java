package com.osallek.eu4parser.model.save.events;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import com.osallek.eu4parser.model.game.Event;
import com.osallek.eu4parser.model.game.Game;
import com.osallek.eu4parser.model.save.Id;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FiredEvents {

    private final ClausewitzItem item;

    private final Game game;

    public FiredEvents(ClausewitzItem item, Game game) {
        this.item = item;
        this.game = game;
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

    public List<Event> getEvents() {
        return this.item.getVariables()
                        .stream()
                        .map(var -> this.game.getEvent(ClausewitzUtils.removeQuotes(var.getValue())))
                        .collect(Collectors.toList());
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
        List<String> eventIds = events.stream().map(Event::getId).collect(Collectors.toList());

        this.item.removeVariableIf(var -> !eventIds.contains(var.getValue()));
        events.removeIf(event -> this.item.hasVar(event.getId()));
        events.forEach(this::addFiredEvent);
    }
}
