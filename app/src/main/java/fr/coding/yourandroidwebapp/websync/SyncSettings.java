package fr.coding.yourandroidwebapp.websync;

import org.json.JSONException;
import org.json.JSONObject;

public class SyncSettings {
    private String sync_download_url;
    private String sync_downloadheaderkey;
    private String sync_downloadheadervalue;

    private String sync_upload_url;
    private String sync_uploadheaderkey;
    private String sync_uploadheadervalue;
    private String sync_upload_httpmethod;
    private String sync_upload_downloadsettings_exclude;
    private String sync_upload_uploadsettings_exclude;


    public static SyncSettings JSONobjToWebApp(JSONObject jsonobj) throws JSONException {
        SyncSettings advSettings = new SyncSettings();
        /*advSettings.disableMediasRequireUserGesture = jsonobj.getBoolean("disableMediasRequireUserGesture");
        if (jsonobj.has("userAgent"))
            advSettings.userAgent = jsonobj.getString("userAgent");
        if (jsonobj.has("forceDownloadViewerChooser"))
            advSettings.forceDownloadViewerChooser = jsonobj.getBoolean("forceDownloadViewerChooser");
        if (jsonobj.has("allowGeoloc"))
            advSettings.allowGeoloc = jsonobj.getBoolean("allowGeoloc");
*/
        return advSettings;
    }

    public JSONObject toJSONobj() throws JSONException {
        JSONObject jsonobj = new JSONObject();
        /*
        jsonobj.put("disableMediasRequireUserGesture", disableMediasRequireUserGesture);
        if ((userAgent != null)&&(!userAgent.isEmpty()))
            jsonobj.put("userAgent", userAgent);
        jsonobj.put("forceDownloadViewerChooser", forceDownloadViewerChooser);
        jsonobj.put("allowGeoloc", allowGeoloc);
        */
        return jsonobj;
    }


}
