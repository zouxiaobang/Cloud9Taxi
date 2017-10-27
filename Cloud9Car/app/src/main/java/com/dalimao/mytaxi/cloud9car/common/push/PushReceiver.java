package com.dalimao.mytaxi.cloud9car.common.push;

/**
 * Created by zouxiaobang on 10/24/17.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dalimao.mytaxi.cloud9car.common.databus.RxBus;
import com.dalimao.mytaxi.cloud9car.common.http.biz.BaseBizResponse;
import com.dalimao.mytaxi.cloud9car.common.lbs.LocationInfo;
import com.dalimao.mytaxi.cloud9car.main.model.bean.Order;
import com.dalimao.mytaxi.cloud9car.main.model.response.OptStateResponse;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.push.PushConstants;

public class PushReceiver extends BroadcastReceiver {
    /**
     * 司机的位置
     */
    private static final int MSG_TYPE_LOCATION = 1;
    /**
     * 司机接单
     */
    private static final int MSG_TYPE_ORDER = 2;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
            String msg = intent.getStringExtra("msg");
            Log.d("bmob", "客户端收到推送内容：" + msg);
            // TODO: 17/6/1  通知业务或UI
            try {
                JSONObject jsonObject = new JSONObject(msg);
                int type = jsonObject.optInt("type");
                if (type == MSG_TYPE_LOCATION) {
                    // 位置变化
                    LocationInfo locationInfo =
                            new Gson().fromJson(jsonObject.optString("data"), LocationInfo.class);
                    RxBus.getInstance().send(locationInfo);

                } else if (type == MSG_TYPE_ORDER){
                    //司机接单
                    Order order = new Gson().fromJson(jsonObject.optString("data"), Order.class);
                    //解析数据
                    OptStateResponse response = new OptStateResponse();
                    response.setData(order);
                    response.setState(order.getState());
                    response.setCode(BaseBizResponse.STATE_OK);
                    //通知UI
                    RxBus.getInstance().send(response);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
