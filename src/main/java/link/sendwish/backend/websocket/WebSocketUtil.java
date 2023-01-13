package link.sendwish.backend.websocket;
import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

public class WebSocketUtil extends WebSocketClient{

    private JSONObject obj;


    public WebSocketUtil(URI serverUri, Draft protocolDraft) {
        super(serverUri, protocolDraft);
    }

    // 통신후 OnMessage에 데이터가 들어오기에 해당 부분을 원하는대로 데이터를 파싱하는 부분
    @Override
    public void onMessage( String message ) {
        obj = new JSONObject(message);
    }

    @Override
    public void onOpen( ServerHandshake handshake ) {
        System.out.println( "opened connection" );
    }

    @Override
    public void onClose( int code, String reason, boolean remote ) {
        System.out.println( "closed connection" );
    }

    @Override
    public void onError( Exception ex ) {
        ex.printStackTrace();
    }

    public static JSONObject getResult() throws Exception {
        URI uri = URI.create("ws://localhost:8080/ws/chat");
        WebSocketUtil webSocketUtil = new WebSocketUtil(uri, new Draft_6455());

        //웹소켓 커넥팅
        webSocketUtil.connectBlocking();

        JSONObject obj = new JSONObject();

        //메세지 던질거 제이슨 형식으로 세팅
        obj.put("message", "Hello World");
        String message = obj.toString();

        //웹소켓 메세지 보내기
        webSocketUtil.send(message);

        JSONObject result = webSocketUtil.getResult();
        webSocketUtil.close();

        return result;
    }

}