package dev.whoteva.netheroverhaul.worldgen;

/**
 * Deterministic value-noise used by the overhauled Nether generator.
 * Hash-based so chunk order never changes the terrain.
 */
public final class NetherNoise {

    private final long seed;

    public NetherNoise(long seed) {
        this.seed = seed;
    }

    public double fbm2(double x, double z, int salt, int octaves) {
        double sum = 0.0;
        double amp = 1.0;
        double freq = 1.0;
        double norm = 0.0;
        for (int i = 0; i < octaves; i++) {
            sum += noise2(x * freq, z * freq, salt + i * 19) * amp;
            norm += amp;
            amp *= 0.5;
            freq *= 2.0;
        }
        return sum / norm;
    }

    public double noise2(double x, double z, int salt) {
        int x0 = floor(x);
        int z0 = floor(z);
        double fx = fade(x - x0);
        double fz = fade(z - z0);
        double n00 = hash(x0, z0, salt);
        double n10 = hash(x0 + 1, z0, salt);
        double n01 = hash(x0, z0 + 1, salt);
        double n11 = hash(x0 + 1, z0 + 1, salt);
        return lerp(fz, lerp(fx, n00, n10), lerp(fx, n01, n11));
    }

    public double noise3(double x, double y, double z, int salt) {
        int x0 = floor(x);
        int y0 = floor(y);
        int z0 = floor(z);
        double fx = fade(x - x0);
        double fy = fade(y - y0);
        double fz = fade(z - z0);
        double n000 = hash3(x0, y0, z0, salt);
        double n100 = hash3(x0 + 1, y0, z0, salt);
        double n010 = hash3(x0, y0 + 1, z0, salt);
        double n110 = hash3(x0 + 1, y0 + 1, z0, salt);
        double n001 = hash3(x0, y0, z0 + 1, salt);
        double n101 = hash3(x0 + 1, y0, z0 + 1, salt);
        double n011 = hash3(x0, y0 + 1, z0 + 1, salt);
        double n111 = hash3(x0 + 1, y0 + 1, z0 + 1, salt);
        double nx00 = lerp(fx, n000, n100);
        double nx10 = lerp(fx, n010, n110);
        double nx01 = lerp(fx, n001, n101);
        double nx11 = lerp(fx, n011, n111);
        return lerp(fz, lerp(fy, nx00, nx10), lerp(fy, nx01, nx11));
    }

    public int floorHeight(int x, int z) {
        double rolling = fbm2(x / 96.0, z / 96.0, 11, 5);
        double ridges = Math.abs(fbm2(x / 48.0, z / 48.0, 29, 3));
        return 56 + (int) Math.round(rolling * 36.0 + ridges * 18.0);
    }

    public int roofHeight(int x, int z) {
        double n = fbm2(x / 80.0, z / 80.0, 47, 4);
        return 214 + (int) Math.round(n * 18.0);
    }

    public boolean pillar(int x, int z) {
        return noise2(x / 14.0, z / 14.0, 73) > 0.62;
    }

    public boolean ravine(int x, int y, int z) {
        double ridge = Math.abs(noise3(x / 42.0, y / 28.0, z / 42.0, 101));
        return ridge < 0.07 && y > 18 && y < 190;
    }

    public double cave(int x, int y, int z) {
        return noise3(x / 22.0, y / 16.0, z / 22.0, 131);
    }

    public int biomeIndex(int biomeX, int biomeZ, int count) {
        double t = fbm2(biomeX / 18.0, biomeZ / 18.0, 151, 3);
        double h = fbm2(biomeX / 18.0, biomeZ / 18.0, 173, 3);
        int ix = (int) Math.floor((t + 1.0) * 0.5 * 4.0);
        int iz = (int) Math.floor((h + 1.0) * 0.5 * 2.0);
        int index = Math.floorMod(ix + iz * 4, count);
        return Math.max(0, Math.min(count - 1, index));
    }

    public long hashPos(int x, int y, int z) {
        long h = seed;
        h = 31 * h + x;
        h = 31 * h + y;
        h = 31 * h + z;
        h ^= (h >>> 30);
        h *= 0xbf58476d1ce4e5b9L;
        h ^= (h >>> 27);
        h *= 0x94d049bb133111ebL;
        h ^= (h >>> 31);
        return h;
    }

    private double hash(int x, int z, int salt) {
        long h = hashPos(x, salt, z);
        return ((h & 0xFFFFFFL) / (double) 0xFFFFFFL) * 2.0 - 1.0;
    }

    private double hash3(int x, int y, int z, int salt) {
        long h = hashPos(x, y + salt * 17, z);
        return ((h & 0xFFFFFFL) / (double) 0xFFFFFFL) * 2.0 - 1.0;
    }

    private static int floor(double v) {
        int i = (int) v;
        return v < i ? i - 1 : i;
    }

    private static double fade(double t) {
        return t * t * t * (t * (t * 6.0 - 15.0) + 10.0);
    }

    private static double lerp(double t, double a, double b) {
        return a + t * (b - a);
    }
}
