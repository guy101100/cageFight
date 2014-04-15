package co.nz.splashYay.cagefight.scenes;

import co.nz.splashYay.cagefight.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

	public class SplashScreen extends Activity{
		private static final int DELAY_TIME = 1500;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			setContentView(R.layout.acitvity_splash);
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					Intent intent = new Intent(SplashScreen.this,Menu.class);
					startActivity(intent);
					finish();
				}
			}, DELAY_TIME );
		}
	
}