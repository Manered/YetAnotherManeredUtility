package dev.manere.utils.menu.normal;

import dev.manere.utils.item.ItemBuilder;
import dev.manere.utils.library.Utils;
import dev.manere.utils.menu.Button;
import dev.manere.utils.menu.CloseListener;
import dev.manere.utils.menu.DragListener;
import dev.manere.utils.menu.MenuBase;
import dev.manere.utils.scheduler.builder.SchedulerBuilder;
import dev.manere.utils.scheduler.builder.TaskType;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * The Menu class allows you to create custom menus with buttons and items in Bukkit/Spigot.
 * You can set buttons, items, borders, and fill patterns within the menu.
 * Menus are displayed to players using the open(Player player) method.
 */
public class Menu implements InventoryHolder, MenuBase<Menu> {
    public final Inventory inventory;
    public final Component title;
    public final int size;
    public final Map<Integer, Button> buttons;
    public final Map<Integer, ItemBuilder> items;
    public CloseListener onClose;
    public DragListener onDrag;

    /**
     * Constructs a new Menu with the specified title and size.
     *
     * @param title The title of the menu.
     * @param size  The size (number of slots) of the menu.
     */
    public Menu(Component title, int size) {
        this.inventory = Utils.plugin().getServer().createInventory(this, size, title);
        this.title = title;
        this.size = size;
        this.buttons = new HashMap<>();
        this.items = new HashMap<>();
    }

    /**
     * Sets a button at the specified slot in the menu.
     *
     * @param slot   The slot to set the button.
     * @param button The Button to set at the slot.
     * @return The Menu instance.
     */
    @Override
    public Menu button(int slot, Button button) {
        buttons.put(slot, button);
        this.inventory.setItem(slot, button.item().build());

        return this;
    }

    /**
     * Sets an item at the specified slot in the menu.
     *
     * @param slot The slot to set the item.
     * @param item The ItemBuilder to set at the slot.
     * @return The Menu instance.
     */
    @Override
    public Menu item(int slot, ItemBuilder item) {
        items.put(slot, item);
        this.inventory.setItem(slot, item.build());

        return this;
    }

    /**
     * Creates a new Menu instance.
     *
     * @return A new Menu instance.
     */
    public static Menu menu(Component title, int size) {
        return new Menu(title, size);
    }

    /**
     * Creates a new Menu instance.
     *
     * @return A new Menu instance.
     */
    public static Menu menu(Component title, int width, int height) {
        return menu(title, width*height);
    }

    /**
     * Gets the size (number of slots) of the menu.
     *
     * @return The size of the menu.
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Gets the title of the menu.
     *
     * @return The title of the menu.
     */
    @Override
    public Component title() {
        return title;
    }

    /**
     * Opens the menu for a specific player.
     *
     * @param player The player to open the menu for.
     */
    @Override
    public void open(Player player) {
        player.openInventory(this.inventory);

        for (Button button : buttons.values()) {
            if (button.isRefreshingButton()) {
                SchedulerBuilder.scheduler()
                        .type(TaskType.REPEATING)
                        .async(button.isRefreshingAsync())
                        .after(button.refreshDelay())
                        .every(button.refreshPeriod())
                        .task(task -> {
                            if (player.getOpenInventory().getTopInventory() != getInventory()) {
                                task.cancel();
                                return;
                            }

                            int slot = slotByButton(button);

                            getInventory().clear(slot);
                            getInventory().setItem(slot, button.item().build());

                            /* player.updateInventory();
                             * This probably shouldn't be used.
                             */
                        })
                        .build();
            }
        }
    }

