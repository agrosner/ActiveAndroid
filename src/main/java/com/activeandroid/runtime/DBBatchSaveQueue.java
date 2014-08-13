package com.activeandroid.runtime;

import android.os.Looper;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.IModel;
import com.activeandroid.manager.SingleDBManager;
import com.activeandroid.util.AALog;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by andrewgrosner
 * Date: 3/19/14
 * Contributors:
 * Description: This queue will bulk save items added to it when it gets access to the DB. It should only exist as one entity.
 */
public class DBBatchSaveQueue extends Thread{

    private static DBBatchSaveQueue mBatchSaveQueue;

    /**
     *  Once the queue size reaches 50 or larger, the thread will be interrupted and we will batch save the models.
     */
    private static final int sMODEL_SAVE_SIZE = 50;

    /**
     * Tells how many items to save at a time. This can be set using {@link #setModelSaveSize(int)}
     */
    private int mModelSaveSize = sMODEL_SAVE_SIZE;

    private boolean mQuit = false;

    public static DBBatchSaveQueue getSharedSaveQueue(){
        if(mBatchSaveQueue==null){
            mBatchSaveQueue = new DBBatchSaveQueue();
        }
        return mBatchSaveQueue;
    }

    public static void disposeSharedQueue(){
        mBatchSaveQueue = null;
    }

    private final ArrayList<IModel> mModels;

    public DBBatchSaveQueue(){
        super("DBBatchSaveQueue");

        mModels = new ArrayList<IModel>();
    }

    /**
     * Sets how many models to save at a time in this queue.
     * Increase it for larger batches, but slower recovery time.
     * Smaller the batch, the more time it takes to save overall.
     * @param mModelSaveSize
     */
    public void setModelSaveSize(int mModelSaveSize) {
        this.mModelSaveSize = mModelSaveSize;
    }

    @Override
    public void run() {
        super.run();
        Looper.prepare();
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        while (true){
            final ArrayList<IModel> tmpModels;
            synchronized (mModels){
                tmpModels = new ArrayList<IModel>(mModels);
                mModels.clear();
            }
            if(tmpModels.size()>0) {
                //run this on the DBManager thread
                SingleDBManager.getSharedInstance().getQueue().add(new DBRequest(DBRequestInfo.create("Batch Saving")) {
                    @Override
                    public void run() {
                        long time = System.currentTimeMillis();
                        ActiveAndroid.beginTransaction();
                        try {
                            AALog.d("DBBatchSaveQueue", "Executing batch save of: " + tmpModels.size() + " on :" + Thread.currentThread().getName());
                            for (IModel IModel: tmpModels) {
                                IModel.save();
                            }
                            ActiveAndroid.setTransactionSuccessful();
                        } catch (Throwable e) {
                            throw new RuntimeException(e.getCause());
                        } finally {
                            ActiveAndroid.endTransaction();
                        }
                        AALog.d("DBBatchSaveQueue", "Time took: " + (System.currentTimeMillis() -time));
                    }
                });
            }

            try {
                //sleep for 5 mins, and then check for leftovers
                Thread.sleep(300000);
            } catch (InterruptedException e) {
                AALog.d("DBBatchSaveQueue", "Batch interrupted to start saving");
            }

            if(mQuit){
                return;
            }
        }
    }

    public void add(final IModel IModel){
        synchronized (mModels){
            mModels.add(IModel);

            if(mModels.size()>mModelSaveSize){
                interrupt();
            }
        }
    }

    public <COLLECTION_CLASS extends Collection<OBJECT_CLASS>, OBJECT_CLASS extends IModel> void addAll(final COLLECTION_CLASS list){
        synchronized (mModels){
            mModels.addAll(list);

            if(mModels.size()>mModelSaveSize){
                interrupt();
            }
        }
    }

    public void remove(final IModel IModel){
        synchronized (mModels){
            mModels.remove(IModel);
        }
    }

    public void removeAll(final Collection collection){
        synchronized (mModels){
            mModels.removeAll(collection);
        }
    }

    public void quit() {
        mQuit = true;
    }
}
