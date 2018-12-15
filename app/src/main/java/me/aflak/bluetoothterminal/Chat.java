package me.aflak.bluetoothterminal;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import me.aflak.bluetooth.Bluetooth;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class Chat extends AppCompatActivity implements Bluetooth.CommunicationCallback {
    private String name;
    private Bluetooth b;
    private EditText message;
    private Button send;
    private TextView text;
    private ScrollView scrollView;
    private boolean registered = false;
    int count_msg = 0;
    private int[] address = {0, 21, 22, 23, 24};

    SimpleDateFormat sdf_jam = new SimpleDateFormat("kk");
    SimpleDateFormat sdf_menit = new SimpleDateFormat("mm");
    private static final boolean encrypt = true, decrypt = false;
    private FusedLocationProviderClient mFusedLocationClient;
    private BroadcastReceiver broadcastReceiver;
    double myLat, myLong;
    double myLat2, myLong2;
    String type_id,lifetime,number_hop,distance;





    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        Intent i = new Intent(getApplicationContext(),GPS_Service.class);
        stopService(i);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String lokasi = (String) intent.getExtras().get("coordinates");
                    String lokasi2[] = lokasi.split("/");
                    double lat,lon;
                    lat = Double.parseDouble(lokasi2[0]);
                    lon = Double.parseDouble(lokasi2[1]);
                    if(lat != 0.0 && lon != 0.0){
                        myLat2 = lat;
                        myLong2 = lon;
                    }

                }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent in_chat = getIntent();
        type_id = in_chat.getStringExtra("type_id");
//        Toast.makeText(this, String.valueOf(id), Toast.LENGTH_SHORT).show();
        if(type_id.equals("1")){
            lifetime = in_chat.getStringExtra("lifetime");
        }else if(type_id.equals("2")){
            number_hop = in_chat.getStringExtra("number_hop");
        }else{
            distance = in_chat.getStringExtra("distance");
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        text = (TextView) findViewById(R.id.text);
        message = (EditText) findViewById(R.id.message);
        send = (Button) findViewById(R.id.send);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        text.setMovementMethod(new ScrollingMovementMethod());
        send.setEnabled(false);

        b = new Bluetooth(this);
        b.enableBluetooth();

        b.setCommunicationCallback(this);

        int pos = getIntent().getExtras().getInt("pos");
        name = b.getPairedDevices().get(pos).getName();

        Display("Connecting...");
        b.connectToDevice(b.getPairedDevices().get(pos));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                myLat = location.getLatitude();
                myLong = location.getLongitude();
            }
        });

        Intent i =new Intent(getApplicationContext(),GPS_Service.class);
        startService(i);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String msg = message.getText().toString();
                message.setText("");
                String msg2 = encryptDecrypt(msg.substring(2, msg.length()), encrypt);
                String pre_msg[] = msg.split(" "); // 0  pesan
                String pre_source[] = name.split("_");  // nama_1
                int source = address[Integer.parseInt(pre_source[1])];
                int dst = address[Integer.parseInt(pre_msg[0])];
                ++count_msg;
                java.text.DecimalFormat nft = new java.text.DecimalFormat("#00.###");
                nft.setDecimalSeparatorAlwaysShown(false);
                String count = nft.format(count_msg);
                if(type_id.equals("1")){  // DTN time
                    String menit = sdf_menit.format(new Date());
                    String ready_msg = String.valueOf(dst)+String.valueOf(source)+count+menit+msg2;
                    b.send(ready_msg);
                    Display("You: " + ready_msg);

                }else if(type_id.equals("2")){  // DTN Hop
                    String jml_hop = "-0";
                    String ready_msg = String.valueOf(dst)+String.valueOf(source)+count+jml_hop+msg2;
                    b.send(ready_msg);
                    Display("You: " + ready_msg);

                }else{     // DTN Distance
                    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(Chat.this);
                    if (ActivityCompat.checkSelfPermission(Chat.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Chat.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }

                    mFusedLocationClient.getLastLocation().addOnSuccessListener(Chat.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            myLat = location.getLatitude();
                            myLong = location.getLongitude();

                        }
                    });
                    double temLat = Math.floor(myLat *100000)/100000,temLon = Math.floor(myLong * 100000)/100000;
                    String posisi = String.valueOf(temLat)+"/"+String.valueOf(temLon);
