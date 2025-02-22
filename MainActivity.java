package ru.shepelev.game530;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.InputStreamReader;
import java.io.InterruptedIOException;

import java.net.ServerSocket;
import java.net.Socket;


public class MainActivity extends AppCompatActivity {
    private final static String FILE_NAME = "test.txt";
    ServerSocket serverSocket = null;
    TextView tWIpAddress;
    TextView textView;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        tWIpAddress = findViewById(R.id.tWIpAddress);
        textView = findViewById(R.id.textView);

        btn = findViewById(R.id.btn);

        WifiManager wifiManager = (WifiManager)MainActivity.this.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
            String ipAddressStr = String.format("%d.%d.%d.%d",
                    (ipAddress & 0xff),
                    (ipAddress >> 8 & 0xff),
                    (ipAddress >> 16 & 0xff),
                    (ipAddress >> 24 & 0xff));
            tWIpAddress.setText(ipAddressStr);
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setText("Ожидание...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runTcpServer();
                    }
                }).start();
            }
        });

    }
    
    private File getExternalPath() {
        return new File(getExternalFilesDir(null), FILE_NAME);
    }

    private void runTcpServer() {
        try {
            serverSocket = new ServerSocket(8888);
            Socket socket = serverSocket.accept();
            
            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
            
            FileOutputStream fos = new FileOutputStream(getExternalPath());
            
            while (inputStreamReader.ready()) {
                fos.write(inputStreamReader.read());
            }
            
            textView.post(new Runnable() {
                public void run() {
                    textView.setText("OK");
                }
            });
            
            socket.close();
        } catch (InterruptedIOException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
