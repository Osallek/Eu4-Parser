package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Objects;

public class EventModifier extends GameModifier {

    private final String picture;

    public EventModifier(ClausewitzItem item) {
        super(item, new Modifiers(item.getVarsNot("picture")));
        this.picture = item.getVarAsString("picture ");
    }

    public String getPicture() {
        return picture;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof EventModifier)) {
            return false;
        }

        EventModifier that = (EventModifier) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
