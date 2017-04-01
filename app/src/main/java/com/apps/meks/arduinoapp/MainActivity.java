package com.apps.meks.arduinoapp;

import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.apps.meks.arduinoapp.driver.UsbSerialDriver;
import com.apps.meks.arduinoapp.driver.UsbSerialPort;
import com.apps.meks.arduinoapp.driver.UsbSerialProber;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText eingabeEditText;
    private Button sendButton;
    private ListView listview;
    private List<String> list = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        eingabeEditText = (EditText) findViewById(R.id.eingabe);
        sendButton = (Button) findViewById(R.id.sendButton);
        listview = (ListView) findViewById(R.id.listview);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData();
            }
        });
    }

    private void sendData() {

        // Find all available drivers from attached devices.
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);

        if (availableDrivers.isEmpty()) {
            i++;
            list.add(0, new String(i + ". " + "NO DEVICES ATTACHED"));
            adapter.notifyDataSetChanged();
            return;
        }

        // Open a connection to the first available driver.
        UsbSerialDriver driver = availableDrivers.get(0);
        UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
        if (connection == null) {
            return;
        }

        String userInput = eingabeEditText.getText().toString();
        byte[] b = new byte[0];
        try {
            b = userInput.getBytes("US-ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try{
            UsbSerialPort port = driver.getPorts().get(0);
            port.open(connection);
            port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            port.write(b,1);

            byte buffer[] = new byte[30];
            port.read(buffer, 1000);
            i++;
            String sBuffer = new String(buffer);
            String result = new String(i + ". " + sBuffer);
            list.add(0, result);

        }catch (IOException e){
            i++;
            list.add(0, new String(i + ". " + "Fehler beim Senden der Daten"));

        }
        adapter.notifyDataSetChanged();
    }
}