/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of SpruceUI.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.spruceui.option;

import me.lambdaurora.spruceui.Position;
import me.lambdaurora.spruceui.widget.SpruceWidget;
import me.lambdaurora.spruceui.widget.option.SpruceOptionSliderWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.DoubleOption;
import net.minecraft.client.options.GameOptions;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents a double option.
 * <p>
 * Works the same as the vanilla one but can provide a tooltip.
 *
 * @author LambdAurora
 * @version 2.0.0
 * @since 1.0.0
 */
public class SpruceDoubleOption extends SpruceOption {
    protected final float step;
    protected final double min;
    protected double max;
    private final Supplier<Double> getter;
    private final Consumer<Double> setter;
    private final Function<SpruceDoubleOption, Text> displayStringGetter;

    public SpruceDoubleOption(@NotNull String key, double min, double max, float step, @NotNull Supplier<Double> getter, @NotNull Consumer<Double> setter, @NotNull Function<SpruceDoubleOption, Text> displayStringGetter, @Nullable Text tooltip) {
        super(key);
        this.min = min;
        this.max = max;
        this.step = step;
        this.getter = getter;
        this.setter = setter;
        this.displayStringGetter = displayStringGetter;
        this.setTooltip(tooltip);
    }

    @Override
    public @NotNull SpruceWidget createWidget(@NotNull Position position, int width) {
        SpruceOptionSliderWidget slider = new SpruceOptionSliderWidget(position, width, 20, this);
        this.getOptionTooltip().ifPresent(slider::setTooltip);
        return slider;
    }

    public double getRatio(double value) {
        return MathHelper.clamp((this.adjust(value) - this.min) / (this.max - this.min), 0.0D, 1.0D);
    }

    public double getValue(double ratio) {
        return this.adjust(MathHelper.lerp(MathHelper.clamp(ratio, 0.0D, 1.0D), this.min, this.max));
    }

    private double adjust(double value) {
        if (this.step > 0.0F) {
            value = this.step * (float) Math.round(value / (double) this.step);
        }

        return MathHelper.clamp(value, this.min, this.max);
    }

    public double getMin() {
        return this.min;
    }

    public double getMax() {
        return this.max;
    }

    public void setMax(float max) {
        this.max = max;
    }

    public void set(double value) {
        this.setter.accept(value);
    }

    /**
     * Gets the current value.
     *
     * @return the current value
     */
    public double get() {
        return this.getter.get();
    }

    /**
     * Gets the display string.
     *
     * @return the display string
     */
    public @NotNull Text getDisplayString() {
        return this.displayStringGetter.apply(this);
    }

    /**
     * Returns a new SpruceUI Double Option from the Vanilla one.
     *
     * @param key the option's key
     * @param vanilla the Vanilla option
     * @param tooltip the tooltip
     * @return the SpruceUI option
     */
    public static @NotNull SpruceDoubleOption fromVanilla(@NotNull String key, @NotNull DoubleOption vanilla, float step, @Nullable Text tooltip) {
        GameOptions options = MinecraftClient.getInstance().options;
        return new SpruceDoubleOption(key, vanilla.getMin(), vanilla.getMax(), step,
                () -> vanilla.get(options),
                newValue -> vanilla.set(options, newValue),
                option -> vanilla.getDisplayString(options),
                tooltip);
    }
}
