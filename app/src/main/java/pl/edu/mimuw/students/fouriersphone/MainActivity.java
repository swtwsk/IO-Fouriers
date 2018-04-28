package pl.edu.mimuw.students.fouriersphone;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.support.v4.content.ContextCompat;


public class MainActivity extends AppCompatActivity {

    Switch modeSwitch;
    Button button;
    Receiver r;
    Sender s;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        modeSwitch = findViewById(R.id.modeSwitch);

        r = new Receiver(this);
        s = new Sender(this);

        button = findViewById(R.id.button);
        if (modeSwitch.isChecked()) {
            button.setText(getString(R.string.send_button));
        }
        else {
            button.setText(getString(R.string.receive_button));
        }

        modeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modeSwitch.isChecked()) {
                    if(s.isAlive()) {
                        button.setText("StopS");
                    }
                    else {
                        button.setText("Send");
                    }
                }
                else {
                    if(r.isAlive()) {
                        button.setText("StopR");
                    }
                    else {
                        button.setText("Receive");
                    }
                }
            }
        });
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    public void clickDoButton(View view) {
        EditText editText = findViewById(R.id.editText);
        if (modeSwitch.isChecked()) {
            String message = editText.getText().toString();
            s.start(message);
        }
        else {

            String premissions[] = {Manifest.permission.RECORD_AUDIO};
            requestPermissions(premissions, 1);

            String permission = android.Manifest.permission.RECORD_AUDIO;
            int res = MainActivity.this.checkCallingOrSelfPermission(permission);
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED) {
                r.start();
            }
            else {
                editText.setText("error - premission.RECORD_AUDIO not granted");
            }


        }
    }
}
