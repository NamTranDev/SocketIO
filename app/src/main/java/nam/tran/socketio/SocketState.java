package nam.tran.socketio;

import io.socket.client.Socket;

public enum SocketState {

    READY("ready"),
    INIT_ERROR("initError"),
    CONNECT(Socket.EVENT_CONNECT),
    DISCONNECTED(Socket.EVENT_DISCONNECT),
    CONNECT_ERROR(Socket.EVENT_CONNECT_ERROR);

    public final String label;

    SocketState(String label) {
        this.label = label;
    }
}



