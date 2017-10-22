/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;


/**
 *
 * @author Abhilash Pillai
 */

public class GameAppState extends AbstractAppState {
    protected Application app;
    protected Node rootNode;
    protected AssetManager assetManager;
    protected Camera cam;
    protected AppStateManager stateManager;
    protected InputManager inputManager;
    protected ViewPort viewPort;
    private int creepIndex=1;
    protected int level, score, health=5, budget=5;
    protected long timer_budget, timer_beam;
    protected boolean lastGameWon;
    protected Node playerNode, towerNode, creepNode, beamNode;
    protected final float PLAYER_LOCATION_Z=-14.5f;
    protected long startTime=System.currentTimeMillis();
    private static Geometry selectedTower;
    private static final Trigger SELECT_TOWER=new MouseButtonTrigger(MouseInput.BUTTON_LEFT);
    private static final Trigger RECHARGE=new KeyTrigger(KeyInput.KEY_R);
    private static final String SELECT_MAPPING="Select Tower";
    private static final String RECHARGE_MAPPING="Recharge";
        
    GameAppState(Node rootNode){
        this.rootNode=rootNode;//accessing rootNode
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app=(SimpleApplication)app;
        this.assetManager=this.app.getAssetManager();
        this.stateManager=this.app.getStateManager();
        cam=this.app.getCamera();
        inputManager=this.app.getInputManager();
        viewPort=this.app.getViewPort();
        viewPort.setBackgroundColor(ColorRGBA.Cyan);
        
        //Player, Tower and CreepNodes
        playerNode=new Node("PlayerNode");
        towerNode=new Node("TowerNode");
        creepNode=new Node("CreepNode");
        beamNode=new Node("beamNode");
        
        //Create floor
        createFloor();
        
        //Create player
        Geometry player=createPlayer();
        playerNode.attachChild(player);
        
        //Create tower
        Geometry towerA=createTower(new Vector3f(18.75f, 10, -6.5f), ColorRGBA.Green, "Tower A");
        Geometry towerB=createTower(new Vector3f(-18.75f, 10, -6.5f), ColorRGBA.Red, "Tower B");
        
      //  System.out.println("Distance "+towerA.getLocalTranslation().distance(towerB.getLocalTranslation()));
        
        towerNode.attachChild(towerA);
        towerNode.attachChild(towerB);
        
        //Create creeps
        for(int i=0;i<15;i++){
             creepNode.attachChild(createCreep(new Vector3f(FastMath.nextRandomInt(-15, 15), 4, FastMath.nextRandomInt(10, 33))));
        }

        rootNode.attachChild(playerNode);
        rootNode.attachChild(towerNode);
        rootNode.attachChild(creepNode);
        rootNode.attachChild(beamNode);
        
        startTime=System.currentTimeMillis();
        
        inputManager.addMapping("Select Tower", SELECT_TOWER);
        inputManager.addMapping("Recharge", RECHARGE);
        
        inputManager.addListener(towerListener, "Select Tower");
        inputManager.addListener(rechargeListener, "Recharge");
    }
    
    
    ActionListener towerListener=new ActionListener(){
        @Override
        public void onAction(String name, boolean isPressed, float tpf){
            //System.out.println("Hi");
            CollisionResults results=new CollisionResults();
            Vector3f cursorPos=cam.getWorldCoordinates(inputManager.getCursorPosition(), 0);
            Vector3f endPos=cam.getWorldCoordinates(inputManager.getCursorPosition(), 1);
            Vector3f dir=endPos.subtract(cursorPos).normalize();
            Ray ray=new Ray(cursorPos, dir);
            towerNode.collideWith(ray, results);
            if(results.size()>0){
               selectedTower=results.getClosestCollision().getGeometry();
            }  
        }    
    };
    
