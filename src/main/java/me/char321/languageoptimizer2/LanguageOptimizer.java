package me.char321.languageoptimizer2;

import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import me.char321.languageoptimizer2.logic.Criteria;
import net.fabricmc.api.ModInitializer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.locale.Language;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Recipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class LanguageOptimizer implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("languageoptimizer2");
	public static List<Criteria> criteria = new ArrayList<>();
	public static Minecraft minecraft = Minecraft.getInstance();

	public static void addCriteria(Criteria newCriteria) {
		for (int i = 0; i < criteria.size(); i++) {
			if (criteria.get(i).weight <= newCriteria.weight) {
				criteria.add(i, newCriteria);
				return;
			}
		}
		criteria.add(newCriteria);
	}

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.


	}

	public static void optimize() {
//		for (RecipeCollection collection : collections) {
//			System.out.println(collection.getRecipes().stream().map(Recipe::getId).collect(Collectors.toList()));
//		}
		initItemToCollection();

		List<LanguageResult> results = new ArrayList<>();
		for (LanguageInfo langinfo: minecraft.getLanguageManager().getLanguages()) {
			results.add(getScore(langinfo));
		}
		minecraft.getLanguageManager().setSelected(minecraft.getLanguageManager().getLanguage("en_us"));
		minecraft.getLanguageManager().onResourceManagerReload(minecraft.getResourceManager());
		minecraft.searchRegistry.onResourceManagerReload(minecraft.getResourceManager());
		results.sort((a, b) -> b.score - a.score);
		for (LanguageResult result : results) {
			System.out.println(result.score + " " + result.langinfo.getCode() + " " + result.langinfo.toString() + " " + result.searchStrings);
		}
	}

	public static LanguageResult getScore(LanguageInfo langinfo) {
		minecraft.getLanguageManager().setSelected(langinfo);
		minecraft.getLanguageManager().onResourceManagerReload(minecraft.getResourceManager());
		minecraft.searchRegistry.onResourceManagerReload(minecraft.getResourceManager());

		int score = 0;
		List<String> searchStrings = new ArrayList<>();
		for (Criteria c : criteria) {
			String searchString = null;
			String recipe0str = c.requiredRecipes.get(0);
			RecipeCollection collection = getCollection(ResourceLocation.tryParse(recipe0str));
			List<String> r0strings = getSearchable(collection);
			recipestring: for (String r0string : r0strings) {
				substring: for (String r0substring : getSubstrings(r0string, c.searchLength)) {
					int _gridsize = 2;
					for (String requiredRecipe : c.requiredRecipes) {
						Recipe<?> recipe = getRecipe(ResourceLocation.tryParse(requiredRecipe));
						if (!recipe.canCraftInDimensions(2,2)) {
							_gridsize = 3;
							break;
						}
					}
					int gridsize = _gridsize;
					StackedContents contents = new StackedContents();
					for (ItemStack junkItem : c.junkItems) {
						contents.accountSimpleStack(junkItem);
					}
					List<RecipeCollection> collections = minecraft.player.getRecipeBook().getCollection(RecipeBookCategories.SEARCH);
					collections.forEach(collection1 -> collection1.canCraft(contents, gridsize, gridsize, minecraft.player.getRecipeBook()));
					collections = new ArrayList<>(collections);
					collections.removeIf(recipeCollection -> !recipeCollection.hasFitting());
					ObjectSet<RecipeCollection> objectSet = new ObjectLinkedOpenHashSet<>(
							minecraft.getSearchTree(SearchRegistry.RECIPE_COLLECTIONS).search(r0substring.toLowerCase(Locale.ROOT))
					);
					collections.removeIf(recipeCollection -> !objectSet.contains(recipeCollection));
					collections.removeIf(recipeCollection -> !recipeCollection.hasCraftable());

					for (String requiredRecipe : c.requiredRecipes) {
						Recipe<?> recipe = getRecipe(ResourceLocation.tryParse(requiredRecipe));
						if (!collections.contains(getCollection(recipe.getId()))) {
							// required recipe not found
							continue substring;
						}
					}

					if (c.noJunk && collections.size() > c.requiredRecipes.size()) {
						// junk exists
						continue substring;
					}
					score += c.weight;
					searchString = r0substring;
					break recipestring;
				}
			}
			searchStrings.add(searchString);
		}
		return new LanguageResult(score, searchStrings, langinfo);
	}

	private static List<String> getSearchable(RecipeCollection collection) {
		List<String> res = new ArrayList<>();
		for (Recipe<?> recipe : collection.getRecipes()) {
			recipe.getResultItem().getTooltipLines(null, TooltipFlag.Default.NORMAL).stream()
					.map(component -> ChatFormatting.stripFormatting(component.getString()).trim())
					.filter(s -> !s.isEmpty())
					.forEach(res::add);
		}
		return res;
	}

	private static Set<String> getSubstrings(String r0string, int searchLength) {
		Set<String> res = new HashSet<>();
		for (int i = 0; i < r0string.codePointCount(0, r0string.length()) - searchLength + 1; i++) {
			res.add(codePointSubstring(r0string, i, i + searchLength));
		}
		return res;
	}

	private static String codePointSubstring(String s, int start, int end) {
		int a = s.offsetByCodePoints(0, start);
		return s.substring(a, s.offsetByCodePoints(a, end - start));
	}

	static Map<ResourceLocation, RecipeCollection> recipeToCollection = new HashMap<>();
	static Map<ResourceLocation, Recipe<?>> locationToRecipe = new HashMap<>();

	public static void initItemToCollection() {
		for (RecipeCollection collection : minecraft.player.getRecipeBook().getCollections()) {
			for (Recipe<?> recipe : collection.getRecipes()) {
				recipeToCollection.put(recipe.getId(), collection);
				locationToRecipe.put(recipe.getId(), recipe);
			}
		}
	}

	public static Recipe<?> getRecipe(ResourceLocation recipe) {
		return locationToRecipe.get(recipe);
	}

	public static RecipeCollection getCollection(ResourceLocation recipe) {
		return recipeToCollection.get(recipe);
	}

	public static class LanguageResult {
		public int score;
		public List<String> searchStrings;
		public LanguageInfo langinfo;
		public LanguageResult(int score, List<String> searchStrings, LanguageInfo langinfo) {
			this.score = score;
			this.searchStrings = searchStrings;
			this.langinfo = langinfo;
		}
	}

}
