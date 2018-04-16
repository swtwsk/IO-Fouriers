package pl.edu.mimuw.students.fouriersphone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void clickDoButton(View view) {
        Switch mySwitch = findViewById(R.id.switch1);
        EditText editText = findViewById(R.id.editText);

        if (mySwitch.isChecked()) {
            String s = editText.getText().toString();
            Sender.run(s);
        }
        else {
            String s = Receiver.run();
            editText.setText(s);
        }
    }
}
