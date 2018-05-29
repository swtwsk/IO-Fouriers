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
import android.support.v4.content.ContextCompat;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    AppState state;
    Button button_act;
    Button button_send_file;
    Button button_receive_file;
    Button button_set_mode_text;
    Button button_set_mode_file;
    Button button_set_mode_send;
    Button button_set_mode_hear;
    EditText editText;

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
                    TextView frequency = findViewById(R.id.frequency);
                    String s = (String)msg.obj;
                    frequency.setText((String) msg.obj);

                    Double f = new Double((String) msg.obj);
                    if(f > 0.1) {
                        EditText editText = findViewById(R.id.editText);
                        final String translatedCharacter = Translator.ftos(f);
                        if(     translatedCharacter != "OOV" &&
                                translatedCharacter != "START" &&
                                translatedCharacter != "STOP") {
                            if(translatedCharacter == "NEXT") {
                                nextIsOk = true;
                            } else if(nextIsOk) {
                                editText.append(translatedCharacter);
                                nextIsOk = false;
                            }
                        }
                    }
                } else {
                    super.handleMessage(msg);
                }
            }
        };

        button_act = findViewById(R.id.main_act);
        button_send_file = findViewById(R.id.send_file);
        button_receive_file = findViewById(R.id.receive_file);
        button_set_mode_text = findViewById(R.id.select_text);
        button_set_mode_file = findViewById(R.id.select_file);
        button_set_mode_send = findViewById(R.id.select_send);
        button_set_mode_hear = findViewById(R.id.select_hear);
        editText = findViewById(R.id.editText);

        state = new AppState();
        r = new Receiver(this, handler, state);
        s = new Sender(this, state);

        if (state.mode_send) {
            button_act.setText(getString(R.string.send_button));
        }
        else {
            button_act.setText(getString(R.string.receive_button));
        }

        button_set_mode_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state.mode_send = true;
                refresh_button_act();
                refresh_body();
            }
        });

        button_set_mode_hear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state.mode_send = false;
                refresh_button_act();
                refresh_body();
            }
        });

        button_set_mode_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state.mode_text = true;
                refresh_button_act();
                refresh_body();
            }
        });

        button_set_mode_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state.mode_text = false;
                refresh_button_act();
                refresh_body();
            }
        });
    }

    public void refresh_button_act() {
        if (state.mode_send) {
            if(s.isAlive()) {
                button_act.setText(getString(R.string.send_button_stop));
            }
            else {
                button_act.setText(getString(R.string.send_button));
            }
        }
        else {
            if(r.isAlive()) {
                button_act.setText(getString(R.string.receive_button_stop));
            }
            else {
                button_act.setText(getString(R.string.receive_button));
            }
        }
    }

    public void refresh_body() {
        button_receive_file.setVisibility(View.INVISIBLE);
        button_send_file.setVisibility(View.INVISIBLE);
        editText.setVisibility(View.INVISIBLE);
        if (state.mode_text) {
            editText.setVisibility(View.VISIBLE);
        }
        else {
            if(state.mode_send) {
                button_send_file.setVisibility(View.VISIBLE);
            }
            else {
                button_receive_file.setVisibility(View.VISIBLE);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void clickDoButton(View view) {
        EditText editText = findViewById(R.id.editText);
        if (state.mode_send) {
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
        r.stop();
    }
}
