package com.osallek.eu4parser.model.counters;

import com.osallek.clausewitzparser.model.ClausewitzList;

public class IdCounters {

    private final ClausewitzList list;

    public IdCounters(ClausewitzList list) {
        this.list = list;
    }

    public Long getCounter(Counter counter) {
        return this.list.getAsLong(counter.ordinal());
    }

    public void setCounter(Counter counter, Long value) {
        this.list.set(counter.ordinal(), value);
    }

    public void incrementCounter(Counter counter) {
        Long value = getCounter(counter);

        if (value == null) {
            this.list.set(counter.ordinal(), 1);
        } else {
            this.list.set(counter.ordinal(), value + 1);
        }
    }
}
