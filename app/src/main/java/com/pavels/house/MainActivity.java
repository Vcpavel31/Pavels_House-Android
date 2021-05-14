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
        new java.util.Random();

    }

    //if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals (action)){
        /*NetworkInfo netInfo = Intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        if (ConnectivityManager.TYPE_WIFI == netInfo.getType()) {
            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifiManager.getConnectionInfo();
            String ssid = info.getSSID();
        }*/
    //}*/
    public void ShowMenu(View view)
    {

    }
    public void onClick(View view)
    {

        ImageView no_connection = (ImageView) findViewById(R.id.No_Connection);
        ImageView unsecure = (ImageView) findViewById(R.id.Unsecure);
        ImageView wifi = (ImageView) findViewById(R.id.WIFI);
        ImageView vpn = (ImageView) findViewById(R.id.VPN);

        int ID = (int)(Math.random() * 4);
        Log.d("Random", String.valueOf(ID));

        switch((int)ID) {
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

