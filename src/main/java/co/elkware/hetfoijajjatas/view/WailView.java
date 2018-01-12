package co.elkware.hetfoijajjatas.view;

import co.elkware.hetfoijajjatas.service.WailService;
import co.elkware.hetfoijajjatas.service.WailThumbService;
import co.elkware.hetfoijajjatas.util.Utils;
import com.github.appreciated.material.MaterialTheme;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@SpringView(name = WailView.NAME)
public class WailView extends CustomComponent implements View {
    public static final String NAME = "";

    private VerticalLayout wailContentLayout;
    private final WailService wailService;
    private final WailThumbService wailThumbService;
    private Button pageCounter;

    private int currentPage = 0;
    private String pageCountTemplate = "Oldal {0}/{1}";

    @Autowired
    public WailView(WailService wailService, WailThumbService wailThumbService) {
        this.wailService = wailService;
        this.wailThumbService = wailThumbService;
    }

    @PostConstruct
    private void init() {
        VerticalLayout wailLayout = new VerticalLayout();
        wailContentLayout = new VerticalLayout();
        pageCounter = new Button();
        pageCounter.addStyleNames(MaterialTheme.BUTTON_ROUND, MaterialTheme.BUTTON_FLAT, MaterialTheme.BUTTON_FRIENDLY, MaterialTheme.BUTTON_SMALL);
        pageCounter.setEnabled(false);
        pageCounter.setCaption(pageCountTemplate.replace("{0}", Integer.toString(currentPage + 1)).replace("{1}", Integer.toString(numberOfPages() + 1)));
        wailContentLayout.setMargin(false);

        Button nextBtn = new Button(VaadinIcons.ARROW_CIRCLE_RIGHT);
        nextBtn.addStyleNames(MaterialTheme.BUTTON_ROUND, MaterialTheme.BUTTON_SMALL);
        nextBtn.addClickListener(cl -> turnPage(1));
        Button prevBtn = new Button(VaadinIcons.ARROW_CIRCLE_LEFT);
        prevBtn.addStyleNames(MaterialTheme.BUTTON_ROUND, MaterialTheme.BUTTON_SMALL);
        prevBtn.addClickListener(cl -> turnPage(-1));

        HorizontalLayout btnLayout = new HorizontalLayout(prevBtn, pageCounter, nextBtn);
        btnLayout.setSizeUndefined();
        wailLayout.addComponent(btnLayout);
        wailLayout.addComponent(wailContentLayout);
        wailLayout.setComponentAlignment(btnLayout, Alignment.MIDDLE_LEFT);
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
        wailContentLayout.removeAllComponents();
        populatePanel(currentPage * Utils.PAGE_SIZE);
        pageCounter.setCaption(pageCountTemplate.replace("{0}", Integer.toString(currentPage + 1)).replace("{1}", Integer.toString(numberOfPages() + 1)));
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
                Button viewCommentsBtn = new Button("Megnezem a hozzaszolasokat!", cl -> UI.getCurrent().getNavigator().navigateTo(SingleWailView.NAME + "/w="+wail.getId()));
                viewCommentsBtn.addStyleNames(MaterialTheme.BUTTON_FLAT, MaterialTheme.BUTTON_FRIENDLY, MaterialTheme.BUTTON_TINY);

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
                wailContentLayout.addComponent(p);
            }
        );
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        Page.getCurrent().setTitle("Hétfői jajgatás");
    }
}
