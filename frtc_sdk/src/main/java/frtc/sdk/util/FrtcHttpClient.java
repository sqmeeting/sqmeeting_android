package frtc.sdk.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import frtc.sdk.internal.model.CommonResponse;
import frtc.sdk.internal.model.ResultType;
import frtc.sdk.log.Log;
import okhttp3frtc.Call;
import okhttp3frtc.Callback;
import okhttp3frtc.HttpUrl;
import okhttp3frtc.MediaType;
import okhttp3frtc.OkHttpClient;
import okhttp3frtc.Request;
import okhttp3frtc.RequestBody;
import okhttp3frtc.Response;

public class FrtcHttpClient {
    private final String TAG = getClass().getSimpleName();
    private Context context;

    private static FrtcHttpClient httpClient;
    private OkHttpClient client = null;
    private static final MediaType CONTENT_TYPE = MediaType.get("application/json; charset=utf-8");
    private static int READ_TIME_OUT = 10;

    public static final String HTTP_SCHEMA = "https://";
    public static final String BASE_URI_API_V1 = "/api/v1";

    public static final String HEADER_KEY_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_KEY_USER_AGENT = "User-Agent";
    public static final String HEADER_KEY_HOST = "Host";

    public static final String HEADER_CONTENT_TYPE = "application/json";
    public static String USER_AGENT = "FrtcMeeting/3.4.0 android";
    public static final String USER_AGENT_PREFIX = "FrtcMeeting/";

    private FrtcHttpClient(Context context) {
        client = new OkHttpClient.Builder()
                .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
                .sslSocketFactory(createSSLSocketFactory(), getX509TrustManager())
                .hostnameVerifier(new TrustAllHostnameVerifier())
                .build();

        this.context = context;
        updateUserAgent();
    }

    public static FrtcHttpClient getInstance(Context context) {
        if (httpClient == null) {
            synchronized (FrtcHttpClient.class) {
                if (httpClient == null) {
                    httpClient = new FrtcHttpClient(context);
                }
            }
        }
        return httpClient;
    }

    private void updateUserAgent(){

        String appVersion = getApplicationVersion();

        if(isHarmonyOs()){
            USER_AGENT = USER_AGENT_PREFIX + appVersion + " Harmony " + getHarmonyVersion();
        }else{
            String brand = android.os.Build.BRAND;
            String sdkVersion = android.os.Build.VERSION.RELEASE;
            USER_AGENT = USER_AGENT_PREFIX + appVersion + " " + brand + " Android " + sdkVersion;
        }
    }

