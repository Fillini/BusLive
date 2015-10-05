package fill.com.buslive.http;

import fill.com.buslive.http.pojo.AbstractPOJO;

/**
 * Created by Fill on 11.12.2014.
 */
public interface ResponseCallback {


    public void onSucces(AbstractPOJO response);
    public void onFailure(String message);

}
