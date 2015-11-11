package fr.coding.tools.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Matthieu on 03/10/2015.
 */
public class SslByPass {
    public String CName;
    public String Host;
    public long ValidNotAfter;
    public Date dtCreated;
    public boolean activated;

    public String toString() {
        return Host + (activated?"":" (NotAllowed)");
    }

    public static SslByPass JSONobjToSslByPass(JSONObject jsonobj) throws JSONException {
        SslByPass sslByPass = new SslByPass();
        if (jsonobj.has("ValidNotAfter"))
            sslByPass.ValidNotAfter = jsonobj.getLong("ValidNotAfter");
        sslByPass.CName = jsonobj.getString("CName");
        sslByPass.activated = jsonobj.getBoolean("activated");

        sslByPass.dtCreated = new Date();
        if (jsonobj.has("dtCreated"))
            sslByPass.dtCreated = new Date(jsonobj.getLong("dtCreated"));

        sslByPass.Host = "";
        if (jsonobj.has("Host"))
            sslByPass.Host = jsonobj.getString("Host");

        return sslByPass;
    }

    public JSONObject toJSONobj() throws JSONException {
        JSONObject jsonobj = new JSONObject();
        jsonobj.put("ValidNotAfter", ValidNotAfter);
        jsonobj.put("CName", CName);
        jsonobj.put("activated", activated);
        jsonobj.put("dtCreated", dtCreated.getTime());
        jsonobj.put("Host", Host);
        return jsonobj;
    }
}
