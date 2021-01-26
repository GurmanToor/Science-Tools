package edu.whimc.sciencetools;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import co.aikar.commands.PaperCommandManager;
import edu.whimc.sciencetools.commands.BaseToolCommand;
import edu.whimc.sciencetools.commands.GetData;
import edu.whimc.sciencetools.commands.acf.ConversionsCommand;
import edu.whimc.sciencetools.commands.acf.ScienceToolsCommand;
import edu.whimc.sciencetools.commands.acf.ToolsCommand;
import edu.whimc.sciencetools.commands.acf.ValidationsCommand;
import edu.whimc.sciencetools.javascript.JSExpression;
import edu.whimc.sciencetools.managers.ConversionManager;
import edu.whimc.sciencetools.managers.ScienceToolManager;
import edu.whimc.sciencetools.managers.ScienceToolManager.ToolType;
import edu.whimc.sciencetools.models.Conversion;

public class ScienceTools extends JavaPlugin implements Listener {

	private ScienceToolManager toolManager;
	private ConversionManager convManager;

	PaperCommandManager manager;

	@Override
	public void onEnable() {
		getCommand("sciencetools").setExecutor(new BaseToolCommand(this));

		saveDefaultConfig();
		getConfig().options().copyDefaults(false);

		convManager = ConversionManager.loadConversions(this);
		toolManager = ScienceToolManager.loadTools(this, convManager);

		for (ToolType tool : ToolType.values()) {
			getCommand(tool.toString().toLowerCase()).setExecutor(new GetData(this, tool));
		}

		registerCommands();
	}

	public ScienceToolManager getToolManager() {
		return toolManager;
	}

	public ConversionManager getConversionManager() {
		return convManager;
	}

	public void reloadScienceTools() {
		reloadConfig();
		convManager = ConversionManager.loadConversions(this);
		toolManager = ScienceToolManager.loadTools(this, convManager);
	}

	private void registerCommands() {
	    manager = new PaperCommandManager(this);

	    manager.enableUnstableAPI("help");

	    manager.getCommandReplacements().addReplacements(
	            "perm.admin", "whimc-sciencetools.admin"
        );

	    manager.registerDependency(ConversionManager.class, convManager);
	    manager.getCommandContexts().registerContext(
	            Conversion.class,
	            convManager.getContextResolver());
	    manager.getCommandCompletions().registerCompletion(
	            "conversions",
	            convManager.getCommandCompletionHandler());
	    manager.getCommandConditions().addCondition(
	            String.class,
	            "unique-conversion",
	            convManager.getConditionHandler());

	    manager.getCommandContexts().registerContext(
	            JSExpression.class,
	            JSExpression.getContextResolver());

	    manager.registerCommand(new ConversionsCommand());
	    manager.registerCommand(new ScienceToolsCommand());
	    manager.registerCommand(new ToolsCommand());
	    manager.registerCommand(new ValidationsCommand());
	}

}