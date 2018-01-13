package co.elkware.hetfoijajjatas.ui;

import co.elkware.hetfoijajjatas.service.WailThumbService;
import co.elkware.hetfoijajjatas.util.Utils;
import co.elkware.hetfoijajjatas.view.AddWailView;
import co.elkware.hetfoijajjatas.view.JajViewDisplay;
import co.elkware.hetfoijajjatas.view.WailView;
import com.github.appreciated.material.MaterialTheme;
import com.vaadin.annotations.Theme;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;

@Theme("jaj")
@SpringUI
public class JajUI extends UI {

    @Autowired
    private JajViewDisplay jajViewDisplay;

    @Autowired
    private WailThumbService wailThumbService;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        addStyleName("background-pic");
        wailThumbService.insertFingerprintIfNotExists(Utils.getBrowserFingerprint());
        String url = vaadinRequest.getParameterMap().get("v-loc")[0];

        Page.getCurrent().setTitle("Hétfői jajgatás");
        CssLayout layout = new CssLayout(new VerticalLayout(header(), jajViewDisplay));
        layout.setWidth(100, Unit.PERCENTAGE);
        setContent(layout);
        String viewName = WailView.NAME;
        try {
            viewName = url.substring(url.indexOf("#!"));
            viewName = viewName.substring(2);
        } catch (Exception ignored) { }
        getNavigator().navigateTo(viewName);
    }

    private Component header() {
        //Label headerLabel = new Label("Jaj de jó lenne ha nem lenne ez a hétfő");
        //headerLabel.addStyleName(MaterialTheme.LABEL_LARGE);
        MenuBar menuBar = new MenuBar();
        menuBar.addItem("(Nem csak) Hétfői jajgatás", c -> {
            Window window = new Window("Információ a (hétfői) jajgató oldalról");
            Panel p = new Panel();
            Label l = new Label("Tudniillik, ez az oldal azert készült el, mert a hetfőket kibírni nem lehet.<br>Az igazat megvallva a többi munkanap is csapnivaló, de a hétfő a legszörnyűbb.<br>" +
                    "Viszont ha a hétfőt betiltanák, akkor a kedd válna új hétfővé, tehát az nem megoldás.<br><br>" +
                    "Az egyedüli megoldás az az, hogy vásárolsz jó sört, és megiszod.<br>" +
                    "De ha már magadnak veszel akkor miért ne vennél nekem is egyet. Ezt megteheted <a href=\"https://www.paypal.me/elkware\" target=\"_blank\">ITT</a>.<br>" +
                    "Köszönöm előre is, kedves Barátom!", ContentMode.HTML);
            VerticalLayout layout = new VerticalLayout(l);
            p.setContent(layout);
            window.setContent(p);
            window.setModal(true);
            window.setResizable(false);
            window.setClosable(true);
            UI.getCurrent().addWindow(window);
        }).setStyleName("boldfont");
        menuBar.addItem("Jajgatások", (MenuBar.Command) menuItem ->  getNavigator().navigateTo(WailView.NAME));
        menuBar.addItem("Jajjantok én is egyet!", (MenuBar.Command) menuItem -> getNavigator().navigateTo(AddWailView.NAME));
        /*MenuBar.MenuItem fbMenuItem = menuBar.addItem("Megosztom a fészen!", VaadinIcons.FACEBOOK_SQUARE, c -> JavaScript.getCurrent().execute("window.open('https://www.facebook.com/sharer/sharer.php?u=' + encodeURIComponent(document.URL) + '&t=' + encodeURIComponent(document.title)); return false;"));
        if (!Page.getCurrent().getWebBrowser().isFirefox()) {
            fbMenuItem.setStyleName("menuRight");
        }*/
        menuBar.addStyleName(MaterialTheme.MENUBAR_PRIMARY);
        menuBar.setWidth(100, Unit.PERCENTAGE);
        //Component fbShareBtn = Utils.getFBShareButton();
        VerticalLayout layout = new VerticalLayout(/*headerLabel,*/ menuBar, Utils.getFBShareButton()/*, fbShareBtn*/);
        layout.setSpacing(false);
        //layout.setComponentAlignment(fbShareBtn, Alignment.MIDDLE_RIGHT);
        layout.setWidth(100, Unit.PERCENTAGE);
        return layout;
    }
}
