package com.example.renwu.CreateAccount;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.renwu.qrlogin.loginpage;
import com.example.renwu.qrserver.MainActivity;
import com.example.renwu.qrserver.R;
import com.example.renwu.util.util_user;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by lin on 2016/6/21.
 */
public class CreateAccount extends AppCompatActivity {
    private EditText edt_Insert_Account;
    private EditText edt_Insert_Password;
    private Button btn_Insert_lgoin;
    private static final String NAMESPACE = "http://tempuri.org/";
    private static final String URL = "http://180.177.249.46/";
    private String data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginpage);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        edt_Insert_Account=(EditText)findViewById(R.id.edt_Insert_Account);
        edt_Insert_Password=(EditText)findViewById(R.id.edt_Insert_Password);
        btn_Insert_lgoin=(Button)findViewById(R.id.btn_Insert_lgoin);
        btn_Insert_lgoin.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View v) {
                if(edt_Insert_Account.getText().toString().equals("") || edt_Insert_Password.getText().toString().equals("") )
                {
                    Toast.makeText(getApplicationContext(), getString(R.string.Insert_false), Toast.LENGTH_SHORT).show();
                }else {
                    Thread networkThread = new Thread(new Runnable() {
                        public void run() {
                            try {

                                SoapObject request = new SoapObject(NAMESPACE, "Account_Insert");
                                request.addProperty("Account", edt_Insert_Account.getText().toString());
                                request.addProperty("Password", edt_Insert_Password.getText().toString());
                                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                                envelope.dotNet = true;
                                envelope.setOutputSoapObject(request);
                                HttpTransportSE ht = new HttpTransportSE(URL);
                                ht.debug = true;
                                ht.call(NAMESPACE + "Account_Insert", envelope);
                                final SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                                data = response.toString();
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        if(data.equals("新增成功"))
                                        {
                                            finish();
                                            Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT).show();
                                        }
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
        });
    }
}
