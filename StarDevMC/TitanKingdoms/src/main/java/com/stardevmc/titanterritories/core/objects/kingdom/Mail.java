package com.stardevmc.titanterritories.core.objects.kingdom;

import com.firestar311.lib.pagination.IElement;
import com.stardevmc.titanterritories.core.TitanTerritories;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;
import java.util.Map.Entry;

public class Mail implements ConfigurationSerializable, IElement {
    
    private UUID creator;
    private MailMessage message;
    private List<UUID> recipients;
    private List<UUID> read;
    private int id = -1;
    
    public Mail(UUID creator, UUID recipient, MailMessage message) {
        this.creator = creator;
        this.message = message;
        this.recipients = new ArrayList<>(Collections.singletonList(recipient));
        this.read = new ArrayList<>();
    }
    
    public Mail(UUID creator, List<UUID> recipients, MailMessage message) {
        this.creator = creator;
        this.message = message;
        this.recipients = new ArrayList<>(recipients);
        this.read = new ArrayList<>();
    }
    
    public Mail(Map<String, Object> serialized) {
        this.recipients = new ArrayList<>();
        this.read = new ArrayList<>();
        if (serialized.containsKey("creator")) {
            this.creator = UUID.fromString((String) serialized.get("creator"));
        }
        
        if (serialized.containsKey("message")) {
            this.message = (MailMessage) serialized.get("message");
        }
        
        if (serialized.containsKey("recipients")) {
            for (String u : (List<String>) serialized.get("recipients")) {
                UUID uuid = UUID.fromString(u);
                this.recipients.add(uuid);
            }
        }
        
        if (serialized.containsKey("read")) {
            for (String u : (List<String>) serialized.get("read")) {
                this.read.add(UUID.fromString(u));
            }
        }
        
        if (serialized.containsKey("id")) {
            this.id = (int) serialized.get("id");
        }
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("creator", creator.toString());
        serialized.put("message", message);
        if (!recipients.isEmpty()) {
            List<String> memberStrings = new ArrayList<>();
            for (UUID recipient : recipients) {
                memberStrings.add(recipient.toString());
            }
            serialized.put("recipients", memberStrings);
        }
        if (!read.isEmpty()) {
            List<String> readStrings = new ArrayList<>();
            for (UUID uuid : read) {
                readStrings.add(uuid.toString());
            }
            serialized.put("read", readStrings);
        }
        serialized.put("id", id);
        return serialized;
    }
    
    public boolean isRecipient(UUID player) {
        if (recipients.isEmpty()) { return false; }
        for (UUID member : recipients) {
            if (member.equals(player)) {
                return true;
            }
        }
        return false;
    }
    
    public void addRecipient(UUID member) {
        this.recipients.add(member);
    }
    
    public void removeRecipient(UUID member) {
        this.recipients.remove(member);
    }
    
    public boolean hasRead(UUID player) {
        return read.contains(player);
    }
    
    public void markRead(UUID member) {
        if (isRecipient(member)) {
            this.read.add(member);
        }
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String formatLine(String... args) {
        return " &8 " + this.id + "- &eFrom: " + TitanTerritories.getInstance().getMemberManager().getMember(creator).getName() + " &eSubject: " + this.message.getSubject();
    }
    
    public UUID getCreator() {
        return creator;
    }
    
    public MailMessage getMessage() {
        return message;
    }
    
    public List<UUID> getRecipients() {
        return new ArrayList<>(recipients);
    }
    
    public List<UUID> getRead() {
        return new ArrayList<>(read);
    }
    
    public int getId() {
        return id;
    }
    
    public static class MailMessage implements ConfigurationSerializable {
        
        private String subject;
        private SortedMap<Integer, String> lines = new TreeMap<>();
        
        public MailMessage(String subject) {
            this.subject = subject;
        }
        
        public MailMessage(String subject, List<String> lines) {
            this.subject = subject;
            lines.forEach(this::addLine);
        }
    
        public MailMessage(String subject, String... lines) {
            this.subject = subject;
            for (String line : lines) {
                addLine(line);
            }
        }
        
        public MailMessage(Map<String, Object> serialized) {
            for (Entry<String, Object> entry : serialized.entrySet()) {
                if (entry.getKey().equalsIgnoreCase("subject")) {
                    this.subject = (String) entry.getValue();
                } else {
                    String key = entry.getKey();
                    if (key.contains("line")) {
                        String[] keySplit = key.split("-");
                        int lineNumber = Integer.parseInt(keySplit[1]);
                        this.lines.put(lineNumber, (String) entry.getValue());
                    }
                }
            }
        }
        
        public Map<String, Object> serialize() {
            Map<String, Object> serialized = new HashMap<>();
            serialized.put("subject", this.subject);
            for (Entry<Integer, String> entry : lines.entrySet()) {
                serialized.put("line-" + entry.getKey(), entry.getValue());
            }
            return serialized;
        }
        
        public void addLine(String text) {
            try {
                int line = 0;
                if (lines.lastKey() != null) {
                    line = lines.lastKey() + 1;
                }
                this.lines.put(line, text);
            } catch (NoSuchElementException e) {}
        }
        
        public void removeLine(int line) {
            this.lines.remove(line);
        }
        
        public void setLine(int line, String text) {
            this.lines.put(line, text);
        }
        
        public void addLines(SortedMap<Integer, String> lines) {
            this.lines.putAll(lines);
        }
        
        public String getSubject() {
            return subject;
        }
        
        public SortedMap<Integer, String> getLines() {
            return new TreeMap<>(lines);
        }
        
        public String format() {
            StringBuilder sb = new StringBuilder("&aSubject: ");
            sb.append(this.subject).append("\n&aContent\n");
            this.lines.values().forEach(line -> sb.append("&a").append(line));
            return sb.toString();
        }
    }
}