package eu.project.rapid.gvirtus.gvirtus4a;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.pierry.simpletoast.SimpleToast;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.nanotasks.BackgroundWork;
import com.nanotasks.Completion;
import com.nanotasks.Tasks;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;

import cn.refactor.smileyloadingview.lib.SmileyLoadingView;

public class MainActivity extends AppCompatActivity {

    private float meanTimeGPU, meanTimeCPU;
    Spinner spinner1, spinner2;
    Button btn_run, btn_all4, btn_test1, btn_test2, btn_all9;
    TextView text_resGPU, text_resCPU, log;
    int iteration = 0;
    String ip, port;
    SmileyLoadingView load;
    int wa = 0, wb = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        load = (SmileyLoadingView) findViewById(R.id.loading_view);
        log = (TextView) findViewById(R.id.text_log);
        log.setMovementMethod(new ScrollingMovementMethod());
        SharedPreferences prefs = getSharedPreferences("GVIRTUS_PREF", MODE_PRIVATE);
        ip = prefs.getString("ip", null);
        if (ip != null) {
            port = prefs.getString("port", null);
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.Dialog);
            alertDialogBuilder.setTitle("Alert!");
            alertDialogBuilder
                    .setMessage("Ip not found! Do you want setting now?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent setIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                            startActivity(setIntent);
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            alertDialogBuilder.show();
        }
        if (port == null) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.Dialog);
            alertDialogBuilder.setTitle("Alert!");
            alertDialogBuilder
                    .setMessage("Port not found! Do you want setting now?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent setIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                            startActivity(setIntent);
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            alertDialogBuilder.show();
        }
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        btn_run = (Button) findViewById(R.id.form_button);
        btn_all9 = (Button) findViewById(R.id.btn_all9);
        btn_test1 = (Button) findViewById(R.id.btn_test1);
        btn_test2 = (Button) findViewById(R.id.btn_test2);
        btn_all4 = (Button) findViewById(R.id.btn_all4);
        text_resGPU = (TextView) findViewById(R.id.resGPU);
        text_resCPU = (TextView) findViewById(R.id.resCPU);
        iteration = spinner2.getSelectedItemPosition() + 1;
        if (ip == null || port == null) {
            lockButton();
        }
        if (!check3gConnection()) {
            if (!checkWifiConnection()) {
                lockButton();
                SimpleToast.error(getApplicationContext(), "No client connections available!");
            }
        }


        btn_test1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lockButton();
                final String[] timeGPU = new String[1];
                final String[] timeCPU = new String[1];
                iteration = 5;
                wa = 8;
                wb = 12;
                spinner2.setSelection(iteration - 1);
                spinner1.setSelection(1);
                meanTimeGPU = 0;
                meanTimeCPU = 0;
                Tasks.executeInBackground(getApplicationContext(), new BackgroundWork<Void>() {
                    @Override
                    public Void doInBackground() throws Exception {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                text_resCPU.setText("");
                                text_resGPU.setText("");
                                load.start();
                            }
                        });
                        for (int i = 0; i < iteration; i++) {
                            final int finalI = i;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    log.append("\n\nReplication " + (finalI + 1) + " is running...");
                                    log.append("\n" + DateFormat.getDateTimeInstance().format(new Date()));
                                }
                            });
                            matrixMul(getApplicationContext(), wa, wb, wa, ip, port);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    meanTimeGPU = (meanTimeGPU / iteration) / 1000;
                                    timeGPU[0] = String.format("%.2f", meanTimeGPU);
                                    text_resGPU.setText(timeGPU[0] + " sec");
                                    meanTimeCPU = (meanTimeCPU / iteration) / 1000;
                                    timeCPU[0] = String.format("%.2f", meanTimeCPU);
                                    text_resCPU.setText(timeCPU[0] + " sec");
                                }
                            });
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    drawBarGraph(meanTimeCPU, meanTimeGPU);
                                }
                            });
                        }
                        return null;
                    }
                }, new Completion<Void>() {
                    @Override
                    public void onSuccess(Context context, Void v) {
                        SimpleToast.ok(getApplicationContext(), "Task completed!");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                load.stop();
                                unlockButton();
                                log.append("\n" + DateFormat.getDateTimeInstance().format(new Date()));

                            }
                        });
                    }

                    @Override
                    public void onError(Context context, Exception e) {
                        SimpleToast.error(getApplicationContext(), "Server not available!");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                load.stop();
                                unlockButton();
                                log.append("\n" + DateFormat.getDateTimeInstance().format(new Date()));
                            }
                        });
                    }
                });
            }
        });

        btn_test2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lockButton();
                final String[] timeGPU = new String[1];
                final String[] timeCPU = new String[1];
                iteration = 2;
                wa = 16;
                wb = 24;
                spinner2.setSelection(iteration - 1);
                spinner1.setSelection(2);
                meanTimeGPU = 0;
                meanTimeCPU = 0;
                Tasks.executeInBackground(getApplicationContext(), new BackgroundWork<Void>() {
                    @Override
                    public Void doInBackground() throws Exception {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                text_resCPU.setText("");
                                text_resGPU.setText("");
                                load.start();
                            }
                        });
                        for (int i = 0; i < iteration; i++) {
                            final int finalI = i;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    log.append("\n\nReplication " + (finalI + 1) + " is running...");
                                    log.append("\n" + DateFormat.getDateTimeInstance().format(new Date()));
                                }
                            });
                            matrixMul(getApplicationContext(), wa, wb, wa, ip, port);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    meanTimeGPU = (meanTimeGPU / iteration) / 1000;
                                    timeGPU[0] = String.format("%.2f", meanTimeGPU);
                                    text_resGPU.setText(timeGPU[0] + " sec");
                                    meanTimeCPU = (meanTimeCPU / iteration) / 1000;
                                    timeCPU[0] = String.format("%.2f", meanTimeCPU);
                                    text_resCPU.setText(timeCPU[0] + " sec");
                                }
                            });
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    drawBarGraph(meanTimeCPU, meanTimeGPU);
                                }
                            });
                        }
                        return null;
                    }
                }, new Completion<Void>() {
                    @Override
                    public void onSuccess(Context context, Void v) {
                        SimpleToast.ok(getApplicationContext(), "Task completed!");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                load.stop();
                                unlockButton();
                                log.append("\n" + DateFormat.getDateTimeInstance().format(new Date()));
                            }
                        });
                    }

                    @Override
                    public void onError(Context context, Exception e) {
                        SimpleToast.error(getApplicationContext(), "Server not available!");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                load.stop();
                                unlockButton();
                                log.append("\n" + DateFormat.getDateTimeInstance().format(new Date()));
                            }
                        });
                    }
                });
            }
        });

        btn_all4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lockButton();
                Tasks.executeInBackground(getApplicationContext(), new BackgroundWork<Void>() {
                    @Override
                    public Void doInBackground() throws Exception {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                text_resCPU.setText("");
                                text_resGPU.setText("");
                                load.start();
                                log.append("\n" + DateFormat.getDateTimeInstance().format(new Date()));
                            }
                        });
                        test_all4(ip, port);
                        return null;
                    }
                }, new Completion<Void>() {
                    @Override
                    public void onSuccess(Context context, Void v) {
                        SimpleToast.ok(getApplicationContext(), "Task completed!");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                load.stop();
                                unlockButton();
                                log.append("\n" + DateFormat.getDateTimeInstance().format(new Date()));
                            }
                        });
                    }

                    @Override
                    public void onError(Context context, Exception e) {
                        SimpleToast.error(getApplicationContext(), "Server not available!");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                load.stop();
                                unlockButton();
                                log.append("\n" + DateFormat.getDateTimeInstance().format(new Date()));
                            }
                        });
                    }
                });
            }
        });

        btn_all9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lockButton();
                Tasks.executeInBackground(getApplicationContext(), new BackgroundWork<Void>() {
                    @Override
                    public Void doInBackground() throws Exception {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                text_resCPU.setText("");
                                text_resGPU.setText("");
                                load.start();
                                log.append("\n" + DateFormat.getDateTimeInstance().format(new Date()));
                            }
                        });
                        test_all9(ip, port);
                        return null;
                    }
                }, new Completion<Void>() {
                    @Override
                    public void onSuccess(Context context, Void v) {
                        SimpleToast.ok(getApplicationContext(), "Task completed!");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                load.stop();
                                unlockButton();
                                log.append("\n" + DateFormat.getDateTimeInstance().format(new Date()));
                            }
                        });
                    }

                    @Override
                    public void onError(Context context, Exception e) {
                        SimpleToast.error(getApplicationContext(), "Server not available!");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                load.stop();
                                unlockButton();
                                log.append("\n" + DateFormat.getDateTimeInstance().format(new Date()));
                            }
                        });
                    }
                });
            }
        });

        btn_run.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                lockButton();
                int position = spinner1.getSelectedItemPosition();
                log.append("\n" + DateFormat.getDateTimeInstance().format(new Date()));
                log.append("\nMatrixMul Execution : ");
                final String[] timeGPU = new String[1];
                final String[] timeCPU = new String[1];

                switch (position) {
                    case 0:
                        wa = 4;
                        wb = 6;
                        break;

                    case 1:
                        wa = 8;
                        wb = 12;
                        break;

                    case 2:
                        wa = 16;
                        wb = 24;
                        break;

                    case 3:
                        wa = 24;
                        wb = 36;
                        break;

                    case 4:
                        wa = 32;
                        wb = 48;
                        break;

                    case 5:
                        wa = 40;
                        wb = 60;
                        break;

                    case 6:
                        wa = 48;
                        wb = 72;
                        break;

                    case 7:
                        wa = 56;
                        wb = 84;
                        break;

                    case 8:
                        wa = 64;
                        wb = 96;
                        break;

                    default:
                        wa = 8;
                        wb = 12;
                        break;
                }
                meanTimeGPU = 0;
                meanTimeCPU = 0;
                Tasks.executeInBackground(getApplicationContext(), new BackgroundWork<Void>() {
                    @Override
                    public Void doInBackground() throws Exception {
                        iteration = spinner2.getSelectedItemPosition() + 1;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                text_resCPU.setText("");
                                text_resGPU.setText("");
                                load.start();
                            }
                        });
                        for (int i = 0; i < iteration; i++) {
                            final int finalI = i;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    log.append("\n\nReplication " + (finalI + 1) + " is running...");
                                }
                            });
                            matrixMul(getApplicationContext(), wa, wb, wa, ip, port);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    meanTimeGPU = (meanTimeGPU / iteration) / 1000;
                                    timeGPU[0] = String.format("%.2f", meanTimeGPU);
                                    text_resGPU.setText(timeGPU[0] + " sec");
                                    meanTimeCPU = (meanTimeCPU / iteration) / 1000;
                                    timeCPU[0] = String.format("%.2f", meanTimeCPU);
                                    text_resCPU.setText(timeCPU[0] + " sec");
                                }
                            });
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    drawBarGraph(meanTimeCPU, meanTimeGPU);
                                }
                            });
                        }

                        return null;
                    }
                }, new Completion<Void>() {
                    @Override
                    public void onSuccess(Context context, Void v) {
                        SimpleToast.ok(getApplicationContext(), "Task completed!");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                load.stop();
                                unlockButton();
                                log.append("\n" + DateFormat.getDateTimeInstance().format(new Date()));
                            }
                        });
                    }

                    @Override
                    public void onError(Context context, Exception e) {
                        SimpleToast.error(getApplicationContext(), "Server not available!");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                load.stop();
                                unlockButton();
                                log.append("\n" + DateFormat.getDateTimeInstance().format(new Date()));
                            }
                        });
                    }
                });

            }
        });

    }

    public static float[] constantInit(float[] data, int size, float val) {
        for (int i = 0; i < size; ++i) {
            data[i] = val;
        }
        return data;
    }

    public void matrixMul(Context x, int widthA, int heightA, int widthB, String ip, String port) throws IOException {


        long time1, time2;
        final float valB = 0.01f;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                log.append("\nmatrixMulDrv (Driver API)");
            }
        });

        final CudaDrFrontend driver = new CudaDrFrontend(ip, Integer.parseInt(port));
        driver.cuInit(0);
        time1 = System.currentTimeMillis();
        String context = driver.cuCtxCreate(0, 0);
        final String nameDevice = driver.cuDeviceGetName(255, 0);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                log.append("\nDevice name: " + nameDevice);

            }
        });
        driver.cuDeviceGet(0);
        String p = "cuda-kernels/matrixMul_kernel64.ptx";
        String ptxSource = Util.readAssetFileAsString(x, p);
        int jitNumOptions = 3;
        int[] jitOptions = new int[jitNumOptions];

        // set up size of compilation log buffer
        jitOptions[0] = 4;// CU_JIT_INFO_LOG_BUFFER_SIZE_BYTES;
        long jitLogBufferSize = 1024;
        long jitOptVals0 = jitLogBufferSize;

        // set up pointer to the compilation log buffer
        jitOptions[1] = 3;// CU_JIT_INFO_LOG_BUFFER;

        char[] jitLogBuffer = new char[(int) jitLogBufferSize];
        char[] jitOptVals1 = jitLogBuffer;

        // set up pointer to set the Maximum # of registers for a particular
        // kernel
        jitOptions[2] = 0;// CU_JIT_MAX_REGISTERS;
        long jitRegCount = 32;
        long jitOptVals2 = jitRegCount;


        String cmodule = driver.cuModuleLoadDataEx(ptxSource, jitNumOptions, jitOptions, jitOptVals0,
                jitOptVals1, jitOptVals2);

        String cfunction = driver.cuModuleGetFunction(cmodule, "matrixMul_bs32_32bit");

        // allocate host memory for matrices A and B
        int block_size = 32; // larger block size is for Fermi and above
        final int WA = (widthA * block_size); // Matrix A width
        final int HA = (heightA * block_size); // Matrix A height
        final int WB = (widthB * block_size); // Matrix B width
        final int HB = WA; // Matrix B height
        int WC = WB; // Matrix C width
        int HC = HA; // Matrix C height
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                log.append("\nWA:" + WA + " HA:" + HA + " WB:" + WB + " HB:" + HB);
            }
        });

        int size_A = WA * HA;
        int mem_size_A = Float.SIZE / 8 * size_A;
        float[] h_A = new float[size_A];
        int size_B = WB * HB;
        int mem_size_B = Float.SIZE / 8 * size_B;
        float[] h_B = new float[size_B];
