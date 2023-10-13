package me.char321.languageoptimizer2.command;

import com.mojang.brigadier.CommandDispatcher;
import me.char321.languageoptimizer2.LanguageOptimizer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class ClearCriteriaCommand {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("clearcriteria").executes(context -> {
			LanguageOptimizer.criteria.clear();
			context.getSource().sendSuccess(new net.minecraft.network.chat.TextComponent("Cleared criteria"), true);
			return 0;
		}));
	}
}
