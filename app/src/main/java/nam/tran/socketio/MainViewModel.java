package nam.tran.socketio;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.v4.util.ArraySet;

import java.util.Collection;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.observers.DisposableCompletableObserver;

public class MainViewModel extends ViewModel {

    private CompositeDisposable disposable;
    private RxSocketIo socket;
    private MutableLiveData<SocketState> _stateStatus = new MutableLiveData<>();
    public LiveData<SocketState> stateStatus = _stateStatus;

    private MutableLiveData<String> _value = new MutableLiveData<>();
    public LiveData<String> value = _value;

    public void onCreate() {

        disposable = new CompositeDisposable();

        Collection<String> events = new ArraySet<>();
        events.add("stock-stream");
        socket = RxSocketIo.create("http://13.213.204.104:8080/", events);

        disposable.add(socket.observeState()
                .subscribe(this::onState, Logger::debug));

        disposable.add(socket.observeMessages()
                .subscribe(this::onIncomingMessage, Logger::debug));
    }

    public void onResume() {
        socket.connect();
    }

    public void onPause() {
        socket.disconnect();
    }

    @Override
    protected void onCleared() {
        disposable.clear();
        super.onCleared();
    }

    private void onState(SocketStateEvent event) {
        _stateStatus.postValue(event.state());
    }

    private void onIncomingMessage(SocketEvent event) {
//        Logger.debug(event);
        String eventName = event.name();
        switch (eventName) {
            case "stock-stream":
                Logger.debug(event.data());
                _value.postValue(event.data().toString());
                break;
        }
    }

    public void emit() {
        disposable.add(socket.emit("tes-emit", "Hello world").subscribeWith(new DisposableCompletableObserver() {
            @Override
            public void onComplete() {
                Logger.debug("onComplete()");
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Logger.debug(e);
            }
        }));
    }
}
