# Loop Mania 2.0

[**Link to specification**](https://gitlab.cse.unsw.edu.au/COMP2511/21T2/project-specification)

## How to Play :sunglasses:
Similar to spec above. Points of difference:

- To consume a health potion, press ```H``` key :tea:
- To move an item from unequipped to equipped, drag the item to the correct spot in equipped grid
- To replace your equipped weapon, drag another one over the top
- Move rare item into equipped to use unless it's the one ring
- Pay 10% tax everytime you go past the tax office 

## Assumptions

## Extensions :construction_worker:
- Animating character movements for a more seamless experience
- 10% chance of getting the tax office added to game when fighting Elan
- Differences in chance of getting rare item after winning battle based on game mode 
                            Survival < Berserker < Normal
- Durability on equipped items 
- Music and sound effects
- Elan comes out of a rocket
- Doggie comes out of a crypto mine


## General :clipboard:
- When a battle is over, rewards appear in the Character's inventory
- Between 0 and 2 items can appear on the ground per cycle
- If max number of a building has been reached in the game, character gets same gold as card's value

## Buildings :house:
- In standard mode, you can sell and buy from Hero's Castle as many times as you like before you resume the game
- A tower will attack enemy if enemy is in battle and within the tower's shooting range
- Buildings cannot be placed on top of one another
- Maximum of 2 barracks in the game world.
- Maximum of 2 campfires in the game world.
- Maximum of 3 towers in the game world.
- Maximum of 3 traps in the game world.

## Enemies :skull:
- Slugs do not drop any items when killed because they don't have pockets :(
- All enemy types increase character's gold, experience and give a card when defeated.
- One or two zombies at a time can be produced from the Zombie pit.
- Only one vampire is produced from Vampire's Castle at a time.
- Since critical bites are rare, vampire preserves them for main character.
- The number of rounds for which a vampire's critical bite is more effective is 3.
- 10% chance slug will be randomly spawned.


## Character :video_game:
- Health is capped at 100


## Items :gear:
- Multiple health potions can be used in one cycle (however cannot stack and go over 100 health).
- Equipped inventory consists of weapon and protective gear.
- Character can wear all forms of protective gear at one point
- Character can only have maximum one equipped weapon at any point in time.
- Health potion will remain in unequipped but its value can be used.
- A health potion is consumed by pressing ```H``` key.
- Items in equipped inventory have designated spots (Weapon, Helmet, Shield, Armour respectively)
- Character can only sell items that are in the unequipped inventory.
- When an item is won/picked up/bought, then it automatically goes to unequipped inventory.
- Once a character equips a helmet/armour/shield it cannot be unequipped.
- Character can change weapon of choice by dragging a different weapon to the equipped weapon box (index 0).
- Items can be bought and sold at constant price (no depreciation in value).
- The One Ring only appears once in the game world, and cannot reappear once it has been used.

## Allied Soldier :gear:
- Maximum of 4 allied soldiers at any point in time can accompany the Character.


## Game modes
- The game mode affects the character's ability to shop at the Hero's Castle
- Affects chance of getting a rare item

# References
## Assets
Assets were used from the following places:
- [Allied Soldier](https://lionheart963.itch.io/4-directional-character)
- [Elan Musk](https://www.spriters-resource.com/fullview/128323)
- [Main Player](https://szadiart.itch.io/rpg-main-character)
- [Zombies](https://opengameart.org/content/zombie-rpg-sprites)
- [Vampire](http://finalbossblues.com/timefantasy/wp-content/uploads/2015/10/vampire.png)
- [Slug](https://opengameart.org/content/a-load-of-overworld-34-rpg-sprites)
- [Doggie (I wrote the text)](https://forums.rpgmakerweb.com/index.php?attachments/dog-png.129710/)
- [Inventory](https://opengameart.org/content/golden-ui)

SpriteAnimation class taken from [here](https://netopyr.com/2012/03/09/creating-a-sprite-animation-with-javafx), permission from forums [here](https://edstem.org/courses/5957/discussion/533499)