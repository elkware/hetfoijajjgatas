package co.elkware.hetfoijajjatas.service;

import co.elkware.hetfoijajjatas.db.generated.tables.Wail;
import co.elkware.hetfoijajjatas.db.generated.tables.records.WailRecord;
import co.elkware.hetfoijajjatas.util.Utils;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WailService {

    @Autowired
    private UserService userService;

    public List<WailRecord> list(int offset) {
        try (Connection connection = Utils.getConnection()) {
            DSLContext dslContext = DSL.using(connection, SQLDialect.POSTGRES);
            List<WailRecord> wailRecords = dslContext
                    .select(Wail.WAIL.ID, Wail.WAIL.CONTENT, Wail.WAIL.LINK, Wail.WAIL.USER_ID, Wail.WAIL.CREATED_AT, Wail.WAIL.THUMBS_UP, Wail.WAIL.THUMBS_DOWN)
                    .from(Wail.WAIL)
                    .orderBy(Wail.WAIL.CREATED_AT.desc())
                    .limit(Utils.PAGE_SIZE)
                    .offset(offset)
                    .stream()
                    .map(res -> new WailRecord(res.value1(), res.value2(), res.value3(), res.value4(), res.value5(), res.value6(), res.value7()))
                    .collect(Collectors.toList());
            dslContext.close();
            return wailRecords;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public boolean add(String username, String content, String url) {
        userService.addIfNotExists(username);
        try (Connection connection = Utils.getConnection()) {
            DSLContext dslContext = DSL.using(connection, SQLDialect.POSTGRES);
            boolean added = dslContext
                    .insertInto(Wail.WAIL, Wail.WAIL.CONTENT, Wail.WAIL.LINK, Wail.WAIL.CREATED_AT, Wail.WAIL.USER_ID)
                    .values(content, url, new Timestamp(new Date().getTime()), username)
                    .execute() != 0;
            dslContext.close();
            return added;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int thumbUp(Integer id) {
        try (Connection connection = Utils.getConnection()) {
            DSLContext dslContext = DSL.using(connection, SQLDialect.POSTGRES);
            int thumbs = dslContext.select(Wail.WAIL.THUMBS_UP).from(Wail.WAIL).where(Wail.WAIL.ID.eq(id)).fetch().get(0).value1() + 1;
            dslContext.update(Wail.WAIL).set(Wail.WAIL.THUMBS_UP, thumbs).where(Wail.WAIL.ID.eq(id)).execute();
            dslContext.close();
            return thumbs;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int thumbDown(Integer id) {
        try (Connection connection = Utils.getConnection()) {
            DSLContext dslContext = DSL.using(connection, SQLDialect.POSTGRES);
            int thumbs = dslContext.select(Wail.WAIL.THUMBS_DOWN).from(Wail.WAIL).where(Wail.WAIL.ID.eq(id)).fetch().get(0).value1() - 1;
            dslContext.update(Wail.WAIL).set(Wail.WAIL.THUMBS_DOWN, thumbs).where(Wail.WAIL.ID.eq(id)).execute();
            dslContext.close();
            return thumbs;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public Optional<WailRecord> findOne(Integer id) {
        try (Connection connection = Utils.getConnection()) {
            DSLContext dslContext = DSL.using(connection, SQLDialect.POSTGRES);
            Optional<WailRecord> wailRecord = dslContext.select(Wail.WAIL.ID, Wail.WAIL.CONTENT, Wail.WAIL.LINK, Wail.WAIL.USER_ID, Wail.WAIL.CREATED_AT, Wail.WAIL.THUMBS_UP, Wail.WAIL.THUMBS_DOWN)
                    .from(Wail.WAIL).where(Wail.WAIL.ID.equal(id))
                    .stream()
                    .map(res -> new WailRecord(res.value1(), res.value2(), res.value3(), res.value4(), res.value5(), res.value6(), res.value7()))
                    .findFirst();
            dslContext.close();
            return wailRecord;
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public int count() {
        try (Connection connection = Utils.getConnection()) {
            DSLContext dslContext = DSL.using(connection, SQLDialect.POSTGRES);
            int cnt = dslContext.selectCount().from(Wail.WAIL).fetchOne(0, int.class);
            dslContext.close();
            return cnt;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

}
