package co.elkware.hetfoijajjatas.service;

import co.elkware.hetfoijajjatas.db.generated.tables.Comment;
import co.elkware.hetfoijajjatas.db.generated.tables.records.CommentRecord;
import co.elkware.hetfoijajjatas.util.Utils;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private UserService userService;

    public List<CommentRecord> listByWailId(Integer wailId, int offset) {
        try (Connection connection = Utils.getConnection()) {
            DSLContext dslContext = DSL.using(connection, SQLDialect.POSTGRES);
            Settings settings = new Settings();
            settings.setRenderSchema(Boolean.TRUE);
            List<CommentRecord> commentRecords = dslContext
                    .select(Comment.COMMENT.ID, Comment.COMMENT.CONTENT, Comment.COMMENT.USER_ID, Comment.COMMENT.WAIL_ID, Comment.COMMENT.CREATED_AT)
                    .from(Comment.COMMENT)
                    .where(Comment.COMMENT.WAIL_ID.equal(wailId))
                    .orderBy(Comment.COMMENT.CREATED_AT.desc())
                    .limit(Utils.PAGE_SIZE)
                    .offset(offset)
                    .stream()
                    .map(r -> new CommentRecord(r.value1(), r.value2(), r.value3(), r.value4(), r.value5()))
                    .collect(Collectors.toList());
            dslContext.close();
            return commentRecords;
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public void add(Integer wailId, String user, String content) {
        userService.addIfNotExists(user);
        try (Connection connection = Utils.getConnection()) {
            DSLContext dslContext = DSL.using(connection, SQLDialect.POSTGRES);
            dslContext.insertInto(Comment.COMMENT, Comment.COMMENT.CONTENT, Comment.COMMENT.WAIL_ID, Comment.COMMENT.USER_ID, Comment.COMMENT.CREATED_AT)
                    .values(content, wailId, user, new Timestamp(new Date().getTime())).execute();
            dslContext.close();
        } catch (SQLException e) {e.printStackTrace();}
    }


    public int countByWailId(Integer wailId) {
        try (Connection connection = Utils.getConnection()) {
            DSLContext dslContext = DSL.using(connection, SQLDialect.POSTGRES);
            int cnt = dslContext.selectCount().from(Comment.COMMENT).where(Comment.COMMENT.WAIL_ID.equal(wailId)).fetchOne(0, int.class);
            dslContext.close();
            return cnt;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

}
