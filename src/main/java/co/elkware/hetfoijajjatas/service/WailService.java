package co.elkware.hetfoijajjatas.service;

import co.elkware.hetfoijajjatas.db.generated.tables.User;
import co.elkware.hetfoijajjatas.db.generated.tables.Wail;
import co.elkware.hetfoijajjatas.db.generated.tables.records.UserRecord;
import co.elkware.hetfoijajjatas.db.generated.tables.records.WailRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WailService {

    public static final int PAGE_SIZE = 10;

    @Autowired
    private DSLContext dslContext;

    public List<WailRecord> getWails(int offset) {
        return dslContext
                .select(Wail.WAIL.ID, Wail.WAIL.CONTENT, Wail.WAIL.LINK, Wail.WAIL.USER_ID, Wail.WAIL.CREATED_AT, Wail.WAIL.THUMBS_UP, Wail.WAIL.THUMBS_DOWN)
                .from(Wail.WAIL)
                .orderBy(Wail.WAIL.CREATED_AT.desc())
                .limit(PAGE_SIZE)
                .offset(offset)
                .stream()
                .map(res -> new WailRecord(res.value1(), res.value2(), res.value3(), res.value4(), res.value5(), res.value6(), res.value7()))
                .collect(Collectors.toList());
    }

    public boolean addWail(String username, String content, String url) {
        if(dslContext
                .select(User.USER.USERNAME)
                .from(User.USER)
                .where(User.USER.USERNAME.equal(username))
                .execute() == 0) {
            dslContext.insertInto(User.USER, User.USER.USERNAME, User.USER.CREATED_AT).values(username, new Timestamp(new Date().getTime())).execute();
        }

        return dslContext
                .insertInto(Wail.WAIL, Wail.WAIL.CONTENT, Wail.WAIL.LINK, Wail.WAIL.CREATED_AT, Wail.WAIL.USER_ID)
                .values(content, url, new Timestamp(new Date().getTime()), username)
                .execute() != 0;
    }

    public int thumbUp(Integer id) {
        int thumbs = dslContext.select(Wail.WAIL.THUMBS_UP).from(Wail.WAIL).where(Wail.WAIL.ID.eq(id)).fetch().get(0).value1() + 1;
        dslContext.update(Wail.WAIL).set(Wail.WAIL.THUMBS_UP, thumbs).where(Wail.WAIL.ID.eq(id)).execute();
        return thumbs;
    }

    public int thumbDown(Integer id) {
        int thumbs = dslContext.select(Wail.WAIL.THUMBS_DOWN).from(Wail.WAIL).where(Wail.WAIL.ID.eq(id)).fetch().get(0).value1() - 1;
        dslContext.update(Wail.WAIL).set(Wail.WAIL.THUMBS_DOWN, thumbs).where(Wail.WAIL.ID.eq(id)).execute();
        return thumbs;
    }

    public int getWailCount() {
        return dslContext.select(Wail.WAIL.ID).from(Wail.WAIL).execute();
    }

}
