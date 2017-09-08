/*
========================================================
This Source Code Form is subject to the terms
of the Mozilla Public License, v. 2.0.
If a copy of the MPL was not distributed with this file,
You can obtain one at https://mozilla.org/MPL/2.0/.
========================================================
*/
/*
 * BookingView.java
 *
 * Created on August 14, 2017, 4:04
 * @author pymancer <pymancer@gmail.com>
 * @since 2017.0.1
 */

package ru.instefa.cafepickpos.services;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.time.LocalDateTime;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.XmppException;
import rocks.xmpp.core.session.XmppClient;
import rocks.xmpp.core.stanza.MessageEvent;
import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.core.session.ConnectionEvent;
import rocks.xmpp.core.session.SessionStatusEvent;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Request.Builder;
import okhttp3.Response;
import okhttp3.ResponseBody;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import ru.instefa.cafepickpos.PosLog;
import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.util.POSUtil;
import ru.instefa.cafepickpos.util.P3Decryptor;
import ru.instefa.cafepickpos.config.TerminalConfig;
import ru.instefa.cafepickpos.exceptions.CryptorException;
import ru.instefa.cafepickpos.ui.views.BookingView;

public class BookingService {
	/**
	 * cafepick.ru API interaction. Singleton.
	 */
    // Singleton instance variable
    private static BookingService bookingService;
    
    // set to `true` only in development to bypass self-signed certificates check
    // TODO: implement non-committable development mode to eliminate
    // the possibility of the unsafe connection in production
    private final boolean DEVMODE = false;
    
    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient CLIENT = getOkHttpClient();
    private final BlockingQueue<String> QUEUE = new ArrayBlockingQueue<String>(1024);
    private final BookingClient XMPPCLIENT = new BookingClient(QUEUE);
    private final HashMap<String, String> STATUSES = getStatuses();
    private ExecutorService executor = null;
    private ScheduledExecutorService scheduledExecutor = null;
    private boolean connected = false;
    protected String[] connectKey = null;
    private String apiUsername = null;
    private String apiPassword = null;
    private String apiBaseURL =  null;
    private String apiToken = null;

    private BookingService(){
        // private constructor
    }
    
    /**
     * Static method to get instance.
     * @return 
     */
    public static BookingService getInstance(){
        if(bookingService == null){
            bookingService = new BookingService();
        }
        return bookingService;
    }
    
    /**
     * New bookings receiver.
     */
    class BookingClient implements Runnable {
        protected BlockingQueue<String> queue = null;

        public BookingClient(BlockingQueue<String> queue) {
            this.queue = queue;
        }

