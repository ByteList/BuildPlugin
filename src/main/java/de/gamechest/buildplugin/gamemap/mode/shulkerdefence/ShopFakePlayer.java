package de.gamechest.buildplugin.gamemap.mode.shulkerdefence;

import de.gamechest.GameChest;
import de.gamechest.fakeplayer.FakePlayer;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by ByteList on 23.12.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class ShopFakePlayer {

    private final GameChest gameChest = GameChest.getInstance();

    @Getter
    private HashMap<UUID, Integer> entityIds = new HashMap<>();
    @Getter
    private ArrayList<UUID> interacted = new ArrayList<>();

    @Getter
    private final Location fakePlayerLocation;
    private final double spawnDistance = 40;
    @Getter
    private final String displayname;

    public ShopFakePlayer(Location fakePlayerLocation, String teamColor) {
        this.fakePlayerLocation = fakePlayerLocation;
        this.displayname = teamColor+"Dealer";
    }

    public void create(Player player) {
        if(this.entityIds.containsKey(player.getUniqueId())) return;

        FakePlayer npc = FakePlayer.FakePlayerBuilder.newInstance()
                .player(player)
                .location(this.fakePlayerLocation)
                .displayname(this.displayname)
                .skin("eyJ0aW1lc3RhbXAiOjE0ODkzMzgwNjg0MzgsInByb2ZpbGVJZCI6ImYyYjNhZWRjYThkYjRiYTM5ZWU4YTVlZTJkMWViNGNjIiwicHJvZmlsZU5hbWUiOiJaYWRuaWsiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzZkOThmZmI4YTI3MzdmMmUwOGY1YWVlZGQ2NDU5ZDgxZDkyYzMxM2I4MDIyYzU3MzFhNjI5MGQ2ZDVjNjEifX19",
                        "dS3AVCgPAvVcugh9eujizb0gqT+vrS0K9iJqRjybIRsDthNYzvcdfmad2DuHUv2z0MDzFSs10bkcMcGH8c53lVFtVFXYs6WsFgQntc7Y12X9CNJqI55LGnivddUJ2YyA7s0ABpj6g6lbAaj2ZPhJsDLTNg/hcHKux+nQ6tcyfPoKDV+YZloPy8xSw4t+hLQAxeP3+9YsOJw3ozcgNQ2wBqc22IRYXXU6oUxM55UCmkH0ISh/Xj6vADqh2WzdenCRepYTQDa0x4JiFxMODP8webMZaAg9QZFi7klaPtR36z+2nFeRECO6mBqVXDdd9frsMQlSgovr/ZxmhFgMIKqvoaT7g0mgjrofpyU1Dmi03LiQC1Bq8knaGF//JcZ0VSJYJQeKzff8rGzEcEq72zMv7+qq+Flg5A2U8ncXyk17bgoDlxc5h+nca1moZvnsWRX841fZnNrwrEfNMYQlKcf7e38MD8YLam9jRx/qbNVVLMzmlBCPYwaqCnsLJ36nKoO9DRYDisY7HhcM6XRZ8wNpE1O8K9tD8rjakVdS6Wn5GajvnW6cHr+Aap6dWi7R9qa3HJi4VJ0Y8ajl3HziR3cy2cGTEY2/8hLYmWGApyABkqkmP9Atkt+Dc0dxDrOulKcXjJa7QtBDju/tTfOob3OKYj9cdfTufUaBdmK5y1Itccg=")
                .runnable((fakePlayer, targetPlayer) -> {
                    if (player.getLocation().distance(this.fakePlayerLocation) <= this.spawnDistance) {
                        if(!fakePlayer.isSpawned()) {
                            fakePlayer.spawn();
                            fakePlayer.removeFromTabList();
                        } else {
                            fakePlayer.lookAtPlayer();
                        }
                    } else {
                        fakePlayer.destroy();
                    }
                }).build();

        gameChest.getFakePlayerManager().addNewFakePlayer(player.getUniqueId(), false, npc);
        this.entityIds.put(player.getUniqueId(), npc.getEntityId());
    }

    public void remove(Player player) {
        if(!this.entityIds.containsKey(player.getUniqueId()))
            return;

        int npcId = this.entityIds.remove(player.getUniqueId());
        gameChest.getFakePlayerManager().removeFakePlayer(player.getUniqueId(), npcId);
    }
}
