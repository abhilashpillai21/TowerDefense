/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Line;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ASUS1
 */
public class TowerControl extends AbstractControl {
    //Any local variables should be encapsulated by getters/setters so they
    //appear in the SDK properties window and can be edited.
    //Right-click a local variable to encapsulate it with getters and setters.
    
    private GameAppState appstate;
    private List<Charge> chargeArray=new ArrayList<>();
    private List<CreepControl> reachable=new ArrayList<>();
    private int counter=0;
    
    private long currentTime=System.currentTimeMillis();
    private int interval=650;
    private long nextTime=currentTime+interval;
    
    private Geometry fireLine;
    
    private boolean fired;
            
    TowerControl(GameAppState appstate){
        this.appstate=appstate;
    }
    
    @Override
    public void setSpatial(Spatial spatial){
        super.setSpatial(spatial);
        
        for(int i=0;i<(int)((Geometry)spatial).getUserData("chargesNum");i++)
            chargeArray.add(new Charge());
    }

    @Override
    protected void controlUpdate(float tpf) {
        //TODO: add code that controls Spatial,
        //e.g. spatial.rotate(tpf,tpf,tpf);
        
        if(availableCharges()>0){
            for(int i=0;i<appstate.creepNode.getQuantity();i++){
                float distance=spatial.getLocalTranslation().distance(appstate.creepNode.getChild(i).getLocalTranslation());
                CreepControl temp=appstate.creepNode.getChildren().get(i).getControl(CreepControl.class);
                if(distance<19 && reachable.indexOf(temp)==-1)
                    reachable.add(temp);
            }
            
            for (int i=0;i<reachable.size();i++) {
                CreepControl temp=reachable.get(i);
                if(temp.getSpatial().getParent()==null)
                    reachable.remove(temp);
            }
            
                
            if(reachable.size()>0 && System.currentTimeMillis()>nextTime){
                fireBulletAtCreeps();
                fired=true;
                nextTime=System.currentTimeMillis()+interval;
            }   
        }
        
        if(fired && System.currentTimeMillis()>nextTime-500){
            fireLine.removeFromParent();
            fired=false;
        }
            
        for(int i=0;i<chargeArray.size();i++){
            if(chargeArray.get(i).bulletCount<=0)
                chargeArray.remove(chargeArray.get(i));
        }
    }
    
    public void fireBulletAtCreeps(){
        chargeArray.get(0).fireBullet();
        CreepControl creepControl=reachable.get(0);
        Line line=new Line(spatial.getLocalTranslation(), creepControl.getSpatial().getLocalTranslation());
        fireLine=new Geometry("LineOfFire", line);
        Material fireLineMat=new Material(appstate.assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        fireLineMat.setColor("Color", (ColorRGBA)spatial.getUserData("index"));
        fireLine.setMaterial(fireLineMat);
 
        appstate.beamNode.attachChild(fireLine);
        creepControl.setHealth(creepControl.getHealth()-chargeArray.get(0).getDamageValue());
        //System.out.println(spatial.getName()+" : "+(++counter)+" fires shot at "+ creepControl.getSpatial().getName()+" Creep Health: "+creepControl.getHealth());
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    } 
    
    protected ColorRGBA getColor(){
        return ((Geometry)spatial).getUserData("index");
    }
    
    protected int getChargesNum(){
        return ((Geometry)spatial).getUserData("chargesNum");
    }
    
    protected int availableCharges(){
        return chargeArray.size();
    }
    
    protected void addCharge()
    {
        chargeArray.add(new Charge());
    }
    
    //method to return height of the spatial
    /*protected float getHeight(){
        
    }*/
}