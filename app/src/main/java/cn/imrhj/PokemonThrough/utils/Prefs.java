package cn.imrhj.PokemonThrough.utils;

import android.os.Environment;

/**
 * Created by rhj on 16/7/20.
 */
public class Prefs {
    public static final String KEY_OPEN_MODIFY = "pref_key_open";
    public static final String KEY_LOCATION_LAT = "pref_key_location_lat";
    public static final String KEY_LOCATION_LON = "pref_key_location_lon";
    public static final String KEY_MOVE_BTN_OPEN = "pref_key_move_btn_open";

    public static final String KEY_LOCATION_BUTTON_X = "pref_key_location_button_x";
    public static final String KEY_LOCATION_BUTTON_Y = "pref_key_location_button_y";

    public static final String SD_CARD = Environment.getExternalStorageDirectory().getAbsolutePath();

    public static final String LAT_PATH = SD_CARD + "/pokemon/lat";
    public static final String LON_PATH = SD_CARD + "/pokemon/lon";
}
