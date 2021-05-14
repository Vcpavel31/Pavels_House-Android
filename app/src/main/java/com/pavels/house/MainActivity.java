package com.pavels.house;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

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

    public void onClick(View view)
    {
        Change_Network_Status(view, 1);
    }

    public void Change_Network_Status(View view, int ID){
        ImageView No_Connection = (ImageView) findViewById(R.id.No_Connection);
        ImageView unsecure = (ImageView) findViewById(R.id.Unsecure);
        ImageView WIFI = (ImageView) findViewById(R.id.WIFI);
        ImageView VPN = (ImageView) findViewById(R.id.VPN);
        switch(ID){
            case 1:
                WIFI.setVisibility(View.VISIBLE);
                VPN.setVisibility(View.GONE);
                unsecure.setVisibility(View.GONE);
                No_Connection.setVisibility(View.GONE);
            case 2:
                WIFI.setVisibility(View.GONE);
                unsecure.setVisibility(View.GONE);
                VPN.setVisibility(View.VISIBLE);
                No_Connection.setVisibility(View.GONE);
            case 3:
                VPN.setVisibility(View.GONE);
                unsecure.setVisibility(View.VISIBLE);
                WIFI.setVisibility(View.GONE);
                No_Connection.setVisibility(View.GONE);
            default:
                WIFI.setVisibility(View.GONE);
                VPN.setVisibility(View.GONE);
                unsecure.setVisibility(View.GONE);
                No_Connection.setVisibility(View.VISIBLE);
        }

    }
}