//System.out.prinf("%.2f", valB);

        h_A = constantInit(h_A, size_A, 1.0f);
        h_B = constantInit(h_B, size_B, valB);
        // allocate device memory
        String d_A;
        d_A = driver.cuMemAlloc(mem_size_A);
        String d_B;
        d_B = driver.cuMemAlloc(mem_size_B);
        driver.cuMemcpyHtoD(d_A, h_A, mem_size_A);
        driver.cuMemcpyHtoD(d_B, h_B, mem_size_B);
        // allocate device memory for result
        long size_C = WC * HC;
        float[] h_C = new float[WC * HC];

        long mem_size_C = Float.SIZE / 8 * size_C;
        String d_C;


        d_C = driver.cuMemAlloc(mem_size_C);
        Util.Dim3 grid = new Util.Dim3(WC / block_size, HC / block_size, 1);

        int offset = 0;
        // setup execution parameters


        driver.cuParamSetv(cfunction, offset, d_C, Util.Sizeof.LONG);

        offset += Util.Sizeof.LONG;
        driver.cuParamSetv(cfunction, offset, d_A, Util.Sizeof.LONG);
        offset += Util.Sizeof.LONG;
        driver.cuParamSetv(cfunction, offset, d_B, Util.Sizeof.LONG);
        offset += Util.Sizeof.LONG;

        int Matrix_Width_A = WA;
        int Matrix_Width_B = WB;
        int Sizeof_Matrix_Width_A = Util.Sizeof.INT;
        int Sizeof_Matrix_Width_B = Util.Sizeof.INT;


        driver.cuParamSeti(cfunction, offset, Matrix_Width_A);


        offset += Sizeof_Matrix_Width_A;


        driver.cuParamSeti(cfunction, offset, Matrix_Width_B);
        offset += Sizeof_Matrix_Width_B;


        driver.cuParamSetSize(cfunction, offset);


        driver.cuFuncSetBlockShape(cfunction, block_size, block_size, grid.z);

        driver.cuFuncSetSharedSize(cfunction, 2 * block_size * block_size * (Float.SIZE / 8));

        driver.cuLaunchGrid(cfunction, grid.x, grid.y);

        h_C = driver.cuMemcpyDtoH(d_C, mem_size_C);


        time2 = System.currentTimeMillis();

        final long finalTime = time2 - time1;
        final float timeF = Float.valueOf(finalTime) / 1000;
        final float[] finalH_C = h_C;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                log.append("\nExecution time WITH GPU in sec : " + Float.toString(timeF));
                log.append("\nChecking computed result for correctness...");
                log.append("\ncheck! Matrix[" + 0 + "]=" + finalH_C[0] + ", ref=" + WA * valB);
            }
        });
        meanTimeGPU = meanTimeGPU + (time2 - time1);
        boolean correct = true;
        for (int i = 0; i < WC * HC; i++) {
            if (Math.abs(h_C[i] - (WA * valB)) > 1e-2) {
                correct = false;
            }
        }
        final boolean finalCorrect = correct;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                log.append(finalCorrect ? "\nResult = PASS" : "\nResult = FAIL");

            }
        });

        driver.cuMemFree(d_A);
        driver.cuMemFree(d_B);
        driver.cuMemFree(d_C);
        driver.cuCtxDestroy(context);

        time1 = System.currentTimeMillis();
        float[][] a = generaMatrice(HA, WA, 1.0f);
        float[][] b = generaMatrice(WB, HB, valB);

        final float[][] matC = prodotto(a, b);
        time2 = System.currentTimeMillis();
        meanTimeCPU = meanTimeCPU + (time2 - time1);

        final long finalTime3 = time2 - time1;
        final float finalTimeF = Float.valueOf(finalTime3) / 1000;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                log.append("\nExecution time (CPU) in sec : " + Float.toString(finalTimeF));
                log.append("\ncheck! Matrix[" + 0 + "]=" + matC[0][0] + "%.8f, ref=" + WA * valB);
            }
        });
    }

    public static float[][] generaMatrice(int dim1, int dim2, float valB) {
        float[][] matrice = new float[dim1][dim2];
        for (int i = 0; i < matrice.length; i++)
            for (int j = 0; j < matrice[i].length; j++)
                matrice[i][j] = valB;
        return matrice;
    }

    public static float[][] prodotto(float[][] a, float[][] b) {
        if (a[0].length != b.length) {
            System.out.println("a[0].length" + a[0].length);
            System.out.println("b.length" + b.length);
            System.out.println("error!!!");
        }
        int n = a.length;
        int m = b.length;
        int l = b[0].length;
        float[][] prodotto = new float[n][l];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < l; j++)
                for (int x = 0; x < m; x++)
                    prodotto[i][j] += a[i][x] * b[x][j];
        return prodotto;
    }

    void test_all4(String ip, String port) throws IOException {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text_resCPU.setText("n/a");
                text_resGPU.setText("n/a");
                log.append("\nRun all tests");
            }
        });


        final int[] size_label = new int[4]; //new int[5]
        size_label[0] = 128 * 192;
        size_label[1] = 256 * 384;
        size_label[2] = 512 * 768;
        size_label[3] = 768 * 1152;
        final String[] size_label_strings = new String[4];
        size_label_strings[0] = "A[128,192] * B[128,128]";
        size_label_strings[1] = "A[256,384] * B[256,256]";
        size_label_strings[2] = "A[512,768] * B[512,512]";
        size_label_strings[3] = "A[768,1152] * B[768,768]";

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < size_label.length; i++)
                log.append("\nTest: " + (i + 1) + " size Matrix C result : " + size_label_strings[i]);
            }
        });

        final float[] TimeGPU_test = new float[4];
        final float[] TimeCPU_test = new float[4];


        meanTimeGPU = 0;
        meanTimeCPU = 0;
        matrixMul(getApplicationContext(), 4, 6, 4, ip, port);
        TimeGPU_test[0] = meanTimeGPU / 1000;
        TimeCPU_test[0] = meanTimeCPU / 1000;
        drawLineGraph(size_label, TimeCPU_test, TimeGPU_test, 1);

        meanTimeGPU = 0;
        meanTimeCPU = 0;
        matrixMul(getApplicationContext(), 8, 12, 8, ip, port);
        TimeGPU_test[1] = meanTimeGPU / 1000;
        TimeCPU_test[1] = meanTimeCPU / 1000;
        drawLineGraph(size_label, TimeCPU_test, TimeGPU_test, 2);

        meanTimeGPU = 0;
        meanTimeCPU = 0;
        matrixMul(getApplicationContext(), 16, 24, 16, ip, port);
        TimeGPU_test[2] = meanTimeGPU / 1000;
        TimeCPU_test[2] = meanTimeCPU / 1000;
        drawLineGraph(size_label, TimeCPU_test, TimeGPU_test, 3);

        meanTimeGPU = 0;
        meanTimeCPU = 0;
        matrixMul(getApplicationContext(), 24, 36, 24, ip, port);
        TimeGPU_test[3] = meanTimeGPU / 1000;
        TimeCPU_test[3] = meanTimeCPU / 1000;
        drawLineGraph(size_label, TimeCPU_test, TimeGPU_test, 4);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                for (int i = 0; i < TimeGPU_test.length; i++) {
                    log.append("\nTest: " + (i + 1) + " time GPU with GVirtuS: " + TimeGPU_test[i] + " sec");
                }
            }
        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < TimeCPU_test.length; i++) {
                    log.append("\nTest: " + (i + 1) + " time local CPU : " + TimeCPU_test[i] + "sec");
                }
            }
        });
    }



    void test_all9(String ip, String port) throws IOException {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text_resCPU.setText("n/a");
                text_resGPU.setText("n/a");
                log.append("\nRun all tests");
            }
        });


        final int[] size_label = new int[9]; //new int[5]
        size_label[0]=128 * 192;
        size_label[1]=256 * 384;
        size_label[2]=512 * 768;
        size_label[3]=768 * 1152;
        size_label[4]=1024 * 1536;
        size_label[5]= 1280 * 1920;
        size_label[6]= 1536 * 2304;
        size_label[7]= 1792 * 2688;
        size_label[8] = 2048 * 3072;

        final String[] size_label_strings = new String[9];
        size_label_strings[0]="A[128,192] * B[128,128]";
        size_label_strings[1]="A[256,384] * B[256,256]";
        size_label_strings[2]="A[512,768] * B[512,512]";
        size_label_strings[3]="A[768,1152] * B[768,768]";
        size_label_strings[4]="A[1024,1536] * B[1024,1024]";
        size_label_strings[5]="A[1280,1920] * B[1280,1280]";
        size_label_strings[6]="A[1536,2304] * B[1536,1536]";
        size_label_strings[7]="A[1792,2688] * B[1792,1792]";
        size_label_strings[8]="A[2048,3072] * B[2048,2048]";

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i<size_label.length; i++)
                log.append("\nTest: " + (i+1) +" size Matrix C result : " + size_label_strings[i]);
            }
        });

        final float[] TimeGPU_test = new float[9]; //new float[5]
        final float[] TimeCPU_test = new float[9]; //new float[5]


        meanTimeGPU = 0;
        meanTimeCPU = 0;
        matrixMul(getApplicationContext(), 4, 6, 4, ip, port);
        TimeGPU_test[0] = meanTimeGPU/ 1000;
        TimeCPU_test[0] = meanTimeCPU/ 1000;
        drawLineGraph(size_label,TimeCPU_test,TimeGPU_test,1);

        meanTimeGPU = 0;
        meanTimeCPU = 0;
        matrixMul(getApplicationContext(), 8, 12, 8, ip, port);
        TimeGPU_test[1] = meanTimeGPU/ 1000;
        TimeCPU_test[1] = meanTimeCPU/ 1000;
        drawLineGraph(size_label,TimeCPU_test,TimeGPU_test,2);

        meanTimeGPU = 0;
        meanTimeCPU = 0;
        matrixMul(getApplicationContext(), 16, 24, 16, ip, port);
        TimeGPU_test[2] = meanTimeGPU/ 1000;
        TimeCPU_test[2] = meanTimeCPU/ 1000;
        drawLineGraph(size_label,TimeCPU_test,TimeGPU_test,3);

        meanTimeGPU = 0;
        meanTimeCPU = 0;
        matrixMul(getApplicationContext(), 24, 36, 24, ip, port);
        TimeGPU_test[3] = meanTimeGPU/ 1000;
        TimeCPU_test[3] = meanTimeCPU/ 1000;
        drawLineGraph(size_label,TimeCPU_test,TimeGPU_test,4);

        meanTimeGPU = 0;
        meanTimeCPU = 0;
        matrixMul(getApplicationContext(), 32, 48, 32, ip, port);
        TimeGPU_test[4] = meanTimeGPU/ 1000;
        TimeCPU_test[4] = meanTimeCPU/ 1000;
        drawLineGraph(size_label,TimeCPU_test,TimeGPU_test,5);

        meanTimeGPU = 0;
        meanTimeCPU = 0;
        matrixMul(getApplicationContext(), 40, 60, 40, ip, port);
        TimeGPU_test[5] = meanTimeGPU/ 1000;
        TimeCPU_test[5] = meanTimeCPU/ 1000;
        drawLineGraph(size_label,TimeCPU_test,TimeGPU_test,6);

        meanTimeGPU = 0;
        meanTimeCPU = 0;
        matrixMul(getApplicationContext(), 48, 72, 48, ip, port);
        TimeGPU_test[6] = meanTimeGPU/ 1000;
        TimeCPU_test[6] = meanTimeCPU/ 1000;
        drawLineGraph(size_label,TimeCPU_test,TimeGPU_test,7);

        meanTimeGPU = 0;
        meanTimeCPU = 0;
        matrixMul(getApplicationContext(), 56, 84, 56, ip, port);
        TimeGPU_test[7] = meanTimeGPU/ 1000;
        TimeCPU_test[7] = meanTimeCPU/ 1000;
        drawLineGraph(size_label,TimeCPU_test,TimeGPU_test,8);

        meanTimeGPU = 0;
        meanTimeCPU = 0;
        matrixMul(getApplicationContext(), 64, 96, 64, ip, port);
        TimeGPU_test[8] = meanTimeGPU/ 1000;
        TimeCPU_test[8] = meanTimeCPU/ 1000;
        drawLineGraph(size_label,TimeCPU_test,TimeGPU_test,9);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i<size_label.length; i++)
                    log.append("\nTest: " + (i+1) +" time GPU with GVirtuS: " + TimeGPU_test[i]+" sec");
            }
        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i<size_label.length; i++)
                log.append("\nTest: " + (i+1) +" time local CPU : " + TimeCPU_test[i] + "sec");
            }
        });

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



    void drawBarGraph(float meanTimeCPU_test, float meanTimeGPU_test ) {

        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.removeAllSeries();
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(2);

        float maxY;
        if (meanTimeCPU_test > meanTimeGPU_test)
            maxY = meanTimeCPU_test;
        else
            maxY = meanTimeGPU_test;

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(maxY);

        //CPU
        BarGraphSeries<DataPoint> series1 = new BarGraphSeries<DataPoint>(new DataPoint[] { new DataPoint(1, meanTimeCPU_test)});
        //GPU
        BarGraphSeries<DataPoint> series2 = new BarGraphSeries<DataPoint>(new DataPoint[] { new DataPoint(1.3, meanTimeGPU_test)});

        series1.setTitle("CPU");
        series1.setColor(Color.BLUE);

        series2.setTitle("GPU");
        series2.setColor(Color.GREEN);

        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);


        graph.getGridLabelRenderer().setHorizontalAxisTitle("Size of test");
        graph.getGridLabelRenderer().setVerticalAxisTitle("Time in sec");

        graph.addSeries(series1);
        graph.addSeries(series2);

    }

    void drawLineGraph(int[] size_label, float [] TimeCPU_test, float[] TimeGPU_test,int number_of_tests){
        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.removeAllSeries();
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(size_label[size_label.length-1]+100000);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        float[] ord_TimeCPU_test = TimeCPU_test.clone();
        float[] ord_TimeGPU_test = TimeGPU_test.clone();
        Arrays.sort(ord_TimeGPU_test);
        Arrays.sort(ord_TimeCPU_test);
        if (ord_TimeCPU_test[ord_TimeCPU_test.length-1] > ord_TimeGPU_test[ord_TimeGPU_test.length-1])
        graph.getViewport().setMaxY(ord_TimeCPU_test[ord_TimeCPU_test.length-1]);
        else graph.getViewport().setMaxY(ord_TimeGPU_test[ord_TimeGPU_test.length-1]);

        //CPU
        DataPoint[] arrDataPointCPU = new DataPoint[number_of_tests];
        for (int i =0; i< number_of_tests;i++){
            arrDataPointCPU[i]=new DataPoint(size_label[i],TimeCPU_test[i]);
        }
        //GPU
        DataPoint[] arrDataPointGPU = new DataPoint[number_of_tests];
        for (int i =0; i< number_of_tests;i++){
            arrDataPointGPU[i]=new DataPoint(size_label[i],TimeGPU_test[i]);
        }

        LineGraphSeries<DataPoint> series1 = new LineGraphSeries<DataPoint>(arrDataPointCPU);
        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<DataPoint>(arrDataPointGPU);

        series1.setTitle("CPU");
        series1.setColor(Color.BLUE);
        series1.setDrawDataPoints(true);
        series1.setDataPointsRadius(10);

        series2.setTitle("GPU");
        series2.setColor(Color.GREEN);
        series2.setDrawDataPoints(true);
        series2.setDataPointsRadius(10);

        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);

        graph.getGridLabelRenderer().setHorizontalAxisTitle("Size of test");
        graph.getGridLabelRenderer().setVerticalAxisTitle("Time in sec");
        graph.addSeries(series1);
        graph.addSeries(series2);

    }
    void lockButton(){
        btn_run.setEnabled(false);
        btn_all4.setEnabled(false);
        btn_test2.setEnabled(false);
        btn_test1.setEnabled(false);
        btn_all9.setEnabled(false);
    }
    void unlockButton(){
        btn_run.setEnabled(true);
        btn_all4.setEnabled(true);
        btn_test2.setEnabled(true);
        btn_test1.setEnabled(true);
        btn_all9.setEnabled(true);
    }

    private Boolean check3gConnection() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo m3g = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (m3g.isConnected()) {
            return true;
        } else return false;
    }

    private Boolean checkWifiConnection() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWifi.isConnected()) {
            return true;
        } else return false;
    }

}