//                [2][2][2][~][~]
                    String ready_msg = String.valueOf(dst) + String.valueOf(source) + count + posisi +"/"+msg2;
                    b.send(ready_msg);
                    Display("You: " + ready_msg);


                }

            }
        });

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        registered = true;
    }

    private String encryptDecrypt(String input, boolean is_encrypt) {
        String msg = "", hasil = "";
        if (is_encrypt) {
            msg = Shift(input, 3, is_encrypt);
        } else {
            msg = input;
        }
        char[] key = {'M', 'S', 'G'};
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < msg.length(); i++) {
            output.append((char) (msg.charAt(i) ^ key[i % key.length]));
        }

        if (!is_encrypt) {
            hasil = Shift(output.toString(), 3, is_encrypt);
        } else {
            hasil = output.toString();
        }
        return hasil;
    }

    private String Shift(String msg, int shift, boolean is_encrypt) {
        String s = "";
        int len = msg.length();
        if (is_encrypt) {
            for (int x = 0; x < len; x++) {
                char c = (char) (msg.charAt(x) + shift);
                s += (char) (msg.charAt(x) + shift);
            }
        } else {

            for (int x = 0; x < len; x++) {
                char c = (char) (msg.charAt(x) - shift);
                s += (char) (msg.charAt(x) - shift);
            }
        }

        return s;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (registered) {
            unregisterReceiver(mReceiver);
            registered = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.close:
                b.removeCommunicationCallback();
                b.disconnect();
                Intent intent = new Intent(this, Type.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void Display(final String s) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.append(s + "\n");
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    @Override
    public void onConnect(BluetoothDevice device) {
        String jam = sdf_jam.format(new Date());
        String menit = sdf_menit.format(new Date());

        if(type_id.equals("1")){    // time
            b.send(jam);
            b.send("/");
            b.send(menit);
            b.send("/");
            b.send(lifetime);
        }else if(type_id.equals("2")){  //hop
            b.send(jam);
            b.send("/");
            b.send(menit);
            b.send("/");
            b.send(number_hop);
        }else{    // distance
            double temLat = Math.floor(myLat *100000)/100000,temLon = Math.floor(myLong * 100000)/100000;
            String lokasi = String.valueOf(temLat)+"/"+String.valueOf(temLon);
            b.send(jam);
            b.send("/");
            b.send(menit);
            b.send("/");
            b.send(distance);
            b.send("/");
            b.send(lokasi);
        }

        Display("Connected to " + device.getName() + " - " + device.getAddress());
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                send.setEnabled(true);
            }
        });
    }



    @Override
    public void onDisconnect(BluetoothDevice device, String message) {
        Display("Disconnected!");
        Display("Connecting again...");
        b.connectToDevice(device);
    }

    @Override
    public void onMessage(String message) {
        String receive;
        String src = message.substring(2,4);
        if(type_id.equals("1") || type_id.equals("2")){  //lifetime and hop
            receive = encryptDecrypt(message.substring(8,message.length()),decrypt);
        }else{  //distance
            String receive_message = message.substring(6,message.length());
            String msg_encrypted[] = receive_message.split("/");
            receive = encryptDecrypt(msg_encrypted[2],decrypt);
        }

        Display(src+": "+receive);
    }

    @Override
    public void onError(String message) {
        Display("Error: "+message);
    }

    @Override
    public void onConnectError(final BluetoothDevice device, String message) {
        Display("Error: "+message);
        Display("Trying again in 3 sec.");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        b.connectToDevice(device);
                    }
                }, 2000);
            }
        });
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                Intent intent1 = new Intent(Chat.this, Select.class);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        if(registered) {
                            unregisterReceiver(mReceiver);
                            registered=false;
                        }
                        startActivity(intent1);
                        finish();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        if(registered) {
                            unregisterReceiver(mReceiver);
                            registered=false;
                        }
                        startActivity(intent1);
                        finish();
                        break;
                }
            }
        }
    };

}
