package me.char321.languageoptimizer2.command;

import com.mojang.brigadier.CommandDispatcher;
import me.char321.languageoptimizer2.LanguageOptimizer;
import me.char321.languageoptimizer2.logic.Criteria;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;

import java.util.List;

public class ListCriteriaCommand {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("listcriteria").executes(context -> {
			listCriteria(context.getSource());
			return 0;
		}));
	}

	private static void listCriteria(CommandSourceStack source) {
		source.sendSuccess(new TextComponent("Criteria:").withStyle(ChatFormatting.BOLD), true);
		List<Criteria> criteria = LanguageOptimizer.criteria;
		for (int i = 0; i < criteria.size(); i++) {
			Criteria criterion = criteria.get(i);

			source.sendSuccess(new TextComponent("").append(new TextComponent(i + ". ").withStyle(ChatFormatting.UNDERLINE, ChatFormatting.AQUA)).append(new TextComponent(criterion.toString()).withStyle(ChatFormatting.RESET)), false);
		}
	}
}
