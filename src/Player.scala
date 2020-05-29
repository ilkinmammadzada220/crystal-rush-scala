import math._
import scala.collection.immutable.HashMap
import scala.util._
import scala.io.StdIn._
/*
  ILKIN MAMMADZADA
 */


class Point(var x  : Int, var y : Int) {  //Every Cell has Point and it has coordinates on map, like X and Y
  def getX() : Int = x //to get X coordinate
  def getY() : Int = y //to get Y coordinate

  def setX(x : Int){  //to set X coordinate
    this.x = x
  }
  def setY(y : Int){  //to set Y coordinate
    this.y = y
  }
}

class Cell(oreStr : String, holeStr : String) { //Cells create a whole map
  //Some items of Cell
  var isKnown = true
  var ore = 0
  var isHole = false

  // Ore and Hole inputs are as String,
  // So we need to parse Ore to String and check Hole as String
  if(oreStr.charAt(0) == '?'){
    isKnown = false
    this.ore = 0
  }else{
    isKnown = true
    this.ore = Integer.parseInt(oreStr)
  }
  this.isHole = holeStr.charAt(0) != '0'
}


/*
  There are 2 types of entities : Static and Dynamic
  Statics are   -items-
  Dynamics are  -robots-
  Every entity has id, type, position, and if it is robot, it has a item type
 */
class Entity(id : Int, `type` : Int, pos : Point, item : Int) {
  getPos()

  var DEAD_POS : Point = new Point(-1,-1) //Robot is placed to this position if it's dead already

  /*
    Instead of Enum I preferred HashMap to keep types of entities in Scala.
    I suggest Enum if you code in Java
   */
  var types:Map[Int,String] = Map()
  types += (-1 -> "NOTHING")
  types += (0 -> "ALLY_ROBOT")
  types += (1 -> "ENEMY_ROBOT")
  types += (2 -> "RADAR")
  types += (3 -> "TRAP")
  types += (4 -> "CRYSTAL")


  /*
    Robot act with special commands which it's given through printf() command.
    These commands are "WAIT" "MOVE" "DIG" "REQUEST"
   */
  var command = new StringBuilder("") // Every specific function call command String extends with special commands.

  def getPos() : Point =  //to get position of entity
  {
    this.pos
  }
  def getId() : Int = {   //to get ID of entity
    this.id
  }
  def isAlive() : Boolean={ //check if entity is alive
    !DEAD_POS.equals(pos)
  }
  def isEmpty() : Boolean={ //check robot has item or not
    item == null
  }
  def getType() : String =  //to get type of entity
  {
    types(`type`)
  }
  def getItem() : String =  //to get what type of item entity has
  {
    types(this.item)
  }

  /*
    Specific functions use for ally robots

    command format must be like that: Ex:  MOVE 4 5  or  DIG 8 15
   */
  def none(): Unit ={
    command.append("WAIT")
    println(command)
    command.setLength(0)
  }
  def move(pos : Point): Unit = {
    command.append("MOVE ").append(pos.x).append(' ').append(pos.y)
    println(command)
    command.setLength(0)
  }
  def dig(pos : Point): Unit = {
    command.append("DIG ").append(pos.x).append(' ').append(pos.y)
    println(command)
    command.setLength(0)
  }
}


import scala.collection.mutable.ListBuffer

/*
  There are 2 teams of robots, our team and opponent team.
  Teams have score and list of robots.
 */
class Team(var score : Int = 0) {
  setScore(score)
  var robots = new ListBuffer[Entity]
  def getScore() : Int = score
  def setScore(score : Int)   //to set score of team
  {
    this.score = score
  }
  def addRobot(robot : Entity)  //to add a robot to team
  {
    robots += robot
  }
}

import scala.io.StdIn.readLine

/*
  Board is our map, which has width and height
  Board consists of bunch of Cells.

  update() method updates all boards elements every game second.

  PS. Its name was 'Map' before. However in Scala it was exist already. I decided to change to 'Board'
 */
class Board(width : Int, height : Int) {

  val robotCell = scala.collection.mutable.Map[Int,Point]() //keep robot and move position together
  def getWidth() : Int = width  //width of Board
  def getHeight() : Int = height  //height of Board
  var myTeam = new Team()
  var opponentTeam = new Team()

  var cells = Array.ofDim[Cell](width,height)

  def update()
  {
    // myScore: Amount of ore delivered
    val Array(myScore, opponentScore) = (readLine split " ").map (_.toInt)
    myTeam.setScore(myScore)
    opponentTeam.setScore(opponentScore)

    for(i <- 0 until height) {
      var inputs = readLine split " "

      for(j <- 0 until width) {
        // ore: amount of ore or "?" if unknown
        // hole: 1 if cell has a hole

        if((2*j+1) < 30)
        {
          var hole = inputs(2*j+1)
          cells(i)(j) = new Cell(inputs(2*j),hole)
        }

      }
    }
    // entityCount: number of entities visible to you
    // radarCooldown: turns left until a new radar can be requested
    // trapCooldown: turns left until a new trap can be requested
    val Array(entityCount, radarCooldown, trapCooldown) = (readLine split " ").map (_.toInt)
    //I passed coolDowns for now

    for(i <- 0 until entityCount) {
      // entityId: unique id of the entity
      // entityType: 0 for your robot, 1 for other robot, 2 for radar, 3 for trap
      // y: position of the entity
      // item: if this entity is a robot, the item it is carrying (-1 for NONE, 2 for RADAR, 3 for TRAP, 4 for ORE)
      val Array(entityId, entityType, x, y, item) = (readLine split " ").map(_.toInt)
      var entity = new Entity(entityId, entityType, new Point(x, y), item)
      if (entity.getType().equals("ALLY_ROBOT")) {
        myTeam.addRobot(entity)
      }
      if (entity.getType().equals("ENEMY_ROBOT")) {
        opponentTeam.addRobot(entity)
      }
    }
  }

  def getCell(pos : Point): Cell =    //to get certain Cell by its position
  {
    cells(pos.getX())(pos.getY())
  }
}


object Player extends App {
  // height: size of the map
  val Array(width, height) = (readLine split " ").map (_.toInt)

  var board = new Board(width,height)

  // game loop
  while(true) {
    //Every game loop, we update our board to change scores, entities' position, items, etc.
    board.update()

    /*
      For now I preferred giving random position to robots for digging.
      And I don't use radars & traps
     */
    for(robot <- board.myTeam.robots) {
      import java.util.concurrent.ThreadLocalRandom
      val posX = ThreadLocalRandom.current.nextInt(0, 30)
      val posY = ThreadLocalRandom.current.nextInt(0, 15)
      val headquarter = new Point(0, robot.getPos().y)  //position of delivery line

      if(!robot.isAlive())  //First check if robot is alive
      {
        robot.none()        //If it is, then WAIT
      }else{
        //Before other actions, check robot's item, have or not
        if(!robot.getItem().equals("NOTHING")){
          if(robot.getItem().equals("CRYSTAL")) {
            robot.move(headquarter)
          }
        }else{
          if(board.robotCell.contains(robot.getId()) == false)
          {
            board.robotCell += (robot.getId() -> new Point(posX,posY))    //give a move position to robot by its id
          }
          var digPos = new Point(board.robotCell.get(robot.getId()).get.x,board.robotCell.get(robot.getId()).get.y)
          robot.dig(digPos) //DIG same position with move position




        }
      }
    }
  }
}