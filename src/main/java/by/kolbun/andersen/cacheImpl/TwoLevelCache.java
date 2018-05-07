package by.kolbun.andersen.cacheImpl;

import by.kolbun.andersen.interfaces.CacheLevel2;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

public class TwoLevelCache<K, V extends Serializable> implements CacheLevel2<K, V> {
    // 1 level
    private RamCache<K, V> ramCache;
    // 2 level
    private MemoryCache<K, V> memoryCache;

    private int maxRamCacheCapacity;

    private int numberOfRequests;

    private int requestsForRecache;

    public TwoLevelCache(int maxRamCacheCapacity, int requestsForRecache) {
        this.maxRamCacheCapacity = maxRamCacheCapacity;
        this.requestsForRecache = requestsForRecache;
        this.ramCache = new RamCache<>();
        this.memoryCache = new MemoryCache<>();
        this.numberOfRequests = 0;
    }

    @Override
    public void cache(K k, V v) {
        ramCache.cache(k, v);
    }

    @Override
    public V getObject(K k) {
        if (ramCache.containsKey(k)) {
            doRecache();
            return ramCache.getObject(k);
        }

        if (memoryCache.containsKey(k)) {
            doRecache();
            return memoryCache.getObject(k);
        }

        return null;
    }

    /**
     * Выполняется при каждом обращении к объекту, если он существует в кэше
     */
    private void doRecache() {
        if (++numberOfRequests >= requestsForRecache) {
            this.recache();
            numberOfRequests = 0;
        }
    }


    @Override
    public void deleteObject(K k) {
        if (ramCache.containsKey(k)) {
            ramCache.deleteObject(k);
        }
        if (memoryCache.containsKey(k)) {
            memoryCache.deleteObject(k);
        }
    }

    @Override
    public void clearCache() {
        memoryCache.clearCache();
        ramCache.clearCache();
    }

    @Override
    public V removeObject(K k) {
        if (ramCache.containsKey(k)) {
            return ramCache.removeObject(k);
        }
        if (memoryCache.containsKey(k)) {
            return memoryCache.removeObject(k);
        }
        return null;
    }

    @Override
    public boolean containsKey(K k) {
        if (ramCache.containsKey(k)) {
            return true;
        }
        return memoryCache.containsKey(k);
    }

    @Override
    public int size() {
        return ramCache.size() + memoryCache.size();
    }

    public int ramSize() {
        return ramCache.size();
    }

    public int memorySize() {
        return memoryCache.size();
    }

    /**
     * 1. При рекэшировании находится среднее арифметическое количества вызовов всех объекта
     * 2. Редко используемые объекты переносятся из оперативной памяти, на жесткий диск.
     * 3. И наоборот, все объекты которые часто используются, хранящиеся на жестком диске,
     * забрасываются в оперативную память.
     * <p>
     * Происходит постоянное перетасовывание объектов между двумя кэшами.
     */
    @Override
    public void recache() {
        TreeSet<K> ramKeySet = new TreeSet<>(ramCache.getSortedCalledKeysSet());
        int averageFrequency = 0;

        // 1.
        for (K k : ramKeySet)
            averageFrequency += ramCache.getNumberOfCallsToObject(k);
        averageFrequency /= ramKeySet.size();

        // 2.
        for (K k : ramKeySet)
            if (ramCache.getNumberOfCallsToObject(k) < averageFrequency)
                memoryCache.cache(k, ramCache.removeObject(k));

        // 3.
        TreeSet<K> memoryKeySet = new TreeSet<>(memoryCache.getSortedCalledKeysSet());
        for (K k : memoryKeySet)
            if (memoryCache.getNumberOfCallsToObject(k) > averageFrequency)
                ramCache.cache(k, memoryCache.removeObject(k));
    }

    @Override
    public Set<K> getSortedCalledKeysSet() {
        TreeSet<K> set = new TreeSet<>(ramCache.getSortedCalledKeysSet());
        set.addAll(memoryCache.getSortedCalledKeysSet());
        return set;
    }

    @Override
    public int getNumberOfCallsToObject(K k) {
        if (ramCache.containsKey(k)) {
            return ramCache.getNumberOfCallsToObject(k);
        }
        if (memoryCache.containsKey(k)) {
            return memoryCache.getNumberOfCallsToObject(k);
        }
        return 0;
    }
}
