package com.example.renwu.qrfinddata;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.renwu.qrserver.R;
import com.example.renwu.qrfinddata.WebServiceUtil_FindData;
import com.example.renwu.qrviewdata.RefreshableView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.List;

/**
 * Created by lin on 2016/6/20.
 */
public class FindDataList extends AppCompatActivity {
    private static final String NAMESPACE = "http://tempuri.org/";
    private static final String URL = "http://180.177.249.46/";
    private String data;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchdataview);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Bundle DataSearch =this.getIntent().getExtras();
        final String sex = DataSearch.getString("ext_search");
        Thread networkThread = new Thread(new Runnable() {
            public void run() {
                try {
                    SoapObject request = new SoapObject(NAMESPACE, "find_one");
                    request.addProperty("Data", sex);
                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);
                    HttpTransportSE ht = new HttpTransportSE(URL);
                    ht.debug = true;
                    ht.call(NAMESPACE + "find_one", envelope);
                    final SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                    data = response.toString();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(),sex , Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        networkThread.start();
    }



}
