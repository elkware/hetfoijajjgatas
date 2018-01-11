package co.elkware.hetfoijajjatas.view;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;

@SpringViewDisplay
public class JajViewDisplay extends CustomComponent implements ViewDisplay {
    @Override
    public void showView(View view) {
        setCompositionRoot((Component) view);
    }
}
