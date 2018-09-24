package com.mike_caron.megacorp.api;

import com.mike_caron.megacorp.impl.Quest;

import java.util.List;

public interface IQuestFactory
{
    List<Quest> createQuests();
}
