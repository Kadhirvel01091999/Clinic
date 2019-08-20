package in.dentocare.clinic_management;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;

public class SignInAction extends AsyncTask<String,Void,String>{

    Context context;
    ProgressDialog p;

    public SignInAction(Context context, ProgressDialog p) {
        this.context = context;
        this.p = p;
    }



    @Override
    protected String doInBackground(String... arg0) {

            try {
                String username = arg0[0];
                String password = arg0[1];

                String link = "https://core-menu.000webhostapp.com/login.php";
                String data = URLEncoder.encode("username", "UTF-8") + "=" +
                        URLEncoder.encode(username, "UTF-8");
                data += "&" + URLEncoder.encode("password", "UTF-8") + "=" +
                        URLEncoder.encode(password, "UTF-8");

                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write(data);
                wr.flush();

                BufferedReader reader = new BufferedReader(new
                        InputStreamReader(conn.getInputStream()));

                StringBuffer sb = new StringBuffer();
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                reader.close();
                return sb.toString();
            } catch (Exception e) {
                return ("Exception: " + e.getMessage());
            }

    }
    @Override
    protected void onPostExecute(String result){
        p.dismiss();
        if(result.equals("")){
            buildDialog(context).show();
            Toast.makeText(context,"Login failed", Toast.LENGTH_SHORT).show();
        }else{
            Intent i = new Intent(context,UserInfo.class);
            i.putExtra("name",result);
            context.startActivity(i);
            ((LoginActivity) context).overridePendingTransition(R.anim.slide_in_right,android.R.anim.slide_out_right);
        }
    }

    public AlertDialog.Builder buildDialog(Context c) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Login failed!");
        builder.setMessage("Please check your username and password then try again.");
        return builder;
    }
}