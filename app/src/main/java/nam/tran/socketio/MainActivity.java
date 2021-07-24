package nam.tran.socketio;

import android.arch.lifecycle.ViewModelProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements IViewListener {

    private MainViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewModelProvider.Factory factory = new ViewModelProvider.NewInstanceFactory();
        model = new ViewModelProvider(this,factory).get(MainViewModel.class);
        model.setViewListener(this);
        model.onCreate();

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

    @Override
    public void onStateView(SocketState state) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
            }
        });
    }

    @Override
    public void onValue(String data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView text = findViewById(R.id.value);
                if (text == null)
                    return;
                text.setText(data);
            }
        });
    }
}