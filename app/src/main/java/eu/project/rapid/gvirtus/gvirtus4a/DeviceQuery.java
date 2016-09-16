package eu.project.rapid.gvirtus.gvirtus4a;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.nanotasks.BackgroundWork;
import com.nanotasks.Completion;
import com.nanotasks.Tasks;

import junit.framework.Test;

import java.io.IOException;

import cn.refactor.smileyloadingview.lib.SmileyLoadingView;

public class DeviceQuery extends AppCompatActivity {
    TextView logger;
    String ip,port;
    SmileyLoadingView load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_query);
        load = (SmileyLoadingView) findViewById(R.id.loading_view_dv);
        logger = (TextView) findViewById(R.id.text_log_dv);
        logger.setMovementMethod(new ScrollingMovementMethod());
        SharedPreferences prefs = getSharedPreferences("GVIRTUS_PREF", MODE_PRIVATE);
        ip = prefs.getString("ip", null);
        if (ip != null) {
            port  = prefs.getString("port", null);
        } else{
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,R.style.Dialog);
            alertDialogBuilder.setTitle("Alert!");
            alertDialogBuilder
                    .setMessage("Ip not found! Do you want setting now?")
                    .setCancelable(false)
                    .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            Intent setIntent= new Intent(getApplicationContext(),SettingsActivity.class);
                            startActivity(setIntent);
                            finish();
                        }
                    })
                    .setNegativeButton("No",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            dialog.cancel();
                        }
                    });
            alertDialogBuilder.show();
        }
        if (port == null){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,R.style.Dialog);
            alertDialogBuilder.setTitle("Alert!");
            alertDialogBuilder
                    .setMessage("Port not found! Do you want setting now?")
                    .setCancelable(false)
                    .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            Intent setIntent= new Intent(getApplicationContext(),SettingsActivity.class);
                            startActivity(setIntent);
                            finish();
                        }
                    })
                    .setNegativeButton("No",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            dialog.cancel();
                        }
                    });
            alertDialogBuilder.show();
        }

        Tasks.executeInBackground(getApplicationContext(), new BackgroundWork<Void>() {
            @Override
            public Void doInBackground() throws Exception {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        load.start();
                    }
                });
                deviceQuery(ip,port);
                return null;
            }
        }, new Completion<Void>() {
            @Override
            public void onSuccess(Context context, Void v) {
                logger.append("\nTask terminated");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        load.stop();
                    }
                });
            }
            @Override
            public void onError(Context context, Exception e) {
                logger.append("\nTask error");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        load.stop();
                    }
                });

            }
        });

    }

    public void deviceQuery(String ip, String port) throws IOException {

        CudaRtFrontend runtime = new CudaRtFrontend(ip, Integer.parseInt(port));
        String textDeviceQuery = "CUDA Device Query (Runtime API)\n";
        textDeviceQuery+="Starting...\nCUDA Device Query (Runtime API) version\n";
        int deviceCount = runtime.cudaGetDeviceCount();
        if (Util.ExitCode.getExit_code() != 0) {

            textDeviceQuery+="cudaGetDeviceCount returned " + Util.ExitCode.getExit_code() + " -> "
                    + runtime.cudaGetErrorString(Util.ExitCode.getExit_code());
            textDeviceQuery = textDeviceQuery +"cudaGetDeviceCount returned " + Util.ExitCode.getExit_code() + " -> "
                    + runtime.cudaGetErrorString(Util.ExitCode.getExit_code());
            textDeviceQuery+="Result = FAIL\n";
            final String finalTextDeviceQuery3 = textDeviceQuery;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    logger.append("\n" + finalTextDeviceQuery3);
                }
            });
            return;
        }
        if (deviceCount == 0) {
            textDeviceQuery+="There are no available device(s) that support CUDA\n";

        } else {
            textDeviceQuery+="Detected " + deviceCount + " CUDA Capable device(s)";
        }
        final String finalTextDeviceQuery = textDeviceQuery;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logger.append("\n" + finalTextDeviceQuery);
            }
        });
        for (int i = 0; i < deviceCount; i++) {
            textDeviceQuery="";
            textDeviceQuery+= "\n\n\n";
            runtime.cudaSetDevice(i);
            CudaDeviceProp deviceProp = new CudaDeviceProp();
            deviceProp = runtime.cudaGetDeviceProperties(i);
            int driverVersion = runtime.cudaDriverGetVersion();
            int runtimeVersion = runtime.cudaRuntimeGetVersion();

            textDeviceQuery = textDeviceQuery + "\nName Device " + (i+1) + ": " + deviceProp.getName();
            textDeviceQuery = textDeviceQuery +  "\nCUDA Driver Version/Runtime Version: " + driverVersion / 1000 + "."
                    + (driverVersion % 100) / 10 + " / " + runtimeVersion / 1000 + "." + (runtimeVersion % 100) / 10;
            textDeviceQuery = textDeviceQuery +"\nCUDA Capability Major/Minor version number: " + deviceProp.getMajor() + "."
                    + deviceProp.getMinor();
            textDeviceQuery = textDeviceQuery +"\nTotal amount of global memory: " + deviceProp.getTotalGlobalMem() / 1048576.0f
                    + " MBytes (" + deviceProp.getTotalGlobalMem() + " bytes)\n";
            textDeviceQuery = textDeviceQuery +"\nGPU Clock rate: " + deviceProp.getClockRate() * 1e-3f
                    + " Mhz (" + deviceProp.getClockRate() * 1e-6f + ")";
            textDeviceQuery = textDeviceQuery +"\nMemory Clock rate: " + deviceProp.getMemoryClockRate() * 1e-3f + " Mhz";
            textDeviceQuery = textDeviceQuery +"\nMemory Bus Width: " + deviceProp.getMemoryBusWidth() + "-bit";
            if (deviceProp.getL2CacheSize() == 1) {
                textDeviceQuery = textDeviceQuery +"\nL2 Cache Size: " + deviceProp.getL2CacheSize() + " bytes";
            }
            textDeviceQuery = textDeviceQuery +"\nMaximum Texture Dimension Size (x,y,z)  1D=(" + deviceProp.getMaxTexture1D()
                    + "), 2D=(" + deviceProp.getMaxTexture2D()[0] + "," + deviceProp.getMaxTexture2D()[1] + "), 3D=("
                    + deviceProp.getMaxTexture3D()[0] + ", " + deviceProp.getMaxTexture3D()[1] + ", "
                    + deviceProp.getMaxTexture3D()[2] + ")";
            textDeviceQuery = textDeviceQuery + "\nMaximum Layered 1D Texture Size, (num) layers  1D=(" + deviceProp.getMaxTexture1DLayered()[0]
                    + "), " + deviceProp.getMaxTexture1DLayered()[1] + " layers";
            textDeviceQuery = textDeviceQuery + "\nMaximum Layered 2D Texture Size, (num) layers  2D=("
                    + deviceProp.getMaxTexture2DLayered()[0] + ", " + deviceProp.getMaxTexture2DLayered()[1] + "), "
                    + deviceProp.getMaxTexture2DLayered()[2] + " layers";
            textDeviceQuery = textDeviceQuery + "\nTotal amount of constant memory: " + deviceProp.getTotalConstMem() + " bytes";
            textDeviceQuery = textDeviceQuery + "\nTotal amount of shared memory per block: " + deviceProp.getSharedMemPerBlock() + " bytes";
            textDeviceQuery = textDeviceQuery + "\nTotal number of registers available per block: " + deviceProp.getRegsPerBlock();
            textDeviceQuery = textDeviceQuery + "\nWarp size: " + deviceProp.getWarpSize();
            textDeviceQuery = textDeviceQuery +"\nMaximum number of threads per multiprocessor: " + deviceProp.getMaxThreadsPerMultiProcessor();
            textDeviceQuery = textDeviceQuery +"\nMaximum number of threads per block: " + deviceProp.getMaxThreadsPerBlock();
            textDeviceQuery = textDeviceQuery +"\nMax dimension size of a thread block (x,y,z): (" + deviceProp.getMaxThreadsDim()[0]
                    + ", " + deviceProp.getMaxThreadsDim()[1] + ", " + deviceProp.getMaxThreadsDim()[2] + ")";
            textDeviceQuery = textDeviceQuery +"\nMax dimension size of a grid size    (x,y,z): (" + deviceProp.getMaxGridSize()[0] + ", "
                    + deviceProp.getMaxGridSize()[1] + "," + deviceProp.getMaxGridSize()[2] + ")";
            textDeviceQuery = textDeviceQuery +"\nMaximum memory pitch: " + deviceProp.getMemPitch() + " bytes";
            textDeviceQuery = textDeviceQuery +"\nTexture alignment:  " + deviceProp.getTextureAlignment() + " bytes";
            if (deviceProp.getDeviceOverlap() == 0) {
                textDeviceQuery = textDeviceQuery +"\nConcurrent copy and kernel execution: No with "
                        + deviceProp.getAsyncEngineCount() + " copy engine(s)";
            }
            else {
                textDeviceQuery = textDeviceQuery +"\nConcurrent copy and kernel execution: Yes with "
                        + deviceProp.getAsyncEngineCount() + " copy engine(s)";
            }
            if (deviceProp.getKernelExecTimeoutEnabled() == 0) {
                textDeviceQuery = textDeviceQuery +"\nRun time limit on kernels: No";
            } else {
                textDeviceQuery = textDeviceQuery +"\nRun time limit on kernels: Yes";
            }
            int x = runtime.cudaDeviceCanAccessPeer(i, 1);
            textDeviceQuery = textDeviceQuery +"\nTest device " + i + " peer is " + x;
            final String finalTextDeviceQuery2 = textDeviceQuery;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    logger.append("\n" + finalTextDeviceQuery2);
                }
            });
            runtime.cudaDeviceReset();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    logger.append("\nCuda reset successfull");
                }
            });

        }
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
