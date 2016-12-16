package com.example.marty_000.martijnheijstekfinalapp;
         import android.os.AsyncTask;
        import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStreamReader;
        import java.net.URL;

// Standard AsyncTask class
public class WeatherAsyncTask extends AsyncTask<URL,Integer,String> {

       // Retrieve the information from the server on a seperate thread
    @Override
    protected String doInBackground(URL... params) {
        URL url = params[0];
        BufferedReader rd = null;

        try {
            // Open the stream
            rd = new BufferedReader(new InputStreamReader(url.openStream()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        String result = "";
        String jsonLine;

        try {
            // Append all results
            while ((jsonLine = rd.readLine()) != null) {
                result += jsonLine;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        // Close stream
        finally {
            try {
                rd.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        // Returns the json dict as a string
        return result;
    }
}