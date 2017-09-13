package edu.gatech.mbsec.adapter.magicdraw.writer;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import java.io.IOException;
import java.io.OutputStream;
import org.openjena.riot.Lang;

/**
 * A generic {@code OutputStream} {@link Model model} {@link ModelWriter writer}.
 * @author rherrera
 */
public class StreamModelWriter implements ModelWriter {
    /**
     * The target output.
     */
    private final OutputStream output;
    /**
     * Constructs an instance specifying the target output.
     * @param output the target output.
     */
    public StreamModelWriter(OutputStream output) {
        this.output = output;
    }
    /**
     * Gets the output of this writer.
     * @return the output of this writer.
     */
    public OutputStream getOutput() {
        return output;
    }

    @Override
    public void write(Model model, Lang language) throws IOException {
        RDFWriter writer = model.getWriter(language.getName());
        writer.write(model, output, (String)null);
    }

}