    private String getApplicationVersion(){
        String version = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
             String versionName = packageInfo.versionName;
            if (TextUtils.isEmpty(versionName)) {
                version = "1.0.0";
            }else{
                version = versionName.substring(0,versionName.lastIndexOf("."));
            }

        } catch (Exception e) {
            Log.e(TAG,"getApplicationVersion"+e.toString());
        }
        return version;
    }

    private static boolean isHarmonyOs() {
        try {
            Class<?> buildExClass = Class.forName("com.huawei.system.BuildEx");
            Object osBrand = buildExClass.getMethod("getOsBrand").invoke(buildExClass);
            return "Harmony".equalsIgnoreCase(osBrand.toString());
        } catch (Throwable x) {
            return false;
        }
    }

    private static String getHarmonyVersion() {
        return getProp("hw_sc.build.platform.version", "");
    }

    private static String getProp(String property, String defaultValue) {
        try {
            Class spClz = Class.forName("android.os.SystemProperties");
            Method method = spClz.getDeclaredMethod("get", String.class);
            String value = (String) method.invoke(spClz, property);
            if (TextUtils.isEmpty(value)) {
                return defaultValue;
            }
            return value;
        } catch (Throwable e) {
            Log.e("FrtcHttpClient","getProp:"+e.toString());
        }
        return defaultValue;
    }

    private static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            if(hostname != null && !hostname.isEmpty())
              return true;
            return false;
        }
    }

    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }

        return ssfFactory;
    }

    private static class TrustAllCerts implements X509TrustManager {

        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private static X509TrustManager getX509TrustManager() {
        X509TrustManager trustManager = null;
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
            }
            trustManager = (X509TrustManager) trustManagers[0];
        } catch (Exception e) {
            Log.e("FrtcHttpClient","getX509TrustManager failed "+e.toString());
        }

        return trustManager;
    }


    public static class RequestWrapper {
        private String url;
        private String host;
        private String postData;

        public RequestWrapper(String url, String postData) {
            this.url = url;
            this.postData = postData;
        }

        public RequestWrapper(String url, String postData, String host) {
            this.url = url;
            this.host = host;
            this.postData = postData;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getPostData() {
            return postData;
        }

        public void setPostData(String postData) {
            this.postData = postData;
        }
    }

    public interface IResultCallback {
        void onResult(ResultType resultType, String result);
    }

    public String asyncGet(RequestWrapper requestWrapper, final IResultCallback callback) throws IOException {
        String url = requestWrapper.getUrl();
        String host = requestWrapper.getHost();
        Request.Builder builder = new Request.Builder();
        if (host != null && !host.isEmpty()) {
            builder.addHeader(HEADER_KEY_HOST, host);
        }
        Request request = builder
                .addHeader(HEADER_KEY_USER_AGENT, USER_AGENT)
                .addHeader(HEADER_KEY_CONTENT_TYPE, HEADER_CONTENT_TYPE)
                .url(url)
                .get()
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Fail:"+e.toString());
                if(e instanceof SocketTimeoutException || e instanceof ConnectException){
                    callback.onResult(ResultType.CONNECTION_FAILED, null);
                }else{
                    callback.onResult(ResultType.FAILED, null);
                }
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String errorCode = "";
                if (response.code() >= 200 && response.code() <= 399) {
                    String content = response.body().string();
                    callback.onResult(ResultType.SUCCESS, content);
                } else if (response.code() == 401) {
                    Log.e(TAG, "UNAUTHORIZED");
                    callback.onResult(ResultType.UNAUTHORIZED, null);
                    return;
                } else {
                    String content = response.body().string();
                    Log.e(TAG, "Other Fail:" + response.code() + "  " +content);
                    try {
                        CommonResponse rsp = JSONUtil.transform(content, CommonResponse.class);
                        if(rsp != null){
                            errorCode = rsp.getErrorCode();
                        }
                        if(errorCode.equals("0x10000000")){
                            callback.onResult(ResultType.COMMON_ERROR, content);
                        }else if(errorCode.equals("0x10000001")){
                            callback.onResult(ResultType.PARAMETERS_ERROR, content);
                        }else if(errorCode.equals("0x10000002")){
                            callback.onResult(ResultType.AUTHORIZATION_ERROR, content);
                        }else if(errorCode.equals("0x10000003")){
                            callback.onResult(ResultType.PERMISSION_ERROR, content);
                        }else if(errorCode.equals("0x10000004")){
                            callback.onResult(ResultType.MEETING_NOT_EXIST, content);
                        }else if(errorCode.equals("0x10000005")){
                            callback.onResult(ResultType.MEETING_DATA_ERROR, content);
                        }else if(errorCode.equals("0x10000006")){
                            callback.onResult(ResultType.OPERATION_FORBIDDEN, content);
                        }else if(errorCode.equals("0x10000007")){
                            callback.onResult(ResultType.MEETING_STATUS_ERROR, content);
                        }else if(errorCode.equals("0x10002001")){
                            callback.onResult(ResultType.RECORDING_STREAMING_SERVICE_ERROR, content);
                        }else if(errorCode.equals("0x10000008")){
                            callback.onResult(ResultType.LENGTH_ERROR, content);
                        }else if(errorCode.equals("0x10000009")){
                            callback.onResult(ResultType.FORMAT_ERROR, content);
                        }else if(errorCode.equals("0x10000010")){
                            callback.onResult(ResultType.LICENSE_ERROR, content);
                        }else {
                            callback.onResult(ResultType.FAILED, content);
                        }
                    }catch (Exception e) {
                        Log.i("TAG", "isNetworkAvailable:" + e.getMessage());
                    }
                }
            }
        });
        return null;
    }

    public void asyncPost(RequestWrapper requestWrapper, final IResultCallback callback) throws IOException {
        String url = requestWrapper.getUrl();
        String json = requestWrapper.getPostData();
        String host = requestWrapper.getHost();
        RequestBody body = RequestBody.create(CONTENT_TYPE, json);
        Request.Builder builder = new Request.Builder();
        if (host != null && !host.isEmpty()) {
            builder.addHeader(HEADER_KEY_HOST, host);
        }
        Request request = builder
                .addHeader(HEADER_KEY_USER_AGENT, USER_AGENT)
                .addHeader(HEADER_KEY_CONTENT_TYPE, HEADER_CONTENT_TYPE)
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Fail:"+e.toString());
                if(e instanceof SocketTimeoutException || e instanceof ConnectException){
                    callback.onResult(ResultType.CONNECTION_FAILED, null);
                }else{
                    callback.onResult(ResultType.FAILED, null);
                }
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String errorCode = "";
                if (response.code() >= 200 && response.code() <= 399) {
                    String content = response.body().string();
                    callback.onResult(ResultType.SUCCESS, content);
                } else if (response.code() == 401) {
                    Log.e(TAG, "UNAUTHORIZED");
                    String content = response.body().string();
                    callback.onResult(ResultType.UNAUTHORIZED, content);
                    return;
                } else {
                    String content = response.body().string();
                    Log.e(TAG, "Other Fail:" + response.code() + "  " +content);
                    CommonResponse rsp = JSONUtil.transform(content, CommonResponse.class);
                    if(rsp != null){
                        errorCode = rsp.getErrorCode();
                    }
                    if(TextUtils.isEmpty(errorCode)){
                        callback.onResult(ResultType.FAILED, content);
                        return;
                    }
                    if(errorCode.equals("0x10000000")){
                        callback.onResult(ResultType.COMMON_ERROR, content);
                    }else if(errorCode.equals("0x10000001")){
                        callback.onResult(ResultType.PARAMETERS_ERROR, content);
                    }else if(errorCode.equals("0x10000002")){
                        callback.onResult(ResultType.AUTHORIZATION_ERROR, content);
                    }else if(errorCode.equals("0x10000003")){
                        callback.onResult(ResultType.PERMISSION_ERROR, content);
                    }else if(errorCode.equals("0x10000004")){
                        callback.onResult(ResultType.MEETING_NOT_EXIST, content);
                    }else if(errorCode.equals("0x10000005")){
                        callback.onResult(ResultType.MEETING_DATA_ERROR, content);
                    }else if(errorCode.equals("0x10000006")){
                        callback.onResult(ResultType.OPERATION_FORBIDDEN, content);
                    }else if(errorCode.equals("0x10000007")){
                        callback.onResult(ResultType.MEETING_STATUS_ERROR, content);
                    }else if(errorCode.equals("0x10002001")){
                        callback.onResult(ResultType.RECORDING_STREAMING_SERVICE_ERROR, content);
                    }else if(errorCode.equals("0x10000008")){
                        callback.onResult(ResultType.LENGTH_ERROR, content);
                    }else if(errorCode.equals("0x10000009")){
                        callback.onResult(ResultType.FORMAT_ERROR, content);
                    }else if(errorCode.equals("0x10000010")){
                        callback.onResult(ResultType.LICENSE_ERROR, content);
                    }else {
                        callback.onResult(ResultType.FAILED, content);
                    }
                }
            }
        });
    }

    public void asyncPut(RequestWrapper requestWrapper, final IResultCallback callback) throws IOException {
        String url = requestWrapper.getUrl();
        String json = requestWrapper.getPostData();
        String host = requestWrapper.getHost();
        RequestBody body = RequestBody.create(CONTENT_TYPE, json);
        Request.Builder builder = new Request.Builder();
        if (host != null && !host.isEmpty()) {
            builder.addHeader(HEADER_KEY_HOST, host);
        }
        Request request = builder
                .addHeader(HEADER_KEY_USER_AGENT, USER_AGENT)
                .addHeader(HEADER_KEY_CONTENT_TYPE, HEADER_CONTENT_TYPE)
                .url(url)
                .put(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Fail:"+e.toString());
                if(e instanceof SocketTimeoutException || e instanceof ConnectException){
                    callback.onResult(ResultType.CONNECTION_FAILED, null);
                }else{
                    callback.onResult(ResultType.FAILED, null);
                }
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String errorCode = "";
                if (response.code() >= 200 && response.code() <= 399) {
                    String content = response.body().string();
                    callback.onResult(ResultType.SUCCESS, content);
                } else if (response.code() == 401) {
                    Log.e(TAG, "UNAUTHORIZED");
                    callback.onResult(ResultType.UNAUTHORIZED, null);
                    return;
                } else {
                    String content = response.body().string();
                    Log.e(TAG, "Other Fail:" + response.code() + "  " +content);
                    CommonResponse rsp = JSONUtil.transform(content, CommonResponse.class);
                    if(rsp != null){
                        errorCode = rsp.getErrorCode();
                    }
                    if(errorCode.equals("0x10000000")){
                        callback.onResult(ResultType.COMMON_ERROR, content);
                    }else if(errorCode.equals("0x10000001")){
                        callback.onResult(ResultType.PARAMETERS_ERROR, content);
                    }else if(errorCode.equals("0x10000002")){
                        callback.onResult(ResultType.AUTHORIZATION_ERROR, content);
                    }else if(errorCode.equals("0x10000003")){
                        callback.onResult(ResultType.PERMISSION_ERROR, content);
                    }else if(errorCode.equals("0x10000004")){
                        callback.onResult(ResultType.MEETING_NOT_EXIST, content);
                    }else if(errorCode.equals("0x10000005")){
                        callback.onResult(ResultType.MEETING_DATA_ERROR, content);
                    }else if(errorCode.equals("0x10000006")){
                        callback.onResult(ResultType.OPERATION_FORBIDDEN, content);
                    }else if(errorCode.equals("0x10000007")){
                        callback.onResult(ResultType.MEETING_STATUS_ERROR, content);
                    }else if(errorCode.equals("0x10002001")){
                        callback.onResult(ResultType.RECORDING_STREAMING_SERVICE_ERROR, content);
                    }else if(errorCode.equals("0x10000008")){
                        callback.onResult(ResultType.LENGTH_ERROR, content);
                    }else if(errorCode.equals("0x10000009")){
                        callback.onResult(ResultType.FORMAT_ERROR, content);
                    }else if(errorCode.equals("0x10000010")){
                        callback.onResult(ResultType.LICENSE_ERROR, content);
                    }else {
                        callback.onResult(ResultType.FAILED, content);
                    }
                }
            }
        });
    }

    public void asyncDelete(RequestWrapper requestWrapper, final IResultCallback callback) throws IOException {
        String url = requestWrapper.getUrl();
        String json = requestWrapper.getPostData();
        String host = requestWrapper.getHost();
        RequestBody body = RequestBody.create(CONTENT_TYPE, json);
        Request.Builder builder = new Request.Builder();
        if (host != null && !host.isEmpty()) {
            builder.addHeader(HEADER_KEY_HOST, host);
        }
        Request request = builder
                .addHeader(HEADER_KEY_USER_AGENT, USER_AGENT)
                .addHeader(HEADER_KEY_CONTENT_TYPE, HEADER_CONTENT_TYPE)
                .url(url)
                .delete(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Fail:"+e.toString());
                if(e instanceof SocketTimeoutException || e instanceof ConnectException){
                    callback.onResult(ResultType.CONNECTION_FAILED, null);
                }else{
                    callback.onResult(ResultType.FAILED, null);
                }
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String errorCode = "";
                if (response.code() >= 200 && response.code() <= 399) {
                    String content = response.body().string();
                    callback.onResult(ResultType.SUCCESS, content);
                } else if (response.code() == 401) {
                    Log.e(TAG, "UNAUTHORIZED");
                    callback.onResult(ResultType.UNAUTHORIZED, null);
                    return;
                } else {
                    String content = response.body().string();
                    Log.e(TAG, "Other Fail:" + response.code() + "  " +content);
                    CommonResponse rsp = JSONUtil.transform(content, CommonResponse.class);
                    if(rsp != null){
                        errorCode = rsp.getErrorCode();
                    }
                    if(errorCode.equals("0x10000000")){
                        callback.onResult(ResultType.COMMON_ERROR, content);
                    }else if(errorCode.equals("0x10000001")){
                        callback.onResult(ResultType.PARAMETERS_ERROR, content);
                    }else if(errorCode.equals("0x10000002")){
                        callback.onResult(ResultType.AUTHORIZATION_ERROR, content);
                    }else if(errorCode.equals("0x10000003")){
                        callback.onResult(ResultType.PERMISSION_ERROR, content);
                    }else if(errorCode.equals("0x10000004")){
                        callback.onResult(ResultType.MEETING_NOT_EXIST, content);
                    }else if(errorCode.equals("0x10000005")){
                        callback.onResult(ResultType.MEETING_DATA_ERROR, content);
                    }else if(errorCode.equals("0x10000006")){
                        callback.onResult(ResultType.OPERATION_FORBIDDEN, content);
                    }else if(errorCode.equals("0x10000007")){
                        callback.onResult(ResultType.MEETING_STATUS_ERROR, content);
                    }else if(errorCode.equals("0x10002001")){
                        callback.onResult(ResultType.RECORDING_STREAMING_SERVICE_ERROR, content);
                    }else if(errorCode.equals("0x10000008")){
                        callback.onResult(ResultType.LENGTH_ERROR, content);
                    }else if(errorCode.equals("0x10000009")){
                        callback.onResult(ResultType.FORMAT_ERROR, content);
                    }else if(errorCode.equals("0x10000010")){
                        callback.onResult(ResultType.LICENSE_ERROR, content);
                    }else {
                        callback.onResult(ResultType.FAILED, content);
                    }
                }
            }
        });
    }

    public static String buildUrl(String serviceAddress, String resourceContext, Map<String, String> params) {
        String url = String.format(HTTP_SCHEMA + serviceAddress + BASE_URI_API_V1 + "/" + resourceContext, "");
        HttpUrl.Builder httpUrlBuilder = HttpUrl.parse(url).newBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            httpUrlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
        }
        return httpUrlBuilder.toString();
    }

    public String buildUrl(String serviceAddress, String resourceContextPattern, String var, Map<String, String> params) {
        String url = String.format(HTTP_SCHEMA + serviceAddress + BASE_URI_API_V1 + "/" + resourceContextPattern, var);
        HttpUrl.Builder httpUrlBuilder = HttpUrl.parse(url).newBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            httpUrlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
        }
        return httpUrlBuilder.toString();
    }

    public String buildUrl(String serviceAddress, String resourceContextPattern, String var) {
        String url = String.format(HTTP_SCHEMA + serviceAddress + BASE_URI_API_V1 + "/" + resourceContextPattern, var);
        HttpUrl.Builder httpUrlBuilder = HttpUrl.parse(url).newBuilder();
        return httpUrlBuilder.toString();
    }

}
