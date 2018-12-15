package me.aflak.bluetoothterminal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class InputHop extends AppCompatActivity {
    String type_id = "2";
    Button next_button;
    EditText getHop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_hop);

        getHop = (EditText)findViewById(R.id.input_hop_editText);
        next_button = (Button)findViewById(R.id.input_hop_next_button);
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go_select = new Intent(getApplicationContext(),Select.class);
                go_select.putExtra("type_id",type_id);
                go_select.putExtra("number_hop",getHop.getText().toString());
                startActivity(go_select);
                finish();
            }
        });
    }
}
