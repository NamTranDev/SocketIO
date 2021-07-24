package nam.tran.socketio;

import java.net.URISyntaxException;
import java.util.Collection;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableEmitter;
import io.reactivex.rxjava3.core.CompletableOnSubscribe;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.socket.client.IO;
import io.socket.client.Socket;

public class RxSocketIo {

    private final BehaviorSubject<SocketStateEvent> state = BehaviorSubject.create();

    private synchronized void onSocketState(SocketStateEvent state) {
        this.state.onNext(state);
    }

    public synchronized Flowable<SocketStateEvent> observeState() {
        return state.toFlowable(BackpressureStrategy.BUFFER);
    }

    private final PublishSubject<SocketEvent> messages = PublishSubject.create();

    private synchronized void onExternalEvent(SocketEvent event) {
        this.messages.onNext(event);
    }

    public synchronized Flowable<SocketEvent> observeMessages() {
        return messages.toFlowable(BackpressureStrategy.BUFFER);
    }

    private Socket socket;

    private RxSocketIo() {
    }

    public void connect() {
        Logger.debug("connect()");
        socket.connect();
    }

    public void disconnect() {
        Logger.debug("disconnect()");
        socket.disconnect();
    }

    public Completable emit(String event, String message) {
        boolean connected = socket.connected();
        Logger.debug("connected : " + connected);
        if (!connected) connect();
        return Completable.create(emitter -> {
            socket.emit(event, message);
            Logger.debug("emit");
        }).subscribeOn(Schedulers.io());
    }

    public static RxSocketIo create(String serverAddress, Collection<String> externalEvents) {
        RxSocketIo res = new RxSocketIo();
        try {
            IO.Options options = new IO.Options();
            options.timeout = 50000;
            res.socket = IO.socket(serverAddress,options);

            //internal events
            for (SocketState state : SocketState.values()) {
                res.socket.on(state.label, objects -> res.onSocketState(SocketStateEvent.create(state, objects)));
            }

            //external events
            for (String e : externalEvents) {
                res.socket.on(e, objects -> res.onExternalEvent(SocketEvent.create(e, objects)));
            }

            res.onSocketState(SocketStateEvent.create(SocketState.READY, new Object[]{}));

        } catch (URISyntaxException e) {
            res.onSocketState(SocketStateEvent.create(SocketState.INIT_ERROR, new Object[]{e}));
        }

        return res;
    }
}
