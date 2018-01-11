package co.elkware.hetfoijajjatas.util;

import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;

import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.Set;

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

    @SuppressWarnings("unchecked")
    public static Set<Integer> thumbSet() {
        Object thumbUpSet = VaadinSession.getCurrent().getAttribute("thumb_set");
        if (thumbUpSet == null) {
            VaadinSession.getCurrent().setAttribute("thumb_set", new HashSet<Integer>());
        }
        return (Set<Integer>) VaadinSession.getCurrent().getAttribute("thumb_set");
    }

    public static void addThumb(Integer id) {
        thumbSet().add(id);
    }

}
