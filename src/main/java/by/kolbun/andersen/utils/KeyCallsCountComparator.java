package by.kolbun.andersen.utils;

import java.util.Comparator;
import java.util.Map;

public class KeyCallsCountComparator implements Comparator {

    private Map base;

    public KeyCallsCountComparator(Map base) {
        this.base = base;
    }

    @Override
    public int compare(Object o1, Object o2) {
        if ((Integer) base.get(o1) < (Integer) base.get(o2) || base.get(o1) == base.get(o2)) {
            return 1;
        } else {
            return -1;
        }
    }

}
