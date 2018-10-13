package fr.coding.yourandroidwebapp.settings;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Matthieu on 27/05/2016.
 */
public class AdvancedAppSettings {

    public boolean disableMediasRequireUserGesture;
    public String userAgent;
    public boolean forceDownloadViewerChooser;
    public boolean allowGeoloc;

    public AdvancedAppSettings() {
        forceDownloadViewerChooser = false;
        allowGeoloc = false;
    }

    public static AdvancedAppSettings JSONobjToWebApp(JSONObject jsonobj) throws JSONException {
        AdvancedAppSettings advSettings = new AdvancedAppSettings();
        advSettings.disableMediasRequireUserGesture = jsonobj.getBoolean("disableMediasRequireUserGesture");
        if (jsonobj.has("userAgent"))
            advSettings.userAgent = jsonobj.getString("userAgent");
        if (jsonobj.has("forceDownloadViewerChooser"))
            advSettings.forceDownloadViewerChooser = jsonobj.getBoolean("forceDownloadViewerChooser");
        if (jsonobj.has("allowGeoloc"))
            advSettings.allowGeoloc = jsonobj.getBoolean("allowGeoloc");

        return advSettings;
    }

    public JSONObject toJSONobj() throws JSONException {
        JSONObject jsonobj = new JSONObject();
        jsonobj.put("disableMediasRequireUserGesture", disableMediasRequireUserGesture);
        if ((userAgent != null)&&(!userAgent.isEmpty()))
            jsonobj.put("userAgent", userAgent);
        jsonobj.put("forceDownloadViewerChooser", forceDownloadViewerChooser);
        jsonobj.put("allowGeoloc", allowGeoloc);
        return jsonobj;
    }

}
