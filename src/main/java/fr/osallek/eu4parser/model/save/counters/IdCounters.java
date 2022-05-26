package fr.osallek.eu4parser.model.save.counters;

import fr.osallek.clausewitzparser.model.ClausewitzList;

public record IdCounters(ClausewitzList list) {

    public Integer getCounter(Counter counter) {
        return this.list.getAsInt(counter.ordinal());
    }

    public void setCounter(Counter counter, Integer value) {
        this.list.set(counter.ordinal(), value);
    }

    public void incrementCounter(Counter counter) {
        Integer value = getCounter(counter);

        if (value == null) {
            this.list.set(counter.ordinal(), 1);
        } else {
            this.list.set(counter.ordinal(), value + 1);
        }
    }

    public Integer getAndIncrement(Counter counter) {
        Integer value = getCounter(counter);

        if (value == null) {
            this.list.set(counter.ordinal(), 1);
        } else {
            this.list.set(counter.ordinal(), value + 1);
        }

        return value;
    }
}
