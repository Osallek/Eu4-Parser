package fr.osallek.eu4parser.model.save.counters;

import fr.osallek.clausewitzparser.model.ClausewitzList;

import java.util.Optional;

public record IdCounters(ClausewitzList list) {

    public Optional<Integer> getCounter(Counter counter) {
        return this.list.getAsInt(counter.ordinal());
    }

    public void setCounter(Counter counter, Integer value) {
        this.list.set(counter.ordinal(), value);
    }

    public void incrementCounter(Counter counter) {
        getCounter(counter).ifPresentOrElse(value -> this.list.set(counter.ordinal(), value + 1), () -> this.list.set(counter.ordinal(), 1));
    }

    public Integer getAndIncrement(Counter counter) {
        return getCounter(counter).map(value -> {
            this.list.set(counter.ordinal(), value + 1);

            return value;
        }).orElseGet(() -> {
            this.list.set(counter.ordinal(), 1);
            return 1;
        });
    }
}
