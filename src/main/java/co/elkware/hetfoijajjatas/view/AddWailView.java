package co.elkware.hetfoijajjatas.view;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.wcs.wcslib.vaadin.widget.recaptcha.ReCaptcha;
import com.wcs.wcslib.vaadin.widget.recaptcha.shared.ReCaptchaOptions;

@SpringView(name = AddWailView.NAME)
public class AddWailView extends CustomComponent implements View {
    public static final String NAME = "add-wail";

    public AddWailView() {
        final String secret = System.getenv("RECAPTCHA_SECRET");
        final String siteKey = System.getenv("RECAPTCHA_SITE_KEY");
        ReCaptcha captcha = new ReCaptcha(secret,
                new ReCaptchaOptions() {{//your options
                    theme = "light";
                    sitekey = siteKey;
                }}
        );

        TextField username = new TextField("Jajgato");
        username.setRequiredIndicatorVisible(true);
        TextArea wail = new TextArea("Jajgatas");
        wail.setRequiredIndicatorVisible(true);
        Button sendBtn = new Button("Jaj!");
        FormLayout layout = new FormLayout(username, wail, captcha, sendBtn);

        setCompositionRoot(layout);

    }
}
