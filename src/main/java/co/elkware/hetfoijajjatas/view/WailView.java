package co.elkware.hetfoijajjatas.view;

import co.elkware.hetfoijajjatas.service.WailService;
import com.github.appreciated.material.MaterialTheme;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.server.ExternalResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@SpringView(name = WailView.NAME)
public class WailView extends CustomComponent implements View {
    public static final String NAME = "wails";

    private VerticalLayout wailLayout;

    @Autowired
    private WailService wailService;

    private int currentPage = 0;

    public WailView() {
    }

    @PostConstruct
    private void init() {
        wailLayout = new VerticalLayout();
        Button nextBtn = new Button(VaadinIcons.ARROW_CIRCLE_RIGHT);
        nextBtn.addStyleNames(MaterialTheme.BUTTON_ROUND);
        nextBtn.addClickListener(cl -> turnPage(1));
        Button prevBtn = new Button(VaadinIcons.ARROW_CIRCLE_LEFT);
        prevBtn.addStyleNames(MaterialTheme.BUTTON_ROUND);
        prevBtn.addClickListener(cl -> turnPage(-1));

        HorizontalLayout btnLayout = new HorizontalLayout(prevBtn, nextBtn);
        btnLayout.setSizeUndefined();
        wailLayout.addComponent(btnLayout);
        wailLayout.setComponentAlignment(btnLayout, Alignment.MIDDLE_CENTER);
        populatePanel(currentPage * WailService.PAGE_SIZE);
        setCompositionRoot(wailLayout);
    }

    private void turnPage(int dir) {
        if (currentPage == 0 && dir == -1) {
            return;
        }
        if (currentPage == numberOfPages() && dir == 1) {
            return;
        }
        currentPage += dir;
        List<Panel> componentsToRemove = new ArrayList<>();
        wailLayout.iterator().forEachRemaining(component -> {
            if (component instanceof Panel) {
                componentsToRemove.add((Panel) component);
            }
        });
        componentsToRemove.forEach(p -> wailLayout.removeComponent(p));
        populatePanel(currentPage * WailService.PAGE_SIZE);
    }

    private int numberOfPages() {
        return wailService.getWailCount() / WailService.PAGE_SIZE;
    }

    private void populatePanel(int offset) {
        final boolean[] alterBackground = {false};
        wailService.getWails(offset).forEach(wail -> {
                VerticalLayout contentLayout = new VerticalLayout();

                Label wailer = new Label(wail.getUserId());
                wailer.addStyleNames(MaterialTheme.LABEL_COLORED, MaterialTheme.LABEL_LARGE, MaterialTheme.LABEL_BORDERLESS);
                contentLayout.addComponent(wailer);

                Label wailLbl = new Label(wail.getContent());
                wailLbl.addStyleName(MaterialTheme.LABEL_BORDERLESS);
                contentLayout.addComponent(wailLbl);

                if (wail.getLink() != null) {
                    Link link = new Link("Jajgatást kiegészítő hivatkozás, kattints rá!", new ExternalResource(wail.getLink()));
                    link.setTargetName("_blank");
                    contentLayout.addComponent(link);
                }

                Label date = new Label("Jajgatás #" + wail.getId().toString() + " @ " + wail.getCreatedAt().toString());
                date.addStyleNames(MaterialTheme.LABEL_TINY, MaterialTheme.LABEL_BORDERLESS);
                contentLayout.addComponent(date);
                contentLayout.setComponentAlignment(date, Alignment.MIDDLE_RIGHT);

                Panel p = new Panel();
                p.setContent(contentLayout);
                if (alterBackground[0]) {
                    p.addStyleName("alternate-panel-color");
                }
                alterBackground[0] = !alterBackground[0];
                wailLayout.addComponent(p);
            }
        );
    }
}
