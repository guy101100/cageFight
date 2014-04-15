package co.nz.splashYay.cagefight.scenes;

import co.nz.splashYay.cagefight.R;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;
public class Menu extends Activity {
	 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // ((Button)findViewById(R.id.IPAddress)).setText("192.168.43.1");  
        setContentView(R.layout.activity_menu);
    } 
    public void serverOnClick(View view){
    	Intent intent = new Intent(this, co.nz.splashYay.cagefight.MainActivity.class);
    	intent.putExtra("server", true);
    	startActivity(intent);
    }
    public void clientOnClick(View view){
    	setContentView(R.layout.activity_client);
    }
    public void exitOnclick(View view){
    	android.os.Process.killProcess(android.os.Process.myPid());
    }
    public void submitOnClick(View view){
    	TextView tv = (TextView) findViewById(R.id.IPAddress);
    	Intent intent = new Intent(this, co.nz.splashYay.cagefight.MainActivity.class);
    	intent.putExtra("server", false);
    	intent.putExtra("ip", tv.getText().toString());
    	startActivity(intent);
    }
}
