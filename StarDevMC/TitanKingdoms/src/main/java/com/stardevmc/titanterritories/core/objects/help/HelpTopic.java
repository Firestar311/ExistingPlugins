package com.stardevmc.titanterritories.core.objects.help;

import java.util.*;

public class HelpTopic {
    private String name;
    private String description;
    private SortedMap<Integer, TopicEntry> entries = new TreeMap<>();
}