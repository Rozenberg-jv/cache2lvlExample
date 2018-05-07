package by.kolbun.andersen;

import by.kolbun.andersen.cacheImpl.TwoLevelCache;
import by.kolbun.andersen.obj.CachableObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class Main {
    private final static int MAX_RAM_CACHE_CAPACITY = 10;
    private final static int REQUESTS_FOR_CACHE = 3;

    public static void main(String[] args) {
        TwoLevelCache twoLevelCache = new TwoLevelCache(MAX_RAM_CACHE_CAPACITY, REQUESTS_FOR_CACHE);

        /**
         * кэшируем 21 объект и обращаемся к каждому третьему
         */
        for (int i = 0; i < 21; i++) {
            twoLevelCache.cache("" + i, new CachableObject("object#" + i, i));
            if (i % 3 == 0) {
                System.out.println(twoLevelCache.getObject("" + (i - 2)));
                testCacheSize(twoLevelCache);
            }
        }

        /**
         * обращаемся еще 50 раз к случайным объектам
         */
        System.out.println("getting 50 random objects...");
        Random r = new Random();
        int ri = 0;
        for (int i = 0; i < 50; i++) {
            ri = r.nextInt(21);
            CachableObject v = (CachableObject) twoLevelCache.getObject(ri + "");
            System.out.println(v);
        }

        String[] input;
        printLegend();

        try (BufferedReader rdr = new BufferedReader(new InputStreamReader(System.in))) {
            input = rdr.readLine().split(" ");

            while (!"e".equals(input[0])) {
                switch (input[0]) {
                    case "g":
                        System.out.println(twoLevelCache.getObject(input[1]) + " : "
                                + twoLevelCache.getNumberOfCallsToObject(input[1]));
                        testCacheSize(twoLevelCache);
                        break;
                    case "recache":
                        twoLevelCache.recache();
                        System.out.println("...recached");
                        break;
                    case "e":
                        System.out.println("Bye!");
                        break;
                    case "calls":
                        printCallsTable(twoLevelCache);
                        break;
                    default:
                        System.out.println("Bad input");
                        break;
                }
                input = rdr.readLine().split(" ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(" - end -");

        twoLevelCache.clearCache();
    }

    private static void printCallsTable(TwoLevelCache cache) {
        for (int i = 0; i < 21; i++)
            System.out.println(i + " : " + cache.getNumberOfCallsToObject(i + ""));
    }

    private static void printLegend() {
        System.out.println("\n\"g #\" - get object #" +
                "\ne - to exit" +
                "\nrecache - do recache" +
                "\ncalls - print number of calls table" +
                "\n");
    }

    public static void testCacheSize(TwoLevelCache cache) {
        System.out.println("------");
        System.out.println("Ram: " + cache.ramSize());
        System.out.println("Memory: " + cache.memorySize());
        System.out.println("------");
    }
}
