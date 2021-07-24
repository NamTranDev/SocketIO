package nam.tran.socketio;

import java.util.Arrays;
import java.util.Collection;

public class SocketStateEvent extends SocketEvent {

    private SocketState state;
    public SocketState state(){
        return state;
    }

    protected SocketStateEvent(String name, Collection<Object> data, SocketState state) {
        super(name, data);
        this.state = state;
    }

    public static SocketStateEvent create(SocketState socketState, Object[] objects) {
        return new SocketStateEvent(socketState.label, Arrays.asList(objects), socketState);
    }
}
