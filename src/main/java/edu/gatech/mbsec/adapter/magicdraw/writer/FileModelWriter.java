package edu.gatech.mbsec.adapter.magicdraw.writer;

import com.hp.hpl.jena.rdf.model.Model;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.openjena.riot.Lang;

/**
 * A file {@link Model model} {@link ModelWriter writer}.
 * @author rherrera
 */
public class FileModelWriter extends StreamModelWriter {
    /**
     * Constructs an instance specifying the target file.
     * @param path the target file path.
     * @exception FileNotFoundException if file cannot be created/handled.
     */
    public FileModelWriter(String path) throws FileNotFoundException {
        super(new FileOutputStream(path));
    }

    @Override
    public void write(Model model, Lang language) throws IOException {
        try (OutputStream output = getOutput()) {
            super.write(model, language);
            output.flush();
        }
    }

}