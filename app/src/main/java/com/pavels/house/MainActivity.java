package com.pavels.house;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

@SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables", "DefaultLocale"})

public class MainActivity extends AppCompatActivity {

    Dialog Network_popup;
    Dialog WIFI_status;
    Dialog Home_WIFI_list;
    private Handler handler = new Handler();

    public int network_state = 0;
    public String WIFI_SSID = "";
    public int WIFI_RSSI = 0;
    public String WIFI_IP = "0.0.0.0";

    private static final String url = "jdbc:mysql://10.0.0.16:3306/Pavels House_v2";
    private static final String user = "root";
    private static final String pass = "Pavel31213";

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
        Home_WIFI_list = new Dialog(this);
        WIFI_status = new Dialog(this);
        handler.postDelayed(runnable, 0);

        Drop_Down();
        Network(findViewById(android.R.id.content).getRootView());
        GetWifiInfo();

    }

    private void GetWifiInfo(){

        WIFI_status.setContentView(R.layout.wifistatuspopup);

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        TextView tvWifiEnabled = (TextView) WIFI_status.findViewById(R.id.tvWifiEnabled);
        TextView tvWifiState = (TextView) WIFI_status.findViewById(R.id.tvWifiState);

        tvWifiEnabled.setText("isWifiEnabled(): " + wifiManager.isWifiEnabled());
        tvWifiState.setText(readtvWifiState(wifiManager));

        TextView tvWifiInfo = (TextView) WIFI_status.findViewById(R.id.tvWifiInfo);
        TextView tvSSID = (TextView) WIFI_status.findViewById(R.id.tvSSID);
        TextView tvRssi = (TextView) WIFI_status.findViewById(R.id.tvRssi);
        TextView tvIP = (TextView) WIFI_status.findViewById(R.id.tvIP);
        TextView tvFormattedIP1 = (TextView) WIFI_status.findViewById(R.id.tvFormattedIP);

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if(wifiInfo == null){
            tvWifiInfo.setText("wifiInfo == null !!!");
        }else{
            tvWifiInfo.setText(wifiInfo.toString());
            this.WIFI_SSID = wifiInfo.getSSID().substring(1, wifiInfo.getSSID().length() - 1);
            tvSSID.setText("SSID: " + wifiInfo.getSSID());
            this.WIFI_RSSI = (int) wifiInfo.getRssi();
            tvRssi.setText("Rssi: " + wifiInfo.getRssi() + " dBm");

            int ipAddress = wifiInfo.getIpAddress();

            String FormatedIpAddress2 = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));

            tvIP.setText("IP: " + wifiInfo.getIpAddress());

            tvFormattedIP1.setText("" + FormatedIpAddress2);
            this.WIFI_IP = FormatedIpAddress2;
        }
    }
//TODO - get from file
    public String[] GetHomeSSID(){

        String SSID_Home;
        SSID_Home = readFromFile("SSID_Home.txt", this);
        Log.d("SSID", SSID_Home);
        String[] SSID = SSID_Home.replace("[", "").replace("]", "").split(",");
        Log.d("SSID v souboru", Arrays.toString(Arrays.copyOfRange(SSID, 1, SSID.length)));
        int i;
        String[] mezi = Arrays.copyOfRange(SSID, 1, SSID.length);
        List<String> mezi_for = new ArrayList<String>();
        for (i = 0; i < mezi.length; i++) {
            mezi_for.add(mezi[i]);
        }
        String[] done = new String[ mezi_for.size() ];
        mezi_for.toArray( done );

        return done;

        //new String[]{"Kubisci", "Kubisci_Antena"};

    }

    private String readFromFile(String name, Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(name);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString); //.append("\n")
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

//TODO - append to file
    public void AddSSID(View view){
        Toast.makeText(MainActivity.this, "Přidávání SSID: "+this.WIFI_SSID, Toast.LENGTH_SHORT) .show();

        String previous = Arrays.stream((readFromFile("SSID_Home.txt", this).replace("[", "").replace("]", "") + "," + this.WIFI_SSID).split(",")).distinct().collect(Collectors.joining(","));

        //String[] modifiedArray = Arrays.copyOfRange(previous, 1, previous.length);

        Log.d("Adding SSID", previous);
        writeToFile("SSID_Home.txt", previous, this);
    }

    private void writeToFile(String name, String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(name, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public void Clean_Home_SSID(View view) {
        writeToFile("SSID_Home.txt", "",this);
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
            List<String> WIFIList = new ArrayList<>(Arrays.asList(GetHomeSSID()));
            return WIFIList.contains(this.WIFI_SSID);
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
        }
        catch (Exception ex) {
            Log.e("Error","getMobileIPAddress");
        }
        return "";
    }

    public void Drop_Down(){

        Spinner home_spin = findViewById(R.id.Home_Tab);
        String[] items_home = new String[]{"Nastavení", "Mapa", "Test"};
        ArrayAdapter<String> adapter_home = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items_home);
        home_spin.setAdapter(adapter_home);

        Spinner currency_spin = findViewById(R.id.Primary_Currency);
        String[] items_currency = new String[]{"Kč", "Euro", "BTC"};
        ArrayAdapter<String> adapter_currency = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items_currency);
        currency_spin.setAdapter(adapter_currency);

    }
