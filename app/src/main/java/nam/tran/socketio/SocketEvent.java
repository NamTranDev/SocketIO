package nam.tran.socketio;

import java.util.Arrays;
import java.util.Collection;

public class SocketEvent {

    private String name;

    public String name() {
        return name;
    }

    private Collection<Object> data;

    public Collection<Object> data() {
        return data;
    }

    protected SocketEvent(String name,  Collection<Object> data ) {
        this.name = name;
        this.data = data;
    }

    public static SocketEvent create(String name, Object[] objects) {
        return new SocketEvent(name, Arrays.asList(objects));
    }

    @Override
    public String toString() {
        return "SocketEvent{" +
                "name='" + name + '\'' +
                ", data=" + data +
                '}';
    }
}
