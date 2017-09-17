package edu.gatech.mbsec.adapter.magicdraw;

import edu.gatech.mbsec.adapter.magicdraw.builder.MetaInformation;
import edu.gatech.mbsec.adapter.magicdraw.builder.Vocabularies;
import edu.gatech.mbsec.adapter.magicdraw.builder.ModelDescriptor;
import com.hp.hpl.jena.rdf.model.Model;
import com.nomagic.runtime.ApplicationExitedException;
import edu.gatech.mbsec.adapter.magicdraw.builder.SysMLRDFBuilder;
import edu.gatech.mbsec.adapter.magicdraw.parser.MagicDrawApplication;
import edu.gatech.mbsec.adapter.magicdraw.parser.MagicDrawParser;
import edu.gatech.mbsec.adapter.magicdraw.writer.FileModelWriter;
import edu.gatech.mbsec.adapter.magicdraw.writer.HttpModelWriter;
import edu.gatech.mbsec.adapter.magicdraw.writer.ModelWriter;
import edu.gatech.mbsec.adapter.magicdraw.writer.StreamModelWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.openjena.riot.Lang;

/**
 * Application's command executor.
 * @author rherrera
 */
public class Executor {
    /**
     * Logger of this class.
     */
    private static final Logger LOG = Logger.getLogger(Executor.class.getName());
    /**
     * Gets the available RDF languages.
     * @param toLowerCase indicates whether names should be on lowercase.
     * @return available RDF languages.
     */
    public static Set<String> getAvailableRDFLanguages(boolean toLowerCase) {
        Lang[] values = Lang.values();
        Set<String> availables = new HashSet<>();
        for (Lang language : values) {
            if (toLowerCase)
                availables.add(language.getName().toLowerCase());
            else
                availables.add(language.getName());
        }
        return availables;
    }
    /**
     * Determines the target language from the command line.
     * @param command the execution command.
     * @return the chosen/target language.
     */
    private static Lang getLanguage(CommandLine command) {
        Lang language = Lang.TURTLE;
        String format = command.getOptionValue(Args.format.name());
        if (format != null)
            language = Lang.get(format);
        return language;
    }
    /**
     * Gets the {@link MetaInformation version control} for this execution.
     * @param properties the execution properties.
     * @return the version control instance.
     */
    private static MetaInformation getMetaInformation(CommandLine command) {
        String[] prefixes = command.getOptionValues(Args.nsprefix.name());
        Properties vocabularies = Vocabularies.asProperties(prefixes);
        String[] meta = command.getOptionValues(Args.meta.name());
        return MetaInformation.getInstance(vocabularies, meta);
    }
    /**
     * Executes the command given from console.
     * @param command the command to execute.
     * @param magicdraw the magicdraw application.
     * @throws MalformedURLException if some URL is malformed.
     * @throws FileNotFoundException if the output target file does not exist.
     * @throws IOException if some I/O exception occurs.
     * @throws ApplicationExitedException if the application ends unexpectedly.
     */
    public static void execute(CommandLine command, MagicDrawApplication magicdraw)
            throws MalformedURLException, FileNotFoundException, IOException,
            ApplicationExitedException {
        Model model;
        ModelWriter writer;
        ModelDescriptor descriptor;
        ByteArrayOutputStream buffer = null;
        Lang language = getLanguage(command);
        SysMLRDFBuilder builder = new SysMLRDFBuilder();
        MetaInformation meta = getMetaInformation(command);
        MagicDrawParser parser = new MagicDrawParser(magicdraw);
        String mdzipFile = command.getOptionValue(Args.mdzip.name());
        String target = command.getOptionValue(Args.target.name());
        if (target == null) {
            buffer = new ByteArrayOutputStream();
            descriptor = new ModelDescriptor(meta);
            writer = new StreamModelWriter(buffer);
        } else {
            if (target.startsWith("http")) {
                writer = new HttpModelWriter(target, meta.getID());
                descriptor = new ModelDescriptor(meta, ((HttpModelWriter)writer).getTarget());
            } else {
                descriptor = new ModelDescriptor(meta);
                writer = new FileModelWriter(target);
            }
        }
        model = builder.build(parser.parse(mdzipFile), descriptor);
        descriptor.customize(model);
        writer.write(model, language);
        if (buffer != null) {
            LOG.info(buffer.toString("UTF-8"));
        }
    }

}