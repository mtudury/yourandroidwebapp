package fr.coding.webappsettingsviews;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fr.coding.yourandroidwebapp.R;
import fr.coding.yourandroidwebapp.settings.AppSettings;
import fr.coding.yourandroidwebapp.settings.AppSettingsManager;
import fr.coding.yourandroidwebapp.settings.WebApp;

public class WebAppSettingsGeneralFragment extends Fragment {

    private WebAppSettingsGeneralViewModel mViewModel;
    private WebApp wa;

    public static WebAppSettingsGeneralFragment newInstance() {
        return new WebAppSettingsGeneralFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.web_app_settings_general_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(WebAppSettingsGeneralViewModel.class);
        if (mViewModel.setWebApp(wa)) {
            fillFragment();
        }
    }

    public void fillWebAppfromView(WebApp wa) {
        wa.name = ((TextView)getView().findViewById(R.id.webapp_name)).getText().toString();
        wa.url = ((TextView)getView().findViewById(R.id.webapp_url)).getText().toString();
        wa.iconUrl = ((TextView)getView().findViewById(R.id.webapp_iconurl)).getText().toString();
    }

    public void fillFragment() {
        WebApp wa = mViewModel.getWebApp();
        ((TextView)getView().findViewById(R.id.webapp_name)).setText(wa.name);
        ((TextView)getView().findViewById(R.id.webapp_url)).setText(wa.url);
        ((TextView)getView().findViewById(R.id.webapp_iconurl)).setText(wa.iconUrl);
    }

    public void setWebApp(WebApp weba) {
        wa = weba;
    }
}
