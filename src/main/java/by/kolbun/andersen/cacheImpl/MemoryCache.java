package by.kolbun.andersen.cacheImpl;

import by.kolbun.andersen.interfaces.Cache;
import by.kolbun.andersen.interfaces.CallObjectCounter;
import by.kolbun.andersen.utils.KeyCallsCountComparator;

import java.io.*;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

public class MemoryCache<K, V> implements Cache<K, V>, CallObjectCounter<K> {

    HashMap<K, String> cache;
    TreeMap<K, Integer> keyCallsCounter;
    String cacheFolderPath = "cache";

    public MemoryCache() {
        cache = new HashMap<>();
        keyCallsCounter = new TreeMap();

        File tempFolder = new File("/" + cacheFolderPath + "/");
        if (!tempFolder.exists()) {
            tempFolder.mkdirs();
        }
    }

    @Override
    public void cache(K k, V v) {
        String pathToObject = "/" + cacheFolderPath + "/" + UUID.randomUUID() + ".ch2";
        keyCallsCounter.put(k, 1);
        cache.put(k, pathToObject);

        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(pathToObject))) {
            objectOutputStream.writeObject(v);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 1. Получаем по ключу адрес файла на жестком диске из hashMap
     * 2. Читаем его
     * 3. Десериализуем
     * 4. Увеличиваем на один частоту вызова
     * 5. Очищаем потоки - само, try-with-res
     */
    @Override
    public V getObject(K k) {
        V deserializedObject = null;
        // 1
        String pathToFile = cache.get(k);

        // 2
        try (ObjectInputStream objectStream = new ObjectInputStream(new FileInputStream(pathToFile))) {
            // 3
            deserializedObject = (V) objectStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            deleteObject(k);
            e.printStackTrace();
        }
        // 4
        int frequency = keyCallsCounter.remove(k);
        keyCallsCounter.put(k, ++frequency);

        return deserializedObject;
    }

    @Override
    public void deleteObject(K k) {
        if (cache.containsKey(k)) {
            File deletingFile = new File(cache.remove(k));
            keyCallsCounter.remove(k);
            deletingFile.delete();
        }
    }

    @Override
    public void clearCache() {
        for (K key : cache.keySet()) {
            File deletingFile = new File(cache.get(key));
            deletingFile.delete();
        }

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
    public Set<K> getSortedCalledKeysSet() {
        KeyCallsCountComparator comparator = new KeyCallsCountComparator(keyCallsCounter);
        TreeMap<K, Integer> sorted = new TreeMap<>(comparator);
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
