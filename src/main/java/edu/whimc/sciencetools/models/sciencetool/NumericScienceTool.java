package edu.whimc.sciencetools.models.sciencetool;

import edu.whimc.sciencetools.javascript.JSContext;
import edu.whimc.sciencetools.javascript.JSNumericExpression;
import edu.whimc.sciencetools.models.conversion.Conversion;
import edu.whimc.sciencetools.utils.Utils;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * A ScienceTool that only uses numeric values. Measurements from this type of tool can be validated.
 */
public class NumericScienceTool extends ScienceTool {

    /* The tool's main unit of measurement. */
    private final String unit;
    /* The decimal precision of the tool. */
    private final int precision;
    /* The accepted unit conversions of the tool. */
    private final List<Conversion> conversions;

    /**
     * Constructs a NumericScienceTool.
     *
     * @param toolKey            The key for this tool in the config.
     * @param displayName        The name to be displayed in-game for this tool.
     * @param aliases            Alternate names for the tool.
     * @param defaultMeasurement The default measurement used when no region or world value is found.
     * @param worldMeasurements  The world-specific global measurements.
     * @param regionMeasurements The region-specific measurements.
     * @param disabledWorlds     The worlds where the tool cannot be measured.
     * @param unit               The unit of measurement.
     * @param precision          The decimal precision.
     * @param conversions        The accepted unit conversions.
     */
    public NumericScienceTool(String toolKey,
                              String displayName,
                              List<String> aliases,
                              String defaultMeasurement,
                              Map<World, String> worldMeasurements,
                              Map<World, Map<String, String>> regionMeasurements,
                              Set<World> disabledWorlds,
                              String unit,
                              int precision,
                              List<Conversion> conversions) {
        super(toolKey, displayName, aliases, defaultMeasurement, worldMeasurements, regionMeasurements, disabledWorlds);
        this.unit = unit;
        this.precision = precision;
        this.conversions = conversions;
    }

    /**
     * Display the measured number to the player based off their current location.
     *
     * @param player The target player.
     * @return The measurement
     */
    @Override
    public @Nullable String displayMeasurement(Player player) {
        // check if player in disabled world

        if (super.disabledWorlds.contains(player.getWorld())) {
            Utils.msg(player, Message.DISABLED_IN_WORLD.format(this, player));
            return null;
        }
        String message = Message.NUMERIC_MEASURE.format(this, player);
        double data = getData(player.getLocation());

        // display converted values
        for (Conversion conversion : conversions) {
            String converted = Utils.trimDecimals(conversion.convert(data), precision);
            message += " (" + converted + conversion.getUnit() + ")";
        }

        Utils.msg(player, message);

        return Utils.trimDecimals(data, this.precision) + this.unit;
    }

    /**
     * Gets the measurement data at a specified location.
     *
     * @param loc The location to get the measurement data from.
     * @return The measurement data at the specified location.
     */
    public double getData(Location loc) {
        JSNumericExpression expression = new JSNumericExpression(super.getMeasurement(loc));
        return expression.evaluate(JSContext.create(loc));
    }

    public String getMainUnit() {
        return this.unit;
    }

    /**
     * The number of decimals the numeric measurement will have.
     */
    public int getPrecision() {
        return this.precision;
    }
}
