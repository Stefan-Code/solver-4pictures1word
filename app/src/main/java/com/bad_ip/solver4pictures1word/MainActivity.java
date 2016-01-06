package com.bad_ip.solver4pictures1word;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final String FOUNDWORDSEXTRA = "com.bad_ip.solver4pictures1word.FOUNDWORDSEXTRA";
    private ArrayList<String> wordList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        InputStream inputStream = null;
        String result = null;
        this.wordList = new ArrayList<String>();
        try {
            inputStream = this.getResources().openRawResource(R.raw.de);
            // json is UTF-8 by default
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            result = sb.toString();
        } catch (Exception e) {
            Log.e("test", "Exception occured");
        }
        finally {
            try{if(inputStream != null)inputStream.close();}catch(Exception squish){}
        }
        JSONObject jObject = null;
        try {
            jObject = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray jArray = null;
        try {
            jArray = jObject.getJSONArray("words");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i=0; i < jArray.length(); i++)
        {
            try {
                String word = jArray.get(i).toString();
                this.wordList.add(word);
                // Pulling items from the array
                //String oneObjectsItem = oneObject.getString("STRINGNAMEinTHEarray");
            } catch (JSONException e) {
                // Oops
                Log.e("test", e.toString());
            }
        }

    }
    public void getResults(View view) {
        // Go button pressed
        // TODO crash on empty length (int parsing)
        // TODO don't allow empty randomchars
        Intent intent = new Intent(this, ResultsActivity.class);
        EditText randomCharsEditText = (EditText) findViewById(R.id.randomChars);
        EditText wordLengthEditText = (EditText) findViewById(R.id.wordLength);
        String randomChars = randomCharsEditText.getText().toString();
        String wordLengthString = wordLengthEditText.getText().toString();
        int wordLength = 0;
        if(wordLengthString.length() > 0) {
            wordLength = Integer.parseInt(wordLengthString);
        }
        ArrayList<String> results = this.get_words(randomChars, wordLength);
        intent.putExtra(FOUNDWORDSEXTRA, results);
        startActivity(intent);

    }

    public ArrayList<String> get_words(String randomCharsString, int length) {
        //creating ArrayList from random characters
        ArrayList<Character> randomChars = new ArrayList<Character>();
        ArrayList<String> foundWords = new ArrayList<String>();
        for(int i = 0; i < randomCharsString.length(); i++) {
            randomChars.add(randomCharsString.charAt(i));
        }
        //looping over every word in dictionary
        for(int i = 0; i < wordList.size(); i++) {
            String word = wordList.get(i);
            System.out.println("Processing word");
            System.out.println(word);
            if(test_word(word, randomChars)) {
                //only add word if length matches
                if(word.length() == length) {
                    foundWords.add(word);
                }
            }

        }
        return foundWords;
    }
    public boolean test_word(String word, ArrayList<Character> randomCharsOriginal) {
        //looping over every char in word
        //shallow copy
        ArrayList<Character> randomChars = new ArrayList<Character>(randomCharsOriginal);
        for(int j = 0; j<word.length(); j++) {
            char wordChar = word.charAt(j);
            if(randomChars.contains(wordChar)) {
                System.out.println("contains");
                System.out.println(wordChar);
                randomChars.remove((Character) wordChar);
                System.out.println(randomChars);
            }
            else {
                //the word from the dictionary contains a char that is not available from the random chars
                return false;
            }

            //we need to check if the char from the dictionary is in the
            //System.out.println(wordChar);
        }
        //There were enough random chars to build the word
        System.out.println("found word " + word);
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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