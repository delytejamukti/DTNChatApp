package me.aflak.bluetoothterminal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Type extends AppCompatActivity {
    Button btn_time,btn_hop,btn_distane;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type);

        btn_time = (Button)findViewById(R.id.time_button);
        btn_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go_input_time = new Intent(getApplicationContext(),InputTime.class);
                startActivity(go_input_time);
            }
        });
        btn_hop = (Button)findViewById(R.id.hop_button);
        btn_hop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go_input_hop = new Intent(getApplicationContext(),InputHop.class);
                startActivity(go_input_hop);
            }
        });
        btn_distane = (Button)findViewById(R.id.distance_btn);
        btn_distane.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go_input_distance = new Intent(getApplicationContext(),InputDistance.class);
                startActivity(go_input_distance);
            }
        });

    }
}
