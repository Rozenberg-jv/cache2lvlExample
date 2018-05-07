package by.kolbun.andersen.interfaces;

import java.util.Set;

/**
 * Интерфейс используется алгоритмами вытеснения,
 * для подсчёта количества (частоты) вызовов объекта в кеше.
 *
 * @param <Key> - тип ключа объектов в кеше
 */

public interface CallObjectCounter<Key> {

    /**
     * @return Set ключей, отсортированный по количеству вызовов
     */
    Set<Key> getSortedCalledKeysSet();

    /**
     * Возвращает количество вызовов объекта по ключу
     */
    int getNumberOfCallsToObject(Key key);
}
