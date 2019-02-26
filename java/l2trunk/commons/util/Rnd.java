package l2trunk.commons.util;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public final class Rnd {
    private static final ThreadLocal<RandomGenerator> rnd = new ThreadLocalGeneratorHolder();
    private static final AtomicLong seedUniquifier = new AtomicLong(8682522807148012L);

    private Rnd() {
    }

    private static RandomGenerator rnd() {
        return rnd.get();
    }

    public static double get() {
        return rnd().nextDouble();
    }

    public static int get(int n) {
        return rnd().nextInt(n);
    }

    public static int get(int min, int max) {
        return min + get(max - min + 1);
    }

    public static long get(long min, long max) {
        return min + (long) rnd().nextDouble() * (max - min + 1);
    }

    public static int nextInt() {
        return rnd().nextInt();
    }

    public static float nextFloat() {
        return rnd().nextFloat();
    }

    public static boolean chance(int chance) {
        return chance >= 1 && (chance > 99 || rnd().nextInt(99) + 1 <= chance);
    }

    public static boolean chance(double chance) {
        return rnd().nextDouble() <= chance / 100.;
    }

    public static <E> E get(E[] list) {
        if (list.length == 0)
            return null;

        return list[get(list.length)];
    }

    public static <E> E get(Collection<E> list) {
        if (list.size() == 0)
            throw new IllegalArgumentException("can't get random from emprty list: ");

        return list.stream()
                .skip((int) (list.size() * nextFloat()))
                .findFirst().orElseGet(() -> list.iterator().next());
    }

    private static final class ThreadLocalGeneratorHolder extends ThreadLocal<RandomGenerator> {
        @Override
        public RandomGenerator initialValue() {
            return new MersenneTwister(seedUniquifier.getAndIncrement() + System.nanoTime());
        }
    }
}