package eu.project.rapid.gvirtus.gvirtus4a;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    Button button,btn_restart;
    TextView text_ip,text_port;
    String ip,port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        SharedPreferences prefs = getSharedPreferences("GVIRTUS_PREF", MODE_PRIVATE);
        ip = prefs.getString("ip", null);
        port=prefs.getString("port",null);
        button = (Button) findViewById(R.id.button_save);
        btn_restart = (Button) findViewById(R.id.btn_restart);
        text_ip= (TextView) findViewById(R.id.text_ip);
        text_port= (TextView) findViewById(R.id.text_port);
        text_ip.setText(ip);
        text_port.setText(port);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (text_ip.getText().equals("")) return;
                if (text_port.getText().equals("")) return;
                SharedPreferences.Editor editor = getSharedPreferences("GVIRTUS_PREF", MODE_PRIVATE).edit();
                editor.putString("ip", text_ip.getText().toString());
                editor.putString("port", text_port.getText().toString());
                editor.apply();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SettingsActivity.this,R.style.Dialog);
                alertDialogBuilder.setTitle("Notice!");
                alertDialogBuilder
                        .setMessage("To save settings the app will be restarted")
                        .setCancelable(false)
                        .setNeutralButton("Ok",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                Intent mStartActivity = new Intent(getApplicationContext(), MainActivity.class);
                                int mPendingIntentId = 123456;
                                PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId, mStartActivity,
                                        PendingIntent.FLAG_CANCEL_CURRENT);
                                AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                                System.exit(0);
                            }
                        });

                alertDialogBuilder.show();
            }
        });

        btn_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent mStartActivity = new Intent(getApplicationContext(), MainActivity.class);
                int mPendingIntentId = 123456;
                PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId, mStartActivity,
                        PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                System.exit(0);
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(i);
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.devicequery:
                Intent intent2 = new Intent(this, DeviceQuery.class);
                startActivity(intent2);
                finish();
                return true;
            case R.id.about:
                Intent intent3 = new Intent(this,About.class);
                startActivity(intent3);
                finish();
                return true;
            case R.id.help:
                Intent intent4 = new Intent(this,Help.class);
                startActivity(intent4);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
