package co.elkware.hetfoijajjatas.util;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.WebBrowser;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.UI;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class Utils {

    public static final int PAGE_SIZE = 10;

    private Utils() {}

    public static Component getFBShareButton() {
        try {
            final String fbShareStuff = "<div id=\"share-buttons\"><a href=\"https://www.facebook.com/sharer/sharer.php?u=&t=\" title=\"Megosztom a fejszbukkon!\" target=\"_blank\" onclick=\"window.open('https://www.facebook.com/sharer/sharer.php?u=' + encodeURIComponent(document.URL) + '&t=' + encodeURIComponent(document.URL)); return false;\">" +
                    "<img src=\"/VAADIN/themes/jaj/facebook.png\" alt=\"Megosztom a fejszbukkon!\" />" +
                    "</a></div>";
            return new CustomLayout(new ByteArrayInputStream(fbShareStuff.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

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
