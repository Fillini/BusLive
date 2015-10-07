package fill.com.buslive.utils;

import android.util.Log;

/**
 * Logging
 * Usage:
 *      L.trace(object);
 */
public class L {

    public final static boolean DEBUG = true;
    static final String TAG = L.class.getPackage().toString();

    public final static int DEB = 0;
    public final static int ERR = 1;

    public static void d(String message){

        if(DEBUG){
            int index = 0;
            for(int i=0; i<Thread.currentThread().getStackTrace().length; i++){
                if(Thread.currentThread().getStackTrace()[i].getClassName().equals(L.class.getName())){
                    index = i+1;
                }
            }
            String fullClassName = Thread.currentThread().getStackTrace()[index].getClassName();
            String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
            String methodName = Thread.currentThread().getStackTrace()[index].getMethodName();
            int lineNumber = Thread.currentThread().getStackTrace()[index].getLineNumber();
            Log.d(TAG, className + "." + methodName + "(" + className + ".java:" + lineNumber + ") :" + message);

        }

    }
    public static void d(Object obj){
        d("" + obj);
    }

    public static void e(String message){
        if(DEBUG){

            int index = 0;
            for(int i=0; i<Thread.currentThread().getStackTrace().length; i++){

                if(Thread.currentThread().getStackTrace()[i].getClassName().equals(L.class.getName())){
                    index = i+1;
                }
            }
            String fullClassName = Thread.currentThread().getStackTrace()[index].getClassName();
            String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
            String methodName = Thread.currentThread().getStackTrace()[index].getMethodName();
            int lineNumber = Thread.currentThread().getStackTrace()[index].getLineNumber();
            Log.e(TAG, className + "." + methodName + "(" + className + ".java:" + lineNumber + ") :" + message);

        }
    }
    public static void e(Object obj){
        e(""+obj);
    }


    public static void trace(Object object){
        d(object);
    }
    public static void trace(String message){
        d(message);
    }

    public static void trace(Object object, int type){
        switch (type){
            case DEB:
                d(object);
                break;
            case ERR:
                e(object);
        }
    }

    public static void trace(String message, int type){
        switch (type){
            case DEB:
                d(message);
                break;
            case ERR:
                e(message);
        }
    }

}