    ActionListener rechargeListener=new ActionListener(){
        @Override
        public void onAction(String name, boolean isPressed, float tpf){
        
            if(selectedTower!=null){
                if(budget!=0){
                    TowerControl temp=selectedTower.getControl(TowerControl.class);
                    budget=-5;
                    if(budget<0)
                        budget=0;
                }
            }
        }    
    };
    
    //Create Creep method
    private Geometry createCreep(Vector3f loc)
    {
        Box creepMesh=new Box(0.5f, 2, 0.5f);
        Geometry creep=new Geometry("Creep "+creepIndex++, creepMesh);
        Material creepMat=new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        creepMat.setColor("Color", ColorRGBA.Black);
        creep.setMaterial(creepMat);
        creep.setLocalTranslation(loc);
        creep.setUserData("index", creep.getName());
        creep.setUserData("health", 5);
        creep.addControl(new CreepControl(this));
        return creep;
    }
        
    //Create tower method
    
    private Geometry createTower(Vector3f loc, ColorRGBA color, String name)
    {
        Box towerMesh=new Box(2, 8, 1);
        Geometry tower=new Geometry(name, towerMesh);
        Material towerMat=new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        towerMat.setColor("Color", color);
        tower.setMaterial(towerMat);
        tower.setLocalTranslation(loc);
        tower.setUserData("index", color);
        tower.setUserData("chargesNum", 5);
        tower.addControl(new TowerControl(this));
        return tower;
    }
    
    //Create floor method
    
    private void createFloor(){
        Box floorMesh=new Box(33, 2, 33);
        Geometry floor=new Geometry("Floor", floorMesh);
        Material floorMat=new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        floorMat.setColor("Color", ColorRGBA.Orange);
        floor.setMaterial(floorMat);
        floor.setLocalTranslation(Vector3f.ZERO);
        rootNode.attachChild(floor);
    }
    
    //Create player method
    
    private Geometry createPlayer(){
        Box playerMesh=new Box(16.5f, 5, 1);
        Geometry player=new Geometry("Player", playerMesh);
        Material playerMat=new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        playerMat.setColor("Color", ColorRGBA.Yellow);
        player.setMaterial(playerMat);
        player.setLocalTranslation(new Vector3f(0, 7, PLAYER_LOCATION_Z));
        return player;
    }
    @Override
    public void update(float tpf) {
        //TODO: implement behavior during runtime
       /* if(System.currentTimeMillis()-startTime>1000){
            clearBeamNode();
            startTime=System.currentTimeMillis();
        }*/
        addBudgetByTimer(tpf);
        checkGameWinStatus();
    }
    
    @Override
    public void cleanup() {

        
        super.cleanup();
        rootNode.detachAllChildren();
            
        //TODO: clean up what you initialized in the initialize method,
        //e.g. remove all spatials from rootNode
        //this is called on the OpenGL thread after the AppState has been detached
    }
    
    
    protected int getlevel(){
        return level;
    }
    protected int getScore(){
        return score;   
    }
    
    protected int getHealth(){
        return health;
    }
    
    protected void reduceHealth(){
        health-=2;
    }
    
    protected void addBudget(){
        budget++;
    }
    protected int getBudget(){
        return budget;
    }
    
    protected boolean isLastGameWon(){
        return lastGameWon;
    }
    
    protected void checkGameWinStatus(){    
        if(creepNode.getQuantity()==0&& health>0){
          lastGameWon=true;
          System.out.println("Game Won");
          stateManager.detach(this);
        } 
        if(health<=0){
          lastGameWon=false;
          System.out.println("Game Lost");
          stateManager.detach(this);
        } 
    }
     
    protected void addBudgetByTimer(float tpf){
        if(timer_budget<10)
            timer_budget+=tpf;
        else{
            addBudget();
            timer_budget=0;
        }    
    }
    
    protected void clearBeamNode(){
        beamNode.detachAllChildren();
    }
    
    protected Geometry getSelected(){
        return selectedTower;
    }
}