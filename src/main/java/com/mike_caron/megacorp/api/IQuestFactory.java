package com.mike_caron.megacorp.api;

import com.google.gson.JsonObject;
import com.mike_caron.megacorp.impl.Quest;
import com.mike_caron.megacorp.impl.QuestLocalization;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public interface IQuestFactory
{
    @Nonnull List<Quest> createQuests(@Nullable JsonObject tag);
    QuestLocalization localize(String locale, Quest quest);
}
