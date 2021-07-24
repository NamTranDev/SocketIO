package nam.tran.socketio;

public interface IViewListener {
    void onStateView(SocketState state);
    void onValue(String data);
}
