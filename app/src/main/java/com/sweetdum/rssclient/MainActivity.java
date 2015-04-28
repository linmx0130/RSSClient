package com.sweetdum.rssclient;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    private ListView rssView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rssView=(ListView)findViewById(R.id.rss_view);
        rssView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RSSItem item=(RSSItem)rssView.getAdapter().getItem(position);
                Intent i=new Intent(Intent.ACTION_VIEW, Uri.parse(item.getLink()));
                startActivity(i);
            }
        });
        FetcherTask c=new FetcherTask();
        c.execute("http://songshuhui.net/feed");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private class FetcherTask extends AsyncTask<String, Void, ArrayList<RSSItem>>{
        @Override
        protected ArrayList<RSSItem> doInBackground(String... params) {
            String s=params[0];
            RSSChannel c=new RSSChannel(s);
            c.readData();
            return (ArrayList<RSSItem>)c.getItemList();
        }

        @Override
        protected void onPostExecute(ArrayList<RSSItem> rssItems) {
            super.onPostExecute(rssItems);

            for (RSSItem item: rssItems){
                Log.d("HHH",item.getTitle());
            }
            RSSAdapter adapter=new RSSAdapter(rssItems);
            MainActivity.this.rssView.setAdapter(adapter);
        }

        private class RSSAdapter extends ArrayAdapter<RSSItem>{
            public RSSAdapter(ArrayList<RSSItem> rssItems){
                super(MainActivity.this,0,rssItems);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView==null){
                    convertView=MainActivity.this.getLayoutInflater().inflate(R.layout.item_view,null);
                }
                RSSItem i=getItem(position);
                TextView titleView,desView;
                titleView=(TextView)convertView.findViewById(R.id.title_text_view);
                titleView.setText(i.getTitle());
                desView=(TextView)convertView.findViewById(R.id.description_text_view);
                desView.setText(i.getDescription());
                return convertView;
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
