package com.pavels.house;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Spinner;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

// TODO:
// Remove top panel
// How to make multiple screens ???
// Getting all info from mysql... Depending on connection - Probably some cache to file
// Some way of backuper...
// As example old web interface
// 1) Right top corner connection signalization (Local Wifi; VPN; Unsecure Connection; No connection)
// 2) User settings - Primary currency; Menu organisation; Add this device...; Link this device...; Sign-out
// 3) Admin settings - Editing roles
// 4) Films - Seen films; Adding new films; serials
// 5) Devices - Network map; Adding devices; Removing devices; Connections ???; IP list; Ping to devices
// 6) Mapy.cz - Integrate with custom points from DB... Maybe some grouping
// 7) Task manager - showing HDO; Printer task; ...
// 8) Password manager - Probably use OpenSource -> KeePass it has clients for Linux; WIN; PHP; Android

// 1) WIFI SSID:
// Get WIFI SSID
// Compare SSID

// 2) User settings
// Default screen
// Default Currency
// File Accessing

public class MainActivity extends AppCompatActivity {

    Dialog Network_popup;
    private Handler handler = new Handler();
    private Context context;

    public int network_state = 0;
    public String WIFI_SSID = "";
    public int WIFI_RSSI = 0;
    public String WIFI_IP = "0.0.0.0";

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            Network(findViewById(android.R.id.content).getRootView());
            GetWifiInfo();

            Log.d("Handler", "Running");
            handler.postDelayed(this,5000); // 2000 = 2 seconds. This time is in millis.

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Network_popup = new Dialog(this);
        handler.postDelayed(runnable, 0);

        Drop_Down();

    }

    private void GetWifiInfo(){
        TextView tvWifiEnabled = (TextView)findViewById(R.id.tvWifiEnabled);
        TextView tvWifiState = (TextView)findViewById(R.id.tvWifiState);
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        tvWifiEnabled.setText("isWifiEnabled(): " + wifiManager.isWifiEnabled());
        tvWifiState.setText(readtvWifiState(wifiManager));

        TextView tvWifiInfo = (TextView)findViewById(R.id.tvWifiInfo);
        TextView tvSSID = (TextView)findViewById(R.id.tvSSID);
        TextView tvRssi = (TextView)findViewById(R.id.tvRssi);
        TextView tvIP = (TextView)findViewById(R.id.tvIP);
        TextView tvFormattedIP1 = (TextView)findViewById(R.id.tvFormattedIP1);
        TextView tvFormattedIP2 = (TextView)findViewById(R.id.tvFormattedIP2);

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if(wifiInfo == null){
            tvWifiInfo.setText("wifiInfo == null !!!");
        }else{
            tvWifiInfo.setText(wifiInfo.toString());
            this.WIFI_SSID = wifiInfo.getSSID();
            tvSSID.setText("SSID: " + wifiInfo.getSSID());
            this.WIFI_RSSI = (int) wifiInfo.getRssi();
            tvRssi.setText("Rssi: " + wifiInfo.getRssi() + " dBm");

            int ipAddress = wifiInfo.getIpAddress();

            String FormatedIpAddress2 = String.format("%d.%d.%d.%d",
                    (ipAddress & 0xff),
                    (ipAddress >> 8 & 0xff),
                    (ipAddress >> 16 & 0xff),
                    (ipAddress >> 24 & 0xff));

            tvIP.setText("IP: " + wifiInfo.getIpAddress());

            tvFormattedIP1.setText("" + FormatedIpAddress2);
            this.WIFI_IP = FormatedIpAddress2;
        }
    }

    private int isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED) {
            return 1;
        }
        else {
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                return 2;
            } else
                return 0;
        }
    }

    public boolean SpaceIsConnected() {
        try {
            String command = "ping -c 1 10.0.0.16";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean O2IsConnected() {
        try {
            String command = "ping -c 1 90.177.36.121";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkVPN() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getNetworkInfo(ConnectivityManager.TYPE_VPN).isConnectedOrConnecting();
    }

    private boolean checkWIFI() {
        if (isNetworkConnected() == 2){



            return true;
        }
        return false;
    }

    private String readtvWifiState(WifiManager wm){
        String result = "";
        switch (wm.getWifiState()){
            case WifiManager.WIFI_STATE_DISABLED:
                result = "WIFI_STATE_DISABLED";
                break;
            case WifiManager.WIFI_STATE_DISABLING:
                result = "WIFI_STATE_DISABLING";
                break;
            case WifiManager.WIFI_STATE_ENABLED:
                result = "WIFI_STATE_ENABLED";
                break;
            case WifiManager.WIFI_STATE_ENABLING:
                result = "WIFI_STATE_ENABLING";
                break;
            case WifiManager.WIFI_STATE_UNKNOWN:
                result = "WIFI_STATE_UNKNOWN";
                break;
            default:
        }
        return result;
    }

    public static String getMobileIPAddress() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        return  addr.getHostAddress();
                    }
                }
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }

    public void Drop_Down(){

        Spinner home_spin = findViewById(R.id.Home_Tab);
        String[] items_home = new String[]{"Settings", "Map", "Test"};
        ArrayAdapter<String> adapter_home = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items_home);
        home_spin.setAdapter(adapter_home);

        Spinner currency_spin = findViewById(R.id.Primary_Currency);
        String[] items_currency = new String[]{"CZK", "EURO", "BTC"};
        ArrayAdapter<String> adapter_currency = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items_currency);
        currency_spin.setAdapter(adapter_currency);

    }
