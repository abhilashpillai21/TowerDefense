package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    public static void main(String[] args) {
        AppSettings settings=new AppSettings(true);
        settings.setSettingsDialogImage("Images/tower-07.jpg");
        settings.setFullscreen(true);
        Main app = new Main();
        //app.setDisplayFps(false);
        //app.setDisplayStatView(false);
        
        app.setSettings(settings);
        app.start();

    }

    @Override
    public void simpleInitApp() {
        cam.setLocation(new Vector3f(0, 10, 50));
        flyCam.setDragToRotate(true);
        inputManager.setCursorVisible(true);
        flyCam.setMoveSpeed(10);
        GameAppState appState=new GameAppState(rootNode);
        stateManager.attach(appState);
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
