package mvpdemo.hd.greendao;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.LinearLayout;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mvpdemo.hd.greendao.entity.Groups;
import mvpdemo.hd.greendao.entity.Setting;
import mvpdemo.hd.greendao.entity.Word;

import static android.provider.UserDictionary.Words.WORD;

public class DbInit {
    private static final String DEFAULT_GROUP_NAME = "默认生词本";
    private static final String DB_NAME = "word1";
    private static final String DB_PASSWORD = "psw1";
    private static DaoSession daoSession;

    public static void saveWord(Word w) {
        daoSession.getWordDao().insertOrReplace(w);
    }

    public static void saveWord(List<Word> word) {
        daoSession.getWordDao().insertOrReplaceInTx(word);
    }

    public static List<Word> isExistWord(String word) {
        QueryBuilder qb = daoSession.getWordDao().queryBuilder();
        qb.where(WordDao.Properties.Spell.eq(word));
        return qb.list();
    }

    public static void deleteWord(List<Word> word) {
        daoSession.getWordDao().deleteInTx(word);
    }

    public static void deleteWord(Word word) {
        daoSession.getWordDao().delete(word);
    }

    public static List<Word> getWordsByGroupId(List<Long> groups) {
        QueryBuilder qb = daoSession.getWordDao().queryBuilder();
//        List<Word> list = qb.orderDesc(WordDao.Properties.Id).list();
//        Iterator<Word> iterator = list.listIterator();
//        for (Long gid : groups) {
//            if (gid < 0) {
//                continue;
//            }
//            while (iterator.hasNext()) {
//                Word w = iterator.next();
//                if (!w.getGroupIds().contains(gid)) {
//                    iterator.remove();
//                }
//            }
//        }
//        return list;
        ArrayList<WhereCondition> l = new ArrayList<>();
        for (Long gid : groups) {
            if (gid < 0) {
                continue;
            }
            qb.where(WordDao.Properties.GroupIds.like("%"+gid+"%"));
        }

        return qb.list();
    }

    public static List<Word> getWordsByGroupId(Long group) {
        List<Long> list = new ArrayList<>();
        list.add(group);
        return getWordsByGroupId(list);
    }

    public static List<Word> ambiguousSearch(String word) {
        QueryBuilder qb = daoSession.getWordDao().queryBuilder();
//        qb.where(WordDao.Properties.Spell.like("%" + word + "%"));
        qb.where(WordDao.Properties.Spell.like("%" + word + "%")).orderDesc(WordDao.Properties.Id);
        return qb.list();
    }

    public static List<Word> vagueSearch(String word) {
//        String SQL_DISTINCT_ENAME = "SELECT " + WordDao.Properties.Spell.columnName + " FROM " + WordDao.TABLENAME + " WHERE " + WordDao.Properties.Spell.columnName + " LIKE %" + word + "%";
//        String SQL_DISTINCT_ENAME = "SELECT *" + " FROM " + WordDao.TABLENAME + " LIKE " + word;
        String SQL_DISTINCT_ENAME = "SELECT * FROM WORD WHERE SPELL LIKE ? OR SPELL LIKE ?";
        ArrayList<Word> result = new ArrayList();
        Log.e("XXX", " vagueSearch: " + SQL_DISTINCT_ENAME);

        Database db = daoSession.getWordDao().getDatabase();
        Cursor c = null;
        try {
            c = db.rawQuery(SQL_DISTINCT_ENAME, new String[]{"%s%","%o%"});
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (c.moveToFirst()) {
                do {
                    Word w = new Word();
                    w.setSpell(c.getString(WordDao.Properties.Spell.ordinal));
                    result.add(w);
                } while (c.moveToNext());
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return result;

//        Query query = daoSession.getWordDao().queryBuilder().where(new
//                WhereCondition.StringCondition(" * IN " + "(SELECT SPELL FROM WORD WHERE SPELL LIKE ?" + word + ")")).build();
//        return query.list();
//        return null;
    }

    public static void init(Context context) {
        daoSession = new DaoMaster(new OpenHelper(context, DB_NAME).getEncryptedWritableDb(DB_PASSWORD)).newSession();
//        for (int i = 0; i < 5; i++) {
//            getStudentDao().insertOrReplace(new Student(new Long(i), "Peter", "" + (21 + i), "1361186257888" + (i + 1), "" + (i + 100)));
//        }
        enableQueryBuilderLog();
    }

    private static class OpenHelper extends DaoMaster.OpenHelper {
        public OpenHelper(Context context, String name) {
            super(context, name);
        }
    }

    public static void enableQueryBuilderLog() {
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
    }

    public static Setting getSetting() {
        List<Setting> list = daoSession.getSettingDao().loadAll();
        Setting setting;
        if (list.size() == 0) {
            setting = new Setting();
        } else {
            setting = list.get(0);
        }
        return setting;
    }

    public static void saveSetting(Setting setting) {
        daoSession.getSettingDao().insertOrReplace(setting);
    }

    public static List<Word> getAllWords() {
        return daoSession.getWordDao().loadAll();
    }

    public static List<Word> getWordsByIds(List<Integer> ids) {
        List<Word> list = new ArrayList<>();
        for (int i : ids) {
            list.add(daoSession.getWordDao().load(Long.parseLong(i + "")));
        }
        return list;
    }

    public static List<Groups> getAllGroups() {
        List<Groups> list = daoSession.getGroupsDao().loadAll();
        boolean existDefault = false;
        for (Groups g : list) {
            if (DEFAULT_GROUP_NAME.equals(g.name)) {
                existDefault = true;
                break;
            }
        }

        if (!existDefault) {
            Groups g = new Groups();
            g.setName(DEFAULT_GROUP_NAME);
            saveGroups(g);
            list.add(g);
        }

        return list;
    }

    public static Groups getGroup(String name) {
        QueryBuilder qb = daoSession.getGroupsDao().queryBuilder();
        qb.where(GroupsDao.Properties.Name.eq(name));
        if (qb.list().size() > 0) {
            return (Groups) qb.list().get(0);
        } else {
            return null;
        }
    }

    public static void saveGroups(Groups groups) {
        daoSession.getGroupsDao().insertOrReplace(groups);
    }

    public static void saveGroups(List<Groups> groups) {
        daoSession.getGroupsDao().insertInTx(groups);
    }

    public static void deleteGroups(Groups groups) {
        daoSession.getGroupsDao().delete(groups);
    }

    public static void deleteGroups(List<Groups> groups) {
        daoSession.getGroupsDao().deleteInTx(groups);
    }
}
