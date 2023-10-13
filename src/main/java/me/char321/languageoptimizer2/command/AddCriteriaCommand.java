package me.char321.languageoptimizer2.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.char321.languageoptimizer2.LanguageOptimizer;
import me.char321.languageoptimizer2.logic.Criteria;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static me.char321.languageoptimizer2.LanguageOptimizer.minecraft;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;


public class AddCriteriaCommand {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(literal("addcriteria").then(
				argument("weight", integer()).then(
						argument("searchLength", integer()).then(
								argument("noJunk", bool()).then(
										argument("requiredRecipes...", StringArgumentType.greedyString()).executes(context -> {
											addCriteria(
													IntegerArgumentType.getInteger(context, "weight"),
													BoolArgumentType.getBool(context, "noJunk"),
													StringArgumentType.getString(context, "requiredRecipes..."),
													IntegerArgumentType.getInteger(context, "searchLength"),
													context.getSource());
											return 0;
										})
								)
						)
				)
		));
	}

	private static void addCriteria(int weight, boolean noJunk, String itemsString, int searchLength, CommandSourceStack source) throws CommandSyntaxException {
		String[] s = itemsString.split(" ");
		List<String> recipes = new ArrayList<>();
		Set<ResourceLocation> allRecipes = minecraft.player.getRecipeBook().getCollections().stream().flatMap(collection -> collection.getRecipes().stream()).map(Recipe::getId).collect(Collectors.toSet());

		for (String itemString : s) {
			String trim = itemString.trim();
			if (!allRecipes.contains(ResourceLocation.tryParse(trim))) {
				throw new SimpleCommandExceptionType(() -> "Recipe " + trim + " does not exist").create();
			}
			recipes.add(trim);
		}
		List<ItemStack> inventory= source.getPlayerOrException().inventory.items.stream().map(ItemStack::copy).filter(it -> !it.isEmpty()).collect(Collectors.toList());
		Criteria criteria = new Criteria(recipes, noJunk, inventory, weight, searchLength);
		LanguageOptimizer.addCriteria(criteria);
		source.sendSuccess(new TextComponent("Added criteria: " + criteria.toString()), true);
	}
}
