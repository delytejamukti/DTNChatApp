package me.aflak.bluetoothterminal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class InputTime extends AppCompatActivity {
    String type_id = "1";
    Button next_button;
    EditText getLifeTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_time);
        getLifeTime = (EditText)findViewById(R.id.input_lifetime_editText);

        next_button = (Button)findViewById(R.id.input_lifetime_next_button);
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go_select = new Intent(getApplicationContext(),Select.class);
                go_select.putExtra("type_id",type_id);
                go_select.putExtra("lifetime",getLifeTime.getText().toString());
                startActivity(go_select);
                finish();
            }
        });
    }
}