        @Override
        public void run(){
            try {
                try (XmppClient xmppClient = XmppClient.create(connectKey[3])) {
                    // Listen for session state change
                    xmppClient.addSessionStatusListener(new Consumer<SessionStatusEvent>() {
                        @Override
                        public void accept(SessionStatusEvent evt) {
                            try {
                                switch (evt.getStatus()) {
                                    case AUTHENTICATED:
                                        // session has authenticated
                                        // since messages should not have spaces
                                        // sending exact status, and not evt.toString()
                                        // which contains an old status too
                                        queue.put("AUTHENTICATED");
                                        break;
                                    case DISCONNECTED:
                                        // temporarily disconnected by an exception
                                        queue.put("DISCONNECTED");
                                        String msg = evt.toString() + evt.getThrowable().getMessage();
                                        PosLog.error(BookingClient.class, msg);
                                        break;
									default:
										break;
                                }
                            } catch (InterruptedException ex) {
                                PosLog.error(BookingClient.class, ex.getMessage());
                            }
                        }
                    });
                    // Listen for connection state change
                    xmppClient.addConnectionListener(new Consumer<ConnectionEvent>() {
                        @Override
                        public void accept(ConnectionEvent evt) {
                            String msg;
                            try {
                                switch (evt.getType()) {
                                    case DISCONNECTED:
                                        // disconnected due to evt.getCause()
                                        queue.put("DISCONNECTED");
                                        msg = evt.toString() + ": " + evt.getCause().getMessage();
                                        PosLog.error(BookingClient.class, msg);
                                        break;
                                    case RECONNECTION_SUCCEEDED:
                                        // successfully reconnected
                                        queue.put("RECONNECTION_SUCCEEDED");
                                        break;
                                    case RECONNECTION_FAILED:
                                        // reconnection failed due to evt.getCause()
                                        queue.put("RECONNECTION_FAILED");
                                        msg = evt.toString() + ": " + evt.getCause().getMessage();
                                        PosLog.error(BookingClient.class, msg);
                                        break;
									default:
										break;
                                }
                            } catch (InterruptedException ex) {
                                PosLog.error(BookingClient.class, ex.getMessage());
                            }
                        }
                    });
                    // Listen for messages
                    xmppClient.addInboundMessageListener(new Consumer<MessageEvent>() {
                        @Override
                        public void accept(MessageEvent evt) {
                            Message message = evt.getMessage();
                            Message.Type type = message.getType();
                            if (message.isNormal() || type == Message.Type.CHAT) {
                                if (message.getFrom().asBareJid().equals(Jid.of(connectKey[7]))) {
                                    String body = message.getBody();
                                    if (body != null) {
                                        try {
                                            queue.put(body);
                                        } catch (InterruptedException ex) {
                                            PosLog.error(BookingClient.class, ex.getMessage());
                                        }
                                    } else {
                                    	PosLog.error(BookingClient.class, "message is null");
                                    }
                                } else {
                                    PosLog.error(BookingClient.class, "unknown message sender");
                                }
                            } else if (type == Message.Type.ERROR) {
                                PosLog.error(BookingClient.class, "erroneous type message");
                            }
                        }
                    });
                    // connect to server
                    xmppClient.connect();
                    xmppClient.login(connectKey[4], connectKey[5], connectKey[6]);

                    // running XMPP client until shutdown command
                    try {
                        while (true) {Thread.sleep(100);}
                    } catch (InterruptedException e) {
                        PosLog.info(BookingClient.class, "booking client shutdown");
                        xmppClient.close();
                    }
                }
            } catch (XmppException e) {
            	try {
					queue.put("CONNECTION_FAILED");
				} catch (InterruptedException ex) {
					PosLog.error(BookingClient.class, ex.getMessage());
				}
                PosLog.error(BookingClient.class, e.getMessage());
            }
        }
    }
    
    private OkHttpClient getOkHttpClient() {
        OkHttpClient client;

        if (DEVMODE) {
            /*
            * Accepts any SSL certificates. Development only!
            */
            X509TrustManager insecureTrustManager = new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    X509Certificate[] cAIarray = new X509Certificate[0];
                    return cAIarray;
                }

                @Override
                public void checkServerTrusted(final X509Certificate[] chain,
                                               final String authType) throws CertificateException {
                }

                @Override
                public void checkClientTrusted(final X509Certificate[] chain,
                                               final String authType) throws CertificateException {
                }
            };
            
            HostnameVerifier insecureHostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            
            final TrustManager[] containsInsecureTrustManager = new TrustManager[]{insecureTrustManager};
            
            try {
                SSLContext sslContext = SSLContext.getInstance("TLS");//NOI18N
                sslContext.init(new KeyManager[0], containsInsecureTrustManager, new SecureRandom());
                SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
                
                client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, insecureTrustManager)
                    .hostnameVerifier(insecureHostnameVerifier)
                    .build();
                
