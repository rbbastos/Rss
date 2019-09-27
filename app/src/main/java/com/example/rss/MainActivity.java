package com.example.rss;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    //test
    //for startActivityForResult()
    final int STANDARD_REQUEST_CODE = 2;
    private Button btnProcessRss;

    public void processRss(View v){
        ProcessRssTask processRssTask = new ProcessRssTask();
        processRssTask.execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnProcessRss = findViewById(R.id.btnProcessRss);

        btnProcessRss.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnProcessRss){


            //Crashing the app *************************** //
            processRss(view);
            startOtherActivity(view);
        }
    }


    //from onClick attribute in XML
    //must have a View parameter
    public void startOtherActivity(View v) {
        Log.d("Ricardo", "startOtherActivity");
        if(v.getId() == R.id.btnProcessRss) {
            //explicit intent
            Intent i = new Intent(this, OtherActivity.class);
            // i.putExtra("test", "some data");
            // i.putExtra("pi", 3.1415f);
            startActivity(i);

            //startActivityForResult(i, STANDARD_REQUEST_CODE);
        }
    }

    //final step in the startActivityForResult pattern
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == STANDARD_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // String name = data.getStringExtra("name");
                // boolean happy = data.getBooleanExtra("happy", false);
                Log.d("Ricardo", "OnActivityResult - ResultOK");
            }
            else { //must be RESULT_CANCELLED
            }
        }

    }

    class ProcessRssTask extends AsyncTask {
        private SAXParser saxParser;
        //to store item titles
        private ArrayList<String> title;
        private URL url;
        //private InputStream inputStream;

        //initialization block
        {
            title = new ArrayList<String>(10);
            //Global New feed
            try {
                url = new URL("https://globalnews.ca/feed/");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        //
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("Ricardo", "ProcessRssTask onPreExecute");
            //Toast.makeText(MainActivity.this, "Processing news feed", Toast.LENGTH_LONG).show();
            //initialize our SAXParser and run it!
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            try {
                saxParser = saxParserFactory.newSAXParser();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
        }

        //the "main" method of the async task
        //does NOT have access to the UI thread. Can't display toast, can't change textView, etc.
        @Override
        protected Object doInBackground(Object[] objects) {

            try {
                InputStream inputStream = url.openStream();
                //parse the inputStream using our custom handler
                GlobalHandler globalHandler = new GlobalHandler();
                saxParser.parse(inputStream, globalHandler);

            } catch (IOException e){
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
            return null;
        }


        //does have access to the UI thread.
        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Toast.makeText(MainActivity.this, "Processing complete!", Toast.LENGTH_LONG).show();

        }

        class GlobalHandler extends DefaultHandler {

            //to keep track of what element(s) the parser is in
            private boolean inItem, inTitle, inPubDate, inDesc;
            StringBuilder sb;

            @Override
            public void startDocument() throws SAXException {
                super.startDocument();
                Log.d("Ricardo", "startDocument");
            }

            @Override
            public void endDocument() throws SAXException {
                super.endDocument();
                Log.d("Ricardo", "endDocument");
                //what's in the arraylist?
                for(int i = 0; i< title.size(); i++){
                    Log.d("Ricardo", i + ": " + title.get(i));
                }
            }


            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                super.startElement(uri, localName, qName, attributes);
                Log.d("Ricardo", "startElement: " + qName);
                if(qName.equals("item")) {
                    inItem = true;
                } else if(inItem && qName.equals("title")) {
                    inTitle = true;
                    sb = new StringBuilder(50);
                }
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                super.endElement(uri, localName, qName);
                Log.d("Ricardo", "endElement: " + qName);
                if(qName.equals("item")) {
                    inItem = false;
                } else if(inItem && qName.equals("title")) {
                    inTitle = false;
                    title.add(sb.toString());
                }
            }

            @Override
            public void characters(char[] ch, int start, int length) throws SAXException {
                super.characters(ch, start, length);
                //String s = new String(ch, start, length);
                //Log.d("Ricardo", "characters: " + s);
                if(inItem && inTitle) {
                    //title.add(new String(ch, start, length));
                    //sb += new String(ch, start, length);
                    sb.append(ch, start, length);
                }
            }
        }
    }
}
