package cli;

import com.nomagic.runtime.ApplicationExitedException;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import org.apache.commons.cli.Option;
import org.apache.http.conn.HttpHostConnectException;
import org.openjena.riot.RiotException;

public class MagicDraw2RDF {
    /**
     * The log manager for logging configuration.
     */
    private static final LogManager LOG_MANAGER = LogManager.getLogManager();
    /**
     * Logger of this class.
     */
    private static final Logger LOG = Logger.getLogger(MagicDraw2RDF.class.getName());
    /**
     * Gets the {@link Options} supported by this app.
     * @return options supported by this app.
     */
    private static Options getOptions() {
        Option.Builder builder;
        Options options = new Options();
        for (Args arg : Args.values()) {
            builder = Option.builder(arg.name()).desc(arg.getDescription());
            if (arg.isMultiple()) {
                builder.hasArgs();
            } else if (arg.hasArgument()) {
                builder.hasArg().argName(arg.getArgumentName());
            }
            options.addOption(builder.required(arg.isRequired()).build());
        }
        return options;
    }
    /**
     * Configures the java default logging.
     * @throws IOException if some I/O exception occurs.
     */
    private static void configureLogging() throws IOException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream config = loader.getResourceAsStream("logging.properties");
        LOG_MANAGER.readConfiguration(config);
    }
    /**
     * Prints the available options to run this app.
     * @param options options available for this app.
     */
    private static void printOptions(Options options) {
        StringBuilder footer = new StringBuilder();
        HelpFormatter formatter = new HelpFormatter();
        Map<String, String> prefixes = Vocabularies.getKnownPrefixes();
        Set<String> languages = Executor.getAvailableRDFLanguages(false);
        String command = "magicdrawsysml2rdf(.sh|.bat) (<MagicDraw Path> | -help) <options>";
        footer.append("<format> = ");
        footer.append(languages);
        footer.append("\n\n=============== ");
        footer.append("KNOWN <PREFIXES:NAMESPACES> FOR META-DATA");
        footer.append(" ===============\n\n");
        for(Map.Entry<String, String> prefix : prefixes.entrySet()) {
            footer.append('<');
            footer.append(prefix.getKey());
            footer.append(':');
            footer.append(prefix.getValue());
            footer.append(">\n");
        }
        formatter.printHelp(command, "", options, footer.toString());
    }
    /**
     * Determines whether help was requested.
     * @param args the command line arguments.
     * @return {@code true} if help was requested, {@code false} otherwise.
     * @throws ParseException if command cannot be parsed.
     */
    private static boolean isHelpRequested(String[] args)
            throws ParseException {
        String argument = "-" + Args.help.name();
        for(String arg : args) {
            if (arg.equalsIgnoreCase(argument)) {
                return true;
            }
        }
        return false;
    }
    /**
     * The main entry point.
     * @param args console arguments.
     * @throws com.nomagic.runtime.ApplicationExitedException
     */
    @SuppressWarnings("UseSpecificCatch")
    public static void main(String[] args) throws ApplicationExitedException {
        PrintStream writer;
        CommandLine command;
        ByteArrayOutputStream bos;
        Options options = getOptions();
        CommandLineParser parser = new DefaultParser();
        try {
            configureLogging();
            if (isHelpRequested(args)) {
                printOptions(options);
            } else {
                command = parser.parse(options, args);
                Executor.execute(command);
            }
        } catch(ParseException | IllegalArgumentException | RiotException ex) {
            LOG.severe(ex.getMessage());
            printOptions(options);
        } catch(FileNotFoundException | MalformedURLException |
                HttpHostConnectException ex) {
            LOG.severe(ex.getMessage());
        } catch(Exception ex) {
            bos = new ByteArrayOutputStream();
            writer = new PrintStream(bos);
            ex.printStackTrace(writer);
            writer.flush();
            LOG.log(Level.SEVERE, "Could not execute translation. {0}: {1}\n {2}",
                    new Object[]{ex.getClass().getName(), ex.getMessage(), bos.toString()});
        } finally {
            Executor.finish();
        }
    }

}