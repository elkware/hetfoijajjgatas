package co.elkware.hetfoijajjatas.view;

import co.elkware.hetfoijajjatas.db.generated.tables.records.CommentRecord;
import co.elkware.hetfoijajjatas.db.generated.tables.records.WailRecord;
import co.elkware.hetfoijajjatas.service.CommentService;
import co.elkware.hetfoijajjatas.service.WailService;
import co.elkware.hetfoijajjatas.service.WailThumbService;
import co.elkware.hetfoijajjatas.util.Utils;
import com.github.appreciated.material.MaterialTheme;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@SpringView(name = SingleWailView.NAME)
public class SingleWailView extends CustomComponent implements View {
    public static final String NAME = "viewWail";
    private VerticalLayout baseLayout;
    private VerticalLayout commentLayout;
    private HorizontalLayout commentNavigationLayout;
    private FormLayout addNewCommentLayout;

    @Autowired
    private WailService wailService;

    @Autowired
    private CommentService commentService;
    @Autowired
    private WailThumbService wailThumbService;

    int currentPage = 0;

    public SingleWailView() {
        baseLayout = new VerticalLayout();
        commentLayout = new VerticalLayout();
        commentNavigationLayout = new HorizontalLayout();
        addNewCommentLayout = new FormLayout();
        setCompositionRoot(baseLayout);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        Map<String, String> pm = event.getParameterMap();
        if (pm.get("w") == null) {
            Notification.show("Hujaja, ne hakkolj!", Notification.Type.ERROR_MESSAGE);
            UI.getCurrent().getNavigator().navigateTo(WailView.NAME);
            return;
        }
        final Integer id;
        try {
            id = Integer.valueOf(pm.get("w"));
        } catch (Exception e) {
            Notification.show("Hujaja, ne hakkolj!", Notification.Type.ERROR_MESSAGE);
            UI.getCurrent().getNavigator().navigateTo(WailView.NAME);
            return;
        }
        Optional<WailRecord> wailRecordOpt = wailService.findOne(id);
        if (!wailRecordOpt.isPresent()) {
            Notification.show("Hujaja, nem talalom a jajjantast", Notification.Type.ERROR_MESSAGE);
            UI.getCurrent().getNavigator().navigateTo(WailView.NAME);
            return;
        }
        setData(id);
        final WailRecord wail = wailRecordOpt.get();
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

        HorizontalLayout ratingAndDateLayout = new HorizontalLayout();
        ratingAndDateLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        ratingAndDateLayout.addComponents(thumbsUpBtn, thumbsDownBtn);

        contentLayout.addComponent(ratingAndDateLayout);
        contentLayout.setComponentAlignment(ratingAndDateLayout, Alignment.MIDDLE_RIGHT);
        contentLayout.addComponent(date);
        contentLayout.setComponentAlignment(date, Alignment.MIDDLE_RIGHT);

        Panel p = new Panel();
        p.setContent(contentLayout);

        baseLayout.addComponent(p);

        // adding comments

        populateComments(id);
        baseLayout.addComponent(commentLayout);

        // add comment navigation
        Button nextCommentBtn = new Button(VaadinIcons.ARROW_CIRCLE_RIGHT);
        nextCommentBtn.addStyleNames(MaterialTheme.BUTTON_ROUND, MaterialTheme.BUTTON_TINY);
        Button prevCommentBtn = new Button(VaadinIcons.ARROW_CIRCLE_LEFT);
        prevCommentBtn.addStyleNames(MaterialTheme.BUTTON_ROUND, MaterialTheme.BUTTON_TINY);

        commentNavigationLayout.addComponents(prevCommentBtn, nextCommentBtn);
        commentNavigationLayout.setComponentAlignment(prevCommentBtn, Alignment.MIDDLE_RIGHT);
        commentNavigationLayout.setComponentAlignment(nextCommentBtn, Alignment.MIDDLE_LEFT);
        baseLayout.addComponent(commentNavigationLayout);

        // new comment form
        TextField commenterTf = new TextField("Alnevem:");
        commenterTf.setRequiredIndicatorVisible(true);
        TextArea commentTa = new TextArea("Hozzafuznivalom:");
        commentTa.setRequiredIndicatorVisible(true);
        Button sendCommentBtn = new Button("Hozzafuzom");
        sendCommentBtn.addClickListener(cl -> {
            commentService.add(id, commenterTf.getValue(), commentTa.getValue());
            populateComments(id);
        });
        addNewCommentLayout.addComponents(commenterTf, commentTa, sendCommentBtn);
        baseLayout.addComponent(new Panel(addNewCommentLayout));

    }

    private int numberOfPages(Integer wailId) {
        return (commentService.countByWailId(wailId) - 1) / Utils.PAGE_SIZE;
    }

    private void populateComments(Integer id) {
        commentLayout.removeAllComponents();
        List<CommentRecord> comments = commentService.listByWailId(id, currentPage);
        if (comments.isEmpty()) {
            Label l = new Label("Nincs meg kommentar! Legy te az elso aki megmondja a tutit!");
            l.addStyleNames(MaterialTheme.LABEL_LARGE, MaterialTheme.LABEL_COLORED);
            commentLayout.addComponent(l);
            commentLayout.setComponentAlignment(l, Alignment.MIDDLE_CENTER);
            commentLayout.setWidth(100, Unit.PERCENTAGE);
        } else {
            for (CommentRecord comment : comments) {
                Panel commentPanel = new Panel();
                commentPanel.addStyleNames(MaterialTheme.PANEL_WELL);
                Label commenter = new Label("<b><font color=\"red\">" + comment.getUserId() + "</font></b> " +comment.getCreatedAt().toString()+ "-kor a kovetkezo okossaggal allt elo:", ContentMode.HTML);
                commenter.addStyleName(MaterialTheme.LABEL_SMALL);

                Label commentContent = new Label(comment.getContent());

                VerticalLayout layout = new VerticalLayout(commenter, commentContent);
                commentPanel.setContent(layout);
                commentLayout.addComponent(commentPanel);
            }
        }
    }
}