                PosLog.info(BookingClient.class, "Insecure Trust Manager in use!");
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                client = new OkHttpClient();
                PosLog.error(BookingClient.class, e.getMessage());
            }
        } else {
            client = new OkHttpClient();
        }
        
        return client;
    }
    
    /**
     * Booking service connect.
     * @return result, if < 0 connect was unsuccessful
     */
    public int connect() {
        if (!isConnected()) {
            PosLog.info(BookingService.class, "connecting to service");
            // update connection key in case settings are changed
            connectKey = getConnectKey();
            if (connectKey.length == 8) {
                apiUsername = connectKey[0];
                apiPassword = connectKey[1];
                apiBaseURL = connectKey[2];
                
                executor = Executors.newSingleThreadExecutor();
                executor.execute(XMPPCLIENT);
                // queue checker
                scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
                scheduledExecutor.scheduleAtFixedRate(checkQueue, 1, 1, TimeUnit.SECONDS);
                return 0;
            }
            else {
                setStatus(Messages.getString("BookingService.0"));
                return -1;
            }
        }
        else if (executor == null) {
            setStatus(Messages.getString("BookingService.19"));
            PosLog.error(BookingService.class, "bad connected state");
            return -1;
        }
        else {
        	// already connected
            return 1;
        }
    }
    
    /**
     * Booking service disconnect.
     * @return result, if < 0 disconnect was unsuccessful
     */
    public int disconnect() {
        if (isConnected()) {
            try {
                executor.shutdownNow();
                executor.awaitTermination(100, TimeUnit.MILLISECONDS);
                executor = null;
                setConnected(false);

                scheduledExecutor.shutdown();
                scheduledExecutor.awaitTermination(100, TimeUnit.MILLISECONDS);
                setStatus(Messages.getString("BookingService.1"));
                return 0;
            } catch (InterruptedException ex) {
                if (isConnected()) {
                    setStatus(Messages.getString("BookingService.15"));
                    return -1;
                } else {
                    setStatus(Messages.getString("BookingService.16"));
                    return 1;
                }
            }
        }
        else if (executor != null) {
            setStatus(Messages.getString("BookingService.17"));
            return -1;
        }
        else {
            // already disconnected
            return 1;
        }
    }
    
    public boolean isConnected() {
        return connected;
    }
    
    public void setConnected(boolean state) {
        connected = state;
        // UI update
        BookingView.getInstance().renderControls();
        // booking service notification
        setCafeOnline(state);
    }
    
    /**
     * Extracts connection data from app settings,
     * i.e. Booking service and dns service dependent XMPP connection parameters
     * @return connection parameters,
     * something like ["apilogin", "apipass", "https://api.root.url/",
     * "some.domain", "username", "pass", "room", "hostuser"]
     */
    private String[] getConnectKey() {
        String[] decrypted = new String[] {};
        try {
            String key = P3Decryptor.p3Decrypt(TerminalConfig.getBookingConnection(), TerminalConfig.getBookingKey());
            String[] raw = key.split("::");
            String[] decomposed = raw[3].split("/");
            String[] fullLogin = decomposed[0].split("@");
            String login = fullLogin[0];
            String domain = fullLogin[1];
            String room = decomposed[1];
            decrypted = new String[] {raw[0], raw[1], raw[2], domain, login, raw[4], room, raw[7]};
        } catch (CryptorException e) {
            PosLog.error(P3Decryptor.class, e.getMessage());
        } catch (UnsupportedEncodingException | ArrayIndexOutOfBoundsException e) {
            PosLog.error(BookingService.class, e.getMessage());
        }
        
        return decrypted;
    }
    
    /**
     * 
     */
    private final Runnable checkQueue = new Runnable() {
        @Override
        public void run() {
            while (QUEUE.size() > 0) {
                routeMessage((String) QUEUE.poll());
            }
        }
    };
    
    private HashMap<String, String> getStatuses() {
        HashMap<String, String> statuses = new HashMap<String, String>();
        statuses.put("AUTHENTICATED", Messages.getString("BookingService.11"));
        statuses.put("DISCONNECTED", Messages.getString("BookingService.12"));
        statuses.put("RECONNECTION_SUCCEEDED", Messages.getString("BookingService.13"));
        statuses.put("RECONNECTION_FAILED", Messages.getString("BookingService.14"));
        statuses.put("CONNECTION_FAILED", Messages.getString("BookingService.20"));
        
        return statuses;
    }
    
    /**
     * Shortcut.
     * @param msg 
     */
    private void setStatus(String msg) {
        BookingView.getInstance().setStatus(msg);
    }
    
    /**
     * @param msg incoming string message
     * Numbers to book, strings - to use as a status key.
     * If number > 0 - create booking.
     * If number < 0 - cancel booking.
     */
    private void routeMessage(String msg) {
        try {
            int uid = Integer.parseInt(msg);
            // msg is a booking uid
            if (uid > 0) {
                setStatus(Messages.getString("BookingService.2") + uid);
                BookingView.getInstance().initiateBooking(String.valueOf(uid));
            } else {
                uid = uid * -1;
                setStatus(Messages.getString("BookingService.3") + uid);
                BookingView.getInstance().cancelBooking(String.valueOf(uid));
            }
        } catch (NumberFormatException e) {
            // msg is a status key
            // track connection state
            if ("AUTHENTICATED".equals(msg)) {
                setConnected(true);
            } else if ("DISCONNECTED".equals(msg)) {
                setConnected(false);
            } else if ("CONNECTION_FAILED".equals(msg)) {
                setConnected(false);
            }
            // inform user
            String status = (String) STATUSES.get(msg);
            if (status != null) {
                setStatus(status);
            }
        }
    }
    
    private Map<String, Object> post(String url, String data, Map<String, String> headers) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("body", "");
        
        RequestBody body = RequestBody.create(JSON, data);
        Builder requestBuilder = new Request.Builder().url(url).post(body);

        if (headers != null) {
            for (Map.Entry<String, String> item : headers.entrySet()) {
                requestBuilder.addHeader(item.getKey(), item.getValue());
            }
        }

        Request request = requestBuilder.build();

        try (Response r = CLIENT.newCall(request).execute()) {
            if (r != null) {
                result.put("code", r.code());
                // extracting responseBody here since it seems response
                // gets corrupted while passing to another methods
                ResponseBody payload = r.body();
                if (payload != null) {
                    result.put("body", payload.string());
                } else {
                    PosLog.info(BookingService.class, "empty body to POST: " + url);
                }
            } else {
                PosLog.info(BookingService.class, "empty response to POST: " + url);
            }
        } catch (IOException e) {
            PosLog.error(BookingService.class, e.getMessage());
        }
        return result;
    }
    
    private String getToken() {
        String token = null;
        String url = apiBaseURL + "api-token-auth/";
        Map<String, String> data = new HashMap<>();
        data.put("username", apiUsername);
        data.put("password", apiPassword);
        Map<String, Object> r = post(url, new Gson().toJson(data), null);
        
        int code = (int) r.get("code");
        String body = (String) r.get("body");
        if (code == 200) {
            apiToken = token = POSUtil.toJsonObject(body).get("token").getAsString();
        } else {
            handleHttpError(code, body);
        }
        
        return token;
    }
    
    private String refreshToken() {
        String result = apiToken; // just a useful convention, can return anything but null
        if (apiToken != null) {
            String url = apiBaseURL + "api-token-refresh/";
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "JWT " + apiToken);
            Map<String, String> data = new HashMap<>();
            data.put("token", apiToken);
            Map<String, Object> r = post(url, new Gson().toJson(data), headers);
            
            int code = (int) r.get("code");
            if (code != 200) {
                handleHttpError(code, (String) r.get("body"));
                result = getToken();
            }
        } else {
            result = getToken();
        }
        return result;
    }
    
    /**
     * send a direct new booking order
     * @param bookingStart  book from datetime
     * @param bookingEnd    book till datetime
     * @param tableIds       coolection of table ids to book
     * example of request data:
     * {"timemark":"2017-08-17T01:00:00+03:00","expiration":"2017-08-17T02:00:00+03:00", "tables": ["1", "2"]}
     * @return new booking parameters as json string
     */
    public String pushBooking(LocalDateTime bookingStart, LocalDateTime bookingEnd, Collection<Integer> tableIds) {
        String result = null;
        setStatus(Messages.getString("BookingService.4") + tableIds.toString());
        if (refreshToken() != null && isConnected()) {
            String url = apiBaseURL + "api/v1/service/bookings";
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "JWT " + apiToken);
            
            List<String> tables = new ArrayList<>(tableIds.size());
            for (Integer i:tableIds) { 
                tables.add(String.valueOf(i)); 
            }
            Map<String, Object> data = new HashMap<>();
            data.put("tables", tables);
            data.put("timemark", POSUtil.toISODateTime(bookingStart));
            data.put("expiration", POSUtil.toISODateTime(bookingEnd));
            
            Map<String, Object> r = post(url, new Gson().toJson(data), headers);
            int code = (int) r.get("code");
            String body = (String) r.get("body");
            if (code == 201) {
                return body;
            } else {
                handleHttpError(code, body);
            }
        } else {
            PosLog.info(BookingService.class, "push, no booking service token, check connection");
        }
        return result;
    }
    
    /**
     * Advance booking to next step.
     * @param bookingId
     * @param newStatus
     * @return booking service response
     */
    public String processBooking(String bookingId, String newStatus) {
        JsonObject json;
        String data = null;
        String url = apiBaseURL + "api/v1/service/bookings/" + bookingId;
        
        if (refreshToken() != null && isConnected()) {
            // checking booking existence
            Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "JWT " + apiToken)
                .build();
            
            try (Response getResponse = CLIENT.newCall(request).execute()) {
                if (getResponse != null) {
                    ResponseBody responseBody = getResponse.body();
                    if (responseBody != null) {
                        if (getResponse.code() == 200) {
                            json = POSUtil.toJsonObject(responseBody.string());
                            json.remove("state");
                            json.add("state", POSUtil.toJsonElement(newStatus));

                            // advance booking to next step
                            RequestBody requestBody = RequestBody.create(JSON, json.toString());
                            request = new Request.Builder()
                                .url(url)
                                .addHeader("Authorization", "JWT " + apiToken)
                                .put(requestBody)
                                .build();

                            try (Response putResponse = CLIENT.newCall(request).execute()) {
                                if (putResponse != null) {
                                    responseBody = putResponse.body();
                                    if (responseBody != null) {
                                        if (putResponse.code() == 200) {
                                            data = responseBody.string();
                                        } else {
                                            handleHttpError(putResponse.code(), responseBody.string());
                                        }
                                    } else {
                                        PosLog.info(BookingService.class, "empty body to PUT: " + url);
                                    }
                                } else {
                                    PosLog.info(BookingService.class, "bad response to PUT: " + url);
                                }
                            } catch (IOException e) {
                                PosLog.error(BookingService.class, e.getMessage());
                            }
                        } else {
                            handleHttpError(getResponse.code(), responseBody.string());
                        }
                    } else {
                        PosLog.info(BookingService.class, "empty body to GET: " + url);
                    }
                } else {
                    PosLog.info(BookingService.class, "bad response to GET: " + url);
                }
            } catch (IOException e) {
                PosLog.error(BookingService.class, e.getMessage());
            }
        } else {
            PosLog.info(BookingService.class, "token or connection error");
        }
        return data;
    }

    private void setCafeOnline(boolean state) {
        if (refreshToken() != null) {
            String slug = state == true ? "api/v1/service/online" : "api/v1/service/offline";
            String url = apiBaseURL + slug;
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "JWT " + apiToken);
            
            Map<String, Object> r = post(url, new Gson().toJson(""), headers);
            int code = (int) r.get("code");
            if (code != 200) {
                handleHttpError(code, (String) r.get("body"));
            }
        } else {
            setStatus(Messages.getString("BookingService.21"));
        }
    }
    
    private void handleHttpError(int code, String body) {
        switch (code) {
            case 200:
                setStatus(Messages.getString("BookingService.5") + body);
                break;
            case 400:
                setStatus(Messages.getString("BookingService.6") + body);
                break;
            case 401:
                setStatus(Messages.getString("BookingService.7"));
                break;
            case 403:
                setStatus(Messages.getString("BookingService.8"));
                break;
            case 404:
                setStatus(Messages.getString("BookingService.9"));
                break;
            default:
                setStatus(Messages.getString("BookingService.10") + code + " " + body);
                break;
        }
    }
}
