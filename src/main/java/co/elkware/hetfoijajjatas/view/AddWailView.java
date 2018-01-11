package co.elkware.hetfoijajjatas.view;

import co.elkware.hetfoijajjatas.service.WailService;
import com.github.appreciated.material.MaterialTheme;
import com.vaadin.navigator.View;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.wcs.wcslib.vaadin.widget.recaptcha.ReCaptcha;
import com.wcs.wcslib.vaadin.widget.recaptcha.shared.ReCaptchaOptions;
import org.springframework.beans.factory.annotation.Autowired;

@SpringView(name = AddWailView.NAME)
public class AddWailView extends CustomComponent implements View {
    public static final String NAME = "add-wail";

    @Autowired
    private WailService wailService;

    @Autowired
    public AddWailView() {
        final String secret = System.getenv("RECAPTCHA_SECRET");
        final String siteKey = System.getenv("RECAPTCHA_SITE_KEY");
        ReCaptcha captcha = new ReCaptcha(secret,
                new ReCaptchaOptions() {{
                    theme = "light";
                    sitekey = siteKey;
                }}
        );
        //CheckBox robotCb = new CheckBox("Nem vagyok robot!");
        TextField username = new TextField("Álnevem:");
        username.setWidth(100, Unit.PERCENTAGE);
        username.setRequiredIndicatorVisible(true);
        TextArea wail = new TextArea("Jajgatásom:");
        wail.setRequiredIndicatorVisible(true);
        wail.setWidth(100, Unit.PERCENTAGE);
        TextField linkField = new TextField("Kiegeszítő hivatkozás a jajgatásomhoz:");
        linkField.setWidth(100, Unit.PERCENTAGE);
        Label hintLabel = new Label("Ha ugyanazt az álnevet használod, akkor a jajgatórendszer csoportosítja jajgatásaid az álneved alapján. Jó fícsör, mi?");
        hintLabel.addStyleName(MaterialTheme.LABEL_TINY);
        Button sendBtn = new Button("Jaj!");
        sendBtn.addStyleNames(MaterialTheme.BUTTON_ROUND, MaterialTheme.BUTTON_PRIMARY);
        sendBtn.addClickListener(clickEvent -> {
            if (!captcha.validate()) {
                Notification.show("Robot vagy mégis, nem jajgató!", Notification.Type.ERROR_MESSAGE);
                captcha.reload();
                return;
            }
            /*if (!robotCb.getValue()) {
                Notification.show("Robot vagy mégis, nem jajgató!", Notification.Type.ERROR_MESSAGE);
                robotCb.setValue(Boolean.FALSE);
                return;
            }*/
            if (username.getValue() == null || "".equals(username.getValue().trim())) {
                Notification.show("Álnév nélkül nem lehet jajgatni!", Notification.Type.ERROR_MESSAGE);
                captcha.reload();
                //robotCb.setValue(Boolean.FALSE);
                return;
            }
            if (wail.getValue() == null || "".equals(wail.getValue().trim())) {
                Notification.show("Jajgatni kötelező, mi az értelme az egésznek jajgatás nélkül?!", Notification.Type.ERROR_MESSAGE);
                captcha.reload();
                //robotCb.setValue(Boolean.FALSE);
                return;
            }
            wailService.add(username.getValue(), wail.getValue(), "".equals(linkField.getValue()) ? null : linkField.getValue());
            UI.getCurrent().getNavigator().navigateTo(WailView.NAME);
            Notification.show("Mélyen tisztelt jajgató, jajgatásod meghallgatásra talált!", Notification.Type.TRAY_NOTIFICATION);
        });
        FormLayout layout = new FormLayout(username, hintLabel, wail, linkField, captcha, /*robotCb,*/ sendBtn);
        layout.setMargin(true);
        VerticalLayout baseLayout = new VerticalLayout(new Panel("Jajgatok", layout));

        Label ytLbl = new Label("<iframe width=\"560\" height=\"315\" src=\"https://www.youtube.com/embed/Rhu4BNs77Tc?rel=0&amp;start=61&amp;autoplay=0\" frameborder=\"0\" allow=\"autoplay; encrypted-media\" allowfullscreen></iframe>", ContentMode.HTML);
        ytLbl.addStyleNames(MaterialTheme.LABEL_BORDERLESS, MaterialTheme.LABEL_NO_MARGIN);
        baseLayout.addComponent(ytLbl);
        baseLayout.setComponentAlignment(ytLbl, Alignment.TOP_CENTER);

        setCompositionRoot(baseLayout);

    }
}
