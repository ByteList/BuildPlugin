package de.gamechest.buildplugin.command;

import de.gamechest.GameChest;
import de.gamechest.buildplugin.BuildPlugin;
import de.gamechest.buildplugin.BuildMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ByteList on 15.01.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class LoadSchematicCommand implements CommandExecutor {

    private final BuildPlugin buildPlugin = BuildPlugin.getInstance();
    private final GameChest gameChest = GameChest.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if(buildPlugin.getBuildMode(player.getUniqueId()) == BuildMode.SPECTATE) {
                player.sendMessage(buildPlugin.prefix+"§cDu hast keine Berechtigung für diesen Befehl!");
                return true;
            }
        }

        if(args.length == 1) {
            //https://med.bytelist.de/upload/server/files/test.schematic
            String schematicUrl = args[0];
            String[] splitted = schematicUrl.replaceFirst("https://", "").split("/");
            String schematicName = splitted[splitted.length-1];
            String domain = splitted[0];

            if(!(domain.equalsIgnoreCase("med.bytelist.de") || domain.equalsIgnoreCase("vs.bytelist.de") || domain.equalsIgnoreCase("hub.bytelist.de"))) {
                sender.sendMessage(buildPlugin.prefix+"§cError: Domain is not certified!");
                return true;
            }

            if(schematicUrl.endsWith("/")) {
                sender.sendMessage(buildPlugin.prefix+"§cError: URL can not end with \"/\"!");
                return true;
            }
            
            if(!schematicName.endsWith(".schematic")) {
                sender.sendMessage(buildPlugin.prefix+"§cError: File must be a schematic!");
                return true;
            }

            long fileSize = getFileSize(schematicUrl);

            if(fileSize == -1) {
                sender.sendMessage(buildPlugin.prefix+"§cError: Can not check file size! Try it again later.");
                return true;
            }

            if(fileSize > 102400) {
                sender.sendMessage(buildPlugin.prefix+"§cError: File size to large!");
                return true;
            }

            downloadFile(sender, schematicUrl, schematicName);
            return true;
        }
        sender.sendMessage(buildPlugin.prefix+"§c/loadschematic <URL to schematic>");
        return true;
    }

    private long getFileSize(String url) {
        URL link = null;
        try {
            link = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection conn = null;
        long fileSize = -1;
        if(link != null) {
            try {
                conn = (HttpURLConnection) link.openConnection();
                conn.setRequestMethod("HEAD");
                fileSize = conn.getContentLengthLong();
            } catch (IOException e) {
                fileSize = -1;
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }
        return fileSize;
    }

    private void downloadFile(CommandSender sender, String url, String fileName) {
        try {
            URL link = new URL(url);
            long fileSize = getFileSize(url);

            InputStream in = new BufferedInputStream(link.openStream());
            ByteArrayOutputStream out = new ByteArrayOutputStream();


            byte[] buf = new byte[1024];
            int n;
            System.out.println("("+sender.getName()+") Start downloading schematic (size: "+fileSize+" bytes)"+url+"...");
            sender.sendMessage(buildPlugin.prefix+"§7Start downloading schematic (size: §a"+fileSize+"§7 bytes)...");
            while (-1 != (n = in.read(buf))) {
                out.write(buf, 0, n);
            }
            out.close();
            in.close();
            byte[] response = out.toByteArray();

            FileOutputStream fos = new FileOutputStream("./plugins/WorldEdit/schematics/"+fileName);
            fos.write(response);
            fos.close();

            System.out.println("("+sender.getName()+") Download finished. Saved as "+fileName);
            sender.sendMessage(buildPlugin.prefix+"§aDownload finished! §7Saved as §e"+fileName);
        } catch (IOException ex) {
            System.err.println("("+sender.getName()+") Can't download files! Maybe doesn't exists?");
            sender.sendMessage(buildPlugin.prefix+"§cError: Can't download files! Maybe doesn't exists?");
        }
    }
}
