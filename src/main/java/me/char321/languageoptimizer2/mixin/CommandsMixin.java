package me.char321.languageoptimizer2.mixin;

import com.mojang.brigadier.CommandDispatcher;
import me.char321.languageoptimizer2.command.AddCriteriaCommand;
import me.char321.languageoptimizer2.command.ClearCriteriaCommand;
import me.char321.languageoptimizer2.command.ListCriteriaCommand;
import me.char321.languageoptimizer2.command.OptimizeCommand;
import me.char321.languageoptimizer2.command.RemoveCriteriaCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Commands.class)
public class CommandsMixin {
	@Shadow @Final private CommandDispatcher<CommandSourceStack> dispatcher;

	@Inject(method = "<init>", at=@At(value="INVOKE",shift = At.Shift.BEFORE,target="Lcom/mojang/brigadier/CommandDispatcher;findAmbiguities(Lcom/mojang/brigadier/AmbiguityConsumer;)V"))
	public void registerCommands(Commands.CommandSelection selection, CallbackInfo ci) {
		AddCriteriaCommand.register(dispatcher);
		ListCriteriaCommand.register(dispatcher);
		OptimizeCommand.register(dispatcher);
		ClearCriteriaCommand.register(dispatcher);
		RemoveCriteriaCommand.register(dispatcher);
	}
}
