package cli;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
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
        HelpFormatter formatter = new HelpFormatter();
        Set<String> languages = Executor.getAvailableRDFLanguages(false);
        String command = "java -jar magicdrawsysml2rdf.jar <options>";
        String footer = "Available output formats are: " + languages;
        formatter.printHelp(command, "", options, footer);
    }
    /**
     * Determines whether help was requested.
     * @param args the command line arguments.
     * @return {@code true} if help was requested, {@code false} otherwise.
     * @throws ParseException if command cannot be parsed.
     */
    private static boolean isHelpRequested(String[] args)
            throws ParseException {
        CommandLine command;
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        options.addOption(Option.builder(Args.help.name()).build());
        command = parser.parse(options, args, true);
        return command.hasOption(Args.help.name());
    }
    /**
     * The main entry point.
     * @param args console arguments.
     */
    @SuppressWarnings("UseSpecificCatch")
    public static void main(String[] args) {
        CommandLine command;
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
            ex.printStackTrace(System.out);
            LOG.log(Level.SEVERE, "Could not execute translation. {0}: {1}",
                    new Object[]{ex.getClass().getName(), ex.getMessage()});
        }
    }

}