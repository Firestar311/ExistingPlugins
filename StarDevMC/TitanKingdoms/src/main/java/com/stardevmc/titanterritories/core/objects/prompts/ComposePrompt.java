package com.stardevmc.titanterritories.core.objects.prompts;

import com.firestar311.lib.player.User;
import com.firestar311.lib.util.Utils;
import com.stardevmc.titanterritories.core.TitanTerritories;
import com.stardevmc.titanterritories.core.objects.interfaces.IHolder;
import com.stardevmc.titanterritories.core.util.MailBuilder;
import org.bukkit.conversations.*;

public class ComposePrompt extends StringPrompt {
    
    private IHolder holder;
    private MailBuilder mailBuilder;
    
    public ComposePrompt(IHolder holder, MailBuilder builder) {
        this.mailBuilder = builder;
        this.holder = holder;
    }
    
    public String getPromptText(ConversationContext context) {
        return Utils.color("&aPlease enter whom this is to, separated by commas");
    }
    
    public Prompt acceptInput(ConversationContext context, String input) {
        if (input.contains(",")) {
            String[] rawRArray = input.split(",");
            for (String p : rawRArray) {
                User info = TitanTerritories.getInstance().getPlayerManager().getUser(p);
                if (info == null) {
                    context.getForWhom().sendRawMessage(Utils.color("&cSorry, but " + p + " is not a valid player name."));
                    return END_OF_CONVERSATION;
                }
                
                if (holder.getUserController().get(info.getUniqueId()) != null) {
                    context.getForWhom().sendRawMessage(Utils.color("&cSorry, but " + info.getLastName() + " is not a member of your " + holder.getClass().getSimpleName() + "."));
                    return END_OF_CONVERSATION;
                }
                
                this.mailBuilder.addRecipient(info.getUniqueId());
            }
        } else {
            User info = TitanTerritories.getInstance().getPlayerManager().getUser(input);
            if (info == null) {
                context.getForWhom().sendRawMessage(Utils.color("&cThat is not a valid player name."));
                return END_OF_CONVERSATION;
            }
            
            if (holder.getUserController().get(info.getUniqueId()) == null) {
                context.getForWhom().sendRawMessage(Utils.color("&cThat player is not in your " + holder.getClass().getSimpleName() + "."));
                return END_OF_CONVERSATION;
            }
            mailBuilder.setRecipient(info.getUniqueId());
        }
        return new StringPrompt() {
            public String getPromptText(ConversationContext context) {
                return Utils.color("&aPlease enter a message subject.");
            }
            
            public Prompt acceptInput(ConversationContext context, String input) {
                mailBuilder.setSubject(input);
                return new NumericPrompt() {
                    protected Prompt acceptValidatedInput(ConversationContext context, Number input) {
                        final int totalLines = (int) input;
                        int currentLine = 0;
                        return new LinePrompt(mailBuilder, currentLine, totalLines, holder);
                    }
                    
                    public String getPromptText(ConversationContext context) {
                        return Utils.color("&aHow many lines do you want to have in your message?");
                    }
                };
            }
        };
    }
}

class LinePrompt extends StringPrompt {
    
    private int currentLine;
    private IHolder holder;
    private MailBuilder mailBuilder;
    private int totalLines;
    
    public LinePrompt(MailBuilder mailBuilder, int currentLine, int totalLines, IHolder holder) {
        this.mailBuilder = mailBuilder;
        this.currentLine = currentLine;
        this.totalLines = totalLines;
        this.holder = holder;
    }
    
    public String getPromptText(ConversationContext context) {
        return Utils.color("&aPlease enter the text for line " + currentLine);
    }
    
    public Prompt acceptInput(ConversationContext context, String input) {
        mailBuilder.addLine(currentLine, input);
        currentLine++;
        if (currentLine < totalLines) {
            return new LinePrompt(mailBuilder, currentLine, totalLines, holder);
        } else {
            return new MessagePrompt() {
                protected Prompt getNextPrompt(ConversationContext context) {
                    return new BooleanPrompt() {
                        protected Prompt acceptValidatedInput(ConversationContext context, boolean input) {
                            if (input) {
                                holder.getMailController().sendMail(mailBuilder.build());
                                return END_OF_CONVERSATION;
                            } else {
                                return new ComposePrompt(holder, new MailBuilder(mailBuilder.getCreator()));
                            }
                        }
                        
                        public String getPromptText(ConversationContext context) {
                            return Utils.color("&aDoes this look good? If so, type yes, if not, type no and you will start from the beginning.");
                        }
                    };
                }
                
                public String getPromptText(ConversationContext context) {
                    return Utils.color("&aThank you, your message looks like: \n&r" + mailBuilder.preview());
                }
            };
        }
    }
}