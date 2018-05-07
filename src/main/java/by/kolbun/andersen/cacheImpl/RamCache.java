package by.kolbun.andersen.cacheImpl;

import by.kolbun.andersen.interfaces.Cache;
import by.kolbun.andersen.interfaces.CallObjectCounter;
import by.kolbun.andersen.utils.KeyCallsCountComparator;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

/**
 * Первый уровень кеша (оперативная память).
 * В основе лежит HashMap.
 */
public class RamCache<K, V> implements Cache<K, V>, CallObjectCounter<K> {

    // Кэш
    private HashMap<K, V> cache;

    // Счетчик вызовов
    private TreeMap<K, Integer> keyCallsCounter;

    public RamCache() {
        cache = new HashMap<>();
        keyCallsCounter = new TreeMap<>();
    }

    @Override
    public void cache(K k, V v) {
        keyCallsCounter.put(k, 1);
        cache.put(k, v);
    }

    @Override
    public V getObject(K k) {
        if (cache.containsKey(k)) {
            int frequency = keyCallsCounter.get(k);
            keyCallsCounter.put(k, ++frequency);
            return cache.get(k);
        }
        return null;
    }

    @Override
    public void deleteObject(K k) {
        if (cache.containsKey(k)) {
            cache.remove(k);
            keyCallsCounter.remove(k);
        }
    }

    @Override
    public void clearCache() {
        cache.clear();
        keyCallsCounter.clear();
    }

    @Override
    public V removeObject(K k) {
        if (cache.containsKey(k)) {
            V result = this.getObject(k);
            this.deleteObject(k);
            return result;
        }
        return null;
    }

    @Override
    public boolean containsKey(K k) {
        return cache.containsKey(k);
    }

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<K> getSortedCalledKeysSet() {
        TreeMap<K, Integer> sorted = new TreeMap<>(new KeyCallsCountComparator(keyCallsCounter));
        sorted.putAll(keyCallsCounter);
        return sorted.keySet();
    }

    @Override
    public int getNumberOfCallsToObject(K key) {
        if (cache.containsKey(key)) {
            return keyCallsCounter.get(key);
        }
        return 0;
    }
}
