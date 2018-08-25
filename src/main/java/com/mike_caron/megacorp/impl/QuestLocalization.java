package com.mike_caron.megacorp.impl;

public class QuestLocalization
{
    public final String title;
    public final String description;

    public QuestLocalization()
    {
        this.title = "Unknown Title";
        this.description = "Unknown Description";
    }

    public QuestLocalization(String title, String description)
    {
        this.title = title;
        this.description = description;
    }

    public QuestLocalization withTitle(String title)
    {
        return new QuestLocalization(title, description);
    }

    public QuestLocalization withDescription(String description)
    {
        return new QuestLocalization(title, description);
    }

}
