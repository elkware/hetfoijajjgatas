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
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.Cookie;
import java.util.Arrays;

@Theme("jaj")
@SpringUI
public class JajUI extends UI {

    @Autowired
    private JajViewDisplay jajViewDisplay;

    @Autowired
    private WailThumbService wailThumbService;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
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
        Label headerLabel = new Label("Jaj de jó lenne ha nem lenne ez a hétfő");
        headerLabel.addStyleName(MaterialTheme.LABEL_HUGE);
        MenuBar menuBar = new MenuBar();
        menuBar.addItem("Jajgatások", (MenuBar.Command) menuItem ->  getNavigator().navigateTo(WailView.NAME));
        menuBar.addItem("Jajjantok én is egyet!", (MenuBar.Command) menuItem -> getNavigator().navigateTo(AddWailView.NAME));
        menuBar.addStyleName(MaterialTheme.MENUBAR_PRIMARY);
        menuBar.setWidth(100, Unit.PERCENTAGE);
        Component fbShareBtn = Utils.getFBShareButton();
        VerticalLayout layout = new VerticalLayout(headerLabel, menuBar, fbShareBtn);
        layout.setComponentAlignment(fbShareBtn, Alignment.MIDDLE_RIGHT);
        return layout;
    }
}
