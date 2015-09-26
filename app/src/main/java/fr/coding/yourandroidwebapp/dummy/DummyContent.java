package fr.coding.yourandroidwebapp.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.coding.yourandroidwebapp.settings.WebApp;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static List<WebApp> ITEMS = new ArrayList<WebApp>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<String, WebApp> ITEM_MAP = new HashMap<String, WebApp>();

    static {
        // Add 3 sample items.
        WebApp webApp = new WebApp();
        webApp.id = "toto";
        webApp.url = "http://test/";
        webApp.iconUrl = "htt://test/favicon.ico";
        webApp.name = "toto";
        addItem(webApp);

        webApp = new WebApp();
        webApp.id = "toto2";
        webApp.url = "http://test/";
        webApp.iconUrl = "htt://test/favicon.ico";
        webApp.name = "toto2";
        addItem(webApp);

        webApp = new WebApp();
        webApp.id = "toto3";
        webApp.url = "http://test/";
        webApp.iconUrl = "htt://test/favicon.ico";
        webApp.name = "toto3";
        addItem(webApp);
    }

    private static void addItem(WebApp item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }
}
