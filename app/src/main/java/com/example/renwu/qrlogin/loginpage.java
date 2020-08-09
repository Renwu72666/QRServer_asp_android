package com.example.renwu.qrlogin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.renwu.CreateAccount.CreateAccount;
import com.example.renwu.qrserver.MainActivity;
import com.example.renwu.qrserver.R;
import com.example.renwu.qrviewdata.DataListView;
import com.example.renwu.util.util_user;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.HashMap;

/**
 * Created by lin on 2016/6/20.
 */

public class loginpage extends AppCompatActivity {
    private static final String NAMESPACE = "http://tempuri.org/";
    private static final String URL = "http://180.177.249.46/";
    private HashMap<String, String> session =new HashMap<String, String>();
    private EditText edt_Account;
    private EditText edt_Password;
    private Button btn_lgoin;
    private String data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createaccount);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        edt_Account=(EditText)findViewById(R.id.edt_Account);
        edt_Password=(EditText)findViewById(R.id.edt_Password);
        btn_lgoin=(Button)findViewById(R.id.btn_lgoin);
        btn_lgoin.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View v) {
                checkUser();

            }
        });
    }
    public void Insert_Account(View view) {
        Intent intent  = new Intent();
        intent .setClass(loginpage.this, CreateAccount.class);
        startActivity(intent);
    }
    boolean checklogin=false;
    private void checkUser(){
        Thread networkThread = new Thread(new Runnable() {
            public void run() {
                try {
                    SoapObject request = new SoapObject(NAMESPACE, "Account_checkLogin");
                    request.addProperty("Account", edt_Account.getText().toString());
                    request.addProperty("Password", edt_Password.getText().toString());
                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);
                    HttpTransportSE ht = new HttpTransportSE(URL);
                    ht.debug = true;
                    ht.call(NAMESPACE + "Account_checkLogin", envelope);
                    SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                    data = response.toString();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if(data.equals("true"))
                            {
                                checklogin=true;
                                session.put("urse", edt_Account.getText().toString());
                                Toast.makeText(loginpage.this,  getString(R.string.Longin_success), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(loginpage.this, MainActivity.class);
                                Bundle map = new Bundle();
                                map.putSerializable("sessionid", session);
                                intent.putExtra("session", map);
                                startActivity(intent);
                                util_user.id=edt_Account.getText().toString();
                                finish();
                            }else {
                                Toast.makeText(loginpage.this,  getString(R.string.Longin_fail), Toast.LENGTH_SHORT).show();
                                checklogin=false;
                                edt_Password.setText("");
                            }
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
