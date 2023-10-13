# languageoptimizer2

## Usage

install it as a mod in 1.16.1 fabric

*disclaimer: this mod makes no guarantees about the integrity of your game, don't join any servers, make backup worlds/use a separate instance*

You will specify a list of criteria that you want your language to satisfy. Roughly, a criterion is one or more recipes that you want to search for with a given length of characters and with minimal junk items. For example, a criterion may say to craft a bucket using a single character with no junk items.

To specify criteria, first fill your inventory with items that you expect to have when crafting your item. (e.g. for bucket, typically bt materials + sticks and iron). Note that the amount of items you have is relevant, and you must have enough ingredients to at least craft your item. (e.g. you need to have at least 3 iron if you want to specify a bucket criterion, wood + wool for beds, etc)

Then, type `/addcriteria <weight> <searchLength> <noJunk> <requiredRecipes...>`. Parameters are as follows:

Weight is a number that specifies how important this criterion is. If a language satisfies the criterion, this number will be added to its score. 

searchLength is the length of the string you want your search to be. For example, if you want a single character craft, you would set this to 1. Often, there are not enough languages that are able to satisfy criteria with just one character, so you may want to set this higher. Note that this is an exact length, so if you set it to 2, you won't get any single character crafts. 

noJunk is true or false, and specifies whether you want to allow junk items in your search.  If it is true, the criteria will only be satisfied if there is a search string that crafts the item with no junk items. If it is false, the criteria will be satisfied if there is a search string that crafts the item with any number of junk items.

requiredRecipes is a space-separated list of recipes that you want to search for, although it can be a single recipe. All recipes must be matched in this list for a criterion to be satisfied. A list of recipes is at recipes.txt, although most recipes are just the name of the output item (e.g. `bucket`, `respawn_anchor`, etc.)

Example: `/addcriteria 100 1 true bucket` will add a "one-to-one" criterion for buckets with weight 100. It is "one-to-one" because the search string must be 1 character, and there must be no junk items.

Advanced: If you want to allow junk recipes, you can specify duplicate recipes. For example, `/addcriteria 100 1 true white_bed respawn_anchor respawn_anchor` will allow up to 3 total possible recipes, and since one recipe will be white bed and another will be respawn anchor, the last recipe can be any recipe, and this criteria will essentially allow one junk recipe. You can specify even more duplicates to allow even more junk items. *~~It's not a bug, it's a feature~~*

You can type `/listcriteria` to see a list of all criteria you have added.

You can type `/removecriteria <index>` to remove a criterion. The index is the number that appears before the criterion when you type `/listcriteria`.

You can type `/clearcriteria` to remove all criteria.

Finally, once you have added all desired criteria, type `/optimize` and press pause :) Once it is done, the results will appear in console/logs. The languages are listed in order of highest score, and the strings for each criteria are listed afterwards if it is satisfied, or null if it isn't satisfied.  
