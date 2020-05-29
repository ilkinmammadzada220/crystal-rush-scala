## Crystal Rush  CODINGAME
### Language Scala

## Strategy
This part is only about poor strategy, not classes, methods and other stuffs.

Actually this code doesn't use radar and trap. So, robots take random point to move or dig.
These positions are below, posX and posY
```
      val posX = ThreadLocalRandom.current.nextInt(0, 30)
      val posY = ThreadLocalRandom.current.nextInt(0, 15)
      val headquarter = new Point(0, robot.getPos().y)
```

Before starts, we check the certain robot is dead or not.If is dead, then we 
run WAIT (none()) action.
```
      if(!robot.isAlive())
          {
            robot.none()
          }
```

As we read from rules, robots have to deliver ore which it has. Ore is item. There is no
only item type. So, we have to check does robot has item and it is ore(CRYSTAL) or not.
If it is CRYSTAL, then robot must deliver it to headquarter.
```
      if(!robot.getItem().equals("NOTHING")){
          if(robot.getItem().equals("CRYSTAL")) {
            robot.move(headquarter)
          }
```

In empty situation robot can dig some cells. There is a hashmap, which take robot id and position(Point).
It provides to know that robot has arrived to certain position or not. If it has arrived, it can dig proper cell.
```
      if(board.robotCell.contains(robot.getId()) == false)
          {
            board.robotCell += (robot.getId() -> new Point(posX,posY))
          }
          var digPos = new Point(board.robotCell.get(robot.getId()).get.x,board.robotCell.get(robot.getId()).get.y)
          robot.dig(digPos)
        }
```

