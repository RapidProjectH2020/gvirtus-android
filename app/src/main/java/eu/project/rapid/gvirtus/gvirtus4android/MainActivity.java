package eu.project.rapid.gvirtus.gvirtus4android;

        import android.content.Context;
        import android.net.Uri;
        import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
        import android.content.Intent;
        import android.os.Bundle;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.google.android.gms.appindexing.Action;
        import com.google.android.gms.appindexing.AppIndex;
        import com.google.android.gms.common.api.GoogleApiClient;

        import java.io.IOException;
        import android.widget.ArrayAdapter;
        import android.widget.Spinner;
public class MainActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private Spinner spinner1;
    private long meanTimeGPU, meanTimeCPU;
   // private TextView text_resCPU;// = (TextView) findViewById(R.id.resCPU);

    public static float[] constantInit(float[] data, int size, float val) {
        for (int i = 0; i < size; ++i) {
            data[i] = val;
        }
        return data;
    }

    public static float[][] initMat(int righe, int colonne, float val) {
        float[][] data = new float[righe][colonne];
        for (int i = 0; i < righe; i++)
            for (int j = 0; j < colonne; j++)
                data[i][j] = val;

        return data;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final Button button = (Button) findViewById(R.id.form_button);
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner1.setPrompt("Select input size !");
        final TextView text_resGPU= (TextView) findViewById(R.id.resGPU);
        final TextView text_resCPU= (TextView) findViewById(R.id.resCPU);

        int position = spinner1.getSelectedItemPosition();

        button.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                final EditText edit_IP = (EditText)findViewById(R.id.edit_name);
                final EditText edit_PORT = (EditText)findViewById(R.id.edit_lastname);

                String ip = edit_IP.getText().toString();
                String port = edit_PORT.getText().toString();
                int position = spinner1.getSelectedItemPosition();

                System.out.println("*********ip " + ip + "    port " + port +  "Spinner 1 : "+ String.valueOf(spinner1.getSelectedItem()) + "position " + position );
                try {
             System.out.println("MatrixMul Execution : ");
                    String timeGPU;
                    String timeCPU;
                    switch(position) {



                        case 0:
                            meanTimeGPU = 0;
                            meanTimeCPU = 0;
                            for (int i = 0; i < 5; i++)
                                matrixMul(getApplicationContext(), 8, 12, 8, ip, port);

                            System.out.println("sum time GPU " + meanTimeGPU);
                            meanTimeGPU = meanTimeGPU/5;
                            System.out.println("mean time GPU " + meanTimeGPU);
                             timeGPU = Integer.toString((int) meanTimeGPU);
                            text_resGPU.setText(timeGPU);


                            meanTimeCPU = meanTimeCPU/5;
                             timeCPU = Integer.toString((int) meanTimeCPU);
                            text_resCPU.setText(timeCPU);

                            break;

                        case 1:
                            meanTimeGPU = 0;
                            meanTimeCPU = 0;

                            for (int i = 0; i < 5; i++)
                                matrixMul(getApplicationContext(), 16, 24, 16, ip, port);
                            meanTimeGPU = meanTimeGPU/5;
                             timeGPU = Integer.toString((int) meanTimeGPU);
                            text_resGPU.setText(timeGPU);
                            meanTimeCPU = meanTimeCPU/5;
                             timeCPU = Integer.toString((int) meanTimeCPU);
                            text_resCPU.setText(timeCPU);
                            break;
                        case 2:
                            meanTimeGPU = 0;
                            meanTimeCPU = 0;

                            for (int i = 0; i < 2; i++)
                                matrixMul(getApplicationContext(), 32, 48, 32, ip, port);
                            meanTimeGPU = meanTimeGPU/2;
                             timeGPU = Integer.toString((int) meanTimeGPU);
                            text_resGPU.setText(timeGPU);
                            meanTimeCPU = meanTimeCPU/2;
                             timeCPU = Integer.toString((int) meanTimeCPU);
                            text_resCPU.setText(timeCPU);
                            break;

                        case 3: meanTimeGPU = 0;
                            meanTimeCPU = 0;

                            for (int i = 0; i < 2; i++)
                                matrixMul(getApplicationContext(), 64, 96, 64, ip, port);
                            meanTimeGPU = meanTimeGPU/2;
                             timeGPU = Integer.toString((int) meanTimeGPU);
                            text_resGPU.setText(timeGPU);
                            meanTimeCPU = meanTimeCPU/2;
                             timeCPU = Integer.toString((int) meanTimeCPU);
                            text_resCPU.setText(timeCPU);
                            break;
                        default:
                            meanTimeGPU = 0;
                            meanTimeCPU = 0;

                            for (int i = 0; i < 5; i++)
                                matrixMul(getApplicationContext(), 8, 12, 8, ip, port);
                            System.out.println("sum time GPU " + meanTimeGPU);
                            meanTimeGPU = meanTimeGPU/5;
                            System.out.println("mean time GPU " + meanTimeGPU);
                            timeGPU = Integer.toString((int) meanTimeGPU);
                            text_resGPU.setText(timeGPU);

                            meanTimeCPU = meanTimeCPU/5;
                             timeCPU = Integer.toString((int) meanTimeCPU);
                            text_resCPU.setText(timeCPU);
                            break;
                    }

              //matrixMul(getApplicationContext(), 4, 6, 4, ip, port);
        } catch (IOException e) {
            e.printStackTrace();
        }

            }
        });




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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


    public  void matrixMul(Context x, int widthA, int heightA, int widthB, String ip, String port) throws IOException {


        long time1, time2;
        float valB = 0.01f;

        System.out.println("matrixMulDrv (Driver API)");
        CudaDrFrontend driver = new CudaDrFrontend(ip, Integer.parseInt(port));
       // CudaDrFrontend driver = new CudaDrFrontend("193.205.230.23", 9991);

        driver.cuInit(0);
        time1 = System.currentTimeMillis();
        String context = driver.cuCtxCreate(0, 0);
        driver.cuDeviceGetName(255, 0);
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
        int WA = (widthA * block_size); // Matrix A width
        int HA = (heightA * block_size); // Matrix A height
        int WB = (widthB * block_size); // Matrix B width
        int HB = WA; // Matrix B height
        int WC = WB; // Matrix C width
        int HC = HA; // Matrix C height
        System.out.println("WA:" + WA + " HA:" + HA + " WB:" + WB + " HB:" + HB);

        Toast.makeText(getApplicationContext(),
                "WA:" + WA + " HA:" + HA + " WB:" + WB + " HB:" + HB, Toast.LENGTH_LONG).show();


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
        System.out.println("Execution time WITH GPU in ms : " + (time2 - time1));
        meanTimeGPU = meanTimeGPU + (time2 - time1);
        boolean correct = true;
        System.out.println("Checking computed result for correctness...");
        System.out.println("check! Matrix[" + 0 + "]=" + h_C[0] + ", ref=" + WA * valB);

        for (int i = 0; i < WC * HC; i++) {
            if (Math.abs(h_C[i] - (WA * valB)) > 1e-2) {
                // System.out.println(i + "Error! Matrix["+i+"]="+h_C[i]+",
                // ref="+WA*valB+" error term is > 1e-4\n");
                correct = false;
            }
        }

        System.out.println(correct ? "Result = PASS" : "Result = FAIL");

        driver.cuMemFree(d_A);
        driver.cuMemFree(d_B);
        driver.cuMemFree(d_C);
        driver.cuCtxDestroy(context);

        time1 = System.currentTimeMillis();
        float[][] a = generaMatrice(HA, WA, 1.0f);
        float[][] b = generaMatrice(WB, HB, valB);

        float[][] matC = prodotto(a, b);
        time2 = System.currentTimeMillis();
        System.out.println("Execution time (CPU) in ms : " + (time2 - time1));
       meanTimeCPU = meanTimeCPU + (time2 - time1);
        System.out.println("check! Matrix[" + 0 + "]=" + matC[0][0] + "%.8f, ref=" + WA * valB);

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

    public static void deviceQuery() throws IOException {

        int exit_code = 1;
        CudaRtFrontend runtime = new CudaRtFrontend("193.205.230.23", 9991);

        System.out.println("Starting...\nCUDA Device Query (Runtime API) version (CUDART static linking)\n\n");

        int deviceCount = runtime.cudaGetDeviceCount();
        if (Util.ExitCode.getExit_code() != 0) {

            System.out.println("cudaGetDeviceCount returned " + Util.ExitCode.getExit_code() + " -> "
                    + runtime.cudaGetErrorString(Util.ExitCode.getExit_code()));
            System.out.println("Result = FAIL\n");
            return;
        }
        if (deviceCount == 0) {
            System.out.println("There are no available device(s) that support CUDA\n");

        } else {
            System.out.println("Detected " + deviceCount + " CUDA Capable device(s)");
        }
        for (int i = 0; i < deviceCount; i++) {
            runtime.cudaSetDevice(i);
            CudaDeviceProp deviceProp = new CudaDeviceProp();
            deviceProp = runtime.cudaGetDeviceProperties(i);
            System.out.println("\nDevice " + i + ": " + deviceProp.getName());
            int driverVersion = runtime.cudaDriverGetVersion();
            int runtimeVersion = runtime.cudaRuntimeGetVersion();
            System.out.println("CUDA Driver Version/Runtime Version:         " + driverVersion / 1000 + "."
                    + (driverVersion % 100) / 10 + " / " + runtimeVersion / 1000 + "." + (runtimeVersion % 100) / 10);
            System.out.println("CUDA Capability Major/Minor version number:  " + deviceProp.getMajor() + "."
                    + deviceProp.getMinor());
            System.out.println(
                    "Total amount of global memory:                 " + deviceProp.getTotalGlobalMem() / 1048576.0f
                            + " MBytes (" + deviceProp.getTotalGlobalMem() + " bytes)\n");
            System.out.println("GPU Clock rate:                              " + deviceProp.getClockRate() * 1e-3f
                    + " Mhz (" + deviceProp.getClockRate() * 1e-6f + ")");
            System.out.println(
                    "Memory Clock rate:                           " + deviceProp.getMemoryClockRate() * 1e-3f + " Mhz");
            System.out
                    .println("Memory Bus Width:                            " + deviceProp.getMemoryBusWidth() + "-bit");
            if (deviceProp.getL2CacheSize() == 1) {
                System.out.println(
                        "L2 Cache Size:                               " + deviceProp.getL2CacheSize() + " bytes");
            }
            System.out.println("Maximum Texture Dimension Size (x,y,z)         1D=(" + deviceProp.getMaxTexture1D()
                    + "), 2D=(" + deviceProp.getMaxTexture2D()[0] + "," + deviceProp.getMaxTexture2D()[1] + "), 3D=("
                    + deviceProp.getMaxTexture3D()[0] + ", " + deviceProp.getMaxTexture3D()[1] + ", "
                    + deviceProp.getMaxTexture3D()[2] + ")");
            System.out.println(
                    "Maximum Layered 1D Texture Size, (num) layers  1D=(" + deviceProp.getMaxTexture1DLayered()[0]
                            + "), " + deviceProp.getMaxTexture1DLayered()[1] + " layers");
            System.out.println("Maximum Layered 2D Texture Size, (num) layers  2D=("
                    + deviceProp.getMaxTexture2DLayered()[0] + ", " + deviceProp.getMaxTexture2DLayered()[1] + "), "
                    + deviceProp.getMaxTexture2DLayered()[2] + " layers");
            System.out.println(
                    "Total amount of constant memory:               " + deviceProp.getTotalConstMem() + " bytes");
            System.out.println(
                    "Total amount of shared memory per block:       " + deviceProp.getSharedMemPerBlock() + " bytes");
            System.out.println("Total number of registers available per block: " + deviceProp.getRegsPerBlock());
            System.out.println("Warp size:                                     " + deviceProp.getWarpSize());
            System.out.println(
                    "Maximum number of threads per multiprocessor:  " + deviceProp.getMaxThreadsPerMultiProcessor());
            System.out.println("Maximum number of threads per block:           " + deviceProp.getMaxThreadsPerBlock());
            System.out.println("Max dimension size of a thread block (x,y,z): (" + deviceProp.getMaxThreadsDim()[0]
                    + ", " + deviceProp.getMaxThreadsDim()[1] + ", " + deviceProp.getMaxThreadsDim()[2] + ")");
            System.out.println("Max dimension size of a grid size    (x,y,z): (" + deviceProp.getMaxGridSize()[0] + ", "
                    + deviceProp.getMaxGridSize()[1] + "," + deviceProp.getMaxGridSize()[2] + ")");
            System.out.println("Maximum memory pitch:                          " + deviceProp.getMemPitch() + " bytes");
            System.out.println(
                    "Texture alignment:                             " + deviceProp.getTextureAlignment() + " bytes");
            if (deviceProp.getDeviceOverlap() == 0)
                System.out.println("Concurrent copy and kernel execution:          No with "
                        + deviceProp.getAsyncEngineCount() + " copy engine(s)");
            else
                System.out.println("Concurrent copy and kernel execution:          Yes with "
                        + deviceProp.getAsyncEngineCount() + " copy engine(s)");
            if (deviceProp.getKernelExecTimeoutEnabled() == 0)
                System.out.println("Run time limit on kernels:                     No");
            else
                System.out.println("Run time limit on kernels:                     Yes");
            int x = runtime.cudaDeviceCanAccessPeer(i, 1);
            System.out.println("Test device " + i + " peer is " + x);
            runtime.cudaDeviceReset();
            System.out.println("Cuda reset successfull");
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://eu.project.rapid.gvirtus.gvirtus4android/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://eu.project.rapid.gvirtus.gvirtus4android/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}

