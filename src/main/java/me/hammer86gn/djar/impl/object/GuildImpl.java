package me.hammer86gn.djar.impl.object;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.hammer86gn.djar.api.DJAR;
import me.hammer86gn.djar.api.object.Guild;
import me.hammer86gn.djar.impl.cache.GuildCache;

import java.net.MalformedURLException;
import java.net.URL;

import java.net.URISyntaxException;

public class GuildImpl implements Guild {

    private final DJAR djar;
    private final long id;

    private String name;
    private long ownerID;
    private String iconURL;

    public GuildImpl(DJAR djar, long id) {
        this.djar = djar;
        this.id = id;
    }

    @Override
    public long getGuildID() {
        return id;
    }

    @Override
    public String getGuildName() {
        return name;
    }

    @Override
    public long getGuildOwnerID() {
        return ownerID;
    }

    @Override
    public String getGuildIcon() {
        return iconURL;
    }

    @Override
    public URL getGuildIconURL() {
        try {
            return new URL(iconURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Guild getGuildByID(long id) {
        for (Guild guild : GuildCache.getInstance().getAll()) {
            if (guild.getGuildID() == id) {
                return guild;
            }
        }
        return null;
    }

    @Override
    public DJAR getDJAR() {
        return djar;
    }
}
