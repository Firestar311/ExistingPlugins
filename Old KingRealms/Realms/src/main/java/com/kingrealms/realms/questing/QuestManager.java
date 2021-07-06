package com.kingrealms.realms.questing;

import com.kingrealms.realms.questing.lines.QuestLine;
import com.kingrealms.realms.questing.quests.*;
import com.starmediadev.lib.util.ID;
import org.bukkit.Material;

import java.util.*;

@SuppressWarnings("UnusedAssignment")
public class QuestManager {
    
    private final ID mainQuestLine, netherQuestLine;
    private Map<ID, QuestLine> questLines = new HashMap<>();
    private Map<ID, Quest> quests = new HashMap<>();
    
    public QuestManager() {
        QuestLine main = new QuestLine(new ID("main"), "Main");
        mainQuestLine = main.getId();
        main.setIcon(Material.DIAMOND);
        questLines.put(main.getId(), main);
        
        int mainPos = 0;
        addQuest(new GettingStartedQuest(), mainPos - 1, main, mainPos++);
        addQuest(new GatherResourcesQuest(), mainPos - 1, main, mainPos++);
        addQuest(new CobblestoneSliverQuest(), mainPos - 1, main, mainPos++);
        addQuest(new WheatScrapQuest(), mainPos - 1, main, mainPos++);
        addQuest(new ChickenShardQuest(), mainPos - 1, main, mainPos++);
        addQuest(new CraftingSkillTableQuest(), mainPos - 1, main, mainPos++);
        addQuest(new ObtainOneThousandCoins(), mainPos - 1, main, mainPos++);
        
        QuestLine nether = new QuestLine(new ID("nether"), "Nether");
        netherQuestLine = nether.getId();
        nether.setIcon(Material.NETHER_BRICK);
        questLines.put(nether.getId(), nether);
        
        int netherPos = 0;
        addQuest(new NetherStartQuest(), netherPos - 1, nether, netherPos++);
        addQuest(new NetherHeadsQuest(), netherPos - 1, nether, netherPos++);
        addQuest(new NetherSandQuest(), netherPos - 1, nether, netherPos++);
        addQuest(new NetherObsidianQuest(), netherPos - 1, nether, netherPos++);
        addQuest(new NetherPickaxeCraftQuest(), netherPos - 1, nether, netherPos++);
        addQuest(new MineObsidianQuest(), netherPos - 1, nether, netherPos++);
        addQuest(new NetherPortalIgniterQuest(), netherPos - 1, nether, netherPos++);
        addQuest(new NetherPaymentQuest(), netherPos - 1, nether, netherPos++);
        addQuest(new EnterTheNetherQuest(), netherPos - 1, nether, netherPos++);
    }
    
    private void addQuest(Quest quest, int required, QuestLine questLine, int pos) {
        Quest previous = quests.get(questLine.getQuests().get(required));
        if (previous != null) {
            quest.addRequired(quests.get(questLine.getQuests().get(required)));
        }
        quest.setParentLine(questLine);
        this.quests.put(quest.getId(), quest);
        questLine.addQuest(pos, quest);
    }
    
    public Quest getQuest(ID id) {
        return quests.get(id);
    }
    
    public QuestLine getQuestLine(ID id) {
        return questLines.get(id);
    }
    
    public QuestLine getMainQuestLine() {
        return questLines.get(this.mainQuestLine);
    }
    
    public QuestLine getNetherQuestLine() {
        return questLines.get(this.netherQuestLine);
    }
    
    public Collection<QuestLine> getQuestLines() {
        return new ArrayList<>(questLines.values());
    }
}