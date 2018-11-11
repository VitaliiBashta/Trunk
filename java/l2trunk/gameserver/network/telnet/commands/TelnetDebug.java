package l2trunk.gameserver.network.telnet.commands;

import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.loginservercon.AuthServerCommunication;
import l2trunk.gameserver.network.telnet.TelnetCommand;
import l2trunk.gameserver.network.telnet.TelnetCommandHolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

public class TelnetDebug implements TelnetCommandHolder {
    private final Set<TelnetCommand> _commands = new LinkedHashSet<>();

    public TelnetDebug() {
        _commands.add(new TelnetCommand("dumpnpc", "dnpc") {
            @Override
            public String getUsage() {
                return "dumpnpc";
            }

            @Override
            public String handle(String[] args) {
                StringBuilder sb = new StringBuilder();

                int total = 0;
                int maxId = 0, maxCount = 0;

                Map<Integer,List<NpcInstance>> npcStats = new HashMap<>();

                for (GameObject obj : GameObjectsStorage.getAllObjects())
                    if (obj.isCreature())
                        if (obj.isNpc()) {
                            List<NpcInstance> list;
                            NpcInstance npc = (NpcInstance) obj;
                            int id = npc.getNpcId();

                            if ((list = npcStats.get(id)) == null)
                                npcStats.put(id, list = new ArrayList<>());

                            list.add(npc);

                            if (list.size() > maxCount) {
                                maxId = id;
                                maxCount = list.size();
                            }

                            total++;
                        }

                sb.append("Total NPCs: ").append(total).append("\n");
                sb.append("Maximum NPC ID: ").append(maxId).append(" count : ").append(maxCount).append("\n");

                for ( Map.Entry<Integer, List<NpcInstance>> itr: npcStats.entrySet()) {
                    int id = itr.getKey();
                    List<NpcInstance> list = itr.getValue();
                    sb.append("=== ID: ").append(id).append(" ").append(" Count: ").append(list.size()).append(" ===").append("\n");

                    for (NpcInstance npc : list)
                        try {
                            sb.append("AI: ");

                            if (npc.hasAI())
                                sb.append(npc.getAI().getClass().getName());
                            else
                                sb.append("none");

                            sb.append(", ");

                            if (npc.getReflectionId() > 0) {
                                sb.append("ref: ").append(npc.getReflectionId());
                                sb.append(" - ").append(npc.getReflection().getName());
                            }

                            sb.append("loc: ").append(npc.getLoc());
                            sb.append(", ");
                            sb.append("spawned: ");
                            sb.append(npc.isVisible());
                            sb.append("\n");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                }

                try {
                    Files.createDirectories(Paths.get("stats"));
                    Files.write(Paths.get("stats/NpcStats-" + new SimpleDateFormat("MMddHHmmss").format(System.currentTimeMillis()) + ".txt"),
                            Collections.singleton(sb.toString()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return "NPC stats saved.\n";
            }

        });

        _commands.add(new TelnetCommand("asrestart") {
            @Override
            public String getUsage() {
                return "asrestart";
            }

            @Override
            public String handle(String[] args) {
                AuthServerCommunication.getInstance().restart();

                return "Restarted.\n";
            }

        });
    }

    @Override
    public Set<TelnetCommand> getCommands() {
        return _commands;
    }
}