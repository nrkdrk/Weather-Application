package nd.yazilim.weather.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import it.gmariotti.cardslib.library.BuildConfig;
import nd.yazilim.weather.R;
import nd.yazilim.weather.activity.AboutActivity;
import nd.yazilim.weather.activity.GlobalActivity;
import nd.yazilim.weather.fragment.GraphsFragment;
import nd.yazilim.weather.fragment.MapsFragment;
import nd.yazilim.weather.fragment.WeatherFragment;
import nd.yazilim.weather.permissions.GPSTracker;
import nd.yazilim.weather.permissions.Permissions;
import nd.yazilim.weather.preferences.Preferences;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondarySwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.weather_icons_typeface_library.WeatherIcons;

public class WeatherActivity extends AppCompatActivity {

    Permissions permission;
    Preferences preferences;
    String lat, lon;
    GPSTracker gps;
    FloatingActionButton fab;
    WeatherFragment wf;
    Toolbar toolbar;
    Drawer drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = new Preferences(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        wf = new WeatherFragment();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment, wf)
                    .commit();
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabClick();
            }
        });
        initDrawer();
    }

    public void hideFab() {
        fab.hide();
    }

    public void showFab() {
        fab.show();
    }

    public void initDrawer() {
        final IProfile profile = new ProfileDrawerItem().withName("Hava Durumu")
                .withIcon(R.drawable.ic_launcher_dark);
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .withTextColor(ContextCompat.getColor(this , R.color.md_amber_400))
                .addProfiles(
                        profile
                )
                .withSelectionListEnabled(false)
                .build();
        SecondaryDrawerItem item1 = new SecondaryDrawerItem().withIdentifier(1).withName(R.string.drawer_item_home)
                .withIcon(new IconicsDrawable(this)
                        .icon(WeatherIcons.Icon.wic_day_sunny)
                        .sizeRes(R.dimen.activity_horizontal_margin));
        SecondaryDrawerItem item3 = new SecondaryDrawerItem().withIdentifier(3).withName(R.string.drawer_item_map)
                .withIcon(new IconicsDrawable(this)
                        .icon(GoogleMaterial.Icon.gmd_map)
                        .sizeRes(R.dimen.activity_horizontal_margin));
        SecondaryDrawerItem item6 = new SecondaryDrawerItem().withIdentifier(5).withName(R.string.drawer_item_about)
                .withIcon(new IconicsDrawable(this)
                        .icon(GoogleMaterial.Icon.gmd_info)
                        .sizeRes(R.dimen.activity_horizontal_margin))
                .withSelectable(false);
        final SecondarySwitchDrawerItem item4 , item5;
        if (preferences.getUnits().equals("imperial"))
            item4 = new SecondarySwitchDrawerItem().withIdentifier(6).withName("Fahrenheit kullanın")
                    .withChecked(true)
                    .withIcon(new IconicsDrawable(this)
                            .icon(WeatherIcons.Icon.wic_fahrenheit)
                            .sizeRes(R.dimen.activity_horizontal_margin))
                    .withSelectable(false);
        else
            item4 = new SecondarySwitchDrawerItem().withIdentifier(6).withName("Fahrenheit kullanın")
                    .withChecked(false)
                    .withIcon(new IconicsDrawable(this)
                            .icon(WeatherIcons.Icon.wic_fahrenheit)
                            .sizeRes(R.dimen.activity_horizontal_margin))
                    .withSelectable(false);
        if (preferences.getNotifs())
            item5 = new SecondarySwitchDrawerItem().withIdentifier(6).withName("Saatlik Bildirimleri Göster")
                    .withChecked(true)
                    .withIcon(new IconicsDrawable(this)
                            .icon(GoogleMaterial.Icon.gmd_notifications)
                            .sizeRes(R.dimen.activity_horizontal_margin))
                    .withSelectable(false);
        else
            item5 = new SecondarySwitchDrawerItem().withIdentifier(6).withName("Saatlik Bildirimleri Göster")
                    .withChecked(false)
                    .withIcon(new IconicsDrawable(this)
                            .icon(GoogleMaterial.Icon.gmd_notifications)
                            .sizeRes(R.dimen.activity_horizontal_margin))
                    .withSelectable(false);

        item4.withOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    preferences.setUnits("imperial");
                }
                else {
                    preferences.setUnits("metric");
                }
                drawer.closeDrawer();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment, new WeatherFragment())
                        .commit();
            }
        });

        item5.withOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    preferences.setNotifs(true);
                }
                else {
                    preferences.setNotifs(false);
                }
                drawer.closeDrawer();
            }
        });

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withSelectedItem(1)
                .withAccountHeader(headerResult)
                .withActionBarDrawerToggleAnimated(true)
                .addDrawerItems(
                        item1,
                        item3,
                        new DividerDrawerItem(),
                        item4,
                        item5,
                        new DividerDrawerItem(),
                        item6
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        Bundle bundle = new Bundle();
                        if (drawerItem != null) {
                            if (drawerItem.getIdentifier() == 1) {
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.fragment, new WeatherFragment())
                                        .commit();
                            }
                            else if (drawerItem.getIdentifier() == 3) {
                                bundle.putString("json" , wf.getDailyJson().toString());
                                MapsFragment mapsFragment = new MapsFragment();
                                mapsFragment.setArguments(bundle);
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.fragment, mapsFragment)
                                        .commit();
                            }
                            else if (drawerItem.getIdentifier() == 5) {
                                startActivity(new Intent(WeatherActivity.this, AboutActivity.class));
                            }
                        }
                        return false;
                    }
                })
                .build();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String permissions[],@NonNull int[] grantResults) {
        switch (requestCode) {
            case 20: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showCity();
                } else {
                    permission.permissionDenied();
                }
                break;
            }
        }
    }

    private void fabClick() {
        fab.hide();
        showInputDialog();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void showInputDialog() {
        new MaterialDialog.Builder(this)
                .title("Şehri değiştir ")
                .content("Şehir adını girerek şehri değiştirebilirsiniz\n")
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        fab.show();
                    }
                })
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        fab.show();
                    }
                })
                .negativeText("Vazgeç")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog , @NonNull DialogAction which) {
                        dialog.dismiss();
                        fab.show();
                    }
                })
                .input(null, null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, @NonNull CharSequence input) {
                        changeCity(input.toString());
                        fab.show();
                    }
                }).show();
    }

    public void changeCity(String city){
        WeatherFragment wf = (WeatherFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment);
        wf.changeCity(city);
        GlobalActivity.cp.setCity(city);
    }

    public void changeCity(String lat, String lon) {
        WeatherFragment wf = (WeatherFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment);
        wf.changeCity(lat , lon);
    }

    private void showCity() {
        gps = new GPSTracker(this);
        if (!gps.canGetLocation())
            new MaterialDialog.Builder(this)
                    .content("GPS Konumunuzun Hava Durumu Verilerini görüntülemek için etkin olması gerekiyor\n")
                    .title("GPS'yi etkinleştir")
                    .positiveText("Etkinleştir")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .show();
        else {
            lat = gps.getLatitude();
            lon = gps.getLongitude();
            changeCity(lat, lon);
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}
