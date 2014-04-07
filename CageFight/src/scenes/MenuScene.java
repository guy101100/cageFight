package scenes;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.ui.activity.BaseGameActivity;

import co.nz.splashYay.cagefight.Player;
import co.nz.splashYay.cagefight.SceneManager;
import co.nz.splashYay.cagefight.SceneManager.AllScenes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.widget.EditText;

public class MenuScene extends Scene {

	private BaseGameActivity activity;	
	private SceneManager sceneManager;

	public MenuScene(BaseGameActivity act, SceneManager sceneManager) {
		this.activity = act;
		this.sceneManager = sceneManager;

	}

	public void loadRes() {

	}

	public Scene createScene() {

		this.setBackground(new Background(0, 0, 0));

		Handler mHandler = new Handler(Looper.getMainLooper());

		mHandler.post(new Runnable() {
			public void run() {
				startServerOrClient(activity);
			}
		});

		return this;

	}
	
	public void startServerOrClient(Context context) {

		final AlertDialog.Builder alert = new AlertDialog.Builder(context);
		final EditText input = new EditText(context);

		alert.setView(input);
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String string = input.getText().toString().trim();
				if (string.equalsIgnoreCase("1")) {
					sceneManager.loadServerGameRes();
					sceneManager.createServerGameScene();
					sceneManager.setCurrentScene(AllScenes.GAME_SERVER);
				} else {
					getServerIp(activity);
				}
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});
		alert.show();
	}

	//really bad temporary input for server ip and player id methods
	public void getServerIp(Context context) {

		final AlertDialog.Builder alert = new AlertDialog.Builder(context);
		final EditText input = new EditText(context);

		alert.setView(input);
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				sceneManager.setIpaddress(input.getText().toString().trim());				
				getPlayerID(activity);
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});
		alert.show();
	}
	
	//gets id creates a player, starts game scene, needs to change
	public void getPlayerID(Context context) {

		final AlertDialog.Builder alert = new AlertDialog.Builder(context);
		final EditText input = new EditText(context);

		alert.setView(input);
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String id = input.getText().toString().trim();
				int idInt = Integer.parseInt(id);
				sceneManager.createPlayerAndStartGame(idInt);
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});
		alert.show();
	}

}
