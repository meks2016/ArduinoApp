package com.apps.meks.arduinoapp;

import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.apps.meks.arduinoapp.driver.UsbSerialDriver;
import com.apps.meks.arduinoapp.driver.UsbSerialPort;
import com.apps.meks.arduinoapp.driver.UsbSerialProber;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText eingabeEditText;
    private TextView ausgabeTextView;
    private TextView ausgabeTextView2;
    private Button sendButton;
    private Button sendAButton;
    private final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        eingabeEditText = (EditText) findViewById(R.id.eingabe);
        ausgabeTextView = (TextView) findViewById(R.id.ausgabeTextView);
        ausgabeTextView2 = (TextView) findViewById(R.id.ausgabe2TextView);
        sendButton = (Button) findViewById(R.id.sendButton);
        sendAButton = (Button) findViewById(R.id.sendAButton);




        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });

        sendAButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendA();
            }
        });

    }

    private void sendA() {
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
        if (availableDrivers.isEmpty()) {
            ausgabeTextView.setText("NO DEVICES ATTACHED");
            return;
        }
        // Open a connection to the first available driver.
        UsbSerialDriver driver = availableDrivers.get(0);
        UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
        if (connection == null) {
            // You probably need to call UsbManager.requestPermission(driver.getDevice(), ..)
            return;
        }

        //ERM
        String userInput = "A";
        byte[] b = new byte[0];
        try {
            b = userInput.getBytes("US-ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ausgabeTextView2.setText(String.valueOf(b));


        //byte[] b = new byte['A'];

        try{
            UsbSerialPort port = driver.getPorts().get(0);
            port.open(connection);
            port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            port.write(b,1);

            byte buffer[] = new byte[16];
            int numBytesRead = port.read(buffer, 1);

            ausgabeTextView.setText("Read " + String.valueOf(numBytesRead) + " bytes.");
        }catch (IOException e){
            ausgabeTextView.setText("Fehler beim Write");
        }


    }

    private void getData() {
        // Find all available drivers from attached devices.
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
        if (availableDrivers.isEmpty()) {
            ausgabeTextView.setText("NO DEVICES ATTACHED");
            return;
        }

        // Open a connection to the first available driver.
        UsbSerialDriver driver = availableDrivers.get(0);
        UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
        if (connection == null) {
            // You probably need to call UsbManager.requestPermission(driver.getDevice(), ..)
            return;
        }

        // Read some data! Most have just one port (port 0).
        UsbSerialPort port = driver.getPorts().get(0);
        try {
            port.open(connection);
            port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);

            byte buffer[] = new byte[16];
            int numBytesRead = port.read(buffer, 1000);
            ausgabeTextView.setText("Read " + numBytesRead + " bytes.");
            Log.d(TAG, "Read " + numBytesRead + " bytes.");
        } catch (IOException e) {
            Log.e(TAG, "Error!");
        } finally {
            //port.close();
        }
    }
}
