package me.char321.languageoptimizer2.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import static me.char321.languageoptimizer2.LanguageOptimizer.optimize;

public class OptimizeCommand {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("optimize").executes(context -> {
			optimize();
			context.getSource().sendSuccess(new net.minecraft.network.chat.TextComponent("done optimizing, check logs for results"), true);
			return 0;
		}));
	}
}
