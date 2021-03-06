package nam.tran.socketio;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private MainViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewModelProvider.Factory factory = new ViewModelProvider.NewInstanceFactory();
        model = new ViewModelProvider(this,factory).get(MainViewModel.class);
        model.onCreate();
        model.stateStatus.observe(this, state -> {
            if (state == null)
                return;
            TextView text = findViewById(R.id.state);
            if (text == null)
                return;
            switch (state){
                case READY:
                    text.setText("Connecting");
                    break;
                case INIT_ERROR:
                    text.setText("Error while create socket");
                    break;
                case CONNECT:
                    text.setText("Connected");
                    break;
                case DISCONNECTED:
                    text.setText("Disconnected");
                    break;
                case CONNECT_ERROR:
                    text.setText("Connect Error");
                    break;
            }
        });
        model.value.observe(this,value -> {
            if (value == null)
                return;
            TextView text = findViewById(R.id.value);
            if (text == null)
                return;
            text.setText(value);
        });

        model.emit.observe(this, Logger::debug);

        findViewById(R.id.emit).setOnClickListener(v -> model.emit());
    }

    @Override
    protected void onResume() {
        super.onResume();
        model.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        model.onPause();
    }
}