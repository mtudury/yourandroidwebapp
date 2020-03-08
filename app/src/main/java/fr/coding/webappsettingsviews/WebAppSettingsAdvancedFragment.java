package fr.coding.webappsettingsviews;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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


        // TODO: Use the ViewModel
    }

    public void setWebApp(WebApp weba) {
        wa = weba;
    }
}
