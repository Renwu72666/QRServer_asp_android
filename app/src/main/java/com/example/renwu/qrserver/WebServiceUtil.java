package com.example.renwu.qrserver;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Created by RenWu on 2016/6/18.
 */
public class WebServiceUtil {
    public static final String SERVICE_NS = "http://tempuri.org/";
    public static final String SERVICE_URL = "http://180.177.249.46/";

    public static List<String> getDataList(String Account){
        List<String> province = new ArrayList<String>();
        final String methodName = "showData";
        final HttpTransportSE ht = new HttpTransportSE(SERVICE_URL);
        ht.debug = true;
        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        SoapObject soapObject = new SoapObject(SERVICE_NS, methodName);
        soapObject.addProperty("Account_name",Account); //
        envelope.bodyOut = soapObject;
        envelope.dotNet = true;
        FutureTask<List<String>> task = new FutureTask<List<String>>(new Callable<List<String>>() {
            @Override
            public List<String> call() throws Exception {
                ht.call(SERVICE_NS + methodName, envelope);
                if(envelope.getResponse() != null){
                    SoapObject result = (SoapObject) envelope.bodyIn;
                    SoapObject detail = (SoapObject) result.getProperty(methodName+"Result");
                    return parseProvinceOrCity(detail);
                }
                return null;
            }
        });

        new Thread(task).start();
        try {
            return task.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }
    private static List<String> parseProvinceOrCity(SoapObject detail) {
        List<String> result = new ArrayList<String>();
        for(int i=0; i<detail.getPropertyCount(); i++){
            // parse data
            result.add(detail.getProperty(i).toString().split(",")[0]);
        }

        return result;
    }
}
