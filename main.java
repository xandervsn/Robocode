package avs;
import robocode.*;
import robocode.Robot;
import robocode.ScannedRobotEvent;
import static robocode.util.Utils.normalRelativeAngleDegrees;
import java.awt.Color;
import java.util.Scanner;
import java.lang.Math;
/**
 * Strafe - a robot by Xander Siruno-Nebel
 * 1/28/22
 * Java Programming
 * A Robocode robot that locks on to a target, then tries to evade any bullets
 */
public class Strafe extends TeamRobot
  {
  //variables
  boolean goingLeft = false;
  double enemyEnergy;
  boolean energyFound = false;
  boolean goingAhead = true;
  boolean onWall = false;
  double random;
  double directionNum = 0.75;
  boolean heads;
  boolean bulletHit;
  int timeWithoutFiring;
  boolean hasNotMoved = false;
  boolean still = false;
  public void run() {
    setColors(Color.red,Color.blue,Color.green); // body,gun,radar
    if(goingLeft){
      /*scans for a robot, either right or left depending on
      what direction the previous robot it looked at was moving
       */
      while(true){
        turnGunLeft(90);
      }
    }else{
      while(true){
        turnGunRight(90);
      }
  }
}

public void onBulletHit(BulletHitEvent event){
  bulletHit = true;
  still = true;
}

public void onBulletHitBullet(BulletHitBulletEvent event){
  bulletHit = true;
}

public void onBulletMissed(BulletMissedEvent event){
  still = false;
  timeWithoutFiring = 0;
}

/**
 * onScannedRobot: What to do when you see another robot
 */
public void onScannedRobot(ScannedRobotEvent e) {
  if(isTeammate(e.getName())) {
    //if the other robot is a teamate, nothing else is needed
    return;
  }
  if(energyFound == false){
    //finds the energy of the enemy robot
    enemyEnergy = e.getEnergy();
    energyFound = true;
  }

  //following code is taken from TrackFire bot:
  double absoluteAngle = getHeading() + e.getBearing();
  double relativeAngle = normalRelativeAngleDegrees(absoluteAngle -
  getGunHeading());
  turnGunRight(relativeAngle);
  /*finds the overall angle that the bot is looking, as well as the
  angle that it's looking
   * at the robot from, to create an absolute angle
   *
   * finds exactly what angle to turn to keep looking at the other
  robot by finding the
   * angle it's looking at from a bird's eye view, then
  substracting that from the angle
   * of the enemy robot
   */
  findDirection(relativeAngle, e);
  if(e.getDistance() < 300){
    smartFire(e.getDistance());
  }else{
    timeWithoutFiring++;
  }
  if(timeWithoutFiring > 10){
    dumbFire();
  }
  if(e.getEnergy() < enemyEnergy){ //DODGING SEQUENCE
  /* if the enemy loses energy, & not bc of our bullet, it's
  most likely
   * because it's just fired a bullet. we need to dodge
   */
  random = Math.random();
  if(random > 0.5){
    //the evasion pattern is random, and unpredictable
    heads = true;
  }else{heads = false;}
    /*if we're not on a wall, we can ignore it, and move totally randomly
   * we go either forwards or back,
   */
  if(heads){
    directionNum = 0.75;
  }else{
    directionNum = 0.25;
  }
  if(random > 0.75){
    setAhead(100);
    goingAhead = true;
  }else{
    setBack(100);
    goingAhead = false;
  }
  if(heads){
    //at some random angle
    setTurnRight(random * 10 + 30);
    setTurnGunLeft(random * 10 + 30);
  }else{
    setTurnLeft(random * 10 + 30);
    setTurnGunRight(random * 10 + 30);
  }
    waitFor(new TurnCompleteCondition(this));
    energyFound = false;
  }else {
    bulletHit = false;
  }
}

public void onHitWall(HitWallEvent event){
  onWall = true;
  if(goingAhead){
    setBack(100);
  }else{
    setAhead(100);
  }
  waitFor(new TurnCompleteCondition(this));
}

public void findDirection(double relativeAngle, ScannedRobotEvent e){
  /*finds the enemy robot in relation to us
   * depending on the change in angles, we know whether or not the robot
  is moving right or left
   * this means that we have a rough idea of what direction to scan for
  if we lose track of it
   */
  double absoluteAngle = getHeading() + e.getBearing();
  double relativeAngle2 = normalRelativeAngleDegrees(absoluteAngle -
  getGunHeading());
  turnGunRight(relativeAngle2);
  if(relativeAngle > relativeAngle2){
    goingLeft = true;
  }else if(relativeAngle < relativeAngle2){
    goingLeft = false;
  }else if(hasNotMoved && relativeAngle == relativeAngle2){
  }
}

public void dumbFire(){
  if(still == true){
    if(getGunHeat() == 0){
      fire(3);
    }
  }else{
    if(getGunHeat() == 0){
    fire(3);
  }
    timeWithoutFiring = 0;
  }
}

  public void smartFire(double botDistance){
    timeWithoutFiring = 0;
    //depending on how far away from us the robot is, it's esseentially
    useless to shoot
    if(getGunHeat() == 0){
      if(botDistance < 300 && botDistance >= 100){
        fire(1);
      }else if(botDistance < 100 && botDistance >= 50){
        fire(2);
      }else if(botDistance < 50){
        fire(3);
      }
    }
  }
}
