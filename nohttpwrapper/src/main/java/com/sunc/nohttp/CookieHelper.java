package com.sunc.nohttp;

import android.webkit.CookieSyncManager;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;

import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class CookieHelper {
    private final static String APPEND_COOKIE_TAG = "isInWebView=tjdj_webViewAndroid;";
    /**
     * 将webview的cookie同步给本机
     * @param url
     */
    public static void syncWebCookiesToLocal(String url) {
        android.webkit.CookieManager webCookieManager = android.webkit.CookieManager.getInstance();
        String webCookie = webCookieManager.getCookie(url);
        CookieManager cookieManager = (CookieManager) NoHttp.getDefaultCookieHandler();
        CookieStore store = cookieManager.getCookieStore();
        String[] strings = webCookie.split(";");
        URI uri = URI.create(url);
        for (String item : strings) {
            String[] cookieString = item.split("=", 2);
            HttpCookie cookie = new HttpCookie(cookieString[0], cookieString[1]);
            cookie.setDomain(uri.getHost());
            cookie.setMaxAge(86400);  //这里的有效时间为1天
            cookie.setPath("/");
            store.add(uri, cookie);
        }
    }

    /**
     * 将本应用的cookie同步给webview
     * @param domain
     */
    public void syncCookiesToWeb(String domain) {

        CookieManager cookieManager = (CookieManager) NoHttp.getDefaultCookieHandler();
        CookieStore store = cookieManager.getCookieStore();
        List<HttpCookie> cookiesList = store.getCookies();

        CookieSyncManager.createInstance(NoHttp.getContext());
        android.webkit.CookieManager webCookieManager = android.webkit.CookieManager.getInstance();
        webCookieManager.removeAllCookie();
        webCookieManager.setAcceptCookie(true);
        android.webkit.CookieManager.getInstance().setCookie(domain, APPEND_COOKIE_TAG);

        if(cookiesList != null) {
            int size = cookiesList.size();
            if(size > 0) {
                for (int i = 0; i < size; i++) {
                    HttpCookie cookie = cookiesList.get(i);
                    String cookieString = cookie.getName() + "=" + cookie.getValue();
                    webCookieManager.setCookie(domain, cookieString);
                }
            }
        }
        CookieSyncManager.getInstance().sync();
    }

    /**
     * 清除所有本地cookies
     */
    public void clearAllLocalCookies() {
        CookieManager cookieManager = (CookieManager) NoHttp.getDefaultCookieHandler();
        CookieStore store = cookieManager.getCookieStore();
        store.removeAll();
    }

    /**
     *
     * 为了内外网登录，需要copy一份cookie到另一个域名下
     * 请在登录完成之后调用此接口完成copy
     * @param domain
     */
    public static void copyCookieToOuterNet(String domain) {

        CookieManager cookieManager = (CookieManager) NoHttp.getDefaultCookieHandler();
        CookieStore store = cookieManager.getCookieStore();
        List<HttpCookie> cookiesList = store.getCookies();
        List<HttpCookie> newCookieList = new ArrayList<HttpCookie>();

        for (HttpCookie item : cookiesList) {
            HttpCookie newCookie;
            if (item.getName().equals("login_user")) {
                newCookie = new HttpCookie("login_temp", item.getValue());
            } else {
                newCookie = new HttpCookie(item.getName(), item.getValue());
            }
            newCookie.setDomain(domain);
            newCookie.setMaxAge(item.getMaxAge());
            newCookie.setVersion(item.getVersion());
            newCookie.setPath(item.getPath());
            newCookieList.add(newCookie);
        }
        for (HttpCookie cok : newCookieList) {
            try {
                URI uri = new URI(domain);
                store.add(uri, cok);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

    }

}
