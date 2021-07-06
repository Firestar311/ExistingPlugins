package com.kingrealms.realms.cmd;

import com.kingrealms.realms.economy.shop.Shop;
import com.kingrealms.realms.economy.shop.ShopHandler;
import com.kingrealms.realms.economy.shop.builder.ShopBuilder;
import com.kingrealms.realms.economy.shop.enums.OwnerType;
import com.kingrealms.realms.economy.shop.enums.ShopType;
import com.kingrealms.realms.economy.shop.item.ShopItem;
import com.kingrealms.realms.economy.shop.types.IPlaceable;
import com.kingrealms.realms.economy.shop.types.impl.ServerSignShop;
import com.kingrealms.realms.economy.shop.types.impl.gui.ServerGUIShop;
import com.kingrealms.realms.economy.shop.types.impl.gui.ShopCategory;
import com.kingrealms.realms.profile.RealmProfile;
import com.starmediadev.lib.collection.IncrementalMap;
import com.starmediadev.lib.util.MaterialNames;
import com.starmediadev.lib.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static com.starmediadev.lib.util.Constants.NUMBER_FORMAT;

public class ShopCommands extends BaseCommand {
    
    private final IncrementalMap<ShopBuilder> shopBuilders = new IncrementalMap<>();
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.color("&cOnly players can use that command."));
            return true;
        }
    
        RealmProfile profile = plugin.getProfileManager().getProfile(sender);
        ShopHandler shopHandler = plugin.getEconomyManager().getShopHandler();
        
        if (cmd.getName().equalsIgnoreCase("shop")) {
            ServerGUIShop defaultShop = shopHandler.getDefaultShop();
            defaultShop.getGui().openGUI(profile.getBukkitPlayer());
        } else if (cmd.getName().equalsIgnoreCase("guishop")) {
            if (!profile.hasPermission("realms.admin.guishop")) {
                profile.sendMessage("&cYou do not have permission to use that command.");
                return true;
            }
            if (!(args.length > 0)) {
                sender.sendMessage(Utils.color("&cYou must provide a sub command."));
                return true;
            }
    
            ServerGUIShop defaultShop = shopHandler.getDefaultShop();
            
            if (Utils.checkCmdAliases(args, 0, "category")) {
                if (!profile.hasPermission("realms.admin.guishop.category")) {
                    profile.sendMessage("&cYou do not have permission to use that command.");
                    return true;
                }
                if (!(args.length > 1)) {
                    sender.sendMessage(Utils.color("&cYou must provide a sub command."));
                    return true;
                }
                
                if (Utils.checkCmdAliases(args, 1, "create", "c")) {
                    if (!profile.hasPermission("realms.admin.guishop.category.create")) {
                        profile.sendMessage("&cYou do not have permission to use that command.");
                        return true;
                    }
                    if (!(args.length > 2)) {
                        sender.sendMessage(Utils.color("&cYou must provide an id to create the category."));
                        return true;
                    }
                    
                    String id = args[2];
                    if (defaultShop.getCategory(id) != null) {
                        profile.sendMessage("&cA category with that id already exists.");
                        return true;
                    }
                    
                    if (!(args.length > 3)) {
                        sender.sendMessage(Utils.color("&cYou must provide a material type to be used to display the category."));
                        return true;
                    }
    
                    Material material;
                    try {
                        material = Material.valueOf(args[3].toUpperCase());
                    } catch (IllegalArgumentException e) {
                        profile.sendMessage("&cYou provided an invalid material name.");
                        return true;
                    }
                    
                    if (!material.isItem()) {
                        profile.sendMessage("&cThat material type cannot be made into a valid item.");
                        return true;
                    }
    
                    ShopCategory category = new ShopCategory(defaultShop, material, id);
                    defaultShop.addCategory(category);
                    profile.sendMessage("&iYou created a shop category with the id &j" + category.getId());
                } else {
                    if (!(args.length > 2)) {
                        sender.sendMessage(Utils.color("&cYou must provide a category name."));
                        return true;
                    }
                    
                    ShopCategory category = defaultShop.getCategory(args[1]);
                    if (category == null) {
                        profile.sendMessage("&cThat is not a valid category name.");
                        return true;
                    }
                    
                    if (!(args.length > 3)) {
                        sender.sendMessage(Utils.color("&cYou must provide a sub command."));
                        return true;
                    }
                    
                    if (Utils.checkCmdAliases(args, 2, "additem", "setitem")) {
                        if (!profile.hasPermission("realms.admin.guishop.category.item")) {
                            profile.sendMessage("&cYou do not have permission to use that command.");
                            return true;
                        }
                        if (!(args.length > 3)) {
                            sender.sendMessage(Utils.color("&cYou must provide a Shop Item ID."));
                            return true;
                        }
                        
                        ShopItem shopItem = shopHandler.getItem(args[3]);
                        if (shopItem == null) {
                            profile.sendMessage("&cYou provided an invalid shop item id.");
                            return true;
                        }
                        
                        if (Utils.checkCmdAliases(args, 2, "additem")) {
                            category.addItem(shopItem);
                            profile.sendMessage("&iYou added the item &j" + shopItem.getId() + " &ito the category &j" + category.getId());
                        } else {
                            if (!(args.length > 4)) {
                                sender.sendMessage(Utils.color("&cYou must provide a position to set the item."));
                                return true;
                            }
                            
                            int position;
                            try {
                                position = Integer.parseInt(args[4]);
                            } catch (NumberFormatException e) {
                                profile.sendMessage("&cYou provided an invalid number for the position.");
                                return true;
                            }
                            
                            shopItem.setPosition(position);
                            category.addItem(shopItem);
                            profile.sendMessage("&iYou set the item &j" + shopItem.getId() + " &iat the position &j" + position + " &iin the category &j" + category.getId());
                        }
                    } else if (Utils.checkCmdAliases(args, 2, "setname")) {
                        if (!profile.hasPermission("realms.admin.guishop.category.setname")) {
                            profile.sendMessage("&cYou do not have permission to use that command.");
                            return true;
                        }
                        if (!(args.length > 3)) {
                            sender.sendMessage(Utils.color("&cYou must provide a new name for the category."));
                            return true;
                        }
                        
                        String name = StringUtils.join(args, " ", 3, args.length);
                        category.setName(name);
                        profile.sendMessage("&iYou set the name of the category &j" + category.getId() + " &ito &j" + category.getName());
                    } else if (Utils.checkCmdAliases(args, 2, "setdescription")) {
                        if (!profile.hasPermission("realms.admin.guishop.category.setdescription")) {
                            profile.sendMessage("&cYou do not have permission to use that command.");
                            return true;
                        }
                        if (!(args.length > 4)) {
                            sender.sendMessage(Utils.color("&cYou must provide a new description for the category."));
                            return true;
                        }
    
                        String description = StringUtils.join(args, " ", 3, args.length);
                        category.setDescription(description);
                        profile.sendMessage("&iYou set the description of the category &j" + category.getId() + " &ito &j" + category.getName());
                    } else if (Utils.checkCmdAliases(args, 2, "seticon")) {
                        if (!profile.hasPermission("realms.admin.guishop.category.seticon")) {
                            profile.sendMessage("&cYou do not have permission to use that command.");
                            return true;
                        }
                        if (!(args.length > 3)) {
                            sender.sendMessage(Utils.color("&cYou must provide a material type for the icon."));
                            return true;
                        }
                        
                        Material icon;
                        try {
                            icon = Material.valueOf(args[3].toUpperCase());
                        } catch (IllegalArgumentException e) {
                            profile.sendMessage("&cYou provided an invalid material type for the icon.");
                            return true;
                        }
                        
                        if (!icon.isItem()) {
                            profile.sendMessage("&cThe material type provided is not a valid item.");
                            return true;
                        }
                        
                        category.setIcon(icon);
                        profile.sendMessage("&iYou set the icon type to &j" + MaterialNames.getName(icon) + " &ifor the category &j" + category.getId());
                    }
                }
            }
        } else if (cmd.getName().equalsIgnoreCase("shopitem")) {
            if (!profile.hasPermission("realms.admin.shopitem")) {
                profile.sendMessage("&cYou do not have permission to use that command.");
                return true;
            }
            if (!(args.length > 0)) {
                sender.sendMessage(Utils.color("&cYou must provide a subcommand."));
                return true;
            }
            
            if (Utils.checkCmdAliases(args, 0, "create", "c")) {
                if (!profile.hasPermission("realms.admin.shopitem.create")) {
                    profile.sendMessage("&cYou do not have permission to use that command.");
                    return true;
                }
                ItemStack heldItem = profile.getBukkitPlayer().getInventory().getItemInMainHand();
                if (heldItem == null) {
                    profile.sendMessage("&cYou must be holding an item to do that.");
                    return true;
                }
                
                if (!(args.length > 1)) {
                    sender.sendMessage(Utils.color("&cYou must provide an Item ID. This will be used to identify the item."));
                    return true;
                }
                
                String id = args[1];
                if (shopHandler.getItem(id) != null) {
                    profile.sendMessage("&cThere is an item with that id already.");
                    return true;
                }
                
                ShopItem shopItem = new ShopItem(heldItem, id);
                shopHandler.addItem(shopItem);
                profile.sendMessage("&gCreated a shop item with the id &h" + shopItem.getId());
                if (args.length > 3) {
                    double buy;
                    try {
                        buy = Double.parseDouble(args[2]);
                    } catch (NumberFormatException e) {
                        profile.sendMessage("&cYou provided an invalid number for the buy price.");
                        return true;
                    }
    
                    double sell;
                    try {
                        sell = Double.parseDouble(args[3]);
                    } catch (NumberFormatException e) {
                        profile.sendMessage("&cYou provided an invalid number for the sell price.");
                        return true;
                    }
    
                    shopItem.setBuyPrice(buy);
                    shopItem.setSellPrice(sell);
    
                    String buyFormat = NUMBER_FORMAT.format(buy);
                    String sellFormat = NUMBER_FORMAT.format(sell);
                    profile.sendMessage(" &8- &iBuy Price: &j" + buyFormat);
                    profile.sendMessage(" &8- &iSell Price: &j" + sellFormat);
                }
            } else if (Utils.checkCmdAliases(args, 0, "delete", "d")) {
                if (!profile.hasPermission("realms.admin.shopitem.delete")) {
                    profile.sendMessage("&cYou do not have permission to use that command.");
                    return true;
                }
                if (!(args.length > 1)) {
                    sender.sendMessage(Utils.color("&cYou must provide a shop item id."));
                    return true;
                }
                
                ShopItem shopItem = shopHandler.getItem(args[1]);
                if (shopItem == null) {
                    profile.sendMessage("&cThe id you provided did not match a valid shop item.");
                    return true;
                }
                
                shopHandler.removeItem(shopItem);
                profile.sendMessage("&iYou removed the shop item &j" + shopItem.getId());
            } else {
                if (!(args.length > 0)) {
                    sender.sendMessage(Utils.color("&cYou must provide a shop item id."));
                    return true;
                }
    
                ShopItem shopItem = shopHandler.getItem(args[0]);
                if (shopItem == null) {
                    profile.sendMessage("&cThe id you provided did not match a valid shop item.");
                    return true;
                }
                
                if (!(args.length > 1)) {
                    sender.sendMessage(Utils.color("&cYou must provide a subcommand."));
                    return true;
                }
                
                if (Utils.checkCmdAliases(args, 1, "set", "s")) {
                    if (!(args.length > 3)) {
                        sender.sendMessage(Utils.color("&cYou must provide a type and a new value."));
                        return true;
                    }
                    
                    if (Utils.checkCmdAliases(args, 2, "displayname", "name", "dn", "n")) {
                        if (!profile.hasPermission("realms.shopitem.modify.set.displayname")) {
                            profile.sendMessage("&cYou do not have permission to use that command.");
                            return true;
                        }
                        String name = StringUtils.join(args, " ", 3, args.length);
                        shopItem.setDisplayName(name);
                        profile.sendMessage("&iYou changed &j" + shopItem.getId() + "'s &idisplayname to &j" + name);
                    } else if (Utils.checkCmdAliases(args, 2, "buyprice", "buy", "bp", "b", "sellprice", "sell", "sp", "s")) {
                        if (!profile.hasPermission("realms.admin.shopitem.modify.set.price")) {
                            profile.sendMessage("&cYou do not have permission to use that command.");
                            return true;
                        }
                        double price;
                        try {
                            price = Double.parseDouble(args[3]);
                        } catch (NumberFormatException e) {
                            profile.sendMessage("&cYou provided an invalid number.");
                            return true;
                        }
                        
                        String priceFormat = NUMBER_FORMAT.format(price);
                        
                        if (Utils.checkCmdAliases(args, 2, "buyprice", "buy", "bp", "b")) {
                            shopItem.setBuyPrice(price);
                            profile.sendMessage("&iYou set the buy price of the item &j" + shopItem.getId() + " &ito &j" + priceFormat);
                        } else if (Utils.checkCmdAliases(args, 2, "sellprice", "sell", "sp", "s")) {
                            shopItem.setSellPrice(price);
                            profile.sendMessage("&iYou set the sell price of the item &j" + shopItem.getId() + " &ito &j" + priceFormat);
                        }
                    } else if (Utils.checkCmdAliases(args, 2, "minimumamount", "minamount", "minamt", "ma")) {
                        if (!profile.hasPermission("realms.admin.shopitem.modify.set.minimumamount")) {
                            profile.sendMessage("&cYou do not have permission to use that command.");
                            return true;
                        }
                        int amount;
                        try {
                            amount = Integer.parseInt(args[3]);
                        } catch (NumberFormatException e) {
                            profile.sendMessage("&cYou provided an invalid number.");
                            return true;
                        }
                        
                        shopItem.setMinAmount(amount);
                        profile.sendMessage("&iYou set the minimum amount for the item &j" + shopItem.getId() + " &ito &j" + amount);
                    } else if (Utils.checkCmdAliases(args, 2, "item", "i")) {
                        if (!profile.hasPermission("realms.admin.shopitem.modify.set.item")) {
                            profile.sendMessage("&cYou do not have permission to use that command.");
                            return true;
                        }
                        ItemStack heldItem = profile.getBukkitPlayer().getInventory().getItemInMainHand();
                        if (heldItem == null || heldItem.getType() == Material.AIR) {
                            profile.sendMessage("&cYou must be holding an item");
                            return true;
                        }
                        
                        shopItem.setItemStack(heldItem);
                        profile.sendMessage("&iYou set the item of the shop item &j" + shopItem.getId() + " &ito your held item.");
                    } else if (Utils.checkCmdAliases(args, 2, "prices")) {
                        if (!profile.hasPermission("realms.admin.shopitem.modify.set.price")) {
                            profile.sendMessage("&cYou do not have permission to use that command.");
                            return true;
                        }
                        if (!(args.length > 4)) {
                            profile.sendMessage("&cYou must provide both a buy price and sell price");
                            return true;
                        }
                        
                        double buy;
                        try {
                            buy = Double.parseDouble(args[3]);
                        } catch (NumberFormatException e) {
                            if (args[3].equalsIgnoreCase("~")) {
                                buy = shopItem.getBuyPrice();
                            } else {
                                profile.sendMessage("&cYou provided an invalid number for the buy price.");
                                return true;
                            }
                        }
                        
                        double sell;
                        try {
                            sell = Double.parseDouble(args[4]);
                        } catch (NumberFormatException e) {
                            if (args[4].equalsIgnoreCase("~")) {
                                sell = shopItem.getSellPrice();
                            } else {
                                profile.sendMessage("&cYou provided an invalid number for the sell price.");
                                return true;
                            }
                        }
                        
                        shopItem.setBuyPrice(buy);
                        shopItem.setSellPrice(sell);
                        
                        String buyFormat = NUMBER_FORMAT.format(buy);
                        String sellFormat = NUMBER_FORMAT.format(sell);
                        
                        profile.sendMessage("&iSet the prices of the item &j" + shopItem.getId() + " &ito &j" + buyFormat + " buy &iand &j" + sellFormat + " sell&i.");
                    }
                } else if (Utils.checkCmdAliases(args, 1, "get", "g")) {
                    if (!profile.hasPermission("realms.admin.shopitem.get")) {
                        profile.sendMessage("&cYou do not have permission to use that command.");
                        return true;
                    }
                    ItemStack itemStack = shopItem.getShopStack();
                    profile.getInventory().addItem(itemStack);
                    profile.sendMessage("&iYou have received an item with the information of that shop item.");
                }
            }
        } else if (cmd.getName().equalsIgnoreCase("signshop")) {
            if (!profile.hasPermission("realms.admin.signshop")) {
                profile.sendMessage("&cYou do not have permission to use that command.");
                return true;
            }
            if (!(args.length > 0)) {
                profile.sendMessage("&cYou must provide a subcommand.");
                return true;
            }
    
            if (Utils.checkCmdAliases(args, 0, "create", "c", "createtemplate", "ct")) {
                if (!profile.hasPermission("realms.signshop.create")) {
                    profile.sendMessage("&cYou do not have permission to use that command.");
                    return true;
                }
                if (!(args.length > 1)) {
                    profile.sendMessage("&cYou must provide a name.");
                    return true;
                }
        
                ShopType shopType = ShopType.SIGN;
                OwnerType ownerType = OwnerType.SERVER;
        
                String name = ChatColor.stripColor(StringUtils.join(args, " ", 1, args.length));
        
                ShopBuilder shopBuilder = new ShopBuilder();
                shopBuilder.setOwnerType(ownerType).setShopType(shopType).setName(name);
                shopBuilder.setTemplate(Utils.checkCmdAliases(args, 0, "createtemplate", "ct"));
        
                int index = this.shopBuilders.add(shopBuilder);
                profile.sendMessage("&iYou have started the creation process for a shop named &j" + name);
                profile.sendMessage("&iThe shop reference number is &j" + index + " &i. Use this number to configure.");
                profile.sendMessage("&iBelow is a list of the values still needed to finalize the shop.");
                for (String s : shopBuilder.getRemainingValues()) {
                    profile.sendMessage("    &j" + s);
                }
                profile.sendMessage("&iWhen these values have been supplied the shop will be finalized.");
            } else if (Utils.checkCmdAliases(args, 0, "delete", "d")) {
                if (!profile.hasPermission("realms.admin.signshop.delete")) {
                    profile.sendMessage("&cYou do not have permission to use that command.");
                    return true;
                }
                if (!(args.length > 1)) {
                    profile.sendMessage("&cYou must provide a shop name.");
                    return true;
                }
        
                Shop shop = plugin.getEconomyManager().getShopHandler().getShop(args[1].replace("_", " "));
        
                if (shop == null) {
                    profile.sendMessage("&cYou provided an invalid shop identifier.");
                    return true;
                }
        
                plugin.getEconomyManager().getShopHandler().removeShop(shop);
                profile.sendMessage("&iRemoved the shop &i" + shop.getName());
            } else if (Utils.checkCmdAliases(args, 0, "modify")) {
                if (!profile.hasPermission("realms.admin.signshop.modify")) {
                    profile.sendMessage("&cYou do not have permission to use that command.");
                    return true;
                }
                if (!(args.length > 1)) {
                    profile.sendMessage("&cYou must provide a shop identifier.");
                    return true;
                }
        
                int reference = -1;
                try {
                    reference = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {}
        
                ShopBuilder shopBuilder = null;
                Shop shop = null;
                if (reference != -1) {
                    shopBuilder = this.shopBuilders.get(reference);
                } else {
                    shop = plugin.getEconomyManager().getShopHandler().getShop(args[1].replace("_", " "));
                }
        
                if (shopBuilder == null && shop == null) {
                    profile.sendMessage("&cYou provided an invalid shop identifier.");
                    return true;
                }
        
                if (!(args.length > 2)) {
                    profile.sendMessage("&cYou must provide a modify sub command.");
                    return true;
                }
        
                if (!(args.length > 3)) {
                    profile.sendMessage("&cYou must provide a value.");
                    return true;
                }
                if (Utils.checkCmdAliases(args, 2, "name")) {
                    if (!profile.hasPermission("realms.admin.signshop.modify.name")) {
                        profile.sendMessage("&cYou do not have permission to use that command.");
                        return true;
                    }
                    String newName = StringUtils.join(args, " ", 3, args.length);
                    if (shopBuilder != null) {
                        shopBuilder.setName(newName);
                        profile.sendMessage("&iSet the name of the shop &j" + reference + " &ito &j" + newName);
                        this.checkShopBuilder(shopBuilder, profile, reference);
                    } else if (shop != null) {
                        shop.setName(newName);
                        profile.sendMessage("&iSet the name of the shop &j" + shop.getUniqueId().toString() + " &ito &j" + newName);
                    }
                    if (shop != null) {
                        shop.update();
                    }
                } else if (Utils.checkCmdAliases(args, 2, "description")) {
                    if (!profile.hasPermission("realms.admin.signshop.modify.description")) {
                        profile.sendMessage("&cYou do not have permission to use that command.");
                        return true;
                    }
                    String newDescription = StringUtils.join(args, " ", 3, args.length);
                    if (shopBuilder != null) {
                        shopBuilder.setDescription(newDescription);
                        profile.sendMessage("&iSet the description of the shop &j" + reference + " &ito &j" + newDescription);
                    } else if (shop != null) {
                        shop.setName(newDescription);
                        profile.sendMessage("&iSet the description of the shop &j" + shop.getUniqueId().toString() + " &ito &j" + newDescription);
                        shop.update();
                    }
                    this.checkShopBuilder(shopBuilder, profile, reference);
                } else if (Utils.checkCmdAliases(args, 2, "setitem")) {
                    if (!profile.hasPermission("realms.admin.signshop.modify.item")) {
                        profile.sendMessage("&cYou do not have permission to use that command.");
                        return true;
                    }
                    
                    if (!(args.length > 3)) {
                        sender.sendMessage(Utils.color("&cYou must provide an item id"));
                        return true;
                    }
                    
                    ShopItem shopItem = plugin.getEconomyManager().getShopHandler().getItem(args[3]);
                    if (shopItem == null) {
                        profile.sendMessage("&cYou provided an invalid shop item id.");
                        return true;
                    }
                    
                    if (shopBuilder != null) {
                        shopBuilder.setItem(shopItem);
                    } else {
                        ((ServerSignShop) shop).setItem(shopItem);
                    }
                    profile.sendMessage("&iSet the shop item of the shop &j" + args[1] + " &ito &j" + shopItem.getId());
                }
            } else if (Utils.checkCmdAliases(args, 0, "placer")) {
                if (!profile.hasPermission("realms.admin.signshop.placer")) {
                    profile.sendMessage("&cYou do not have permission to use that command.");
                    return true;
                }
                if (!(args.length > 2)) {
                    profile.sendMessage("&cYou must provide a shop name.");
                    return true;
                }
        
                Shop shop = plugin.getEconomyManager().getShopHandler().getShop(args[1].replace("_", " "));
        
                if (shop == null) {
                    profile.sendMessage("&cYou provided an invalid shop identifier.");
                    return true;
                }
        
                if (!(shop instanceof IPlaceable)) {
                    profile.sendMessage("&cThat shop is not placeable.");
                    return true;
                }
        
                profile.getInventory().addItem(((IPlaceable) shop).getPlacer());
                profile.sendMessage("&iGave you the placer item for the shop &i" + shop.getName());
            }
        }
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> results = new ArrayList<>(), possibleResults = new ArrayList<>();
        
        if (cmd.getName().equalsIgnoreCase("shopitem")) {
            if (args.length == 1) {
                possibleResults.addAll(Arrays.asList("create", "delete"));
                for (ShopItem item : plugin.getEconomyManager().getShopHandler().getItems()) {
                    possibleResults.add(item.getId());
                }
                results.addAll(Utils.getResults(args[0], possibleResults));
            } else if (args.length == 2) {
                if (Utils.checkCmdAliases(args, 0, "delete", "d")) {
                    for (ShopItem item : plugin.getEconomyManager().getShopHandler().getItems()) {
                        possibleResults.add(item.getId());
                    }
                } else if (StringUtils.isNotEmpty(args[0]) && !Utils.checkCmdAliases(args, 0, "create", "c")){
                    possibleResults.addAll(Arrays.asList("set", "get"));
                }
                
                results.addAll(Utils.getResults(args[1], possibleResults));
            } else if (args.length == 3) {
                if (StringUtils.isNotEmpty(args[0]) && !Utils.checkCmdAliases(args, 0, "delete", "d")) {
                    if (Utils.checkCmdAliases(args, 1, "set", "s")) {
                        possibleResults.addAll(Arrays.asList("displayname", "buyprice", "sellprice", "minimumamount", "item", "prices"));
                    }
                }
    
                results.addAll(Utils.getResults(args[2], possibleResults));
            }
        } else if (cmd.getName().equalsIgnoreCase("guishop")) {
            if (args.length == 1) {
                possibleResults.add("category");
                results.addAll(Utils.getResults(args[0], possibleResults));
            } else if (args.length == 2) {
                if (Utils.checkCmdAliases(args, 0, "category")) {
                    possibleResults.add("create");
                    for (ShopCategory category : plugin.getEconomyManager().getShopHandler().getDefaultShop().getCategories()) {
                        possibleResults.add(category.getId());
                    }
                    results.addAll(Utils.getResults(args[1], possibleResults));
                }
            } else if (args.length == 3) {
                if (Utils.checkCmdAliases(args, 0, "category")) {
                    if (StringUtils.isNotEmpty(args[1]) && !Utils.checkCmdAliases(args, 1, "create", "c")) {
                        possibleResults.addAll(Arrays.asList("additem", "setitem", "setname", "setdescription", "seticon"));
                    }
    
                    results.addAll(Utils.getResults(args[2], possibleResults));
                }
            } else if (args.length == 4) {
                if (Utils.checkCmdAliases(args, 0, "category")) {
                    if (StringUtils.isNotEmpty(args[1])) {
                        List<String> materials = new ArrayList<>();
                        for (Material material : Material.values()) {
                            if (material.isItem()) {
                                materials.add(material.name().toLowerCase());
                            }
                        }
                        
                        if (Utils.checkCmdAliases(args, 1, "create", "c")) {
                            if (StringUtils.isNotEmpty(args[2])) {
                                possibleResults.addAll(materials);
                            }
                        } else {
                            if (Utils.checkCmdAliases(args, 2, "additem", "setitem")) {
                                for (ShopItem item : plugin.getEconomyManager().getShopHandler().getItems()) {
                                    possibleResults.add(item.getId());
                                }
                            } else if (Utils.checkCmdAliases(args, 2, "seticon")) {
                                possibleResults.addAll(materials);
                            }
                        }
                    }
                }
                
                results.addAll(Utils.getResults(args[3], possibleResults));
            }
        } else if (cmd.getName().equalsIgnoreCase("signshop")) {
            if (args.length == 1) {
                possibleResults.addAll(List.of("create", "createtemplate", "delete", "modify", "placer"));
                results.addAll(Utils.getResults(args[0], possibleResults));
            } else if (args.length == 2) {
                if (Utils.checkCmdAliases(args, 0, "placer")) {
                    for (Integer key : this.shopBuilders.keySet()) {
                        possibleResults.add(key + "");
                    }
    
                    for (Shop shop : plugin.getEconomyManager().getShopHandler().getShops()) {
                        possibleResults.add(shop.getName());
                    }
                }
                results.addAll(Utils.getResults(args[1], possibleResults));
            } else if (args.length == 3) {
                if (Utils.checkCmdAliases(args, 0, "modify")) {
                    if (StringUtils.isNotEmpty(args[1])) {
                        possibleResults.addAll(Arrays.asList("name", "description", "setitem"));
                    }
                }
                results.addAll(Utils.getResults(args[2], possibleResults));
            } else if (args.length == 4) {
                if (Utils.checkCmdAliases(args, 0, "modify")) {
                    if (StringUtils.isNotEmpty(args[1])) {
                        if (Utils.checkCmdAliases(args, 2, "setitem")) {
                            for (ShopItem shopItem : plugin.getEconomyManager().getShopHandler().getItems()) {
                                possibleResults.add(shopItem.getId());
                            }
                        }
                    }
                }
            }
            results.addAll(Utils.getResults(args[3], possibleResults));
        }
        
        return results;
    }
    
    private void checkShopBuilder(ShopBuilder shopBuilder, RealmProfile profile, int id) {
        if (shopBuilder != null) {
            if (!shopBuilder.checkRequired()) {
                profile.sendMessage("&iThe shop still needs the values below.");
                for (String s : shopBuilder.getRemainingValues()) {
                    profile.sendMessage("    &j" + s);
                }
            } else {
                if (!shopBuilder.isTemplate()) {
                    Shop shop = shopBuilder.build();
                    plugin.getEconomyManager().getShopHandler().addShop(shop);
                    profile.sendMessage("&iThe required values have been set, shop has been created with id &j" + shop.getUniqueId().toString());
                } else {
                    ItemStack itemStack = shopBuilder.createTemplate();
                    profile.getInventory().addItem(itemStack);
                    profile.sendMessage("&iThe required values have been set, You have been given the template placer.");
                }
                
                this.shopBuilders.remove(id);
            }
        }
    }
}