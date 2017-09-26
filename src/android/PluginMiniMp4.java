package com.demo;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;
import android.widget.Toast;
import com.demo.MiniMp4Activity;
import android.content.Intent;


public class PluginMiniMp4 extends CordovaPlugin {
 private CallbackContext context;
    public boolean execute(String action, JSONArray args,final CallbackContext callbackContext) {
         this.context=callbackContext;
        if (action.equals("showmsg")) {
           JSONObject obj = args.optJSONObject(0);
           String limit = obj.optString("limit");
            Intent intent = new Intent(cordova.getActivity(),MiniMp4Activity.class);
		   intent.putExtra("limit", Integer.parseInt(limit));
		   this.cordova.startActivityForResult((CordovaPlugin) this, intent, 0);
           return true;
        }else{
        	 context.error("Invalid Action");
	         return false;
        }
    }
        

       public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case MiniMp4Activity.OK:
                if(data!=null){
                    context.success(data.getStringExtra("path"));
                    Toast.makeText(cordova.getActivity(), data.getStringExtra("path").toString(), Toast.LENGTH_SHORT).show();
                }
               break;
            default:
                context.success("null");
                // Toast.makeText(this, "no mp4 data", Toast.LENGTH_SHORT).show();
                break;
        }

    }
}