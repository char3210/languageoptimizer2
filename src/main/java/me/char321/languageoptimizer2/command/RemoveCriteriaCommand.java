package me.char321.languageoptimizer2.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import me.char321.languageoptimizer2.LanguageOptimizer;
import me.char321.languageoptimizer2.logic.Criteria;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.commands.Commands.argument;

public class RemoveCriteriaCommand {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("removecriteria").then(
				argument("index", integer()).executes(context -> {
					Criteria removed = LanguageOptimizer.criteria.remove(IntegerArgumentType.getInteger(context, "index"));
					context.getSource().sendSuccess(new net.minecraft.network.chat.TextComponent("Removed criteria " + removed), true);
					return 0;
				})
		));
	}
}
