package com.zhizulx.tt.DB.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.zhizulx.tt.DB.entity.SessionEntity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table Session.
*/
public class SessionDao extends AbstractDao<SessionEntity, Long> {

    public static final String TABLENAME = "Session";

    /**
     * Properties of entity SessionEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property SessionKey = new Property(1, String.class, "sessionKey", false, "SESSION_KEY");
        public final static Property PeerId = new Property(2, int.class, "peerId", false, "PEER_ID");
        public final static Property PeerType = new Property(3, int.class, "peerType", false, "PEER_TYPE");
        public final static Property LatestMsgType = new Property(4, int.class, "latestMsgType", false, "LATEST_MSG_TYPE");
        public final static Property LatestMsgId = new Property(5, int.class, "latestMsgId", false, "LATEST_MSG_ID");
        public final static Property LatestMsgData = new Property(6, String.class, "latestMsgData", false, "LATEST_MSG_DATA");
        public final static Property TalkId = new Property(7, int.class, "talkId", false, "TALK_ID");
        public final static Property Created = new Property(8, int.class, "created", false, "CREATED");
        public final static Property Updated = new Property(9, int.class, "updated", false, "UPDATED");
    };


    public SessionDao(DaoConfig config) {
        super(config);
    }
    
    public SessionDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'Session' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'SESSION_KEY' TEXT NOT NULL UNIQUE ," + // 1: sessionKey
                "'PEER_ID' INTEGER NOT NULL ," + // 2: peerId
                "'PEER_TYPE' INTEGER NOT NULL ," + // 3: peerType
                "'LATEST_MSG_TYPE' INTEGER NOT NULL ," + // 4: latestMsgType
                "'LATEST_MSG_ID' INTEGER NOT NULL ," + // 5: latestMsgId
                "'LATEST_MSG_DATA' TEXT NOT NULL ," + // 6: latestMsgData
                "'TALK_ID' INTEGER NOT NULL ," + // 7: talkId
                "'CREATED' INTEGER NOT NULL ," + // 8: created
                "'UPDATED' INTEGER NOT NULL );"); // 9: updated
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'Session'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, SessionEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getSessionKey());
        stmt.bindLong(3, entity.getPeerId());
        stmt.bindLong(4, entity.getPeerType());
        stmt.bindLong(5, entity.getLatestMsgType());
        stmt.bindLong(6, entity.getLatestMsgId());
        stmt.bindString(7, entity.getLatestMsgData());
        stmt.bindLong(8, entity.getTalkId());
        stmt.bindLong(9, entity.getCreated());
        stmt.bindLong(10, entity.getUpdated());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public SessionEntity readEntity(Cursor cursor, int offset) {
        SessionEntity entity = new SessionEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // sessionKey
            cursor.getInt(offset + 2), // peerId
            cursor.getInt(offset + 3), // peerType
            cursor.getInt(offset + 4), // latestMsgType
            cursor.getInt(offset + 5), // latestMsgId
            cursor.getString(offset + 6), // latestMsgData
            cursor.getInt(offset + 7), // talkId
            cursor.getInt(offset + 8), // created
            cursor.getInt(offset + 9) // updated
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, SessionEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setSessionKey(cursor.getString(offset + 1));
        entity.setPeerId(cursor.getInt(offset + 2));
        entity.setPeerType(cursor.getInt(offset + 3));
        entity.setLatestMsgType(cursor.getInt(offset + 4));
        entity.setLatestMsgId(cursor.getInt(offset + 5));
        entity.setLatestMsgData(cursor.getString(offset + 6));
        entity.setTalkId(cursor.getInt(offset + 7));
        entity.setCreated(cursor.getInt(offset + 8));
        entity.setUpdated(cursor.getInt(offset + 9));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(SessionEntity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(SessionEntity entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
