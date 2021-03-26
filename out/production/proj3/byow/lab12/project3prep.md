# Project 3 Prep

**For tessellating hexagons, one of the hardest parts is figuring out where to place each hexagon/how to easily place hexagons on screen in an algorithmic way.
After looking at your own implementation, consider the implementation provided near the end of the lab.
How did your implementation differ from the given one? What lessons can be learned from it?**

Answer: The implementation given from the lab made a list of positions for every individual hexagon with places to start as well as just a lot of abstraction. I, myself, did not have as good of an abstraction and just hardcoded, I learned to really attempt to abstract and split the problem into smaller sections.

-----

**Can you think of an analogy between the process of tessellating hexagons and randomly generating a world using rooms and hallways?
What is the hexagon and what is the tesselation on the Project 3 side?**

Answer: Yes I can draw similarities, specifically the hexagons in tessellating would be akin to the elements of the map like rooms, hallways, etc. while tesselating them together would be essentially putting together all the parts! The color was randomized for the tesselation, but the idea can be applied to randomizing rooms and structures.

-----
**If you were to start working on world generation, what kind of method would you think of writing first? 
Think back to the lab and the process used to eventually get to tessellating hexagons.**

Answer: I would start the way that Connor started and start with top down overview and think about what methods I might need. One class would be room creation which would generate a room but with multiple attributes to help and another might be generation of hallways to connect two rooms.

-----
**What distinguishes a hallway from a room? How are they similar?**

Answer: A way I would distinguish the two would be by dimensions in that rooms tend to be wider and hallways have one dimension much larger than the other, so I might set a limit to how wide hallways could be, on the other side it connects rooms so I might add an attribute to showcase an entry point and then have hallways connect at least two.
