package by.kolbun.andersen.interfaces;

/**
 * Интерфейс является абстракцией двухуровневого кеша.
 * Метод recache() используется для перехода объекта с одного уровня кеша к другому.
 */
public interface CacheLevel2<Key, Value> extends Cache<Key, Value>, CallObjectCounter<Key> {

    void recache();
}
