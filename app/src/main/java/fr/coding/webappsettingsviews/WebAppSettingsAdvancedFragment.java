package fr.coding.webappsettingsviews;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import fr.coding.yourandroidwebapp.R;
import fr.coding.yourandroidwebapp.settings.WebApp;

public class WebAppSettingsAdvancedFragment extends Fragment {

    private WebAppSettingsAdvancedViewModel mViewModel;
    private WebApp wa;

    public static WebAppSettingsAdvancedFragment newInstance() {
        return new WebAppSettingsAdvancedFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.web_app_settings_advanced_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(WebAppSettingsAdvancedViewModel.class);

        if (mViewModel.setWebApp(wa)) {
            fillFragment();
        }
    }

    public void fillWebAppfromView(WebApp wa) {
        wa.allCertsByPass = ((CheckBox)getView().findViewById(R.id.webapp_sslall_activated)).isChecked();
        wa.allowedSSlActivated = ((CheckBox)getView().findViewById(R.id.webapp_ssl_activated)).isChecked();
        wa.autoAuth = ((CheckBox)getView().findViewById(R.id.webapp_autoauth)).isChecked();


        // alternate fields
        wa.alternateUrl = ((EditText) getView().findViewById(R.id.webapp_alternateurl)).getText().toString();
        wa.alternateSSIDs = ((EditText) getView().findViewById(R.id.webapp_alternateurlssid)).getText().toString();

        wa.alternateUrlNotConnected = ((EditText) getView().findViewById(R.id.webapp_alternateurloffline)).getText().toString();

        // insecure fields
        wa.allCertsByPass = ((CheckBox) getView().findViewById(R.id.webapp_sslall_activated)).isChecked();
        wa.allowedSSlActivated = ((CheckBox) getView().findViewById(R.id.webapp_ssl_activated)).isChecked();
        wa.autoAuth = ((CheckBox) getView().findViewById(R.id.webapp_autoauth)).isChecked();

        wa.reloadOnConnectionChange = ((CheckBox) getView().findViewById(R.id.webapp_reload_connection_change)).isChecked();

        // cache mode
        wa.cacheMode = SpinnerToCacheMode((Spinner) getView().findViewById(R.id.webapp_cache_mode));

        // pinchZoomMode
        wa.pinchZoomMode = ((Spinner) getView().findViewById(R.id.webapp_pinchzoom_mode)).getSelectedItemPosition();
    }

    public void fillFragment() {
        // cachemode
        Spinner cachemode = (Spinner) getView().findViewById(R.id.webapp_cache_mode);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getView().getContext(),
                R.array.cache_mode, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cachemode.setAdapter(adapter);

        // pinchzoommode
        Spinner pinchzoommode = (Spinner) getView().findViewById(R.id.webapp_pinchzoom_mode);
        ArrayAdapter<CharSequence> pzadapter = ArrayAdapter.createFromResource(getView().getContext(),
                R.array.pinchzoom_mode, android.R.layout.simple_spinner_item);
        pzadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pinchzoommode.setAdapter(pzadapter);


        WebApp wa = mViewModel.getWebApp();
        ((CheckBox)getView().findViewById(R.id.webapp_sslall_activated)).setChecked(wa.allCertsByPass);
        ((CheckBox)getView().findViewById(R.id.webapp_ssl_activated)).setChecked(wa.allowedSSlActivated);
        ((CheckBox)getView().findViewById(R.id.webapp_autoauth)).setChecked(wa.autoAuth);

        // alternate fields
        ((EditText) getView().findViewById(R.id.webapp_alternateurl)).setText(wa.alternateUrl);
        ((EditText) getView().findViewById(R.id.webapp_alternateurlssid)).setText(wa.alternateSSIDs);

        ((EditText) getView().findViewById(R.id.webapp_alternateurloffline)).setText(wa.alternateUrlNotConnected);

        // insecure fields
        ((CheckBox) getView().findViewById(R.id.webapp_sslall_activated)).setChecked(wa.allCertsByPass);
        ((CheckBox) getView().findViewById(R.id.webapp_ssl_activated)).setChecked(wa.allowedSSlActivated);
        ((CheckBox) getView().findViewById(R.id.webapp_autoauth)).setChecked(wa.autoAuth);

        ((CheckBox) getView().findViewById(R.id.webapp_reload_connection_change)).setChecked(wa.reloadOnConnectionChange);

        // cache mode
        ((Spinner) getView().findViewById(R.id.webapp_cache_mode)).setSelection(CacheModeToSpinner(wa.cacheMode));

        // pinchZoomMode
        ((Spinner) getView().findViewById(R.id.webapp_pinchzoom_mode)).setSelection(wa.pinchZoomMode);
    }

    public void setWebApp(WebApp weba) {
        wa = weba;
    }


    private int SpinnerToCacheMode(Spinner spinner) {
        int result = 0;
        switch (spinner.getSelectedItemPosition()) {
            case 0:
                result = WebSettings.LOAD_DEFAULT;
                break;
            case 1:
                result = WebSettings.LOAD_NO_CACHE;
                break;
            case 2:
                result = WebSettings.LOAD_CACHE_ELSE_NETWORK;
                break;
            case 3:
                result = WebSettings.LOAD_CACHE_ONLY;
                break;
        }
        return result;
    }


    private int CacheModeToSpinner(int cacheMode) {
        int result = 0;
        switch (cacheMode) {
            case WebSettings.LOAD_DEFAULT:
                result = 0;
                break;
            case WebSettings.LOAD_NO_CACHE:
                result = 1;
                break;
            case WebSettings.LOAD_CACHE_ELSE_NETWORK:
                result = 2;
                break;
            case WebSettings.LOAD_CACHE_ONLY:
                result = 3;
                break;
        }
        return result;
    }
}
