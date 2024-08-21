package yawix;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public class Bot extends ListenerAdapter {
    Pattern IP_PATTERN = Pattern.compile("^(((2(([0-4][0-9])|(5[0-5])))|(1[0-9][0-9])|([1-9][0-9]{0,1}))\\.){3}((2(([0-4][0-9])|(5[0-5])))|(1[0-9][0-9])|([1-9][0-9]{0,1}))$");
    String bot_iD_chan = "ид где будет работать бот канала";
    static ArrayList<String> blacklist = new ArrayList<String>();

    public static void main(String[] args) throws IOException {
        JDABuilder jdaBuilder = JDABuilder.createDefault("сюда токен");
        jdaBuilder.setStatus(OnlineStatus.DO_NOT_DISTURB);
        jdaBuilder.setActivity(Activity.streaming("Marauder bot", "https://www.youtube.com/watch?v=r3bEjsv9JFw"));
        jdaBuilder.addEventListeners(new Bot());
        System.out.println("Bot is run code by Yawixooo ;)");
        jdaBuilder.build().updateCommands().addCommands(
                Commands.slash("tcp", "Run test")
                        .addOption(OptionType.STRING, "ipport", "Server ip and port", true),
                Commands.slash("mhelp", "Help menu."),
                Commands.slash("resolve", "Resolve ip")
                        .addOption(OptionType.STRING, "domens", "Resolve domen to ip:port",true),
                Commands.slash("ping", "Check VPS availability")
        ).queue();

        blacklist.add("127.0.0.1:8080");
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        // channel check
        if (!event.getChannelId().equals(bot_iD_chan)){
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("`\uD83D\uDD3BERROR Bot use !`" + "#"+bot_iD_chan, null);
            eb.setColor(Color.RED);
            eb.setAuthor("\uD83D\uDD39Marauder bot");
            eb.setFooter("power by yawixooo.", "https://i.imgur.com/q9oVzYO.gif");
            event.replyEmbeds(eb.build()).queue();
            return;
        }

        // mineping
        if (event.getName().equals("tcp")) {
            String ipport = event.getOption("ipport").getAsString();
            if (blacklist.contains(ipport)){
                EmbedBuilder em = new EmbedBuilder();
                em.setColor(Color.RED);
                em.setTitle("`\uD83D\uDD3BERROR server is black list!`", null);
                em.setAuthor("\uD83D\uDD3BMarauder bot");
                em.setFooter("power by yawixooo.", "https://i.imgur.com/q9oVzYO.gif");
                event.replyEmbeds(em.build()).queue();
                return;
            }
            String[] args = ipport.split(":");
            String ip = args[0];
            int port = Integer.parseInt(args[1]);

            Boolean valid_args = (Boolean) (IP_PATTERN.matcher(ip).matches() && (0 < port && port < 65535));
            if (!valid_args) {
                EmbedBuilder embed = EmbedBadIp(ip, port);
                event.replyEmbeds(embed.build()).queue();
                return;
            }

            EmbedBuilder eb = EmbedOk(ip, port);
            event.replyEmbeds(eb.build()).queue();
            System.out.println("Attack start!");

            // help
        } else if (event.getName().equals("mhelp")) {
            EmbedBuilder em = embedhelp();
            event.replyEmbeds(em.build()).queue();

            // resolve
        } else if (event.getName().equals("resolve")) {
            String domensip = event.getOption("domens").getAsString();
            resolve(domensip,event);
        } else if (event.getName().equals("ping")) {
            EmbedBuilder em = new EmbedBuilder();
            em.setColor(Color.RED);
            em.setTitle("`\uD83D\uDD3B Bot is working`", null);
            em.setAuthor("\uD83D\uDD39Marauder bot");
            em.setFooter("power by yawixooo.", "https://i.imgur.com/q9oVzYO.gif");
            event.replyEmbeds(em.build()).queue();
        }
    }

    public EmbedBuilder EmbedOk(String ip, int port)
    {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("`\uD83D\uDD3BAttack sent successfully!`", null);
        eb.setColor(Color.red);
        eb.setDescription("**`Target ~>`** **IP: " + ip + " | PORT: " + Integer.toString(port) + "**");
        eb.addField("**`Power ~>`**", "**FREE PLAN**", true);
        eb.addField("**`Threads ~>`**", "**500**", true);
        eb.setImage("https://c.tenor.com/F05TermKdOsAAAAd/tenor.gif");
        eb.setAuthor("\uD83D\uDD39Marauder bot");
        eb.setFooter("power by yawixooo.", "https://i.imgur.com/q9oVzYO.gif");
        return eb;
    }

    public static void resolve(String domensip, SlashCommandInteractionEvent event){
        String urlpath = "https://api.mcsrvstat.us/2/" + domensip;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlpath)).build();
        System.out.println("Resolve");
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("`\uD83D\uDD3B server fetch in progress`");
        embed.setColor(Color.red);
        embed.setAuthor("\uD83D\uDD39Marauder bot");
        embed.setFooter("power by yawixooo.", "https://i.imgur.com/q9oVzYO.gif");
        event.replyEmbeds(embed.build()).queue();

        CompletableFuture<HttpResponse<String> > future = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        future.thenAccept(response -> {
            System.out.println("Fetch");
            EmbedBuilder eb = new EmbedBuilder();
            JSONObject jsonObject = new JSONObject(response.body());
            if (!jsonObject.getBoolean("online")) {
                eb.setTitle("`\uD83D\uDD3BERROR server is off`");
                eb.setDescription("```Invalid target ~>```** " + domensip);
                eb.setColor(Color.red);
                eb.setAuthor("\uD83D\uDD39Marauder bot");
                eb.setFooter("power by yawixooo.", "https://i.imgur.com/q9oVzYO.gif");
            }else{
                System.out.println("Online");
                eb.setTitle("`\uD83D\uDD3B Resolved domens`");
                eb.setColor(Color.green);
                String ip = jsonObject.getString("ip");
                int port = jsonObject.getInt("port");
                eb.addField("**Target ~> **", "`" + ip +":" + port + "`", true);
                eb.addField("**Players ~> **", Integer.toString(jsonObject.getJSONObject("players").getInt("online")), true);
                eb.addField("**Protocol ~> **", "`" + jsonObject.getInt("protocol") + "`", true);
                eb.addField("**Version ~> **", "`" + jsonObject.getString("protocol_name") + "`", true);
                eb.addField("**Core ~> **", "`" + jsonObject.getString("software") + "`", true);
                eb.setImage("http://status.mclive.eu/Marauder bot/" + ip + "/" + port + "/banner.png");
                eb.setAuthor("\uD83D\uDD39Marauder bot");
                eb.setFooter("power by yawixooo.", "https://i.imgur.com/q9oVzYO.gif");
                System.out.println("Embed");

            }
            System.out.println("Message");
            //event.replyEmbeds(eb.build()).setEphemeral(true).queue();
            event.getHook().editOriginalEmbeds(eb.build()).queue();

        });

    }

    public EmbedBuilder EmbedBadIp(String ip, int port)
    {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("`\uD83D\uDD3BERROR target!`", null);
        eb.setColor(Color.red);
        eb.setDescription("```Target ~>```** IP: " + ip + " | PORT: " + Integer.toString(port) + "**");
        eb.addField("", "**Invalid IP or PORT.**", false);
        eb.setAuthor("\uD83D\uDD39Marauder bot");
        eb.setFooter("power by yawixooo.", "https://i.imgur.com/q9oVzYO.gif");
        return eb;
    }

    public EmbedBuilder embedhelp(){
        EmbedBuilder em = new EmbedBuilder();
        em.setColor(Color.RED);
        em.setTitle("`\uD83D\uDD3B Help menu!`");
        em.addField("```/tcp```", "**~ Tcp test **", true);
        em.addField("```/resolve```", "** Resolve domens ip**", true);
        em.setAuthor("\uD83D\uDD39Marauder bot");
        em.setFooter("power by yawixooo.", "https://i.imgur.com/q9oVzYO.gif");
        return em;
    }

}
