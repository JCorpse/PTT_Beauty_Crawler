package com.jcorpse.beauty.http;

import com.jcorpse.beauty.constant.Constant;
import com.jcorpse.beauty.entity.WebPage;
import com.jcorpse.beauty.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;

import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.pool.PoolReusePolicy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;

import java.io.*;


@Slf4j
public class HttpManager {
    private static PoolingHttpClientConnectionManager HttpPoolManager = null;
    private static CookieStore CookieStore = new BasicCookieStore();


    static {
        try {
            HttpPoolManager = PoolingHttpClientConnectionManagerBuilder.create()
                    .setSSLSocketFactory(SSLConnectionSocketFactoryBuilder.create()
                            .setSslContext(SSLContextBuilder.create()
                                    .loadTrustMaterial(TrustAllStrategy.INSTANCE)
                                    .build())
                            .build())
                    .setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(Timeout.ofSeconds(3)).build())
                    .setPoolConcurrencyPolicy(PoolConcurrencyPolicy.STRICT)
                    .setConnPoolPolicy(PoolReusePolicy.LIFO)
                    .setConnectionTimeToLive(TimeValue.ofMinutes(1))
                    .build();
        } catch (Exception e) {
            log.error("HttpPoolManager init error: {}", e.getMessage());
        }
    }

    private HttpManager() {
    }

    public static HttpManager getInstance() {
        return Holder.Instance;
    }

    private static class Holder {
        private static final HttpManager Instance = new HttpManager();
    }

    private CloseableHttpClient createHttpClient(long timeout) {
        RequestConfig.Builder RequestBuilder = RequestConfig.custom()
                .setResponseTimeout(Timeout.ofSeconds(timeout))
                .setConnectTimeout(Timeout.ofSeconds(timeout));
        RequestConfig Request = RequestBuilder.build();

        CloseableHttpClient client = HttpClients.custom()
                .setUserAgent(Constant.USER_AGENT)
                .setDefaultRequestConfig(Request)
                .setConnectionManager(HttpPoolManager)
                .setDefaultCookieStore(CookieStore)
                .build();
        return client;
    }

    public CloseableHttpResponse getResponse(String url) {
        return getResponse(url, 30000L);
    }

    public CloseableHttpResponse getResponse(String url, long timeout) {
        HttpGet httpGet = new HttpGet(url);
        return getResponse(httpGet, timeout);
    }

    public CloseableHttpResponse getResponse(HttpGet httpGet, long timeout) {
        CloseableHttpResponse response = null;
        try {
            response = createHttpClient(timeout).execute(httpGet);
        } catch (Exception e) {
            log.error("getResponse error : {}", e.getMessage());
        }
        return response;
    }

    public WebPage getBody(String url) {
        return getBody(url, "UTF-8");
    }

    public WebPage getBody(String url, String charest) {
        WebPage Page = new WebPage();
        CloseableHttpResponse Response = getResponse(url);
        if (Response != null) {
            Page.setCode(Response.getCode());
            Page.setUrl(url);
            if (Response.getCode() == 200) {
                try {
                    Page.setBody(EntityUtils.toString(Response.getEntity(), charest));
                } catch (Exception e) {
                    log.error("getBody error : {}", e.getMessage());
                } finally {
                    try {
                        Response.close();
                    } catch (IOException e) {
                        log.error("Response close error : {}", e.getMessage());
                    }
                }
            }
        }
        return Page;
    }

    public void Download(String url, String path, String filename) {
        CloseableHttpResponse Response = getResponse(url);
        if (Response != null) {
            if (Response.getCode() == 200) {
                if (!FileUtil.isExist(path)) {
                    FileUtil.DirMaker(path);
                }
                String filePath = path + "\\" + filename;
                BufferedInputStream bis = null;
                BufferedOutputStream bos = null;
                try {
                    bis = new BufferedInputStream(Response.getEntity().getContent());
                    bos = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
                    int inByte;
                    while ((inByte = bis.read()) != -1) bos.write(inByte);
                } catch (Exception e) {
                    log.error("Download error: {}", e.getMessage());
                } finally {
                    try {
                        if ((bis != null)) {
                            bis.close();
                        }
                        if ((bos != null)) {
                            bos.close();
                        }
                    } catch (IOException e) {
                        log.error("Download Stream Close error: {}", e.getMessage());
                    }
                }
            }
        }

    }

    public static void setCookie(String name, String value, String Domain) {
        BasicClientCookie cookie = new BasicClientCookie(name, value);
        cookie.setDomain(Domain);
        cookie.setPath("/");
        CookieStore.addCookie(cookie);
    }
}
