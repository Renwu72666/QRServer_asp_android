package com.example.renwu.qrviewdata;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.renwu.qrserver.R;
import com.example.renwu.qrserver.WebServiceUtil;
import com.example.renwu.util.util_user;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.HashMap;
import java.util.List;

/**
 * Created by RenWu on 2016/6/18.
 */

public class DataListView extends AppCompatActivity {
    private TextView showtxt;
    RefreshableView refreshableView;
    ListView listView;
    ArrayAdapter<String> adapter;
    List<String> detail = WebServiceUtil.getDataList(util_user.id);
    String [] items = new String[detail.size()/2];
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datalistview);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Show_List();
        refreshableView = (RefreshableView) findViewById(R.id.refreshable_view);
        listView = (ListView) findViewById(R.id.list_view);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);
        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                refreshableView.finishRefreshing();
            }
        }, 0);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView arg0, View arg1, int arg2, long arg3) {
                View item = LayoutInflater.from(DataListView.this).inflate(R.layout.viewclickdata, null);
                EditText view_qrid = (EditText) item.findViewById(R.id.view_qrid);
                TextView view_data = (TextView) item.findViewById(R.id.view_data);
                view_data.setAutoLinkMask(Linkify.ALL);
                int num=(int) arg3;
                if((int) arg3 == 0)
                {
                    view_qrid.setText(detail.get(num).toString());
                    view_data.setText(detail.get(num+1).toString());
                }else {
                    view_qrid.setText(detail.get(num*2).toString());
                    view_data.setText(detail.get(num*2+1).toString());
                }
                view_qrid.setInputType(InputType.TYPE_NULL);
                new AlertDialog.Builder(DataListView.this)
                        .setTitle(getString(R.string.View_setTitle))
                        .setView(item)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
            }

        });
    }
    private void Show_List(){
        String date = detail.toString();
        int count =detail.size();
        String size = detail.get(0);
        if(detail.get(0).toString().equals(-1 +"")){
            items[0]=getString(R.string.noData);
        }else {
            items[0] = getString(R.string.Id) + detail.get(0).toString() + '\n' + getString(R.string.Data) + detail.get(1).toString();
            for (int i = 1; i < detail.size() / 2; i++) {
                items[i] = getString(R.string.Id) + detail.get(i * 2).toString() + '\n' +getString(R.string.Data)  + detail.get(i * 2 + 1).toString();
                }
        }
    }
}
