package eu.project.rapid.gvirtus.gvirtus4a;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class Help extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

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
