package com.example.weather;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.res.Configuration;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.text.SimpleDateFormat;


public class MainActivity extends AppCompatActivity implements ListFragment.ItemSelected {



    private boolean isLandscape = false;
    private TextView txtDetails;
    private String currentFragment = "glasgow";
    private ScrollView detailsScrollView;


    private String glasgowURL = "https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/2648579";
    private String londonURL = "https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/2643743";
    private String newYorkURL = "https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/5128581";
    private String omanURL = "https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/287286";
    private String mauritiusURL = "https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/934154";
    private String bangladeshURL = "https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/1185241";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtDetails = findViewById(R.id.txtDetails);
        detailsScrollView = findViewById(R.id.detailsScrollView);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);


        if (savedInstanceState == null)
        {
            FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
            Fragment selectedFragment = new ListFragment();

            Bundle args = new Bundle();
            if(findViewById(R.id.layout_land) != null)
            {
                isLandscape = true;
            }
            args.putBoolean("landscape", isLandscape);
            args.putString("url", glasgowURL);
            selectedFragment.setArguments(args);

            tr.replace(R.id.fragment_container, selectedFragment, "currentFragment");
            tr.addToBackStack(null);
            tr.commit();
        }

        getSupportFragmentManager().executePendingTransactions();

        //Phone is portrait
        if(findViewById(R.id.layout_portrait) != null)
        {
            FragmentManager fm = this.getSupportFragmentManager();
            Fragment f = fm.findFragmentByTag("currentFragment");

            if(f!=null) {
                fm.beginTransaction()
                        .hide(fm.findFragmentById(R.id.details_container))
                        .show(fm.findFragmentById(R.id.fragment_container))
                        .addToBackStack(null)
                        .commit();
            }
        }

        //Phone is landscape
        if(findViewById(R.id.layout_land) != null)
        {
            FragmentManager fm = this.getSupportFragmentManager();
            Fragment f = fm.findFragmentByTag("currentFragment");

            if(f!=null)
            {
                fm.beginTransaction()
                        .hide(fm.findFragmentById(R.id.details_container))
                        .show(fm.findFragmentById(R.id.fragment_container))
                        .addToBackStack(null)
                        .commit();
            }
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    Fragment selectedFragment = new ListFragment();
                    Bundle args = new Bundle();

                    if(findViewById(R.id.layout_land) != null)
                    {
                        isLandscape = true;
                    }
                    args.putBoolean("landscape", isLandscape);

                    switch(menuItem.getItemId())
                    {
                        case R.id.navGlasgow:
                            args.putString("url", glasgowURL);
                            selectedFragment.setArguments(args);
                            currentFragment = "glasgow";
                            break;
                        case R.id.navLondon:
                            args.putString("url", londonURL);
                            selectedFragment.setArguments(args);
                            currentFragment = "london";
                            break;
                        case R.id.navNewYork:
                            args.putString("url", newYorkURL);
                            selectedFragment.setArguments(args);
                            currentFragment = "newYork";
                            break;
                        case R.id.navOman:
                            args.putString("url", omanURL);
                            selectedFragment.setArguments(args);
                            currentFragment = "oman";
                            break;
                        case R.id.navMauritius:
                            args.putString("url", mauritiusURL);
                            selectedFragment.setArguments(args);
                            currentFragment = "mauritius";
                            break;
                    }

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment, "currentFragment")
                            .commit();
                    Log.e("Current fragment", currentFragment);



                    getSupportFragmentManager().executePendingTransactions();

                    //Phone is portrait
                    if(findViewById(R.id.layout_portrait) != null)
                    {
                        FragmentManager fm = getSupportFragmentManager();
                        Fragment f = fm.findFragmentByTag("currentFragment");

                        if(f!=null) {
                            fm.beginTransaction()
                                    .hide(fm.findFragmentById(R.id.details_container))
                                    .show(fm.findFragmentById(R.id.fragment_container))
                                    .commit();
                        }
                    }

                    //Phone is landscape
                    if(findViewById(R.id.layout_land) != null)
                    {
                        FragmentManager fm = getSupportFragmentManager();
                        Fragment f = fm.findFragmentByTag("currentFragment");

                        if(f!=null) {
                            fm.beginTransaction()
                                    .hide(fm.findFragmentById(R.id.details_container))
                                    .show(fm.findFragmentById(R.id.fragment_container))
                                    .commit();
                        }
                    }

                    return true;
                }
            };

// Loads changes to orientation
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);

        if(isLandscape == true)
        {
            isLandscape = false;
        }
        else
        {
            isLandscape = true;
        }
    }


    @Override
    public void onItemSelected(RssResponse response, int index)
    {

        SimpleDateFormat fmt = new SimpleDateFormat("dd-MMMM-yyyy");

        if(response.getIsDefault() == false)
        {
            StringBuilder output = new StringBuilder();

            output.append("<b>Basic Info: </b>" + "<br /><br />");
            String str = response.getTitle();
            output.append(str+ "<br /><br />");

            output.append("<b>Advanced Information: </b>" + "<br /><br />");

            str = response.getDescription();
            str = str.replaceAll("<br />", "<br /><br />"); //"\r\n\n");
            output.append(str+ "<br /><br />");

            output.append("<b>Source Link: </b>" + "<br /><br />");
            str = response.getLink();
            output.append(str+ "<br /><br />");

            output.append("<b>Date Published: </b>" + "<br /><br />");
            str = response.getPublished();
            output.append(str);

            txtDetails.setText(Html.fromHtml(output.toString()));
        }

        else
        {
            txtDetails.setText(response.getDescription());
        }

        if(findViewById(R.id.layout_portrait) != null)
        {
            FragmentManager fm = this.getSupportFragmentManager();
            Fragment f = fm.findFragmentByTag("currentFragment");

            if(f!=null)
            {
                fm.beginTransaction()
                        .show(fm.findFragmentById(R.id.details_container))
                        .hide(fm.findFragmentById(R.id.fragment_container))
                        .addToBackStack(null)
                        .commit();
            }

        }
        if(findViewById(R.id.layout_land) != null)
        {
            FragmentManager fm = this.getSupportFragmentManager();
            Fragment f = fm.findFragmentByTag("currentFragment");

            if(f!=null)
            {
                fm.beginTransaction()
                        .show(fm.findFragmentById(R.id.details_container))
                        .hide(fm.findFragmentById(R.id.fragment_container))
                        .addToBackStack(null)
                        .commit();
            }

        }
        detailsScrollView.smoothScrollTo(0, txtDetails.getTop());
    }
}