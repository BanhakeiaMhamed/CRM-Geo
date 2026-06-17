package com.backend.backend.metier.services;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GeoCodingService {

    private static final Map<String, double[]> POSTAL_COORDS = Map.ofEntries(
            Map.entry("69001", coords(45.7676, 4.8344)),
            Map.entry("69002", coords(45.7477, 4.8265)),
            Map.entry("69003", coords(45.7580, 4.8650)),
            Map.entry("69004", coords(45.7798, 4.8260)),
            Map.entry("69005", coords(45.7620, 4.8265)),
            Map.entry("69006", coords(45.7699, 4.8511)),
            Map.entry("69007", coords(45.7320, 4.8350)),
            Map.entry("69008", coords(45.7398, 4.8710)),
            Map.entry("69009", coords(45.7735, 4.8060)),
            Map.entry("69100", coords(45.7660, 4.8790)),
            Map.entry("69200", coords(45.7350, 4.9100)),
            Map.entry("69300", coords(45.7950, 4.8470)),
            Map.entry("69400", coords(45.7740, 4.7200)),
            Map.entry("69500", coords(45.7040, 4.8850)),
            Map.entry("69600", coords(45.7080, 4.8100)),
            Map.entry("28700", coords(48.4720, 1.7900))
    );

    public double[] resolveCoordinates(String address, String postalCode, String city) {
        double[] base = baseFor(postalCode, city);
        String seedKey = (address != null ? address.trim() : "") + "|" + postalCode + "|" + city;
        return jitter(base[0], base[1], seedKey.hashCode());
    }

    private double[] baseFor(String postalCode, String city) {
        if (postalCode != null && POSTAL_COORDS.containsKey(postalCode)) {
            return POSTAL_COORDS.get(postalCode);
        }
        int seed = ((postalCode != null ? postalCode : "") + (city != null ? city : "")).hashCode();
        return jitter(45.7640, 4.8357, seed);
    }

    private static double[] coords(double lat, double lng) {
        return new double[]{lat, lng};
    }

    /** Decalage par adresse pour distinguer les marqueurs d'une meme ville. */
    private double[] jitter(double lat, double lng, int seed) {
        double dLat = ((seed % 100) - 50) * 0.0012;
        double dLng = (((seed / 100) % 100) - 50) * 0.0016;
        return new double[]{lat + dLat, lng + dLng};
    }
}
