package fr.osallek.eu4parser.model.game;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum EventType {
    COUNTRY("country_event"),
    PROVINCE("province_event");

    public final String type;

    EventType(String type) {
        this.type = type;
    }

    private static final Map<String, EventType> BY_TYPE = new HashMap<>();

    static {
        Arrays.stream(values()).forEach(eventType -> BY_TYPE.put(eventType.type, eventType));
    }

    public static EventType getByType(String type) {
        return BY_TYPE.get(type);
    }
}
