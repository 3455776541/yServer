package org.yserver.task;

import org.yserver.TaskManeger;
import org.yserver.y;

import java.util.ArrayList;

public class TaskManagerImpl implements TaskManeger {
	private static TaskManeger instance;
	private static ArrayList<Runnable> mRunables = new ArrayList<Runnable>();

    /**
     * registerInstance:(注册实例). <br> 
     * 
     * @author ysj  
     * @since JDK 1.7
     * date: 2015-12-4 上午11:40:31 <br>
     */
    public static void registerInstance() {
        if (instance == null) {
            synchronized (TaskManeger.class) {
                if (instance == null) {
                    instance = new TaskManagerImpl();
                }
            }
        }
        y.Core.setTaskController(instance);
    }

    /**
     * run in background thread
     *
     * @param runnable
     */
    @Override
    public void run(Runnable runnable) {
    	mRunables.add(runnable);
    	if (!TaskProxy.defaultExecutor.isBusy()) {
            TaskProxy.defaultExecutor.execute(runnable);
        } else {
            new Thread(runnable).start();
        }
    }

    @Override
    public void removeCallbacks(Runnable runnable) {
    	TaskProxy.defaultExecutor.getThreadPoolExecutor().remove(runnable);
    }
    
    @Override
    public void shutdown() {
    	TaskProxy.defaultExecutor.getThreadPoolExecutor().shutdown();
    }
}
