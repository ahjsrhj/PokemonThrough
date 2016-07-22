package cn.imrhj.PokemonThrough.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.yarolegovich.lovelyuserinput.LovelyInput;
import com.yarolegovich.mp.MaterialPreferenceScreen;
import com.yarolegovich.mp.io.MaterialPreferences;
import com.yarolegovich.mp.io.SharedPreferenceStorageModule;
import com.yarolegovich.mp.io.StorageModule;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import cn.imrhj.PokemonThrough.BuildConfig;
import cn.imrhj.PokemonThrough.utils.Prefs;
import cn.imrhj.PokemonThrough.R;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, View.OnClickListener {

    private MoveButton view;
    private WindowManager.LayoutParams params;
    private WindowManager wm;
    private boolean isAdded;
    private PositionDialog dialog;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MaterialPreferences.instance().setUserInputModule(createLovelyInputModule());
        MaterialPreferences.instance().setStorageModule(createStrageFactory());
        setContentView(R.layout.activity_main);


        preferences = getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_WORLD_READABLE);
        preferences.registerOnSharedPreferenceChangeListener(this);



        MaterialPreferenceScreen screen = (MaterialPreferenceScreen) findViewById(R.id.main_screen);
        screen.setVisibilityController(R.id.open_move_button, Arrays.asList(R.id.move_button_position), true);

        findViewById(R.id.move_button_position).setOnClickListener(this);

        initMoveButton();
        setMoveButton(preferences.getBoolean(Prefs.KEY_MOVE_BTN_OPEN, false));



    }

    private void initMoveButton() {
        view = new MoveButton(getApplicationContext());

        wm = (WindowManager)getApplicationContext().getSystemService(WINDOW_SERVICE);
        params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; // 不能抢占聚焦点
        params.flags = params.flags | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        params.flags = params.flags | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS; // 排版不受限制
        params.alpha = 1.0f;
        params.format = PixelFormat.TRANSLUCENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.x = preferences.getInt(Prefs.KEY_LOCATION_BUTTON_X, 0);
        params.y = preferences.getInt(Prefs.KEY_LOCATION_BUTTON_Y, 0);
    }


    private LovelyInput createLovelyInputModule() {
        Map<String, Integer> iconMappings = new HashMap<String, Integer>();
        iconMappings.put(Prefs.KEY_LOCATION_LAT, R.drawable.ic_location_on_white);
        iconMappings.put(Prefs.KEY_LOCATION_LON, R.drawable.ic_location_on_white);
        int topColor = ContextCompat.getColor(this, R.color.lovelyDialogTop);
        return new LovelyInput.Builder()
                .setKeyIconMappings(iconMappings)
                .setTopColor(topColor)
                .build();
    }

    private StorageModule.Factory createStrageFactory() {
        return new StorageModule.Factory() {
            @Override
            public StorageModule create(Context context) {
                SharedPreferences prefs = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_WORLD_READABLE);
                return new SharedPreferenceStorageModule(prefs);
            }
        };
    }

    @Override
    protected void onDestroy() {
        preferences.unregisterOnSharedPreferenceChangeListener(this);
        preferences = null;
        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(Prefs.KEY_MOVE_BTN_OPEN)) {
            setMoveButton(sharedPreferences.getBoolean(Prefs.KEY_MOVE_BTN_OPEN, false));
        }
    }

    /**
     * 是否显示摇杆
     * @param isOpen
     */
    private void setMoveButton(boolean isOpen) {
        if (isOpen) {
            wm.addView(view, params);
            isAdded = true;
        } else {
            if (isAdded) {
                wm.removeView(view);
                isAdded = false;
            }
        }
    }

    @Override
    public void onClick(View view) {
        setMoveButton(false);
        dialog = new PositionDialog(this)
                .setConfirmButton(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Point point = dialog.getPosition();
                        params.x = point.x;
                        params.y = point.y;
                        savePoint(point);
                        Log.d(this.getClass().getName(), "onClick: point = " + point.toString());
                        dialog.dismiss();
                        setMoveButton(true);
                    }
                });
        dialog.show();
    }

    /**
     * 将位置保存到配置文件
     * @param point
     */
    private void savePoint(Point point) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(Prefs.KEY_LOCATION_BUTTON_X, point.x);
        editor.putInt(Prefs.KEY_LOCATION_BUTTON_Y, point.y);
        editor.apply();
    }
}
