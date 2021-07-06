package com.stardevmc.titanterritories.core.controller;

import com.firestar311.lib.pagination.Paginator;
import com.firestar311.lib.pagination.PaginatorFactory;
import com.firestar311.lib.util.Utils;
import com.stardevmc.titanterritories.core.TitanTerritories;
import com.stardevmc.titanterritories.core.objects.interfaces.IHolder;
import com.stardevmc.titanterritories.core.objects.interfaces.IUser;
import com.stardevmc.titanterritories.core.objects.kingdom.Mail;
import com.stardevmc.titanterritories.core.objects.prompts.ComposePrompt;
import com.stardevmc.titanterritories.core.util.MailBuilder;
import org.bukkit.command.Command;
import org.bukkit.conversations.ConversationFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MailController<T extends IHolder> extends Controller<T> {
    private List<Mail> messages = new ArrayList<>();
    
    public MailController(T holder) {
        super(holder);
    }
    
    private MailController() {}
    
    public static MailController deserialize(Map<String, Object> serialized) {
        List<Mail> messages = new ArrayList<>();
        if (serialized.containsKey("messageAmount")) {
            int messageAmount = (int) serialized.get("messageAmount");
            for (int i = 0; i < messageAmount; i++) {
                messages.add((Mail) serialized.get("message" + i));
            }
        }
        
        MailController mailController = new MailController();
        mailController.messages = messages;
        return mailController;
        
    }
    
    public void handleCommand(Command cmd, IHolder holder, IUser user, String[] args) {
        if (Utils.checkCmdAliases(args, 0, "mail", "m")) {
            if (args.length == 1) {
                List<Mail> mail = getMessagesByRecipient(user.getUniqueId());
                System.out.println(mail.size());
                AtomicInteger amountUnread = new AtomicInteger(0);
                mail.forEach(m -> {
                    if (!m.hasRead(user.getUniqueId())) {
                        amountUnread.getAndIncrement();
                    }
                });
                user.sendMessage("&aYou have " + amountUnread.get() + " unread messages.");
                return;
            }
            
            if (Utils.checkCmdAliases(args, 1, "read", "r")) {
                if (!(args.length > 2)) {
                    user.sendMessage("&cYou do not have enough arguments.");
                    return;
                }
                
                int id;
                try {
                    id = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    user.sendMessage("&cThe value for the mail id was not a valid number.");
                    return;
                }
                
                Mail message = getMessage(id);
                if (!message.isRecipient(user.getUniqueId())) {
                    user.sendMessage("&cYou are not a recipient of that message.");
                    return;
                }
                
                user.sendMessage(Utils.blankLine(35));
                user.sendMessage(message.getMessage().format());
                user.sendMessage(Utils.blankLine(35));
                message.markRead(user.getUniqueId());
            } else if (Utils.checkCmdAliases(args, 1, "compose", "c", "send", "s")) {
                ConversationFactory cf = new ConversationFactory(TitanTerritories.getInstance()).withFirstPrompt(new ComposePrompt(holder, new MailBuilder(user.getUniqueId())));
                cf.withLocalEcho(false).buildConversation(user.getPlayer()).begin();
            } else if (Utils.checkCmdAliases(args, 1, "list", "l")) {
                Paginator<Mail> paginator = PaginatorFactory.generatePaginator("&7List of mail &e({pagenumber}/{totalpages})", "&7Type /kingdoms mail list page {nextpage} for more.", 7, getMessagesByRecipient(user.getUniqueId()));
                int page = 1;
                if (args.length > 3) {
                    if (Utils.checkCmdAliases(args, 2, "page", "p")) {
                        try {
                            page = Integer.parseInt(args[3]);
                        } catch (NumberFormatException e) {
                            user.sendMessage("&cThe value for the page was not a valid number.");
                            return;
                        }
                    }
                }
                
                paginator.display(user.getPlayer(), page);
            }
        }
    }
    
    public List<Mail> getMessagesByRecipient(UUID recipient) {
        List<Mail> msgs = new ArrayList<>();
        for (Mail message : this.messages) {
            if (message.isRecipient(recipient)) {
                msgs.add(message);
            }
        }
        
        return msgs;
    }
    
    public Mail getMessage(int id) {
        for (Mail mail : messages) {
            if (mail.getId() == id) {
                return mail;
            }
        }
        return null;
    }
    
    public void addMessage(Mail message) {
        message.setId(getLastMailIndex() + 1);
        this.messages.add(message);
    }
    
    public int getLastMailIndex() {
        int greatestIndex = -1;
        for (Mail mail : messages) {
            if (greatestIndex < mail.getId()) {
                greatestIndex = mail.getId();
            }
        }
        return greatestIndex;
    }
    
    public void removeMessage(Mail message) {
        this.messages.remove(message);
    }
    
    public void sendMail(Mail mail) {
        IUser sender = holder.getUserController().get(mail.getCreator());
        sender.sendMessage("&aYour message has been sent.");
        addMessage(mail);
        for (UUID recipient : mail.getRecipients()) {
            IUser user = holder.getUserController().get(recipient);
            user.sendMessage("&aYou have recieved mail from " + sender.getName());
        }
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("amount", getMessages().size());
        for (int i = 0; i < getMessages().size(); i++) {
            serialized.put("message" + i, getMessages().get(i));
        }
        return serialized;
    }
    
    public List<Mail> getMessages() {
        return new ArrayList<>(messages);
    }
    
    public List<Mail> getMessagesByCreator(UUID creator) {
        return messages.stream().filter(message -> message.getCreator().equals(creator)).collect(Collectors.toList());
    }
}