package com.pavels.house;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiInfo;
import android.content.Intent;
import java.lang.Math;
import java.net.IDN;
import java.net.NetworkInterface;

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

// 1) WIFI SSID: - ICON in design make "visible" one other should be "gone"
// https://stackoverflow.com/questions/21391395/get-ssid-when-wifi-is-connected > Get SSID
// Get SSID from MYSQL and check if connected wifi is in local network
// Is Wireguard tunnel active ???
// Is here ping to public IP ? > Unsecure if !ping then show No connection

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    /*if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals (action)){
        NetworkInfo netInfo = Intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        if (ConnectivityManager.TYPE_WIFI == netInfo.getType()) {
            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifiManager.getConnectionInfo();
            String ssid = info.getSSID();
        }
    }*/
    

    public void ShowMenu(View view)
    {

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
                return 3;
        }
    }

    public boolean internetIsConnected() {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
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

    public void onClick(View view)
    {

        ImageView no_connection = (ImageView) findViewById(R.id.No_Connection);
        ImageView unsecure = (ImageView) findViewById(R.id.Unsecure);
        ImageView wifi = (ImageView) findViewById(R.id.WIFI);
        ImageView vpn = (ImageView) findViewById(R.id.VPN);
        ImageView mobile = (ImageView) findViewById(R.id.Mobile);

        //int ID = (int)(Math.random() * 4);
        //Log.d("Random", String.valueOf(ID));
        int connected = 0;
        int NetworkInterface = isNetworkConnected();

        Log.d("Network - ", String.valueOf(NetworkInterface));
        Log.d("Space - ", String.valueOf(SpaceIsConnected()));
        Log.d("O2 - ", String.valueOf(O2IsConnected()));
        Log.d("Internet - ", String.valueOf(internetIsConnected()));

        if(NetworkInterface == 2) { // WIFI - WIFI or VPN or UNSECURE
            if (SpaceIsConnected()){ // WIFI or VPN
                // get wifi connection name
                if(true) {
                    // TEST IF NAME OF WIFI IS IN HOME NETWORK
                    connected = 1; // WIFI
                }
                else { // VPN OR UNSECURE
                    // check if VPN is active
                    if (checkVPN()) connected = 2; // WIFI or VNP or UNSECURE
                    else {
                        if (O2IsConnected()) connected = 3; // WIFI or VPN or UNSECURE
                        else connected = 4;
                    }
                }
            }
        }
        else {
            if (NetworkInterface == 1){ // MOBILE - UNSECURE
                if (SpaceIsConnected()) connected = 2; // VPN
                else {
                    if (O2IsConnected()) connected = 3; // VPN or UNSECURE
                    else connected = 4;
                }
            }
            else connected = 4;
        }
        switch((int)connected) {
            case 1:
                wifi.setVisibility(View.VISIBLE);
                vpn.setVisibility(View.GONE);
                unsecure.setVisibility(View.GONE);
                no_connection.setVisibility(View.GONE);
                break;
            case 2:
                wifi.setVisibility(View.GONE);
                unsecure.setVisibility(View.GONE);
                vpn.setVisibility(View.VISIBLE);
                no_connection.setVisibility(View.GONE);
                break;
            case 3:
                vpn.setVisibility(View.GONE);
                unsecure.setVisibility(View.VISIBLE);
                wifi.setVisibility(View.GONE);
                no_connection.setVisibility(View.GONE);
                break;
            default:
                wifi.setVisibility(View.GONE);
                vpn.setVisibility(View.GONE);
                unsecure.setVisibility(View.GONE);
                no_connection.setVisibility(View.VISIBLE);
                break;
        }
    }


}

