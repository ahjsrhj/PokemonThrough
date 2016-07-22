package cn.imrhj.PokemonThrough;

import android.location.Location;
import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import cn.imrhj.PokemonThrough.utils.FileIO;
import cn.imrhj.PokemonThrough.utils.Prefs;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Created by rhj on 16/7/19.
 */
public class Main implements IXposedHookZygoteInit, IXposedHookLoadPackage {

    private static final String TAG = "pokemonGoController  ";

    private static final double InitLat = -35.277602;
    private static final double InitLon = 149.096300;

    private double myLat = 30.2825628334;
    private double myLon = 120.0202970108;



    private boolean mOpenModify = true;
    private boolean mInitLocationLat;
    private boolean mInitLocationLon;

    private double destLat = -35.277602;
    private double destLon = 149.096300;
    private XSharedPreferences preferences = new XSharedPreferences(BuildConfig.APPLICATION_ID, BuildConfig.APPLICATION_ID);

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {

        init();

    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {


        if (!loadPackageParam.packageName.equals("com.nianticlabs.pokemongo")) return;

        init();
        if (!mOpenModify) return;

        XposedBridge.log(TAG + "handleLoadPackage: " + loadPackageParam.packageName);

        findAndHookMethod("android.location.LocationManager", loadPackageParam.classLoader, "getLastKnownLocation", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Log.d(TAG, "location provider is : " + param.args[0].toString());
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Location location = (Location) param.getResult();

                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    Log.d(TAG, "location = " + latitude + ", " + longitude);
                } else {
                    Log.e(TAG, "location is null!");
                }
            }
        });

        findAndHookMethod("android.location.Location", loadPackageParam.classLoader, "getLatitude", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                double result = (double) param.getResult();
                if (!mInitLocationLat) {
                    mInitLocationLat = true;
                    myLat = result;
                }

                double modify = Double.parseDouble(FileIO.read(Prefs.LAT_PATH));
                double end =  result - (myLat - destLat) - modify;
                String format = String.format("%7f", end);
                XposedBridge.log(TAG + "afterHookedMethod: lan = " + format);
                param.setResult(Double.parseDouble(format));
            }
        });

        findAndHookMethod("android.location.Location", loadPackageParam.classLoader, "getLongitude", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                double result = (double) param.getResult();
                if (!mInitLocationLon) {
                    mInitLocationLon = true;
                    myLon = result;
                }

                double modify = Double.parseDouble(FileIO.read(Prefs.LON_PATH));
                double end = result - (myLon - destLon) - modify;
                String format = String.format("%7f", end);
                XposedBridge.log(TAG + "afterHookedMethod: lon = " + format);
                param.setResult(Double.parseDouble(format));
            }
        });


    }


    private void init() {

        preferences.reload();
        XposedBridge.log(TAG + preferences.getFile().getAbsolutePath());
        mOpenModify = preferences.getBoolean(Prefs.KEY_OPEN_MODIFY, false);
        destLat = Double.parseDouble(preferences.getString(Prefs.KEY_LOCATION_LAT, String.valueOf(InitLat)));
        destLon = Double.parseDouble(preferences.getString(Prefs.KEY_LOCATION_LON, String.valueOf(InitLon)));
        XposedBridge.log(TAG + "init: isOpen = " + mOpenModify + ", destLat = " + destLat + ", destLon = " + destLon);
    }

}
