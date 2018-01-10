package co.elkware.hetfoijajjatas.ui;

import co.elkware.hetfoijajjatas.view.AddWailView;
import co.elkware.hetfoijajjatas.view.JajViewDisplay;
import co.elkware.hetfoijajjatas.view.WailView;
import com.github.appreciated.material.MaterialTheme;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;

@Theme("jaj")
@SpringUI
public class JajUI extends UI {

    @Autowired
    private JajViewDisplay jajViewDisplay;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        VerticalLayout layout = new VerticalLayout(header(), jajViewDisplay);
        layout.setWidth(100, Unit.PERCENTAGE);
        setContent(layout);
        getNavigator().navigateTo(WailView.NAME);
    }

    private Component header() {
        Label headerLabel = new Label("Jaj de jó lenne ha nem lenne ez a hétfő");
        headerLabel.addStyleName(MaterialTheme.LABEL_HUGE);
        MenuBar menuBar = new MenuBar();
        menuBar.addItem("Jajgatasok", (MenuBar.Command) menuItem ->  getNavigator().navigateTo(WailView.NAME));
        menuBar.addItem("Jajjantok egyet en is!", (MenuBar.Command) menuItem -> { getNavigator().navigateTo(AddWailView.NAME); });
        menuBar.addStyleName(MaterialTheme.MENUBAR_PRIMARY);
        menuBar.setWidth(100, Unit.PERCENTAGE);
        VerticalLayout layout = new VerticalLayout(headerLabel, menuBar);
        return layout;
    }
}
