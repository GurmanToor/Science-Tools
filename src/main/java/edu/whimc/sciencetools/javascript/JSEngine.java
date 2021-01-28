package edu.whimc.sciencetools.javascript;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import co.aikar.commands.InvalidCommandArgument;

public class JSEngine {

    private static final ScriptEngineManager engineManager = new ScriptEngineManager();
    private static final ScriptEngine engine = engineManager.getEngineByName("Nashorn");
    private static final Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);

    /**
     * Set up the engine and remove some harmful bindings
     */
    static {
        bindings.remove("print");
        bindings.remove("load");
        bindings.remove("loadWithNewGlobal");
        bindings.remove("exit");
        bindings.remove("java");
        bindings.remove("quit");
        try {
            for (JSFunction func : JSFunction.values()) {
                engine.eval(func.getDefinition());
            }
        } catch (ScriptException e) {}
    }

    protected static Object run(String code, boolean throwArgumentError) {
        try {
            return engine.eval(code);
        } catch (ScriptException e) {
            if (throwArgumentError) {
                String error = "Your expression contains invalid syntax!\n";
                error += e.getMessage();

                throw new InvalidCommandArgument(error, false);
            }
            return null;
        }
    }

    protected static Double evaluate(String expression, boolean throwArgumentError) {
        Object res = run(expression, throwArgumentError);

        if (res instanceof Number) {
            return Double.valueOf(((Number) res).doubleValue());
        }

        if (!throwArgumentError) {
            return null;
        }

        String type = "Unknown";
        if (res != null) {
            type = res.getClass().getSimpleName();
        }

        throw new InvalidCommandArgument("The JavaScript expression must resolve to a number (Found type \"" + type + "\")", false);
    }

}