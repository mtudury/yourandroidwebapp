package fr.coding.tools.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Matthieu on 03/10/2015.
 */
public class HostAuth {
    public String Host;
    public String Login;
    public String Password;
    public Date dtCreated;
    public boolean activated;

    public HostAuth() {
        dtCreated = new Date();
    }

    public String toString() {
        return Host + (activated?"":" (NotAllowed)");
    }

    public static HostAuth JSONobjToSslByPass(JSONObject jsonobj) throws JSONException {
        HostAuth hostAuth = new HostAuth();
        hostAuth.Host = jsonobj.getString("Host");
        hostAuth.Login = jsonobj.getString("Login");
        hostAuth.Password = jsonobj.getString("Password");
        hostAuth.activated = jsonobj.getBoolean("activated");
        hostAuth.dtCreated = new Date(jsonobj.getLong("dtCreated"));
        return hostAuth;
    }

    public JSONObject toJSONobj() throws JSONException {
        JSONObject jsonobj = new JSONObject();
        jsonobj.put("Host", Host);
        jsonobj.put("Login", Login);
        jsonobj.put("Password", Password);
        jsonobj.put("activated", activated);
        jsonobj.put("dtCreated", dtCreated.getTime());
        return jsonobj;
    }
}
