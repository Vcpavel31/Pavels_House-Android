package com.pavels.house;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.TextView;

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

    Dialog Network_popup;
    private Handler handler = new Handler();
    private int network_state = 0;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // The method you want to call every now and then.
            Network(findViewById(android.R.id.content).getRootView());
            Log.d("Handler", "Running");
            handler.postDelayed(this,30000); // 2000 = 2 seconds. This time is in millis.
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Network_popup = new Dialog(this);
        Network(findViewById(android.R.id.content).getRootView());
        handler.postDelayed(runnable, 30000);

    }

    public void ShowMenu(View view) {

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

    // TODO
    private boolean checkWIFI() {
        if (isNetworkConnected() == 2){
            // Get connection's names if they are matching to home, test ping to space
            return true;
        }
        return false;
    }

    public void Test_Button(View view){

    }

    public void Network(View view) {

        ImageView no_connection = (ImageView) findViewById(R.id.No_Connection);
        ImageView unsecure = (ImageView) findViewById(R.id.Unsecure);
        ImageView wifi = (ImageView) findViewById(R.id.WIFI);
        ImageView vpn = (ImageView) findViewById(R.id.VPN);
        ImageView mobile = (ImageView) findViewById(R.id.Mobile);

        if (checkVPN() && SpaceIsConnected()) {
            this.network_state = 2;
            vpn.setVisibility(View.VISIBLE);
            unsecure.setVisibility(View.GONE);
            wifi.setVisibility(View.GONE);
            no_connection.setVisibility(View.GONE);
            mobile.setVisibility(View.GONE);
        }
        else if (checkWIFI() && SpaceIsConnected()) {
            this.network_state = 1;
            wifi.setVisibility(View.VISIBLE);
            vpn.setVisibility(View.GONE);
            unsecure.setVisibility(View.GONE);
            no_connection.setVisibility(View.GONE);
            mobile.setVisibility(View.GONE);
        }
        else if (isNetworkConnected() == 1) {
            this.network_state = 3;
            wifi.setVisibility(View.GONE);
            vpn.setVisibility(View.GONE);
            unsecure.setVisibility(View.GONE);
            no_connection.setVisibility(View.GONE);
            mobile.setVisibility(View.VISIBLE);
        }
        else if (O2IsConnected()) {
            this.network_state = 4;
            wifi.setVisibility(View.GONE);
            vpn.setVisibility(View.GONE);
            unsecure.setVisibility(View.VISIBLE);
            no_connection.setVisibility(View.GONE);
            mobile.setVisibility(View.GONE);
        }
        else{
            this.network_state = 5;
            wifi.setVisibility(View.GONE);
            vpn.setVisibility(View.GONE);
            unsecure.setVisibility(View.GONE);
            no_connection.setVisibility(View.VISIBLE);
            mobile.setVisibility(View.GONE);
        }

    }

    public void ShowNetwork(View view) {
        Log.d("Popup", "Show Network");
        Network(findViewById(android.R.id.content).getRootView());
        TextView txtclose;
        TextView ConnectionName;
        ImageView ConnectionIcon;
        Network_popup.setContentView(R.layout.networksatuspopup);
        txtclose =(TextView) Network_popup.findViewById(R.id.txtclose);
        ConnectionName =(TextView) Network_popup.findViewById(R.id.ConnectionName);
        ConnectionIcon =(ImageView) Network_popup.findViewById(R.id.ConnectionIcon);
        switch(network_state) {
            case 1:
                ConnectionName.setText("WIFI connection");
                ConnectionIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_wifi_24));
                break;
            case 2:
                ConnectionName.setText("VPN connection");
                ConnectionIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_vpn_lock_24));
                break;
            case 3:
                ConnectionName.setText("Mobile data");
                ConnectionIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_import_export_24));
                break;
            case 4:
                ConnectionName.setText("Unsecure");
                ConnectionIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_cloud_24));
                break;
            case 5:
                ConnectionName.setText("No connection");
                ConnectionIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_close_24));
                break;
        }

        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Popup", "Close Network");
                Network_popup.dismiss();
            }
        });

        Network_popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Network_popup.show();
    }

}

