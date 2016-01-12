package co.familyplay.androidprototype.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.parse.ParseUser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import co.familyplay.androidprototype.R;
import co.familyplay.androidprototype.connectivity.Check;
import co.familyplay.androidprototype.fragment.GroupFragment;
import co.familyplay.androidprototype.fragment.NearByFragment;
import co.familyplay.androidprototype.fragment.PopularFragment;
import co.familyplay.androidprototype.fragment.SchoolFragment;

//Visit the wiki for more info
public class DashActivity extends ActionBarActivity implements ActionBar.TabListener {
    private Location location;
    private Context context;
    private String area, region, country, locality;
    private SharedPreferences prefs;
    private android.support.v7.app.ActionBar actionBar;
    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash);
        initActionBar();
        context = DashActivity.this;

        List<Fragment> fragment = new ArrayList<>();
        fragment.add(new PopularFragment());
        fragment.add(new NearByFragment());
        fragment.add(new SchoolFragment());
        fragment.add(new GroupFragment());
        prefs = getSharedPreferences("appData", Context.MODE_PRIVATE);
        clearData();

        pager = (ViewPager) findViewById(R.id.vpPager);

        GoogleMap hMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();
        hMap.setMyLocationEnabled(true);
        hMap.setOnMyLocationChangeListener(new LocationListener());
        FragmentManager fragmentManager = getSupportFragmentManager();
        pager.setAdapter(new PageAdapter(fragmentManager, fragment));
        pager.setOnPageChangeListener(new PageChange());
    }

    // Method for modifying the actionbar.
    private void initActionBar() {
        actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.action_bar_title);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_user);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.Tab pop = actionBar.newTab();
        pop.setText("Popular");
        pop.setTabListener(this);

        ActionBar.Tab near = actionBar.newTab();
        near.setText("Near");
        near.setTabListener(this);

        ActionBar.Tab school = actionBar.newTab();
        school.setText("School");
        school.setTabListener(this);

        ActionBar.Tab groups = actionBar.newTab();
        groups.setText("Groups");
        groups.setTabListener(this);

        actionBar.addTab(pop);
        actionBar.addTab(near);
        actionBar.addTab(school);
        actionBar.addTab(groups);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                context);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                context,
                android.R.layout.select_dialog_singlechoice);
        switch (id) {
            case android.R.id.home:
                String gps;
                final Boolean isFakeGps = prefs.getBoolean("gps_fake", false);
                if (isFakeGps){
                    gps = "User device gps";
                } else {
                    gps = "Use fake gps";
                }
                builderSingle.setTitle("User options");
                arrayAdapter.add(gps);
                arrayAdapter.add("Profile");
                arrayAdapter.add("Logout");
                builderSingle.setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                builderSingle.setAdapter(arrayAdapter,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int position) {
                                switch (position) {
                                    case 0:
                                        SharedPreferences.Editor editor = prefs.edit();
                                        if (isFakeGps){
                                            editor.putBoolean("gps_fake", false);
                                        } else {
                                            editor.putBoolean("gps_fake", true);
                                        }
                                        editor.commit();
                                        area = null;
                                        locality = null;
                                        country = null;
                                        break;
                                    case 1:
                                        Intent i = new Intent(context, ProfileActivity.class);
                                        startActivity(i);
                                        break;
                                    case 2:
                                        AlertDialog.Builder builder = new AlertDialog.Builder(DashActivity.this);
                                        builder.setTitle("Logout");
                                        builder.setMessage("Are you sure?");
                                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                ParseUser.logOut();
                                                Intent i = new Intent(DashActivity.this, LoginActivity.class);
                                                startActivity(i);
                                                finish();
                                            }
                                        });
                                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        builder.show();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        });
                builderSingle.show();
                break;
            case R.id.action_search:
                Intent i = new Intent(context, SearchActivity.class);
                startActivity(i);
                break;
            default:
                break;
        }

        return false;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        try {
            pager.setCurrentItem(tab.getPosition());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    // Google maps location listener.
    private class LocationListener implements GoogleMap.OnMyLocationChangeListener {

        @Override
        public void onMyLocationChange(Location arg0) {
            location = arg0;

            if (Check.hasNetworkConnection(context)) {
                new AsyncTask<String, String, String>() {

                    @Override
                    protected String doInBackground(String... params) {
                        String url;
                        Boolean isFakeGps = prefs.getBoolean("gps_fake", false);
                        if (isFakeGps){
                            url = "http://maps.googleapis.com/maps/api/geocode/json?latlng="
                                    + 7.0704905 + "," + 125.6211856
                                    + "&sensor=true_or_false";
                        } else {
                            url = "http://maps.googleapis.com/maps/api/geocode/json?latlng="
                                + location.getLatitude() + "," + location.getLongitude()
                                + "&sensor=true_or_false";
                        }
                        HttpGet httpget = new HttpGet(url);
                        DefaultHttpClient client = new DefaultHttpClient();
                        HttpResponse response;
                        InputStream inputStream;
                        String result;
                        StringBuilder theStringBuilder;
                        try {
                            response = client.execute(httpget);
                            HttpEntity entity = response.getEntity();
                            inputStream = entity.getContent();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(
                                    inputStream, "UTF-8"), 8);
                            theStringBuilder = new StringBuilder();
                            String line;

                            while ((line = reader.readLine()) != null) {
                                theStringBuilder.append(line);
                            }

                            result = theStringBuilder.toString();

                            JSONObject jsonObject = new JSONObject(result);
                            JSONArray jsonArray = jsonObject.getJSONArray("results");
                            JSONObject jsonResults = jsonArray.getJSONObject(0);

                            JSONArray addressComponents = jsonResults.getJSONArray("address_components");
                            for (int i = 0; i < addressComponents.length(); i++) {
                                JSONObject jsonAddress = addressComponents.getJSONObject(i);
                                JSONArray jsonLocality = jsonAddress.getJSONArray("types");
                                for (int j = 0; j < jsonLocality.length(); j++) {
                                    if (jsonLocality.getString(j).equals("locality") ||
                                            jsonLocality.getString(j).equals("sublocality")) {
                                        locality = jsonAddress.getString("long_name");
                                    } else if (jsonLocality.getString(j).equals("administrative_area_level_1") ||
                                            jsonLocality.getString(j).equals("administrative_area_level_2") ||
                                            jsonLocality.getString(j).equals("neighborhood")) {
                                        region = jsonAddress.getString("long_name");
                                    } else if (jsonLocality.getString(j).equals("country")) {
                                        country = jsonAddress.getString("long_name");
                                    }
                                }
                            }
                            area = region + " " + locality;
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("area", area);
                            editor.putString("region", region);
                            editor.putString("country", country);
                            editor.putString("locality", locality);
                            editor.putString("latitude", String.valueOf(location.getLatitude()));
                            editor.putString("longitude", String.valueOf(location.getLongitude()));
                            editor.commit();
                            return result;
                        } catch (Exception e) {
                            e.printStackTrace();
                            return e.toString();
                        }
                    }
                }.execute();
            }
        }

    }

    // Adapter for the fragments involved in the view
    private class PageAdapter extends FragmentPagerAdapter{
        private List<Fragment> fragments;

        public PageAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return this.fragments.get(position);
        }

        @Override
        public int getCount() {
            return this.fragments.size();
        }
    }

    // Listens every time a swipe action is initiated
    private class PageChange implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            actionBar.setSelectedNavigationItem(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            switch (state){
                case ViewPager.SCROLL_STATE_IDLE:

                case ViewPager.SCROLL_STATE_DRAGGING:

                case ViewPager.SCROLL_STATE_SETTLING:

                default:
                    break;
            }
        }
    }

    // Clear all the content of sharedprefferences
    private void clearData(){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("area", null);
        editor.putString("region", null);
        editor.putString("country", null);
        editor.putString("locality", null);
        editor.putString("latitude", null);
        editor.putString("longitude", null);
        editor.commit();
    }
}
