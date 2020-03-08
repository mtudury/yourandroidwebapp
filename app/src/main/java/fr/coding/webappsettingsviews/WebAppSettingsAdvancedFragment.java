package fr.coding.webappsettingsviews;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
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


    }

    public void setWebApp(WebApp weba) {
        wa = weba;
    }
}
