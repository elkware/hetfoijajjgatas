package co.elkware.hetfoijajjatas.util;

import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;

import java.io.ByteArrayInputStream;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;

public final class Utils {

    private static final String FB_JS = "<div id=\"fb-root\"></div>\n" +
            "<script>(function(d, s, id) {\n" +
            "  var js, fjs = d.getElementsByTagName(s)[0];\n" +
            "  if (d.getElementById(id)) return;\n" +
            "  js = d.createElement(s); js.id = id;\n" +
            "  js.src = 'https://connect.facebook.net/en_GB/sdk.js#xfbml=1&version=v2.11';\n" +
            "  fjs.parentNode.insertBefore(js, fjs);\n" +
            "}(document, 'script', 'facebook-jssdk'));</script>";

    private Utils() {}

    public static Component getFBShareButton(String linkToShare) {
        final String encodedLink;
        try {
            encodedLink = URLEncoder.encode(linkToShare, "UTF-8");
            final String fbShareStuff = "<div class=\"fb-share-button\" data-href=\""+ linkToShare + "\" data-layout=\"button_count\" data-size=\"small\" data-mobile-iframe=\"true\"><a class=\"fb-xfbml-parse-ignore\" target=\"_blank\" href=\"https://www.facebook.com/sharer/sharer.php?u=" + encodedLink + "&amp;src=sdkpreparse\">Share</a></div>";
            return new CustomLayout(new ByteArrayInputStream(fbShareStuff.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static Component fbJS() {
        try {
            Component c = new CustomLayout(new ByteArrayInputStream(FB_JS.getBytes()));
            c.addStyleName("hide");
            return c;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static Set<Integer> thumbSet() {
        Object thumbUpSet = VaadinSession.getCurrent().getAttribute("thumb_set");
        if (thumbUpSet == null) {
            VaadinSession.getCurrent().setAttribute("thumb_set", new HashSet<Integer>());
        }
        return (Set<Integer>) VaadinSession.getCurrent().getAttribute("thumb_set");
    }


}
