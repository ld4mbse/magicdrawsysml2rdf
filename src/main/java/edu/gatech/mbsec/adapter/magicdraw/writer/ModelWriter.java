package edu.gatech.mbsec.adapter.magicdraw.writer;

import com.hp.hpl.jena.rdf.model.Model;
import java.io.IOException;
import org.openjena.riot.Lang;

/**
 * An RDF {@link Model model} writer.
 * @author rherrera
 */
public interface ModelWriter {
    /**
     * Writes a model into a target media.
     * @param model the model to write.
     * @param language the target language.
     * @throws IOException if some I/O exception occurs.
     */
    void write(Model model, Lang language) throws IOException;
}