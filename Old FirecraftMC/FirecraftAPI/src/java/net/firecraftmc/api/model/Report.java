package net.firecraftmc.api.model;

import net.firecraftmc.api.paginator.Paginatable;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.text.SimpleDateFormat;
import java.util.*;

public class Report implements Paginatable {

    private int id;
    private final UUID reporter;
    private final UUID target;
    private UUID assignee;
    private final String reason;
    private Status status;
    private Outcome outcome;
    private final Location location;
    private String reporterName, targetName, assigneeName;
    private List<Response> responses = new ArrayList<>();
    private final long date;

    public Report(UUID reporter, UUID target, String reason, Location location, long date) {
        this.reporter = reporter;
        this.target = target;
        this.reason = reason;
        this.location = location;
        this.date = date;
        this.status = Status.OPEN;
        this.outcome = Outcome.NONE;
    }

    public Report(int id, UUID reporter, UUID target, String reason, long date, UUID assignee, Status status, Outcome outcome, Location location, String reporterName, String targetName, String assigneeName, List<Response> responses) {
        this(reporter, target, reason, location, date);
        this.id = id;
        this.assignee = assignee;
        this.status = status;
        this.outcome = outcome;
        this.reporterName = reporterName;
        this.targetName = targetName;
        this.assigneeName = assigneeName;
        this.responses = responses;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Outcome getOutcome() {
        return outcome;
    }

    public void setOutcome(Outcome outcome) {
        this.outcome = outcome;
    }

    public UUID getReporter() {
        return reporter;
    }

    public UUID getTarget() {
        return target;
    }

    public String getReason() {
        return reason;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void addNote(Response response) {
        this.responses.add(response);
    }

    public void createNote(long timestamp, UUID noter, String message) {
        this.responses.add(new Response(timestamp, noter, message));
    }
    
    public List<Response> getResponses() {
        return responses;
    }

    public Location getLocation() {
        return location;
    }

    public String toString() {
        String string = "&4{id} &e- &5{reporter} &e- &d{target} &e- &1{assignee} &e- &3{reason} &e- {status}&e - {outcome}";
        string = string.replace("{id}", id + "");
        string = string.replace("{reporter}", reporterName);
        string = string.replace("{target}", targetName);
        string = string.replace("{assignee}", ((assignee != null) ? assigneeName : "NONE"));
        string = string.replace("{reason}", reason);
        string = string.replace("{status}", status.getColor() + status.toString());
        string = string.replace("{outcome}", outcome.getColor() + outcome.toString());
        return string;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public UUID getAssignee() {
        return assignee;
    }

    public void setAssignee(UUID assignee) {
        this.assignee = assignee;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public boolean isInvolved(UUID uuid) {
        if (uuid.equals(reporter)) return true;
        return uuid.equals(target);
    }

    public long getDate() {
        return date;
    }
    
    public String formatLine() {
        String format = "&4{id} &7{date} &5{reporter} &e-> &d{target}&8: &3{reason}";
        format = format.replace("{id}", id + "");
        format = format.replace("{reporter}", reporterName);
        format = format.replace("{target}", targetName);
        format = format.replace("{reason}", reason);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy h:mm a, z");
        format = format.replace("{date}", dateFormat.format(calendar.getTime()));
        return format;
    }
    
    public enum Status {
        OPEN(ChatColor.GREEN, "A report that has not yet been resolved."),
        CLOSED(ChatColor.DARK_GRAY, "A report that has been resolved."),
        PENDING(ChatColor.BLUE, "A report that has been seen by staff, but there are no staff online to handle it."),
        INVESTIGATING(ChatColor.AQUA, "A report in which the staff team is currently investigating."),
        WAITING_RESPONSE(ChatColor.GOLD, "A report that is awaiting a response from the reporter.");

        private final String description;
        private final ChatColor color;

        Status(ChatColor color, String description) {
            this.description = description;
            this.color = color;
        }

        public String getDescription() {
            return description;
        }

        public ChatColor getColor() {
            return color;
        }
    }

    public enum Outcome {
        ACCEPTED("A report that has been accepted by the staff team.", ChatColor.DARK_GREEN),
        DENIED("A report that has been denied by the staff team.", ChatColor.RED),
        NONE("A report that has not had an outcome set.", ChatColor.DARK_GRAY);

        private final String description;
        private final ChatColor color;

        Outcome(String description, ChatColor color) {
            this.description = description;
            this.color = color;
        }

        public String getDescription() {
            return description;
        }

        public ChatColor getColor() {
            return color;
        }
    }
    
    public static class Response {
        private final long timestamp;
        private final UUID responder;
        private final String message;

        public Response(long timestamp, UUID responder, String message) {
            this.timestamp = timestamp;
            this.responder = responder;
            this.message = message;
        }
    
        public long getTimestamp() {
            return timestamp;
        }
    
        public UUID getResponder() {
            return responder;
        }
        
        public String getMessage() {
            return message;
        }
    }
}