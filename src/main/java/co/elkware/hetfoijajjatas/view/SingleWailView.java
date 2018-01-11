package co.elkware.hetfoijajjatas.view;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;


@SpringView(name = SingleWailView.NAME)
public class SingleWailView extends CustomComponent implements View {
    public static final String NAME = "viewWail";
    Label params = new Label();
    public SingleWailView() {
        setCompositionRoot(params);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        params.setValue(event.getParameters());
    }
}
