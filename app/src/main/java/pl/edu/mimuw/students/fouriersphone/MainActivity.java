package pl.edu.mimuw.students.fouriersphone;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    Switch modeSwitch;
    Button button;
    Receiver r;
    Sender s;

    Handler handler;
    boolean nextIsOk = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == Constants.RECEIVED_MESSAGE_IS_FREQUENCY) {
                    TextView frequency = (TextView) findViewById(R.id.frequency);
                    String s = (String)msg.obj;
                    frequency.setText((String) msg.obj);

                    Double f = new Double((String) msg.obj);
                    if(f > 0.1) {
                        EditText editText = findViewById(R.id.editText);
                        final String translatedCharacter = Translator.ftos(f);
                        final String oldCharacters = editText.getText().toString();
                        if(     translatedCharacter != "OOV" &&
                                translatedCharacter != "START" &&
                                translatedCharacter != "STOP") {
                            if(translatedCharacter == "NEXT") {
                                nextIsOk = true;
                            } else if(nextIsOk) {
                                editText.setText(oldCharacters + translatedCharacter);
                                nextIsOk = false;
                            }
                        }
                    }
                } else {
                    super.handleMessage(msg);
                }
            }
        };

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
                        button.setText(getString(R.string.send_button_stop));
                    }
                    else {
                        button.setText(getString(R.string.send_button));
                    }
                }
                else {
                    if(r.isAlive()) {
                        button.setText(getString(R.string.receive_button_stop));
                    }
                    else {
                        button.setText(getString(R.string.receive_button));
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

    @Override
    public void onStop() {
        super.onStop();  // Always call the superclass method first
        s.stop();
        //r.stop();
    }
}
