package ci.function.Core.Database;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import java.util.List;

import ci.function.Core.CIApplication;

/**
 * Created by 1500242 on 2015/7/7.
 */
public class SDbHelper<Entry> {
    private RuntimeExceptionDao<Entry, Integer> dao            = null;
    private CIDatabaseManager                   ormLiteHelper  = null;
    private Class<Entry>                        entryClassType = null;

    public SDbHelper(Class<Entry> entryClassType) {
        ormLiteHelper = CIApplication.getDbManager();
        this.entryClassType = entryClassType;
    }

    /**
     * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our SContactData class. It will
     * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
     */
    public RuntimeExceptionDao<Entry, Integer> getExceptionDao() {
        if (dao == null) {
            dao = ormLiteHelper.getRuntimeExceptionDao(entryClassType);
        }
        return dao;
    }

    @SuppressWarnings("unchecked") // let it crash if
    public static void delete(RuntimeExceptionDao dao, List list){

        List deletelist = null;
        int count = 0;
        int block = 100;
        do {
            if(count+block<list.size()) {
                deletelist = list.subList(count, count+block);
                count+=block;
            }else {
                deletelist = list.subList(count, list.size());
                count=list.size();
            }
            dao.delete(deletelist);
        }while (count<list.size());

    }
}
