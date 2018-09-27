package ai;

import java.util.LinkedHashMap;
import java.util.Map;

public class TranspositionTable extends LinkedHashMap<Long,Integer> {

    private static final int MAX_ENTRIES = 10000000;

    protected boolean removeEldestEntry(Map.Entry eldest) {
        return size() > MAX_ENTRIES;
    }
}