    /**
     * Sets a border around the menu using a Button and a pattern.
     *
     * @param borderItem    The Button to use for the border.
     * @param borderPatterns The patterns for the border.
     * @return The Menu instance.
     */
    @Override
    public Menu border(Button borderItem, String... borderPatterns) {
        int row = 0;
        for (String borderPattern : borderPatterns) {
            if (row < this.size) {
                String[] rowCharacters = borderPattern.split(" ");

                for (int col = 0; col < rowCharacters.length && col < 9; col++) {
                    String character = rowCharacters[col];

                    if (character.equals("X")) {
                        this.inventory.setItem(col + row * 9, borderItem.item().build().clone());
                        buttons.put(col + row * 9, borderItem);
                    }
                }

                row++;
            }
        }

        return this;
    }

    /**
     * Fills the menu slots with a specified filler object based on a pattern.
     *
     * @param filler  The filler object (Button or ItemBuilder) to fill the slots with.
     * @param pattern The pattern to use for filling.
     * @return The Menu instance.
     */
    @Override
    public Menu fill(Object filler, String... pattern) {
        int row = 0;

        for (String rowPattern : pattern) {
            if (row < size / 9) {
                String[] rowCharacters = rowPattern.split(" ");

                for (int col = 0; col < rowCharacters.length && col < 9; col++) {
                    String character = rowCharacters[col];
                    int slot = col + row * 9;

                    if (character.equals("X")) {
                        if (filler instanceof Button) {
                            // Assignment just so the stupid "return value not used" method doesn't show up
                            Menu builder = button(slot, (Button) filler);
                        } else if (filler instanceof ItemBuilder) {
                            // Assignment just so the stupid "return value not used" method doesn't show up
                            Menu builder = item(slot, (ItemBuilder) filler);
                        } else {
                            int callersLineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
                            Utils.plugin().getLogger().log(Level.WARNING, "You can not use Menu.fill(Object filler, String... pattern) with a object different than an ItemBuilder or Button!");
                            Utils.plugin().getLogger().log(Level.WARNING, "    Line: [" + callersLineNumber + "], File: [" + Thread.currentThread().getStackTrace()[2].getFileName() + "]");
                        }
                    }
                }

                row++;
            }
        }

        return this;
    }

    /**
     * Gets the ItemBuilder at a specific slot in the menu.
     *
     * @param slot The slot to get the ItemBuilder from.
     * @return The ItemBuilder at the specified slot.
     */
    public ItemBuilder item(int slot) {
        return items.get(slot);
    }

    /**
     * Gets the Button at a specific slot in the menu.
     *
     * @param slot The slot to get the Button from.
     * @return The Button at the specified slot.
     */
    public Button button(int slot) {
        return buttons.get(slot);
    }

    /**
     * Gets the slot of a Button within the menu.
     *
     * @param button The Button to find the slot for.
     * @return The slot of the Button, or -1 if not found.
     */
    public int slotByButton(Button button) {
        for (Map.Entry<Integer, Button> entry : buttons.entrySet()) {
            if (entry.getValue() == button) {
                return entry.getKey();
            }
        }
        return -1;
    }

    /**
     * Gets the slot of an ItemBuilder within the menu.
     *
     * @param item The ItemBuilder to find the slot for.
     * @return The slot of the ItemBuilder, or -1 if not found.
     */
    public int slotByItem(ItemBuilder item) {
        for (Map.Entry<Integer, ItemBuilder> entry : items.entrySet()) {
            if (entry.getValue() == item) {
                return entry.getKey();
            }
        }
        return -1;
    }

    /**
     * Retrieves the Bukkit Inventory associated with this Menu.
     *
     * @return The Inventory object.
     */
    @NotNull
    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    /**
     * Get the inventory associated with this Menu.
     *
     * @return The associated Inventory object.
     */
    @NotNull
    @Override
    public Inventory inventory() {
        return getInventory();
    }

    @Override
    public Menu type() {
        return this;
    }

    @Override
    public Menu onClose(CloseListener onClose) {
        this.onClose = onClose;
        return this;
    }

    @Override
    public Menu onDrag(DragListener onDrag) {
        this.onDrag = onDrag;
        return this;
    }
}