package Game.Entities.Static;


import Main.Handler;

/**
 * Created by AlexVR on 7/2/2018.
 */
public class Apple {
	
    private Handler handler;
    public int xCoord;
    public int yCoord;
    private static boolean isGood = true;
    public Apple(Handler handler,int x, int y){
        this.handler=handler;
        this.xCoord=x;
        this.yCoord=y;
        
  
        
    }
    public static boolean goodApple() {
    	return isGood;
    	
    	
    }
    public static void setGood(boolean goodApple) {
    	 isGood = goodApple; 
    }
    	
    
}
