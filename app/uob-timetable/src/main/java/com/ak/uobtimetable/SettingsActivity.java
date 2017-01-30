package com.ak.uobtimetable;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.ak.uobtimetable.Utilities.AndroidUtilities;
import com.ak.uobtimetable.Utilities.Logger;
import com.ak.uobtimetable.Utilities.SettingsManager;

/**
 * Activity which allows the user to configure settings. Also contains a hidden application log
 * viewer.
 */
public class SettingsActivity extends AppCompatActivity {

    private CheckBox cbLongRoomNames;
    private CheckBox cbRefreshWiFi;
    private CheckBox cbRefreshCellular;
    private TextView tvLog;
    private Button btDeveloperMode;
    private Button btClearSettings;
    private int devBtnClickCount = 0;
    private boolean showingDebug = false;

    private SettingsManager settings;

    public enum Args {
        debug
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settings = SettingsManager.getInstance(this);

        // Get UI references
        cbLongRoomNames = (CheckBox)findViewById(R.id.cbLongRoomNames);
        cbRefreshWiFi = (CheckBox)findViewById(R.id.cbRefreshWiFi);
        cbRefreshCellular = (CheckBox)findViewById(R.id.cbRefreshCellular);
        tvLog = (TextView)findViewById(R.id.tvLog);
        btDeveloperMode = (Button)findViewById(R.id.btDevMode);
        btClearSettings = (Button)findViewById(R.id.btClearSettings);

        // Set initial values
        tvLog.setMovementMethod(new ScrollingMovementMethod());
        tvLog.setVisibility(View.INVISIBLE);
        btDeveloperMode.setText(" ");
        btClearSettings.setVisibility(View.GONE);

        if (AndroidUtilities.isTabletLayout(this) == false)
            tvLog.setTextSize(12);

        cbLongRoomNames.setChecked(settings.getLongRoomNames());
        cbRefreshWiFi.setChecked(settings.getRefreshWiFi());
        cbRefreshCellular.setChecked(settings.getRefreshCellular());

        // Set checkbox event handler
        CompoundButton.OnCheckedChangeListener changedListener = new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveSettings();
            }
        };

        cbLongRoomNames.setOnCheckedChangeListener(changedListener);
        cbRefreshWiFi.setOnCheckedChangeListener(changedListener);
        cbRefreshCellular.setOnCheckedChangeListener(changedListener);

        // Dev mode button
        btDeveloperMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            devBtnClickCount++;

            if (devBtnClickCount == 7)
                showDebugOptions();
            }
        });

        btClearSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            SettingsManager.getInstance(SettingsActivity.this).clear();
            updateLog();
            }
        });

        // Restore state
        if (savedInstanceState != null && savedInstanceState.getBoolean(Args.debug.name(), false) == true)
            showDebugOptions();
    }

    private void saveSettings(){

        settings.setLongRoomNames(cbLongRoomNames.isChecked());
        settings.setRefreshWiFi(cbRefreshWiFi.isChecked());
        settings.setRefreshCellular(cbRefreshCellular.isChecked());
    }

    private void showDebugOptions(){

        showingDebug = true;
        updateLog();
        tvLog.setVisibility(View.VISIBLE);
        btClearSettings.setVisibility(View.VISIBLE);
        btDeveloperMode.setVisibility(View.GONE);
    }

    private void updateLog(){

        tvLog.setText(AndroidUtilities.fromHtml(Logger.getInstance().toHtml()));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        Logger.getInstance().debug("SettingsActivity", "Saving state");
        savedInstanceState.putBoolean(Args.debug.name(), showingDebug);
    }
}
