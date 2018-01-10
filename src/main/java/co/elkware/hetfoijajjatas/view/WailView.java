package co.elkware.hetfoijajjatas.view;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

@SpringView(name = WailView.NAME)
public class WailView extends CustomComponent implements View {
    public static final String NAME = "wails";

    public WailView() {
        VerticalLayout layout = new VerticalLayout();
        for (int i=0; i<10; i++) {
            Panel panel = new Panel("Jajgatas " + (i+1));
            panel.setContent(new Label("Lorem ipsum " + (i+1)));
            layout.addComponent(panel);
        }
        setCompositionRoot(layout);
    }
}
