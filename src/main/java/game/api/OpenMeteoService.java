package game.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

/**
 * Implementation of MoonPhaseService that fetches real-world moon phase data
 * from the Open-Meteo Weather API.
 * <p>
 * This service makes HTTP requests to Open-Meteo's free astronomy API endpoint
 * to retrieve current moon phase information for Kuala Lumpur, Malaysia.
 * The API requires no authentication or API key, making it ideal for educational
 * projects and assignments.
 * <p>
 * API Details:
 * <ul>
 *   <li>Provider: Open-Meteo (https://open-meteo.com)</li>
 *   <li>Cost: 100% FREE, no registration required</li>
 *   <li>Rate Limit: 10,000 requests per day per IP</li>
 *   <li>Location: Kuala Lumpur, Malaysia (3.139°N, 101.686°E)</li>
 * </ul>
 * <p>
 * Caching Strategy:
 * The service caches the first successful API response to avoid repeated network
 * calls during a single game session. This improves performance and respects
 * rate limits (only 1 API call per game session instead of per turn).
 * <p>
 * Error Handling:
 * If the API is unreachable (no internet, firewall, server down), methods throw
 * exceptions that are caught by LunarPhaseManager, which defaults to NORMAL phase.
 *
 * @author Taabish Farooq Bhat
 * @version 1.0
 * @see MoonPhaseService
 * @see MoonPhase
 */
public class OpenMeteoService implements MoonPhaseService {

    private final OkHttpClient client = new OkHttpClient();
    private MoonPhase cachedPhase = null;

    /**
     * Fetches the current moon phase from Open-Meteo API.
     * <p>
     * API Request Details:
     * <ul>
     *   <li>Endpoint: https://api.open-meteo.com/v1/forecast</li>
     *   <li>Parameters:
     *     <ul>
     *       <li>latitude=3.139 (Kuala Lumpur)</li>
     *       <li>longitude=101.686 (Kuala Lumpur)</li>
     *       <li>daily=moon_phase (request moon phase data)</li>
     *       <li>timezone=Asia/Kuala_Lumpur (local timezone)</li>
     *     </ul>
     *   </li>
     * </ul>
     * <p>
     * Expected JSON Response Format:
     * <pre>
     * {
     *   "daily": {
     *     "time": ["2025-11-02"],
     *     "moon_phase": [0.53]
     *   }
     * }
     * </pre>
     * <p>
     * The moon_phase value ranges from 0.0 to 1.0:
     * <ul>
     *   <li>0.0 = New Moon</li>
     *   <li>0.5 = Full Moon</li>
     *   <li>1.0 = New Moon (cycle repeats)</li>
     * </ul>
     * <p>
     * Caching Behavior:
     * On first call, fetches from API and caches result. Subsequent calls return
     * the cached value without making additional HTTP requests. Cache persists
     * for the lifetime of this OpenMeteoService instance (entire game session).
     *
     * @return the current MoonPhase enum value (NEW_MOON, NORMAL, or FULL_MOON)
     * @throws Exception if HTTP request fails, JSON parsing fails, or API returns
     *                   unsuccessful response code (e.g., 404, 500, timeout)
     */
    @Override
    public MoonPhase fetchCurrentPhase() throws Exception {
        if (cachedPhase != null) return cachedPhase;

        String url = "https://api.open-meteo.com/v1/forecast?latitude=3.139&longitude=101.686&daily=moon_phase&timezone=Asia%2FKuala_Lumpur";
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new Exception("API not reachable!");
            JSONObject json = new JSONObject(response.body().string());
            double phaseValue = json.getJSONObject("daily").getJSONArray("moon_phase").getDouble(0);
            cachedPhase = MoonPhase.fromApiValue(phaseValue);
            return cachedPhase;
        }
    }

    /**
     * Checks if the Open-Meteo API service is currently available and reachable.
     * <p>
     * Implementation:
     * Attempts to fetch the current moon phase by calling {@link #fetchCurrentPhase()}.
     * If the fetch succeeds, the service is considered available. If any exception
     * occurs (network error, timeout, JSON parsing error), returns false.
     * <p>
     * Note: This method may make an HTTP request if no cached phase exists,
     * which could take several seconds if the network is slow or timing out.
     * Consider using this method sparingly (e.g., only at game initialization).
     *
     * @return true if API is reachable and responds successfully, false if any
     *         error occurs (no internet, firewall, API down, timeout, etc.)
     */
    @Override
    public boolean isAvailable() {
        try {
            fetchCurrentPhase();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
