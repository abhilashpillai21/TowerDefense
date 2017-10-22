/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author ASUS1
 */
public class CreepControl extends AbstractControl {
    //Any local variables should be encapsulated by getters/setters so they
    //appear in the SDK properties window and can be edited.
    //Right-click a local variable to encapsulate it with getters and setters.
    
    GameAppState appState;
    protected int health=5;
    protected static int counter=0;
    
    CreepControl(GameAppState appState){
        this.appState=appState;
    }
    
    public static int getCounter()
    {
        return counter;
    }
            
    @Override
    protected void controlUpdate(float tpf) {
        //TODO: add code that controls Spatial,
        //e.g. spatial.rotate(tpf,tpf,tpf);
        if(getHealth()>0&&spatial.getLocalTranslation().getZ()>appState.PLAYER_LOCATION_Z)
            spatial.move(new Vector3f(0, 0, -tpf*1.5f));
        if(getHealth()>0&&spatial.getLocalTranslation().getZ()<appState.PLAYER_LOCATION_Z){
            appState.reduceHealth();
            spatial.removeFromParent();
        }
        if(getHealth()<=0){
            appState.addBudget();
            spatial.removeFromParent();
            //System.out.println(spatial.getName()+ " killed");
            counter++;
        }
    }
    
    protected String getIndex(){
        return spatial.getUserData("index");
    }    
    
    protected int getHealth(){
        return health;
    }
    
    protected void setHealth(int health){
       this.health=health;
    }
    
 
    @Override
    public void controlRender(RenderManager rm, ViewPort vp){
     
    }       
}
