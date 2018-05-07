package by.kolbun.andersen.interfaces;

public interface Cache<Key, Value> {
    void cache(Key k, Value v);

    Value getObject(Key k);

    void deleteObject(Key k);

    void clearCache();

    Value removeObject(Key k);

    boolean containsKey(Key k);

    int size();
}
