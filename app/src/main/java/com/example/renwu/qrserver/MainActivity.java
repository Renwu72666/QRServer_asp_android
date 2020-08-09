package com.example.renwu.qrserver;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.renwu.qrcdoescanner.CodeScanner;
import com.example.renwu.qrfinddata.FindDataList;
import com.example.renwu.qrlogin.loginpage;
import com.example.renwu.qrviewdata.DataListView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    CodeScanner cs;
    private static final String NAMESPACE = "http://tempuri.org/";
    private static final String URL = "http://180.177.249.46/";
    private static final String SOAP_ACTION = "http://tempuri.org/Insert";
    private static final String METHOD_NAME = "Insert";
    private String Rturn_Data;
    private static final String NAMESPACE_View = "http://tempuri.org/";
    private static final String URL_View = "http://180.177.249.46/";
    private static final String SOAP_ACTION_View = "http://tempuri.org/showData";
    private static final String METHOD_NAME_View = "showData";
    private TextView showtxt;
    private TextView textWeatherAfterday;
    private String data;
    private HashMap<String, String> session;
    @SuppressWarnings("unchecked")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        ImageButton btn_Insert = (ImageButton) findViewById(R.id.btn_Insert);
        ImageButton btn_ViewData = (ImageButton) findViewById(R.id.btn_ViewData);
        ImageButton btn_DeleteData= (ImageButton) findViewById(R.id.btn_DeleteData);
        session =  (HashMap<String, String>) this.getIntent().
                getBundleExtra("session").getSerializable("sessionid");
        final String urse=session.get("urse");
        btn_Insert.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View v) {
                CodeScanner();
            }
        });
        btn_ViewData.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View v) {
                session.put("urse", urse);
                Intent intent  = new Intent();
                intent .setClass(MainActivity.this, DataListView.class);
                Bundle map = new Bundle();
                map.putSerializable("sessionid", session);
                intent.putExtra("session", map);
                startActivity(intent);
            }
        });

        btn_DeleteData.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View v) {
                final View item = LayoutInflater.from(MainActivity.this).inflate(R.layout.deletedata, null);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(getString(R.string.Del_setTitle))
                        .setView(item)
                       .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               final EditText edt_DeleteData = (EditText) item.findViewById(R.id.edt_DeleteData);
                               if (edt_DeleteData.getText().toString().equals("")) {
                                   Toast.makeText(getApplicationContext(), getString(R.string.Datd_Null), Toast.LENGTH_SHORT).show();
                               } else {
                                   Thread networkThread = new Thread(new Runnable() {
                                       public void run() {
                                           try {
                                               SoapObject request = new SoapObject(NAMESPACE, "delete");
                                               request.addProperty("QRId", edt_DeleteData.getText().toString());
                                               SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                                               envelope.dotNet = true;
                                               envelope.setOutputSoapObject(request);
                                               HttpTransportSE ht = new HttpTransportSE(URL);
                                               ht.debug = true;
                                               ht.call(NAMESPACE + "delete", envelope);
                                               SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                                               data = response.toString();
                                               runOnUiThread(new Runnable() {
                                                   public void run() {
                                                       Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT).show();
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
                       })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Toast.makeText(getApplicationContext(), getString(R.string.cancel_data), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
            }
        });
    }
    private void show()
    {
        String weatherToday = "";
        final String Account_urse=session.get("urse");
        List<String> detail = WebServiceUtil.getDataList(Account_urse);
        String date = detail.toString();
        String[] rshString = new String[detail.size()];
        int count =detail.size();
        String size = detail.get(0);
        for(int i = 1; i < detail.size(); i++)
        {
            weatherToday=weatherToday+detail.get(i)+'\n';
        }
        showtxt.setText(weatherToday);
    }
    public void clickToast(String data)
    {
        Toast.makeText(this,data, Toast.LENGTH_LONG).show();
    }
    private void CodeScanner() {
        cs = new CodeScanner(MainActivity.this, codeReaderListener);
        cs.setMode(CodeScanner.Mode.QR_CODE_MODE);
        cs.setCrop(true);
        cs.scan();
    }
    CodeScanner.CodeReaderListener codeReaderListener = new CodeScanner.CodeReaderListener(){
        @Override
        public void codeReadResult(final String type, final String data) {
        }
    };
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        cs.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (resultCode == RESULT_OK) {
                final String contents = data.getStringExtra("SCAN_RESULT");
                Thread networkThread = new Thread(new Runnable() {
                    public void run() {
                        try {
                            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                            request.addProperty("Data", contents.toString());
                            final String Insert_urse=session.get("urse");
                            request.addProperty("Account_name",Insert_urse);
                            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                            envelope.dotNet = true;
                            envelope.setOutputSoapObject(request);
                            HttpTransportSE ht = new HttpTransportSE(URL);
                            ht.debug = true;
                            ht.call(NAMESPACE + METHOD_NAME, envelope);
                            final SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                            Rturn_Data = response.toString();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    clickToast(Rturn_Data);
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                networkThread.start();
            }

        } else {
            Toast.makeText(this,  getString(R.string.cancel_scan), Toast.LENGTH_LONG).show();
        }
    }
}

