package me.char321.languageoptimizer2.logic;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

import java.util.List;

public class Criteria {
	public List<String> requiredRecipes;
	public boolean noJunk;
	public List<ItemStack> junkItems;
	public int weight;
	public int searchLength;

	public Criteria(List<String> requiredRecipes, boolean noJunk, List<ItemStack> junkItems, int weight, int searchLength) {
		this.requiredRecipes = requiredRecipes;
		this.noJunk = noJunk;
		this.junkItems = junkItems;
		this.weight = weight;
		this.searchLength = searchLength;
	}

	@Override
	public String toString() {
		return "("+weight+") length "+searchLength+" "+requiredRecipes+(noJunk?"; no junk, ":"; ")+"inventory:"+junkItems;
	}
}
