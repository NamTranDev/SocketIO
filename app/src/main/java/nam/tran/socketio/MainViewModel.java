package nam.tran.socketio;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;
import android.support.v4.util.ArraySet;

import java.util.Collection;

import io.reactivex.rxjava3.core.Flowable;

public class MainViewModel extends ViewModel {

    private RxSocketIo socket;
    private final MediatorLiveData<SocketState> _stateStatus = new MediatorLiveData<>();
    public LiveData<SocketState> stateStatus = _stateStatus;
    private final MediatorLiveData<String> _value = new MediatorLiveData<>();
    public LiveData<String> value = _value;
    private final MediatorLiveData<Boolean> _emit = new MediatorLiveData<>();
    public LiveData<Boolean> emit = _emit;

    public void onCreate() {
        Collection<String> events = new ArraySet<>();
        events.add("stock-stream");
        socket = RxSocketIo.create("http://13.213.204.104:8080/", events);

        _stateStatus.addSource(LiveDataReactiveStreams
                        .fromPublisher(socket.observeState().map(SocketStateEvent::state))
                , state -> _stateStatus.setValue(state));

        _value.addSource(LiveDataReactiveStreams
                        .fromPublisher(socket.observeMessages().map(event -> event.data().toString()))
                , data -> _value.setValue(data));
    }

    public void onResume() {
        socket.connect();
    }

    public void onPause() {
        socket.disconnect();
    }

    public void emit() {
        _emit.addSource(LiveDataReactiveStreams.fromPublisher(socket.emit("tes-emit", "Hello world")
                .toFlowable()), (Observer<Boolean>) aBoolean -> _emit.setValue(true));
    }
}