//       Show current network status on main page
    public void Network(View view) {

        ImageView Connection_icon = (ImageView) this.findViewById(R.id.Connection_icon);

        Network_popup.setContentView(R.layout.networksatuspopup);

        TextView ConnectionType = (TextView) Network_popup.findViewById(R.id.connectionName);
        TextView ConnectionName = (TextView) Network_popup.findViewById(R.id.ConnectionType);
        TextView connectionStrength = (TextView) Network_popup.findViewById(R.id.connectionStrength);
        TextView connectionIP = (TextView) Network_popup.findViewById(R.id.connectionIP);
        ImageView ConnectionIcon = (ImageView) Network_popup.findViewById(R.id.ConnectionIcon);

        if (checkVPN() && SpaceIsConnected() && isNetworkConnected() == 1) {
            this.network_state = 2;
            Connection_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_vpn_lock_24));
        } else if (checkVPN() && SpaceIsConnected() && isNetworkConnected() == 2) {
            this.network_state = 6;
            Connection_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_vpn_lock_24));
        } else if (isNetworkConnected() == 1) {
            this.network_state = 3;
            Connection_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_import_export_24));
        } else if (checkWIFI() && SpaceIsConnected()) {
            this.network_state = 1;
            Connection_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_wifi_24));
        } else if (O2IsConnected()) {
            this.network_state = 4;
            Connection_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_cloud_24));
        } else {
            this.network_state = 5;
            Connection_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_close_24));
        }

        PopupNetwork(this.network_state);

    }
//       Show current network status on popup page
    public void ShowNetwork(View view) {

        Log.d("Popup", "Show Network");

        Network(findViewById(android.R.id.content).getRootView());

        PopupNetwork(this.network_state);

//      For closing popup with close button
/*
        TextView txtclose =(TextView) Network_popup.findViewById(R.id.txtclose);
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Popup", "Close Network");
                Network_popup.dismiss();
            }
        });
*/

        Network_popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Network_popup.show();
    }

    private void PopupNetwork(int current){
        Network_popup.setContentView(R.layout.networksatuspopup);

        TextView ConnectionType =(TextView) Network_popup.findViewById(R.id.connectionName);
        TextView ConnectionName =(TextView) Network_popup.findViewById(R.id.ConnectionType);
        TextView connectionStrength =(TextView) Network_popup.findViewById(R.id.connectionStrength);
        TextView connectionIP =(TextView) Network_popup.findViewById(R.id.connectionIP);
        ImageView ConnectionIcon =(ImageView) Network_popup.findViewById(R.id.ConnectionIcon);

        switch(current) {
            case 1:
                ConnectionName.setText("WIFI connection");
                ConnectionType.setVisibility(View.VISIBLE);
                connectionStrength.setVisibility(View.VISIBLE);
                ConnectionType.setText("SSID: " + this.WIFI_SSID);
                connectionStrength.setText(Integer.toString(this.WIFI_RSSI) + " dBm");
                connectionIP.setText("IP: " + this.WIFI_IP);
                ConnectionIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_wifi_24));
                break;
            case 2:
                ConnectionName.setText("VPN connection");
                ConnectionType.setText("Mobile Data");
                ConnectionType.setVisibility(View.VISIBLE);
                connectionStrength.setVisibility(View.GONE);
                connectionIP.setText("IP: " + getMobileIPAddress());
                ConnectionIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_vpn_lock_24));
                break;
            case 3:
                ConnectionName.setText("Mobile data");
                ConnectionType.setVisibility(View.GONE);
                connectionStrength.setVisibility(View.GONE);
                connectionIP.setText("IP: " + getMobileIPAddress());
                ConnectionIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_import_export_24));
                break;
            case 4:
                ConnectionName.setText("Unsecure");
                ConnectionType.setVisibility(View.VISIBLE);
                connectionStrength.setVisibility(View.VISIBLE);
                ConnectionType.setText("SSID: " + this.WIFI_SSID);
                connectionStrength.setText(Integer.toString(this.WIFI_RSSI)+ " dBm");
                connectionIP.setText("IP: " + this.WIFI_IP);
                ConnectionIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_cloud_24));
                break;
            case 5:
                ConnectionName.setText("No connection");
                ConnectionType.setVisibility(View.GONE);
                connectionStrength.setVisibility(View.GONE);
                connectionIP.setText("IP: 0.0.0.0");
                ConnectionIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_close_24));
                break;
            case 6:
                ConnectionName.setText("VPN connection");
                ConnectionType.setVisibility(View.VISIBLE);
                connectionStrength.setVisibility(View.VISIBLE);
                ConnectionType.setText("SSID: " + this.WIFI_SSID);
                connectionStrength.setText(Integer.toString(this.WIFI_RSSI)+ " dBm");
                connectionIP.setText("IP: " + this.WIFI_IP);
                ConnectionIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_vpn_lock_24));
                break;
        }
    }
}

