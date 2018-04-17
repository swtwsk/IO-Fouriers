package pl.edu.mimuw.students.fouriersphone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {

    Switch modeSwitch;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        modeSwitch = findViewById(R.id.modeSwitch);

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
                    button.setText(getString(R.string.send_button));
                }
                else {
                    button.setText(getString(R.string.receive_button));
                }
            }
        });
    }

    public void clickDoButton(View view) {
        EditText editText = findViewById(R.id.editText);

        if (modeSwitch.isChecked()) {
            String s = editText.getText().toString();
            Sender.run(s);
        }
        else {
            String s = Receiver.run();
            editText.setText(s);
        }
    }
}
