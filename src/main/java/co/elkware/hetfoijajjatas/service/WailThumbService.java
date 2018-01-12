package co.elkware.hetfoijajjatas.service;

import co.elkware.hetfoijajjatas.db.generated.tables.WailThumbs;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WailThumbService {

    @Autowired
    private DSLContext dslContext;

    public void insertFingerprintIfNotExists(String fingerprint) {
        if (dslContext.select(WailThumbs.WAIL_THUMBS.BROWSER_FINGERPRINT).from(WailThumbs.WAIL_THUMBS).where(WailThumbs.WAIL_THUMBS.BROWSER_FINGERPRINT.equal(fingerprint)).execute() == 0) {
            dslContext.insertInto(WailThumbs.WAIL_THUMBS, WailThumbs.WAIL_THUMBS.BROWSER_FINGERPRINT).values(fingerprint).execute();
        }
    }

    public boolean hasThumbed(String fingerprint, Integer wailId) {
        Optional<Record1<String>> res = dslContext.select(WailThumbs.WAIL_THUMBS.WAIL_IDS)
                .from(WailThumbs.WAIL_THUMBS)
                .where(WailThumbs.WAIL_THUMBS.BROWSER_FINGERPRINT.equal(fingerprint))
                .stream().findFirst();
        if (!res.isPresent()) {
            return false;
        }
        String val = res.get().value1();
        if (val == null || "".equals(val.trim())) {
            return false;
        }
        return Arrays.stream(val.split(",")).map(Integer::valueOf).collect(Collectors.toSet()).contains(wailId);
    }

    public void addThumb(String fingerprint, Integer wailId) {
        insertFingerprintIfNotExists(fingerprint);
        Optional<Record1<String>> res = dslContext.select(WailThumbs.WAIL_THUMBS.WAIL_IDS)
                .from(WailThumbs.WAIL_THUMBS)
                .where(WailThumbs.WAIL_THUMBS.BROWSER_FINGERPRINT.equal(fingerprint))
                .stream().findFirst();
        res.ifPresent(record1 -> {
            String thumbsStr = record1.value1();
            final Set<Integer> thumbsSet;
            if (thumbsStr == null || "".equals(thumbsStr.trim())) {
                thumbsSet = new HashSet<>();
            } else {
                thumbsSet = Arrays.stream(thumbsStr.split(",")).map(Integer::valueOf).collect(Collectors.toSet());
            }
            if (!thumbsSet.contains(wailId)) {
                thumbsSet.add(wailId);
                thumbsStr = String.join(",", thumbsSet.stream().map(Object::toString).collect(Collectors.toSet()));
                dslContext.update(WailThumbs.WAIL_THUMBS).set(WailThumbs.WAIL_THUMBS.WAIL_IDS, thumbsStr).where(WailThumbs.WAIL_THUMBS.BROWSER_FINGERPRINT.equal(fingerprint)).execute();
            }
        });

    }
}
