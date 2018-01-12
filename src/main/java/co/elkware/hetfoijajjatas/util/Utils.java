package co.elkware.hetfoijajjatas.util;

import com.vaadin.server.Page;
import com.vaadin.server.WebBrowser;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public final class Utils {

    public static final int PAGE_SIZE = 10;

    private Utils() {}

    public static String getBrowserFingerprint() {
        WebBrowser browser = Page.getCurrent().getWebBrowser();
        int scrHeight = browser.getScreenHeight();
        int scrWidth = browser.getScreenWidth();
        String ip = browser.getAddress();
        String timezone = browser.getTimeZoneId();
        String userAgent = browser.getBrowserApplication();

        String rawFingerprint = "h=" + scrHeight +
                "&w=" + scrWidth +
                "&ip=" + ip +
                "&tz=" + timezone +
                "&ua=" + userAgent;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawFingerprint.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
