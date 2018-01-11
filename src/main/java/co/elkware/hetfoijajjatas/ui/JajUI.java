package co.elkware.hetfoijajjatas.ui;

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

import java.util.Arrays;

@Theme("jaj")
@SpringUI
public class JajUI extends UI {

    @Autowired
    private JajViewDisplay jajViewDisplay;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        CssLayout layout = new CssLayout(Utils.fbJS(), new VerticalLayout(header(), jajViewDisplay));
        layout.setWidth(100, Unit.PERCENTAGE);
        setContent(layout);
        String url = vaadinRequest.getParameterMap().get("v-loc")[0];
        String viewName = WailView.NAME;
        try {
            viewName = url.substring(url.indexOf("#!"));
            viewName = viewName.substring(2);
        } catch (Exception ignored) { }
        System.out.println(viewName);
        getNavigator().navigateTo(viewName);
        Page.getCurrent().setTitle("Hétfői jajgatás");
    }

    private Component header() {
        Label headerLabel = new Label("Jaj de jó lenne ha nem lenne ez a hétfő");
        headerLabel.addStyleName(MaterialTheme.LABEL_HUGE);
        MenuBar menuBar = new MenuBar();
        menuBar.addItem("Jajgatások", (MenuBar.Command) menuItem ->  getNavigator().navigateTo(WailView.NAME));
        menuBar.addItem("Jajjantok én is egyet!", (MenuBar.Command) menuItem -> { getNavigator().navigateTo(AddWailView.NAME); });
        menuBar.addStyleName(MaterialTheme.MENUBAR_PRIMARY);
        menuBar.setWidth(100, Unit.PERCENTAGE);

        VerticalLayout layout = new VerticalLayout(headerLabel, menuBar/*, fbShareBtn*/);
        //layout.setComponentAlignment(fbShareBtn, Alignment.MIDDLE_RIGHT);
        return layout;
    }
}
