package dev.manere.utils.command;

import dev.manere.utils.command.builder.CommandBuilder;
import dev.manere.utils.command.builder.dispatcher.CommandDispatcher;
import dev.manere.utils.command.builder.dispatcher.SuggestionDispatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Bukkit-like wrapper for a command, uses {@link CommandBuilder}.
 * @see CommandBuilder
 */
public abstract class Commander implements CommandDispatcher, SuggestionDispatcher {
    private CommandSettings settings;
    private String name;
    private final List<String> aliases = new ArrayList<>();
    private String description;
    private String permission;
    private String usage;

    /**
     * Constructs a new Commander instance.
     * @param name The name of the command to use for registration.
     */
    public Commander(String name) {
        this.settings = CommandSettings.settings();
        this.name = name;
    }

    /**
     * Gets the settings for this command.
     *
     * @return The command settings
     */
    public CommandSettings settings() {
        return settings;
    }

    /**
     * Gets the name of this command.
     *
     * @return The command name
     */
    public String name() {
        return name;
    }

    /**
     * Sets the settings for this command.
     *
     * @param settings The new settings
     */
    public void settings(CommandSettings settings) {
        this.settings = settings;
    }

    /**
     * Sets the name for this command.
     *
     * @param name The new name
     * @return This commander instance
     */
    public Commander name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Sets the description for this command.
     *
     * @param description The description
     * @return This commander instance
     */
    public Commander description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Sets the required permission for this command.
     *
     * @param permission The permission
     * @return This commander instance
     */
    public Commander permission(String permission) {
        this.permission = permission;
        return this;
    }

    /**
     * Sets the usage info for this command.
     *
     * @param usage The usage info
     * @return This commander instance
     */
    public Commander usage(String usage) {
        this.usage = usage;
        return this;
    }

    /**
     * Gets the aliases for this command.
     *
     * @return The aliases
     */
    public List<String> aliases() {
        return aliases;
    }

    /**
     * Gets the description for this command.
     *
     * @return The description
     */
    public String description() {
        return description;
    }

    /**
     * Gets the required permission for this command.
     *
     * @return The permission
     */
    public String permission() {
        return permission;
    }

    /**
     * Gets the usage info for this command.
     *
     * @return The usage info
     */
    public String usage() {
        return usage;
    }
}