//       Show current network status on main page
    public void Network(View view) {

        ImageView Connection_icon = (ImageView) this.findViewById(R.id.Connection_icon);

        Network_popup.setContentView(R.layout.networksatuspopup);

        if (checkVPN() && isNetworkConnected() == 1) { // && SpaceIsConnected()
            this.network_state = 2;
            Connection_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_vpn_lock_24));
        } else if (checkVPN() && isNetworkConnected() == 2) { // && SpaceIsConnected()
            this.network_state = 6;
            Connection_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_vpn_lock_24));
        } else if (isNetworkConnected() == 1) {
            this.network_state = 3;
            Connection_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_import_export_24));
        } else if (checkWIFI()) { // && SpaceIsConnected()
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

        Network_popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Network_popup.show();
    }

    public void ShowWIFIStatus(View view) {

        Log.d("Popup", "Show WIFI Status");
        WIFI_status.setContentView(R.layout.wifistatuspopup);

        WIFI_status.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WIFI_status.show();
        GetWifiInfo();
    }

    public void Network_Info(View view){
        Button close =(Button) view.findViewById(R.id.Network_info);
        ScrollView show =(ScrollView) Network_popup.findViewById(R.id.IconInfo);

        Log.d("Button", close.getText().toString());
        if (close.getText().toString().equals("Show Info")){
            close.setText("Close Info");
            show.setVisibility(View.VISIBLE);
        }
        else{
            close.setText("Show Info");
            show.setVisibility(View.GONE);
        }
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
                ConnectionName.setText("Domácí WIFI");
                ConnectionType.setVisibility(View.VISIBLE);
                connectionStrength.setVisibility(View.VISIBLE);
                ConnectionType.setText("SSID: " + this.WIFI_SSID);
                connectionStrength.setText(Integer.toString(this.WIFI_RSSI) + " dBm");
                connectionIP.setText("IP adresa: " + this.WIFI_IP);
                ConnectionIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_wifi_24));
                break;
            case 2:
                ConnectionName.setText("VPN připojení");
                ConnectionType.setText("Mobilní data");
                ConnectionType.setVisibility(View.VISIBLE);
                connectionStrength.setVisibility(View.GONE);
                connectionIP.setText("IP adresa: " + getMobileIPAddress());
                ConnectionIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_vpn_lock_24));
                break;
            case 3:
                ConnectionName.setText("Mobilní data");
                ConnectionType.setVisibility(View.GONE);
                connectionStrength.setVisibility(View.GONE);
                connectionIP.setText("IP adresa: " + getMobileIPAddress());
                ConnectionIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_import_export_24));
                break;
            case 4:
                ConnectionName.setText("Nezabezpečená síť");
                ConnectionType.setVisibility(View.VISIBLE);
                connectionStrength.setVisibility(View.VISIBLE);
                ConnectionType.setText("SSID: " + this.WIFI_SSID);
                connectionStrength.setText(Integer.toString(this.WIFI_RSSI)+ " dBm");
                connectionIP.setText("IP adresa: " + this.WIFI_IP);
                ConnectionIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_cloud_24));
                break;
            case 5:
                ConnectionName.setText("Žádné připojení");
                ConnectionType.setVisibility(View.GONE);
                connectionStrength.setVisibility(View.GONE);
                connectionIP.setText("IP adresa: 0.0.0.0");
                ConnectionIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_close_24));
                break;
            case 6:
                ConnectionName.setText("VPN připojení");
                ConnectionType.setVisibility(View.VISIBLE);
                connectionStrength.setVisibility(View.VISIBLE);
                ConnectionType.setText("SSID: " + this.WIFI_SSID);
                connectionStrength.setText(Integer.toString(this.WIFI_RSSI)+ " dBm");
                connectionIP.setText("IP adresa: " + this.WIFI_IP);
                ConnectionIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_vpn_lock_24));
                break;
        }
    }

    // TODO
    public void ShowWIFIlist(View view){

        Home_WIFI_list.setContentView(R.layout.homewifilist);
        Home_WIFI_list.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Home_WIFI_list.show();

    }

    public void ShowMap(View view){

        Intent myIntent = new Intent(view.getContext(), MapsActivity.class);
        startActivityForResult(myIntent, 0);

    }

}

