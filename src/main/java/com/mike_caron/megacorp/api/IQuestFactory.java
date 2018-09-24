package com.mike_caron.megacorp.api;

import com.mike_caron.megacorp.impl.Quest;
import com.mike_caron.megacorp.impl.QuestLocalization;

import java.util.List;

public interface IQuestFactory
{
    List<Quest> createQuests();
    QuestLocalization localize(String locale, Quest quest);
}
