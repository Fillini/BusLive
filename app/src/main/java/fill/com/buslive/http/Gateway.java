package fill.com.buslive.http;

import android.content.Context;
import android.preference.PreferenceActivity;

import com.google.gson.JsonParseException;



import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;

import fill.com.buslive.R;
import fill.com.buslive.http.pojo.AbstractPOJO;
import fill.com.buslive.utils.L;

public abstract class Gateway {

    public Context context;
    public ResponseCallback callback;


    public String serviceUrl;

    protected Gateway(Context context, ResponseCallback callback) {
        this.context = context;
        this.callback = callback;

    }

    protected void send(String prefix, Object[] args, final Class type){

        String format = MessageFormat.format(prefix, args);
        String request = serviceUrl+format;

        L.trace(request);

        AbstractPOJO bean = null;
        InputStream is=null;
        try{
            URL url = new URL(request);
            HttpURLConnection conn = ((HttpURLConnection) url.openConnection());
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "close");
            conn.setDoInput(true);

            conn.connect();

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int ret = 0;
            while ((ret = is.read(buf)) > 0) {
                os.write(buf, 0, ret);
            }
            String responseBody = new String(os.toByteArray());

            bean = (AbstractPOJO) new Deserializer()
                    .getGson()
                    .fromJson(responseBody, type);
            if(callback!=null){
                callback.onSucces(bean);
            }

            L.trace(bean);

        }catch (JsonParseException e){
            e.printStackTrace();
        }catch (Exception e){

            e.printStackTrace();

            if(callback!=null){
                callback.onFailure(context.getResources().getString(R.string.response_failure_message));
            }

        }finally {
            if(is!=null){
                try{is.close();}catch (Exception e){ e.printStackTrace();}
            }
        }

    }


    protected void send(String prefix, String args, final Class type){
        send(prefix, new String[]{args}, type);
    }


}
