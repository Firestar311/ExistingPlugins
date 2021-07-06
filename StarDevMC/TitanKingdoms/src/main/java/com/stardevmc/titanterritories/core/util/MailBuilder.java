package com.stardevmc.titanterritories.core.util;

import com.firestar311.lib.util.Utils;
import com.stardevmc.titanterritories.core.TitanTerritories;
import com.stardevmc.titanterritories.core.objects.kingdom.Mail;
import com.stardevmc.titanterritories.core.objects.kingdom.Mail.MailMessage;
import org.apache.commons.lang.StringUtils;

import java.util.*;

public class MailBuilder {
    private UUID creator;
    private List<UUID> recipients = new ArrayList<>();
    private String subject;
    private SortedMap<Integer, String> lines = new TreeMap<>();
    
    public MailBuilder(UUID creator) {
        this.creator = creator;
    }
    
    public void setRecipient(UUID info) {
        this.recipients.clear();
        this.recipients.add(info);
    }
    
    public void addRecipient(UUID... players) {
        this.recipients.addAll(Arrays.asList(players));
    }
    
    public void setSubject(String input) {
        this.subject = input;
    }
    
    public void addLine(int index, String line) {
        this.lines.put(index, line);
    }
    
    public String preview() {
        StringBuilder sb = new StringBuilder();
        sb.append(Utils.blankLine(35)).append("\n&r").append("&aSubject: ").append(subject).append("\n").append("Recipients: ");
        List<String> names = new ArrayList<>();
        recipients.forEach(info -> names.add(TitanTerritories.getInstance().getMemberManager().getMember(info).getName()));
        sb.append(StringUtils.join(names, ", ")).append("\n&a&lContent:\n");
        this.lines.values().forEach(line -> sb.append("&a").append(line).append("\n"));
        sb.append("\n").append(Utils.blankLine(35));
        return sb.toString();
    }
    
    public Mail build() {
        MailMessage msg = new MailMessage(subject);
        msg.addLines(this.lines);
        return new Mail(creator, recipients, msg);
    }
    
    public UUID getCreator() {
        return this.creator;
    }
}