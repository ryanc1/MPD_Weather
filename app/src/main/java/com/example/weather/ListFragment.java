package com.example.weather;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ListFragment extends androidx.fragment.app.ListFragment {

    private int responseID = 0;
    private ItemSelected activity;
    private boolean landValue = false;
    private ArrayList<String> titles;
    private ArrayList<RssResponse> weather;
    private String URL = null;
    private SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MMMM-dd");
    private CustomAdapter adapter;


    public interface ItemSelected
    {
        void onItemSelected(RssResponse w, int index);
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        activity = (ItemSelected) context;
        landValue = this.getArguments().getBoolean("landscape");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        URL = getArguments().getString("url");
        new ProcessInBackground().execute();
    }


    public void selectDefault()
    {
        if(landValue)
        {
            activity.onItemSelected(weather.get(0), 0);
        }
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        ArrayList<RssResponse> newWeather = (ArrayList<RssResponse>) adapter.getFilteredData();

        if(newWeather.size() > 0)
        {
            RssResponse w = newWeather.get((int)id);
            activity.onItemSelected(w, position);
        }
        else
        {
            RssResponse defaultW = new RssResponse();
            defaultW.setDescription("Nothing to show");
            activity.onItemSelected(defaultW, position);
        }
    }


    public InputStream getInputStream(java.net.URL url)
    {
        try
        {
            return url.openConnection().getInputStream();
        }
        catch (IOException e)
        {
            return null;
        }
    }



    public class ProcessInBackground extends AsyncTask<Void, Void, ArrayList<RssResponse>>
    {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        Exception ex = null;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            titles = new ArrayList<String>();
            weather = new ArrayList<RssResponse>();

            progressDialog.setMessage("Loading");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(ArrayList<RssResponse> s)
        {
            super.onPostExecute(s);
            progressDialog.dismiss();

            adapter = new CustomAdapter(getActivity(), weather);
            setListAdapter(adapter);
        }

        @Override
        protected ArrayList<RssResponse> doInBackground(Void... params)
        {
            try
            {
                java.net.URL url = new URL(URL);

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(getInputStream(url), "UTF_8");
                boolean insideItem = false;
                RssResponse weatherToAdd = null;

                int eventType = xpp.getEventType();

                while(eventType != XmlPullParser.END_DOCUMENT)
                {
                    if(eventType == XmlPullParser.START_TAG)
                    {
                        if(xpp.getName().equalsIgnoreCase("item"))
                        {
                            insideItem = true;
                            weatherToAdd = new RssResponse();
                            weatherToAdd.setIsDefault(false);
                            weatherToAdd.setUniqueID(responseID);
                            responseID = responseID + 1;
                        }


                        else if(xpp.getName().equalsIgnoreCase("title"))
                        {
                            if(insideItem)
                            {
                                weatherToAdd.setTitle(xpp.nextText());
                            }
                        }

                        else if(xpp.getName().equalsIgnoreCase("description"))
                        {
                            if(insideItem)
                            {
                                String details = xpp.nextText();
                                String[] detailsArray = details.split("<br />+");
                                String detailsRest = "";

                                if(detailsArray.length > 1)
                                {
                                    String[] startArray = detailsArray[0].split("\\s+");
                                    String[] endArray = detailsArray[1].split("\\s+");
                                    Date start = fmt.parse(startArray[5] + "-" + startArray[4] + "-" + startArray[3]);
                                    Date end = fmt.parse(endArray[5] + "-" + endArray[4] + "-" + endArray[3]);
                                }

                                else
                                 {
                                    detailsRest = detailsArray[0];
                                 }

                                if(detailsArray.length > 2) {
                                    detailsRest = detailsArray[2];
                                    String[] detailsRestSplit = detailsRest.split("\n+");
                                    if(detailsRestSplit.length > 1)
                                    {
                                        detailsRest = "";
                                        for(int i = 0; i < detailsRestSplit.length - 1; i+=2)
                                        {
                                            detailsRest = detailsRest + detailsRestSplit[i] + " " + detailsRestSplit[i+1] + "<br/><br/>";
                                        }
                                        detailsRest = detailsRest.substring(0, detailsRest.length() - 10);
                                    }
                                }

                                weatherToAdd.setDescription(detailsRest);
                            }
                        }
                        else if (xpp.getName().equalsIgnoreCase("link"))
                        {
                            if(insideItem)
                            {
                                weatherToAdd.setLink(xpp.nextText());
                            }
                        }
                        else if (xpp.getName().equalsIgnoreCase("pubDate"))
                        {
                            if(insideItem)
                            {
                                weatherToAdd.setPublished(xpp.nextText());
                            }
                        }
                    }
                    else if(eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item"))
                    {
                        insideItem = false;
                        weather.add(weatherToAdd);
                    }

                    eventType = xpp.next();
                }
            }
            catch(MalformedURLException e)
            {
                ex = e;
            }
            catch (XmlPullParserException e)
            {
                ex = e;
            }
            catch (IOException e)
            {
                ex = e;
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }

            if (weather.size() <= 0)
            {
                RssResponse defaultResponse = new RssResponse();
                defaultResponse.setTitle("Something went wrong, nothing to show");
                defaultResponse.setDescription("No data");
                weather.add(defaultResponse);
            }
            return weather;
        }
    }
}