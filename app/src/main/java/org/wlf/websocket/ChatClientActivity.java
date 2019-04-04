package org.wlf.websocket;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;

import org.java_websocket.WebSocketImpl;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.drafts.Draft_75;
import org.java_websocket.drafts.Draft_76;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * @author wlf(Andy)
 * @datetime 2016-02-16 09:28 GMT+8
 * @email 411086563@qq.com
 */
public class ChatClientActivity extends AppCompatActivity implements OnClickListener {

    private ScrollView svChat;
    private Spinner spDraft;
    private EditText etAddress;
    private Spinner spAddress;
    private Button btnConnect;
    private Button btnClose;
    private EditText etDetails;

    private EditText etName;
    private EditText etMessage;
    private Button btnSend;

    private WebSocketClient client;// 连接客户端
    private DraftInfo selectDraft;// 连接协议

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_client);



        svChat = (ScrollView) findViewById(R.id.svChat);
        spDraft = (Spinner) findViewById(R.id.spDraft);
        etAddress = (EditText) findViewById(R.id.etAddress);
        spAddress = (Spinner) findViewById(R.id.spAddress);
        btnConnect = (Button) findViewById(R.id.btnConnect);
        btnClose = (Button) findViewById(R.id.btnClose);
        etDetails = (EditText) findViewById(R.id.etDetails);

        etName = (EditText) findViewById(R.id.etName);
        etMessage = (EditText) findViewById(R.id.etMessage);
        btnSend = (Button) findViewById(R.id.btnSend);

        DraftInfo[] draftInfos = {new DraftInfo("WebSocket协议Draft_17", new Draft_17()), new DraftInfo
                ("WebSocket协议Draft_10", new Draft_10()), new DraftInfo("WebSocket协议Draft_76", new Draft_76()), new 
                DraftInfo("WebSocket协议Draft_75", new Draft_75())};// 所有连接协议
        selectDraft = draftInfos[0];// 默认选择第一个连接协议

        ArrayAdapter<DraftInfo> draftAdapter = new ArrayAdapter<DraftInfo>(this, android.R.layout
                .simple_spinner_item, draftInfos);
        spDraft.setAdapter(draftAdapter);
        spDraft.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectDraft = (DraftInfo) spDraft.getItemAtPosition(position);// 选择连接协议

                etDetails.append("当前连接协议：" + selectDraft.draftName + "\n");

                Log.e("wlf", "选择连接协议：" + selectDraft.draftName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectDraft = null;// 清空

                Log.e("wlf", "未选择任何连接协议");
            }
        });

        ServerInfo[] serverInfos = {new ServerInfo("连接后台", "ws://47.106.225.62:8082/")};// 所有连接后台
        etAddress.setText(serverInfos[0].serverAddress);// 默认选择第一个连接协议

        ArrayAdapter<ServerInfo> serverAdapter = new ArrayAdapter<ServerInfo>(this, android.R.layout
                .simple_spinner_item, serverInfos);
        spAddress.setAdapter(serverAdapter);
        spAddress.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ServerInfo selectServerInfo = (ServerInfo) spAddress.getItemAtPosition(position);// 选择连接后台
                etAddress.setText(selectServerInfo.serverAddress);

                etDetails.append("当前连接后台：" + selectServerInfo.serverName + "\n");

                Log.e("wlf", "当前连接后台：" + selectServerInfo.serverName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectDraft = null;// 清空

                Log.e("wlf", "未选择任何连接后台");
            }
        });


        btnConnect.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        btnSend.setOnClickListener(this);

        WebSocketImpl.DEBUG = true;
        System.setProperty("java.net.preferIPv6Addresses", "false");
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnConnect:
                try {
                    if (selectDraft == null) {
                        return;
                    }
                    String address = etAddress.getText().toString().trim();
                    if (address.contains("JSR356-WebSocket")) {
                        address += etName.getText().toString().trim();
                    }
                    Log.e("wlf", "连接地址：" + address);
                    client = new WebSocketClient(new URI(address), selectDraft.draft) {
                        @Override
                        public void onOpen(final ServerHandshake serverHandshakeData) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    etDetails.append("已经连接到服务器【" + getURI() + "】\n");

                                    Log.e("wlf", "已经连接到服务器【" + getURI() + "】");

                                    spDraft.setEnabled(false);
                                    etAddress.setEnabled(false);
                                    btnConnect.setEnabled(false);
                                    etName.setEnabled(false);

                                    btnClose.setEnabled(true);
                                    btnSend.setEnabled(true);
                                }
                            });
                        }

                        @Override
                        public void onMessage(final String message) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    etDetails.append("获取到服务器信息【" + message + "】\n");

                                    Log.e("wlf", "获取到服务器信息【" + message + "】");
                                }
                            });
                        }

                        @Override
                        public void onClose(final int code, final String reason, final boolean remote) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    etDetails.append("断开服务器连接【" + getURI() + "，状态码： " + code + "，断开原因：" + reason +
                                            "】\n");

                                    Log.e("wlf", "断开服务器连接【" + getURI() + "，状态码： " + code + "，断开原因：" + reason + "】");

                                    spDraft.setEnabled(true);
                                    etAddress.setEnabled(true);
                                    btnConnect.setEnabled(true);
                                    etName.setEnabled(true);

                                    btnClose.setEnabled(false);
                                    btnSend.setEnabled(false);
                                }
                            });
                        }

                        @Override
                        public void onError(final Exception e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    etDetails.append("连接发生了异常【异常原因：" + e + "】\n");

                                    Log.e("wlf", "连接发生了异常【异常原因：" + e + "】");

                                    spDraft.setEnabled(true);
                                    etAddress.setEnabled(true);
                                    btnConnect.setEnabled(true);
                                    etName.setEnabled(true);

                                    btnClose.setEnabled(false);
                                    btnSend.setEnabled(false);
                                }
                            });
                        }
                    };
                    client.connect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btnClose:
                if (client != null) {
                    client.close();
                }
                break;
            case R.id.btnSend:
                try {
                    if (client != null) {
                        client.send(etName.getText().toString().trim() + "说：" + etMessage.getText().toString().trim());
                        svChat.post(new Runnable() {
                            @Override
                            public void run() {
                                svChat.fullScroll(View.FOCUS_DOWN);
                                etMessage.setText("");
                                etMessage.requestFocus();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (client != null) {
            client.close();
        }
    }

    private class DraftInfo {

        private final String draftName;
        private final Draft draft;

        public DraftInfo(String draftName, Draft draft) {
            this.draftName = draftName;
            this.draft = draft;
        }

        @Override
        public String toString() {
            return draftName;
        }
    }

    private class ServerInfo {

        private final String serverName;
        private final String serverAddress;

        public ServerInfo(String serverName, String serverAddress) {
            this.serverName = serverName;
            this.serverAddress = serverAddress;
        }

        @Override
        public String toString() {
            return serverName;
        }
    }
}