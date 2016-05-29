package fr.coding.yourandroidwebapp.settings;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Matthieu on 27/05/2016.
 */
public class AdvancedAppSettings {

    public boolean disableMediasRequireUserGesture;
    public String userAgent;

    public static AdvancedAppSettings JSONobjToWebApp(JSONObject jsonobj) throws JSONException {
        AdvancedAppSettings advSettings = new AdvancedAppSettings();
        advSettings.disableMediasRequireUserGesture = jsonobj.getBoolean("disableMediasRequireUserGesture");
        if (jsonobj.has("userAgent"))
            advSettings.userAgent = jsonobj.getString("userAgent");

        return advSettings;
    }

    public JSONObject toJSONobj() throws JSONException {
        JSONObject jsonobj = new JSONObject();
        jsonobj.put("disableMediasRequireUserGesture", disableMediasRequireUserGesture);
        if ((userAgent != null)&&(!userAgent.isEmpty()))
            jsonobj.put("userAgent", userAgent);
        return jsonobj;
    }

}