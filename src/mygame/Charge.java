/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

/**
 *
 * @author ASUS1
 */
public class Charge {
    protected int bulletCount;
    protected int damageValue;
    
    Charge(){
        setBulletCount(2);
        setDamageValue(5);
    }
    
    protected void setBulletCount(int i){
        this.bulletCount=i;
    }
    
    protected int getBulletCount(){
        return bulletCount;
    }
    
    protected void setDamageValue(int i){
        this.damageValue=i;
    }
    
    protected int getDamageValue(){
        return damageValue;
    }
    
    protected void fireBullet(){
            --bulletCount;
    }   
}