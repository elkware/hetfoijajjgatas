package co.elkware.hetfoijajjatas.view;

import co.elkware.hetfoijajjatas.service.WailService;
import co.elkware.hetfoijajjatas.service.WailThumbService;
import co.elkware.hetfoijajjatas.util.Utils;
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
    public static final String NAME = "";

    private VerticalLayout wailLayout;

    private final WailService wailService;

    private int currentPage = 0;

    private WailThumbService wailThumbService;

    @Autowired
    public WailView(WailService wailService, WailThumbService wailThumbService) {
        this.wailService = wailService;
        this.wailThumbService = wailThumbService;
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
        populatePanel(currentPage * Utils.PAGE_SIZE);
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
        populatePanel(currentPage * Utils.PAGE_SIZE);
    }

    private int numberOfPages() {
        return (wailService.count() - 1) / Utils.PAGE_SIZE;
    }

    private void populatePanel(int offset) {
        final boolean[] alterBackground = {false};
        wailService.list(offset).forEach(wail -> {
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

                Button thumbsUpBtn = new Button(wail.getThumbsUp().toString(), VaadinIcons.THUMBS_UP);
                thumbsUpBtn.addStyleNames(MaterialTheme.BUTTON_ROUND, MaterialTheme.BUTTON_FRIENDLY, MaterialTheme.BUTTON_TINY);

                Button thumbsDownBtn = new Button(wail.getThumbsDown().toString(), VaadinIcons.THUMBS_DOWN);
                thumbsDownBtn.addStyleNames(MaterialTheme.BUTTON_ROUND, MaterialTheme.BUTTON_DANGER, MaterialTheme.BUTTON_TINY);

                if (wailThumbService.hasThumbed(Utils.getBrowserFingerprint(), wail.getId())) {
                    thumbsUpBtn.setEnabled(false);
                    thumbsDownBtn.setEnabled(false);
                } else {
                    thumbsUpBtn.addClickListener(cl -> {
                        int thumbsUp = wailService.thumbUp(wail.getId());
                        thumbsUpBtn.setCaption(Integer.toString(thumbsUp));
                        wailThumbService.addThumb(Utils.getBrowserFingerprint(), wail.getId());
                        thumbsUpBtn.setEnabled(false);
                        thumbsDownBtn.setEnabled(false);
                    });

                    thumbsDownBtn.addClickListener(cl -> {
                        int thumbsDown = wailService.thumbDown(wail.getId());
                        thumbsDownBtn.setCaption(Integer.toString(thumbsDown));
                        wailThumbService.addThumb(Utils.getBrowserFingerprint(), wail.getId());
                        thumbsUpBtn.setEnabled(false);
                        thumbsDownBtn.setEnabled(false);
                    });
                }

                Label date = new Label("Jajgatás #" + wail.getId().toString() + " @ " + wail.getCreatedAt().toString());
                date.addStyleNames(MaterialTheme.LABEL_TINY, MaterialTheme.LABEL_BORDERLESS);

                HorizontalLayout ratingLayout = new HorizontalLayout();
                ratingLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
                ratingLayout.addComponents(thumbsUpBtn, thumbsDownBtn);

                contentLayout.addComponent(ratingLayout);
                contentLayout.setComponentAlignment(ratingLayout, Alignment.MIDDLE_RIGHT);

                HorizontalLayout commentAndDateLayout = new HorizontalLayout();
                commentAndDateLayout.setWidth(100, Unit.PERCENTAGE);
                Link viewCommentsBtn = new Link("Megnezem a hozzaszolasokat!", new ExternalResource("/#!" + SingleWailView.NAME + "/w="+wail.getId()));
                viewCommentsBtn.addStyleNames(MaterialTheme.LINK_SMALL);
                commentAndDateLayout.addComponents(viewCommentsBtn, date);
                commentAndDateLayout.setComponentAlignment(viewCommentsBtn, Alignment.MIDDLE_LEFT);
                commentAndDateLayout.setComponentAlignment(date, Alignment.MIDDLE_RIGHT);
                contentLayout.addComponent(commentAndDateLayout);

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
